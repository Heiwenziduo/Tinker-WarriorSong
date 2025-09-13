package com.github.heiwenziduo.tinker_warrior_song.initializer;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;

public class InitModifier {
    private static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TinkerWarriorSong.ModId);
    public static void register(IEventBus eventBus) {
        MODIFIERS.register(eventBus);
    }

}