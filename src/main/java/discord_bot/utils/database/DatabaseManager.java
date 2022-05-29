package discord_bot.utils.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseManager {
    private final Dotenv env = Dotenv.configure().directory("src/resources").filename("config.env").load();
    private final String IP;
    private final String DATABASE;
    private final String USER;
    private final String PASSWORD;
    private final String URL;
    private static Connection connection = null;
    private static DatabaseManager instance = null;

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
            try {
                connection = DriverManager.getConnection(instance.URL, instance.USER, instance.PASSWORD);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private DatabaseManager() {
        IP = env.get("database_ip");
        DATABASE = env.get("database_db");
        USER = env.get("database_user");
        PASSWORD = env.get("database_password");
        URL = "jdbc:mysql://" + IP + ":3306/" + DATABASE;
    }

    public Connection getConnection() {
        return connection;
    }
}
