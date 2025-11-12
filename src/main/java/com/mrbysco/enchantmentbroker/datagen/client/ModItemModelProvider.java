package com.mrbysco.enchantmentbroker.datagen.client;

import com.mrbysco.enchantmentbroker.EnchantmentBroker;
import com.mrbysco.enchantmentbroker.registry.ModRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class ModItemModelProvider extends ModelProvider {
	public ModItemModelProvider(PackOutput output) {
		super(output, EnchantmentBroker.MOD_ID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		itemModels.generateFlatItem(ModRegistry.BROKER_SPAWN_EGG.get(), ModelTemplates.FLAT_ITEM);
	}
}
