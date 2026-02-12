package com.allinone.skills.cooking.data;

public enum CookingItem {
    SHRIMPS(1, "Raw shrimps", "Shrimps", 30.0),
    SARDINE(1, "Raw sardine", "Sardine", 40.0),
    HERRING(5, "Raw herring", "Herring", 50.0),
    MACKEREL(10, "Raw mackerel", "Mackerel", 60.0),
    TROUT(15, "Raw trout", "Trout", 70.0),
    PIKE(20, "Raw pike", "Pike", 80.0),
    SALMON(25, "Raw salmon", "Salmon", 90.0),
    TUNA(30, "Raw tuna", "Tuna", 100.0),
    LOBSTER(40, "Raw lobster", "Lobster", 120.0),
    SWORDFISH(45, "Raw swordfish", "Swordfish", 140.0),
    MONKFISH(62, "Raw monkfish", "Monkfish", 150.0),
    SHARK(80, "Raw shark", "Shark", 210.0);

    private final int levelRequired;
    private final String rawName;
    private final String cookedName;
    private final double xp;

    CookingItem(int levelRequired, String rawName, String cookedName, double xp) {
        this.levelRequired = levelRequired;
        this.rawName = rawName;
        this.cookedName = cookedName;
        this.xp = xp;
    }

    public int getLevelRequired() { return levelRequired; }
    public String getRawName() { return rawName; }
    public String getCookedName() { return cookedName; }
    public double getXp() { return xp; }
}
