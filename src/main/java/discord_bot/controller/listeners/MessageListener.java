package discord_bot.controller.listeners;

import discord_bot.model.TopicModel;
import discord_bot.model.searcher.Searcher;
import discord_bot.model.searcher.StackExchangeSearcher;
import discord_bot.model.searcher.WikipediaSearcher;
import discord_bot.utils.database.TopicDAO;
import discord_bot.utils.enums.Table;
import discord_bot.utils.exceptions.JSONParseException;
import discord_bot.view.Topic;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class MessageListener extends ListenerAdapterImpl {
    private TopicModel topicModel = new TopicModel();
    private Searcher searcher;
    private final TopicDAO topicDao = new TopicDAO();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        switch (event.getName()) {
            case "search-wiki":
                event.deferReply().queue();
                OptionMapping option = event.getOption("query");
                if (option == null) {
                    event.getHook().sendMessage("Unexpected error").queue();
                    return;
                }
                searcher = new WikipediaSearcher();
                searcher.setType(Table.WIKIPEDIA);
                topicModel.setSearcher(searcher);

                getData(event, option.getAsString(), Table.WIKIPEDIA);
                break;

            case "search-stack":
                event.deferReply().queue();
                OptionMapping query = event.getOption("query");
                OptionMapping source = event.getOption("source");
                if (query == null || source == null) {
                    event.getHook().sendMessage("Unexpected error").queue();
                    return;
                }
                searcher = new StackExchangeSearcher();
                searcher.setSite(source.getAsString());
                searcher.setType(Table.fromString(source.getAsString()));
                topicModel.setSearcher(searcher);

                getData(event, query.getAsString(), Table.fromString(source.getAsString()));
                break;

            default:
                event.deferReply().queue();
                event.getHook().sendMessage("This command was removed, wait for it to disappear from Discord UI")
                        .queue();
                break;
        }
    }

    private void getData(SlashCommandInteractionEvent event, String query, Table table) {
        Topic topic;
        try {
            topic = topicModel.getResultByIndex(query, 0, false, table);
        } catch (JSONParseException e) {
            event.getHook().sendMessage("Query failed\n" + e.getMessage()).queue();
            return;
        }
        topic.setSource(table);
        event.getHook().sendMessage(formatResponse(topic)).queue((message) -> {
            long replyId = event.getHook().getInteraction().getMessageChannel().getLatestMessageIdLong();
            topicDao.insertMessage(topic, replyId, query);
            topic.setTotalMatches(topicDao.getTotalMatches(replyId));
            MessageChannel channel = event.getChannel();
            channel.addReactionById(replyId, PREV_RESULT_EMOTE).queue();
            channel.addReactionById(replyId, NEXT_RESULT_EMOTE).queue();
        });
    }
}
