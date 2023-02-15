package xyz.deftu.lib.events;

public enum InputAction {
    PRESS,
    RELEASE;

    public static InputAction from(int action) {
        return action == 1 ? PRESS : RELEASE;
    }
}
