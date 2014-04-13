package apt.app.giheung;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jaebeomlee on 2014. 4. 13..
 */
public class StringUtils {
    public static int countOccurrences(String input, String word){
        int count =0;
        Pattern pattern = Pattern.compile(word);
        Matcher matcher = pattern.matcher(input);

        while(matcher.find()){
            count++;
        }
        return count;
    }
}
