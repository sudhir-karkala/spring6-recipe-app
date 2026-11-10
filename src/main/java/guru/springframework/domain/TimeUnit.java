package guru.springframework.domain;

public enum TimeUnit {
    MINUTES("Minutes"),
    HOURS("Hours");

    private final String displayName;

    TimeUnit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
