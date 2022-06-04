package discord_bot.controller.listeners;

import discord_bot.view.Topic;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ListenerAdapterImpl extends ListenerAdapter {
    private static final int DISCORD_MAX_MESSAGE_LEN = 2000;
    private static final int ERROR_MARGIN = 100;

    protected String formatResponse(Topic topic) {
        StringBuilder out = new StringBuilder();

        out.append("**").append(topic.getTitle()).append("**");
        if (!topic.getIgnoreContent()) {
            out.append("\n").append(topic.getContent());
        }
        if (out.length() > DISCORD_MAX_MESSAGE_LEN - ERROR_MARGIN) {
            out = new StringBuilder(out.substring(0, DISCORD_MAX_MESSAGE_LEN - ERROR_MARGIN))
                    .append(" ...");
        }
        if (!out.toString().endsWith("\n")) {
            out.append("\n");
        }
        switch (topic.getSource()) {
            case "wikipedia":
                out.append("Read more at: https://en.wikipedia.org/?curid=").append(topic.getId());
                break;
            case "stackexchange":
                out.append("Read more at: https://en.wikipedia.org/?curid=").append(topic.getId());
            default:
                break;
        }

        return out.toString();
    }
}
