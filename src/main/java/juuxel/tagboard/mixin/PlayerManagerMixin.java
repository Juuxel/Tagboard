package juuxel.tagboard.mixin;

import juuxel.tagboard.impl.Networking;
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
abstract class PlayerManagerMixin {
    @Shadow
    public abstract void sendToAll(Packet<?> packet);

    @Inject(method = "onDataPacksReloaded", at = @At("RETURN"))
    private void onOnDataPacksReloaded(CallbackInfo info) {
        sendToAll(Networking.createTagCriteriaS2C());
    }

    @Inject(method = "sendScoreboard", at = @At("RETURN"))
    private void onSendScoreboard(ServerScoreboard scoreboard, ServerPlayerEntity player, CallbackInfo info) {
        player.networkHandler.sendPacket(Networking.createTagCriteriaS2C());
    }
}
