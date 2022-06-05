package discord_bot.view;

import discord_bot.utils.enums.Table;

public class Topic {
    private String title = "not found";
    private String content = "";
    private int id = -1;
    private Table source = Table.UNKNOWN;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setId(int pageId) {
        this.id = pageId;
    }

    public int getId() {
        return id;
    }

    public void setSource(Table source) {
        this.source = source;
    }

    public Table getSource() {
        return source;
    }
}
