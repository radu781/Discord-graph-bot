package discord_bot.listeners;

import java.util.HashMap;
import java.util.List;

import org.json.simple.parser.ParseException;

import discord_bot.model.TopicModel;
import discord_bot.view.Topic;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter {
    private TopicModel topicModel = new TopicModel();
    private final String NEXT_QUERY = "";
    private HashMap<Long, String> messages;

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getReaction().isSelf()) {
            StringBuilder sendMe = new StringBuilder();
            List<Topic> topics;
            try {
                topics = topicModel.searchPages(messages.get(event.getMessageIdLong()));
            } catch (ParseException e) {
                return;
            }
            Topic firstTopic = topics.get(0);
            sendMe.append("**").append(firstTopic.getTitle()).append("**")
                    .append("\n").append(firstTopic.getContent());
            if (sendMe.length() > 2000) {
                sendMe = new StringBuilder(sendMe.substring(0, 1900)).append("...");
            }
            if (!sendMe.toString().endsWith("\n")) {
                sendMe.append("\n");
            }
            sendMe.append("Read more at: https://en.wikipedia.org/?curid=").append(firstTopic.getPageId());
            event.getChannel().sendMessage(sendMe).queue();
        }
    }

    public ReactionListener(HashMap<Long, String> messages) {
        this.messages = messages;
    }
}
