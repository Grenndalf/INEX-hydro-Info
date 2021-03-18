package Others.Utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        LinkedHashMap<Double,Double> linkedHashMap = new LinkedHashMap<> ();
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
        linkedHashMap.put (4.0, 1.5);
        linkedHashMap.put (4.5, 1.55);
        linkedHashMap.put (5.0, 1.6);
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
                if (value - key> 0) {
                    lowerBound.set (key);
                }
            });
            return lowerBound.get ();
        }
    }

    public static double getUpperBoundsForInterpolationB (double lowerBound) {
        if (lowerBound == 9999) {
            return 9999;
        } else if (lowerBound > 100) {
            return lowerBound;
        } else {
            AtomicReference<Double> upperBound = new AtomicReference<> (lowerBound);
            for (Map.Entry<Double, Double> entry : interpolationMap ().entrySet ()) {
                Double key = entry.getKey ();
                if (key - lowerBound > 0) {
                    System.out.println ("klucz" + key);
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
