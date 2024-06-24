package martian.etultra.common.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class AsteroidFeature extends Feature<AsteroidConfiguration> {
    public AsteroidFeature(Codec<AsteroidConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<AsteroidConfiguration> context) {
        AsteroidConfiguration config = context.config();
        BlockPos origin = context.origin();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        boolean bl = false;
        int radius = config.radius().sample(random) / 2;

        for (BlockPos p2 : BlockPos.betweenClosed(origin.offset(-radius, -radius, -radius), origin.offset(radius, radius, radius))) {
            int x2 = p2.getX() - origin.getX();
            int y2 = p2.getY() - origin.getY();
            int z2 = p2.getZ() - origin.getZ();
            var a = x2 * x2 + y2 * y2 + z2 * z2;
            var b = radius * radius + random.nextIntBetweenInclusive(-4, 4);

            if (a <= b) {
                BlockState state;

                double distFromOrigin = origin.getCenter().distanceTo(p2.getCenter());
                float r = random.nextFloat();

                if (distFromOrigin <= 2) {
                    state = config.blockSettings().innerCore().getState(random, p2);
                } else if (distFromOrigin <= 3.5) {
                    if (r <= 0.2)
                        state = config.blockSettings().innerCore().getState(random, p2);
                    else
                        state = config.blockSettings().outerCore().getState(random, p2);
                } else if (distFromOrigin <= 6) {
                    if (r <= 0.4)
                        state = config.blockSettings().outerCore().getState(random, p2);
                    else
                        state = config.blockSettings().innerShell().getState(random, p2);
                } else {
                    if (r <= 0.05)
                        state = config.blockSettings().indicator().getState(random, p2);
                    else
                        state = config.blockSettings().outerShell().getState(random, p2);
                }

                level.setBlock(p2, state, 2);
                this.markAboveForPostProcessing(level, p2);
            }
        }

        return bl;
    }
}
