package juuxel.moreboard.api;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class TagStatRegistry {
    private static final Map<StatType<?>, Entry<?>> registry = new HashMap<>();
    private static final Map<StatType<?>, SetMultimap<Identifier, ScoreboardCriterion>> cache = new HashMap<>();

    @ApiStatus.Internal
    public static <T> Set<ScoreboardCriterion> getTagCriteria(Stat<T> stat) {
        if (!cache.containsKey(stat.getType())) {
            return Collections.emptySet();
        }

        Identifier valueId = stat.getType().getRegistry().getId(stat.getValue());
        return cache.get(stat.getType()).get(valueId);
    }

    @SuppressWarnings("unchecked")
    @ApiStatus.Internal
    public static void rebuild() {
        cache.clear();

        for (StatType<?> statType : Registry.STAT_TYPE) {
            Entry<?> entry = registry.get(statType);
            if (entry == null) continue;

            Identifier statId = Registry.STAT_TYPE.getId(statType);
            Multimap<Identifier, ScoreboardCriterion> criterionMap = MultimapBuilder.hashKeys().hashSetValues().build();

            for (Map.Entry<Identifier, ? extends Tag<?>> tagEntry : entry.groupSupplier.get().getTags().entrySet()) {
                Identifier tagId = tagEntry.getKey();
                Tag<?> tag = tagEntry.getValue();

                ScoreboardCriterion criterion = getCriterion(statId, tagId);

                for (Object value : tag.values()) {
                    Identifier id = ((Function<Object, Identifier>) entry.idGetter).apply(value);
                    criterionMap.put(id, criterion);
                }
            }

            cache.put(statType, ImmutableSetMultimap.copyOf(criterionMap));
        }
    }

    private static ScoreboardCriterion getCriterion(Identifier statId, Identifier tagId) {
        return getCriterion(getName(statId, tagId));
    }

    private static ScoreboardCriterion getCriterion(String name) {
        return ScoreboardCriterion.OBJECTIVES.containsKey(name)
            ? ScoreboardCriterion.OBJECTIVES.get(name)
            : new ScoreboardCriterion(name);
    }

    private static String getName(Identifier statId, Identifier tagId) {
        return idToString(statId) + ":#" + idToString(tagId);
    }

    private static String idToString(Identifier id) {
        return id.toString().replace(':', '.');
    }

    public static <T> void register(StatType<T> statType, Registry<T> registry, Supplier<TagGroup<T>> groupSupplier) {
        TagStatRegistry.registry.put(statType, new Entry<>(registry::getId, groupSupplier));
    }

    private static final class Entry<T> {
        final Function<T, Identifier> idGetter;
        final Supplier<TagGroup<T>> groupSupplier;

        Entry(Function<T, Identifier> idGetter, Supplier<TagGroup<T>> groupSupplier) {
            this.idGetter = idGetter;
            this.groupSupplier = groupSupplier;
        }
    }
}
