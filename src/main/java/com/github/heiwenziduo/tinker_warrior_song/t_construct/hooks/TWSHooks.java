package com.github.heiwenziduo.tinker_warrior_song.t_construct.hooks;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;

public class TWSHooks {
    public static final ModuleHook<KillingHook> KILLING_HOOK;
    public static final ModuleHook<DamageRedirectHook> DAMAGE_REDIRECT_HOOK;

    static {
        KILLING_HOOK = ModifierHooks.register(
                ResourceLocation.fromNamespaceAndPath(TinkerWarriorSong.ModId, "hook_killing"),
                KillingHook.class,
                KillingHook.AllMerge::new,
                new KillingHook() {}
        );

        DAMAGE_REDIRECT_HOOK = ModifierHooks.register(
                ResourceLocation.fromNamespaceAndPath(TinkerWarriorSong.ModId, "hook_living_hurt"),
                DamageRedirectHook.class,
                DamageRedirectHook.AllMerge::new,
                (tool, attacker, target, level, source) -> source
        );
    }
}
