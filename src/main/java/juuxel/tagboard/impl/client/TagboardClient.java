package juuxel.tagboard.impl.client;

import juuxel.tagboard.impl.Networking;
import juuxel.tagboard.impl.SourcedCriterion;
import juuxel.tagboard.impl.TagCriterionManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.scoreboard.ScoreboardCriterion;

import java.util.Set;

@Environment(EnvType.CLIENT)
public final class TagboardClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(Networking.TAG_CRITERIA_S2C_PACKET, (context, buf) -> {
            int size = buf.readInt();
            String[] names = new String[size];
            for (int i = 0; i < size; i++) {
                names[i] = buf.readString();
            }

            context.getTaskQueue().execute(() -> {
                // Delete existing synced criteria
                deleteSyncedCriteria();

                // ...and recreate any missing ones
                for (String name : names) {
                    TagCriterionManager.getCriterion(name, SourcedCriterion.Source.SYNC);
                }
            });
        });
    }

    public static void deleteSyncedCriteria() {
        ScoreboardCriterion.OBJECTIVES.entrySet().removeIf(entry -> {
            SourcedCriterion.Source source = SourcedCriterion.getSource(entry.getValue());
            return source == SourcedCriterion.Source.SYNC;
        });
    }
}
