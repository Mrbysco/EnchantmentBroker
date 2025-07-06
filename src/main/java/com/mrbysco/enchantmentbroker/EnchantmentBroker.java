package com.mrbysco.enchantmentbroker;

import com.mojang.logging.LogUtils;
import com.mrbysco.enchantmentbroker.client.ClientHandler;
import com.mrbysco.enchantmentbroker.commands.BrokerCommands;
import com.mrbysco.enchantmentbroker.entity.Broker;
import com.mrbysco.enchantmentbroker.registry.ModRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.slf4j.Logger;

@Mod(EnchantmentBroker.MOD_ID)
public class EnchantmentBroker {
	public static final String MOD_ID = "enchantmentbroker";
	public static final Logger LOGGER = LogUtils.getLogger();

	public EnchantmentBroker(IEventBus eventBus, Dist dist, ModContainer container) {
		ModRegistry.ITEMS.register(eventBus);
		ModRegistry.ENTITIES.register(eventBus);
		ModRegistry.SOUND_EVENTS.register(eventBus);

		eventBus.addListener(this::addTabContents);
		eventBus.addListener(this::registerSpawnPlacement);
		eventBus.addListener(this::registerAttributes);
		NeoForge.EVENT_BUS.addListener(this::onCommandRegister);
		NeoForge.EVENT_BUS.addListener(this::onLivingDamage);

		if (dist.isClient()) {
			container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
			eventBus.addListener(ClientHandler::registerEntityRenderers);
		}
	}

	public static ResourceLocation modLoc(String name) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
	}

	private void addTabContents(final BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
			event.accept(ModRegistry.BROKER_SPAWN_EGG.get());
		}
	}

	private void registerSpawnPlacement(RegisterSpawnPlacementsEvent event) {
		event.register(ModRegistry.BROKER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Broker::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
	}

	private void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(ModRegistry.BROKER.get(), Broker.generateAttributes().build());
	}

	public void onCommandRegister(RegisterCommandsEvent event) {
		BrokerCommands.initializeCommands(event.getDispatcher(), event.getBuildContext());
	}

	/**
	 * Handles the conversion of a Wandering Trader to a Broker when the trader takes damage from an anvil.
	 */
	public void onLivingDamage(LivingDamageEvent.Post event) {
		DamageSource source = event.getSource();
		Entity entity = source.getDirectEntity();
		if (entity instanceof FallingBlockEntity fallingBlockEntity && fallingBlockEntity.getBlockState().is(BlockTags.ANVIL)) {
			LivingEntity livingEntity = event.getEntity();
			if (livingEntity instanceof WanderingTrader wanderingTrader && wanderingTrader.getType() == EntityType.WANDERING_TRADER) {
				Level level = livingEntity.level();
				Broker broker = ModRegistry.BROKER.get().create(level);
				if (broker != null) {
					broker.setPos(wanderingTrader.getX(), wanderingTrader.getY(), wanderingTrader.getZ());
					broker.setYRot(wanderingTrader.getYRot());
					broker.setXRot(wanderingTrader.getXRot());
					wanderingTrader.discard();
					level.addFreshEntity(broker);
				}
			}
		}
	}
}
