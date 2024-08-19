package net.wiredtomato.burgered.api.data.burger

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.Item
import net.wiredtomato.burgered.Burgered
import net.wiredtomato.burgered.api.StatusEffectEntry
import net.wiredtomato.burgered.api.event.LivingEntityEvents
import net.wiredtomato.burgered.init.BurgeredEatEvents
import net.wiredtomato.burgered.init.BurgeredRegistries
import net.wiredtomato.burgered.util.byNameCodec
import org.jetbrains.annotations.ApiStatus.Internal
import java.util.*

data class BurgerStackable(
    val item: Item,
    val hunger: Int,
    val saturation: Float,
    val modelHeight: Double = 1.0,
    val statusEffects: List<StatusEffectEntry> = listOf(),
    val customName: Optional<String> = Optional.empty(),
    val eatEvent: Optional<BurgerStackableEatCallback> = Optional.empty()
) {
    companion object {
        val CODEC: Codec<BurgerStackable> = RecordCodecBuilder.create { builder ->
            builder.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(BurgerStackable::item),
                Codec.INT.fieldOf("hunger").orElse(0).forGetter(BurgerStackable::hunger),
                Codec.FLOAT.fieldOf("saturation").orElse(0f).forGetter(BurgerStackable::saturation),
                Codec.DOUBLE.fieldOf("modelHeight").orElse(1.0).forGetter(BurgerStackable::modelHeight),
                StatusEffectEntry.CODEC.listOf().fieldOf("statusEffects").orElse(listOf())
                    .forGetter(BurgerStackable::statusEffects),
                Codec.STRING.optionalFieldOf("customName").forGetter(BurgerStackable::customName),
                BurgeredRegistries.EAT_EVENT.byNameCodec().optionalFieldOf("eatEvent")
                    .forGetter(BurgerStackable::eatEvent),
            ).apply(builder, ::BurgerStackable)
        }

        val PACKET_CODEC: StreamCodec<RegistryFriendlyByteBuf, BurgerStackable> = StreamCodec.of(::streamEncode, ::streamDecode)

        private fun streamEncode(buf: RegistryFriendlyByteBuf, stack: BurgerStackable) {
            buf.writeResourceLocation(BuiltInRegistries.ITEM.getKey(stack.item))
            buf.writeInt(stack.hunger)
            buf.writeFloat(stack.saturation)
            buf.writeDouble(stack.modelHeight)
            buf.writeInt(stack.statusEffects.size)
            stack.statusEffects.forEach { StatusEffectEntry.STREAM_CODEC.encode(buf, it) }
            buf.writeOptional(stack.customName) { subBuf, str -> subBuf.writeUtf(str) }
            buf.writeOptional(stack.eatEvent) { subBuf, event ->
                subBuf.writeResourceLocation(
                    BurgeredRegistries.EAT_EVENT.getId(
                        event
                    ) ?: Burgered.modLoc("no_op")
                )
            }
        }

        private fun streamDecode(buf: RegistryFriendlyByteBuf): BurgerStackable {
            val item = BuiltInRegistries.ITEM.get(buf.readResourceLocation())
            val hunger = buf.readInt()
            val saturation = buf.readFloat()
            val modelHeight = buf.readDouble()
            val statusEffectsSize = buf.readInt()
            val statusEffects = mutableListOf<StatusEffectEntry>()
            (0..<statusEffectsSize).forEach { _ ->
                val se = StatusEffectEntry.STREAM_CODEC.decode(buf)
                statusEffects.add(se)
            }
            val customName = buf.readOptional { subBuf -> subBuf.readUtf() }
            val eatEvent: Optional<BurgerStackableEatCallback> = buf.readOptional { subBuf ->
                BurgeredRegistries.EAT_EVENT.get(subBuf.readResourceLocation()) ?: BurgeredEatEvents.NO_OP
            }

            return BurgerStackable(item, hunger, saturation, modelHeight, statusEffects, customName, eatEvent)
        }
    }
}

fun interface BurgerStackableEatCallback : LivingEntityEvents.EatCallback

@Internal
object BurgerStackables : MutableList<BurgerStackable> by mutableListOf()
