package org.teacon.xkdeco.resource;

import com.google.gson.JsonParser;
import net.minecraft.FileUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.XKDecoObjects;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class SpecialWallResources implements PackResources {
    private static final String BLOCK_MODEL_LOCATION = XKDeco.ID + ":block/" + XKDecoObjects.WALL_BLOCK_ENTITY;
    private static final String ITEM_MODEL_LOCATION = XKDeco.ID + ":item/" + XKDecoObjects.WALL_BLOCK_ENTITY;
    private static final String BLOCK_MODEL = "{\"variants\":{\"\":{\"model\":\"" + BLOCK_MODEL_LOCATION + "\"}}}";
    private static final String ITEM_MODEL = "{\"parent\":\"" + ITEM_MODEL_LOCATION + "\"}";
    private static final String PACK_META = "{\"pack\":{\"description\":\"XKDeco: Special Walls\",\"pack_format\":8}}";
    private static final String NAME = "XKDeco: Special Walls"; // TODO Translatable
    private static final String ID = XKDeco.ID + "_" + XKDecoObjects.WALL_BLOCK_ENTITY;

    private static final Pack.Info PACK_INFO = new Pack.Info(Component.literal(NAME), 13, 13, FeatureFlagSet.of(), true);

    private final String packId;

    public SpecialWallResources(String packId) {
        this.packId = packId;
    }

    public static Pack create(/*Pack.PackConstructor pInfoFactory*/) {
        return Pack.create(ID, Component.literal(NAME), true, SpecialWallResources::new, PACK_INFO, PackType.CLIENT_RESOURCES, Pack.Position.TOP, false, PackSource.DEFAULT);
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... pFileName) {
        if (pFileName.length > 0 && "pack.mcmeta".equals(pFileName[0])) {
            return () -> new ByteArrayInputStream(PACK_META.getBytes(StandardCharsets.UTF_8));
        }
        // TODO Determine if this is still necessary
        //if (pFileName.contains("/") || pFileName.contains("\\")) {
        //    throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        //}
        //throw new FileNotFoundException(pFileName);
        return null;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType pType, ResourceLocation pLocation) {
        var path = FileUtil.normalizeResourcePath(pLocation.getPath());
        if (pType == PackType.CLIENT_RESOURCES && pLocation.getNamespace().equals(XKDeco.ID)) {
            if (path.startsWith("blockstates/" + XKDecoObjects.SPECIAL_WALL_PREFIX) && path.endsWith(".json")) {
                var id = new ResourceLocation(XKDeco.ID, path.substring("blockstates/".length(), path.length() - 5));
                if (ForgeRegistries.BLOCKS.containsKey(id)) {
                    return () -> new ByteArrayInputStream(BLOCK_MODEL.getBytes(StandardCharsets.UTF_8));
                }
            }
            if (path.startsWith("models/item/" + XKDecoObjects.SPECIAL_WALL_PREFIX) && path.endsWith(".json")) {
                var id = new ResourceLocation(XKDeco.ID, path.substring("models/item/".length(), path.length() - 5));
                if (ForgeRegistries.BLOCKS.containsKey(id)) {
                    return () -> new ByteArrayInputStream(ITEM_MODEL.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        //throw new FileNotFoundException(pType.getDirectory() + "/" + pLocation.getNamespace() + "/" + path);
        return null;
    }

    @Override
    public void listResources(PackType pType, String pNamespace, String pPath, PackResources.ResourceOutput output) {
        if (pPath.equals("blockstates")) {
            for (var block : walls()) {
                var registryName = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block));
                var name = XKDecoObjects.SPECIAL_WALL_PREFIX + registryName.toString().replace(':', '_');
                output.accept(new ResourceLocation(XKDeco.ID, "blockstates/" + name + ".json"),
                        () -> new ByteArrayInputStream(BLOCK_MODEL.getBytes(StandardCharsets.UTF_8)));
            }
        } else if (pPath.equals("models")) {
            for (var block : walls()) {
                var registryName = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block));
                var name = XKDecoObjects.SPECIAL_WALL_PREFIX + registryName.toString().replace(':', '_');
                output.accept(new ResourceLocation(XKDeco.ID, "models/item/" + name + ".json"),
                        () -> new ByteArrayInputStream(ITEM_MODEL.getBytes(StandardCharsets.UTF_8)));
            }
        }
    }

    private Collection<Block> walls() {
        return List.of(
                Blocks.COBBLESTONE_WALL,
                Blocks.MOSSY_COBBLESTONE_WALL,
                Blocks.BRICK_WALL,
                Blocks.PRISMARINE_WALL,
                Blocks.RED_SANDSTONE_WALL,
                Blocks.MOSSY_STONE_BRICK_WALL,
                Blocks.GRANITE_WALL,
                Blocks.STONE_BRICK_WALL,
                Blocks.MUD_BRICK_WALL,
                Blocks.NETHER_BRICK_WALL,
                Blocks.ANDESITE_WALL,
                Blocks.RED_NETHER_BRICK_WALL,
                Blocks.SANDSTONE_WALL,
                Blocks.END_STONE_BRICK_WALL,
                Blocks.DIORITE_WALL,
                Blocks.BLACKSTONE_WALL,
                Blocks.POLISHED_BLACKSTONE_BRICK_WALL,
                Blocks.POLISHED_BLACKSTONE_WALL,
                Blocks.COBBLED_DEEPSLATE_WALL,
                Blocks.POLISHED_DEEPSLATE_WALL,
                Blocks.DEEPSLATE_TILE_WALL,
                Blocks.DEEPSLATE_BRICK_WALL
        );
    }

    @Override
    public Set<String> getNamespaces(PackType pType) {
        return pType == PackType.CLIENT_RESOURCES ? Set.of(XKDeco.ID) : Set.of();
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> pDeserializer) {
        if ("pack".equals(pDeserializer.getMetadataSectionName())) {
            return pDeserializer.fromJson(JsonParser.parseString(PACK_META).getAsJsonObject().getAsJsonObject("pack"));
        }
        return null;
    }

    @Override
    public String packId() {
        return this.packId;
    }

    @Override
    public void close() {
        // do nothing here
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
