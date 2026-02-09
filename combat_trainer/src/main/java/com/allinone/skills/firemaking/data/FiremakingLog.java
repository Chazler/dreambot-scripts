package com.allinone.skills.firemaking.data;

public enum FiremakingLog {
    NORMAL("Logs", 1, 40.0),
    OAK("Oak logs", 15, 60.0),
    WILLOW("Willow logs", 30, 90.0),
    MAPLE("Maple logs", 45, 135.0),
    YEW("Yew logs", 60, 202.5),
    MAGIC("Magic logs", 75, 303.8);

    private final String itemName;
    private final int levelRequired;
    private final double xp;

    FiremakingLog(String itemName, int levelRequired, double xp) {
        this.itemName = itemName;
        this.levelRequired = levelRequired;
        this.xp = xp;
    }
    
    public double getXp() { return xp; }

    public String getItemName() {
        return itemName;
    }

    public int getLevelRequired() {
        return levelRequired;
    }
}
