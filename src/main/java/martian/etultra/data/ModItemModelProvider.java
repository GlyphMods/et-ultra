package martian.etultra.data;

import martian.etultra.EtUltra;
import martian.etultra.EtUltraContent;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import static martian.etultra.EtUltra.id;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, EtUltra.ID, efh);
    }

    @Override
    protected void registerModels() {
        basicItem(id("oxygen_drill_active"))
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"))
                .texture("layer0", id("item/oxygen_drill_active"));

        basicItem(EtUltraContent.ITEM_OXYGEN_DRILL.get())
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"))
                .override().predicate(id("active"), 1f).model(new ModelFile.ExistingModelFile(id("item/oxygen_drill_active"), this.existingFileHelper));
    }
}
