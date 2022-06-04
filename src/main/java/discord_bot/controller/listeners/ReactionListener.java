package discord_bot.controller.listeners;

import java.util.List;

import discord_bot.utils.database.TopicDAO;
import discord_bot.view.Topic;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ReactionListener extends ListenerAdapterImpl {
    private final TopicDAO topicDAO = new TopicDAO();

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getReaction().isSelf()) {
            long messageId = event.getReaction().getMessageIdLong();

            var a = event.getReactionEmote().getName();
            MessageChannel channel1 = event.getChannel();
            channel1.addReactionById(messageId, a).queue();

            int index = topicDAO.getPromptIndex(messageId);
            if (index == -1) {
                return;
            }
            String userPrompt = topicDAO.getUserPrompt(messageId);
            List<String> topics = topicDAO.getRelevantTitles(userPrompt);
            topicDAO.incrementIndexBy(messageId, 1);
            Topic topic = topicDAO.getTopic(topics.get(index));

            MessageChannel channel = event.getChannel();
            channel.editMessageById(Long.toString(messageId), formatResponse(topic)).queue();
        }
    }
}
