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
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class ModDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		CompletableFuture<Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new ModLootProvider(packOutput, lookupProvider));

		generator.addProvider(true, new ModLanguageProvider(packOutput));
		generator.addProvider(true, new ModSoundProvider(packOutput));
		generator.addProvider(true, new ModItemModelProvider(packOutput));

	}
}
