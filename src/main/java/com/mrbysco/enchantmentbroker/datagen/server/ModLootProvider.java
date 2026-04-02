package com.mrbysco.enchantmentbroker.datagen.server;

import com.mrbysco.enchantmentbroker.registry.ModRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModLootProvider extends LootTableProvider {
	public ModLootProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, Set.of(), List.of(
				new SubProviderEntry(HashireSoriYo::new, LootContextParamSets.ENTITY)
		), lookupProvider);
	}

	private static class HashireSoriYo extends EntityLootSubProvider {
		protected HashireSoriYo(HolderLookup.Provider provider) {
			super(FeatureFlags.REGISTRY.allFlags(), provider);
		}

		@Override
		public void generate() {
			this.add(ModRegistry.BROKER.get(), LootTable.lootTable());
		}

		@Override
		protected Stream<EntityType<?>> getKnownEntityTypes() {
			return ModRegistry.ENTITIES.getEntries().stream().map(Supplier::get);
		}
	}
}
