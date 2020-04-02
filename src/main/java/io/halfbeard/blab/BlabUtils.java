package io.halfbeard.blab;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class BlabUtils {
    private static final Random RANDOM = new Random();

    private static final BookData[] BOOKS;
    private static final DialogData[] DIALOGS;

    static {
        try {
            BOOKS = loadJson(BookData[].class, "blab:blab-data/stories.json");
            DIALOGS = loadJson(DialogData[].class, "blab:blab-data/dialog.json");
        }
        catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T loadJson(Class<T> clazz, String resource) throws IOException {
        Gson gson = new Gson();
        IResourceManager res = Minecraft.getInstance().getResourceManager();
        try(Reader reader = new InputStreamReader(res.getResource(new ResourceLocation(resource)).getInputStream())) {
            return gson.fromJson(reader, clazz);
        }
    }

    public static void setup() {
    }

    public static BookData randomBookData() {
        return BOOKS[RANDOM.nextInt(BOOKS.length)];
    }

    public static DialogData randomDialogData(String type) {
        DialogData[] options = Arrays.stream(DIALOGS).filter(d -> d.type.equals(type)).toArray(DialogData[]::new);
        return options[RANDOM.nextInt(options.length)];
    }
}
