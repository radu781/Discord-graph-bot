package discord_bot.model.searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import discord_bot.utils.Requester;
import discord_bot.utils.Requester.Type;
import discord_bot.utils.enums.Table;
import discord_bot.utils.exceptions.JSONParseException;
import discord_bot.view.Topic;

public class StackExchangeSearcher implements Searcher {
    private static final String LINK = "https://api.stackexchange.com/2.3/search/advanced?";
    private String site;
    private Table table;

    @Override
    public void setType(Table table) {
        this.table = table;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public JSONObject searchPages(String query) throws JSONParseException {
        HashMap<String, String> params = new HashMap<>();
        params.put("order", "desc");
        params.put("sort", "votes");
        params.put("accepted", "True");
        params.put("views", "20");
        params.put("answers", "1");
        params.put("site", site);
        params.put("q", query);
        JSONObject response = Requester.executeRequest(LINK, Requester.build(params), Type.GET);

        if (response != null) {
            return response;
        } else {
            throw new JSONParseException("Got null response");
        }
    }

    @Override
    public List<JSONObject> searchTitle(List<Topic> allQueries) throws JSONParseException {
        return null;
    }

    @Override
    public List<Topic> unpackPages(JSONObject pages) {
        List<Topic> searchTitles = new ArrayList<>();
        JSONArray items = (JSONArray) pages.get("items");
        for (int i = 0; i < items.size(); i++) {
            JSONObject obj = (JSONObject) items.get(i);
            String title = (String) obj.get("title");
            long questionId = (long) obj.get("question_id");

            Topic topic = new Topic();
            topic.setTitle(title);
            topic.setId((int) questionId);
            searchTitles.add(topic);
        }
        return searchTitles;
    }

    @Override
    public List<Topic> unpackTitles(List<JSONObject> mainPages) {
        return null;
    }
}
