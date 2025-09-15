package com.github.heiwenziduo.tinker_warrior_song.data.providers;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

import static com.github.heiwenziduo.tinker_warrior_song.data.TWSDamageType.PURE;

/// {@link slimeknights.tconstruct.common.data.DamageTypeProvider}
public class TWSDamageTypeProvider implements RegistrySetBuilder.RegistryBootstrap<DamageType> {

    /** Registers this provider with the registry set builder */
    public static void register(RegistrySetBuilder builder) {
        builder.add(Registries.DAMAGE_TYPE, new TWSDamageTypeProvider());
    }

    @Override
    public void run(BootstapContext<DamageType> context) {
        context.register(PURE, new DamageType(TinkerWarriorSong.ModId + ".pure", DamageScaling.NEVER, 0.1f));

    }
}
