package com.allinone.framework.loadout;

import com.allinone.framework.ItemTarget;
import java.util.*;

import java.util.stream.Collectors;

public abstract class Loadout {

    private final Map<Slot, List<LoadoutItem>> slots = new HashMap<>();
    private final Map<Slot, String> overrides = new HashMap<>();
    private final List<ItemTarget> inventoryItems = new ArrayList<>();

    // Ordered candidates for an inventory item (e.g. food: best to worst)
    // The first available one in bank will be used.
    private List<String> inventoryCandidates = new ArrayList<>();
    private int inventoryCandidateAmount = 0;
    private boolean inventoryCandidateFill = false;

    /**
     * Define a list of candidate items for a slot, ordered from BEST to WORST.
     * The manager will pick the first one you own and can use.
     */
    protected void setSlot(Slot slot, LoadoutItem... candidates) {
        slots.put(slot, Arrays.asList(candidates));
    }
    
    /**
     * Removes a slot requirement completely.
     */
    public void removeSlot(Slot slot) {
        slots.remove(slot);
        overrides.remove(slot);
    }

    /**
     * Add a specific item that must be in the inventory.
     */
    public void addInventoryItem(String name, int amount) {
        inventoryItems.add(new ItemTarget(name, amount, false));
    }

    public void addInventoryItem(String name) {
        addInventoryItem(name, 1);
    }

    /**
     * Compatibility overload for simple string lists (no requirements).
     */
    protected void setSlot(Slot slot, String... candidates) {
        List<LoadoutItem> items = Arrays.stream(candidates)
                .map(LoadoutItem::new)
                .collect(Collectors.toList());
        slots.put(slot, items);
    }

    /**
     * Force a specific item for this slot, ignoring any automated logic.
     */
    public void addOverride(Slot slot, String itemName) {
        overrides.put(slot, itemName);
    }
    
    public String getName() {
        return this.getClass().getSimpleName();
    }

    // --- Internal Accessors ---

    public boolean hasOverride(Slot slot) {
        return overrides.containsKey(slot);
    }

    public String getOverride(Slot slot) {
        return overrides.get(slot);
    }

    public List<LoadoutItem> getCandidates(Slot slot) {
        return slots.getOrDefault(slot, Collections.emptyList());
    }

    public List<ItemTarget> getInventoryItems() {
        return inventoryItems;
    }

    public Set<Slot> getDefinedSlots() {
        Set<Slot> defined = new HashSet<>(slots.keySet());
        defined.addAll(overrides.keySet());
        return defined;
    }

    /**
     * Set ordered candidates for an inventory item (e.g. food).
     * LoadoutManager will pick the first available one from bank/inventory.
     * @param amount How many to withdraw
     * @param fill   If true, fill remaining inventory space
     * @param candidates Item names ordered best to worst
     */
    public void setInventoryCandidates(int amount, boolean fill, String... candidates) {
        this.inventoryCandidates = Arrays.asList(candidates);
        this.inventoryCandidateAmount = amount;
        this.inventoryCandidateFill = fill;
    }

    public List<String> getInventoryCandidates() {
        return inventoryCandidates;
    }

    public int getInventoryCandidateAmount() {
        return inventoryCandidateAmount;
    }

    public boolean isInventoryCandidateFill() {
        return inventoryCandidateFill;
    }
}
