package martian.etultra.data.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

import static martian.etultra.EtUltra.id;

public class AsteroidPlacedFeatureProvider {
    public static final ResourceKey<PlacedFeature>
            ASTEROID_C_TYPE = key("asteroid_c_type"),
            ASTEROID_S_TYPE_IRON = key("asteroid_s_type_iron"),
            ASTEROID_S_TYPE_COPPER = key("asteroid_s_type_copper"),
            ASTEROID_S_TYPE_GOLD = key("asteroid_s_type_gold"),
            ASTEROID_S_TYPE_DIAMOND = key("asteroid_s_type_diamond")
    ;

    private static ResourceKey<PlacedFeature> key(String key) {
        return ResourceKey.create(Registries.PLACED_FEATURE, id(key));
    }

    private static BootstapContext<PlacedFeature> context;
    private static HolderGetter<ConfiguredFeature<?, ?>> lookup;

    static void build(BootstapContext<PlacedFeature> context) {
        AsteroidPlacedFeatureProvider.context = context;
        lookup = context.lookup(Registries.CONFIGURED_FEATURE);

        asteroid(ASTEROID_C_TYPE, AsteroidConfiguredFeatureProvider.ASTEROID_C_TYPE);
        asteroid(ASTEROID_S_TYPE_IRON, AsteroidConfiguredFeatureProvider.ASTEROID_S_TYPE_IRON);
        asteroid(ASTEROID_S_TYPE_COPPER, AsteroidConfiguredFeatureProvider.ASTEROID_S_TYPE_COPPER);
        asteroid(ASTEROID_S_TYPE_GOLD, AsteroidConfiguredFeatureProvider.ASTEROID_S_TYPE_GOLD);
        asteroid(ASTEROID_S_TYPE_DIAMOND, AsteroidConfiguredFeatureProvider.ASTEROID_S_TYPE_DIAMOND);
    }

    private static void asteroid(ResourceKey<PlacedFeature> placedFeatureKey, ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey) {
        context.register(placedFeatureKey, new PlacedFeature(lookup.getOrThrow(configuredFeatureKey), List.of(
                RarityFilter.onAverageOnceEvery(8),
                CountPlacement.of(1),
                RandomOffsetPlacement.horizontal(UniformInt.of(0, 16)),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(16), VerticalAnchor.absolute(128))
        )));
    }
}
