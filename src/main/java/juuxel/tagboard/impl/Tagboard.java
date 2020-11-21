package juuxel.tagboard.impl;

import juuxel.tagboard.api.TagboardApi;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.stat.StatType;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class Tagboard implements ModInitializer {
    private static final Map<Registry<?>, TagGroupEntry<?>> tagGroupRegistry = new HashMap<>();

    public static <T, G extends TagGroup<T>> void addTagsForRegistry(Registry<? extends T> registry, Supplier<G> groupSupplier) {
        if (tagGroupRegistry.containsKey(registry)) {
            throw new IllegalStateException("Already registered: " + registry);
        }

        tagGroupRegistry.put(registry, new TagGroupEntry<>(registry, groupSupplier));
    }

    @Override
    public void onInitialize() {
        TagboardApi.addTagsForRegistry(Registry.BLOCK, () -> ServerTagManagerHolder.getTagManager().getBlocks());
        TagboardApi.addTagsForRegistry(Registry.ITEM, () -> ServerTagManagerHolder.getTagManager().getItems());
        TagboardApi.addTagsForRegistry(Registry.ENTITY_TYPE, () -> ServerTagManagerHolder.getTagManager().getEntityTypes());
        TagboardApi.addTagsForRegistry(Registry.FLUID, () -> ServerTagManagerHolder.getTagManager().getFluids());

        Util.visit(Registry.STAT_TYPE, (statType, id) -> {
            addStat(statType, tagGroupRegistry.get(statType.getRegistry()));
        });

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CriterionRebuilder());
    }

    @SuppressWarnings("unchecked")
    private static <T> void addStat(StatType<?> statType, @Nullable TagGroupEntry<T> tagGroupEntry) {
        if (tagGroupEntry == null) return;

        TagObjectiveManager.addStat((StatType<T>) statType, tagGroupEntry.registry, tagGroupEntry.groupSupplier);
    }

    public static Identifier id(String path) {
        return new Identifier("tagboard", path);
    }

    private static final class TagGroupEntry<T> {
        final Registry<T> registry;
        final Supplier<TagGroup<T>> groupSupplier;

        @SuppressWarnings("unchecked")
        TagGroupEntry(Registry<? extends T> registry, Supplier<? extends TagGroup<T>> groupSupplier) {
            this.registry = (Registry<T>) registry; // safe because covariant (in this case)
            this.groupSupplier = (Supplier<TagGroup<T>>) groupSupplier; // safe because covariant
        }
    }
}
