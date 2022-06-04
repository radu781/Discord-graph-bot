package discord_bot.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

public class Requester {
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final JSONParser jsonParser = new JSONParser();
    private static final HashMap<String, String> commonSymbols = new HashMap<>() {
        {
            put(" ", "%20");
            put("#", "%23");
            put("\\$", "%24");
            put("%", "%25");
            put("&", "%26");
            put("@", "%40");
            put("`", "%60");
            put("/", "%2F");
            put(":", "%3A");
            put(";", "%3B");
            put("<", "%3C");
            put("=", "%3D");
            put(">", "%3E");
            put("\\?", "%3F");
            put("\\[", "%5B");
            put("\\\\", "%5C");
            put("\\]", "%5D");
            put("\\^", "%5E");
            put("\\{", "%7B");
            put("\\|", "%7C");
            put("\\}", "%7D");
            put("~", "%7E");
            put("“", "%22");
            put("‘", "%27");
            put("\\+", "%2B");
            put(",", "%2C");
        }
    };

    public enum Type {
        GET
    }

    public static String build(HashMap<String, String> values) {
        StringBuilder out = new StringBuilder();

        for (Map.Entry<String, String> pair : values.entrySet()) {
            out.append(pair.getKey());
            out.append("=");
            String value = pair.getValue();
            // for (Map.Entry<String, String> mapping : commonSymbols.entrySet()) {
            //     value = value.replaceAll(mapping.getKey(), mapping.getValue());
            // }
            out.append(value);
            out.append("&");
        }

        return out.substring(0, out.length() - 1);
    }

    public static JSONObject executeRequest(String link, String urlParameters, Type type) {
        urlParameters = urlParameters.replaceAll(" ", "%20");
        HttpRequestBase request;
        switch (type) {
            case GET:
                request = new HttpGet(link + urlParameters);
                return getResponse(request);

            default:
                return null;
        }
    }

    private static JSONObject getResponse(HttpRequestBase request) {
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            return null;
        }
        HttpEntity entity = response.getEntity();
        Header headers = entity.getContentType();
        System.out.println(headers);

        String result;
        try {
            result = EntityUtils.toString(entity);
        } catch (ParseException | IOException e) {
            return null;
        }
        try {
            return (JSONObject) jsonParser.parse(result);
        } catch (org.json.simple.parser.ParseException e) {
            return null;
        }
    }
}
