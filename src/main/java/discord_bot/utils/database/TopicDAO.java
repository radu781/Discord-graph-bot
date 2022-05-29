package discord_bot.utils.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class TopicDAO {
    private Connection connection = DatabaseManager.getInstance().getConnection();
    private static final String TABLE_NAME = "messages";

    public String getUserInput(int id) {
        try {
            PreparedStatement statement = connection
                    .prepareStatement("select user_input from " + TABLE_NAME + " where id = ?");
            statement.setInt(1, id);
            System.out.println(statement);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                return result.getString("user_input");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
