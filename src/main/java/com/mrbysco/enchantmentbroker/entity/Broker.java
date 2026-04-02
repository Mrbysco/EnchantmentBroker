package com.mrbysco.enchantmentbroker.entity;

import com.mrbysco.enchantmentbroker.data.BrokerData;
import com.mrbysco.enchantmentbroker.data.StoredEnchantment;
import com.mrbysco.enchantmentbroker.registry.ModRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Broker extends AbstractVillager {
	private static final int NUMBER_OF_TRADE_OFFERS = 3;

	public Broker(EntityType<? extends AbstractVillager> entityType, Level level) {
		super(entityType, level);
	}

	public static AttributeSupplier.Builder generateAttributes() {
		return createMobAttributes();
	}

	@Override
	public boolean showProgressBar() {
		return false;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Zombie.class, 8.0F, 0.5, 0.5));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Evoker.class, 12.0F, 0.5, 0.5));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Vindicator.class, 8.0F, 0.5, 0.5));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Vex.class, 8.0F, 0.5, 0.5));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Pillager.class, 15.0F, 0.5, 0.5));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Illusioner.class, 12.0F, 0.5, 0.5));
		this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Zoglin.class, 10.0F, 0.5, 0.5));
		this.goalSelector.addGoal(1, new PanicGoal(this, 0.5));
		this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
		this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.35));
		this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.35));
		this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
		this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (this.isAlive() && !this.isTrading() && !this.isBaby()) {
			if (hand == InteractionHand.MAIN_HAND) {
				player.awardStat(Stats.TALKED_TO_VILLAGER);
			}

			if (this.level() instanceof ServerLevel serverLevel) {
				// Set the trading player to the current player
				this.setTradingPlayer(player);

				ItemEnchantments enchantments = itemstack.getTagEnchantments();
				if (!enchantments.isEmpty()) {
					BrokerData data = BrokerData.get(serverLevel);
					for (Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
						data.addEnchantment(player.getGameProfile().id(), entry.getKey(), entry.getIntValue());
					}
					data.setDirty();
					itemstack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
					itemstack.set(DataComponents.REPAIR_COST, 0);

					this.updateTrades(serverLevel);
					this.setTradingPlayer(null);
					player.sendOverlayMessage(Component.translatable("enchantmentbroker.broker.accepted").withStyle(ChatFormatting.GREEN));
					playSound(SoundEvents.GRINDSTONE_USE, 1.0F, getRandom().nextFloat() * 0.1F + 0.9F);

					return InteractionResult.CONSUME;
				}

				this.updateTrades(serverLevel);
				if (this.getOffers().isEmpty()) {
					player.sendOverlayMessage(Component.translatable("enchantmentbroker.broker.empty").withStyle(ChatFormatting.RED));
					playSound(ModRegistry.BROKER_NO.get(), 1.0F, getRandom().nextFloat() * 0.1F + 0.9F);
					this.setTradingPlayer(null);
					return InteractionResult.CONSUME;
				}

				this.openTradingScreen(player, this.getDisplayName(), 1);
			}

			return InteractionResult.SUCCESS;
		} else {
			return super.mobInteract(player, hand);
		}
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		this.offers = new MerchantOffers();
	}

	@Override
	protected void rewardTradeXp(MerchantOffer offer) {

	}

	@Override
	public MerchantOffers getOffers() {
		if (this.level().isClientSide()) {
			throw new IllegalStateException("Cannot load Broker offers on the client");
		} else {
			if (this.offers == null) {
				this.offers = new MerchantOffers();
			}

			return this.offers;
		}
	}

	@Override
	protected void updateTrades(ServerLevel serverLevel) {
		if (this.offers == null) {
			this.offers = new MerchantOffers();
		} else {
			this.offers.clear();
		}

		if (this.level().isClientSide()) {
			throw new IllegalStateException("Cannot update Broker trades on the client");
		}
		if (this.getTradingPlayer() == null) {
			// If the trading player is null, we cannot proceed with updating trades
			return;
		} else {
			BrokerData data = BrokerData.get(this.level());
			List<StoredEnchantment> storedEnchantments = new ArrayList<>(data.getEnchantments(this.getTradingPlayer().getGameProfile().id()));
			if (storedEnchantments.isEmpty()) {
				// If there are no enchantments, we cannot proceed with updating trades
				return;
			}
			// Randomly sort the enchantments
			storedEnchantments.sort((e1, e2) -> this.random.nextInt(2) - 1);
			// Add 3 random enchantments to the offers
			for (int i = 0; i < NUMBER_OF_TRADE_OFFERS; i++) {
				if (i < storedEnchantments.size()) {
					StoredEnchantment enchantment = storedEnchantments.get(i);
					Holder<Enchantment> enchantmentHolder = enchantment.enchantmentHolder();
					int level = enchantment.level();

					ItemStack bookStack = new ItemStack(Items.ENCHANTED_BOOK);
					bookStack.enchant(enchantmentHolder, level);

					int cost = random.nextIntBetweenInclusive(1, (level * 2)) + 1;
					MerchantOffer offer = new MerchantOffer(
							new ItemCost(Items.EMERALD, cost),
							Optional.empty(),
							bookStack, 1, 5, 0.2F);
					this.offers.add(offer);
				}
			}
		}
	}

	@Override
	public void notifyTrade(MerchantOffer offer) {
		if (this.getTradingPlayer() instanceof ServerPlayer serverPlayer) {
			ItemStack resultStack = offer.getResult();
			ItemEnchantments enchantments = resultStack.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);

			BrokerData data = BrokerData.get(this.level());
			Optional<Entry<Holder<Enchantment>>> enchantmentHolder = enchantments.entrySet().stream().findFirst();
			enchantmentHolder.ifPresent(entry ->
					data.removeStoredEnchantment(serverPlayer.getGameProfile().id(), entry.getKey(), entry.getIntValue())
			);
			data.setDirty();
		}
		super.notifyTrade(offer);
	}

	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		return null;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isTrading() ? ModRegistry.BROKER_TRADE.get() : ModRegistry.BROKER_AMBIENT.get();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return ModRegistry.BROKER_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ModRegistry.BROKER_DEATH.get();
	}

	@Override
	protected SoundEvent getTradeUpdatedSound(boolean getYesSound) {
		return getYesSound ? ModRegistry.BROKER_YES.get() : ModRegistry.BROKER_NO.get();
	}

	@Override
	public SoundEvent getNotifyTradeSound() {
		return ModRegistry.BROKER_YES.get();
	}
}
