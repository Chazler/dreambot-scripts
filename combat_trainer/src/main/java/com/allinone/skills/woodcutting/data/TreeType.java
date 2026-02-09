package com.allinone.skills.woodcutting.data;

public enum TreeType {
    NORMAL("Tree", 1, "Logs"),
    OAK("Oak", 15, "Oak logs"),
    WILLOW("Willow", 30, "Willow logs"),
    MAPLE("Maple", 45, "Maple logs"),
    YEW("Yew", 60, "Yew logs"),
    MAGIC("Magic tree", 75, "Magic logs");

    private final String treeName;
    private final int levelRequired;
    private final String logName;

    TreeType(String treeName, int levelRequired, String logName) {
        this.treeName = treeName;
        this.levelRequired = levelRequired;
        this.logName = logName;
    }

    public String getTreeName() {
        return treeName;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public String getLogName() {
        return logName;
    }
}
