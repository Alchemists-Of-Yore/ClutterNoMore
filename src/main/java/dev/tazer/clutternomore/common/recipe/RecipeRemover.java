package dev.tazer.clutternomore.common.recipe;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
//? if >=1.21 {
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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
*///?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;

import java.util.*;

public class RecipeRemover {

    //? if >=1.21 {
    private record TagInfo(JsonArray expandedItems, Set<Item> shapes) {}

    public static void removeShapeRecipes(RecipeManager manager) {
        if (ShapeMap.inverseView().isEmpty()) return;

        RecipeManagerAccessor accessor = (RecipeManagerAccessor) manager;

        Map<String, JsonElement> shapeToParentJson = new HashMap<>();
        for (Map.Entry<Item, Item> e : ShapeMap.inverseView().entrySet()) {
            String shapeId = BuiltInRegistries.ITEM.getKey(e.getKey()).toString();
            String parentId = BuiltInRegistries.ITEM.getKey(e.getValue()).toString();
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
        int removed = 0, replaced = 0;
        int dumped = 0;

        ClutterNoMore.LOGGER.info("[CNM] removeShapeRecipes start, tagCache size={}, shapeReplacements size={}",
                tagCache.size(), shapeToParentJson.size());

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
                    removed++;
                    continue;
                }

                var encodeResult = Recipe.CODEC.encodeStart(ops, recipe);
                Optional<JsonElement> encoded = encodeResult.result();
                if (encoded.isEmpty()) {
                    if (encodeResult.error().isPresent()) {
                        ClutterNoMore.LOGGER.warn("[CNM] encode fail for {}: {}", holder.id(), encodeResult.error().get().message());
                    }
                    kept.add(holder);
                    continue;
                }
                JsonElement json = encoded.get();

                String jsonStr = json.toString();
                boolean mentionsTag = jsonStr.contains("wooden_slabs") && jsonStr.contains("\"tag\"");
                boolean shouldDump = mentionsTag && dumped < 5;
                if (shouldDump) {
                    ClutterNoMore.LOGGER.info("[CNM dump pre] {} -> {}", holder.id(), jsonStr);
                    dumped++;
                }

                if (resultItem != null) {
                    Set<String> dangerousIds = new HashSet<>();
                    for (Item s : ShapeMap.getShapes(resultItem)) {
                        if (ShapeMap.inSameShapeSet(s, resultItem)) {
                            dangerousIds.add(BuiltInRegistries.ITEM.getKey(s).toString());
                        }
                    }
                    Set<String> dangerousTags = new HashSet<>();
                    for (Map.Entry<String, TagInfo> entry : tagCache.entrySet()) {
                        for (Item shape : entry.getValue().shapes()) {
                            if (ShapeMap.inSameShapeSet(shape, resultItem)) {
                                dangerousTags.add(entry.getKey());
                                break;
                            }
                        }
                    }
                    if (jsonContainsAnyId(json, dangerousIds, dangerousTags)) {
                        removed++;
                        continue;
                    }
                }

                if (mutateRecipeJson(json, shapeToParentJson, tagCache)) {
                    if (shouldDump) {
                        ClutterNoMore.LOGGER.info("[CNM dump post] {} -> {}", holder.id(), json);
                    }
                    var decodeResult = Recipe.CODEC.parse(ops, json);
                    Optional<Recipe<?>> decoded = decodeResult.result();
                    if (decoded.isPresent()) {
                        kept.add(new RecipeHolder<>(holder.id(), decoded.get()));
                        replaced++;
                        continue;
                    }
                    ClutterNoMore.LOGGER.warn("[CNM] decode fail for {}: {}", holder.id(),
                            decodeResult.error().map(com.mojang.serialization.DataResult.Error::message).orElse("(no error)"));
                }

                kept.add(holder);
            } catch (Exception e) {
                ClutterNoMore.LOGGER.error("Error processing recipe {}: {}", holder.id(), e.getMessage());
                kept.add(holder);
            }
        }

        //? if >=26 {
        accessor.cnm$setRecipeMap(RecipeMap.create(kept));
        //?} else {
        /*manager.replaceRecipes(kept);
        *///?}

        ClutterNoMore.LOGGER.info("[CNM] removeShapeRecipes done. removed={}, replaced={}, kept={}",
                removed, replaced, kept.size());
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

        for (Holder<Item> h : tag) {
            Item item = h.value();
            String id;
            if (ShapeMap.isShape(item)) {
                shapes.add(item);
                id = BuiltInRegistries.ITEM.getKey(ShapeMap.getParent(item)).toString();
            } else {
                id = BuiltInRegistries.ITEM.getKey(item).toString();
            }
            if (seen.add(id)) {
                JsonObject itemObj = new JsonObject();
                itemObj.addProperty("item", id);
                expanded.add(itemObj);
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
                var stacks = display.result().resolveForStacks(context);
                for (var stack : stacks) {
                    if (!stack.isEmpty()) return stack.getItem();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
    //?}

    private static String tagIdOf(JsonElement el) {
        if (el.isJsonObject()) {
            JsonObject obj = el.getAsJsonObject();
            JsonElement t = obj.get("tag");
            if (t != null && t.isJsonPrimitive() && t.getAsJsonPrimitive().isString()) {
                return t.getAsString();
            }
        } else if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
            String s = el.getAsString();
            if (s.startsWith("#")) return s.substring(1);
        }
        return null;
    }

    private static boolean isResultKey(String key) {
        return "result".equals(key) || "results".equals(key);
    }

    private static boolean jsonContainsAnyId(JsonElement el, Set<String> dangerousLiterals, Set<String> dangerousTags) {
        String tagId = tagIdOf(el);
        if (tagId != null && dangerousTags.contains(tagId)) return true;

        if (el.isJsonObject()) {
            for (Map.Entry<String, JsonElement> e : el.getAsJsonObject().entrySet()) {
                if ("type".equals(e.getKey()) || isResultKey(e.getKey())) continue;
                if (jsonContainsAnyId(e.getValue(), dangerousLiterals, dangerousTags)) return true;
            }
        } else if (el.isJsonArray()) {
            for (JsonElement child : el.getAsJsonArray()) {
                if (jsonContainsAnyId(child, dangerousLiterals, dangerousTags)) return true;
            }
        } else if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
            return dangerousLiterals.contains(el.getAsString());
        }
        return false;
    }

    private static JsonElement tryReplaceValue(JsonElement val, Map<String, JsonElement> replacements, Map<String, TagInfo> tagCache) {
        String tagId = tagIdOf(val);
        if (tagId != null) {
            TagInfo info = tagCache.get(tagId);
            if (info != null) return info.expandedItems().deepCopy();
            return null;
        }
        if (val.isJsonPrimitive() && val.getAsJsonPrimitive().isString()) {
            JsonElement repl = replacements.get(val.getAsString());
            if (repl != null) return repl.deepCopy();
        }
        return null;
    }

    private static boolean mutateRecipeJson(JsonElement el, Map<String, JsonElement> replacements, Map<String, TagInfo> tagCache) {
        boolean changed = false;
        if (el.isJsonObject()) {
            var obj = el.getAsJsonObject();
            for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                if ("type".equals(e.getKey()) || isResultKey(e.getKey())) continue;
                JsonElement val = e.getValue();
                JsonElement repl = tryReplaceValue(val, replacements, tagCache);
                if (repl != null) {
                    obj.add(e.getKey(), repl);
                    changed = true;
                } else {
                    changed |= mutateRecipeJson(val, replacements, tagCache);
                }
            }
        } else if (el.isJsonArray()) {
            var arr = el.getAsJsonArray();
            for (int i = 0; i < arr.size(); i++) {
                JsonElement val = arr.get(i);
                JsonElement repl = tryReplaceValue(val, replacements, tagCache);
                if (repl != null) {
                    arr.set(i, repl);
                    changed = true;
                } else {
                    changed |= mutateRecipeJson(val, replacements, tagCache);
                }
            }
        }
        return changed;
    }
    //?} else {
    /*public static void removeShapeRecipes(RecipeManager manager) {
        if (ShapeMap.inverseView().isEmpty()) return;

        int removed = 0, rewritten = 0;
        ArrayList<Recipe<?>> kept = new ArrayList<>();

        for (Recipe<?> recipe : manager.getRecipes()) {
            int outcome = processRecipe(recipe);
            if (outcome == 2) { removed++; continue; }
            if (outcome == 1) rewritten++;
            kept.add(recipe);
        }

        if (removed > 0) manager.replaceRecipes(kept);

        if (removed > 0 || rewritten > 0) {
            ClutterNoMore.LOGGER.info("Removed {} shape recipes, rewrote {} ingredient lists", removed, rewritten);
        }
    }

    private static Ingredient rewriteOrNull(Ingredient ing, Item resultItem, boolean[] removeOut) {
        boolean changed = false;
        List<ItemStack> kept = new ArrayList<>();
        for (ItemStack stack : ing.getItems()) {
            Item item = stack.getItem();
            if (ShapeMap.isShape(item)) {
                Item parent = ShapeMap.getParent(item);
                if (parent == resultItem) { removeOut[0] = true; return null; }
                ItemStack copy = parent.getDefaultInstance();
                copy.setCount(stack.getCount());
                kept.add(copy);
                changed = true;
            } else {
                kept.add(stack);
            }
        }
        if (!changed || kept.isEmpty()) return null;
        return Ingredient.of(kept.stream());
    }

    private static int processRecipe(Recipe<?> recipe) {
        Item result = recipe.getResultItem(RegistryAccess.EMPTY).getItem();
        if (ShapeMap.isShape(result)) return 2;

        boolean[] remove = new boolean[1];
        boolean changed = false;

        if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
            NonNullList<Ingredient> ings = recipe.getIngredients();
            for (int i = 0; i < ings.size(); i++) {
                Ingredient rewritten = rewriteOrNull(ings.get(i), result, remove);
                if (remove[0]) return 2;
                if (rewritten != null) { ings.set(i, rewritten); changed = true; }
            }
        } else if (recipe instanceof SingleItemRecipe single) {
            SingleItemRecipeAccessor acc = (SingleItemRecipeAccessor) single;
            Ingredient rewritten = rewriteOrNull(acc.getInput(), result, remove);
            if (remove[0]) return 2;
            if (rewritten != null) { acc.setInput(rewritten); changed = true; }
        }

        return changed ? 1 : 0;
    }
    *///?}
}
