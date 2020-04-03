package io.halfbeard.blab;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.util.data.audio.AudioPlayer;
import net.minecraft.block.Block;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.AudioInputStream;
import java.util.Random;

import static io.halfbeard.blab.BlabMod.BLAB_MOD;
import static io.halfbeard.blab.BlabMod.EventListeners.*;

@Mod(BLAB_MOD)
public class BlabMod {
    public static final String BLAB_MOD = "blab";

    private static final Logger LOGGER = LogManager.getLogger();

    public BlabMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(EventListeners::livingHurt);///////
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

        private static final Random RANDOM = new Random();
        private static final MaryInterface MARY;
        private static final String[] VOICES;
        private static AudioPlayer prevPlayer;

        static {
            try {
                MARY = new LocalMaryInterface();
                VOICES = MARY.getAvailableVoices().toArray(new String[0]);

                MARY.setVoice(VOICES[0]);
            }
            catch(MaryConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        // TODO refactor
        public static void livingHurt(LivingHurtEvent event) {
            if(event.getEntityLiving().getHealth() <= event.getAmount()) {
                return;
            }

            if(event.getEntityLiving() instanceof VillagerEntity) {
//                VillagerEntity villager = (VillagerEntity) event.getEntityLiving();

                if(prevPlayer != null) {
                    prevPlayer.cancel();
                }

                DialogData data = BlabUtils.randomDialogData("hurt");
                try(AudioInputStream stream = MARY.generateAudio(data.text)) {
                    AudioPlayer player = new AudioPlayer();
                    player.setAudio(stream);
                    player.start();
                    prevPlayer = player;
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @SubscribeEvent
        public static void temp(FMLCommonSetupEvent event) {

            MARY.setVoice(VOICES[RANDOM.nextInt(VOICES.length)]);

            try(AudioInputStream stream = MARY.generateAudio("The bird is the word.")) {
                AudioPlayer player = new AudioPlayer();
                player.setAudio(stream);
                player.start();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
