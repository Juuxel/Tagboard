package juuxel.tagboard.impl.client;

import juuxel.tagboard.impl.Networking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class TagboardClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Networking.initClient();
    }
}
