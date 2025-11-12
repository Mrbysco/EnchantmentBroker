package com.mrbysco.enchantmentbroker.registry;

import com.mrbysco.enchantmentbroker.EnchantmentBroker;
import com.mrbysco.enchantmentbroker.entity.Broker;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRegistry {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnchantmentBroker.MOD_ID);
	public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(EnchantmentBroker.MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, EnchantmentBroker.MOD_ID);

	public static final Supplier<EntityType<Broker>> BROKER = ENTITIES.registerEntityType("broker",
			Broker::new,
			MobCategory.CREATURE,
			builder -> builder
					.sized(0.6F, 1.95F)
					.eyeHeight(1.62F)
					.clientTrackingRange(10)
	);

	public static final DeferredItem<SpawnEggItem> BROKER_SPAWN_EGG = ITEMS.registerItem("broker_spawn_egg", SpawnEggItem::new, (properties) ->
			properties.spawnEgg(ModRegistry.BROKER.get()));

	public static final DeferredHolder<SoundEvent, SoundEvent> BROKER_TRADE = createSoundEvent("broker.trade");
	public static final DeferredHolder<SoundEvent, SoundEvent> BROKER_AMBIENT = createSoundEvent("broker.ambient");
	public static final DeferredHolder<SoundEvent, SoundEvent> BROKER_HURT = createSoundEvent("broker.hurt");
	public static final DeferredHolder<SoundEvent, SoundEvent> BROKER_DEATH = createSoundEvent("broker.death");
	public static final DeferredHolder<SoundEvent, SoundEvent> BROKER_YES = createSoundEvent("broker.yes");
	public static final DeferredHolder<SoundEvent, SoundEvent> BROKER_NO = createSoundEvent("broker.no");

	private static DeferredHolder<SoundEvent, SoundEvent> createSoundEvent(String name) {
		return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(EnchantmentBroker.modLoc(name)));
	}
}
