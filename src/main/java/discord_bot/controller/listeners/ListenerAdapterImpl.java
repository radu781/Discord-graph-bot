package discord_bot.controller.listeners;

import discord_bot.view.Topic;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ListenerAdapterImpl extends ListenerAdapter {
    private static final int DISCORD_MAX_MESSAGE_LEN = 2000;
    private static final int ERROR_MARGIN = 100;
    protected static final String NEXT_RESULT_EMOTE = "▶️";
    protected static final String PREV_RESULT_EMOTE = "◀️";

    protected String formatResponse(Topic topic) {
        StringBuilder out = new StringBuilder();

        out.append("**").append(topic.getTitle()).append("**");
        out.append("\n").append(topic.getContent());
        if (out.length() > DISCORD_MAX_MESSAGE_LEN - ERROR_MARGIN) {
            out = new StringBuilder(out.substring(0, DISCORD_MAX_MESSAGE_LEN - ERROR_MARGIN))
                    .append(" ...");
        }
        if (!out.toString().endsWith("\n")) {
            out.append("\n");
        }
        switch (topic.getSource()) {
            case WIKIPEDIA:
                out.append("Read more at: https://en.wikipedia.org/?curid=").append(topic.getId());
                break;
            case STACKEXCHANGE:
                out.append("Read more at: https://stackoverflow.com/questions/").append(topic.getId());
                break;
            default:
                out.append("Unknown source");
                break;
        }

        return out.toString();
    }
}
