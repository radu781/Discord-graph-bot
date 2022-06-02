package discord_bot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import discord_bot.model.searcher.Searcher;
import discord_bot.view.Topic;
import discord_bot.utils.database.TopicDAO;
import discord_bot.utils.exceptions.JSONParseException;

@Service
public class TopicModel {
    private Searcher searcher;
    private TopicDAO topicDAO = new TopicDAO();

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    public Topic searchResultByIndex(String query, int index, boolean readOnly)
            throws JSONParseException, ArrayIndexOutOfBoundsException {
        List<String> dbTopics = topicDAO.getRelevantTitles(query);
        if (index < 0 || index >= dbTopics.size()) {
            throw new ArrayIndexOutOfBoundsException(Integer.toString(dbTopics.size()));
        }
        if (!dbTopics.isEmpty()) {
            String title = dbTopics.get(index);
            Topic result = topicDAO.getTopic(title);
            if (result != null) {
                System.out.println("Query in database, skipping request");
                return result;
            }
        }

        List<Topic> titles = searchTitles(query, readOnly);
        List<Topic> topics = searchPages(titles, readOnly);
        return topics.get(index);
    }

    private List<Topic> searchTitles(String query, boolean readOnly) throws JSONParseException {
        List<Topic> searchTitles = new ArrayList<>();
        JSONObject pages = searcher.searchPages(query);
        for (Object page : pages.entrySet()) {
            String key = ((Map.Entry<String, Object>) page).getKey();
            Topic currentTopic = new Topic();
            JSONObject content = (JSONObject) (pages.get(key));
            currentTopic.setTitle(content.get("title").toString());
            currentTopic.setPageId(Integer.parseInt(content.get("pageid").toString()));
            searchTitles.add(currentTopic);
            if (!readOnly) {
                topicDAO.insertPage(query, currentTopic.getTitle());
            }
        }
        return searchTitles;
    }

    private List<Topic> searchPages(List<Topic> searchTitles, boolean readOnly) throws JSONParseException {
        List<Topic> topics = new ArrayList<>();
        List<JSONObject> mainPages = searcher.searchTitle(searchTitles);
        for (JSONObject object : mainPages) {
            for (Object page : object.entrySet()) {
                String key = ((Map.Entry<String, Object>) page).getKey();
                JSONObject content = (JSONObject) (object.get(key));

                try {
                    Topic currentTopic = new Topic();
                    currentTopic.setTitle(content.get("title").toString());
                    currentTopic.setContent(content.get("extract").toString());
                    currentTopic.setPageId(Integer.parseInt(content.get("pageid").toString()));
                    topics.add(currentTopic);
                    if (!readOnly) {
                        topicDAO.insertArticle(currentTopic);
                    }
                } catch (NullPointerException e) {
                }

                break;
            }
        }
        return topics;
    }
}
