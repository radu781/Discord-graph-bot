package discord_bot.model.searcher;

import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import discord_bot.utils.Requester;
import discord_bot.utils.Requester.Type;
import discord_bot.utils.exceptions.JSONParseException;
import discord_bot.view.Topic;

public class StackExchangeSearcher implements Searcher {
    private static final String LINK = "https://api.stackexchange.com/2.3/search/advanced?";
    private String site;

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public JSONObject searchPages(String query) throws JSONParseException {
        HashMap<String, String> params = new HashMap<>();
        params.put("order", "desc");
        params.put("site", site);
        params.put("q", query);
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
            pages = (JSONObject) ((JSONObject) ((JSONObject) obj).get("items")).get("title");
        } catch (NullPointerException e) {
            throw new JSONParseException(e.getMessage());
        }
        return pages;
    }

    @Override
    public List<JSONObject> searchTitle(List<Topic> allQueries) throws JSONParseException {
        return null;
    }
}
