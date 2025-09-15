package com.github.heiwenziduo.tinker_warrior_song.data;

import com.github.heiwenziduo.tinker_warrior_song.TinkerWarriorSong;
import com.github.heiwenziduo.tinker_warrior_song.data.providers.TWSDamageTypeProvider;
import com.github.heiwenziduo.tinker_warrior_song.data.providers.TWSDamageTypeTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {
    /// {@link slimeknights.tconstruct.TConstruct#gatherData(GatherDataEvent)}
    @SubscribeEvent
    static void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        boolean server = event.includeServer();
        boolean client = event.includeClient();

        RegistrySetBuilder registrySetBuilder = new RegistrySetBuilder();
        TWSDamageTypeProvider.register(registrySetBuilder);

        DatapackBuiltinEntriesProvider datapackRegistryProvider = new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, registrySetBuilder, Set.of(TinkerWarriorSong.ModId));
        generator.addProvider(server, datapackRegistryProvider);

        generator.addProvider(server, new TWSDamageTypeTagProvider(packOutput, datapackRegistryProvider.getRegistryProvider(), existingFileHelper));
    }
}
