package Helper;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

public class Base64Custom {

    public static String encode64Base(String encodedText){

        return Base64.encodeToString(encodedText.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static String decode64Base(String decodedText){

        return new String(Base64.decode(decodedText, Base64.DEFAULT));
    }
}
