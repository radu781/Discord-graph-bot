package discord_bot;

import javax.security.auth.login.LoginException;

import discord_bot.controller.listeners.MessageListener;
import discord_bot.controller.listeners.ReactionListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class DiscordBot {
    private static final Dotenv env = Dotenv.configure().directory("src/resources").filename("config.env").load();
    private static final String TOKEN = env.get("token");
    private static final String GID = env.get("gid");

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
        try {
            bot.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        bot.addEventListener(new MessageListener());
        bot.addEventListener(new ReactionListener());

        Guild guild = bot.getGuildById(GID);
        if (guild != null) {
            guild.upsertCommand("search", "Search Wikipedia for your prompt")
                    .addOption(OptionType.STRING, "query", "query to search for", true).queue();
        }
    }
}
