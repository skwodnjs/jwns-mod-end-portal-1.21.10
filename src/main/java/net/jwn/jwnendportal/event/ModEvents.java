package net.jwn.jwnendportal.event;

import net.jwn.jwnendportal.JWNsEndPortalMod;
import net.jwn.jwnendportal.data.PlayerPortalPosSavedData;
import net.jwn.jwnendportal.portal.MyPortalBlockEntity;
import net.jwn.jwnendportal.register.ModBlocks;
import net.jwn.jwnendportal.register.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

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
        if (!event.getEntity().isCreative() && event.getLevel() instanceof ServerLevel level) {
            UUID uuid = event.getEntity().getUUID();
            if (level.getBlockEntity(event.getPos()) instanceof MyPortalBlockEntity entity
                    && event.getEntity().getMainHandItem().is(ModItems.END_PORTAL_GENERATOR)) {
                if (Objects.equals(entity.getOwner(), uuid)) {
                    level.destroyBlock(event.getPos(), false, event.getEntity());
                } else {
                    event.getEntity().displayClientMessage(
                            Component.translatable("message.jwnendportal.destroy_only_own_portal", entity.getOwnerName()), false
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public static void traceCustomEndPortal(ServerTickEvent.Post event) {
        PlayerPortalPosSavedData data = PlayerPortalPosSavedData.get(event.getServer());
        data.getAll().forEach((uuid, playerPortalPos) -> {
            if (playerPortalPos.getOverworldPos() != null) {
                if (event.getServer().getLevel(Level.OVERWORLD) != null &&
                        event.getServer().getLevel(Level.OVERWORLD).isLoaded(playerPortalPos.getOverworldPos()) &&
                        !event.getServer().getLevel(Level.OVERWORLD).getBlockState(playerPortalPos.getOverworldPos()).is(ModBlocks.CUSTOM_END_PORTAL)) {
                    data.setOverworldPos(uuid, null);
                }
            }
            if (playerPortalPos.getEndPos() != null) {
                if (event.getServer().getLevel(Level.END) != null &&
                        event.getServer().getLevel(Level.END).isLoaded(playerPortalPos.getEndPos()) &&
                        !event.getServer().getLevel(Level.END).getBlockState(playerPortalPos.getEndPos()).is(ModBlocks.CUSTOM_END_PORTAL)) {
                    data.setEndPos(uuid, null);
                }
            }
        });
    }
}
