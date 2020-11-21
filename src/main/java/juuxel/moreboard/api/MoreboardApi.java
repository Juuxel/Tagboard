package juuxel.moreboard.api;

import juuxel.moreboard.impl.Moreboard;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

public final class MoreboardApi {
    public static <T, G extends TagGroup<T>> void addTagsForRegistry(Registry<? extends T> registry, Supplier<G> groupSupplier) {
        Moreboard.addTagsForRegistry(registry, groupSupplier);
    }
}
