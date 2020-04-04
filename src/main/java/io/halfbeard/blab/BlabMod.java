package io.halfbeard.blab;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static io.halfbeard.blab.BlabMod.BLAB_MOD;

@Mod(BLAB_MOD)
public class BlabMod {
    public static final String BLAB_MOD = "blab";

    private static final Logger LOGGER = LogManager.getLogger();

    public BlabMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(EventListeners::livingHurt);///////
        MinecraftForge.EVENT_BUS.addListener(EventListeners::livingUpdate);///////
        MinecraftForge.EVENT_BUS.addListener(EventListeners::livingInteract);///////
        MinecraftForge.EVENT_BUS.addListener(EventListeners::livingJump);///////
    }

    private void setup(FMLCommonSetupEvent event) {
        BlabUtils.setup();
    }

    public static BlabShelf SHELF;

    public static Item ITEM_SHELF;

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EventListeners {

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
            event.getRegistry().register(ITEM_SHELF.setRegistryName(SHELF.getRegistryName()));
        }

        @SubscribeEvent
        public static void temp(FMLCommonSetupEvent event) throws Exception {
            BlabUtils.startDialogText("The bird is the word.");
        }

        // TODO refactor
        public static void livingHurt(LivingHurtEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if(event.getEntityLiving().getHealth() <= event.getAmount()) {
                BlabUtils.stopDialog(event.getEntityLiving());
                return;
            }

            BlabUtils.playRandomDialog(entity.getType().getRegistryName() + " hurt", entity);

            Entity source = event.getSource().getTrueSource();
            if(source != null) {
                BlabUtils.playRandomDialog(entity.getType().getRegistryName() + " attack", entity);
                BlabUtils.playRandomDialog(source.getType().getRegistryName() + " attack " + entity.getType().getRegistryName(), entity);
            }
        }

        private static final Random RANDOM = new Random();

        // TODO refactor
        public static void livingUpdate(LivingEvent.LivingUpdateEvent event) {
            LivingEntity entity = event.getEntityLiving();
            if(RANDOM.nextInt(10000) == 0) {
                BlabUtils.playRandomDialog(entity.getType().getRegistryName() + " idle", entity);
            }
        }

        // TODO refactor
        public static void livingInteract(PlayerInteractEvent.EntityInteract event) {
            LivingEntity entity = event.getEntityLiving();
            BlabUtils.playRandomDialog(entity.getType().getRegistryName() + " interact", entity);
            //todo fix
        }

        // TODO refactor
        public static void livingJump(LivingEvent.LivingJumpEvent event) {
            LivingEntity entity = event.getEntityLiving();
            BlabUtils.playRandomDialog(entity.getType().getRegistryName() + " jump", entity);
        }
    }
}
