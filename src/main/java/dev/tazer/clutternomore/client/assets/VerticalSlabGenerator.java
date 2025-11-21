package dev.tazer.clutternomore.client.assets;

import com.google.gson.JsonObject;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static dev.tazer.clutternomore.client.assets.AssetGenerator.write;

public final class VerticalSlabGenerator {
    public static ArrayList<ResourceLocation> SLABS = new ArrayList<>();

    public static void generate(ResourceManager manager) {
        for (ResourceLocation parent : SLABS) {
            String namespace = parent.getNamespace() + "/";
            if (parent.getNamespace().equals("minecraft")) namespace = "";

            ResourceLocation shape = ClutterNoMore.location(namespace + "vertical_" + parent.getPath());

            try {
                generateBlock(parent, shape, manager);
                AssetGenerator.generateItem(shape, manager);
            } catch (IOException e) {
                ClutterNoMore.LOGGER.catching(e);
                throw new RuntimeException(e);
            }
        }
    }

    public static void generateBlock(ResourceLocation parent, ResourceLocation shape, ResourceManager manager) throws IOException {
        Optional<Resource> existingBlockState = manager.getResource(shape.withPrefix("blockstates/").withSuffix(".json"));
        if (existingBlockState.isPresent()) return;

        JsonObject textures = AssetGenerator.getTextures(manager, parent);
        if (textures == null) return;

        JsonObject blockModel = new JsonObject();
        blockModel.addProperty("parent", "clutternomore:block/templates/vertical_slab");
        blockModel.add("textures", textures);
        write("models/block/%s.json".formatted(shape.getPath()), blockModel);
        blockModel.addProperty("parent", "clutternomore:block/templates/vertical_slab_double");
        write("models/block/%s_double.json".formatted(shape.getPath()), blockModel);

        JsonObject variants = new JsonObject();
        VerticalSlabBlock.FACING.getAllValues().forEach(directionValue -> {
            VerticalSlabBlock.DOUBLE.getAllValues().forEach(doubleState->{
                JsonObject model = new JsonObject();
                String modelString = "clutternomore:block/"+shape.getPath();
                modelString = modelString.replace("waxed_", "");

                if (doubleState.value()) {
                    model.addProperty("model", modelString+"_double");
                } else {
                    model.addProperty("model", modelString);
                }

                if (directionValue.value().equals(Direction.EAST)) {
                    model.addProperty("y", 90);
                } else if (directionValue.value().equals(Direction.SOUTH)) {
                    model.addProperty("y", 180);
                } else if (directionValue.value().equals(Direction.WEST)) {
                    model.addProperty("y", 270);
                }

                variants.add(directionValue+","+doubleState, model);
            });
        });

        JsonObject blockState = new JsonObject();
        blockState.add("variants", variants);
        write("blockstates/%s.json".formatted(shape.getPath()), blockState);
    }
}

