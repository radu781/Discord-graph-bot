package discord_bot.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import discord_bot.utils.ParamBuilder;
import discord_bot.view.Topic;

@Service
public class TopicModel {
    private final String LINK = "https://en.wikipedia.org/w/api.php?";

    public Topic search(String query) {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "query");
        params.put("origin", "*");
        params.put("format", "json");
        params.put("generator", "search");
        params.put("gsrnamespace", "0");
        params.put("gsrlimit", "5");
        params.put("gsrsearch", query);
        Topic out = new Topic();
        String response = executeRequest(ParamBuilder.build(params), "GET");
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(response);
        } catch (ParseException e) {
            e.printStackTrace();
            return out;
        }
        List<String> results = new ArrayList<>();
        JSONObject pages = (JSONObject) ((JSONObject) ((JSONObject) obj).get("query")).get("pages");
        for (Object page : pages.entrySet()) {
            String key = ((Map.Entry<String, Object>) page).getKey();
            JSONObject content = (JSONObject) (pages.get(key));
            results.add(content.get("title").toString());
        }
        return searchInner(results.get(0));
    }

    private Topic searchInner(String query) {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "query");
        params.put("prop", "extracts");
        params.put("exintro", "");
        params.put("explaintext", "");
        params.put("format", "json");
        params.put("titles", query);
        String response = executeRequest(ParamBuilder.build(params), "GET");
        Topic out = new Topic();

        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(response);
        } catch (ParseException e) {
            e.printStackTrace();
            return out;
        }
        JSONObject pages = (JSONObject) ((JSONObject) ((JSONObject) obj).get("query")).get("pages");
        for (Object page : pages.entrySet()) {
            String key = ((Map.Entry<String, Object>) page).getKey();
            JSONObject content = (JSONObject) (pages.get(key));
            out.setTitle(content.get("title").toString());
            out.setContent(content.get("extract").toString());
            out.setPageId(Integer.parseInt(content.get("pageid").toString()));
            break;
        }
        return out;
    }

    private String executeRequest(String urlParameters, String type) {
        HttpURLConnection connection = null;
        URL url = null;
        try {
            url = new URL(LINK);
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
            connection.setRequestMethod(type);
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
            e.printStackTrace();
            return "Request failed";
        }
    }
}
