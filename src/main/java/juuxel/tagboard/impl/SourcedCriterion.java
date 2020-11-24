package juuxel.tagboard.impl;

import net.minecraft.scoreboard.ScoreboardCriterion;
import org.jetbrains.annotations.Nullable;

public final class SourcedCriterion extends ScoreboardCriterion {
    private final Source source;

    SourcedCriterion(String name, Source source) {
        super(name);
        this.source = source;

        if (source == null || source == Source.OTHER) {
            throw new IllegalArgumentException("Invalid source: " + source);
        }
    }

    public static Source getSource(@Nullable ScoreboardCriterion criterion) {
        return criterion instanceof SourcedCriterion ? ((SourcedCriterion) criterion).source : Source.OTHER;
    }

    public enum Source {
        /** Created from a tag on the server. */
        TAG,
        /** Synchronised from the client. */
        SYNC,
        /** Non-Tagboard criterion from another source. */
        OTHER,
    }
}
