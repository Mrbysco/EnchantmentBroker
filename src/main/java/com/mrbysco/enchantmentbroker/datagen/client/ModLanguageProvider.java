package com.mrbysco.enchantmentbroker.datagen.client;

import com.mrbysco.enchantmentbroker.EnchantmentBroker;
import com.mrbysco.enchantmentbroker.registry.ModRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.function.Supplier;

public class ModLanguageProvider extends LanguageProvider {
	public ModLanguageProvider(PackOutput packOutput) {
		super(packOutput, EnchantmentBroker.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		this.addEntityType(ModRegistry.BROKER, "The Refundler");
		this.addItem(ModRegistry.BROKER_SPAWN_EGG, "Enchantment Broker Spawn Egg");

		this.add("enchantmentbroker.broker.empty", "Enchantment Broker has no trades available!");
		this.add("enchantmentbroker.broker.hint", "Right-click the broker with your enchanted item to retrieve enchantments");
		this.add("enchantmentbroker.broker.accepted", "Enchantment Broker has accepted your enchantments!");

		this.addSubtitle(ModRegistry.BROKER_AMBIENT, "Enchantment Broker mumbles");
		this.addSubtitle(ModRegistry.BROKER_TRADE, "Enchantment Broker trades");
		this.addSubtitle(ModRegistry.BROKER_HURT, "Enchantment Broker hurts");
		this.addSubtitle(ModRegistry.BROKER_DEATH, "Enchantment Broker dies");
		this.addSubtitle(ModRegistry.BROKER_YES, "Enchantment Broker agrees");
		this.addSubtitle(ModRegistry.BROKER_NO, "Enchantment Broker disagrees");

		this.add("command.enchantmentbroker.list.message", "List of enchantments for §e%s§r:");
		this.add("command.enchantmentbroker.list.message.enchant", "Enchantment: §e%s§r, Level §e%s§r");
		this.add("command.enchantmentbroker.list.empty", "No enchantments found for §e%s§r");
		this.add("command.enchantmentbroker.clear.message", "Cleared all stored enchantments for §e%s§r");
		this.add("command.enchantmentbroker.clear.empty", "No stored enchantments found for §e%s§r to clear");
		this.add("command.enchantmentbroker.add.message", "Added §e%s§r level §e%s§r to §e%s§r's stored enchantments");
		this.add("command.enchantmentbroker.remove.message", "Removed §e%s§r level §e%s§r from §e%s§r's stored enchantments");
		this.add("command.enchantmentbroker.remove.failed", "Failed to remove §e%s§r level §e%s§r from §e%s§r's stored enchantments, it may not exist");
	}

	/**
	 * Add a subtitle to a sound event
	 *
	 * @param sound The sound event
	 * @param text  The subtitle text
	 */
	public void addSubtitle(Supplier<SoundEvent> sound, String text) {
		this.addSubtitle(sound.get(), text);
	}

	/**
	 * Add a subtitle to a sound event
	 *
	 * @param sound The sound event registry object
	 * @param text  The subtitle text
	 */
	public void addSubtitle(SoundEvent sound, String text) {
		String path = EnchantmentBroker.MOD_ID + ".subtitle." + sound.location().getPath();
		this.add(path, text);
	}
}
