package com.github.heiwenziduo.tinker_warrior_song.data;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class TWSDamageType {
    /// 伤害类型: <span style="color: f4f79e;">纯粹</span>
    public static final ResourceKey<DamageType> PURE = create("pure");

    /** Creates a new damage type tag */
    private static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(TinkerWarriorSong.ModId, name));
    }

    /** Creates a new damage source using a custom type */
    public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type, @Nullable Entity direct, @Nullable Entity causing) {
        return new DamageSource(access.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), direct, causing);
    }

    /** Creates a new damage source using a custom typ with a single entity */
    public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type, @Nullable Entity entity) {
        return source(access, type, entity, entity);
    }

    /** Creates a new damage source using a custom type with no entity */
    public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type) {
        return source(access, type, null, null);
    }
}
