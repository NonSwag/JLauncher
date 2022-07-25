package net.nonswag.tnl.launcher.desktop;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.nonswag.tnl.core.api.file.formats.JsonFile;
import net.nonswag.tnl.core.api.object.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.TreeMap;

import static net.nonswag.tnl.launcher.Screen.COLUMNS;
import static net.nonswag.tnl.launcher.Screen.ROWS;

public class Desktop extends TreeMap<Integer, DesktopEntry> {

    @Nonnull
    private final JsonFile FILE = new JsonFile(".jlauncher", "desktop.jl");

    {
        try {
            if (!FILE.getJsonElement().isJsonArray()) FILE.setJsonElement(new JsonArray());
            JsonArray entries = FILE.getJsonElement().getAsJsonArray();
            for (JsonElement entry : entries) {
                Pair<Integer, DesktopEntry> pair = parse(entry.getAsJsonObject());
                if (entry.isJsonObject()) put(pair.getKey(), pair.nonnull());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    private Pair<Integer, DesktopEntry> parse(@Nonnull JsonObject object) throws FileNotFoundException {
        if (!object.has("index")) throw new NullPointerException("index not defined");
        if (!object.has("name")) throw new NullPointerException("name not defined");
        if (!object.has("icon")) throw new NullPointerException("icon not defined");
        String name = object.get("name").getAsString();
        String icon = object.get("icon").getAsString();
        File file = new File(object.get("file").getAsString());
        int index = object.get("index").getAsInt();
        return new Pair<>(index, new DesktopEntry(name, icon, file));
    }

    public void save() {
        JsonArray array = new JsonArray();
        this.forEach((index, entry) -> {
            JsonObject object = new JsonObject();
            object.addProperty("index", index);
            object.addProperty("name", entry.getName());
            object.addProperty("icon", entry.getIcon());
            object.addProperty("file", entry.getFile().getAbsolutePath());
            array.add(object);
        });
        FILE.setJsonElement(array);
        FILE.save();
    }

    @Nullable
    @Override
    public DesktopEntry put(@Nonnull Integer index, @Nonnull DesktopEntry entry) {
        if (index < ROWS * COLUMNS) return super.put(index, entry);
        throw new IndexOutOfBoundsException("index (%s) >= max (%s)".formatted(index, ROWS * COLUMNS));
    }
}
