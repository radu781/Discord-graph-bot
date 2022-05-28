package discord_bot.listeners;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT)) {
            System.out.printf("[%s][%s] %#s: %s%n", event.getGuild().getName(),
                    event.getChannel().getName(), event.getAuthor(), event.getMessage().getContentDisplay());

            if (event.getMessage().getContentDisplay().equals("ping")) {
                TextChannel textChannel = event.getGuild().getTextChannelsByName("general", true).get(0);
                textChannel.sendMessage("pong").queue();
            }
        } else {
            System.out.printf("[PM] %#s: %s%n", event.getAuthor(), event.getMessage().getContentDisplay());
        }
    }
}
