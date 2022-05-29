package discord_bot.utils;

import java.util.HashMap;
import java.util.Map;

public class ParamBuilder {
    public static String build(HashMap<String, String> values) {
        StringBuilder out = new StringBuilder();

        for (Map.Entry<String, String> pair : values.entrySet()) {
            out.append(pair.getKey());
            out.append("=");
            out.append(pair.getValue());
            out.append("&");
        }

        return out.substring(0, out.length() - 1);
    }
}
