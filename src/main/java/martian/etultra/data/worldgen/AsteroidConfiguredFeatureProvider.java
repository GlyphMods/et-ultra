package martian.etultra.data.worldgen;

import martian.etultra.EtUltra;
import martian.etultra.common.worldgen.AsteroidConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import static martian.etultra.EtUltra.id;

public class AsteroidConfiguredFeatureProvider {
    public static final ResourceKey<ConfiguredFeature<?, ?>>
            ASTEROID_C_TYPE = key("asteroid_c_type"),
            ASTEROID_S_TYPE_IRON = key("asteroid_s_type_iron"),
            ASTEROID_S_TYPE_COPPER = key("asteroid_s_type_copper"),
            ASTEROID_S_TYPE_GOLD = key("asteroid_s_type_gold"),
            ASTEROID_S_TYPE_DIAMOND = key("asteroid_s_type_diamond")
    ;

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String key) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, id(key));
    }

    private static BootstapContext<ConfiguredFeature<?, ?>> context;

    static void build(BootstapContext<ConfiguredFeature<?, ?>> context) {
        AsteroidConfiguredFeatureProvider.context = context;

        asteroid(ASTEROID_C_TYPE, new AsteroidConfiguration(
                new AsteroidConfiguration.AsteroidBlockSettings(
                        BlockStateProvider.simple(Blocks.SNOW_BLOCK),
                        BlockStateProvider.simple(Blocks.STONE),
                        BlockStateProvider.simple(Blocks.ICE),
                        BlockStateProvider.simple(Blocks.PACKED_ICE),
                        BlockStateProvider.simple(Blocks.PACKED_ICE)
                ),
                BlockPredicate.alwaysTrue(),
                BiasedToBottomInt.of(14, 20)
        ));

        asteroidMType(ASTEROID_S_TYPE_IRON, Blocks.IRON_ORE, Blocks.RAW_IRON_BLOCK);
        asteroidMType(ASTEROID_S_TYPE_COPPER, Blocks.COPPER_ORE, Blocks.RAW_COPPER_BLOCK);
        asteroidMType(ASTEROID_S_TYPE_GOLD, Blocks.GOLD_ORE, Blocks.RAW_GOLD_BLOCK);
        asteroidMType(ASTEROID_S_TYPE_DIAMOND, Blocks.COAL_ORE, Blocks.DIAMOND_ORE);
    }

    private static void asteroid(ResourceKey<ConfiguredFeature<?, ?>> key, AsteroidConfiguration config) {
        context.register(key, new ConfiguredFeature<>(EtUltra.FEATURE_ASTEROID.get(), config));
    }

    private static void asteroidMType(ResourceKey<ConfiguredFeature<?, ?>> key, Block outerCore, Block innerCore) {
        asteroid(key, new AsteroidConfiguration(
                new AsteroidConfiguration.AsteroidBlockSettings(
                        BlockStateProvider.simple(Blocks.COBBLESTONE),
                        BlockStateProvider.simple(Blocks.ANDESITE),
                        BlockStateProvider.simple(Blocks.ANDESITE),
                        BlockStateProvider.simple(outerCore),
                        BlockStateProvider.simple(innerCore)
                ),
                BlockPredicate.alwaysTrue(),
                BiasedToBottomInt.of(14, 20)
        ));
    }
}
