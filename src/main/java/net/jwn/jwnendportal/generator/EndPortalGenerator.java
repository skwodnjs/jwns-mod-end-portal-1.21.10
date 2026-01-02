package net.jwn.jwnendportal.generator;

import net.jwn.jwnendportal.data.PlayerPortalPosSavedData;
import net.jwn.jwnendportal.portal.MyPortalBlockEntity;
import net.jwn.jwnendportal.register.ModBlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

public class EndPortalGenerator extends Item {
    public EndPortalGenerator(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel) {
            PlayerPortalPosSavedData.get(serverLevel.getServer()).printAll();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            PlayerPortalPosSavedData portalData = PlayerPortalPosSavedData.get(serverLevel.getServer());
            Player player = context.getPlayer();
            if (player != null) {
                AdvancementHolder adv = serverLevel.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath("minecraft", "end/enter_end_gateway"));
                if (adv != null && !((ServerPlayer) player).getAdvancements().getOrStartProgress(adv).isDone()) {
                    player.displayClientMessage(Component.translatable("message.jwnendportal.remote_gateway_to_open_portal"), false);
                    return InteractionResult.SUCCESS;
                }


                UUID uuid = context.getPlayer().getUUID();
                ServerLevel targetLevel;
                if (serverLevel.dimension() == Level.OVERWORLD) {
                    if (portalData.getOverworldPos(uuid) == null) {
                        portalData.setOverworldPos(uuid, context.getClickedPos());
                    } else {
                        player.displayClientMessage(Component.translatable("message.jwnendportal.no_more_overworld"), false);
                        return InteractionResult.SUCCESS;
                    }
                    targetLevel = serverLevel.getServer().getLevel(Level.END);
                }
                else if (serverLevel.dimension() == Level.END) {
                    if (portalData.getEndPos(uuid) == null) {
                        portalData.setEndPos(uuid, context.getClickedPos());
                    } else {
                        player.displayClientMessage(Component.translatable("message.jwnendportal.no_more_end"), false);
                        return InteractionResult.SUCCESS;
                    }
                    targetLevel = serverLevel.getServer().getLevel(Level.OVERWORLD);
                } else {
                    targetLevel = null;
                }

                if (targetLevel == null) {
                    player.displayClientMessage(Component.translatable("message.jwnendportal.cannot_place_nether"), false);
                    return InteractionResult.SUCCESS;
                }

                serverLevel.setBlock(context.getClickedPos(), ModBlocks.CUSTOM_END_PORTAL.get().defaultBlockState(), 3);

                if (serverLevel.getBlockEntity(context.getClickedPos()) instanceof  MyPortalBlockEntity blockEntity) {
                    blockEntity.setOwner(uuid);
                    blockEntity.setOwnerName(context.getPlayer().getPlainTextName());
                }

                if (!(player.isCreative())) {
                    applyDamage(context.getItemInHand().getDamageValue() + 1,
                            player,
                            (item) -> player.onEquippedItemBroken(item, context.getHand().asEquipmentSlot()),
                            context.getItemInHand());
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void applyDamage(int damage, @Nullable LivingEntity entity, Consumer<Item> consumer, ItemStack itemStack) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(serverPlayer, itemStack, damage);
        }

        itemStack.setDamageValue(damage);
        if (itemStack.isBroken()) {
            Item item = itemStack.getItem();
            itemStack.shrink(1);
            consumer.accept(item);
        }
    }
}
