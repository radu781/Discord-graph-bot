package discord_bot.utils.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import discord_bot.view.Topic;

import java.sql.ResultSet;

public class TopicDAO {
    private Connection connection = DatabaseManager.getInstance().getConnection();

    public List<String> getRelevantTitle(String userPrompt) {
        final String TABLE_NAME = "wikipedia_pages";
        List<String> out = new ArrayList<>();
        try {
            PreparedStatement statement = connection
                    .prepareStatement("select relevant_title from " + TABLE_NAME + " where id = ?");
            statement.setString(1, userPrompt);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                out.add(result.getString("relevant_title"));
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
                topic.setPageId(result.getInt("id"));
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
}
