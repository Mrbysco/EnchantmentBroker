package com.mrbysco.enchantmentbroker.datagen;

import com.mrbysco.enchantmentbroker.datagen.client.ModItemModelProvider;
import com.mrbysco.enchantmentbroker.datagen.client.ModLanguageProvider;
import com.mrbysco.enchantmentbroker.datagen.client.ModSoundProvider;
import com.mrbysco.enchantmentbroker.datagen.server.ModLootProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("removal")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<Provider> lookupProvider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		if (event.includeServer()) {
			generator.addProvider(event.includeServer(), new ModLootProvider(packOutput, lookupProvider));
		}
		if (event.includeClient()) {
			generator.addProvider(event.includeServer(), new ModLanguageProvider(packOutput));
			generator.addProvider(event.includeServer(), new ModSoundProvider(packOutput, helper));
			generator.addProvider(event.includeServer(), new ModItemModelProvider(packOutput, helper));
		}
	}
}
