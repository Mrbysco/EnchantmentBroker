package com.mrbysco.enchantmentbroker.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

public record StoredEnchantment(Holder<Enchantment> enchantmentHolder, int level) {
	public static final Codec<StoredEnchantment> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Enchantment.CODEC.fieldOf("enchantment").forGetter(StoredEnchantment::enchantmentHolder),
					Codec.INT.fieldOf("level").forGetter(StoredEnchantment::level)
			).apply(instance, StoredEnchantment::new)
	);

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
}
