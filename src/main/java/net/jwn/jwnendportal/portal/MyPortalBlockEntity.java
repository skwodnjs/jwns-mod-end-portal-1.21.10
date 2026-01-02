package net.jwn.jwnendportal.portal;

import net.jwn.jwnendportal.register.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.UUID;

public class MyPortalBlockEntity extends TheEndPortalBlockEntity {
    private UUID owner = null;
    private String ownerName = "unknown";

    public MyPortalBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CUSTOM_END_PORTAL_BLOCK_ENTITY.get(), pos, blockState);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.setChanged();
    }

    public void setOwnerName(String name) {
        this.ownerName = name;
        this.setChanged();
    }

    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.owner != null) output.store("owner", UUIDUtil.CODEC, this.owner);
        output.putString("ownerName", this.ownerName);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.owner = input.read("owner", UUIDUtil.CODEC).orElse(null);
        this.ownerName = input.getStringOr("ownerName", "unknown");
    }
}
