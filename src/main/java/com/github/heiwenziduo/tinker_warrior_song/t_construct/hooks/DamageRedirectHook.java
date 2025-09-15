package com.github.heiwenziduo.tinker_warrior_song.t_construct.hooks;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface DamageRedirectHook {
    DamageSource redirectDamageSource(IToolStackView tool, LivingEntity attacker, LivingEntity target, int level, DamageSource source);

    record AllMerge(Collection<DamageRedirectHook> modules) implements DamageRedirectHook {
        @Override
        public DamageSource redirectDamageSource(IToolStackView tool, LivingEntity attacker, LivingEntity target, int level, DamageSource source) {
//            for(DamageRedirectHook module : this.modules) {
//                module.redirectDamageSource(tool, attacker, target, level, source);
//            }
            return source;
        }

    }
}
