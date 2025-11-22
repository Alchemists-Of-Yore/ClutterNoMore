package dev.tazer.clutternomore.common.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

import java.nio.file.Path;

import static dev.tazer.clutternomore.ClutterNoMore.pack;

public class DataGenerator {

    public static JsonArray verticalSlabsArray = new JsonArray();
    public static JsonArray stepsArray = new JsonArray();
    public static JsonArray woodenVerticalSlabsArray = new JsonArray();
    public static JsonArray woodenStepsArray = new JsonArray();
    public static JsonArray pickaxeMineableArray = new JsonArray();
    public static JsonArray shovelMineableArray = new JsonArray();

    public static void addToTag(String path, JsonArray array) {
        addToTag(ClutterNoMore.location(path), array);
    }

    public static void addToTag(ResourceLocation path, JsonArray array) {
        array.add(path.toString());
    }

    public static void generate() {
        blockAndItemTag("vertical_slabs", verticalSlabsArray);
        blockAndItemTag("wooden_vertical_slabs", woodenVerticalSlabsArray);
        blockAndItemTag("steps", stepsArray);
        blockAndItemTag("wooden_steps", woodenStepsArray);
        blockAndItemTag("minecraft","mineable/pickaxe", pickaxeMineableArray);
        blockAndItemTag("minecraft","mineable/shovel", shovelMineableArray);
    }

    private static void blockTag(String s, JsonArray verticalSlabTag) {
        blockTag(ClutterNoMore.MODID, s, verticalSlabTag);
    }

    private static void itemTag(String namespace, String s, JsonArray tagValues) {
        //? if >1.21 {
        var location = ClutterNoMore.location(namespace, "tags/item/" +s + ".json");
         //?} else {
        /*var location = ClutterNoMore.location(namespace, "tags/items/" +s + ".json");
        *///?}
        writeServerData(location, generateTagFile(tagValues));
    }

    private static void blockTag(String namespace, String s, JsonArray tagValues) {
        //? if >1.21 {
        var location = ClutterNoMore.location(namespace, "tags/block/" +s + ".json");
         //?} else {
        /*var location = ClutterNoMore.location(namespace, "tags/blocks/" +s + ".json");
        *///?}
        writeServerData(location, generateTagFile(tagValues));
    }

    private static void itemTag(String path, JsonArray verticalSlabTag) {
        itemTag(ClutterNoMore.MODID, path, verticalSlabTag);
    }

    private static void blockAndItemTag(String path, JsonArray verticalSlabTag) {
        blockTag(path, verticalSlabTag);
        itemTag(path, verticalSlabTag);
    }

    private static void blockAndItemTag(String namespace, String path, JsonArray verticalSlabTag) {
        blockTag(namespace, path, verticalSlabTag);
        itemTag(namespace, path, verticalSlabTag);
    }

    private static JsonObject generateTagFile(JsonArray tagValues) {
        JsonObject tag = new JsonObject();
        JsonArray values = new JsonArray();
        tagValues.forEach((value)->{
            var tagElement = new JsonObject();
            tagElement.add("id", value);
            tagElement.addProperty("required", false);
            values.add(tagElement);
        });
        tag.add("values", values);
        return tag;
    }

    public static void addLootTable(ResourceLocation block, ResourceLocation shape) {
        JsonObject lootTable = new JsonObject();
        lootTable.add("type", new JsonPrimitive("block"));
        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.add("rolls", new JsonPrimitive(1));
        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.add("type", new JsonPrimitive("loot_table"));
        entry.add("value", new JsonPrimitive(block.withPrefix("blocks/").toString()));
        entries.add(entry);
        pool.add("entries", entries);
        pools.add(pool);
        lootTable.add("pools", pools);
        //? if >1.21 {
        var path = "loot_table";
         //?} else {
        /*var path = "loot_tables";
        *///?}
        writeServerData(("%s/blocks/%s.json".formatted(path, shape.getPath())), lootTable);
    }

    public static void writeServerData(ResourceLocation fileName, JsonElement contents) {
        ClutterNoMore.RESOURCES.addJson(PackType.SERVER_DATA, fileName, contents);
        if (ClutterNoMore.STARTUP_CONFIG.RUNTIME_DATA_GENERATION.value()) {
            Path data = pack.resolve("data/clutternomore");
            ClutterNoMore.writeFile(data.resolve(fileName.getPath().substring(0, fileName.getPath().lastIndexOf("/"))), data.resolve(fileName.getPath()), contents.toString());
        }
    }

    public static void writeServerData(String fileName, JsonElement contents) {
        writeServerData(ClutterNoMore.location(fileName), contents);
    }
}
