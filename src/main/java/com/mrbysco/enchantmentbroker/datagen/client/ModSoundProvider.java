package com.mrbysco.enchantmentbroker.datagen.client;

import com.mrbysco.enchantmentbroker.EnchantmentBroker;
import com.mrbysco.enchantmentbroker.registry.ModRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class ModSoundProvider extends SoundDefinitionsProvider {

	public ModSoundProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
		super(packOutput, EnchantmentBroker.MOD_ID, existingFileHelper);
	}

	@Override
	public void registerSounds() {
		this.add(ModRegistry.BROKER_TRADE, definition()
				.subtitle(modSubtitle(ModRegistry.BROKER_TRADE.getId()))
				.with(
						sound(mcLoc("mob/wandering_trader/haggle1")),
						sound(mcLoc("mob/wandering_trader/haggle2")),
						sound(mcLoc("mob/wandering_trader/haggle3"))
				));
		this.add(ModRegistry.BROKER_AMBIENT, definition()
				.subtitle(modSubtitle(ModRegistry.BROKER_AMBIENT.getId()))
				.with(
						sound(mcLoc("mob/wandering_trader/idle1")),
						sound(mcLoc("mob/wandering_trader/idle2")),
						sound(mcLoc("mob/wandering_trader/idle3")),
						sound(mcLoc("mob/wandering_trader/idle4")),
						sound(mcLoc("mob/wandering_trader/idle5"))
				));
		this.add(ModRegistry.BROKER_HURT, definition()
				.subtitle(modSubtitle(ModRegistry.BROKER_HURT.getId()))
				.with(
						sound(mcLoc("mob/vindication_illager/hurt1")),
						sound(mcLoc("mob/vindication_illager/hurt2")),
						sound(mcLoc("mob/vindication_illager/hurt3"))
				));
		this.add(ModRegistry.BROKER_DEATH, definition()
				.subtitle(modSubtitle(ModRegistry.BROKER_DEATH.getId()))
				.with(
						sound(mcLoc("mob/wandering_trader/death"))
				));
		this.add(ModRegistry.BROKER_YES, definition()
				.subtitle(modSubtitle(ModRegistry.BROKER_YES.getId()))
				.with(
						sound(mcLoc("mob/wandering_trader/yes1")),
						sound(mcLoc("mob/wandering_trader/yes2")),
						sound(mcLoc("mob/wandering_trader/yes3")),
						sound(mcLoc("mob/wandering_trader/yes4"))
				));
		this.add(ModRegistry.BROKER_NO, definition()
				.subtitle(modSubtitle(ModRegistry.BROKER_NO.getId()))
				.with(
						sound(mcLoc("mob/wandering_trader/no1")),
						sound(mcLoc("mob/wandering_trader/no2")),
						sound(mcLoc("mob/wandering_trader/no3")),
						sound(mcLoc("mob/wandering_trader/no4")),
						sound(mcLoc("mob/wandering_trader/no5"))
				));
	}

	public String modSubtitle(ResourceLocation id) {
		return EnchantmentBroker.MOD_ID + ".subtitle." + id.getPath();
	}

	public ResourceLocation modLoc(String name) {
		return EnchantmentBroker.modLoc(name);
	}

	public ResourceLocation mcLoc(String name) {
		return ResourceLocation.fromNamespaceAndPath("minecraft", name);
	}
}
