package com.allinone.skills.firemaking.data;

import org.dreambot.api.methods.map.Area;
import java.util.ArrayList;
import java.util.List;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.interactive.Players;

public class StaticBurnAreas {
    public static final List<BurnArea> AREAS = new ArrayList<>();

    static {
        // Grand Exchange (West)
        AREAS.add(new BurnArea("Grand Exchange", new Area(3155, 3480, 3175, 3500), BankLocation.GRAND_EXCHANGE));

        // Varrock West (South of Bank)
        AREAS.add(new BurnArea("Varrock West", new Area(3180, 3425, 3186, 3433), BankLocation.VARROCK_WEST));
        
        // Varrock East (South of Bank)
        AREAS.add(new BurnArea("Varrock East", new Area(3250, 3418, 3258, 3430), BankLocation.VARROCK_EAST));

        // Draynor Village (Near Bank)
        AREAS.add(new BurnArea("Draynor Village", new Area(3088, 3240, 3097, 3250), BankLocation.DRAYNOR));

        // Seers Village (South of Bank)
        AREAS.add(new BurnArea("Seers Village", new Area(2720, 3465, 2730, 3478), BankLocation.SEERS));

        // Falador East (East of bank)
        AREAS.add(new BurnArea("Falador East", new Area(3018, 3350, 3028, 3358), BankLocation.FALADOR_EAST));
    }

    public static BurnArea getNearest() {
        BurnArea best = null;
        double minDist = Double.MAX_VALUE;
        for (BurnArea ba : AREAS) {
            double dist = ba.getArea().getCenter().distance(Players.getLocal());
            if (dist < minDist) {
                minDist = dist;
                best = ba;
            }
        }
        return best;
    }

    public static class BurnArea {
        private final String name;
        private final Area area;
        private final BankLocation nearestBank;

        public BurnArea(String name, Area area, BankLocation nearestBank) {
            this.name = name;
            this.area = area;
            this.nearestBank = nearestBank;
        }

        public String getName() { return name; }
        public Area getArea() { return area; }
        public BankLocation getNearestBank() { return nearestBank; }
    }
}
