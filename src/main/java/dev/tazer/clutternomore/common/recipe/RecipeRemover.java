package dev.tazer.clutternomore.common.recipe;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
//? if >=1.21 {
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.tazer.clutternomore.common.mixin.recipe.RecipeManagerAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.crafting.RecipeHolder;
//?}
//? if >=26 {
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
//?}
//? if <1.21 {
/*import dev.tazer.clutternomore.common.mixin.recipe.SingleItemRecipeAccessor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
*///?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.*;

public class RecipeRemover {

    //? if >=1.21 {
    private record TagInfo(JsonArray expandedItems, Set<Item> shapes) {}

    public static void removeShapeRecipes(RecipeManager manager) {
        if (!ClutterNoMore.STARTUP_CONFIG.RECIPE_FIXES.value()) return;
        if (ShapeMap.inverseView().isEmpty()) return;

        RecipeManagerAccessor accessor = (RecipeManagerAccessor) manager;

        Map<String, JsonElement> shapeToParentJson = new HashMap<>();
        for (Map.Entry<Item, Item> entry : ShapeMap.inverseView().entrySet()) {
            String shapeId = BuiltInRegistries.ITEM.getKey(entry.getKey()).toString();
            String parentId = BuiltInRegistries.ITEM.getKey(entry.getValue()).toString();
            shapeToParentJson.put(shapeId, new JsonPrimitive(parentId));
        }

        Map<String, TagInfo> tagCache = buildTagCache();

        RegistryOps<JsonElement> ops = accessor.cnm$getRegistries().createSerializationContext(JsonOps.INSTANCE);

        //? if >=26 {
        ContextMap displayContext = new ContextMap.Builder().create(SlotDisplayContext.CONTEXT);
        RecipeMap currentMap = accessor.cnm$getRecipeMap();
        Iterable<RecipeHolder<?>> source = currentMap.values();
        //?} else {
        /*Iterable<RecipeHolder<?>> source = manager.getRecipes();
        *///?}

        List<RecipeHolder<?>> kept = new ArrayList<>();
        boolean detailedLogs = ClutterNoMore.STARTUP_CONFIG.DETAILED_LOGS.value();
        int encodeFailures = 0;

        for (RecipeHolder<?> holder : source) {
            try {
                Recipe<?> recipe = holder.value();
                Item resultItem;
                //? if >=26 {
                resultItem = getResultItem(recipe, displayContext);
                //?} else {
                /*resultItem = recipe.getResultItem(accessor.cnm$getRegistries()).getItem();
                *///?}

                if (resultItem != null && ShapeMap.isShape(resultItem)) {
                    continue;
                }

                DataResult<JsonElement> encodeResult = Recipe.CODEC.encodeStart(ops, recipe);
                Optional<JsonElement> encoded = encodeResult.result();
                if (encoded.isEmpty()) {
                    if (encodeResult.error().isPresent()) {
                        encodeFailures++;
                        if (detailedLogs) ClutterNoMore.LOGGER.warn("[CNM] encode fail for {}: {}", holder.id(), encodeResult.error().get().message());
                    }
                    kept.add(holder);
                    continue;
                }
                JsonElement json = encoded.get();

                if (resultItem != null) {
                    Set<String> dangerousIds = new HashSet<>();
                    for (Item shape : ShapeMap.getShapes(resultItem)) {
                        if (ShapeMap.inSameShapeSet(shape, resultItem)) {
                            dangerousIds.add(BuiltInRegistries.ITEM.getKey(shape).toString());
                        }
                    }
                    Set<String> dangerousTags = new HashSet<>();
                    String resultId = BuiltInRegistries.ITEM.getKey(resultItem).toString();
                    for (Map.Entry<String, TagInfo> entry : tagCache.entrySet()) {
                        JsonArray expanded = entry.getValue().expandedItems();
                        if (expanded.size() == 0) continue;
                        boolean allCollapseToResult = true;
                        for (JsonElement element : expanded) {
                            if (!element.getAsJsonObject().get("item").getAsString().equals(resultId)) {
                                allCollapseToResult = false;
                                break;
                            }
                        }
                        if (allCollapseToResult) {
                            dangerousTags.add(entry.getKey());
                        }
                    }
                    if (jsonContainsAnyId(json, dangerousIds, dangerousTags)) {
                        continue;
                    }
                }

                if (mutateRecipeJson(json, shapeToParentJson, tagCache)) {
                    DataResult<Recipe<?>> decodeResult = Recipe.CODEC.parse(ops, json);
                    Optional<Recipe<?>> decoded = decodeResult.result();
                    if (decoded.isPresent()) {
                        kept.add(new RecipeHolder<>(holder.id(), decoded.get()));
                        continue;
                    }
                }

                kept.add(holder);
            } catch (Exception exception) {
                ClutterNoMore.LOGGER.error("Error processing recipe {}: {}", holder.id(), exception.getMessage());
                kept.add(holder);
            }
        }

        if (encodeFailures > 0) {
            ClutterNoMore.LOGGER.info("[CNM] {} recipes could not be re-encoded for shape processing and were kept unchanged (enable detailed logs to list them)", encodeFailures);
        }

        //? if >=26 {
        accessor.cnm$setRecipeMap(RecipeMap.create(kept));
        //?} else {
        /*manager.replaceRecipes(kept);
        *///?}
    }

