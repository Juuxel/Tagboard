package juuxel.tagboard.api;

import juuxel.tagboard.impl.Tagboard;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.registry.Registry;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Public API methods for Tagboard.
 */
public final class TagboardApi {
    /**
     * Associates a tag group supplier with a registry.
     *
     * @param registry      the registry
     * @param groupSupplier a supplier for the tag group
     * @param <T> the registry value type
     * @param <G> the group type
     * @throws NullPointerException if either parameter is null
     * @throws IllegalStateException if the registry is already associated with a tag group
     * @since 1.0.0
     */
    public static <T, G extends TagGroup<T>> void addTagsForRegistry(Registry<? extends T> registry, Supplier<G> groupSupplier) {
        Objects.requireNonNull(registry, "registry");
        Objects.requireNonNull(groupSupplier, "group supplier");
        Tagboard.addTagsForRegistry(registry, groupSupplier);
    }
}
