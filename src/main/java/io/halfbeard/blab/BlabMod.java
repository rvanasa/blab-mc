package io.halfbeard.blab;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import static io.halfbeard.blab.BlabMod.BLAB_MOD;

@Mod(BLAB_MOD)
public class BlabMod {
    public static final String BLAB_MOD = "blab";

    private static final Logger LOGGER = LogManager.getLogger();

    public BlabMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        public static BlabShelf SHELF;

        public static Item ITEM_SHELF;

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            SHELF = new BlabShelf();
            event.getRegistry().register(SHELF.setRegistryName(BLAB_MOD, "shelf"));
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            ITEM_SHELF = new BlockItem(SHELF, new Item.Properties()
                    .maxStackSize(1)
                    .group(ItemGroup.DECORATIONS));
            event.getRegistry().register(ITEM_SHELF.setRegistryName(Objects.requireNonNull(SHELF.getRegistryName())));
        }
    }

//    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
//    public static class BlockEvents {
//
//        @SubscribeEvent
//        public static void onBlockBreak(BlockEvent.BreakEvent event) {
//
//        }
//    }
}
