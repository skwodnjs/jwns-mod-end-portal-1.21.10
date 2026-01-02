package net.jwn.jwnendportal.register;

import net.jwn.jwnendportal.JWNsEndPortalMod;
import net.jwn.jwnendportal.portal.MyPortalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(JWNsEndPortalMod.MOD_ID);

    public static final DeferredBlock<MyPortalBlock> CUSTOM_END_PORTAL = BLOCKS.registerBlock("custom_end_portal",
            (properties) -> new MyPortalBlock(properties.mapColor(MapColor.COLOR_BLACK).noCollision()
                    .lightLevel((p_152692_) -> 15).strength(-1.0F, 3600000.0F)
                    .noLootTable().pushReaction(PushReaction.BLOCK).sound(SoundType.GLASS)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
