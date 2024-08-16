package net.wiredtomato.burgered.api.ingredient

import net.minecraft.util.StringRepresentable
import net.wiredtomato.burgered.Burgered

enum class IngredientQuality(val translationKey: String, val multiplier: Double) : StringRepresentable {
    VERY_POOR("${Burgered.MOD_ID}.ingredient_quality.very_poor", 0.5),
    POOR("${Burgered.MOD_ID}.ingredient_quality.poor", 0.75),
    NORMAL("${Burgered.MOD_ID}.ingredient_quality.normal", 1.0),
    GOOD("${Burgered.MOD_ID}.ingredient_quality.good", 1.25),
    EXCELLENT("${Burgered.MOD_ID}.ingredient_quality.excellent", 1.5);

    override fun getSerializedName(): String {
        return name
    }

    fun nextUp(): IngredientQuality {
        return when (this) {
            EXCELLENT -> EXCELLENT
            GOOD -> EXCELLENT
            NORMAL -> GOOD
            POOR -> NORMAL
            VERY_POOR -> POOR
        }
    }

    fun nextDown(): IngredientQuality {
        return when (this) {
            EXCELLENT -> GOOD
            GOOD -> NORMAL
            NORMAL -> POOR
            POOR -> VERY_POOR
            VERY_POOR -> VERY_POOR
        }
    }

    companion object {
        val CODEC = StringRepresentable.fromEnum { IngredientQuality.entries.toTypedArray() }
    }
}
