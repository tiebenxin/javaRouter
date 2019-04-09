package android.text;

/**
 * Created by Liszt on 2019/4/6.
 */

public class TextUtils {
    public static boolean isEmpty(CharSequence ch) {
        if (ch == null || ch.equals("")) {
            return true;
        }
        return false;
    }
}
