package juuxel.tagboard.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatType;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public enum TagCriterionManager implements SimpleSynchronousResourceReloadListener {
    INSTANCE;

    private static final Identifier ID = Tagboard.id("tag_criterion_manager");
    private static final Set<Identifier> DEPENDENCIES = ImmutableSet.of(ResourceReloadListenerKeys.TAGS);

    private static final Map<StatType<?>, StatTypeEntry<?>> statTypes = new HashMap<>();

    // StatType<T> -> Identifier in Registry<T> -> Set<ScoreboardCriterion>
    private static final Map<StatType<?>, SetMultimap<Identifier, ScoreboardCriterion>> criteriaByStat = new HashMap<>();
    static final Set<ScoreboardCriterion> criteria = new HashSet<>();

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return DEPENDENCIES;
    }

    public static <T> Set<ScoreboardCriterion> getTagCriteria(Stat<T> stat) {
        if (!criteriaByStat.containsKey(stat.getType())) {
            return Collections.emptySet();
        }

        Identifier valueId = stat.getType().getRegistry().getId(stat.getValue());
        return criteriaByStat.get(stat.getType()).get(valueId);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void apply(ResourceManager manager) {
        criteria.clear();
        criteriaByStat.clear();

        for (StatType<?> statType : Registry.STAT_TYPE) {
            StatTypeEntry<?> entry = statTypes.get(statType);
            if (entry == null) continue;

            Identifier statId = Registry.STAT_TYPE.getId(statType);
            Multimap<Identifier, ScoreboardCriterion> criterionMap = MultimapBuilder.hashKeys().hashSetValues().build();

            for (Map.Entry<Identifier, ? extends Tag<?>> tagEntry : entry.groupSupplier.get().getTags().entrySet()) {
                Identifier tagId = tagEntry.getKey();
                Tag<?> tag = tagEntry.getValue();

                ScoreboardCriterion criterion = getCriterion(statId, tagId, SourcedCriterion.Source.TAG);
                criteria.add(criterion);

                for (Object value : tag.values()) {
                    Identifier id = ((Function<Object, Identifier>) entry.idGetter).apply(value);
                    criterionMap.put(id, criterion);
                }
            }

            criteriaByStat.put(statType, ImmutableSetMultimap.copyOf(criterionMap));
        }
    }

    private static ScoreboardCriterion getCriterion(Identifier statId, Identifier tagId, SourcedCriterion.Source source) {
        return getCriterion(idToString(statId) + ":#" + idToString(tagId), source);
    }

    public static ScoreboardCriterion getCriterion(String name, SourcedCriterion.Source source) {
        return ScoreboardCriterion.OBJECTIVES.containsKey(name)
            ? ScoreboardCriterion.OBJECTIVES.get(name)
            : new SourcedCriterion(name, source);
    }

    private static String idToString(Identifier id) {
        return id.toString().replace(':', '.');
    }

    public static <T> void addStat(StatType<T> statType, Registry<T> registry, Supplier<TagGroup<T>> groupSupplier) {
        TagCriterionManager.statTypes.put(statType, new StatTypeEntry<>(registry::getId, groupSupplier));
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
