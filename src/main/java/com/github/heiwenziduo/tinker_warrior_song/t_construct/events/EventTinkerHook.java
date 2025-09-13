package com.github.heiwenziduo.tinker_warrior_song.t_construct.events;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import com.github.heiwenziduo.tinker_warrior_song.initializer.InitHook;
import com.github.heiwenziduo.tinker_warrior_song.t_construct.hooks.KillingHook;
import com.github.heiwenziduo.tinker_warrior_song.t_construct.utils.TinkerToolUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mod.EventBusSubscriber(modid = TinkerWarriorSong.ModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventTinkerHook {

    @SubscribeEvent
    public static void onLivingKill(LivingDeathEvent event) {
        Entity entity = event.getSource().getEntity();
        if (entity instanceof LivingEntity attacker) {
            ToolStack tool = TinkerToolUtil.getToolInHand(attacker);
            if (!TinkerToolUtil.isNotBrokenOrNull(tool)) {
                return;
            }

            tool.getModifierList().forEach((e) -> {
                KillingHook hook = e.getHook(InitHook.KILLING_HOOK);
                hook.onKillLivingTarget(tool, event, attacker, event.getEntity(), e.getLevel());
            });
        }

    }
}
