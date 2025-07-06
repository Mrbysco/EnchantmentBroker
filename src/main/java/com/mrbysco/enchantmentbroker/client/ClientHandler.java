package com.mrbysco.enchantmentbroker.client;

import com.mrbysco.enchantmentbroker.registry.ModRegistry;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientHandler {
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModRegistry.BROKER.get(), BrokerRenderer::new);
	}
}