    private static Map<String, TagInfo> buildTagCache() {
        Map<String, TagInfo> result = new HashMap<>();
        //? if >=26 {
        BuiltInRegistries.ITEM.getTags().forEach(tag -> processTag(tag, result));
        //?} else {
        /*BuiltInRegistries.ITEM.getTags().forEach(pair -> processTag(pair.getSecond(), result));
        *///?}
        return result;
    }

    private static void processTag(HolderSet.Named<Item> tag, Map<String, TagInfo> out) {
        Set<Item> shapes = new HashSet<>();
        Set<String> seen = new LinkedHashSet<>();
        JsonArray expanded = new JsonArray();

        for (Holder<Item> holder : tag) {
            Item item = holder.value();
            String id;
            if (ShapeMap.isShape(item)) {
                shapes.add(item);
                id = BuiltInRegistries.ITEM.getKey(ShapeMap.getParent(item)).toString();
            } else {
                id = BuiltInRegistries.ITEM.getKey(item).toString();
            }
            if (seen.add(id)) {
                JsonObject itemObject = new JsonObject();
                itemObject.addProperty("item", id);
                expanded.add(itemObject);
            }
        }

        if (!shapes.isEmpty()) {
            out.put(tag.key().location().toString(), new TagInfo(expanded, shapes));
        }
    }

    //? if >=26 {
    private static Item getResultItem(Recipe<?> recipe, ContextMap context) {
        try {
            for (RecipeDisplay display : recipe.display()) {
                for (var stack : display.result().resolveForStacks(context)) {
                    if (!stack.isEmpty()) return stack.getItem();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
    //?}

    private static String tagIdOf(JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            JsonElement tagField = object.get("tag");
            if (tagField != null && tagField.isJsonPrimitive() && tagField.getAsJsonPrimitive().isString()) {
                return tagField.getAsString();
            }
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String text = element.getAsString();
            if (text.startsWith("#")) return text.substring(1);
        }
        return null;
    }

    private static boolean isResultKey(String key) {
        return "result".equals(key) || "results".equals(key);
    }

    private static boolean jsonContainsAnyId(JsonElement element, Set<String> dangerousLiterals, Set<String> dangerousTags) {
        String tagId = tagIdOf(element);
        if (tagId != null && dangerousTags.contains(tagId)) return true;

        if (element.isJsonObject()) {
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                if ("type".equals(entry.getKey()) || isResultKey(entry.getKey())) continue;
                if (jsonContainsAnyId(entry.getValue(), dangerousLiterals, dangerousTags)) return true;
            }
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) {
                if (jsonContainsAnyId(child, dangerousLiterals, dangerousTags)) return true;
            }
        } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return dangerousLiterals.contains(element.getAsString());
        }
        return false;
    }

