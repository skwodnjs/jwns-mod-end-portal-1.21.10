package net.jwn.jwnendportal.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Optional;
import java.util.UUID;

public class PlayerPortalPos extends SavedData {
    private final String name;
    private final UUID playerUUID;
    private BlockPos overworldPos = null;
    private BlockPos enderPos = null;

    public static final Codec<PlayerPortalPos> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(PlayerPortalPos::getName),
                    UUIDUtil.CODEC.fieldOf("playerUUID").forGetter(PlayerPortalPos::getPlayerUUID),
                    BlockPos.CODEC.optionalFieldOf("overworldPos").forGetter(p -> Optional.ofNullable(p.getOverworldPos())),
                    BlockPos.CODEC.optionalFieldOf("enderPos").forGetter(p -> Optional.ofNullable(p.getEnderPos()))
            ).apply(instance, (name, uuid, overworld, ender) -> {
                PlayerPortalPos pos = new PlayerPortalPos(name, uuid);
                overworld.ifPresent(pos::setOverworldPos);
                ender.ifPresent(pos::setEndPos);
                return pos;
            }));

    public PlayerPortalPos(String name, UUID playerUUID) {
        this.name = name;
        this.playerUUID = playerUUID;
    }

    // setter

    public void setEndPos(BlockPos enderPos) {
        this.enderPos = enderPos;
    }

    public void setOverworldPos(BlockPos overworldPos) {
        this.overworldPos = overworldPos;
    }

    // getter

    public BlockPos getEnderPos() {
        return enderPos;
    }

    public BlockPos getOverworldPos() {
        return overworldPos;
    }

    public String getName() {
        return name;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
}
