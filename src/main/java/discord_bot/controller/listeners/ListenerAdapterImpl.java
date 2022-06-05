package discord_bot.controller.listeners;

import discord_bot.view.Topic;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ListenerAdapterImpl extends ListenerAdapter {
    private static final int DISCORD_MAX_MESSAGE_LEN = 2000;
    private static final int ERROR_MARGIN = 120;
    protected static final String NEXT_RESULT_EMOTE = "▶️";
    protected static final String PREV_RESULT_EMOTE = "◀️";

    protected String formatResponse(Topic topic) {
        StringBuilder out = new StringBuilder();

        out.append("**").append(":point_right:  " + topic.getTitle()).append("**");
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
                out.append(":mag_right:  Read more at: https://en.wikipedia.org/?curid=").append(topic.getId());
                break;
            case STACKOVERLOW:
                out.append(":mag_right:  Read more at: https://stackoverflow.com/questions/").append(topic.getId());
                break;
            case ASKUBUNTU:
                out.append(":mag_right:  Read more at: https://askubuntu.com/questions/").append(topic.getId());
                break;
            case SERVERFAULT:
                out.append(":mag_right:  Read more at: https://serverfault.com/questions/").append(topic.getId());
                break;
            case SUPERUSER:
                out.append(":mag_right:  Read more at: https://superuser.com/questions/").append(topic.getId());
                break;
            case MATH:
                out.append(":mag_right:  Read more at: https://math.stackexchange.com/questions/")
                        .append(topic.getId());
                break;
            case ASK_DIFFERENT:
                out.append(":mag_right:  Read more at: https://apple.stackexchange/questions/").append(topic.getId());
                break;
            case THEORETICAL_CS:
                out.append(":mag_right:  Read more at: https://cstheory.stackexchange.com/questions/")
                        .append(topic.getId());
                break;
            default:
                out.append(":face_with_monocle:  Unknown source");
                break;
        }
        int totalMatches = topic.getTotalMatches();
        if (totalMatches > 0) {
            out.append("\n:page_facing_up:  Page " + Integer.toString(topic.getIndex() + 1) + "/"
                    + Integer.toString(totalMatches));
        }

        return out.toString();
    }
}
