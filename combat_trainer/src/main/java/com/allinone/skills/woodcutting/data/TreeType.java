package com.allinone.skills.woodcutting.data;

public enum TreeType {
    NORMAL("Tree", 1, "Logs", 25.0),
    OAK("Oak", 15, "Oak logs", 37.5),
    WILLOW("Willow", 30, "Willow logs", 67.5),
    MAPLE("Maple", 45, "Maple logs", 100.0),
    YEW("Yew", 60, "Yew logs", 175.0),
    MAGIC("Magic tree", 75, "Magic logs", 250.0);

    private final String treeName;
    private final int levelRequired;
    private final String logName;
    private final double xp;

    TreeType(String treeName, int levelRequired, String logName, double xp) {
        this.treeName = treeName;
        this.levelRequired = levelRequired;
        this.logName = logName;
        this.xp = xp;
    }
    
    public double getXp() { return xp; }

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
