package com.allinone.skills.fishing;

import com.allinone.framework.AbstractSkillSet;
import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.nodes.BankItemsNode;
import com.allinone.framework.nodes.DismissDialogNode;
import com.allinone.framework.nodes.DropItemsNode;
import com.allinone.framework.nodes.EnsureLoadoutNode;
import com.allinone.framework.loadout.FishingLoadout;
import com.allinone.skills.fishing.data.FishingMethod;
import com.allinone.skills.fishing.data.FishingSpot;
import com.allinone.skills.fishing.nodes.*;
import com.allinone.ui.painters.FishingPainter;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.methods.skills.Skills;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.wrappers.items.Item;

import java.util.function.Predicate;

public class FishingSkill extends AbstractSkillSet {

    @Override
    public int getCurrentLevel() {
        return Skills.getRealLevel(Skill.FISHING);
    }

    @Override
    public String getName() {
        return "Fishing";
    }

    @Override
    public void onStart(Blackboard blackboard) {
         startTime = System.currentTimeMillis();
         Logger.log("Initializing Fishing Behavior Tree...");

         painter = new FishingPainter(blackboard, startTime);

         Node updateStrategy = new UpdateFishingStrategyNode(blackboard);

         Node ensureGear = new EnsureLoadoutNode(() -> {
             FishingSpot spot = blackboard.getCurrentFishingSpot();
             if (spot == null) return new FishingLoadout("Small fishing net");
             String tool = spot.getMethod().getToolName();
             String bait = null;
             int baitAmount = 0;
             if (spot.getMethod() == FishingMethod.BAIT) {
                 bait = "Fishing bait";
                 baitAmount = 500;
             } else if (spot.getMethod() == FishingMethod.FLY) {
                 bait = "Feather";
                 baitAmount = 500;
             }
             return new FishingLoadout(tool, bait, baitAmount);
         });

         Predicate<Item> fishingToolFilter = i -> {
             String name = i.getName().toLowerCase();
             return name.contains("net") || name.contains("rod") || name.contains("feather")
                 || name.contains("bait") || name.contains("harpoon") || name.contains("pot");
         };

         Node bankFish = new BankItemsNode(
             blackboard,
             () -> {
                 FishingSpot spot = blackboard.getCurrentFishingSpot();
                 return spot == null || spot.shouldBank();
             },
             fishingToolFilter,
             "Banking Fish"
         );

         Node dropFish = new DropItemsNode(fishingToolFilter);

         Node travel = new TravelToFishNode(blackboard);

         Node fishSequence = new Sequence(
             travel,
             new FindFishNode(blackboard),
             new FishNode(blackboard)
         );

         rootNode = new Selector(
             new DismissDialogNode(), // Priority: Handle level-up and other dialogs
             updateStrategy,
             ensureGear,   // Priority: Ensure gear
             bankFish,
             dropFish,
             fishSequence
         );

         Logger.log("Fishing Tree Initialized.");
    }
}
