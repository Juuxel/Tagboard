package juuxel.moreboard.mixin;

import com.mojang.authlib.GameProfile;
import juuxel.moreboard.api.TagStatRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin extends PlayerEntity {
    private ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "increaseStat", at = @At("RETURN"))
    private void onIncreaseStat(Stat<?> stat, int amount, CallbackInfo info) {
        for (ScoreboardCriterion criterion : TagStatRegistry.getTagCriteria(stat)) {
            getScoreboard().forEachScore(criterion, this.getEntityName(), score -> score.incrementScore(amount));
        }
    }
}
