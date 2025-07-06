package com.mrbysco.enchantmentbroker.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrbysco.enchantmentbroker.EnchantmentBroker;
import com.mrbysco.enchantmentbroker.data.BrokerData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BrokerCommands {
	public static void initializeCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(EnchantmentBroker.MOD_ID);
		root.requires((commandSource) -> commandSource.hasPermission(2))
				.then(Commands.literal("storedEnchants")
						.then(Commands.argument("uuid", UuidArgument.uuid())
								.suggests(
										(source, builder) ->
												SharedSuggestionProvider.suggest(getKnownUUIDS(source.getSource().getServer()), builder)
								)
								.then(Commands.literal("clear").executes(BrokerCommands::clearEnchantments))
								.then(Commands.literal("add")
										.then(Commands.argument("enchantment", ResourceArgument.resource(context, Registries.ENCHANTMENT))
												.then(Commands.argument("level", IntegerArgumentType.integer())
														.executes(BrokerCommands::addEnchantment)))
								)
								.then(Commands.literal("remove")
										.then(Commands.argument("enchantment", ResourceArgument.resource(context, Registries.ENCHANTMENT)))
										.then(Commands.argument("level", IntegerArgumentType.integer())
												.executes(BrokerCommands::removeEnchantment)))
								.then(Commands.literal("list")
										.executes(BrokerCommands::listEnchantments))
						)
				);
		dispatcher.register(root);
	}

	private static Iterable<String> getKnownUUIDS(MinecraftServer server) {
		Set<String> set = new HashSet<>();

		BrokerData brokerData = BrokerData.get(server.getLevel(Level.OVERWORLD));
		if (brokerData != null) {
			set.addAll(brokerData.getUUIDs());
		}
		for (ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
			UUID uuid = serverPlayer.getGameProfile().getId();
			set.add(uuid.toString());
		}

		return set;
	}

	private static int listEnchantments(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		var uuid = UuidArgument.getUuid(ctx, "uuid");
		var enchantments = BrokerData.get(source.getLevel()).getEnchantments(uuid);
		if (enchantments.isEmpty()) {
			source.sendFailure(Component.translatable("command.enchantmentbroker.list.empty", uuid.toString()));
		} else {
			source.sendSuccess(() -> Component.translatable("command.enchantmentbroker.list.message", uuid.toString()), true);
			enchantments.forEach(enchantment -> source.sendSuccess(() ->
					Component.translatable("command.enchantmentbroker.list.message.enchant",
							enchantment.enchantmentHolder().value().description().getString(), enchantment.level()
					), false)
			);
		}
		return 0;
	}

	private static int clearEnchantments(CommandContext<CommandSourceStack> ctx) {
		CommandSourceStack source = ctx.getSource();
		var uuid = UuidArgument.getUuid(ctx, "uuid");
		BrokerData data = BrokerData.get(source.getLevel());
		boolean removed = data.clearEnchantments(uuid);
		if (removed) {
			source.sendSuccess(() -> Component.translatable("command.enchantmentbroker.clear.message", uuid.toString()).withStyle(ChatFormatting.GOLD), true);
			data.setDirty();
		} else {
			source.sendFailure(Component.translatable("command.enchantmentbroker.clear.empty", uuid.toString()));
		}
		return 0;
	}

	private static int addEnchantment(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSourceStack source = ctx.getSource();
		var uuid = UuidArgument.getUuid(ctx, "uuid");
		Holder.Reference<Enchantment> enchantment = ResourceArgument.getEnchantment(ctx, "enchantment");
		int level = IntegerArgumentType.getInteger(ctx, "level");

		BrokerData data = BrokerData.get(source.getLevel());
		data.addEnchantment(uuid, enchantment, level);
		data.setDirty();
		source.sendSuccess(() -> Component.translatable("command.enchantmentbroker.add.message", enchantment.value().description().getString(), level, uuid.toString()), true);
		return 0;
	}

	private static int removeEnchantment(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		CommandSourceStack source = ctx.getSource();
		var uuid = UuidArgument.getUuid(ctx, "uuid");
		var enchantment = ResourceArgument.getEnchantment(ctx, "enchantment");
		int level = IntegerArgumentType.getInteger(ctx, "level");

		BrokerData data = BrokerData.get(source.getLevel());
		boolean removed = data.removeStoredEnchantment(uuid, enchantment, level);
		if (removed) {
			source.sendSuccess(() -> Component.translatable("command.enchantmentbroker.remove.message",
					enchantment.value().description().getString(), level, uuid.toString()
			), true);
			data.setDirty();
		} else {
			source.sendFailure(Component.translatable("command.enchantmentbroker.remove.failed", enchantment.value().description().getString(), level, uuid.toString()));
		}
		return removed ? 1 : 0;
	}
}
