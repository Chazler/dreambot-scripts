package com.allinone.skills.magic.nodes;

import com.allinone.framework.Blackboard;
import com.allinone.framework.LeafNode;
import com.allinone.framework.Status;
import com.allinone.skills.magic.data.MagicSpell;
import com.allinone.skills.magic.strategies.MagicManager;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.NPC;

public class CastSpellNode extends LeafNode {

    private final Blackboard blackboard;
    private long lastCastTime = 0;

    public CastSpellNode(Blackboard blackboard) {
        this.blackboard = blackboard;
    }

    @Override
    public Status execute() {
        MagicSpell spell = MagicManager.getBestCombatSpell();
        if (spell == null) return Status.FAILURE;

        // Check rune supply
        if (!hasRunes(spell)) {
            log("Out of runes for " + spell.getSpellName());
            return Status.FAILURE;
        }

        // Already in combat - wait for spell cooldown
        if (Players.getLocal().isInCombat()) {
            lastCastTime = System.currentTimeMillis();
            return Status.RUNNING;
        }

        // Spell cooldown (game ticks)
        if (System.currentTimeMillis() - lastCastTime < 2400) {
            return Status.RUNNING;
        }

        NPC target = blackboard.getCurrentTarget();
        if (target == null || !target.exists() || target.getHealthPercent() <= 0) {
            blackboard.setCurrentTarget(null);
            return Status.FAILURE;
        }

        blackboard.setCurrentStatus("Casting " + spell.getSpellName());

        // Camera: rotate to face target if off-screen
        if (!target.isOnScreen()) {
            Camera.rotateToEntity(target);
            blackboard.getAntiBan().sleep(400, 150);
        }

        // Open magic tab if not open
        if (Tabs.getOpen() != Tab.MAGIC) {
            Tabs.open(Tab.MAGIC);
            blackboard.getAntiBan().sleep(300, 100);
        }

        // Map spell name to Normal enum
        Normal normalSpell = mapToNormalSpell(spell);
        if (normalSpell == null) return Status.FAILURE;

        // Cast the spell on the target
        if (Magic.castSpellOn(normalSpell, target)) {
            lastCastTime = System.currentTimeMillis();
            blackboard.getAntiBan().sleep(600, 200);
            Sleep.sleepUntil(() -> Players.getLocal().isInCombat() || Players.getLocal().isAnimating(), 3000);
            return Status.RUNNING;
        }

        return Status.FAILURE;
    }

    private boolean hasRunes(MagicSpell spell) {
        if (spell.getRune1() != null && Inventory.count(spell.getRune1()) < spell.getRune1Amount()) return false;
        if (spell.getRune2() != null && Inventory.count(spell.getRune2()) < spell.getRune2Amount()) return false;
        if (spell.getRune3() != null && Inventory.count(spell.getRune3()) < spell.getRune3Amount()) return false;
        return true;
    }

    private Normal mapToNormalSpell(MagicSpell spell) {
        switch (spell) {
            case WIND_STRIKE: return Normal.WIND_STRIKE;
            case WATER_STRIKE: return Normal.WATER_STRIKE;
            case EARTH_STRIKE: return Normal.EARTH_STRIKE;
            case FIRE_STRIKE: return Normal.FIRE_STRIKE;
            case WIND_BOLT: return Normal.WIND_BOLT;
            case WATER_BOLT: return Normal.WATER_BOLT;
            case EARTH_BOLT: return Normal.EARTH_BOLT;
            case FIRE_BOLT: return Normal.FIRE_BOLT;
            case WIND_BLAST: return Normal.WIND_BLAST;
            case WATER_BLAST: return Normal.WATER_BLAST;
            case EARTH_BLAST: return Normal.EARTH_BLAST;
            case FIRE_BLAST: return Normal.FIRE_BLAST;
            default: return null;
        }
    }
}
