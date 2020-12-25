package RegularClasses.Utils;

import java.util.ResourceBundle;

public class Utils {
    private static final String BUNDLES = "Bundles.labels";

    public static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle(BUNDLES);
    }

    public static double divideIntegers(int a, int b) {
        if (b == 0) {
            return 0.0;
        } else {
            double result = (double) a / (double) b * 100;
            result = result / 100;
            return result;
        }
    }
}
