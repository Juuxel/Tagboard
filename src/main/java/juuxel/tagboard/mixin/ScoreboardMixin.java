package juuxel.tagboard.mixin;

import juuxel.tagboard.impl.TagObjectiveManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.stat.Stat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Scoreboard.class)
abstract class ScoreboardMixin {
    @Shadow
    public abstract void forEachScore(ScoreboardCriterion criterion, String player, Consumer<ScoreboardPlayerScore> action);

    @Inject(method = "forEachScore", at = @At("RETURN"))
    private void onForEachScore(ScoreboardCriterion criterion, String player, Consumer<ScoreboardPlayerScore> action, CallbackInfo info) {
        if (criterion instanceof Stat<?>) {
            for (ScoreboardCriterion tagCriterion : TagObjectiveManager.getTagCriteria((Stat<?>) criterion)) {
                forEachScore(tagCriterion, player, action);
            }
        }
    }
}
