package com.allinone.skills.magic.data;

/**
 * Defines combat spells and their rune requirements for magic training.
 * Staffs negate the need for their element's runes (handled by the loadout).
 */
public enum MagicSpell {
    // Strike spells
    WIND_STRIKE(1, "Wind Strike", 5.5, "Mind rune", 1, "Air rune", 1, null, 0),
    WATER_STRIKE(5, "Water Strike", 7.5, "Mind rune", 1, "Water rune", 1, "Air rune", 1),
    EARTH_STRIKE(9, "Earth Strike", 9.5, "Mind rune", 1, "Earth rune", 2, "Air rune", 1),
    FIRE_STRIKE(13, "Fire Strike", 11.5, "Mind rune", 1, "Fire rune", 3, "Air rune", 2),

    // Bolt spells
    WIND_BOLT(17, "Wind Bolt", 13.5, "Chaos rune", 1, "Air rune", 2, null, 0),
    WATER_BOLT(23, "Water Bolt", 16.5, "Chaos rune", 1, "Water rune", 2, "Air rune", 2),
    EARTH_BOLT(29, "Earth Bolt", 19.5, "Chaos rune", 1, "Earth rune", 3, "Air rune", 2),
    FIRE_BOLT(35, "Fire Bolt", 22.5, "Chaos rune", 1, "Fire rune", 4, "Air rune", 3),

    // Blast spells
    WIND_BLAST(41, "Wind Blast", 25.5, "Death rune", 1, "Air rune", 3, null, 0),
    WATER_BLAST(47, "Water Blast", 28.5, "Death rune", 1, "Water rune", 3, "Air rune", 3),
    EARTH_BLAST(53, "Earth Blast", 31.5, "Death rune", 1, "Earth rune", 4, "Air rune", 3),
    FIRE_BLAST(59, "Fire Blast", 34.5, "Death rune", 1, "Fire rune", 5, "Air rune", 4),

    // High alchemy (non-combat, special handling)
    HIGH_LEVEL_ALCHEMY(55, "High Level Alchemy", 65.0, "Nature rune", 1, "Fire rune", 5, null, 0);

    private final int levelRequired;
    private final String spellName;
    private final double baseXp;
    private final String rune1;
    private final int rune1Amount;
    private final String rune2;
    private final int rune2Amount;
    private final String rune3;
    private final int rune3Amount;

    MagicSpell(int levelRequired, String spellName, double baseXp,
               String rune1, int rune1Amount, String rune2, int rune2Amount,
               String rune3, int rune3Amount) {
        this.levelRequired = levelRequired;
        this.spellName = spellName;
        this.baseXp = baseXp;
        this.rune1 = rune1;
        this.rune1Amount = rune1Amount;
        this.rune2 = rune2;
        this.rune2Amount = rune2Amount;
        this.rune3 = rune3;
        this.rune3Amount = rune3Amount;
    }

    public int getLevelRequired() { return levelRequired; }
    public String getSpellName() { return spellName; }
    public double getBaseXp() { return baseXp; }
    public String getRune1() { return rune1; }
    public int getRune1Amount() { return rune1Amount; }
    public String getRune2() { return rune2; }
    public int getRune2Amount() { return rune2Amount; }
    public String getRune3() { return rune3; }
    public int getRune3Amount() { return rune3Amount; }

    public boolean isCombatSpell() {
        return this != HIGH_LEVEL_ALCHEMY;
    }
}
