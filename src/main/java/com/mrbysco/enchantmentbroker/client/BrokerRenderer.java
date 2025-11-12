package com.mrbysco.enchantmentbroker.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.enchantmentbroker.entity.Broker;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.VillagerRenderState;
import net.minecraft.resources.ResourceLocation;

public class BrokerRenderer extends MobRenderer<Broker, VillagerRenderState, VillagerModel> {
	private static final ResourceLocation BROKER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/wandering_trader.png");

	public BrokerRenderer(EntityRendererProvider.Context context) {
		super(context, new VillagerModel(context.bakeLayer(ModelLayers.WANDERING_TRADER)), 0.5F);
		this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getPlayerSkinRenderCache()));
		this.addLayer(new CrossedArmsItemLayer<>(this));
	}

	@Override
	public VillagerRenderState createRenderState() {
		return new VillagerRenderState();
	}

	@Override
	public void extractRenderState(Broker broker, VillagerRenderState renderState, float partialTick) {
		super.extractRenderState(broker, renderState, partialTick);
		HoldingEntityRenderState.extractHoldingEntityRenderState(broker, renderState, this.itemModelResolver);
		renderState.isUnhappy = broker.getUnhappyCounter() > 0;
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	@Override
	public ResourceLocation getTextureLocation(VillagerRenderState broke) {
		return BROKER_TEXTURE;
	}

	@Override
	protected void scale(VillagerRenderState renderState, PoseStack poseStack) {
		float f = 0.9375F;
		poseStack.scale(f, f, f);
	}
}
