package discord_bot.model;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import discord_bot.model.searcher.Searcher;
import discord_bot.view.Topic;
import discord_bot.utils.database.TopicDAO;
import discord_bot.utils.exceptions.JSONParseException;

@Service
public class TopicModel {
    private Searcher searcher;
    private final TopicDAO topicDAO = new TopicDAO();
    private boolean readOnly;

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    public Topic getResultByIndex(String query, int index)
            throws JSONParseException, ArrayIndexOutOfBoundsException {
        List<Topic> dbTopics = topicDAO.getTopicsByUserPrompt(query, searcher.getType());
        if (!dbTopics.isEmpty()) {
            if (index < 0 || index >= dbTopics.size()) {
                throw new ArrayIndexOutOfBoundsException(Integer.toString(dbTopics.size()));
            }
            String title = dbTopics.get(index).getTitle();
            Topic result = topicDAO.getTopicByTitle(title);
            result.setIndex(index);
            result.setTotalMatches(topicDAO.getTotalMatches(query));
            if (result != null) {
                System.out.println("Query in database, skipping request");
                return result;
            }
        }

        List<Topic> titles = getTitles(query);
        List<Topic> topics = getPages(titles);
        if (topics == null) {
            return titles.get(index);
        }
        return topics.get(index);
    }

    private List<Topic> getTitles(String query) throws JSONParseException {
        JSONObject pages = searcher.searchPages(query);
        List<Topic> topicTitles = searcher.unpackPages(pages);
        if (!readOnly) {
            for (Topic topic : topicTitles) {
                topic.setSource(searcher.getType());
                topicDAO.insertPrompt(query, topic);
            }
        }
        return topicTitles;
    }

    private List<Topic> getPages(List<Topic> searchTitles) throws JSONParseException {
        List<JSONObject> mainPages = searcher.searchTitle(searchTitles);
        List<Topic> topicTitles = searcher.unpackTitles(mainPages);
        if (topicTitles == null) {
            topicTitles = searchTitles;
            if (!readOnly) {
                for (Topic topic : topicTitles) {
                    topicDAO.insertArticle(topic);
                }
            }
        }
        if (!readOnly) {
            for (Topic topic : topicTitles) {
                topicDAO.insertArticle(topic);
            }
        }
        for (int index = 0; index < topicTitles.size(); index++) {
            topicTitles.get(index).setIndex(index);
            topicTitles.get(index).setTotalMatches(topicTitles.size());
        }
        return topicTitles;
    }
}
