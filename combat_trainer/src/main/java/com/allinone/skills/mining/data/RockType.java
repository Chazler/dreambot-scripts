package com.allinone.skills.mining.data;

public enum RockType {
    CLAY("Clay rocks", 1, "Clay", 5.0),
    COPPER("Copper rocks", 1, "Copper ore", 17.5),
    TIN("Tin rocks", 1, "Tin ore", 17.5),
    IRON("Iron rocks", 15, "Iron ore", 35.0),
    SILVER("Silver rocks", 20, "Silver ore", 40.0),
    COAL("Coal rocks", 30, "Coal", 50.0),
    GOLD("Gold rocks", 40, "Gold ore", 65.0),
    MITHRIL("Mithril rocks", 55, "Mithril ore", 80.0),
    ADAMANTITE("Adamantite rocks", 70, "Adamantite ore", 95.0),
    RUNITE("Runite rocks", 85, "Runite ore", 125.0);

    private final String objectName;
    private final int levelRequired;
    private final String oreName;
    private final double xp;

    RockType(String objectName, int levelRequired, String oreName, double xp) {
        this.objectName = objectName;
        this.levelRequired = levelRequired;
        this.oreName = oreName;
        this.xp = xp;
    }

    public String getObjectName() { return objectName; }
    public int getLevelRequired() { return levelRequired; }
    public String getOreName() { return oreName; }
    public double getXp() { return xp; }
}
