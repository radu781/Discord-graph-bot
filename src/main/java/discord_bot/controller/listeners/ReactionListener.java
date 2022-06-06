package discord_bot.controller.listeners;

import java.util.List;

import discord_bot.utils.database.TopicDAO;
import discord_bot.utils.enums.SourceType;
import discord_bot.view.Topic;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ReactionListener extends ListenerAdapterImpl {
    private final TopicDAO topicDAO = new TopicDAO();

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getReaction().isSelf()) {
            long messageId = event.getReaction().getMessageIdLong();
            switch (event.getReactionEmote().getName()) {
                case NEXT_RESULT_EMOTE:
                    topicDAO.incrementIndexBy(messageId, 1);
                    break;
                case PREV_RESULT_EMOTE:
                    topicDAO.incrementIndexBy(messageId, -1);
                    break;
                default:
                    return;
            }

            int index = topicDAO.getPromptIndex(messageId);
            if (index == -1) {
                return;
            }
            String userPrompt = topicDAO.getUserPrompt(messageId);
            SourceType table = topicDAO.getSourceById(messageId);
            List<Topic> titles = topicDAO.getTopicsByUserPrompt(userPrompt, table);
            Topic topic = topicDAO.getTopicByTitle(titles.get(index).getTitle());
            topic.setIndex(topicDAO.getPromptIndex(messageId));
            topic.setTotalMatches(topicDAO.getTotalMatches(messageId));

            MessageChannel channel = event.getChannel();
            MessageReaction reaction = new MessageReaction(channel, event.getReactionEmote(), messageId, false, 0);
            reaction.removeReaction(event.getUser()).queue();
            channel.editMessageById(Long.toString(messageId), formatResponse(topic)).queue();
        }
    }
}
