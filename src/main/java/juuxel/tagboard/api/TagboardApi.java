package juuxel.tagboard.api;

import juuxel.tagboard.impl.Tagboard;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public final class TagboardApi {
    public static <T, G extends TagGroup<T>> void addTagsForRegistry(Registry<? extends T> registry, Supplier<G> groupSupplier) {
        Tagboard.addTagsForRegistry(registry, groupSupplier);
    }
}
