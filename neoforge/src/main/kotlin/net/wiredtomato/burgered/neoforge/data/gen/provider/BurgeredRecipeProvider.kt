package net.wiredtomato.burgered.neoforge.data.gen.provider

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.data.recipes.SingleItemRecipeBuilder
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.CookingBookCategory
import net.minecraft.world.item.crafting.Ingredient
import net.wiredtomato.burgered.Burgered
import net.wiredtomato.burgered.init.BurgeredItems
import net.wiredtomato.burgered.recipe.GrillingRecipe
import net.wiredtomato.burgered.util.id
import java.util.concurrent.CompletableFuture

class BurgeredRecipeProvider(
    packOutput: PackOutput,
    provider: CompletableFuture<HolderLookup.Provider>
) : RecipeProvider(packOutput, provider) {
    override fun buildRecipes(recipeOutput: RecipeOutput, holderLookup: HolderLookup.Provider) {
        burgerSmithingRecipe(Items.BOOK, BurgeredItems.EDIBLE_BOOK).save(recipeOutput, Burgered.modLoc("edible_book_smithing"))

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BurgeredItems.BOOK_OF_BURGERS)
            .requires(BurgeredItems.BURGER)
            .requires(Items.BOOK)
            .unlockedBy(getHasName(Items.BOOK), has(Items.BOOK))
            .save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BurgeredItems.BOOK_OF_BURGERS, 32)
            .pattern("DbD")
            .pattern("DBD")
            .pattern("DDD")
            .define('D', Items.DIAMOND)
            .define('b', BurgeredItems.BOOK_OF_BURGERS)
            .define('B', BurgeredItems.BURGER)
            .unlockedBy(
                getHasName(BurgeredItems.BOOK_OF_BURGERS),
                has(BurgeredItems.BOOK_OF_BURGERS)
            )
            .save(recipeOutput, Burgered.modLoc("book_of_burgers_duplicate"))

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BurgeredItems.BURGER_STACKER)
            .pattern(" F ")
            .pattern(" F ")
            .pattern("SSS")
            .define('F', ItemTags.FENCES)
            .define('S', ItemTags.SLABS)
            .unlockedBy(
                getHasName(BurgeredItems.BOTTOM_BUN),
                has(BurgeredItems.BOTTOM_BUN)
            )
            .save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BurgeredItems.GRILL)
            .pattern("III")
            .pattern("I I")
            .pattern("I I")
            .define('I', Items.IRON_BARS)
            .unlockedBy(
                getHasName(BurgeredItems.BOTTOM_BUN),
                has(BurgeredItems.BOTTOM_BUN)
            )
            .save(recipeOutput)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BurgeredItems.ESTROGEN_WAFFLE)
            .pattern(" C ")
            .pattern("EME")
            .pattern("MEM")
            .define('C', ItemTags.CANDLES)
            .define('E', Items.EGG)
            .define('M', Items.MILK_BUCKET)
            .unlockedBy(getHasName(Items.MILK_BUCKET), has(Items.MILK_BUCKET))
            .save(recipeOutput)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, BurgeredItems.RAW_BEEF_PATTY, 4)
            .requires(Items.BEEF)
            .requires(Items.STICK)
            .unlockedBy(getHasName(Items.BEEF), has(Items.BEEF))
            .save(recipeOutput)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, BurgeredItems.PICKLED_BEETS, 4)
            .requires(Items.SEA_PICKLE)
            .requires(Items.BEETROOT)
            .unlockedBy(getHasName(Items.BEETROOT), has(Items.BEETROOT))
            .save(recipeOutput)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, BurgeredItems.LETTUCE, 4)
            .requires(Items.SEA_PICKLE)
            .requires(Items.STICK)
            .unlockedBy(getHasName(Items.SEA_PICKLE), has(Items.SEA_PICKLE))
            .save(recipeOutput)

        SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(Items.BREAD),
            RecipeCategory.FOOD,
            BurgeredItems.TOP_BUN,
            2
        ).unlockedBy(
            getHasName(Items.BREAD),
            has(Items.BREAD)
        ).save(recipeOutput, Burgered.modLoc("top_bun_stonecutting"))

        SingleItemRecipeBuilder.stonecutting(
            Ingredient.of(Items.BREAD),
            RecipeCategory.FOOD,
            BurgeredItems.BOTTOM_BUN,
            2
        ).unlockedBy(
            getHasName(Items.BREAD),
            has(Items.BREAD)
        ).save(recipeOutput, Burgered.modLoc("bottom_bun_stonecutting"))

        grillingRecipe(
            recipeOutput,
            CookingBookCategory.FOOD,
            Ingredient.of(BurgeredItems.RAW_BEEF_PATTY),
            ItemStack.EMPTY,
            BurgeredItems.BEEF_PATTY.defaultInstance,
            25f,
            100,
            getHasName(BurgeredItems.RAW_BEEF_PATTY),
            has(BurgeredItems.RAW_BEEF_PATTY)
        )

        grillingRecipe(
            recipeOutput,
            CookingBookCategory.FOOD,
            Ingredient.of(Items.MILK_BUCKET),
            Items.BUCKET.defaultInstance,
            ItemStack(BurgeredItems.CHEESE_SLICE, 4),
            25f,
            100,
            getHasName(Items.MILK_BUCKET),
            has(Items.MILK_BUCKET)
        )
    }

    fun grillingRecipe(
        exporter: RecipeOutput,
        category: CookingBookCategory,
        ingredient: Ingredient,
        transform: ItemStack,
        result: ItemStack,
        experience: Float,
        cookingTime: Int,
        unlockedByName: String,
        unlockedBy: Criterion<*>,
        recipeId: ResourceLocation = result.item.id
    ) {
        val recipe = GrillingRecipe("", category, ingredient, transform, result, experience, cookingTime)
        val builder = exporter.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(
            AdvancementRewards.Builder.recipe(recipeId)
        ).requirements(AdvancementRequirements.Strategy.OR)
        builder.addCriterion(unlockedByName, unlockedBy)

        exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefix("recipes/" + category.serializedName + "/")))
    }

    fun burgerSmithingRecipe(base: Item, result: Item): SmithingTransformRecipeBuilder {
        return SmithingTransformRecipeBuilder(
            Ingredient.of(BurgeredItems.BOOK_OF_BURGERS),
            Ingredient.of(base),
            Ingredient.of(BurgeredItems.BURGER),
            RecipeCategory.FOOD,
            result
        ).unlocks(
            getHasName(BurgeredItems.BOOK_OF_BURGERS),
            has(BurgeredItems.BOOK_OF_BURGERS)
        )
    }
}
