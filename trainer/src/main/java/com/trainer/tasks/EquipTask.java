package com.trainer.tasks;

import com.trainer.framework.Task;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.equipment.Equipment;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.items.Item;

import java.util.HashMap;
import java.util.Map;

public class EquipTask implements Task {
    private final AbstractScript script;

    // Define tiers for metals
    private static final Map<String, Integer> METAL_TIERS = new HashMap<>();
    private static final Map<String, Integer> LEVEL_REQS = new HashMap<>();

    static {
        METAL_TIERS.put("bronze", 1);
        METAL_TIERS.put("iron", 2);
        METAL_TIERS.put("steel", 3);
        METAL_TIERS.put("black", 4);
        METAL_TIERS.put("mithril", 5);
        METAL_TIERS.put("adamant", 6);
        METAL_TIERS.put("rune", 7);
        METAL_TIERS.put("dragon", 8);

        LEVEL_REQS.put("bronze", 1);
        LEVEL_REQS.put("iron", 1);
        LEVEL_REQS.put("steel", 5);
        LEVEL_REQS.put("black", 10);
        LEVEL_REQS.put("mithril", 20);
        LEVEL_REQS.put("adamant", 30);
        LEVEL_REQS.put("rune", 40);
        LEVEL_REQS.put("dragon", 60);
    }

    public EquipTask(AbstractScript script) {
        this.script = script;
    }

    @Override
    public boolean accept() {
        return getBestEquippableItem() != null;
    }

    @Override
    public int execute() {
        Item bestItem = getBestEquippableItem();
        if (bestItem != null) {
            script.log("Equipping better item: " + bestItem.getName());
            if (bestItem.interact("Wield") || bestItem.interact("Wear") || bestItem.interact("Equip")) {
                script.sleepUntil(() -> !bestItem.isValid(), 3000, 100);
                return Calculations.random(1000, 2000);
            }
        }
        return 1000;
    }

    @Override
    public String status() {
        return "Equipping Gear";
    }

    private Item getBestEquippableItem() {
        for (Item item : Inventory.all()) {
            if (item == null || item.getName() == null) continue;

            String name = item.getName().toLowerCase();
            String metal = getMetal(name);
            if (metal == null) continue; // Not a standard metal item

            // Check requirements
            int req = LEVEL_REQS.getOrDefault(metal, 1);
            int myAtt = Skills.getRealLevel(Skill.ATTACK);
            int myDef = Skills.getRealLevel(Skill.DEFENCE);
            
            // Weapons check Attack, Armor checks Defence
            // Heuristic assuming standard naming
            boolean isWeapon = name.contains("sword") || name.contains("scimitar") || name.contains("dagger") || name.contains("axe") || name.contains("mace");
            boolean isArmor = name.contains("plate") || name.contains("helm") || name.contains("shield") || name.contains("legs") || name.contains("skirt") || name.contains("chainbody");

            if (isWeapon && myAtt < req) continue;
            if (isArmor && myDef < req) continue;

            // Check against current equipment
            if (isBetterThanEquipped(item, metal, isWeapon)) {
                return item;
            }
        }
        return null;
    }

    private boolean isBetterThanEquipped(Item invItem, String invMetal, boolean isWeapon) {
        int invTier = METAL_TIERS.get(invMetal);
        
        // Find matching equipment slot item
        // This is tricky without knowing exact slot. 
        // We can just check if we satisfy Equipment.contains() for a "better" item? No.
        // We need to compare specific types (e.g. Bronze Scimitar vs Iron Scimitar).
        
        // Simplified Logic: 
        // If we have a "Iron Scimitar" in inventory, check if we are wearing any "Scimitar".
        // If yes, compare tiers. 
        // If no, we should probably equip it (upgrade from nothing).
        
        String invName = invItem.getName().toLowerCase();
        String itemType = getItemType(invName); // e.g., "scimitar", "platebody"
        
        if (itemType == null) return false;

        // Check all equipped items for matching type
        for (Item equip : Equipment.all()) {
            if (equip != null && equip.getName().toLowerCase().contains(itemType)) {
                String equipMetal = getMetal(equip.getName().toLowerCase());
                if (equipMetal == null) return true; // Wearing non-standard? Assume inventory is better (risky but okay for simple trainer)
                
                int equipTier = METAL_TIERS.getOrDefault(equipMetal, 0);
                return invTier > equipTier;
            }
        }
        
        // If we found NO matching item type in equipment, we simply assume it's an upgrade (Empty slot)
        // EXCEPT: Verify we aren't creating conflicts (e.g. 2h sword vs shield). 
        // For simple trainer, assumtion: If I have a Scimitar and holding nothing/Shield, equip it.
        // If I have Platelegs and wearing nothing, equip it.
        return true;
    }

    private String getMetal(String name) {
        for (String metal : METAL_TIERS.keySet()) {
            if (name.startsWith(metal + " ")) return metal;
        }
        return null;
    }
    
    private String getItemType(String name) {
        String[] types = {"scimitar", "longsword", "sword", "dagger", "battleaxe", "warhammer", "mace", 
                          "platebody", "platelegs", "plateskirt", "chainbody", "full helm", "med helm", "kiteshield", "sq shield"};
        for (String t : types) {
            if (name.contains(t)) return t;
        }
        return null;
    }
}
