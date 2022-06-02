package discord_bot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import discord_bot.model.searcher.Searcher;
import discord_bot.view.Topic;
import discord_bot.utils.exceptions.JSONParseException;

@Service
public class TopicModel {
    private Searcher searcher;

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    public Topic searchResultByIndex(String query, int index) throws JSONParseException {
        List<String> searchTitles = new ArrayList<>();
        JSONObject pages = searcher.searchPages(query);
        for (Object page : pages.entrySet()) {
            String key = ((Map.Entry<String, Object>) page).getKey();
            JSONObject content = (JSONObject) (pages.get(key));
            searchTitles.add(content.get("title").toString());
        }

        List<Topic> topics = new ArrayList<>();
        List<JSONObject> mainPages = searcher.searchTitle(searchTitles);
        for (JSONObject object : mainPages) {
            for (Object page : object.entrySet()) {
                String key = ((Map.Entry<String, Object>) page).getKey();
                JSONObject content = (JSONObject) (object.get(key));

                Topic currentTopic = new Topic();
                currentTopic.setTitle(content.get("title").toString());
                currentTopic.setContent(content.get("extract").toString());
                currentTopic.setPageId(Integer.parseInt(content.get("pageid").toString()));

                topics.add(currentTopic);
                break;
            }
        }
        return topics.get(index);
    }
}
