package com.mrbysco.enchantmentbroker.data;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public record StoredEnchantment(Holder<Enchantment> enchantmentHolder, int level) {
	public StoredEnchantment(Holder<Enchantment> enchantmentHolder) {
		this(enchantmentHolder, 1);
	}

	public StoredEnchantment(Holder<Enchantment> enchantmentHolder, int level) {
		if (level < 0 || level > 255) {
			throw new IllegalArgumentException("Enchantment " + enchantmentHolder.getKey() + " has invalid level " + level);
		}
		this.enchantmentHolder = enchantmentHolder;
		this.level = level;
	}

	public CompoundTag toTag() {
		ResourceKey<Enchantment> enchantmentKey = enchantmentHolder.unwrapKey()
				.orElseThrow(() -> new IllegalArgumentException("Enchantment holder does not have a valid key: " + enchantmentHolder));
		CompoundTag tag = new CompoundTag();
		tag.putString("Enchantment", enchantmentKey.location().toString());
		tag.putInt("Level", level);
		return tag;
	}

	public static StoredEnchantment fromTag(CompoundTag tag, Provider provider) {
		RegistryLookup<Enchantment> enchantmentLookup = provider.lookupOrThrow(Registries.ENCHANTMENT);

		if (!tag.contains("Enchantment", 8)) {
			throw new IllegalArgumentException("Tag does not contain a valid enchantment key: " + tag);
		}
		if (!tag.contains("Level", 3)) {
			throw new IllegalArgumentException("Tag does not contain a valid level: " + tag);
		}

		ResourceLocation enchantmentLocation = ResourceLocation.tryParse(tag.getString("Enchantment"));
		if (enchantmentLocation == null) {
			throw new IllegalArgumentException("Invalid enchantment location: " + tag.getString("Enchantment"));
		}
		ResourceKey<Enchantment> enchantmentKey = ResourceKey.create(Registries.ENCHANTMENT, enchantmentLocation);
		int level = tag.getInt("Level");
		return new StoredEnchantment(enchantmentLookup.getOrThrow(enchantmentKey), level);
	}
}
