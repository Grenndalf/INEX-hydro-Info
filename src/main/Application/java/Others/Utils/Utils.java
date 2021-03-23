package Others.Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Utils {
    private static final String BUNDLES = "Bundles.labels";

    public static ResourceBundle getResourceBundle () {
        return ResourceBundle.getBundle (BUNDLES);
    }

    public static double divideIntegers (int a, int b) {
        if (b == 0) {
            return 0.0;
        } else {
            double result = (double) a / (double) b * 100;
            result = result / 100;
            return result;
        }
    }

    private static Map<Double, Double> interpolationMap () {
        LinkedHashMap<Double, Double> linkedHashMap = new LinkedHashMap<> ();
        linkedHashMap.put (0.0, 0.1);
        linkedHashMap.put (0.1, 0.07);
        linkedHashMap.put (0.2, 0.13);
        linkedHashMap.put (0.3, 0.20);
        linkedHashMap.put (0.4, 0.26);
        linkedHashMap.put (0.5, 0.33);
        linkedHashMap.put (0.6, 0.39);
        linkedHashMap.put (0.7, 0.45);
        linkedHashMap.put (0.8, 0.57);
        linkedHashMap.put (0.9, 0.57);
        linkedHashMap.put (1.0, 0.63);
        linkedHashMap.put (1.2, 0.74);
        linkedHashMap.put (1.4, 0.84);
        linkedHashMap.put (1.6, 0.93);
        linkedHashMap.put (1.8, 1.02);
        linkedHashMap.put (2.0, 1.09);
        linkedHashMap.put (2.2, 1.15);
        linkedHashMap.put (2.4, 1.21);
        linkedHashMap.put (2.6, 1.26);
        linkedHashMap.put (2.8, 1.31);
        linkedHashMap.put (3.0, 1.35);
        linkedHashMap.put (3.5, 1.44);
        linkedHashMap.put (4.0, 1.50);
        linkedHashMap.put (4.5, 1.55);
        linkedHashMap.put (5.0, 1.60);
        linkedHashMap.put (6.0, 1.67);
        linkedHashMap.put (7.0, 1.72);
        linkedHashMap.put (8.0, 1.75);
        linkedHashMap.put (10.0, 1.8);
        linkedHashMap.put (15.0, 1.87);
        linkedHashMap.put (20.0, 1.9);
        linkedHashMap.put (30.0, 1.93);
        linkedHashMap.put (40.0, 1.95);
        linkedHashMap.put (50.0, 1.96);
        linkedHashMap.put (100.0, 1.98);

        return linkedHashMap;
    }

    public static double getQuantile (double lowerBound) {
        if (interpolationMap ().get (lowerBound) != null) {
            return interpolationMap ().get (lowerBound);
        } else {
            return 9999;
        }
    }

    private static Map<Double, LinkedList<Double>> interpolationSkewMap () {
        LinkedHashMap<Double, LinkedList<Double>> linkedHashMap = new LinkedHashMap<> ();
        linkedHashMap.put (0.0, new LinkedList<> (Arrays.asList (-9999d, -1.82, -1.28, -1.00, -0.66, -0.41, 0.0, 0.41
                , 0.66, 1.00, 1.28, 1.60, 1.82, 2.01, 2.25, 2.41, 2.90)));
        linkedHashMap.put (0.1, new LinkedList<> (Arrays.asList (-6.65, -1.65, -1.20, -0.95, -0.63, -0.40, 0.0, 0.42,
                                                                 0.68, 1.05, 1.37, 1.73, 1.98, 2.22, 2.51, 2.71,
                                                                 3.34)));
        linkedHashMap.put (0.2, new LinkedList<> (Arrays.asList (-3.31, -1.50, -1.12, -0.90, -0.61, -0.39, 0.0, 0.42,
                                                                 0.70, 1.10, 1.45, 1.87, 2.16, 2.43, 2.78, 3.03,
                                                                 3.82)));
        linkedHashMap.put (0.3, new LinkedList<> (Arrays.asList (-2.20, -1.35, -1.04, -0.85, -0.59, -0.38, 0.0, 0.43,
                                                                 0.72, 1.15, 1.54, 2.00, 2.33, 2.65, 3.05, 3.38,
                                                                 4.27)));
        linkedHashMap.put (0.4, new LinkedList<> (Arrays.asList (-1.64, -1.21, -0.96, -0.80, -0.57, -0.37, 0.0, 0.44,
                                                                 0.74, 1.20, 1.62, 2.14, 2.51, 2.87, 3.34, 3.70,
                                                                 4.77)));
        linkedHashMap.put (0.5, new LinkedList<> (Arrays.asList (-1.29, -1.07, -0.89, -0.75, -0.54, -0.36, 0.0, 0.45,
                                                                 0.76, 1.25, 1.71, 2.28, 2.70, 3.10, 3.64, 4.02,
                                                                 5.28)));
        linkedHashMap.put (0.6, new LinkedList<> (Arrays.asList (-1.06, -0.95, -0.81, -0.70, -0.52, -0.35, 0.0, 0.45,
                                                                 0.78, 1.30, 1.79, 2.42, 2.88, 3.33, 3.93, 4.37,
                                                                 5.81)));
        linkedHashMap.put (0.7, new LinkedList<> (Arrays.asList (-0.89, -0.84, -0.74, -0.65, -0.49, -0.34, 0.0, 0.46,
                                                                 0.80, 1.35, 1.89, 2.58, 3.09, 3.59, 4.25, 4.78,
                                                                 6.38)));
        linkedHashMap.put (0.8, new LinkedList<> (Arrays.asList (-0.78, -0.73, -0.67, -0.60, -0.46, -0.32, 0.0, 0.46,
                                                                 0.81, 1.40, 1.98, 2.73, 3.30, 3.86, 4.59, 5.16,
                                                                 6.98)));
        linkedHashMap.put (0.9, new LinkedList<> (Arrays.asList (-0.65, -0.64, -0.60, -0.55, -0.43, -0.31, 0.0, 0.46,
                                                                 0.83, 1.45, 2.07, 2.89, 3.51, 4.13, 4.94, 5.57,
                                                                 7.60)));
        linkedHashMap.put (1.0, new LinkedList<> (Arrays.asList (-0.57, -0.56, -0.54, -0.50, -0.41, -0.30, 0.0, 0.47,
                                                                 0.84, 1.50, 2.16, 3.05, 3.72, 4.39, 5.29, 5.98,
                                                                 8.22)));
        linkedHashMap.put (1.1, new LinkedList<> (Arrays.asList (-0.49, -0.49, -0.48, -0.45, -0.38, -0.28, 0.0, 0.47,
                                                                 0.86, 1.55, 2.26, 3.22, 3.95, 4.69, 5.68, 6.44,
                                                                 8.92)));
        linkedHashMap.put (1.2, new LinkedList<> (Arrays.asList (-0.42, -0.42, -0.42, -0.40, -0.34, -0.26, 0.0, 0.47,
                                                                 0.87, 1.60, 2.37, 3.40, 4.21, 5.02, 6.10, 6.93,
                                                                 9.70)));
        linkedHashMap.put (1.3, new LinkedList<> (Arrays.asList (-0.36, -0.36, -0.36, -0.35, -0.31, -0.24, 0.0, 0.46,
                                                                 0.88, 1.65, 2.47, 3.60, 4.47, 5.36, 6.56, 7.45,
                                                                 10.53)));
        linkedHashMap.put (1.4, new LinkedList<> (Arrays.asList (-0.31, -0.31, -0.31, -0.30, -0.27, -0.21, 0.0, 0.46,
                                                                 0.89, 1.70, 2.58, 3.80, 4.76, 5.74, 7.05, 8.06,
                                                                 11.45)));
        linkedHashMap.put (1.5, new LinkedList<> (Arrays.asList (-0.25, -0.25, -0.25, -0.25, -0.23, -0.19, 0.0, 0.45,
                                                                 0.89, 1.75, 2.70, 4.04, 5.09, 6.17, 7.62, 8.75,
                                                                 12.53)));
        linkedHashMap.put (1.6, new LinkedList<> (Arrays.asList (-0.20, -0.20, -0.20, -0.20, -0.19, -0.16, 0.0, 0.43,
                                                                 0.88, 1.80, 2.83, 4.31, 5.48, 6.68, 8.31, 9.58,
                                                                 13.84)));
        linkedHashMap.put (1.7, new LinkedList<> (Arrays.asList (-0.15, -0.15, -0.15, -0.15, -0.14, -0.13, 0.0, 0.41,
                                                                 0.87, 1.85, 2.99, 4.64, 5.96, 7.33, 9.19, 10.63,
                                                                 15.52)));
        linkedHashMap.put (1.8, new LinkedList<> (Arrays.asList (-0.10, -0.10, -0.10, -0.10, -0.10, -0.09, 0.0, 0.37,
                                                                 0.84, 1.90, 3.18, 5.08, 6.62, 8.23, 10.42, 12.11,
                                                                 17.94)));
        linkedHashMap.put (1.9, new LinkedList<> (Arrays.asList (-0.05, -0.05, -0.05, -0.05, -0.05, -0.05, 0.0, 0.30,
                                                                 0.77, 1.95, 3.47, 5.81, 7.74, 9.77, 12.56, 14.76,
                                                                 22.24)));
        linkedHashMap.put (2.0, new LinkedList<> (Arrays.asList (0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.0, 0.01, 0.18,
                                                                 2.00, 6.97, 17.90, 28.50, 40.48, 57.79, 71.71,
                                                                 121.21)));

        return linkedHashMap;
    }

    public static Map<Double, LinkedList<Double>> getModifiedInterpolationSkewMap (double skewCoefficient) {
        LinkedHashMap<Double, LinkedList<Double>> linkedHashMap = new LinkedHashMap<> ();
        if (interpolationSkewMap ().get (skewCoefficient) == null) {
            linkedHashMap.put (getLowerBoundsForSkewInterpolation (skewCoefficient),
                               interpolationSkewMap ().get (getLowerBoundsForSkewInterpolation (skewCoefficient)));
            linkedHashMap.put (getUpperBoundsForSkewInterpolation (skewCoefficient),
                               interpolationSkewMap ().get (getUpperBoundsForSkewInterpolation (skewCoefficient)));
        }else {
            linkedHashMap.put (skewCoefficient,interpolationSkewMap ().get (skewCoefficient));
        }
        return linkedHashMap;
    }

    public static double getLowerBoundsForSkewInterpolation (double skewCoefficient) {
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

    public static double getUpperBoundsForSkewInterpolation (double skewCoefficient) {
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

    public static double getLowerBoundsForInterpolationB (double value) {
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

    public static double getUpperBoundsForInterpolationB (double value) {
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

    public static double getLowerSkewCoefficientBound (double lowerBound) {
        if (lowerBound == 9999) {
            return lowerBound;
        } else if (lowerBound > 100) {
            return 2.00;
        } else {
            return interpolationMap ().get (lowerBound);
        }
    }

    public static double getUpperSkewCoefficientBound (double upperBound) {
        if (upperBound == 9999) {
            return upperBound;
        } else if (upperBound > 100) {
            return 2.00;
        } else {
            return interpolationMap ().get (upperBound);
        }
    }

}
