package io.halfbeard.blab;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.SynthesisException;
import marytts.util.data.audio.AudioPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.sound.sampled.AudioInputStream;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class BlabUtils {
    private static final Random RANDOM = new Random();
    private static final IResourceManager RESOURCES = Minecraft.getInstance().getResourceManager();

    private static final BookData[] BOOKS;
    private static final Multimap<String, DialogData> DIALOG_MAP;

    private static final MaryInterface MARY;
    private static final String[] VOICES;
    private static final WeakHashMap<Entity, AudioPlayer> AUDIO_MAP = new WeakHashMap<>();

    static {
        try {
            BOOKS = loadAllJson(BookData[].class, "blab-data/stories").stream()
                    .flatMap(Arrays::stream)
                    .toArray(BookData[]::new);
            //noinspection UnstableApiUsage
            DIALOG_MAP = MultimapBuilder.hashKeys().arrayListValues().build();
            loadAllJson(DialogGroupData[].class, "blab-data/dialog").stream()
                    .flatMap(Arrays::stream)
                    .forEach(g -> {
                        for(DialogData d : g.items) {
                            if(d.filter == null) {
                                d.filter = g.filter;
                            } else {
                                d.filter = Stream.concat(Arrays.stream(g.filter), Arrays.stream(d.filter))
                                        .toArray(String[]::new);
                            }
                            for(String type : d.filter) {
                                DIALOG_MAP.put(type, d);
                            }
                        }
                    });

            MARY = new LocalMaryInterface();
            VOICES = MARY.getAvailableVoices().toArray(new String[0]);

            MARY.setVoice(VOICES[0]);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadJson(Class<T> clazz, String resource) throws IOException {
        return loadJson(clazz, RESOURCES.getResource(new ResourceLocation(resource)));
    }

    public static <T> T loadJson(Class<T> clazz, IResource resource) throws IOException {
        Gson gson = new Gson();
        try(Reader reader = new InputStreamReader(resource.getInputStream())) {
            return gson.fromJson(reader, clazz);
        }
    }

    public static <T> List<T> loadAllJson(Class<T> clazz, String resourceDir) throws IOException {
        List<T> list = new ArrayList<>();
        for(ResourceLocation resource : RESOURCES.getAllResourceLocations(resourceDir, p -> p.endsWith(".json"))) {
            list.add(loadJson(clazz, RESOURCES.getResource(resource)));
        }
        return list;
    }

    public static void setup() {
    }

    public static BookData randomBookData() {
        return BOOKS.length == 0 ? null : BOOKS[RANDOM.nextInt(BOOKS.length)];
    }

    public static DialogData randomDialogData(String type) {
        DialogData[] options = DIALOG_MAP.get(type).toArray(new DialogData[0]);
        return options.length == 0 ? null : options[RANDOM.nextInt(options.length)];
    }

    public static void playRandomDialog(String type, Entity entity) {
//        System.out.println(type);//

        DialogData data = BlabUtils.randomDialogData(type);
        if(data == null) {
            return;
        }
        try {
            stopDialog(entity);
            AUDIO_MAP.put(entity, startDialogText(data.voice));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopDialog(Entity entity) {
        AudioPlayer audio = AUDIO_MAP.get(entity);
        if(audio != null) {
            audio.cancel();
        }
    }

    public static AudioPlayer startDialogText(String text) throws IOException, SynthesisException {
        try(AudioInputStream stream = MARY.generateAudio(text)) {
            AudioPlayer audio = new AudioPlayer();
            audio.setAudio(stream);
            audio.start();
            return audio;
        }
    }
}
