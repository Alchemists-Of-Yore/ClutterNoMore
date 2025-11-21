package dev.tazer.clutternomore.client.assets;

import com.google.gson.JsonObject;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static dev.tazer.clutternomore.client.assets.AssetGenerator.write;

public final class StepGenerator {
    public static ArrayList<ResourceLocation> STAIRS = new ArrayList<>();

    public static void generate(ResourceManager manager) {
        for (ResourceLocation parent : STAIRS) {
            String namespace = parent.getNamespace() + "/";
            if (parent.getNamespace().equals("minecraft")) namespace = "";

            ResourceLocation shape = ClutterNoMore.location(namespace + parent.getPath().replace("stairs", "step"));

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
        blockModel.addProperty("parent", "clutternomore:block/templates/step");
        blockModel.add("textures", textures);
        write("models/block/%s.json".formatted(shape.getPath()), blockModel);
        blockModel.addProperty("parent", "clutternomore:block/templates/step_double");
        write("models/block/%s_double.json".formatted(shape.getPath()), blockModel);
        blockModel.addProperty("parent", "clutternomore:block/templates/step_top");
        write("models/block/%s_top.json".formatted(shape.getPath()), blockModel);

        JsonObject variants = new JsonObject();
        StepBlock.FACING.getAllValues().forEach(directionValue -> {
            StepBlock.SLAB_TYPE.getAllValues().forEach(doubleState->{
                JsonObject model = new JsonObject();
                String modelString = "clutternomore:block/"+shape.getPath();
                modelString = modelString.replace("waxed_", "");

                model.addProperty("uvlock", true);

                if (doubleState.value().equals(SlabType.DOUBLE)) {
                    model.addProperty("model", modelString+"_double");
                } else if (doubleState.value().equals(SlabType.TOP)) {
                    model.addProperty("model", modelString+"_top");
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

