package discord_bot.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Requester {
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

    public static String executeRequest(String link, String urlParameters, Type type) {
        HttpURLConnection connection = null;
        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "Request failed";
        }
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return "Request failed";
        }
        try {
            connection.setRequestMethod(type.toString());
        } catch (ProtocolException e) {
            e.printStackTrace();
            return "Request failed";
        }

        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");
        connection.setUseCaches(false);
        connection.setDoOutput(true);

        try {
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append("\n");
            }
            rd.close();
            if (connection != null) {
                connection.disconnect();
            }
            return response.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }
}
