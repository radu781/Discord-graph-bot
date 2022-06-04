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
    private final String TABLE_NAME;

    public enum Table {
        WIKIPEDIA,
        STACKEXCHANGE
    }

    public TopicDAO(Table table) {
        switch (table) {
            case WIKIPEDIA:
                TABLE_NAME = "wikipedia";
                break;
            case STACKEXCHANGE:
                TABLE_NAME = "stackexchange";
                break;
            default:
                TABLE_NAME = "";
        }
    }

    public String getTABLE_NAME() {
        return TABLE_NAME;
    }

    public List<String> getRelevantTitles(String userPrompt) {
        List<String> out = new ArrayList<>();
        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT title FROM " + TABLE_NAME + "_pages WHERE user_prompt = ?");
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
        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM " + TABLE_NAME + "_articles WHERE title = ?");
            statement.setString(1, title);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                Topic topic = new Topic();
                topic.setId(result.getInt("pageid"));
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
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + TABLE_NAME + "_pages (user_prompt, title) VALUES(?, ?)");
            statement.setString(1, userPrompt);
            statement.setString(2, title);
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertArticle(Topic topic) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO " + TABLE_NAME + "_articles (pageid, title, content) VALUES(?, ?, ?)");
            statement.setInt(1, topic.getId());
            statement.setString(2, topic.getTitle());
            statement.setString(3, topic.getContent());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertMessage(Topic topic, long messageId, String userPrompt) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO messages (messageid, pageid, user_prompt) VALUES(?, ?, ?)");
            statement.setLong(1, messageId);
            statement.setInt(2, topic.getId());
            statement.setString(3, userPrompt);
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUserPrompt(long messageId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT user_prompt FROM messages WHERE messageid = ?");
            statement.setLong(1, messageId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("user_prompt");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPromptIndex(long messageId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT current_index FROM messages WHERE messageid = ?");
            statement.setLong(1, messageId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt("current_index");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void incrementIndexBy(long messageId, int count) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE messages SET current_index = MOD(current_index + ? + @count_number := (SELECT count(*) FROM (SELECT * FROM messages) AS msg JOIN "
                            + TABLE_NAME
                            + "_pages AS wp ON msg.user_prompt = wp.user_prompt AND msg.messageid = ? GROUP BY msg.user_prompt), @count_number) WHERE messageid = ?");
            statement.setInt(1, count);
            statement.setLong(2, messageId);
            statement.setLong(3, messageId);
            System.out.println(statement.toString());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
