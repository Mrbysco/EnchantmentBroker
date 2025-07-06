package com.mrbysco.enchantmentbroker.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.enchantmentbroker.entity.Broker;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

public class BrokerRenderer extends MobRenderer<Broker, VillagerModel<Broker>> {
	private static final ResourceLocation BROKER_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/wandering_trader.png");

	public BrokerRenderer(EntityRendererProvider.Context context) {
		super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.WANDERING_TRADER)), 0.5F);
		this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
		this.addLayer(new CrossedArmsItemLayer<>(this, context.getItemInHandRenderer()));
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	@Override
	public ResourceLocation getTextureLocation(Broker broke) {
		return BROKER_TEXTURE;
	}

	@Override
	protected void scale(Broker broker, PoseStack poseStack, float partialTickTime) {
		float f = 0.9375F;
		poseStack.scale(f, f, f);
	}
}
