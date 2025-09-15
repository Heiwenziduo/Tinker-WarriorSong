package com.github.heiwenziduo.tinker_warrior_song.mixin;

import com.github.heiwenziduo.tinker_warrior_song.api.manager.TimeLockManager;
import com.github.heiwenziduo.tinker_warrior_song.api.mixin.LivingEntityMixinAPI;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.heiwenziduo.tinker_warrior_song.data.TWSDamageType.PURE;

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
    @Shadow
    public abstract float getAbsorptionAmount();
    @Shadow
    public abstract void setAbsorptionAmount(float pAbsorptionAmount);
    @Shadow
    public abstract CombatTracker getCombatTracker();
    @Shadow
    public abstract float getHealth();
    @Shadow
    public abstract void setHealth(float pAbsorptionAmount);

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

    /// 被时间锁定的活物不能行动
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
                if (hurtTime > 0) {
                    --hurtTime;
                }
                // ServerPlayer 的 invulnerable-- 在 tick() 中, 暂时不管它
//                if (invulnerableTime > 0 && !(this instanceof ServerPlayer)) {
//                    --invulnerableTime;
//                }
                if (invulnerableTime > 0) {
                    --invulnerableTime;
                }
                if (isDeadOrDying() && level().shouldTickDeath(this)) {
                    tickDeath();
                }
                if (lastHurtByPlayerTime > 0) {
                    --lastHurtByPlayerTime;
                } else {
                    lastHurtByPlayer = null;
                }
                if (lastHurtMob != null && !lastHurtMob.isAlive()) {
                    lastHurtMob = null;
                }
                if (lastHurtByMob != null) {
                    if (!lastHurtByMob.isAlive()) {
                        setLastHurtByMob((LivingEntity)null);
                    } else if (tickCount - lastHurtByMobTimestamp > 100) {
                        setLastHurtByMob((LivingEntity)null);
                    }
                }
            }


            // 中断
            ci.cancel();
        }
    }

    /// 纯粹伤害不会被减免, 并发出独有事件(待实现) <br/>
    /// 欺骗全世界的类型转换 [(LivingEntity) (Object)](https://www.reddit.com/r/fabricmc/comments/nw3rs8/how_can_i_access_the_this_in_a_mixin_for_a_class/?tl=zh-hans)
    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    public void onTakePureDamage(DamageSource pDamageSource, float pDamageAmount, CallbackInfo ci) {
        if (pDamageSource != null && pDamageSource.is(PURE) && !isInvulnerableTo(pDamageSource)) {
            float oldDamage = pDamageAmount;
            pDamageAmount = Math.max(net.minecraftforge.common.ForgeHooks.onLivingHurt((LivingEntity) (Object) this, pDamageSource, pDamageAmount), pDamageAmount);

            float f1 = Math.max(pDamageAmount - getAbsorptionAmount(), 0.0F);
            setAbsorptionAmount(getAbsorptionAmount() - (pDamageAmount - f1));
            float f = pDamageAmount - f1;
            if (f > 0.0F && f < 3.4028235E37F) {
                Entity entity = pDamageSource.getEntity();
                if (entity instanceof ServerPlayer) {
                    ServerPlayer serverplayer = (ServerPlayer)entity;
                    serverplayer.awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(f * 10.0F));
                }
            }

            f1 = Math.max(net.minecraftforge.common.ForgeHooks.onLivingDamage((LivingEntity) (Object) this, pDamageSource, f1), f1);
            // todo: pure damage event

            if (f1 != 0.0F) {
                getCombatTracker().recordDamage(pDamageSource, f1);
                setHealth(getHealth() - f1);
                setAbsorptionAmount(getAbsorptionAmount() - f1);
                gameEvent(GameEvent.ENTITY_DAMAGE);
            }

            System.out.println("pureDamage: " + f1);

            ci.cancel();
        }
    }
}
