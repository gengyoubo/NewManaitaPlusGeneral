package github.com.gengyoubo.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import github.com.gengyoubo.common.MPGCommon;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/** Shaped crafting whose ingredients and result may carry a Manaita tier ({@link MPGNBTData#ItemType}). */
public class MPNBTCraftingRecipe implements CraftingRecipe {
    public static final MapCodec<MPNBTCraftingRecipe> MAP_CODEC = new MapCodec<>() {
        @Override
        public <T> DataResult<MPNBTCraftingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
            return decodeRecipe(ops, input);
        }

        @Override
        public <T> RecordBuilder<T> encode(MPNBTCraftingRecipe input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            return encodeRecipe(input, ops, prefix);
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of("group", "category", "pattern", "key", "result", "show_notification").map(ops::createString);
        }
    };
    public static final StreamCodec<RegistryFriendlyByteBuf, MPNBTCraftingRecipe> STREAM_CODEC =
            StreamCodec.of(MPNBTCraftingRecipe::encodeToNetwork, MPNBTCraftingRecipe::decodeFromNetwork);
    public static final RecipeSerializer<MPNBTCraftingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final String group;
    private final CraftingBookCategory category;
    private final int width;
    private final int height;
    private final IngredientSpec[] ingredients;
    private final ItemStackTemplate result;
    private final boolean showNotification;
    private @Nullable PlacementInfo placementInfo;

    public MPNBTCraftingRecipe(String group, CraftingBookCategory category, int width, int height,
                               IngredientSpec[] ingredients, ItemStackTemplate result, boolean showNotification) {
        this.group = group;
        this.category = category;
        this.width = width;
        this.height = height;
        this.ingredients = ingredients;
        this.result = result;
        this.showNotification = showNotification;
    }

    @Override
    public @NotNull RecipeSerializer<MPNBTCraftingRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public @NotNull String group() {
        return group;
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return category;
    }

    @Override
    public boolean showNotification() {
        return showNotification;
    }

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
        if (input.width() < width || input.height() < height) {
            return false;
        }
        for (int x = 0; x <= input.width() - width; ++x) {
            for (int y = 0; y <= input.height() - height; ++y) {
                if (matches(input, x, y, true) || matches(input, x, y, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean matches(CraftingInput input, int offsetX, int offsetY, boolean mirrored) {
        for (int x = 0; x < input.width(); ++x) {
            for (int y = 0; y < input.height(); ++y) {
                int patternX = x - offsetX;
                int patternY = y - offsetY;
                IngredientSpec expected = IngredientSpec.EMPTY;
                if (patternX >= 0 && patternY >= 0 && patternX < width && patternY < height) {
                    expected = mirrored
                            ? ingredients[width - patternX - 1 + patternY * width]
                            : ingredients[patternX + patternY * width];
                }
                if (!expected.test(input.getItem(x, y))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input) {
        return result.create();
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (placementInfo == null) {
            placementInfo = PlacementInfo.createFromOptionals(
                    Arrays.stream(ingredients).map(spec -> Optional.ofNullable(spec.ingredient())).toList());
        }
        return placementInfo;
    }

    @Override
    public @NotNull List<RecipeDisplay> display() {
        return List.of(new ShapedCraftingRecipeDisplay(width, height,
                Arrays.stream(ingredients).map(IngredientSpec::display).toList(),
                new SlotDisplay.ItemStackSlotDisplay(result),
                new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
    }

    private static MPNBTCraftingRecipe decodeFromNetwork(RegistryFriendlyByteBuf buf) {
        int width = buf.readVarInt();
        int height = buf.readVarInt();
        String group = buf.readUtf();
        CraftingBookCategory category = CraftingBookCategory.STREAM_CODEC.decode(buf);
        IngredientSpec[] ingredients = new IngredientSpec[width * height];
        for (int i = 0; i < ingredients.length; i++) {
            ingredients[i] = IngredientSpec.STREAM_CODEC.decode(buf);
        }
        ItemStackTemplate result = ItemStackTemplate.STREAM_CODEC.decode(buf);
        boolean showNotification = buf.readBoolean();
        return new MPNBTCraftingRecipe(group, category, width, height, ingredients, result, showNotification);
    }

    private static void encodeToNetwork(RegistryFriendlyByteBuf buf, MPNBTCraftingRecipe recipe) {
        buf.writeVarInt(recipe.width);
        buf.writeVarInt(recipe.height);
        buf.writeUtf(recipe.group);
        CraftingBookCategory.STREAM_CODEC.encode(buf, recipe.category);
        for (IngredientSpec ingredient : recipe.ingredients) {
            IngredientSpec.STREAM_CODEC.encode(buf, ingredient);
        }
        ItemStackTemplate.STREAM_CODEC.encode(buf, recipe.result);
        buf.writeBoolean(recipe.showNotification);
    }

    private static <T> DataResult<MPNBTCraftingRecipe> decodeRecipe(DynamicOps<T> ops, MapLike<T> input) {
        try {
            JsonObject json = toJsonObject(ops, input);
            String group = GsonHelper.getAsString(json, "group", "");
            CraftingBookCategory category = CraftingBookCategory.CODEC
                    .parse(JsonOps.INSTANCE, json.has("category") ? json.get("category") : new JsonPrimitive("misc"))
                    .result().orElse(CraftingBookCategory.MISC);
            String[] pattern = shrink(patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            if (pattern.length == 0) {
                throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
            }
            int width = pattern[0].length();
            int height = pattern.length;
            Map<Character, IngredientSpec> key = keyFromJson(ops, GsonHelper.getAsJsonObject(json, "key"));
            IngredientSpec[] ingredients = dissolvePattern(pattern, key, width, height);
            ItemStackTemplate result = resultFromJson(ops, GsonHelper.getAsJsonObject(json, "result"));
            boolean showNotification = GsonHelper.getAsBoolean(json, "show_notification", true);
            return DataResult.success(new MPNBTCraftingRecipe(group, category, width, height, ingredients, result,
                    showNotification));
        } catch (Exception exception) {
            return DataResult.error(exception::getMessage);
        }
    }

    /** Writes the recipe back as pattern + key, so the output can be decoded again. */
    private static <T> RecordBuilder<T> encodeRecipe(MPNBTCraftingRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> builder) {
        Map<IngredientSpec, Character> symbols = new LinkedHashMap<>();
        JsonArray pattern = new JsonArray();
        for (int y = 0; y < recipe.height; y++) {
            StringBuilder row = new StringBuilder();
            for (int x = 0; x < recipe.width; x++) {
                IngredientSpec spec = recipe.ingredients[x + y * recipe.width];
                row.append(spec.ingredient() == null ? ' '
                        : symbols.computeIfAbsent(spec, ignored -> (char) ('A' + symbols.size())));
            }
            pattern.add(row.toString());
        }
        JsonObject key = new JsonObject();
        symbols.forEach((spec, symbol) -> key.add(String.valueOf(symbol), spec.toJson(ops)));

        builder.add("group", ops.createString(recipe.group));
        builder.add("category", ops.createString(recipe.category.getSerializedName()));
        builder.add("pattern", JsonOps.INSTANCE.convertTo(ops, pattern));
        builder.add("key", JsonOps.INSTANCE.convertTo(ops, key));
        builder.add("result", ItemStackTemplate.CODEC.encodeStart(ops, recipe.result).getOrThrow(JsonSyntaxException::new));
        builder.add("show_notification", ops.createBoolean(recipe.showNotification));
        return builder;
    }

    private static <T> ItemStackTemplate resultFromJson(DynamicOps<T> ops, JsonObject json) {
        JsonObject normalized = json.deepCopy();
        if (normalized.has("item")) {
            normalized.add("id", normalized.remove("item"));
        }
        if (json.has("nbt")) {
            int type = readType(GsonHelper.getAsJsonObject(json, "nbt"));
            if (type >= 0) {
                JsonObject components = normalized.has("components")
                        ? normalized.getAsJsonObject("components") : new JsonObject();
                JsonObject data = components.has("minecraft:custom_data")
                        ? components.getAsJsonObject("minecraft:custom_data") : new JsonObject();
                data.addProperty(MPGNBTData.ItemType, type);
                components.add("minecraft:custom_data", data);
                if (type > 0) {
                    JsonObject model = new JsonObject();
                    JsonArray floats = new JsonArray();
                    floats.add(type);
                    model.add("floats", floats);
                    components.add("minecraft:custom_model_data", model);
                }
                normalized.add("components", components);
            }
        }
        return ItemStackTemplate.CODEC.parse(ops, JsonOps.INSTANCE.convertTo(ops, normalized))
                .getOrThrow(JsonSyntaxException::new);
    }

    private static net.minecraft.core.component.DataComponentPatch tierComponents(int type) {
        net.minecraft.nbt.CompoundTag data = new net.minecraft.nbt.CompoundTag();
        data.putInt(MPGNBTData.ItemType, type);
        var patch = net.minecraft.core.component.DataComponentPatch.builder()
                .set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                        net.minecraft.world.item.component.CustomData.of(data));
        if (type > 0) {
            patch.set(net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA,
                    new net.minecraft.world.item.component.CustomModelData(List.of((float) type),
                            List.of(), List.of(), List.of()));
        }
        return patch.build();
    }

    private static JsonElement normalizeIngredient(JsonElement value) {
        if (value.isJsonArray()) {
            JsonArray result = new JsonArray();
            value.getAsJsonArray().forEach(element -> result.add(normalizeIngredient(element)));
            return result;
        }
        if (value.isJsonObject()) {
            JsonObject object = value.getAsJsonObject();
            if (object.has("item")) return new JsonPrimitive(GsonHelper.getAsString(object, "item"));
            if (object.has("tag")) return new JsonPrimitive("#" + GsonHelper.getAsString(object, "tag"));
        }
        return value;
    }

    private static int readType(JsonObject json) {
        for (String key : new String[]{MPGNBTData.ItemType, "ManaitaPlusLegacyType", "ManaitaType"}) {
            if (json.has(key)) {
                return GsonHelper.getAsInt(json, key);
            }
        }
        return -1;
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

    private static <T> Map<Character, IngredientSpec> keyFromJson(DynamicOps<T> ops, JsonObject json) {
        Map<Character, IngredientSpec> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            if (entry.getKey().length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol");
            }
            char symbol = entry.getKey().charAt(0);
            if (symbol == ' ') {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }
            map.put(symbol, IngredientSpec.fromJson(ops, entry.getValue()));
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

    /** One pattern cell: an ingredient (null for an empty cell) and an optional required Manaita tier. */
    public record IngredientSpec(@Nullable Ingredient ingredient, int requiredType) {
        private static final int ANY_TYPE = Integer.MIN_VALUE;
        private static final IngredientSpec EMPTY = new IngredientSpec(null, ANY_TYPE);
        private static final StreamCodec<RegistryFriendlyByteBuf, IngredientSpec> STREAM_CODEC = StreamCodec.composite(
                Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC,
                spec -> Optional.ofNullable(spec.ingredient()),
                ByteBufCodecs.INT,
                IngredientSpec::requiredType,
                (ingredient, type) -> new IngredientSpec(ingredient.orElse(null), type)
        );

        private boolean test(ItemStack stack) {
            if (ingredient == null) {
                return stack.isEmpty();
            }
            if (!ingredient.test(stack)) {
                return false;
            }
            return requiredType == ANY_TYPE || MPGItemStackData.getInt(stack, MPGNBTData.ItemType) == requiredType;
        }

        private SlotDisplay display() {
            if (ingredient == null) {
                return SlotDisplay.Empty.INSTANCE;
            }
            if (requiredType == ANY_TYPE) {
                return ingredient.display();
            }
            List<SlotDisplay> typedStacks = new ArrayList<>();
            ingredient.items().forEach(item -> {
                typedStacks.add(new SlotDisplay.ItemStackSlotDisplay(
                        new ItemStackTemplate(item, 1, tierComponents(requiredType))));
            });
            return typedStacks.size() == 1 ? typedStacks.getFirst() : new SlotDisplay.Composite(typedStacks);
        }

        private <T> JsonElement toJson(DynamicOps<T> ops) {
            JsonElement encoded = ingredient == null ? new JsonObject()
                    : Ingredient.CODEC.encodeStart(ops, ingredient).map(value -> ops.convertTo(JsonOps.INSTANCE, value))
                    .getOrThrow(JsonSyntaxException::new);
            JsonObject json = new JsonObject();
            json.add("ingredient", encoded);
            if (requiredType != ANY_TYPE) {
                json.addProperty("type", requiredType);
            }
            return json;
        }

        /**
         * Accepts the legacy {@code {"item": id}} / {@code {"tag": id}} objects used by the bundled recipes,
         * plain 26.1 ingredient values, and {@code {"ingredient": value}}; {@code "type"} pins the Manaita tier.
         */
        private static <T> IngredientSpec fromJson(DynamicOps<T> ops, JsonElement json) {
            JsonElement value = json;
            int requiredType = ANY_TYPE;
            if (json.isJsonObject()) {
                JsonObject object = json.getAsJsonObject();
                if (object.has("type")) {
                    requiredType = GsonHelper.getAsInt(object, "type");
                }
                if (object.has("item")) {
                    value = new JsonPrimitive(GsonHelper.getAsString(object, "item"));
                } else if (object.has("tag")) {
                    value = new JsonPrimitive("#" + GsonHelper.getAsString(object, "tag"));
                } else if (object.has("ingredient")) {
                    value = object.get("ingredient");
                } else {
                    throw new JsonSyntaxException("Ingredient needs an 'item', 'tag' or 'ingredient' entry");
                }
            }
            Ingredient ingredient = Ingredient.CODEC.parse(ops, JsonOps.INSTANCE.convertTo(ops, normalizeIngredient(value)))
                    .getOrThrow(JsonSyntaxException::new);
            return new IngredientSpec(ingredient, requiredType);
        }
    }
}
