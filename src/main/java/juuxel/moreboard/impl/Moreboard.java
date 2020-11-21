package juuxel.moreboard.impl;

import juuxel.moreboard.api.TagStatRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceType;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class Moreboard implements ModInitializer {
    @Override
    public void onInitialize() {
        TagStatRegistry.register(Stats.MINED, Registry.BLOCK, Moreboard::getBlockTags);
        TagStatRegistry.register(Stats.CRAFTED, Registry.ITEM, Moreboard::getItemTags);
        TagStatRegistry.register(Stats.USED, Registry.ITEM, Moreboard::getItemTags);
        TagStatRegistry.register(Stats.BROKEN, Registry.ITEM, Moreboard::getItemTags);
        TagStatRegistry.register(Stats.PICKED_UP, Registry.ITEM, Moreboard::getItemTags);
        TagStatRegistry.register(Stats.DROPPED, Registry.ITEM, Moreboard::getItemTags);
        TagStatRegistry.register(Stats.KILLED, Registry.ENTITY_TYPE, Moreboard::getEntityTags);
        TagStatRegistry.register(Stats.KILLED_BY, Registry.ENTITY_TYPE, Moreboard::getEntityTags);

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new CriterionRebuilder());
    }

    public static Identifier id(String path) {
        return new Identifier("moreboard", path);
    }

    private static TagGroup<Block> getBlockTags() {
        return ServerTagManagerHolder.getTagManager().getBlocks();
    }

    private static TagGroup<Item> getItemTags() {
        return ServerTagManagerHolder.getTagManager().getItems();
    }

    private static TagGroup<EntityType<?>> getEntityTags() {
        return ServerTagManagerHolder.getTagManager().getEntityTypes();
    }
}
