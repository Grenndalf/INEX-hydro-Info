package RegularClasses.Utils;

import java.util.ResourceBundle;

public class Utils {
    private static final String BUNDLES = "Bundles.labels";
    public static ResourceBundle getResourceBundle() {
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLES);
        return bundle;
    }
}
