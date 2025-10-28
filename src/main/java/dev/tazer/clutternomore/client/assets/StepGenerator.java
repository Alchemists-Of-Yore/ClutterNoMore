package dev.tazer.clutternomore.client.assets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.tazer.clutternomore.client.assets.AssetGenerator.getModel;
import static dev.tazer.clutternomore.client.assets.AssetGenerator.write;

public final class StepGenerator {
    public static ArrayList<ResourceLocation> STAIRS = new ArrayList<>();
    public static List<String> STEPS = new ArrayList<>();

    public static void generate() {

        for (ResourceLocation id : STAIRS) {
            var name = id.getPath().replace("stairs", "step");
            try {
                var blockState = new JsonObject();
                var variants = new JsonObject();
                var resourceManager = Minecraft.getInstance().getResourceManager();

                //blockstate
                var potentialBlockstate = resourceManager.getResource(ClutterNoMore.location(id.getNamespace(), "blockstates/" + name + ".json"));
                if (potentialBlockstate.isEmpty()) {
                    StepBlock.FACING.getAllValues().forEach(directionValue -> {
                        StepBlock.SLAB_TYPE.getAllValues().forEach(doubleState->{
                            JsonObject model = new JsonObject();
                            var modelString = "clutternomore:block/"+name;
                            if (modelString.contains("waxed"))
                                modelString = modelString.replace("waxed_", "");
                            model.addProperty("uvlock", true);
                            if (doubleState.value().equals(SlabType.DOUBLE)) {
                                model.addProperty("model", modelString+"_double");
                            } else if (doubleState.value().equals(SlabType.TOP)) {
                                model.addProperty("model", modelString+"_top");
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
                var potentialModel = resourceManager.getResource(ClutterNoMore.location(id.getNamespace(), "models/block/" + name + ".json"));
                if (potentialModel.isEmpty()) {
                    var baseSlabModel = resourceManager.getResource(ClutterNoMore.location(id.getNamespace(), "models/block/" + id.getPath() + ".json"));
                    if (baseSlabModel.isPresent()) {
                        JsonObject blockModel = JsonParser.parseReader(baseSlabModel.get().openAsReader()).getAsJsonObject();
                        blockModel.addProperty("parent", "clutternomore:block/templates/step");
                        write(AssetGenerator.assets.resolve("models/block"), name + ".json", blockModel.toString());
                        blockModel.addProperty("parent", "clutternomore:block/templates/step_double");
                        write(AssetGenerator.assets.resolve("models/block"), name + "_double.json", blockModel.toString());
                        blockModel.addProperty("parent", "clutternomore:block/templates/step_top");
                        write(AssetGenerator.assets.resolve("models/block"), name + "_top.json", blockModel.toString());
                    }
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

    public static String stepName(String name) {
        name = name.substring(0, name.length() - 7);

        if (name.endsWith("_block")) name = name.substring(0, name.length() - 6);
        if (name.endsWith("_planks")) name = name.substring(0, name.length() - 7);
        if (name.endsWith("s")) name = name.substring(0, name.length() - 1);
        return name + "_step";
    }
}

