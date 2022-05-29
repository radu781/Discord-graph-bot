package discord_bot;

import java.util.HashMap;

import javax.security.auth.login.LoginException;

import discord_bot.listeners.MessageListener;
import discord_bot.listeners.ReactionListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class DiscordBot {
    private static final Dotenv env = Dotenv.configure().directory("src/resources").filename("config.env").load();
    private static final String TOKEN = env.get("token");
    private static HashMap<Long, String> messages = new HashMap<>();

    public static void run() {
        JDA bot = null;
        try {
            bot = JDABuilder.createDefault(TOKEN)
                    .setActivity(Activity.competing("the helper bot championship"))
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
            return;
        }
        bot.addEventListener(new MessageListener(messages));
        bot.addEventListener(new ReactionListener(messages));
    }
}
