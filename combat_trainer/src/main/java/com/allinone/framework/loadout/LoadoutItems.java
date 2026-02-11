package com.allinone.framework.loadout;

import org.dreambot.api.methods.skills.Skill;

public class LoadoutItems {

    public static class Ranged {
        public static final LoadoutItem MAPLE_SHORTBOW = new LoadoutItem("Maple shortbow").req(Skill.RANGED, 30);
        public static final LoadoutItem WILLOW_SHORTBOW = new LoadoutItem("Willow shortbow").req(Skill.RANGED, 20);
        public static final LoadoutItem OAK_SHORTBOW = new LoadoutItem("Oak shortbow").req(Skill.RANGED, 5);
        public static final LoadoutItem SHORTBOW = new LoadoutItem("Shortbow");

        public static final LoadoutItem ADAMANT_ARROW = new LoadoutItem("Adamant arrow");
        public static final LoadoutItem MITHRIL_ARROW = new LoadoutItem("Mithril arrow");
        public static final LoadoutItem IRON_ARROW = new LoadoutItem("Iron arrow");
        public static final LoadoutItem BRONZE_ARROW = new LoadoutItem("Bronze arrow");
    }

    public static class Woodcutting {
        public static final LoadoutItem DRAGON_AXE = new LoadoutItem("Dragon axe").req(Skill.WOODCUTTING, 61).req(Skill.ATTACK, 60);
        public static final LoadoutItem RUNE_AXE = new LoadoutItem("Rune axe").req(Skill.WOODCUTTING, 41).req(Skill.ATTACK, 40);
        public static final LoadoutItem ADAMANT_AXE = new LoadoutItem("Adamant axe").req(Skill.WOODCUTTING, 31).req(Skill.ATTACK, 30);
        public static final LoadoutItem MITHRIL_AXE = new LoadoutItem("Mithril axe").req(Skill.WOODCUTTING, 21).req(Skill.ATTACK, 20);
        public static final LoadoutItem STEEL_AXE = new LoadoutItem("Steel axe").req(Skill.WOODCUTTING, 6).req(Skill.ATTACK, 5);
        public static final LoadoutItem IRON_AXE = new LoadoutItem("Iron axe").req(Skill.WOODCUTTING, 1).req(Skill.ATTACK, 1);
        public static final LoadoutItem BRONZE_AXE = new LoadoutItem("Bronze axe").req(Skill.WOODCUTTING, 1).req(Skill.ATTACK, 1);
    }

    public static class Mining {
        public static final LoadoutItem DRAGON_PICKAXE = new LoadoutItem("Dragon pickaxe").req(Skill.MINING, 61).req(Skill.ATTACK, 60);
        public static final LoadoutItem RUNE_PICKAXE = new LoadoutItem("Rune pickaxe").req(Skill.MINING, 41).req(Skill.ATTACK, 40);
        public static final LoadoutItem ADAMANT_PICKAXE = new LoadoutItem("Adamant pickaxe").req(Skill.MINING, 31).req(Skill.ATTACK, 30);
        public static final LoadoutItem MITHRIL_PICKAXE = new LoadoutItem("Mithril pickaxe").req(Skill.MINING, 21).req(Skill.ATTACK, 20);
        public static final LoadoutItem STEEL_PICKAXE = new LoadoutItem("Steel pickaxe").req(Skill.MINING, 6).req(Skill.ATTACK, 5);
        public static final LoadoutItem IRON_PICKAXE = new LoadoutItem("Iron pickaxe").req(Skill.MINING, 1).req(Skill.ATTACK, 1);
        public static final LoadoutItem BRONZE_PICKAXE = new LoadoutItem("Bronze pickaxe").req(Skill.MINING, 1).req(Skill.ATTACK, 1);
    }

    public static class Fishing {
        public static final LoadoutItem SMALL_FISHING_NET = new LoadoutItem("Small fishing net");
        public static final LoadoutItem LOBSTER_POT = new LoadoutItem("Lobster pot");
        public static final LoadoutItem HARPOON = new LoadoutItem("Harpoon");
        public static final LoadoutItem BIG_FISHING_NET = new LoadoutItem("Big fishing net");
        public static final LoadoutItem FLY_FISHING_ROD = new LoadoutItem("Fly fishing rod");
        public static final LoadoutItem FISHING_ROD = new LoadoutItem("Fishing rod");
        public static final LoadoutItem FEATHERS = new LoadoutItem("Feather");
        public static final LoadoutItem BAIT = new LoadoutItem("Fishing bait");
    }

    public static class Firemaking {
        public static final LoadoutItem TINDERBOX = new LoadoutItem("Tinderbox");
    }
}
