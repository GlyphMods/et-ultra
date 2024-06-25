package martian.etultra.common.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import earth.terrarium.adastra.common.registry.ModFluids;
import earth.terrarium.adastra.common.tags.ModFluidTags;
import earth.terrarium.adastra.common.utils.FluidUtils;
import earth.terrarium.adastra.common.utils.TooltipUtils;
import earth.terrarium.botarium.common.fluid.FluidConstants;
import earth.terrarium.botarium.common.fluid.base.BotariumFluidItem;
import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.ItemFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidContainer;
import earth.terrarium.botarium.common.fluid.impl.WrappedItemFluidContainer;
import earth.terrarium.botarium.common.fluid.utils.ClientFluidHooks;
import earth.terrarium.botarium.common.item.ItemStackHolder;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class ItemOxygenDrill extends Item implements BotariumFluidItem<WrappedItemFluidContainer> {
    public ItemOxygenDrill(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (FluidUtils.hasFluid(stack) || player.isCreative()) {
            player.startUsingItem(hand);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        super.onUseTick(level, entity, stack, remainingUseDuration);

        if (!(entity instanceof Player player))
            return;

        if (player.getCooldowns().isOnCooldown(this))
            return;

        boolean using = consumeFuel(player, stack, 1);
        if (!using && !player.isCreative()) {
            player.stopUsingItem();
            return;
        }

        var hit = player.pick(player.getBlockReach(), 0, false);
        if (hit.getType() != HitResult.Type.BLOCK)
            return;
        var blockHit = (BlockHitResult) hit;
        if (!level.isInWorldBounds(blockHit.getBlockPos()))
            return;

        var state = level.getBlockState(blockHit.getBlockPos());
        if (state.getDestroySpeed(level, blockHit.getBlockPos()) < 0)
            return;

        if (!level.isClientSide)
            level.destroyBlock(blockHit.getBlockPos(), true, entity);

        player.getCooldowns().addCooldown(this, 10);
    }

    public boolean consumeFuel(Player player, ItemStack stack, long amount) {
        if (!(stack.getItem() instanceof ItemOxygenDrill))
            return false;
        if (player.isCreative())
            return true;
        ItemStackHolder holder = new ItemStackHolder(stack);
        ItemFluidContainer container = FluidContainer.of(holder);
        if (container == null)
            return false;
        FluidHolder extracted = container.extractFluid(FluidHolder.ofMillibuckets(container.getFirstFluid().getFluid(), FluidConstants.fromMillibuckets(amount)), false);
        stack.setTag(holder.getStack().getTag());
        return extracted.getFluidAmount() > 0;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public WrappedItemFluidContainer getFluidContainer(ItemStack holder) {
        return new WrappedItemFluidContainer(
                holder,
                new SimpleFluidContainer(
                        FluidConstants.fromMillibuckets(3000),
                        1,
                        (t, f) -> f.is(ModFluidTags.OXYGEN)
                )
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> text, TooltipFlag flag) {
        text.add(TooltipUtils.getFluidComponent(
                FluidUtils.getTank(stack),
                FluidUtils.getTankCapacity(stack),
                ModFluids.OXYGEN.get()));
        TooltipUtils.addDescriptionComponent(text, Component.literal("Consumes Oxygen to drill blocks efficiently."));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return FluidUtils.hasFluid(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        WrappedItemFluidContainer container = getFluidContainer(stack);
        return (int) (((double) container.getFirstFluid().getFluidAmount() / container.getTankCapacity(0)) * 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return ClientFluidHooks.getFluidColor(FluidUtils.getTank(stack));
    }

    public boolean shouldCauseReequipAnimation(ItemStack old, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public boolean applyForgeHandTransform(PoseStack stack, LocalPlayer player, HumanoidArm arm, ItemStack itemStack, float partialTick, float equipProcess, float swingProcess) {
                if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0) {
                    int i = arm == HumanoidArm.RIGHT ? 1 : -1;
                    stack.translate((float) i * 0.56F, -0.52F + equipProcess * -0.6F, -0.72F);
                    if (arm == HumanoidArm.RIGHT) {
                        stack.translate(-0.25, 0.22, 0.35);
                        stack.mulPose(Axis.XP.rotationDegrees(-80.0F));
                        stack.mulPose(Axis.YP.rotationDegrees(90.0F));
                        stack.mulPose(Axis.ZP.rotationDegrees(0.0F));
                        stack.mulPose(Axis.XP.rotationDegrees(-15F));
                    } else {
                        stack.translate(0.1, 0.83, 0.35);
                        stack.mulPose(Axis.XP.rotationDegrees(-80.0F));
                        stack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                        stack.mulPose(Axis.XP.rotationDegrees(-15F));
                        stack.translate(-0.3, 0.22, 0.35);
                    }
                    return true;
                }
                return false;
            }

            public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
                if (entity.isUsingItem() && entity.getUseItemRemainingTicks() > 0) {
                    return HumanoidModel.ArmPose.CROSSBOW_HOLD;
                }
                return HumanoidModel.ArmPose.ITEM;
            }
        });
    }
}
