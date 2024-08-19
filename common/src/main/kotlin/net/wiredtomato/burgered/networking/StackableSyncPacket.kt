package net.wiredtomato.burgered.networking

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.wiredtomato.burgered.Burgered
import net.wiredtomato.burgered.api.data.burger.BurgerStackable

class StackableSyncPacket(
    val stackables: List<BurgerStackable>
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<StackableSyncPacket> = TYPE

    companion object {
        val TYPE = CustomPacketPayload.Type<StackableSyncPacket>(Burgered.modLoc("stackable_sync"))

        val PACKET_CODEC: StreamCodec<RegistryFriendlyByteBuf, StackableSyncPacket> = StreamCodec.of(::streamEncode, ::streamDecode)

        private fun streamEncode(buf: RegistryFriendlyByteBuf, packet: StackableSyncPacket) {
            buf.writeInt(packet.stackables.size)
            packet.stackables.forEach { stackable ->
                BurgerStackable.PACKET_CODEC.encode(buf, stackable)
            }
        }

        private fun streamDecode(buf: RegistryFriendlyByteBuf): StackableSyncPacket {
            val size = buf.readInt()
            val stackables = mutableListOf<BurgerStackable>()
            (0..<size).forEach { _ ->
                val stackable = BurgerStackable.PACKET_CODEC.decode(buf)
                stackables.add(stackable)
            }

            return StackableSyncPacket(stackables)
        }
    }
}