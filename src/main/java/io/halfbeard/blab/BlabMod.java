package io.halfbeard.blab;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.modules.synthesis.Voice;
import marytts.util.data.audio.AudioPlayer;
import net.minecraft.block.Block;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.audio.AudioStream;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Random;

import static io.halfbeard.blab.BlabMod.BLAB_MOD;
import static io.halfbeard.blab.BlabUtils.randomDialogData;

@Mod(BLAB_MOD)
public class BlabMod {
    public static final String BLAB_MOD = "blab";

    private static final Logger LOGGER = LogManager.getLogger();

    public BlabMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
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

        static {
            try {
                MARY = new LocalMaryInterface();
                VOICES = MARY.getAvailableVoices().toArray(new String[0]);
            }
            catch(MaryConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        @SubscribeEvent
        public static void livingHurt(LivingHurtEvent event) {

            LOGGER.error("$#^$%&$%&$%^&^%*$%^*%$*%$*%^*%^*$%*%$" + event.getSource() + " " + event.getEntityLiving());

            if(event.getEntityLiving() instanceof VillagerEntity) {
                VillagerEntity villager = (VillagerEntity) event.getEntityLiving();

                MARY.setVoice(VOICES[RANDOM.nextInt(VOICES.length)]);

                DialogData data = BlabUtils.randomDialogData("hurt");
                try(AudioInputStream stream = MARY.generateAudio(data.text)) {
                    AudioPlayer player = new AudioPlayer();
                    player.setAudio(stream);
                    player.start();
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }

            event.getEntityLiving().setHealth(0);
        }

        @SubscribeEvent
        public static void temp2(PlayerInteractEvent event) {
                MARY.setVoice(VOICES[RANDOM.nextInt(VOICES.length)]);

                DialogData data = BlabUtils.randomDialogData("hurt");
                try(AudioInputStream stream = MARY.generateAudio(data.text)) {
                    AudioPlayer player = new AudioPlayer();
                    player.setAudio(stream);
                    player.start();
                }
                catch(Exception e) {
                    e.printStackTrace();
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
