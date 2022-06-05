package discord_bot.utils.enums;

public enum Table {
    WIKIPEDIA("wikipedia"),
    STACKEXCHANGE("stackexchange"),
    STACKOVERLOW("stackoverflow"),
    ASKUBUNTU("askubuntu"),
    SERVERFAULT("serverfault"),
    SUPERUSER("superuser"),
    MATH("math"),
    ASK_DIFFERENT("apple"),
    THEORETICAL_CS("cstheory"),
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
            case "wikipedia" -> WIKIPEDIA;
            case "stackexchange" -> STACKEXCHANGE;
            case "stackoverflow" -> STACKOVERLOW;
            case "askubuntu" -> ASKUBUNTU;
            case "serverfault" -> SERVERFAULT;
            case "superuser" -> SUPERUSER;
            case "math" -> MATH;
            case "apple" -> ASK_DIFFERENT;
            case "cstheory" -> THEORETICAL_CS;
            default -> UNKNOWN;
        };
    }
}
