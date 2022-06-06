package discord_bot.view;

import discord_bot.utils.enums.SourceType;

public class Topic {
    private String title = "not found";
    private String content = "";
    private int id = -1;
    private SourceType source = SourceType.UNKNOWN;
    private int index;
    private int totalMatches;

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

    public void setSource(SourceType source) {
        this.source = source;
    }

    public SourceType getSource() {
        return source;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public int getTotalMatches() {
        return totalMatches;
    }
}
