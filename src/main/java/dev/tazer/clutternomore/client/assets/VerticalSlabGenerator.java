package dev.tazer.clutternomore.client.assets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.Platform;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static dev.tazer.clutternomore.ClutterNoMore.MODID;
import static dev.tazer.clutternomore.client.assets.AssetGenerator.write;

public final class VerticalSlabGenerator {
    public static ArrayList<ResourceLocation> SLABS = new ArrayList<>();

    public static void generate() {

        for (ResourceLocation id : SLABS) {
            var name = "vertical_" + id.getPath();
            try {
                var blockState = new JsonObject();
                var variants = new JsonObject();
                var resourceManager = Minecraft.getInstance().getResourceManager();

                //blockstate
                var potentialBlockstate = resourceManager.getResource(ClutterNoMore.location(id.getNamespace(), "blockstates/" + name + ".json"));
                if (potentialBlockstate.isEmpty()) {
                    VerticalSlabBlock.FACING.getAllValues().forEach(directionValue -> {
                        VerticalSlabBlock.DOUBLE.getAllValues().forEach(doubleState->{
                            JsonObject model = new JsonObject();
                            var modelString = "clutternomore:block/"+name;
                            if (modelString.contains("waxed"))
                                modelString = modelString.replace("waxed_", "");
                            if (doubleState.value()) {
                                model.addProperty("model", modelString+"_double");
                            } else {
                                model.addProperty("model", modelString);
                            }
                            variants.add(directionValue.toString()+","+doubleState, model);
                            if (directionValue.value().equals(Direction.EAST)) {
                                model.addProperty("y", 90);
                            } else if (directionValue.value().equals(Direction.SOUTH)) {
                                model.addProperty("y", 180);
                            } else if (directionValue.value().equals(Direction.WEST)) {
                                model.addProperty("y", 270);
                            }
                        });
                    });
                    blockState.add("variants", variants);
                    write(AssetGenerator.assets.resolve("blockstates"), "%s.json".formatted(name), blockState.toString());
                }

                // block models
                var potentialSlabModel = resourceManager.getResource(ClutterNoMore.location(id.getNamespace(), "models/block/" + id.getPath() + ".json"));
                if (potentialSlabModel.isPresent()) {
                    JsonObject blockModel = JsonParser.parseReader(potentialSlabModel.get().openAsReader()).getAsJsonObject();
                    blockModel.addProperty("parent", "clutternomore:block/templates/vertical_slab");
                    write(AssetGenerator.assets.resolve("models/block"), name + ".json", blockModel.toString());
                    blockModel.addProperty("parent", "clutternomore:block/templates/vertical_slab_double");
                    write(AssetGenerator.assets.resolve("models/block"), name + "_double.json", blockModel.toString());
                }
                // item models
                var modelString = name;
                if (modelString.contains("waxed"))
                    modelString = modelString.replace("waxed_", "");
                //? if >1.21.4 {
                JsonObject itemState = new JsonObject();
                JsonObject model = new JsonObject();
                model.addProperty("type", "minecraft:model");
                model.addProperty("model", "clutternomore:block/"+modelString);
                itemState.add("model", model);
                write(AssetGenerator.assets.resolve("items") , "%s.json".formatted(name), itemState.toString());
                //?} else {
                /*JsonObject itemModel = new JsonObject();
                itemModel.addProperty("parent", "clutternomore:block/"+modelString);
                write(AssetGenerator.assets.resolve("models/item") , "%s.json".formatted(name), itemModel.toString());
                *///?}
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String langName(String name) {
        String processed = name.replace("_", " ");

        List<String> nonCapital = List.of("of", "and", "with");

        String[] words = processed.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (!nonCapital.contains(word)) result.append(Character.toUpperCase(word.charAt(0)));
                else result.append(word.charAt(0));
                result.append(word.substring(1)).append(" ");
            }
        }

        return result.toString().trim();
    }

    public static String verticalSlabName(String name) {
        name = "vertical_" + name.substring(0, name.length() - 5);

        if (name.endsWith("_block")) name = name.substring(0, name.length() - 6);
        if (name.endsWith("_planks")) name = name.substring(0, name.length() - 7);
        if (name.endsWith("s")) name = name.substring(0, name.length() - 1);
        return name + "_slab";
    }
}