    private static JsonElement tryReplaceValue(JsonElement value, Map<String, JsonElement> replacements, Map<String, TagInfo> tagCache) {
        String tagId = tagIdOf(value);
        if (tagId != null) {
            TagInfo info = tagCache.get(tagId);
            if (info != null) return info.expandedItems().deepCopy();
            return null;
        }
        if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
            JsonElement replacement = replacements.get(value.getAsString());
            if (replacement != null) return replacement.deepCopy();
        }
        return null;
    }

    private static boolean mutateRecipeJson(JsonElement element, Map<String, JsonElement> replacements, Map<String, TagInfo> tagCache) {
        boolean changed = false;
        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                if ("type".equals(entry.getKey()) || isResultKey(entry.getKey())) continue;
                JsonElement value = entry.getValue();
                JsonElement replacement = tryReplaceValue(value, replacements, tagCache);
                if (replacement != null) {
                    object.add(entry.getKey(), replacement);
                    changed = true;
                } else {
                    changed |= mutateRecipeJson(value, replacements, tagCache);
                }
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                JsonElement value = array.get(i);
                JsonElement replacement = tryReplaceValue(value, replacements, tagCache);
                if (replacement != null) {
                    array.set(i, replacement);
                    changed = true;
                } else {
                    changed |= mutateRecipeJson(value, replacements, tagCache);
                }
            }
        }
        return changed;
    }
    //?} else {
    /*public static void removeShapeRecipes(RecipeManager manager, RegistryAccess registries) {
        if (!ClutterNoMore.STARTUP_CONFIG.RECIPE_FIXES.value()) return;
        if (ShapeMap.inverseView().isEmpty()) return;

        boolean removed = false;
        ArrayList<Recipe<?>> kept = new ArrayList<>();

        for (Recipe<?> recipe : manager.getRecipes()) {
            int outcome = processRecipe(recipe, registries);
            if (outcome == 2) {
                removed = true;
                continue;
            }
            kept.add(recipe);
        }

        if (removed) manager.replaceRecipes(kept);
    }

    private static Ingredient rewriteOrNull(Ingredient ingredient, Item resultItem, boolean[] removeOut) {
        boolean changed = false;
        List<ItemStack> kept = new ArrayList<>();
        for (ItemStack stack : ingredient.getItems()) {
            Item item = stack.getItem();
            if (ShapeMap.isShape(item)) {
                Item parent = ShapeMap.getParent(item);
                if (parent == resultItem) { removeOut[0] = true; return null; }
                kept.add(ShapeMap.transferStack(stack, 0));
                changed = true;
            } else {
                kept.add(stack);
            }
        }
        if (!changed || kept.isEmpty()) return null;
        return Ingredient.of(kept.stream());
    }

    private static int processRecipe(Recipe<?> recipe, RegistryAccess registries) {
        Item result = null;
        try {
            ItemStack resultStack = recipe.getResultItem(registries);
            if (resultStack != null && !resultStack.isEmpty()) result = resultStack.getItem();
        } catch (Throwable throwable) {
            ClutterNoMore.LOGGER.debug("getResultItem failed for {}: {}", recipe.getClass().getSimpleName(), throwable.getMessage());
        }

        if (result != null && ShapeMap.isShape(result)) return 2;

        boolean[] remove = new boolean[1];
        boolean changed = false;

        if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient rewritten = rewriteOrNull(ingredients.get(i), result, remove);
                if (remove[0]) return 2;
                if (rewritten != null) { ingredients.set(i, rewritten); changed = true; }
            }
        } else if (recipe instanceof SingleItemRecipe single) {
            SingleItemRecipeAccessor accessor = (SingleItemRecipeAccessor) single;
            Ingredient rewritten = rewriteOrNull(accessor.getInput(), result, remove);
            if (remove[0]) return 2;
            if (rewritten != null) { accessor.setInput(rewritten); changed = true; }
        }

        return changed ? 1 : 0;
    }
    *///?}
}
