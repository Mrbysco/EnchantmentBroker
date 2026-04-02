package com.mrbysco.enchantmentbroker.data;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.enchantmentbroker.EnchantmentBroker;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BrokerData extends SavedData {
	private static final Identifier DATA_NAME = EnchantmentBroker.modLoc("broker_data");


	public static final Codec<BrokerData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
					Codec.unboundedMap(UUIDUtil.STRING_CODEC, StoredEnchantment.CODEC.listOf())
							.fieldOf("stored_enchantments").forGetter(data -> data.enchantMap))
			.apply(inst, BrokerData::new));

	private final Map<UUID, List<StoredEnchantment>> enchantMap;

	public BrokerData() {
		this(Maps.newHashMap());
	}

	public BrokerData(Map<UUID, List<StoredEnchantment>> infoMap) {
		this.enchantMap = Maps.newHashMap(infoMap);
	}

	public void addEnchantment(UUID uuid, Holder<Enchantment> enchantmentHolder, int level) {
		enchantMap.computeIfAbsent(uuid, id -> new ArrayList<>())
				.add(new StoredEnchantment(enchantmentHolder, level));
	}

	public boolean clearEnchantments(UUID uuid) {
		if (enchantMap.containsKey(uuid)) {
			enchantMap.remove(uuid);
			return true;
		} else {
			return false;
		}
	}

	public List<StoredEnchantment> getEnchantments(UUID uuid) {
		return enchantMap.getOrDefault(uuid, new ArrayList<>());
	}

	public List<String> getUUIDs() {
		List<String> uuids = new ArrayList<>();
		for (UUID uuid : enchantMap.keySet()) {
			uuids.add(uuid.toString());
		}
		return uuids;
	}

	public boolean removeStoredEnchantment(UUID uuid, Holder<Enchantment> enchantmentHolder, int level) {
		List<StoredEnchantment> enchantments = enchantMap.get(uuid);
		if (enchantments != null) {
			return enchantments.removeIf(enchantment ->
					enchantment.enchantmentHolder().equals(enchantmentHolder) && enchantment.level() == level);
		}
		return false;
	}

	public static SavedDataType<BrokerData> type() {
		return new SavedDataType<>(DATA_NAME, BrokerData::new, CODEC, null);
	}

	public static BrokerData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);

		assert overworld != null;
		SavedDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(type());
	}
}
