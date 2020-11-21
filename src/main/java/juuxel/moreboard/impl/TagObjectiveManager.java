package juuxel.moreboard.impl;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public final class TagObjectiveManager {
    private static final Map<StatType<?>, StatTypeEntry<?>> statTypes = new HashMap<>();

    // StatType<T> -> Identifier in Registry<T> -> Set<ScoreboardCriterion>
    private static final Map<StatType<?>, SetMultimap<Identifier, ScoreboardCriterion>> criteria = new HashMap<>();

    public static <T> Set<ScoreboardCriterion> getTagCriteria(Stat<T> stat) {
        if (!criteria.containsKey(stat.getType())) {
            return Collections.emptySet();
        }

        Identifier valueId = stat.getType().getRegistry().getId(stat.getValue());
        return criteria.get(stat.getType()).get(valueId);
    }

    @SuppressWarnings("unchecked")
    public static void rebuild() {
        criteria.clear();

        for (StatType<?> statType : Registry.STAT_TYPE) {
            StatTypeEntry<?> entry = statTypes.get(statType);
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

            criteria.put(statType, ImmutableSetMultimap.copyOf(criterionMap));
        }
    }

    private static ScoreboardCriterion getCriterion(Identifier statId, Identifier tagId) {
        String name = idToString(statId) + ":#" + idToString(tagId);

        return ScoreboardCriterion.OBJECTIVES.containsKey(name)
            ? ScoreboardCriterion.OBJECTIVES.get(name)
            : new ScoreboardCriterion(name);
    }

    private static String idToString(Identifier id) {
        return id.toString().replace(':', '.');
    }

    public static <T> void addStat(StatType<T> statType, Registry<T> registry, Supplier<TagGroup<T>> groupSupplier) {
        TagObjectiveManager.statTypes.put(statType, new StatTypeEntry<>(registry::getId, groupSupplier));
    }

    private static final class StatTypeEntry<T> {
        final Function<T, Identifier> idGetter;
        final Supplier<TagGroup<T>> groupSupplier;

        StatTypeEntry(Function<T, Identifier> idGetter, Supplier<TagGroup<T>> groupSupplier) {
            this.idGetter = idGetter;
            this.groupSupplier = groupSupplier;
        }
    }
}
