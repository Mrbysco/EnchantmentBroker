package com.mrbysco.enchantmentbroker.data;

import com.mrbysco.enchantmentbroker.EnchantmentBroker;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BrokerData extends SavedData {
	private static final String DATA_NAME = EnchantmentBroker.MOD_ID + "_data";
	private final Map<UUID, List<StoredEnchantment>> enchantMap = new HashMap<>();

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

	public static BrokerData load(CompoundTag tag, Provider provider) {
		BrokerData data = new BrokerData();
		for (String key : tag.getAllKeys()) {
			if (key.startsWith("Enchantments_")) {
				CompoundTag enchantTag = tag.getCompound(key);
				UUID uuid = enchantTag.getUUID("UUID");
				List<StoredEnchantment> enchantments = new java.util.ArrayList<>();
				for (String enchantKey : enchantTag.getAllKeys()) {
					if (enchantKey.startsWith("Enchantment")) {
						CompoundTag enchantmentTag = enchantTag.getCompound(enchantKey);
						enchantments.add(StoredEnchantment.fromTag(enchantmentTag, provider));
					}
				}
				data.enchantMap.put(uuid, enchantments);
			}
		}

		return data;
	}

	@Override
	public CompoundTag save(CompoundTag tag, Provider registries) {
		for (Map.Entry<UUID, List<StoredEnchantment>> entry : enchantMap.entrySet()) {
			UUID uuid = entry.getKey();
			List<StoredEnchantment> enchantments = entry.getValue();
			CompoundTag enchantTag = new CompoundTag();
			enchantTag.putUUID("UUID", uuid);
			for (int i = 0; i < enchantments.size(); i++) {
				enchantTag.put("Enchantment" + i, enchantments.get(i).toTag());
			}
			tag.put("Enchantments_" + uuid.toString(), enchantTag);
		}

		return tag;
	}

	public static BrokerData get(Level level) {
		if (!(level instanceof ServerLevel)) {
			throw new RuntimeException("Attempted to get the data from a client world. This is wrong.");
		}
		ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);

		DimensionDataStorage storage = overworld.getDataStorage();
		return storage.computeIfAbsent(new SavedData.Factory<>(BrokerData::new, BrokerData::load, null), DATA_NAME);
	}
}
