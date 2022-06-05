package discord_bot;

import javax.security.auth.login.LoginException;

import discord_bot.controller.listeners.MessageListener;
import discord_bot.controller.listeners.ReactionListener;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class DiscordBot {
    private static final Dotenv env = Dotenv.configure().directory("src/resources").filename("config.env").load();
    private static final String TOKEN = env.get("token");
    private static final String GID = env.get("gid");
    private static final OptionData queryParameter = new OptionData(OptionType.STRING, "query", "query to search for",
            true);
    private static final OptionData sourceParameter = new OptionData(OptionType.STRING, "source",
            "source to query from", true)
            .addChoices(new Command.Choice("stackoverflow", "stackoverflow"),
                    new Command.Choice("askubuntu", "askubuntu"),
                    new Command.Choice("serverfault", "serverfault"),
                    new Command.Choice("superuser", "superuser"),
                    new Command.Choice("math", "math"),
                    new Command.Choice("apple", "apple"),
                    new Command.Choice("cstheory", "cstheory"));

    public static void run() {
        JDA bot = null;
        try {
            bot = JDABuilder.createDefault(TOKEN).setActivity(Activity.watching("over you")).build();
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

        if (GID != "") {
            addServerCommands(bot);
        } else {
            addGlobalCommands(bot);
        }
    }

    private static void addServerCommands(JDA bot) {
        Guild guild = bot.getGuildById(GID);
        if (guild != null) {
            guild.upsertCommand("search-wiki", "Search Wikipedia for your prompt")
                    .addOptions(queryParameter).queue();
            guild.upsertCommand("search-stack", "Search a StackExchange site for your prompt")
                    .addOptions(queryParameter, sourceParameter).queue();
        }
    }

    private static void addGlobalCommands(JDA bot) {
        bot.upsertCommand("search-wiki", "Search Wikipedia for your prompt")
                .addOptions(queryParameter).queue();
        bot.upsertCommand("search-stack", "Search a StackExchange site for your prompt")
                .addOptions(queryParameter, sourceParameter).queue();
    }
}
