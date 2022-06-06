package discord_bot.model.searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import discord_bot.utils.Requester;
import discord_bot.utils.Requester.Type;
import discord_bot.utils.enums.SourceType;
import discord_bot.utils.exceptions.JSONParseException;
import discord_bot.view.Topic;

public class WikipediaSearcher implements Searcher {
    private static final String LINK = "https://en.wikipedia.org/w/api.php?";
    private SourceType table;

    @Override
    public void setType(SourceType table) {
        this.table = table;
    }

    @Override
    public SourceType getType() {
        return table;
    }

    @Override
    public void setSite(String site) {
    }

    @Override
    public JSONObject searchPages(String query) throws JSONParseException {
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "query");
        params.put("origin", "*");
        params.put("format", "json");
        params.put("generator", "search");
        params.put("gsrnamespace", "0");
        params.put("gsrlimit", "5");
        params.put("gsrsearch", query);
        JSONObject response = Requester.executeRequest(LINK, Requester.build(params), Type.GET);

        JSONObject pages = null;
        try {
            pages = (JSONObject) ((JSONObject) ((JSONObject) response).get("query")).get("pages");
        } catch (NullPointerException e) {
            throw new JSONParseException(e.getMessage());
        }
        return pages;
    }

    @Override
    public List<JSONObject> searchTitle(List<Topic> allQueries) throws JSONParseException {
        List<JSONObject> out = new ArrayList<>();
        HashMap<String, String> params = new HashMap<>();
        params.put("action", "query");
        params.put("prop", "extracts");
        params.put("exintro", "");
        params.put("explaintext", "");
        params.put("format", "json");

        for (Topic topic : allQueries) {
            params.put("titles", topic.getTitle());
            JSONObject response = Requester.executeRequest(LINK, Requester.build(params), Type.GET);
            JSONObject pages = (JSONObject) ((JSONObject) ((JSONObject) response).get("query")).get("pages");
            out.add(pages);
        }
        return out;
    }

    @Override
    public List<Topic> unpackPages(JSONObject pages) {
        List<Topic> searchTitles = new ArrayList<>();
        for (Object page : pages.entrySet()) {
            String key = ((Map.Entry<String, Object>) page).getKey();
            Topic currentTopic = new Topic();
            JSONObject content = (JSONObject) (pages.get(key));
            currentTopic.setTitle(content.get("title").toString());
            currentTopic.setId(Integer.parseInt(content.get("pageid").toString()));
            currentTopic.setSource(table);
            searchTitles.add(currentTopic);
        }
        return searchTitles;
    }

    @Override
    public List<Topic> unpackTitles(List<JSONObject> mainPages) {
        List<Topic> topics = new ArrayList<>();
        for (JSONObject object : mainPages) {
            for (Object page : object.entrySet()) {
                String key = ((Map.Entry<String, Object>) page).getKey();
                JSONObject content = (JSONObject) (object.get(key));

                try {
                    Topic currentTopic = new Topic();
                    currentTopic.setTitle(content.get("title").toString());
                    currentTopic.setContent(formatString(content.get("extract").toString()));
                    currentTopic.setId(Integer.parseInt(content.get("pageid").toString()));
                    currentTopic.setSource(table);
                    topics.add(currentTopic);
                } catch (NullPointerException e) {
                }

                break;
            }
        }
        return topics;
    }
}
