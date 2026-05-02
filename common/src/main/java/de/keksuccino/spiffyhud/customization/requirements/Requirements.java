package de.keksuccino.spiffyhud.customization.requirements;

import de.keksuccino.fancymenu.customization.requirement.RequirementRegistry;

public class Requirements {

    public static final IsPlayerUsingItemRequirement IS_PLAYER_USING_ITEM = new IsPlayerUsingItemRequirement();
    public static final IsPlayerInStructureRequirement IS_PLAYER_IN_STRUCTURE = new IsPlayerInStructureRequirement();

    public static void registerAll() {

        RequirementRegistry.register(IS_PLAYER_USING_ITEM);
        RequirementRegistry.register(IS_PLAYER_IN_STRUCTURE);

    }

}
