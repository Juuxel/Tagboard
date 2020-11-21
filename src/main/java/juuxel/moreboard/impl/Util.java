package juuxel.moreboard.impl;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.BiConsumer;

final class Util {
    static <T> void visit(Registry<T> registry, BiConsumer<T, Identifier> visitor) {
        for (T entry : registry) {
            visitor.accept(entry, registry.getId(entry));
        }

        RegistryEntryAddedCallback.event(registry).register((rawId, id, entry) -> visitor.accept(entry, id));
    }
}
