package discord_bot.listeners;

import java.util.HashMap;

import discord_bot.model.TopicModel;
import discord_bot.view.Topic;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
    private TopicModel topicModel = new TopicModel();
    private HashMap<Long, String> messages;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT)) {
            String input = event.getMessage().getContentDisplay();
            if (input.startsWith("/search")) {
                String searchMe = input.substring("/search".length()+1);
                messages.put(event.getMessageIdLong(), searchMe);
                StringBuilder sendMe = new StringBuilder();
                Topic topic = topicModel.search(searchMe);
                sendMe.append("**").append(topic.getTitle()).append("**")
                        .append("\n").append(topic.getContent());
                if (sendMe.length() > 2000) {
                    sendMe = new StringBuilder(sendMe.substring(0, 1900)).append("...");
                }
                if (!sendMe.toString().endsWith("\n")) {
                    sendMe.append("\n");
                }
                sendMe.append("Read more at: https://en.wikipedia.org/?curid=").append(topic.getPageId());
                event.getChannel().sendMessage(sendMe).queue();
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        System.out.println("MessageListener.onSlashCommandInteraction()");
    }

    public MessageListener(HashMap<Long, String> messages) {
        this.messages = messages;
    }
}
