package martian.etultra;

import martian.etultra.data.ModItemModelProvider;
import martian.etultra.data.ModPlanetProvider;
import martian.etultra.data.recipe.ModCraftingProvider;
import martian.etultra.data.worldgen.ModWorldGenProvider;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(EtUltra.ID)
public class EtUltra {
    public static final String ID = "etultra";
    public static final Logger LOG = LoggerFactory.getLogger(ID);

    public EtUltra() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        EtUltraContent.register(modBus);

        modBus.addListener((final FMLClientSetupEvent event) -> {
            event.enqueueWork(() -> {
                ItemProperties.register(EtUltraContent.ITEM_OXYGEN_DRILL.get(), id("active"), (stack, level, entity, id) ->
                    entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1f : 0f
                );
            });
        });

        modBus.addListener((final GatherDataEvent event) -> {
            var generator = event.getGenerator();
            var output = generator.getPackOutput();
            var efh = event.getExistingFileHelper();

            var planetProvider = new ModPlanetProvider(output);

            if (event.includeServer()) {
                generator.addProvider(true, planetProvider.planetProvider);
                generator.addProvider(true, new ModWorldGenProvider(output, event.getLookupProvider()));
                generator.addProvider(true, new ModCraftingProvider(output));
            }

            if (event.includeClient()) {
                generator.addProvider(true, planetProvider.planetRendererProvider);
                generator.addProvider(true, new ModItemModelProvider(output, efh));
            }
        });
    }

    public static ResourceLocation id(String it) {
        return new ResourceLocation(ID, it);
    }
}
