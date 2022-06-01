package discord_bot.controller.listeners;

import discord_bot.view.Topic;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ListenerAdapterImpl extends ListenerAdapter {
    private static final int DISCORD_MAX_MESSAGE_LEN = 2000;

    protected String formatResponse(Topic topic) {
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
}
