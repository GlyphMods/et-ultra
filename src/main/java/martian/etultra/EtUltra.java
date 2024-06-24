package martian.etultra;

import martian.etultra.common.worldgen.AsteroidConfiguration;
import martian.etultra.common.worldgen.AsteroidFeature;
import martian.etultra.data.PlanetProvider;
import martian.etultra.data.worldgen.WorldGenProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(EtUltra.ID)
public class EtUltra {
    public static final String ID = "etultra";
    public static final Logger LOG = LoggerFactory.getLogger(ID);

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, ID);

    public static final RegistryObject<AsteroidFeature> FEATURE_ASTEROID = FEATURES.register("asteroid", () -> new AsteroidFeature(AsteroidConfiguration.CODEC));

    public EtUltra() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        FEATURES.register(modBus);

        modBus.addListener((final FMLCommonSetupEvent event) -> {
            // common setup
        });

        modBus.addListener((final GatherDataEvent event) -> {
            var generator = event.getGenerator();
            var output = generator.getPackOutput();

            var planetProvider = new PlanetProvider(output);

            if (event.includeServer()) {
                generator.addProvider(true, planetProvider.planetProvider);
                generator.addProvider(true, new WorldGenProvider(output, event.getLookupProvider()));
            }

            if (event.includeClient()) {
                generator.addProvider(true, planetProvider.planetRendererProvider);
            }
        });
    }

    public static ResourceLocation id(String it) {
        return new ResourceLocation(ID, it);
    }
}
