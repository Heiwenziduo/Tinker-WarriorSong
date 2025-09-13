package com.github.heiwenziduo.tinker_warrior_song.initializer;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import com.github.heiwenziduo.tinker_warrior_song.t_construct.modifiers.Millennium;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;

public class InitModifier {
    private static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TinkerWarriorSong.ModId);
    public static void register(IEventBus eventBus) {
        MODIFIERS.register(eventBus);
    }

    public static final StaticModifier<Millennium> Millennium = MODIFIERS.register("millennium", Millennium::new);

}