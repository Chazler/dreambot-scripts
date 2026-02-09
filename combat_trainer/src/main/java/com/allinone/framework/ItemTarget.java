package com.allinone.framework;

public class ItemTarget {
    private final String name;
    private final int amount;
    private final boolean equip;
    private final boolean fill;

    public ItemTarget(String name, int amount, boolean equip) {
        this(name, amount, equip, false);
    }

    public ItemTarget(String name, int amount, boolean equip, boolean fill) {
        this.name = name;
        this.amount = amount;
        this.equip = equip;
        this.fill = fill;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public boolean shouldEquip() {
        return equip;
    }
    
    public boolean shouldFill() {
        return fill;
    }
}
