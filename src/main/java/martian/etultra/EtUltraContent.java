package martian.etultra;

import martian.etultra.common.item.ItemOxygenDrill;
import martian.etultra.common.worldgen.AsteroidConfiguration;
import martian.etultra.common.worldgen.AsteroidFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EtUltraContent {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, EtUltra.ID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, EtUltra.ID);

    // Items
    public static final RegistryObject<Item> ITEM_OXYGEN_DRILL = ITEMS.register("oxygen_drill", () -> new ItemOxygenDrill(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    // Features
    public static final RegistryObject<AsteroidFeature> FEATURE_ASTEROID = FEATURES.register("asteroid", () -> new AsteroidFeature(AsteroidConfiguration.CODEC));

    static void register(IEventBus bus) {
        ITEMS.register(bus);
        FEATURES.register(bus);
    }
}
