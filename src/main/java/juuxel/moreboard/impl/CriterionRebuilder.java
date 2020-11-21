package juuxel.moreboard.impl;

import com.google.common.collect.ImmutableSet;
import juuxel.moreboard.api.TagStatRegistry;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Set;

final class CriterionRebuilder implements SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = Moreboard.id("criterion_rebuilder");
    private static final Set<Identifier> DEPENDENCIES = ImmutableSet.of(ResourceReloadListenerKeys.TAGS);

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return DEPENDENCIES;
    }

    @Override
    public void apply(ResourceManager manager) {
        TagStatRegistry.rebuild();
    }
}
