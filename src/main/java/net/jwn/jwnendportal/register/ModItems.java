package net.jwn.jwnendportal.register;

import net.jwn.jwnendportal.JWNsEndPortalMod;
import net.jwn.jwnendportal.generator.EndPortalGenerator;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JWNsEndPortalMod.MOD_ID);

    public static final DeferredItem<Item> END_PORTAL_GENERATOR = ITEMS.registerItem("end_portal_generator",
            (properties) -> new EndPortalGenerator(properties
                    .durability(2)
            )
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
