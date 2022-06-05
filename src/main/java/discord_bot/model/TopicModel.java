package discord_bot.model;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import discord_bot.model.searcher.Searcher;
import discord_bot.view.Topic;
import discord_bot.utils.database.TopicDAO;
import discord_bot.utils.enums.Table;
import discord_bot.utils.exceptions.JSONParseException;

@Service
public class TopicModel {
    private Searcher searcher;
    private TopicDAO topicDAO;
    private boolean ignoreContent = false;

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    public void setTopicDAO(TopicDAO topicDAO) {
        this.topicDAO = topicDAO;
    }

    public TopicDAO getTopicDAO() {
        return topicDAO;
    }

    public void setIgnoreContent(boolean ignoreContent) {
        this.ignoreContent = ignoreContent;
    }

    public boolean getIgnoreContent() {
        return ignoreContent;
    }

    public Topic getResultByIndex(String query, int index, boolean readOnly, Table table)
            throws JSONParseException, ArrayIndexOutOfBoundsException {
        List<String> dbTopics = topicDAO.getRelevantTitles(query);
        if (!dbTopics.isEmpty()) {
            if (index < 0 || index >= dbTopics.size()) {
                throw new ArrayIndexOutOfBoundsException(Integer.toString(dbTopics.size()));
            }
            String title = dbTopics.get(index);
            Topic result = topicDAO.getTopicByTitle(title);
            if (result != null) {
                System.out.println("Query in database, skipping request");
                return result;
            }
        }

        List<Topic> titles = getTitles(query, readOnly, table);
        List<Topic> topics = getPages(titles, readOnly);
        if (topics == null) {
            ignoreContent = true;
            return titles.get(index);
        }
        return topics.get(index);
    }

    private List<Topic> getTitles(String query, boolean readOnly, Table table) throws JSONParseException {
        JSONObject pages = searcher.searchPages(query);
        List<Topic> topicTitles = searcher.unpackPages(pages);
        if (!readOnly) {
            for (Topic topic : topicTitles) {
                topic.setSource(table);
                topicDAO.insertPage(query, topic);
            }
        }
        return topicTitles;
    }

    private List<Topic> getPages(List<Topic> searchTitles, boolean readOnly) throws JSONParseException {
        List<JSONObject> mainPages = searcher.searchTitle(searchTitles);
        List<Topic> topicTitles = searcher.unpackTitles(mainPages);
        if (topicTitles == null) {
            return null;
        }
        if (!readOnly) {
            for (Topic topic : topicTitles) {
                topicDAO.insertArticle(topic);
            }
        }
        return topicTitles;
    }
}
