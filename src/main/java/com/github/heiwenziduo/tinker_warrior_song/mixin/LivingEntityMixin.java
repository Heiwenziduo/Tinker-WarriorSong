package com.github.heiwenziduo.tinker_warrior_song.mixin;

import com.github.heiwenziduo.tinker_warrior_song.api.manager.TimeLockManager;
import com.github.heiwenziduo.tinker_warrior_song.api.mixin.LivingEntityMixinAPI;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityMixinAPI {
    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow
    public int hurtTime;
    @Shadow
    protected int lastHurtByPlayerTime;
    @Shadow
    protected Player lastHurtByPlayer;
    @Shadow
    private LivingEntity lastHurtMob;
    @Shadow
    private LivingEntity lastHurtByMob;
    @Shadow
    private int lastHurtByMobTimestamp;

    @Shadow
    public abstract boolean isDeadOrDying();
    @Shadow
    protected abstract void tickDeath();
    @Shadow
    public abstract void setLastHurtByMob(@Nullable LivingEntity pLivingEntity);

    @Unique
    protected TimeLockManager TWS$timeLockManager = new TimeLockManager();

    @Unique
    @Override
    public TimeLockManager TWS$getTimeLockManager() {
        return TWS$timeLockManager;
    }

    @Inject(method = "baseTick", at = @At("HEAD"), cancellable = true)
    public void timeLockBaseTick(CallbackInfo ci) {

    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void timeLockTick(CallbackInfo ci) {
        // 似乎也会插入所有子类的开头 ?
        if (TWS$getTimeLockManager().isTimeLocked()) {
            TWS$timeLockManager.timeLockDecrement();

            // tinker after-melee hook do Only in server side, sad
            if (tickCount % 10 == 0) {
                for (int i = 0; i < 5; i++) {
                    level().addParticle(
                            ParticleTypes.DRAGON_BREATH,
                            getX()+ random.nextDouble(),
                            getY()+ random.nextDouble(),
                            getZ()+ random.nextDouble(),
                            0,
                            .5,
                            0
                    );
                }

                for (int i = 0; i < 5; i++) {
                    ((ServerLevel) level()).sendParticles(
                            ParticleTypes.DRAGON_BREATH,
                            getX()+ random.nextDouble(),
                            getY()+ random.nextDouble(),
                            getZ()+ random.nextDouble(),
                            1,
                            0,
                            .5,
                            0,
                            1
                    );
                }
            }

            // if is locked, apply some base tick logic, like reducing invulnerable time.
            // from LivingEntity#baseTick
            {
                if (this.hurtTime > 0) {
                    --this.hurtTime;
                }
                // ServerPlayer 的 invulnerable-- 在 tick() 中, 暂时不管它
//                if (this.invulnerableTime > 0 && !(this instanceof ServerPlayer)) {
//                    --this.invulnerableTime;
//                }
                if (this.invulnerableTime > 0) {
                    --this.invulnerableTime;
                }
                if (this.isDeadOrDying() && this.level().shouldTickDeath(this)) {
                    this.tickDeath();
                }
                if (this.lastHurtByPlayerTime > 0) {
                    --this.lastHurtByPlayerTime;
                } else {
                    this.lastHurtByPlayer = null;
                }
                if (this.lastHurtMob != null && !this.lastHurtMob.isAlive()) {
                    this.lastHurtMob = null;
                }
                if (this.lastHurtByMob != null) {
                    if (!this.lastHurtByMob.isAlive()) {
                        this.setLastHurtByMob((LivingEntity)null);
                    } else if (this.tickCount - this.lastHurtByMobTimestamp > 100) {
                        this.setLastHurtByMob((LivingEntity)null);
                    }
                }
            }


            // 中断
            ci.cancel();
        }
    }
}
