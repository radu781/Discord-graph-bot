package discord_bot.utils.enums;

public enum Table {
    WIKIPEDIA("wikipedia"),
    STACKEXCHANGE("stackexchange"),
    UNKNOWN("unknown");

    private String name;

    private Table(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Table fromString(String table) {
        return switch (table) {
            case "wikipedia" -> Table.WIKIPEDIA;
            case "stackexchange" -> Table.STACKEXCHANGE;
            default -> Table.UNKNOWN;
        };
    }
}
