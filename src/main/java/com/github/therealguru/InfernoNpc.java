package com.github.therealguru;

import lombok.Getter;
import net.runelite.api.NPC;

import static com.github.therealguru.Inferno2dMapPlugin.NPC_ID_TO_COLOUR;

@Getter
public class InfernoNpc {

    private final NPC npc;
    private final ColourRegion colourRegion;

    public InfernoNpc(NPC npc) {
        this.npc = npc;
        Coordinate southWest = new Coordinate(npc);
        Coordinate northEast = southWest.transform(npc.getComposition().getSize() - 1, npc.getComposition().getSize() - 1);
        this.colourRegion = new ColourRegion(southWest, northEast, NPC_ID_TO_COLOUR.get(npc.getId()));
    }
}
