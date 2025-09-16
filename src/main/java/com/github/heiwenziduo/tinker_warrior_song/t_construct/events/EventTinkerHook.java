package com.github.heiwenziduo.tinker_warrior_song.t_construct.events;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import com.github.heiwenziduo.tinker_warrior_song.t_construct.hooks.DamageRedirectHook;
import com.github.heiwenziduo.tinker_warrior_song.t_construct.hooks.TWSHooks;
import com.github.heiwenziduo.tinker_warrior_song.t_construct.hooks.KillingHook;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.HashSet;

@Mod.EventBusSubscriber(modid = TinkerWarriorSong.ModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventTinkerHook {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void triggerKillingHook(LivingDeathEvent event) {
        //if (event.isCanceled()) return;
        // bug: 死亡钩子在有伤害源重定向时会触发两次

        Entity entity = event.getSource().getEntity();
        if (entity instanceof LivingEntity attacker) {
            HashSet<ToolStack> s = new HashSet<>();
            ToolStack tinkerTool1 = Modifier.getHeldTool(attacker, InteractionHand.MAIN_HAND);
            ToolStack tinkerTool2 = Modifier.getHeldTool(attacker, InteractionHand.OFF_HAND);
            if (tinkerTool1 != null) s.add(tinkerTool1);
            if (tinkerTool2 != null) s.add(tinkerTool2);

            s.forEach(t -> {
                t.getModifierList().forEach((entry) -> {
                    KillingHook hook = entry.getHook(TWSHooks.KILLING_HOOK);
                    hook.onKillLivingTarget(t, event, attacker, event.getEntity(), entry.getLevel());
                });
            });

        }

    }

    /// {@link LivingEntity#actuallyHurt(DamageSource, float)}
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void triggerDamageRedirectHook(LivingHurtEvent event) {
        if (!event.getSource().is(DamageTypes.PLAYER_ATTACK)) return;
        DamageSource oldSource = event.getSource();
        Entity entity = oldSource.getEntity();
        if (entity instanceof Player attacker) {
            LivingEntity target = event.getEntity();
            ToolStack tinkerTool = Modifier.getHeldTool(attacker, InteractionHand.MAIN_HAND);
            if (tinkerTool != null) {
                DamageSource newSource;

                for (ModifierEntry entry : tinkerTool.getModifierList()) {
                    DamageRedirectHook hook = entry.getHook(TWSHooks.DAMAGE_REDIRECT_HOOK);
                    newSource = hook.redirectDamageSource(tinkerTool, attacker, target, entry.getLevel(), oldSource);
                    // 只有优先级最高的钩子会生效
                    if (newSource != null && !newSource.is(DamageTypes.PLAYER_ATTACK)) {
                        System.out.println("RedirectDamageSource: " + oldSource + " --> " + newSource + "( " + event.getAmount());
                        // 重定向伤害
                        event.setCanceled(true);
                        target.invulnerableTime = 0;
                        target.hurt(newSource, event.getAmount());
                        break;
                    }
                }
            }
        }
    }
}
