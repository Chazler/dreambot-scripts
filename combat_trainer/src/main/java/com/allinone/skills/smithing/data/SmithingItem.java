package com.allinone.skills.smithing.data;

public enum SmithingItem {
    // SMELTING
    BRONZE_BAR(SmithingType.SMELTING, "Bronze bar", 1, "Copper ore", 1, "Tin ore", 1),
    IRON_BAR(SmithingType.SMELTING, "Iron bar", 15, "Iron ore", 1, null, 0),
    SILVER_BAR(SmithingType.SMELTING, "Silver bar", 20, "Silver ore", 1, null, 0),
    STEEL_BAR(SmithingType.SMELTING, "Steel bar", 30, "Iron ore", 1, "Coal", 2),
    GOLD_BAR(SmithingType.SMELTING, "Gold bar", 40, "Gold ore", 1, null, 0),
    MITHRIL_BAR(SmithingType.SMELTING, "Mithril bar", 50, "Mithril ore", 1, "Coal", 4),
    ADAMANT_BAR(SmithingType.SMELTING, "Adamantite bar", 70, "Adamantite ore", 1, "Coal", 6),
    RUNITE_BAR(SmithingType.SMELTING, "Runite bar", 85, "Runite ore", 1, "Coal", 8),

    // FORGING - BRONZE
    BRONZE_KNIFE(SmithingType.FORGING, "Bronze knife", 7, "Bronze bar", 1, null, 0),
    BRONZE_KITESHIELD(SmithingType.FORGING, "Bronze kiteshield", 12, "Bronze bar", 3, null, 0),
    BRONZE_PLATELEGS(SmithingType.FORGING, "Bronze platelegs", 16, "Bronze bar", 3, null, 0),
    BRONZE_PLATEBODY(SmithingType.FORGING, "Bronze platebody", 18, "Bronze bar", 5, null, 0),

    // FORGING - IRON
    IRON_KNIFE(SmithingType.FORGING, "Iron knife", 22, "Iron bar", 1, null, 0),
    IRON_KITESHIELD(SmithingType.FORGING, "Iron kiteshield", 27, "Iron bar", 3, null, 0),
    IRON_PLATELEGS(SmithingType.FORGING, "Iron platelegs", 31, "Iron bar", 3, null, 0),
    IRON_PLATEBODY(SmithingType.FORGING, "Iron platebody", 33, "Iron bar", 5, null, 0),

    // FORGING - STEEL
    STEEL_KNIFE(SmithingType.FORGING, "Steel knife", 37, "Steel bar", 1, null, 0),
    STEEL_KITESHIELD(SmithingType.FORGING, "Steel kiteshield", 42, "Steel bar", 3, null, 0),
    STEEL_PLATELEGS(SmithingType.FORGING, "Steel platelegs", 46, "Steel bar", 3, null, 0),
    STEEL_PLATEBODY(SmithingType.FORGING, "Steel platebody", 48, "Steel bar", 5, null, 0),

    // FORGING - MITHRIL
    MITHRIL_KNIFE(SmithingType.FORGING, "Mithril knife", 57, "Mithril bar", 1, null, 0),
    MITHRIL_KITESHIELD(SmithingType.FORGING, "Mithril kiteshield", 62, "Mithril bar", 3, null, 0),
    MITHRIL_PLATELEGS(SmithingType.FORGING, "Mithril platelegs", 66, "Mithril bar", 3, null, 0),
    MITHRIL_PLATEBODY(SmithingType.FORGING, "Mithril platebody", 68, "Mithril bar", 5, null, 0),

    // FORGING - ADAMANT
    ADAMANT_KNIFE(SmithingType.FORGING, "Adamant knife", 77, "Adamantite bar", 1, null, 0),
    ADAMANT_KITESHIELD(SmithingType.FORGING, "Adamant kiteshield", 82, "Adamantite bar", 3, null, 0),
    ADAMANT_PLATELEGS(SmithingType.FORGING, "Adamant platelegs", 86, "Adamantite bar", 3, null, 0),
    ADAMANT_PLATEBODY(SmithingType.FORGING, "Adamant platebody", 88, "Adamantite bar", 5, null, 0),

    // FORGING - RUNE
    RUNE_KNIFE(SmithingType.FORGING, "Rune knife", 92, "Runite bar", 1, null, 0),
    RUNE_KITESHIELD(SmithingType.FORGING, "Rune kiteshield", 97, "Runite bar", 3, null, 0),
    RUNE_PLATELEGS(SmithingType.FORGING, "Rune platelegs", 99, "Runite bar", 3, null, 0),
    RUNE_PLATEBODY(SmithingType.FORGING, "Rune platebody", 99, "Runite bar", 5, null, 0);

    private final SmithingType type;
    private final String produceName;
    private final int levelRequired;
    private final String primaryItem;
    private final int primaryCount;
    private final String secondaryItem;
    private final int secondaryCount;

    SmithingItem(SmithingType type, String produceName, int levelRequired, String primaryItem, int primaryCount, String secondaryItem, int secondaryCount) {
        this.type = type;
        this.produceName = produceName;
        this.levelRequired = levelRequired;
        this.primaryItem = primaryItem;
        this.primaryCount = primaryCount;
        this.secondaryItem = secondaryItem;
        this.secondaryCount = secondaryCount;
    }

    public SmithingType getType() { return type; }
    public String getProduceName() { return produceName; }
    public int getLevelRequired() { return levelRequired; }
    public String getPrimaryItem() { return primaryItem; }
    public int getPrimaryCount() { return primaryCount; }
    public String getSecondaryItem() { return secondaryItem; }
    public int getSecondaryCount() { return secondaryCount; }
    
    public boolean needsSecondary() {
        return secondaryItem != null && secondaryCount > 0;
    }
}
