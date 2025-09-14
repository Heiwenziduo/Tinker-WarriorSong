package com.github.heiwenziduo.tinker_warrior_song.t_construct.modifiers;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import com.github.heiwenziduo.tinker_warrior_song.api.ManagerAbbr;
import com.github.heiwenziduo.tinker_warrior_song.initializer.InitHook;
import com.github.heiwenziduo.tinker_warrior_song.t_construct.hooks.KillingHook;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.stats.ToolType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

/// 千年, 即 630,720,000,000 tick
public class Millennium extends NoLevelsModifier implements
        KillingHook, TooltipModifierHook, GeneralInteractionModifierHook, InventoryTickModifierHook, ToolStatsModifierHook, MeleeHitModifierHook, AttributesModifierHook
{
    public static final ToolType[] CAN_BE_USE_ON_TYPES = {ToolType.MELEE};
    public static final int TickConsumePerT = 1;

    private static final ResourceLocation MILLENNIUM_TIME = ResourceLocation.fromNamespaceAndPath(TinkerWarriorSong.ModId, "millennium_time");
    private static final ResourceLocation MILLENNIUM_ACTIVE = ResourceLocation.fromNamespaceAndPath(TinkerWarriorSong.ModId, "millennium_active");
    private static final ResourceLocation MILLENNIUM_RANK = ResourceLocation.fromNamespaceAndPath(TinkerWarriorSong.ModId, "millennium_rank");

    // from @DamageSpeedTradeModifier
    private final Lazy<UUID> uuid = Lazy.of(() -> UUID.nameUUIDFromBytes(getId().toString().getBytes()));
    private final Lazy<String> attack_damage = Lazy.of(() -> {
        ResourceLocation id = getId();
        return id.getPath() + "." + id.getNamespace() + ".attack_damage";
    });
    private final Lazy<String> attack_speed = Lazy.of(() -> {
        ResourceLocation id = getId();
        return id.getPath() + "." + id.getNamespace() + ".attack_speed";
    });

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    protected void registerHooks(ModuleHookMap.Builder hookBuilder) {
        hookBuilder.addHook(this, InitHook.KILLING_HOOK, ModifierHooks.TOOLTIP, ModifierHooks.GENERAL_INTERACT, ModifierHooks.INVENTORY_TICK, ModifierHooks.TOOL_STATS, ModifierHooks.MELEE_HIT, ModifierHooks.ATTRIBUTES);
    }


    @Override
    public void addAttributes(IToolStackView tool, ModifierEntry modifier, EquipmentSlot slot, BiConsumer<Attribute, AttributeModifier> consumer) {
        // runs everyTick, this method is good.
        if (slot == EquipmentSlot.MAINHAND && isActive(tool)){
            RANK R = calculateRank(tool);
            consumer.accept(Attributes.ATTACK_DAMAGE, new AttributeModifier(uuid.get(), attack_damage.get(), R.attackDamage, AttributeModifier.Operation.MULTIPLY_TOTAL));
            consumer.accept(Attributes.ATTACK_SPEED, new AttributeModifier(uuid.get(), attack_speed.get(), R.attackSpeed, AttributeModifier.Operation.ADDITION));
        }
    }

    @Override
    public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
        // "Called whenever tool stats are rebuilt."

        // 09/14 toolStats 好像不能动态更新 ?
        /*
        boolean isA = context.getPersistentData().getBoolean(MILLENNIUM_ACTIVE);
        System.out.println("addToolStats: " + context.getPersistentData().getBoolean(MILLENNIUM_ACTIVE)); // false, then stop update
        if(isA){
            String rankName = context.getPersistentData().getString(MILLENNIUM_RANK);
            System.out.println("addToolStats: " + rankName);
            if (rankName.isEmpty()) rankName = RANK.E.name;
            RANK R = RANK.fromName(rankName);
            HookHelper.runAddToolStats(R, modifier, builder);
        }
        */
    }

    @Override
    public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
        if (!canModified(tool)) return;
        if (!isActive(tool)) return;

        LivingEntity target = context.getLivingTarget();
        if (target != null) {
            HookHelper.runAfterMeleeHit(tool, target);

            // LivingEntity attacker = context.getAttacker();
            // ToolDamageUtil.damageAnimated(tool, 2, attacker, context.getSlotType());
        }
    }

    @Override
    public void onKillLivingTarget(IToolStackView tool, LivingDeathEvent event, LivingEntity attacker, LivingEntity target, int level) {
        if (!canModified(tool)) return;

        if(event.getSource().getEntity() == attacker) {
            float addTime = target.tickCount;
            reapTime(tool, addTime);

            // 其实nan检测不必要, 懒得改了
//            if (!Float.isNaN(time0)){
//                tool.getPersistentData().putFloat(MILLENNIUM_TIME, time0 + addTime);
//            } else {
//                tool.getPersistentData().putFloat(MILLENNIUM_TIME, addTime);
//            }
        }
    }

    @Override
    public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
        if (!canModified(tool)) return;
        if(world.isClientSide) return;  // tick会在客户端执行
        if (holder.tickCount % 100 == 0) calculateRankAndSave(tool);
        // if (!(holder instanceof Player player)) return;

        if (!isActive(tool)) return;

        float time = tool.getPersistentData().getFloat(MILLENNIUM_TIME);
        if (time >= TickConsumePerT) {
            time -= TickConsumePerT;
        } else {
            time = 0;
            setActive(tool, false, holder);
        }
        tool.getPersistentData().putFloat(MILLENNIUM_TIME, time);
    }

    @Override
    public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
        if (source == InteractionSource.RIGHT_CLICK && !tool.isBroken()) {
            GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
        if (!canModified(tool)) return;

        if (!tool.isBroken() && entity instanceof Player player) {
            triggerActive(tool, player);
        }
    }

    @Override
    public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
        return 1;
    }

    @Override
    public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
        if (!canModified(tool)) return;

        double sec = Math.floor(tool.getPersistentData().getFloat(MILLENNIUM_TIME) / 20);
        RANK R = calculateRank(tool);
        tooltip.add(Component.literal("已积攒: " + (Double.isNaN(sec) ? 0 : sec) + "秒"));
        tooltip.add(Component.literal("位阶: " + R));
        tooltip.add(Component.literal("离下一阶还有: " + (Math.floor(R.nextRank().tickRequired / 20) - sec) + "秒"));
    }


    private static RANK calculateRankAndSave(IToolStackView tool) {
        RANK R = calculateRank(tool);
        tool.getPersistentData().putString(MILLENNIUM_RANK, R.name);
        System.out.println("calculateRankAndSave: " + R.name);
        return R;
    }
    private static RANK calculateRank(IToolStackView tool) {
        float ticks = tool.getPersistentData().getFloat(MILLENNIUM_TIME);
        return calculateRank(ticks);
    }
    private static RANK calculateRank(float ticks) {
        RANK rank = RANK.E;
        for(RANK r : RANK.values()){
            if(ticks >= r.tickRequired) {
                rank = r;
                break;
            }
        }
        return rank;
    }

    private static boolean triggerActive(IToolStackView tool, LivingEntity user) {
        boolean mode = tool.getPersistentData().getBoolean(MILLENNIUM_ACTIVE);
        tool.getPersistentData().putBoolean(MILLENNIUM_ACTIVE, !mode);
        Level pLevel = user.level();
        if(!mode){
            pLevel.playSound((Player)null, user.getX(), user.getY(), user.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            pLevel.playSound((Player)null, user.getX(), user.getY(), user.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return !mode;
    }

    private static void setActive(IToolStackView tool, boolean mode, LivingEntity user) {
        tool.getPersistentData().putBoolean(MILLENNIUM_ACTIVE, mode);
        if (isActive(tool) != mode) triggerActive(tool, user);
    }

    private static boolean isActive(IToolStackView tool) {
        return tool.getPersistentData().getBoolean(MILLENNIUM_ACTIVE);
    }

    private static boolean canModified(IToolStackView tool) {
        ToolType type = ToolType.from(tool.getItem(), CAN_BE_USE_ON_TYPES);
        return type != null;
    }

    private static void reapTime(IToolStackView tool, float time) {
        float time0 = tool.getPersistentData().getFloat(MILLENNIUM_TIME);
        System.out.println("reapTime: " + time0 + " += " + time);

        tool.getPersistentData().putFloat(MILLENNIUM_TIME, time0 + time);
    }

    // ****************************************************************************
    private static class HookHelper {
        public static void calculateTooltip() {

        }

        public static void runAddToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
            float ticks = context.getPersistentData().getFloat(MILLENNIUM_TIME);
            RANK R = calculateRank(ticks);
            runAddToolStats(R, modifier, builder);
        }
        public static void runAddToolStats(RANK R, ModifierEntry modifier, ModifierStatsBuilder builder) {
            System.out.println("runAddToolStats: " + R.name);
            switch (R) {
                case E -> {
                    ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 1.1);
                    ToolStats.ATTACK_SPEED.add(builder, 0);
                }
                case D -> {
                    ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 1.3);
                    ToolStats.ATTACK_SPEED.add(builder, 0.1);
                }
                case C, B, A, S, SS, SSS -> {
                    ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 2);
                    ToolStats.ATTACK_SPEED.add(builder, 0.4);
                }
            }
        }

        public static void runAfterMeleeHit(IToolStackView tool, LivingEntity target) {
            RANK R = calculateRank(tool);
            switch (R) {
                case B, A, S, SS, SSS -> {
                    ManagerAbbr.setTimeLock(target, 40);
                    reapTime(tool, 40);
                }
            }
        }

    }

    public enum RANK {
//        SSS("SSS", 630720000000f, 2.0f, 1.0f),
//        SS ("SS",  63072000000f,  2.0f, 1.0f),
//        S  ("S",   6307200000f,   2.0f, 1.0f),
//        A  ("A",   630720000f,    2.0f, 1.0f),
//        B  ("B",   63072000f,     1.5f, 1.0f),
//        C  ("C",   6307200f,      1.0f, 0.4f),
//        D  ("D",   630720f,       0.3f, 0.1f),
//        E  ("E",   0f,            0.1f, 0.0f);

        // for the sake of test
        SSS("SSS", 6307f, 2.0f, 0.4f),
        SS ("SS",  630f,  2.0f, 0.4f),
        S  ("S",   630f,   2.0f, 0.4f),
        A  ("A",   630f,    2.0f, 0.4f),
        B  ("B",   630f,     2.0f, 0.4f),
        C  ("C",   630f,      2.0f, 0.4f),
        D  ("D",   63f,       1.3f, 0.1f),
        E  ("E",   0f,            1.1f, 0.0f);

        public final float tickRequired;
        public final float attackDamage;
        public final float attackSpeed;
        public final String name;

        public RANK nextRank() {
            var l = Arrays.stream(RANK.values()).filter(r -> r.tickRequired > this.tickRequired).toList();
            return l.isEmpty() ? RANK.SSS : l.get(l.size() - 1);
        }

        public static RANK fromName(String name) {
            for (RANK R : RANK.values()) {
                if (Objects.equals(R.name, name)){
                    return R;
                }
            }
            System.out.println("RANK#fromNameErrot: noSuchName");
            return RANK.E;
        }

        RANK(String name, float tickRequired, float attackDamage, float attackSpeed) {
            this.name = name;
            this.tickRequired = tickRequired;
            this.attackDamage = attackDamage;
            this.attackSpeed = attackSpeed;
        }
    }
}