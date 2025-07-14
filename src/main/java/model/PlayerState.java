package model;

public enum PlayerState {
    NORMAL("n"), LIGHT_DAMAGED("l"), HEAVY_DAMAGED("h"), DESTROYED("d"), UNKNOWN("u");

    private final String state;

    PlayerState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
