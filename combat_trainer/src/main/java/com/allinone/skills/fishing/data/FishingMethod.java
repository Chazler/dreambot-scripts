package com.allinone.skills.fishing.data;

public enum FishingMethod {
    NET("Small fishing net", "Net"),
    BAIT("Fishing rod", "Bait"), // Requires bait item
    FLY("Fly fishing rod", "Lure"), // Requires feathers
    HARPOON("Harpoon", "Harpoon"),
    CAGE("Lobster pot", "Cage");

    private final String toolName;
    private final String action;

    FishingMethod(String toolName, String action) {
        this.toolName = toolName;
        this.action = action;
    }

    public String getToolName() {
        return toolName;
    }

    public String getAction() {
        return action;
    }
}
