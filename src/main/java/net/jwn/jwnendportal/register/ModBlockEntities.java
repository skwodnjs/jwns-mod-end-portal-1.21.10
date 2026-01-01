package net.jwn.jwnendportal.register;

import net.jwn.jwnendportal.JWNsEndPortalMod;
import net.jwn.jwnendportal.portal.MyPortalBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, JWNsEndPortalMod.MOD_ID);

    public static final Supplier<BlockEntityType<MyPortalBlockEntity>> CUSTOM_END_PORTAL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("custom_end_portal_be", () -> new BlockEntityType<>(
                    MyPortalBlockEntity::new, ModBlocks.CUSTOM_END_PORTAL.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
