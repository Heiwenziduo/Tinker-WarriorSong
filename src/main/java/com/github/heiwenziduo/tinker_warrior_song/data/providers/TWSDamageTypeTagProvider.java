package com.github.heiwenziduo.tinker_warrior_song.data.providers;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.github.heiwenziduo.tinker_warrior_song.data.TWSDamageType.PURE;
import static net.minecraft.tags.DamageTypeTags.*;


/// {@link net.minecraft.data.tags.DamageTypeTagsProvider}
/// {@link slimeknights.tconstruct.common.data.tags.DamageTypeTagProvider}
public class TWSDamageTypeTagProvider extends DamageTypeTagsProvider {

    public TWSDamageTypeTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookup, TinkerWarriorSong.ModId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(BYPASSES_SHIELD).add(PURE);
        tag(BYPASSES_EFFECTS).add(PURE);
        tag(BYPASSES_ARMOR).add(PURE);
        tag(AVOIDS_GUARDIAN_THORNS).add(PURE);
    }
}
