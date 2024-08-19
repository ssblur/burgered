package net.wiredtomato.burgered.client

import dev.architectury.networking.NetworkManager
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry
import net.wiredtomato.burgered.api.data.burger.BurgerStackables
import net.wiredtomato.burgered.client.config.BurgeredClientConfig
import net.wiredtomato.burgered.client.rendering.block.BurgerStackerEntityRenderer
import net.wiredtomato.burgered.client.rendering.block.GrillEntityRenderer
import net.wiredtomato.burgered.client.rendering.item.BurgerItemRenderer
import net.wiredtomato.burgered.init.BurgeredBlockEntities
import net.wiredtomato.burgered.init.BurgeredItems
import net.wiredtomato.burgered.networking.StackableSyncPacket
import net.wiredtomato.burgered.platform.DynamicItemRendererRegistry

object BurgeredClient {
    fun init() {
        BurgeredClientConfig.CONFIG.load()

        BlockEntityRendererRegistry.register(BurgeredBlockEntities.BURGER_STACKER, ::BurgerStackerEntityRenderer)
        BlockEntityRendererRegistry.register(BurgeredBlockEntities.GRILL, ::GrillEntityRenderer)
        DynamicItemRendererRegistry.register(BurgeredItems.BURGER, BurgerItemRenderer)

        NetworkManager.registerReceiver(NetworkManager.s2c(), StackableSyncPacket.TYPE, StackableSyncPacket.PACKET_CODEC) { payload, ctx ->
            val serverStackables = payload.stackables
            BurgerStackables.clear()
            BurgerStackables.addAll(serverStackables)
        }
    }
}