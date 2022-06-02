package discord_bot.utils.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import discord_bot.view.Topic;

public class TopicDAO {
    private Connection connection = DatabaseManager.getInstance().getConnection();

    public List<String> getRelevantTitles(String userPrompt) {
        final String TABLE_NAME = "wikipedia_pages";
        List<String> out = new ArrayList<>();
        try {
            PreparedStatement statement = connection
                    .prepareStatement("select title from " + TABLE_NAME + " where user_prompt = ?");
            statement.setString(1, userPrompt);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                out.add(result.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return out;
    }

    public Topic getTopic(String title) {
        final String TABLE_NAME = "wikipedia_articles";
        try {
            PreparedStatement statement = connection
                    .prepareStatement("select * from " + TABLE_NAME + " where title = ?");
            statement.setString(1, title);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                Topic topic = new Topic();
                topic.setPageId(result.getInt("pageid"));
                topic.setTitle(result.getString("title"));
                topic.setContent(result.getString("content"));
                return topic;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void insertPage(String userPrompt, String title) {
        final String TABLE_NAME = "wikipedia_pages";
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into " + TABLE_NAME + "(user_prompt, title) values(?, ?)");
            statement.setString(1, userPrompt);
            statement.setString(2, title);
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertArticle(Topic topic) {
        final String TABLE_NAME = "wikipedia_articles";
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "insert into " + TABLE_NAME + "(pageid, title, content) values(?, ?, ?)");
            statement.setInt(1, topic.getPageId());
            statement.setString(2, topic.getTitle());
            statement.setString(3, topic.getContent());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
