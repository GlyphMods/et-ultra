package martian.etultra.data;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class ModProvider<T> implements DataProvider {
    protected final PackOutput.PathProvider pathProvider;
    protected final String name;
    protected final Function<T, JsonObject> serializer;

    public ModProvider(String name, PackOutput.PathProvider pathProvider, Function<T, JsonObject> serializer) {
        this.name = name;
        this.pathProvider = pathProvider;
        this.serializer = serializer;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Set<ResourceLocation> ids = Sets.newHashSet();
        List<CompletableFuture<?>> items = new ArrayList<>();

        build((id, it) -> {
            if (!ids.add(id))
                throw new IllegalStateException("Duplicate entry (in " + name + ") " + id);
            else
                items.add(DataProvider.saveStable(output, serializer.apply(it), this.pathProvider.json(id)));
        });

        return CompletableFuture.allOf(items.toArray(new CompletableFuture[0]));
    }

    @Override
    public String getName() {
        return name;
    }

    protected abstract void build(BiConsumer<ResourceLocation, T> consumer);
}
