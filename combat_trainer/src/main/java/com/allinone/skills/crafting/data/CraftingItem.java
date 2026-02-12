package com.allinone.skills.crafting.data;

/**
 * Craftable items with their materials and requirements.
 */
public enum CraftingItem {
    // LEATHER CRAFTING (Needle + Thread + Leather)
    LEATHER_GLOVES(1, "Leather gloves", CraftingType.LEATHER, "Leather", 1, null, 0, 13.8),
    LEATHER_BOOTS(7, "Leather boots", CraftingType.LEATHER, "Leather", 1, null, 0, 16.25),
    LEATHER_COWL(9, "Leather cowl", CraftingType.LEATHER, "Leather", 1, null, 0, 18.5),
    LEATHER_VAMBRACES(11, "Leather vambraces", CraftingType.LEATHER, "Leather", 1, null, 0, 22.0),
    LEATHER_BODY(14, "Leather body", CraftingType.LEATHER, "Leather", 1, null, 0, 25.0),
    LEATHER_CHAPS(18, "Leather chaps", CraftingType.LEATHER, "Leather", 1, null, 0, 27.0),
    HARD_LEATHER_BODY(28, "Hardleather body", CraftingType.LEATHER, "Hard leather", 1, null, 0, 35.0),
    STUDDED_BODY(38, "Studded body", CraftingType.LEATHER, "Leather body", 1, "Steel studs", 1, 40.0),
    STUDDED_CHAPS(44, "Studded chaps", CraftingType.LEATHER, "Leather chaps", 1, "Steel studs", 1, 42.0),
    GREEN_DHIDE_VAMBRACES(57, "Green d'hide vambraces", CraftingType.LEATHER, "Green dragon leather", 1, null, 0, 62.0),
    GREEN_DHIDE_CHAPS(60, "Green d'hide chaps", CraftingType.LEATHER, "Green dragon leather", 2, null, 0, 124.0),
    GREEN_DHIDE_BODY(63, "Green d'hide body", CraftingType.LEATHER, "Green dragon leather", 3, null, 0, 186.0),

    // GEM CUTTING (Chisel + Uncut Gem)
    CUT_OPAL(1, "Opal", CraftingType.GEM, "Uncut opal", 1, null, 0, 15.0),
    CUT_JADE(13, "Jade", CraftingType.GEM, "Uncut jade", 1, null, 0, 20.0),
    CUT_RED_TOPAZ(16, "Red topaz", CraftingType.GEM, "Uncut red topaz", 1, null, 0, 25.0),
    CUT_SAPPHIRE(20, "Sapphire", CraftingType.GEM, "Uncut sapphire", 1, null, 0, 50.0),
    CUT_EMERALD(27, "Emerald", CraftingType.GEM, "Uncut emerald", 1, null, 0, 67.5),
    CUT_RUBY(43, "Ruby", CraftingType.GEM, "Uncut ruby", 1, null, 0, 85.0),
    CUT_DIAMOND(63, "Diamond", CraftingType.GEM, "Uncut diamond", 1, null, 0, 107.5),
    CUT_DRAGONSTONE(55, "Dragonstone", CraftingType.GEM, "Uncut dragonstone", 1, null, 0, 137.5),

    // JEWELRY (Gold bar + Ring mould at furnace)
    GOLD_RING(5, "Gold ring", CraftingType.JEWELRY, "Gold bar", 1, null, 0, 15.0),
    GOLD_NECKLACE(6, "Gold necklace", CraftingType.JEWELRY, "Gold bar", 1, null, 0, 20.0),
    GOLD_BRACELET(7, "Gold bracelet", CraftingType.JEWELRY, "Gold bar", 1, null, 0, 25.0),
    SAPPHIRE_RING(20, "Sapphire ring", CraftingType.JEWELRY, "Gold bar", 1, "Sapphire", 1, 40.0),
    SAPPHIRE_NECKLACE(22, "Sapphire necklace", CraftingType.JEWELRY, "Gold bar", 1, "Sapphire", 1, 55.0),
    EMERALD_RING(27, "Emerald ring", CraftingType.JEWELRY, "Gold bar", 1, "Emerald", 1, 55.0),
    EMERALD_NECKLACE(29, "Emerald necklace", CraftingType.JEWELRY, "Gold bar", 1, "Emerald", 1, 60.0),
    RUBY_RING(43, "Ruby ring", CraftingType.JEWELRY, "Gold bar", 1, "Ruby", 1, 70.0),
    RUBY_NECKLACE(40, "Ruby necklace", CraftingType.JEWELRY, "Gold bar", 1, "Ruby", 1, 75.0),
    DIAMOND_RING(43, "Diamond ring", CraftingType.JEWELRY, "Gold bar", 1, "Diamond", 1, 85.0),
    DIAMOND_NECKLACE(56, "Diamond necklace", CraftingType.JEWELRY, "Gold bar", 1, "Diamond", 1, 90.0);

    private final int levelRequired;
    private final String produceName;
    private final CraftingType type;
    private final String primaryItem;
    private final int primaryCount;
    private final String secondaryItem;
    private final int secondaryCount;
    private final double xp;

    CraftingItem(int levelRequired, String produceName, CraftingType type,
                 String primaryItem, int primaryCount, String secondaryItem, int secondaryCount, double xp) {
        this.levelRequired = levelRequired;
        this.produceName = produceName;
        this.type = type;
        this.primaryItem = primaryItem;
        this.primaryCount = primaryCount;
        this.secondaryItem = secondaryItem;
        this.secondaryCount = secondaryCount;
        this.xp = xp;
    }

    public int getLevelRequired() { return levelRequired; }
    public String getProduceName() { return produceName; }
    public CraftingType getType() { return type; }
    public String getPrimaryItem() { return primaryItem; }
    public int getPrimaryCount() { return primaryCount; }
    public String getSecondaryItem() { return secondaryItem; }
    public int getSecondaryCount() { return secondaryCount; }
    public double getXp() { return xp; }

    public boolean needsSecondary() {
        return secondaryItem != null && secondaryCount > 0;
    }
}
