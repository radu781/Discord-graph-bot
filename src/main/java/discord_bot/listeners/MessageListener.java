package discord_bot.listeners;

import java.util.HashMap;

import discord_bot.model.TopicModel;
import discord_bot.view.Topic;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class MessageListener extends ListenerAdapter {
    private TopicModel topicModel = new TopicModel();
    private static final int DISCORD_MAX_MESSAGE_LEN = 2000;
    private HashMap<Long, String> messages;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("search")) {
            event.deferReply().queue();
            OptionMapping option = event.getOption("query");
            if (option == null) {
                return;
            }
            Topic topic = topicModel.searchPages(option.getAsString());
            event.getHook().sendMessage(formatResponse(topic)).queue();
        }
    }

    private String formatResponse(Topic topic) {
        StringBuilder out = new StringBuilder();

        out.append("**").append(topic.getTitle()).append("**")
                .append("\n").append(topic.getContent());
        if (out.length() > DISCORD_MAX_MESSAGE_LEN) {
            out = new StringBuilder(out.substring(0, DISCORD_MAX_MESSAGE_LEN - 100)).append(" ...");
        }
        if (!out.toString().endsWith("\n")) {
            out.append("\n");
        }
        out.append("Read more at: https://en.wikipedia.org/?curid=").append(topic.getPageId());

        return out.toString();
    }

    public MessageListener(HashMap<Long, String> messages) {
        this.messages = messages;
    }
}
