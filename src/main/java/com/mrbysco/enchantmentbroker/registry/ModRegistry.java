package com.mrbysco.enchantmentbroker.registry;

import com.mrbysco.enchantmentbroker.EnchantmentBroker;
import com.mrbysco.enchantmentbroker.entity.Broker;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRegistry {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnchantmentBroker.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, EnchantmentBroker.MOD_ID);
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, EnchantmentBroker.MOD_ID);

	public static final Supplier<EntityType<Broker>> BROKER = ENTITIES.register("broker",
			() -> EntityType.Builder.<Broker>of(Broker::new, MobCategory.CREATURE)
					.sized(0.6F, 1.95F)
					.eyeHeight(1.62F)
					.clientTrackingRange(10)
					.build("broker")
	);

	public static final DeferredItem<DeferredSpawnEggItem> BROKER_SPAWN_EGG = ITEMS.registerItem("broker_spawn_egg", (properties) ->
			new DeferredSpawnEggItem(ModRegistry.BROKER, 4547222, 15377456, properties));

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
