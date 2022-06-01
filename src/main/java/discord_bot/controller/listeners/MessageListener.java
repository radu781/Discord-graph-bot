package discord_bot.controller.listeners;

import discord_bot.model.TopicModel;
import discord_bot.utils.exceptions.JSONParseException;
import discord_bot.view.Topic;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class MessageListener extends ListenerAdapterImpl {
    private TopicModel topicModel = new TopicModel();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("search")) {
            event.deferReply().queue();
            OptionMapping option = event.getOption("query");
            if (option == null) {
                event.getHook().sendMessage("Unexpected error").queue();
                return;
            }
            Topic topic;
            try {
                topic = topicModel.searchResultByIndex(option.getAsString(), 0);
            } catch (JSONParseException e) {
                event.getHook().sendMessage("Query failed\n" + e.getMessage()).queue();
                return;
            }
            event.getHook().sendMessage(formatResponse(topic)).queue();
        }
    }
}
