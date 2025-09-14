package com.github.heiwenziduo.tinker_warrior_song.api;

import com.github.heiwenziduo.tinker_warrior_song.api.mixin.LivingEntityMixinAPI;
import net.minecraft.world.entity.LivingEntity;

public class ManagerAbbr {
    public static int getTimeLock(LivingEntity living) {
        return ((LivingEntityMixinAPI) living).TWS$getTimeLockManager().getTimeLock();
    }

    public static void setTimeLock(LivingEntity living, int tick) {
        ((LivingEntityMixinAPI) living).TWS$getTimeLockManager().setTimeLock(tick);
    }
}
