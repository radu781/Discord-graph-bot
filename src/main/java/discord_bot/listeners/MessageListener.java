package discord_bot.listeners;

import discord_bot.model.TopicModel;
import discord_bot.view.Topic;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
    private TopicModel topicModel = new TopicModel();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT)) {
            String input = event.getMessage().getContentDisplay();
            if (input.startsWith("/search")) {
                String searchMe = input.substring("/search".length());
                StringBuilder sendMe = new StringBuilder();
                Topic topic = topicModel.search(searchMe);
                sendMe.append("**").append(topic.getTitle()).append("**")
                        .append("\n").append(topic.getContent());
                if (sendMe.length() > 2000) {
                    sendMe = new StringBuilder(sendMe.substring(0, 1900));
                }
                sendMe.append("\nRead more at: https://en.wikipedia.org/?curid=").append(topic.getPageId());
                event.getChannel().sendMessage(sendMe).queue();
            }
        }
    }
}
