package net.jwn.jwnendportal.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerPortalPosSavedData extends SavedData {
    public static final SavedDataType<PlayerPortalPosSavedData> TYPE =
            new SavedDataType<>(
                    "player_portal_pos_data",
                    PlayerPortalPosSavedData::new,
                    RecordCodecBuilder.create(instance -> instance.group(
                            Codec.unboundedMap(UUIDUtil.STRING_CODEC, PlayerPortalPos.CODEC)
                                    .fieldOf("players")
                                    .forGetter(d -> d.players)
                            ).apply(instance, PlayerPortalPosSavedData::new))
            );

    private final Map<UUID, PlayerPortalPos> players;

    private PlayerPortalPosSavedData() {
        this.players = new HashMap<>();
    }

    private PlayerPortalPosSavedData(Map<UUID, PlayerPortalPos> players) {
        this.players = new HashMap<>(players);
    }

    public static PlayerPortalPosSavedData get(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            throw new IllegalStateException("Overworld not loaded");
        }
        return overworld.getDataStorage().computeIfAbsent(TYPE);
    }

    public void createPlayerProfileIfAbsent(ServerPlayer player) {
        players.computeIfAbsent(player.getUUID(), uuid -> new PlayerPortalPos(player.getPlainTextName(), player.getUUID()));
        setDirty();
    }

    public void printAll() {
        System.out.println("=== Player Portal Positions ===");
        players.forEach((uuid, pos) -> {
            System.out.println("Player: " + pos.getName() + " (" + uuid + ")");
            System.out.println(" - Overworld: " + pos.getOverworldPos());
            System.out.println(" - Ender: " + pos.getEnderPos());
        });
    }

    public void setOverworldPos(UUID player, BlockPos pos) {
        PlayerPortalPos playerPortalPos = players.get(player);
        if (playerPortalPos != null) {
            playerPortalPos.setOverworldPos(pos);
        }
        setDirty();
    }

    public void setEndPos(UUID player, BlockPos pos) {
        PlayerPortalPos playerPortalPos = players.get(player);
        if (playerPortalPos != null) {
            playerPortalPos.setEndPos(pos);
        }
        setDirty();
    }

    public BlockPos getOverworldPos(UUID player) {
        PlayerPortalPos playerPortalPos = players.get(player);
        if (playerPortalPos != null) {
            return playerPortalPos.getOverworldPos();
        } else {
            return null;
        }
    }

    public BlockPos getEndPos(UUID player) {
        PlayerPortalPos playerPortalPos = players.get(player);
        if (playerPortalPos != null) {
            return playerPortalPos.getEnderPos();
        } else {
            return null;
        }
    }
}
