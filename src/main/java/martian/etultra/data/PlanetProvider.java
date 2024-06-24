package martian.etultra.data;

import com.mojang.serialization.JsonOps;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.client.dimension.MovementType;
import earth.terrarium.adastra.client.dimension.PlanetRenderer;
import earth.terrarium.adastra.client.dimension.SkyRenderable;
import earth.terrarium.adastra.client.utils.DimensionRenderingUtils;
import earth.terrarium.adastra.common.constants.PlanetConstants;
import martian.etultra.EtUltra;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import static martian.etultra.EtUltra.id;

public class PlanetProvider {
    public final ModProvider<Planet> planetProvider;
    public final ModProvider<PlanetRenderer> planetRendererProvider;

    public static final int DEFAULT_SUNRISE_COLOR = 0xd85f33;

    public static final SimpleWeightedRandomList<Integer> COLORED_STARS = SimpleWeightedRandomList.<Integer>builder()
            .add(0xA9BCDFFF, 3)   // Blue
            .add(0xBBD7FFFF, 5)   // Blue-White,
            .add(0xFFF4E8FF, 100) // Yellow-White
            .add(0xFFD1A0FF, 80)  // Orange
            .add(0xFF8A8AFF, 150) // Red
            .add(0xFF4500FF, 10)  // Orange-Red
            .add(0xFFFFF4FF, 60)  // White
            .add(0xFFF8E7FF, 40)  // Pale Yellow
            .add(0xFFFFFF00, 20)  // Very Pale Yellow
            .add(0xFFFF0000, 1)   // Bright Red
            .build();

    public static final SimpleWeightedRandomList<Integer> DEFAULT_STARS = SimpleWeightedRandomList.<Integer>builder()
            .add(0xffffffff, 1)
            .build();


    public PlanetProvider(PackOutput output) {
        planetProvider = new ModProvider<>(
                "Planets",
                output.createPathProvider(PackOutput.Target.DATA_PACK, "planets"),
                planet -> Planet.CODEC
                        .encodeStart(JsonOps.INSTANCE, planet)
                        .getOrThrow(false, EtUltra.LOG::error)
                        .getAsJsonObject()
        ) {
            @Override
            protected void build(@NotNull BiConsumer<ResourceLocation, Planet> consumer) {
                buildPlanets(consumer);
            }
        };

        planetRendererProvider = new ModProvider<>(
                "Planet Renderers",
                output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "planet_renderers"),
                planet -> PlanetRenderer.CODEC
                        .encodeStart(JsonOps.INSTANCE, planet)
                        .getOrThrow(false, EtUltra.LOG::error)
                        .getAsJsonObject()
        ) {
            @Override
            protected void build(@NotNull BiConsumer<ResourceLocation, PlanetRenderer> consumer) {
                buildRenderers(consumer);
            }
        };
    }

    protected void buildPlanets(@NotNull BiConsumer<ResourceLocation, Planet> consumer) {
        consumer.accept(id("asteroids"), new Planet(
                ResourceKey.create(Registries.DIMENSION, id("asteroids")),
                false,
                PlanetConstants.SPACE_TEMPERATURE,
                PlanetConstants.SPACE_GRAVITY,
                PlanetConstants.SPACE_SOLAR_POWER,
                PlanetConstants.SOLAR_SYSTEM,
                Optional.of(Level.OVERWORLD),
                2,
                List.of()
        ));
    }

    protected void buildRenderers(@NotNull BiConsumer<ResourceLocation, PlanetRenderer> consumer) {
        consumer.accept(id("asteroids"), new PlanetRenderer(
                ResourceKey.create(Registries.DIMENSION, id("asteroids")),
                true,
                false,
                true,
                true,
                true,
                FastColor.ARGB32.color(255, 200, 100, 20),
                100,
                Optional.of(1.0f),
                0,
                true,
                COLORED_STARS,
                List.of(new SkyRenderable(DimensionRenderingUtils.SUN, 9, Vec3.ZERO, Vec3.ZERO, MovementType.TIME_OF_DAY, 0xffffffd9))
        ));
    }
}
