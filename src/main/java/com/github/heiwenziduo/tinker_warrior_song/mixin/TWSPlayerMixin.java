package com.github.heiwenziduo.tinker_warrior_song.mixin;

import com.github.heiwenziduo.tinker_warrior_song.t_construct.hooks.DamageRedirectHook;
import com.github.heiwenziduo.tinker_warrior_song.t_construct.hooks.TWSHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

@Mixin(value = Player.class)
public abstract class TWSPlayerMixin extends LivingEntity {
    protected TWSPlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @ModifyArg(method = "attack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"),
            index = 0)
    public DamageSource redirectDamageSource(DamageSource pSource) {
        /// ? 只有 tconstruct 的工具不会正常触发, 但tc自己代码里没有mixin或取消事件的 why?
        //System.out.println("redirectDamageSource: MixinStart");

//        Entity target = pSource.getEntity();
//        if (target instanceof LivingEntity living) {
//            DamageSource finalSource;
//            ToolStack tinkerTool = Modifier.getHeldTool(this, InteractionHand.MAIN_HAND);
//            if (tinkerTool != null) {
//                for (ModifierEntry entry : tinkerTool.getModifierList()) {
//                    DamageRedirectHook hook = entry.getHook(TWSHooks.DAMAGE_REDIRECT_HOOK);
//                    finalSource = hook.redirectDamageSource(tinkerTool, this, living, entry.getLevel(), pSource);
//                    if (finalSource != null) {
//                        System.out.println("redirectDamageSource: " + finalSource);
//                        return finalSource;
//                    }
//                }
//            }
//        }


        return pSource;
    }
}
