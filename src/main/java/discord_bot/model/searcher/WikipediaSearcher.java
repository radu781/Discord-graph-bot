package discord_bot.model.searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import discord_bot.utils.Requester;
import discord_bot.utils.Requester.Type;
import discord_bot.utils.exceptions.JSONParseException;

public class WikipediaSearcher implements Searcher {
    private final String LINK = "https://en.wikipedia.org/w/api.php?";

    @Override
    public JSONObject searchPages(String query) throws JSONParseException {
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
        return pages;
    }

    @Override
    public List<JSONObject> searchTitle(List<String> allQueries) throws JSONParseException {
        List<JSONObject> out = new ArrayList<>();
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
            out.add(pages);
        }
        return out;
    }
}
