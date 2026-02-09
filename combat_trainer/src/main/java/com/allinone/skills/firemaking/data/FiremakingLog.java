package com.allinone.skills.firemaking.data;

public enum FiremakingLog {
    NORMAL("Logs", 1),
    OAK("Oak logs", 15),
    WILLOW("Willow logs", 30),
    MAPLE("Maple logs", 45),
    YEW("Yew logs", 60),
    MAGIC("Magic logs", 75);

    private final String itemName;
    private final int levelRequired;

    FiremakingLog(String itemName, int levelRequired) {
        this.itemName = itemName;
        this.levelRequired = levelRequired;
    }

    public String getItemName() {
        return itemName;
    }

    public int getLevelRequired() {
        return levelRequired;
    }
}
