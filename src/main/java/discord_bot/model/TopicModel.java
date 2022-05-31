package discord_bot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import discord_bot.utils.Requester;
import discord_bot.utils.Requester.Type;
import discord_bot.view.Topic;
import discord_bot.utils.exceptions.JSONParseException;

@Service
public class TopicModel {
    private final String LINK = "https://en.wikipedia.org/w/api.php?";

    public Topic searchResultByIndex(String query, int index) throws JSONParseException {
        return searchPages(query).get(index);
    }

    public List<Topic> searchPages(String query) throws JSONParseException {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "query");
        params.put("origin", "*");
        params.put("format", "json");
        params.put("generator", "search");
        params.put("gsrnamespace", "0");
        params.put("gsrlimit", "1");
        params.put("gsrsearch", query);
        String response = Requester.executeRequest(LINK, Requester.build(params), Type.GET);
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(response);
        } catch (ParseException e) {
            throw new JSONParseException(response);
        }

        JSONObject pages = null;
        try {
            pages = (JSONObject) ((JSONObject) ((JSONObject) obj).get("query")).get("pages");
        } catch (NullPointerException e) {
            throw new JSONParseException(e.getMessage());
        }
        List<String> results = new ArrayList<>();
        for (Object page : pages.entrySet()) {
            String key = ((Map.Entry<String, Object>) page).getKey();
            JSONObject content = (JSONObject) (pages.get(key));
            results.add(content.get("title").toString());
        }
        return searchTitle(results);
    }

    private List<Topic> searchTitle(List<String> allQueries) throws JSONParseException {
        List<Topic> out = new ArrayList<>();
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "query");
        params.put("prop", "extracts");
        params.put("exintro", "");
        params.put("explaintext", "");
        params.put("format", "json");

        for (String query : allQueries) {
            params.put("titles", query);
            String response = Requester.executeRequest(LINK, Requester.build(params), Type.GET);

            JSONParser parser = new JSONParser();
            Object obj = null;
            try {
                obj = parser.parse(response);
            } catch (ParseException e) {
                throw new JSONParseException(response);
            }
            JSONObject pages = (JSONObject) ((JSONObject) ((JSONObject) obj).get("query")).get("pages");

            Topic currentTopic = new Topic();
            for (Object page : pages.entrySet()) {
                String key = ((Map.Entry<String, Object>) page).getKey();
                JSONObject content = (JSONObject) (pages.get(key));
                currentTopic.setTitle(content.get("title").toString());
                currentTopic.setContent(content.get("extract").toString());
                currentTopic.setPageId(Integer.parseInt(content.get("pageid").toString()));
                out.add(currentTopic);
                break;
            }
        }
        return out;
    }
}
