package com.mrbysco.enchantmentbroker.datagen.client;

import com.mrbysco.enchantmentbroker.EnchantmentBroker;
import com.mrbysco.enchantmentbroker.registry.ModRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
	public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, EnchantmentBroker.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		withExistingParent(ModRegistry.BROKER_SPAWN_EGG.getId().getPath(), ResourceLocation.withDefaultNamespace("item/template_spawn_egg"));
	}
}
