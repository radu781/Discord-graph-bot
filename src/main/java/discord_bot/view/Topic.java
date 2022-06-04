package discord_bot.view;

public class Topic {
    private String title = "not found";
    private String content = "not found";
    private int id = -1;
    private boolean ignoreContent = false;
    private String source;

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

    public void setIgnoreContent(boolean ignoreContent) {
        this.ignoreContent = ignoreContent;
    }

    public boolean getIgnoreContent() {
        return ignoreContent;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
