package settlementexpansion.util;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static List<String> findIntInString(String stringToSearch) {
        Pattern integerPattern = Pattern.compile("-?\\d+");
        Matcher matcher = integerPattern.matcher(stringToSearch);

        List<String> integerList = new ArrayList<>();
        while (matcher.find()) {
            integerList.add(matcher.group());
        }

        return integerList;
    }
}
