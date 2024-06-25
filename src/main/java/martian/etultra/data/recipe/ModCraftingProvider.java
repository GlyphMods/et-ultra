package martian.etultra.data.recipe;

import earth.terrarium.adastra.common.registry.ModItems;
import martian.etultra.EtUltraContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ModCraftingProvider extends RecipeProvider {
    public ModCraftingProvider(PackOutput arg) {
        super(arg);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EtUltraContent.ITEM_OXYGEN_DRILL.get())
                .pattern(" PI")
                .pattern("PTP")
                .pattern("BP ")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('P', TagKey.create(Registries.ITEM, new ResourceLocation("forge", "plates/iron")))
                .define('T', ModItems.GAS_TANK.get())
                .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
                .unlockedBy("has", has(ModItems.GAS_TANK.get()))
                .save(consumer);
    }
}
