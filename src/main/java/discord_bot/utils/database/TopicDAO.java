package discord_bot.utils.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import discord_bot.utils.enums.SourceType;
import discord_bot.view.Topic;

public class TopicDAO {
    private Connection connection = DatabaseManager.getInstance().getConnection();

    public List<Topic> getTopicsByUserPrompt(String userPrompt, SourceType source) {
        List<Topic> out = new ArrayList<>();
        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM user_prompts WHERE user_prompt = ? and source = ?");
            statement.setString(1, userPrompt);
            statement.setString(2, source.getName());
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Topic topic = new Topic();
                topic.setTitle(result.getString("title"));
                topic.setSource(source);
                topic.setId(result.getInt("id"));
                out.add(topic);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return out;
        }
        return out;
    }

    public Topic getTopicByTitle(String title) {
        try {
            PreparedStatement statement = connection
                    .prepareStatement(
                            "SELECT a.id, a.title, up.source, a.content FROM articles AS a JOIN user_prompts AS up ON a.title = up.title AND a.title = ?");
            statement.setString(1, title);
            ResultSet result = statement.executeQuery();
            System.out.println(statement.toString());
            if (result.next()) {
                Topic topic = new Topic();
                topic.setId(result.getInt("id"));
                topic.setTitle(result.getString("title"));
                topic.setContent(result.getString("content"));
                topic.setSource(SourceType.fromString(result.getString("source")));
                return topic;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public Topic getTopicById(long id) {
        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM articles WHERE id = ?");
            statement.setLong(1, id);
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

    public void insertPrompt(String userPrompt, Topic topic) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO user_prompts(id, user_prompt, title, source) VALUES(?, ?, ?, ?)");
            statement.setInt(1, topic.getId());
            statement.setString(2, userPrompt);
            statement.setString(3, topic.getTitle());
            statement.setString(4, topic.getSource().getName());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertArticle(Topic topic) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO articles (id, title, content) VALUES(?, ?, ?)");
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
                    "INSERT INTO messages (messageid, pageid, user_prompt, source) VALUES(?, ?, ?, ?)");
            statement.setLong(1, messageId);
            statement.setInt(2, topic.getId());
            statement.setString(3, userPrompt);
            statement.setString(4, topic.getSource().getName());
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

    public SourceType getSourceById(long messageId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT source FROM messages WHERE messageid = ?");
            statement.setLong(1, messageId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return SourceType.fromString(result.getString("source"));
            }
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return SourceType.UNKNOWN;
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

    public void incrementIndexBy(long messageId, int amount) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE messages SET current_index = MOD(current_index + ? + @count_number := (SELECT count(*) FROM (SELECT * FROM messages) AS msg JOIN user_prompts AS wp ON msg.user_prompt = wp.user_prompt AND msg.messageid = ? GROUP BY msg.user_prompt), @count_number) WHERE messageid = ?");
            statement.setInt(1, amount);
            statement.setLong(2, messageId);
            statement.setLong(3, messageId);
            System.out.println(statement.toString());
            statement.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getTotalMatches(long messageId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM messages AS msg JOIN user_prompts AS up ON msg.user_prompt = up.user_prompt AND messageid = ?");
            statement.setLong(1, messageId);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt("count(*)");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }

    public int getTotalMatches(String userPrompt) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM user_prompts where user_prompt = ?");
            statement.setString(1, userPrompt);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getInt("count(*)");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;
    }
}
