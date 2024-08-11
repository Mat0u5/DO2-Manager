package net.mat0u5.do2manager.world;

public class CommandBlockData {
    private int x, y, z;
    private String type;
    private boolean conditional, auto;
    private String command;

    public CommandBlockData(int x, int y, int z, String type, boolean conditional, boolean auto, String command) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.conditional = conditional;
        this.auto = auto;
        this.command = command;
    }
    public boolean isSameAs(CommandBlockData compareTo) {
        return type.equalsIgnoreCase(compareTo.type) && conditional == compareTo.conditional && auto == compareTo.auto && command.equalsIgnoreCase(compareTo.command);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getType() {
        return type;
    }

    public boolean isConditional() {
        return conditional;
    }

    public boolean isAuto() {
        return auto;
    }

    public String getCommand() {
        return command;
    }
}
