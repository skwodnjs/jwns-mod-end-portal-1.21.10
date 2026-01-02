package net.jwn.jwnendportal.event;

import net.jwn.jwnendportal.JWNsEndPortalMod;
import net.jwn.jwnendportal.data.PlayerPortalPosSavedData;
import net.jwn.jwnendportal.portal.MyPortalBlockEntity;
import net.jwn.jwnendportal.register.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(modid = JWNsEndPortalMod.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            PlayerPortalPosSavedData.get(serverPlayer.level().getServer()).createPlayerProfileIfAbsent(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void breakCustomEndPortal(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel() instanceof ServerLevel level) {
            UUID uuid = event.getEntity().getUUID();
            if (level.getBlockEntity(event.getPos()) instanceof MyPortalBlockEntity entity
                    && event.getEntity().getMainHandItem().is(ModItems.END_PORTAL_GENERATOR)) {
                if (Objects.equals(entity.getOwner(), uuid)) {
                    level.destroyBlock(event.getPos(), false, event.getEntity());

                    ResourceKey<Level> resourcekey = level.dimension();
                    PlayerPortalPosSavedData data = PlayerPortalPosSavedData.get(level.getServer());
                    if (resourcekey == Level.OVERWORLD) {
                        data.setOverworldPos(uuid, null);
                    } else if (resourcekey == Level.END) {
                        data.setEndPos(uuid, null);
                    }
                } else {
                    event.getEntity().displayClientMessage(
                            Component.translatable("message.jwnendportal.destroy_only_own_portal", entity.getOwnerName()), false
                    );
                }
            }
        }
    }
}
