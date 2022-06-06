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

    public enum Type {
        GET
    }

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
