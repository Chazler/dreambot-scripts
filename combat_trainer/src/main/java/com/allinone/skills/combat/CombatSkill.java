package com.allinone.skills.combat;

import com.allinone.framework.AbstractSkillSet;
import com.allinone.framework.Blackboard;
import com.allinone.framework.Node;
import com.allinone.framework.Selector;
import com.allinone.framework.Sequence;
import com.allinone.framework.nodes.DismissDialogNode;
import com.allinone.framework.nodes.EnsureLoadoutNode;
import com.allinone.framework.loadout.CombatLoadout;
import com.allinone.skills.combat.nodes.*;
import com.allinone.ui.painters.CombatPainter;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.Client;

public class CombatSkill extends AbstractSkillSet {

    @Override
    public int getCurrentLevel() {
        return Client.isLoggedIn() ? Players.getLocal().getLevel() : 3;
    }

    @Override
    public String getName() {
        return "Combat";
    }

    @Override
    public void onStart(Blackboard blackboard) {
        startTime = System.currentTimeMillis();
        Logger.log("Initializing Combat Behavior Tree...");

        painter = new CombatPainter(blackboard, startTime);

        Node checkHealth = new CheckHealthNode();
        Node regear = new EnsureLoadoutNode(CombatLoadout::new, true);

        Node updateStrategy = new UpdateStrategyNode(blackboard);
        Node manageStyle = new ManageAttackStyleNode(blackboard);
        Node travel = new TravelNode(blackboard);
        Node loot = new LootNode(blackboard);
        Node bury = new BuryBonesNode(blackboard);
        Node acquireTarget = new AcquireTargetNode(blackboard);
        Node attack = new AttackNode(blackboard);

        Node combatSequence = new Sequence(
            updateStrategy,
            regear,
            manageStyle,
            loot,
            bury,
            travel,
            acquireTarget,
            attack
        );

        rootNode = new Selector(
            new DismissDialogNode(),
            checkHealth,
            combatSequence
        );

        Logger.log("Combat Tree initialized successfully.");
    }
}
