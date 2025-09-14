package com.github.heiwenziduo.tinker_warrior_song.t_construct.events;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import com.github.heiwenziduo.tinker_warrior_song.initializer.InitHook;
import com.github.heiwenziduo.tinker_warrior_song.t_construct.hooks.KillingHook;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = TinkerWarriorSong.ModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventTinkerHook {

    @SubscribeEvent
    public static void onLivingKill(LivingDeathEvent event) {
        Entity entity = event.getSource().getEntity();
        if (entity instanceof LivingEntity attacker) {
            HashSet<ToolStack> s = new HashSet<>();
            ToolStack tinkerTool1 = Modifier.getHeldTool(attacker, InteractionHand.MAIN_HAND);
            ToolStack tinkerTool2 = Modifier.getHeldTool(attacker, InteractionHand.OFF_HAND);
            if (tinkerTool1 != null) s.add(tinkerTool1);
            if (tinkerTool2 != null) s.add(tinkerTool2);

            s.forEach(t -> {
                t.getModifierList().forEach((e) -> {
                    KillingHook hook = e.getHook(InitHook.KILLING_HOOK);
                    hook.onKillLivingTarget(t, event, attacker, event.getEntity(), e.getLevel());
                });
            });

//            ToolStack tool = TinkerToolUtil.getToolInHand(attacker);
//            if (!TinkerToolUtil.isNotBrokenOrNull(tool)) {
//                return;
//            }
//
//            tool.getModifierList().forEach((e) -> {
//                KillingHook hook = e.getHook(InitHook.KILLING_HOOK);
//                hook.onKillLivingTarget(tool, event, attacker, event.getEntity(), e.getLevel());
//            });
        }

    }
}
