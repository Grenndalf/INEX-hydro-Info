package Others.Utils;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static Others.Utils.Utils.interpolationMap;
import static Others.Utils.Utils.interpolationSkewMap;
import static java.lang.Math.log;

public class Calculations {

    @Getter
    private final List<Double> inputList;
    private BigDecimal nominatorB1 = BigDecimal.ZERO;
    private BigDecimal denominatorB1 = BigDecimal.ZERO;
    private BigDecimal averageOfMaxValues = BigDecimal.ZERO;
    private BigDecimal averageNaturalLogarithmOfX = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal q10 = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal q50 = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal q90 = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal q100 = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal factor = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal helperValue = BigDecimal.ZERO;
    @Getter
    @Setter
    private BigDecimal skewCoefficient = BigDecimal.ZERO;
    @Getter
    @Setter
    private LinkedHashMap<Double, Double> finalTableValues;


    public Calculations (List<Double> inputList) {
        this.inputList = inputList;
        this.initVariables ();
    }

    private void initVariables () {
        if (inputList.size () > 1) {
            for (int i = 1; i <= inputList.size (); i++) {
                averageOfMaxValues = averageOfMaxValues.add (BigDecimal.valueOf (inputList.get (i - 1)));
                averageNaturalLogarithmOfX =
                        averageNaturalLogarithmOfX.add (BigDecimal.valueOf (log (getX (i)))).setScale (8,
                                                                                                       RoundingMode.HALF_UP);
            }
            try {
                averageOfMaxValues = averageOfMaxValues.divide (BigDecimal.valueOf (inputList.size ()), 8,
                                                                RoundingMode.HALF_UP);
                averageNaturalLogarithmOfX =
                        averageNaturalLogarithmOfX.divide (BigDecimal.valueOf (inputList.size ()), 8
                                , RoundingMode.HALF_UP);
            } catch (ArithmeticException e) {
                e.printStackTrace ();
            }
            for (int j = 1; j <= inputList.size (); j++) {
                nominatorB1 =
                        nominatorB1.add (((BigDecimal.valueOf (log (getX (j)))).subtract (averageNaturalLogarithmOfX))
                                                 .multiply (BigDecimal.valueOf (inputList.get (j - 1))
                                                                    .subtract (averageOfMaxValues)))
                                .setScale (8, RoundingMode.HALF_UP);
                denominatorB1 =
                        denominatorB1.add ((BigDecimal.valueOf (log (getX (j)))
                                .subtract (averageNaturalLogarithmOfX))
                                                   .pow (2)
                                                   .setScale (8, RoundingMode.HALF_UP));
            }
            BigDecimal b1 = nominatorB1.divide (denominatorB1, 8, RoundingMode.HALF_UP);

            BigDecimal b0 = averageOfMaxValues.subtract (b1.multiply (averageNaturalLogarithmOfX)).setScale (8,
                                                                                                             RoundingMode.HALF_UP);

            setQ10 (b0.add (b1.multiply (BigDecimal.valueOf (log (10)))).setScale (2, RoundingMode.HALF_UP));
            setQ50 (b0.add (b1.multiply (BigDecimal.valueOf (log (50)))).setScale (2, RoundingMode.HALF_UP));
            setQ90 (b0.add (b1.multiply (BigDecimal.valueOf (log (90)))).setScale (2, RoundingMode.HALF_UP));
            setQ100 (b0.add (b1.multiply (BigDecimal.valueOf (log (100)))).setScale (2, RoundingMode.HALF_UP));
            if (q50.signum () > 0) {
                setFactor ((q10.subtract (q90)).divide (BigDecimal.valueOf (2).multiply (q50), 2,
                                                        RoundingMode.HALF_UP));
            }
            try {
                helperValue = (factor.multiply (q50)).divide (q50.subtract (q100), 2, RoundingMode.HALF_UP);
            } catch (ArithmeticException e) {
                helperValue = BigDecimal.ZERO;
            }
            double helper = helperValue.doubleValue ();
            double lowerBound = getLowerBoundsForInterpolationB (helper);
            double upperBound = getUpperBoundsForInterpolationB (helper);
            double lowerSkew = getLowerSkewCoefficientBound (lowerBound);
            double upperSkew = getUpperSkewCoefficientBound (upperBound);
            try {
                if (getQuantile (helper) == 9999) {
                    skewCoefficient = (((helperValue.subtract (BigDecimal.valueOf (lowerBound)))
                            .divide (BigDecimal.valueOf (0.01), 4, RoundingMode.HALF_UP))
                            .multiply ((BigDecimal.valueOf (upperSkew).subtract (BigDecimal.valueOf (lowerSkew)))
                                               .divide ((BigDecimal.valueOf (upperBound)
                                                                .subtract (BigDecimal.valueOf (lowerBound)))
                                                                .divide (BigDecimal.valueOf (0.01), 8,
                                                                         RoundingMode.HALF_UP), 8,
                                                        RoundingMode.HALF_UP)))
                            .add (BigDecimal.valueOf (lowerSkew)).setScale (2, RoundingMode.HALF_UP);
                } else {
                    skewCoefficient = BigDecimal.valueOf (getQuantile (helper));
                }
            } catch (ArithmeticException e) {
                skewCoefficient = BigDecimal.valueOf (9999);
            }
        }
    }

