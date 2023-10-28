package settlementexpansion.util;


/**
 * This final utility class outlines static methods that operate on or return
 * a {@link String}.
 */
public class StringUtil {

    private StringUtil() {}

    public static String capitalize(String str) {
        return str.substring(0,1).toUpperCase() +
                str.substring(1);
    }
}
