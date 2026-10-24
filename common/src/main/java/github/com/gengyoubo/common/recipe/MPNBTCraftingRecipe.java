package github.com.gengyoubo.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;

import com.mojang.serialization.MapLike;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.core.Registry;

import net.minecraft.core.NonNullList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MPNBTCraftingRecipe implements CraftingRecipe {
    private static RecipeSerializer<?> registeredSerializer;


    private net.minecraft.resources.ResourceLocation id;
    @Override public net.minecraft.resources.ResourceLocation getId() { return id; }
    private final String group;
    private final int width;
    private final int height;
    private final IngredientSpec[] ingredients;
    private final ItemStack result;
    private final boolean showNotification;

    public MPNBTCraftingRecipe(String group, int width, int height, IngredientSpec[] ingredients, ItemStack result, boolean showNotification) {
        this.group = group;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.result = result;
        this.showNotification = showNotification;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        if (registeredSerializer == null) {
            throw new IllegalStateException("Manaita NBT crafting serializer is not registered yet");
        }
        return registeredSerializer;
    }

    @Override
    public @NotNull String getGroup() {
        return group;
    }



    @Override
    public @NotNull ItemStack getResultItem() {
        return result.copy();
    }

    public boolean showNotification() {
        return showNotification;
    }

    @Override
    public boolean canCraftInDimensions(int gridWidth, int gridHeight) {
        return gridWidth >= width && gridHeight >= height;
    }

    @Override
    public boolean matches(CraftingContainer container, @NotNull Level level) {
        for (int x = 0; x <= container.getWidth() - width; ++x) {
            for (int y = 0; y <= container.getHeight() - height; ++y) {
                if (matches(container, x, y, true) || matches(container, x, y, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matches(CraftingContainer container, int offsetX, int offsetY, boolean mirrored) {
        for (int x = 0; x < container.getWidth(); ++x) {
            for (int y = 0; y < container.getHeight(); ++y) {
                int patternX = x - offsetX;
                int patternY = y - offsetY;
                IngredientSpec expected = IngredientSpec.EMPTY;
                if (patternX >= 0 && patternY >= 0 && patternX < width && patternY < height) {
                    if (mirrored) {
                        expected = ingredients[width - patternX - 1 + patternY * width];
                    } else {
                        expected = ingredients[patternX + patternY * width];
                    }
                }

                if (!expected.test(container.getItem(x + y * container.getWidth()))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer container) {
        return result.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> displayIngredients = NonNullList.createWithCapacity(this.ingredients.length);
        for (IngredientSpec ingredient : this.ingredients) {
            displayIngredients.add(ingredient.toDisplayIngredient());
        }
        return displayIngredients;
    }

    public static class Serializer implements RecipeSerializer<MPNBTCraftingRecipe> {
        public Serializer() {
            registeredSerializer = this;
        }
        @Override
        public MPNBTCraftingRecipe fromJson(ResourceLocation id, JsonObject json) {
            MPNBTCraftingRecipe recipe = decodeRecipe(JsonOps.INSTANCE, JsonOps.INSTANCE.getMap(json)
                    .getOrThrow(false, message -> { throw new JsonSyntaxException(message); }))
                    .getOrThrow(false, message -> { throw new JsonSyntaxException(message); });
            recipe.id = id;
            return recipe;
        }

        @Override
        public MPNBTCraftingRecipe fromNetwork(ResourceLocation id, net.minecraft.network.FriendlyByteBuf buf) {
            int width = buf.readVarInt();
            int height = buf.readVarInt();
            String group = buf.readUtf();
            IngredientSpec[] ingredients = new IngredientSpec[width * height];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = new IngredientSpec(Ingredient.fromNetwork(buf), buf.readInt());
            }
            MPNBTCraftingRecipe recipe = new MPNBTCraftingRecipe(group, width, height,
                    ingredients, buf.readItem(), buf.readBoolean());
            recipe.id = id;
            return recipe;
        }

        @Override
        public void toNetwork(net.minecraft.network.FriendlyByteBuf buf, MPNBTCraftingRecipe recipe) {
            buf.writeVarInt(recipe.width);
            buf.writeVarInt(recipe.height);
            buf.writeUtf(recipe.group);
            for (IngredientSpec ingredient : recipe.ingredients) {
                ingredient.ingredient().toNetwork(buf);
                buf.writeInt(ingredient.requiredType());
            }
            buf.writeItem(recipe.result);
            buf.writeBoolean(recipe.showNotification);
        }

        private static <T> DataResult<MPNBTCraftingRecipe> decodeRecipe(DynamicOps<T> ops, MapLike<T> input) {
            try {
                JsonObject json = toJsonObject(ops, input);
                String group = GsonHelper.getAsString(json, "group", "");
                String[] pattern = shrink(patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
                if (pattern.length == 0) {
                    throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
                }
                int width = pattern[0].length();
                int height = pattern.length;
                Map<Character, IngredientSpec> key = keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
                IngredientSpec[] ingredients = dissolvePattern(pattern, key, width, height);
                ItemStack result = resultFromJson(GsonHelper.getAsJsonObject(json, "result"));
                boolean showNotification = GsonHelper.getAsBoolean(json, "show_notification", true);
                return DataResult.success(new MPNBTCraftingRecipe(group, width, height, ingredients, result, showNotification));
            } catch (Exception exception) {
                return DataResult.error(exception.getMessage());
            }
        }

        private static ItemStack resultFromJson(JsonObject json) {
            String itemId = GsonHelper.getAsString(json, json.has("item") ? "item" : "id");
            ResourceLocation id = new ResourceLocation(itemId);
            if (!Registry.ITEM.containsKey(id)) throw new JsonSyntaxException("Unknown item: " + id);
            ItemStack stack = new ItemStack(Registry.ITEM.get(id), GsonHelper.getAsInt(json, "count", 1));
            if (json.has("nbt")) {
                try {
                    JsonElement nbt = json.get("nbt");
                    stack.setTag(net.minecraft.nbt.TagParser.parseTag(nbt.isJsonPrimitive() ? nbt.getAsString() : nbt.toString()));
                    net.minecraft.nbt.CompoundTag tag = stack.getTag();
                    for (String key : new String[]{MPGNBTData.ItemType, "ManaitaPlusLegacyType", "ManaitaType"}) {
                        if (tag.contains(key)) {
                            tag.putInt(MPGNBTData.ItemType, tag.getInt(key));
                            break;
                        }
                    }
                } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
                    throw new JsonSyntaxException("Invalid recipe NBT", e);
                }
            }
            return stack;
        }

        private static IngredientSpec[] dissolvePattern(String[] pattern, Map<Character, IngredientSpec> key, int width, int height) {
            IngredientSpec[] ingredients = new IngredientSpec[width * height];
            Arrays.fill(ingredients, IngredientSpec.EMPTY);
            Map<Character, IngredientSpec> remaining = new HashMap<>(key);
            remaining.remove(' ');

            for (int y = 0; y < pattern.length; ++y) {
                for (int x = 0; x < pattern[y].length(); ++x) {
                    char symbol = pattern[y].charAt(x);
                    IngredientSpec ingredient = key.get(symbol);
                    if (ingredient == null) {
                        throw new JsonSyntaxException("Pattern references symbol '" + symbol + "' but it's not defined in the key");
                    }
                    remaining.remove(symbol);
                    ingredients[x + width * y] = ingredient;
                }
            }

            if (!remaining.isEmpty()) {
                throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + remaining.keySet());
            }
            return ingredients;
        }

        private static Map<Character, IngredientSpec> keyFromJson(JsonObject json) {
            Map<Character, IngredientSpec> map = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getKey().length() != 1) {
                    throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol");
                }
                char symbol = entry.getKey().charAt(0);
                if (symbol == ' ') {
                    throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
                }
                map.put(symbol, IngredientSpec.fromJson(GsonHelper.convertToJsonObject(entry.getValue(), "key")));
            }
            map.put(' ', IngredientSpec.EMPTY);
            return map;
        }

        private static String[] patternFromJson(JsonArray json) {
            String[] pattern = new String[json.size()];
            if (pattern.length == 0) {
                throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
            }
            if (pattern.length > 3) {
                throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
            }

            for (int i = 0; i < pattern.length; ++i) {
                String row = GsonHelper.convertToString(json.get(i), "pattern[" + i + "]");
                if (row.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }
                if (i > 0 && pattern[0].length() != row.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }
                pattern[i] = row;
            }
            return pattern;
        }

        private static String[] shrink(String... pattern) {
            int firstColumn = Integer.MAX_VALUE;
            int lastColumn = 0;
            int leadingEmptyRows = 0;
            int trailingEmptyRows = 0;

            for (int row = 0; row < pattern.length; ++row) {
                String line = pattern[row];
                firstColumn = Math.min(firstColumn, firstNonSpace(line));
                int last = lastNonSpace(line);
                lastColumn = Math.max(lastColumn, last);
                if (last < 0) {
                    if (leadingEmptyRows == row) {
                        ++leadingEmptyRows;
                    }
                    ++trailingEmptyRows;
                } else {
                    trailingEmptyRows = 0;
                }
            }

            if (pattern.length == trailingEmptyRows) {
                return new String[0];
            }

            String[] shrunk = new String[pattern.length - trailingEmptyRows - leadingEmptyRows];
            for (int i = 0; i < shrunk.length; ++i) {
                shrunk[i] = pattern[i + leadingEmptyRows].substring(firstColumn, lastColumn + 1);
            }
            return shrunk;
        }

        private static int firstNonSpace(String line) {
            int i = 0;
            while (i < line.length() && line.charAt(i) == ' ') {
                i++;
            }
            return i;
        }

        private static int lastNonSpace(String line) {
            int i = line.length() - 1;
            while (i >= 0 && line.charAt(i) == ' ') {
                --i;
            }
            return i;
        }

        private static <T> JsonObject toJsonObject(DynamicOps<T> ops, MapLike<T> input) {
            JsonObject json = new JsonObject();
            input.entries().forEach(entry -> {
                JsonElement key = ops.convertTo(JsonOps.INSTANCE, entry.getFirst());
                JsonElement value = ops.convertTo(JsonOps.INSTANCE, entry.getSecond());
                json.add(key.getAsString(), value);
            });
            return json;
        }
    }

    private record IngredientSpec(Ingredient ingredient, int requiredType) {
        private static final IngredientSpec EMPTY = new IngredientSpec(Ingredient.EMPTY, Integer.MIN_VALUE);

        private boolean test(ItemStack stack) {
            if (this == EMPTY) {
                return stack.isEmpty();
            }
            if (!ingredient.test(stack)) {
                return false;
            }
            if (requiredType == Integer.MIN_VALUE) {
                return true;
            }
            return github.com.gengyoubo.common.util.MPGItemStackData.getInt(stack, MPGNBTData.ItemType) == requiredType;
        }

        private static IngredientSpec fromJson(JsonObject json) {
            JsonObject ingredientJson = json.deepCopy();
            ingredientJson.remove("type");
            Ingredient ingredient = Ingredient.fromJson(ingredientJson);
            int requiredType = Integer.MIN_VALUE;
            if (json.has("type")) {
                requiredType = GsonHelper.getAsInt(json, "type");
            }
            return new IngredientSpec(ingredient, requiredType);
        }

        private Ingredient toDisplayIngredient() {
            if (this == EMPTY || ingredient.isEmpty() || requiredType == Integer.MIN_VALUE) {
                return ingredient;
            }

            ItemStack[] stacks = Arrays.stream(ingredient.getItems())
                    .map(ItemStack::copy)
                    .peek(stack -> github.com.gengyoubo.common.util.MPGItemStackData.putInt(stack, MPGNBTData.ItemType, requiredType))
                    .toArray(ItemStack[]::new);
            return stacks.length == 0 ? ingredient : Ingredient.of(stacks);
        }
    }
}
