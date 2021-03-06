package juuxel.tagboard.impl;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.util.Identifier;

import java.util.Set;

public final class Networking {
    // Structure:
    //   size: Int
    //   names: String[]
    public static final Identifier TAG_CRITERIA_S2C_PACKET = Tagboard.id("tag_criteria_s2c");

    public static Packet<?> createTagCriteriaS2C() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        Set<ScoreboardCriterion> criteria = TagCriterionManager.criteria;

        buf.writeInt(criteria.size());
        for (ScoreboardCriterion criterion : criteria) {
            buf.writeString(criterion.getName());
        }
        return ServerSidePacketRegistry.INSTANCE.toPacket(TAG_CRITERIA_S2C_PACKET, buf);
    }
}
