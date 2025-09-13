package com.github.heiwenziduo.tinker_warrior_song.initializer;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import com.github.heiwenziduo.tinker_warrior_song.t_construct.hooks.KillingHook;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.module.ModuleHook;

public class InitHook {
    public static final ModuleHook<KillingHook> KILLING_HOOK;

    static {
        KILLING_HOOK = ModifierHooks.register(
                ResourceLocation.fromNamespaceAndPath(TinkerWarriorSong.ModId, "hook_killing"),
                KillingHook.class,
                KillingHook.AllMerge::new,
                new KillingHook() {}
        );
    }
}
