package net.jwn.jwnendportal.portal;

import net.jwn.jwnendportal.data.PlayerPortalPosSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class MyPortalBlock extends EndPortalBlock {
    public MyPortalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MyPortalBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable TeleportTransition getPortalDestination(ServerLevel serverLevel, Entity entity, BlockPos pos) {
        PlayerPortalPosSavedData data = PlayerPortalPosSavedData.get(serverLevel.getServer());
        if (!(serverLevel.getBlockEntity(pos) instanceof MyPortalBlockEntity blockEntity)) return null;

        UUID owner = blockEntity.getOwner();
        ResourceKey<Level> resourcekey = serverLevel.dimension();
        boolean isEnder = resourcekey == Level.END;
        ResourceKey<Level> targetLevelkey = isEnder ? Level.OVERWORLD : Level.END;
        BlockPos blockpos = isEnder ? data.getOverworldPos(owner) : data.getEndPos(owner);
        if (blockpos == null) return null;
        ServerLevel targetLevel = serverLevel.getServer().getLevel(targetLevelkey);
        if (targetLevel == null) return null;

        Vec3 vec3 = blockpos.getBottomCenter();
        Set<Relative> set = Relative.union(Relative.DELTA, Set.of(Relative.X_ROT, Relative.Y_ROT));
        return new TeleportTransition(targetLevel, vec3, Vec3.ZERO,
                0.0F, 0.0F, set, TeleportTransition.PLAY_PORTAL_SOUND.then(TeleportTransition.PLACE_PORTAL_TICKET));
    }
}
