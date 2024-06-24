package martian.etultra.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record AsteroidConfiguration(
        AsteroidBlockSettings blockSettings,
        BlockPredicate target,
        IntProvider radius
) implements FeatureConfiguration {
    public static final Codec<AsteroidConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            AsteroidBlockSettings.CODEC.fieldOf("block_settings").forGetter(AsteroidConfiguration::blockSettings),
            BlockPredicate.CODEC.fieldOf("target").forGetter(AsteroidConfiguration::target),
            IntProvider.POSITIVE_CODEC.fieldOf("radius").forGetter(AsteroidConfiguration::radius)
    ).apply(instance, AsteroidConfiguration::new));

    public record AsteroidBlockSettings(
            BlockStateProvider indicator,
            BlockStateProvider outerShell,
            BlockStateProvider innerShell,
            BlockStateProvider outerCore,
            BlockStateProvider innerCore
    ) {
        public static final Codec<AsteroidBlockSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("indicator").forGetter(AsteroidBlockSettings::indicator),
                BlockStateProvider.CODEC.fieldOf("outer_shell").forGetter(AsteroidBlockSettings::outerShell),
                BlockStateProvider.CODEC.fieldOf("inner_shell").forGetter(AsteroidBlockSettings::innerShell),
                BlockStateProvider.CODEC.fieldOf("outer_core").forGetter(AsteroidBlockSettings::outerCore),
                BlockStateProvider.CODEC.fieldOf("inner_core").forGetter(AsteroidBlockSettings::innerCore)
        ).apply(instance, AsteroidBlockSettings::new));
    }
}
