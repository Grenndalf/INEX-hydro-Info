package Others.Utils;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.lang.Math.log;

public class ListsUtils {

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



    public ListsUtils (List<Double> inputList) {
        this.inputList = inputList;
        this.initVariables ();
    }

    public void initVariables () {
        if (inputList.size () > 1) {
            for (int i = 1; i <= inputList.size (); i++) {
                averageOfMaxValues = averageOfMaxValues.add (BigDecimal.valueOf (inputList.get (i - 1)));
                averageNaturalLogarithmOfX =
                        averageNaturalLogarithmOfX.add (BigDecimal.valueOf (log (getX (i)))).setScale (4,
                                                                                                       RoundingMode.HALF_UP);
            }
            averageOfMaxValues = averageOfMaxValues.divide (BigDecimal.valueOf (inputList.size ()), 4,
                                                            RoundingMode.HALF_UP);
            averageNaturalLogarithmOfX = averageNaturalLogarithmOfX.divide (BigDecimal.valueOf (inputList.size ()), 4
                    , RoundingMode.HALF_UP);
            for (int j = 1; j <= inputList.size (); j++) {
                nominatorB1 =
                        nominatorB1.add (((BigDecimal.valueOf (log (getX (j)))).subtract (averageNaturalLogarithmOfX))
                                                 .multiply (BigDecimal.valueOf (inputList.get (j - 1))
                                                                    .subtract (averageOfMaxValues)))
                                .setScale (4, RoundingMode.HALF_UP);
                denominatorB1 =
                        denominatorB1.add ((BigDecimal.valueOf (log (getX (j)))
                                .subtract (averageNaturalLogarithmOfX))
                                                   .pow (2)
                                                   .setScale (4, RoundingMode.HALF_UP));
            }
            BigDecimal b1 = nominatorB1.divide (denominatorB1, 4, RoundingMode.HALF_UP);
            BigDecimal b0 = averageOfMaxValues.subtract (b1.multiply (averageNaturalLogarithmOfX)).setScale (4,
                                                                                                             RoundingMode.HALF_UP);

            setQ10 (b0.add (b1.multiply (BigDecimal.valueOf (log (10)))).setScale (4, RoundingMode.HALF_UP));
            setQ50 (b0.add (b1.multiply (BigDecimal.valueOf (log (50)))).setScale (4, RoundingMode.HALF_UP));
            setQ90 (b0.add (b1.multiply (BigDecimal.valueOf (log (90)))).setScale (4, RoundingMode.HALF_UP));
            setQ100 (b0.add (b1.multiply (BigDecimal.valueOf (log (100)))).setScale (4, RoundingMode.HALF_UP));
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

            double lowerBound = Utils.getLowerBoundsForInterpolationB (helper);

            double upperBound = Utils.getUpperBoundsForInterpolationB (helper);

            double lowerSkew = Utils.getLowerSkewCoefficientBound (lowerBound);

            double upperSkew = Utils.getUpperSkewCoefficientBound (upperBound);
            if (Utils.getQuantile (helper) == 9999) {
                skewCoefficient = (((helperValue.subtract (BigDecimal.valueOf (lowerBound)))
                        .divide (BigDecimal.valueOf (0.01), 4, RoundingMode.HALF_UP))
                        .multiply ((BigDecimal.valueOf (upperSkew).subtract (BigDecimal.valueOf (lowerSkew)))
                                           .divide ((BigDecimal.valueOf (upperBound)
                                                   .subtract (BigDecimal.valueOf (lowerBound)))
                                                            .divide (BigDecimal.valueOf (0.01), 4,
                                                                     RoundingMode.HALF_UP), 4, RoundingMode.HALF_UP)))
                        .add (BigDecimal.valueOf (lowerSkew)).setScale (2, RoundingMode.HALF_UP);
            } else {
                skewCoefficient = BigDecimal.valueOf (Utils.getQuantile (helper));
            }
        }
    }

    public double getX (int i) {
        return 100 * (i / ((double) inputList.size () + 1));
    }
}