    public Map<Double, LinkedList<Double>> getModifiedInterpolationSkewMap (double skewCoefficient) {
        LinkedHashMap<Double, LinkedList<Double>> linkedHashMap = new LinkedHashMap<> ();
        if (interpolationSkewMap ().get (skewCoefficient) == null) {
            linkedHashMap.put (getLowerBoundsForSkewInterpolation (skewCoefficient),
                               interpolationSkewMap ().get (getLowerBoundsForSkewInterpolation (skewCoefficient)));
            linkedHashMap.put (getUpperBoundsForSkewInterpolation (skewCoefficient),
                               interpolationSkewMap ().get (getUpperBoundsForSkewInterpolation (skewCoefficient)));
        } else {
            linkedHashMap.put (skewCoefficient, interpolationSkewMap ().get (skewCoefficient));
        }
        return linkedHashMap;
    }

    public double getLowerBoundsForSkewInterpolation (double skewCoefficient) {
        if (skewCoefficient < 0 || skewCoefficient > 2) {
            return 9999;
        } else {
            AtomicReference<Double> lowerBound = new AtomicReference<> (skewCoefficient);
            interpolationSkewMap ().forEach ((key, mapValue) -> {
                if (skewCoefficient - key > 0) {
                    lowerBound.set (key);
                }
            });
            return lowerBound.get ();
        }
    }

    public double getUpperBoundsForSkewInterpolation (double skewCoefficient) {
        if (skewCoefficient < 0 || skewCoefficient > 2) {
            return 9999;
        } else {
            AtomicReference<Double> upperBound = new AtomicReference<> (skewCoefficient);
            for (Map.Entry<Double, LinkedList<Double>> entry : interpolationSkewMap ().entrySet ()) {
                Double key = entry.getKey ();
                if (key - skewCoefficient > 0) {
                    upperBound.set (key);
                    break;
                }
            }
            return upperBound.get ();
        }
    }

    public double getLowerBoundsForInterpolationB (double value) {
        if (value < 0) {
            return 9999;
        } else if (value > 100) {
            return value;
        } else if (interpolationMap ().containsKey (value)) {
            return value;
        } else {
            AtomicReference<Double> lowerBound = new AtomicReference<> (value);
            interpolationMap ().forEach ((key, mapValue) -> {
                if (value - key > 0) {
                    lowerBound.set (key);
                }
            });
            return lowerBound.get ();
        }
    }

    public double getUpperBoundsForInterpolationB (double value) {
        if (value < 0) {
            return 9999;
        } else if (value > 100) {
            return value;
        } else if (interpolationMap ().containsKey (value)) {
            return value;
        } else {
            AtomicReference<Double> upperBound = new AtomicReference<> (value);
            for (Map.Entry<Double, Double> entry : interpolationMap ().entrySet ()) {
                Double key = entry.getKey ();
                if (key - value > 0) {
                    upperBound.set (key);
                    break;
                }
            }
            return upperBound.get ();
        }
    }

    public double getLowerSkewCoefficientBound (double lowerBound) {
        if (lowerBound == 9999) {
            return lowerBound;
        } else if (lowerBound > 100) {
            return 2.00;
        } else {
            return interpolationMap ().get (lowerBound);
        }
    }

    public double getUpperSkewCoefficientBound (double upperBound) {
        if (upperBound == 9999) {
            return upperBound;
        } else if (upperBound > 100) {
            return 2.00;
        } else {
            return interpolationMap ().get (upperBound);
        }
    }

    public double getX (int i) {
        return 100 * (i / ((double) inputList.size () + 1));
    }


    public double getQuantile (double lowerBound) {
        if (interpolationMap ().get (lowerBound) != null) {
            return interpolationMap ().get (lowerBound);
        } else {
            return 9999;
        }
    }

    public LinkedList<Double> getFinalResults () {
        LinkedList<Double> resultList = new LinkedList<> ();
        if (Utils.interpolationSkewMap ().containsKey (skewCoefficient.doubleValue ())) {
            Utils.interpolationSkewMap ().get (skewCoefficient)
                    .forEach (value ->
                                      resultList.add (q50.multiply (BigDecimal.ONE.add
                                              (factor.multiply (BigDecimal.valueOf (value)))).doubleValue ()));
        } else {
            if (skewCoefficient.doubleValue () > 2 || skewCoefficient.doubleValue () < 0) {
                return resultList;
            }
            BigDecimal lowerBound =
                    BigDecimal.valueOf (getLowerBoundsForSkewInterpolation (skewCoefficient.doubleValue ()));
            BigDecimal upperBound =
                    BigDecimal.valueOf (getUpperBoundsForSkewInterpolation (skewCoefficient.doubleValue ()));
            LinkedList<Double> upperBoundList =
                    Utils.interpolationSkewMap ().get (getUpperBoundsForSkewInterpolation (skewCoefficient.doubleValue ()));
            LinkedList<Double> lowerBoundList =
                    Utils.interpolationSkewMap ().get (getLowerBoundsForSkewInterpolation (skewCoefficient.doubleValue ()));
            LinkedList<Double> helperList = new LinkedList<> ();
            for (int i = 0; i < lowerBoundList.size (); i++) {
                BigDecimal interpolationResult =
                        ((skewCoefficient.subtract (lowerBound)).divide (upperBound.subtract (lowerBound)
                        , 4, RoundingMode.HALF_UP))
                        .multiply (BigDecimal.valueOf (upperBoundList.get (i)).subtract (BigDecimal.valueOf (lowerBoundList.get (i))))
                        .add (BigDecimal.valueOf (lowerBoundList.get (i))).setScale (2, RoundingMode.HALF_UP);
                helperList.add (interpolationResult.doubleValue ());
            }
            helperList.forEach (value -> resultList.add (q50.multiply (BigDecimal.ONE.add (factor.multiply (BigDecimal.valueOf (value)))).setScale (4,RoundingMode.HALF_UP).doubleValue ()));
        }
        return resultList;
    }
}
