package org.teacon.xkdeco.resource;

import com.google.gson.JsonParser;
import net.minecraft.FileUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.registries.ForgeRegistries;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.XKDecoObjects;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class SpecialWallResources implements PackResources {
    private static final String BLOCK_MODEL_LOCATION = XKDeco.ID + ":block/" + XKDecoObjects.WALL_BLOCK_ENTITY;
    private static final String ITEM_MODEL_LOCATION = XKDeco.ID + ":item/" + XKDecoObjects.WALL_BLOCK_ENTITY;
    private static final String BLOCK_MODEL = "{\"variants\":{\"\":{\"model\":\"" + BLOCK_MODEL_LOCATION + "\"}}}";
    private static final String ITEM_MODEL = "{\"parent\":\"" + ITEM_MODEL_LOCATION + "\"}";
    private static final String PACK_META = "{\"pack\":{\"description\":\"XKDeco: Special Walls\",\"pack_format\":8}}";
    private static final String NAME = "XKDeco: Special Walls";
    private static final String ID = XKDeco.ID + "_" + XKDecoObjects.WALL_BLOCK_ENTITY;

    @Nullable
    public static Pack create(Pack.PackConstructor pInfoFactory) {
        return Pack.create(ID, true, SpecialWallResources::new, pInfoFactory, Pack.Position.TOP, PackSource.DEFAULT);
    }

    @Override
    public InputStream getRootResource(String pFileName) throws IOException {
        if ("pack.mcmeta".equals(pFileName)) {
            return new ByteArrayInputStream(PACK_META.getBytes(StandardCharsets.UTF_8));
        }
        if (pFileName.contains("/") || pFileName.contains("\\")) {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
        throw new FileNotFoundException(pFileName);
    }

    @Override
    public InputStream getResource(PackType pType, ResourceLocation pLocation) throws IOException {
        var path = FileUtil.normalizeResourcePath(pLocation.getPath());
        if (pType == PackType.CLIENT_RESOURCES && pLocation.getNamespace().equals(XKDeco.ID)) {
            if (path.startsWith("blockstates/" + XKDecoObjects.SPECIAL_WALL_PREFIX) && path.endsWith(".json")) {
                var id = new ResourceLocation(XKDeco.ID, path.substring("blockstates/".length(), path.length() - 5));
                if (ForgeRegistries.BLOCKS.containsKey(id)) {
                    return new ByteArrayInputStream(BLOCK_MODEL.getBytes(StandardCharsets.UTF_8));
                }
            }
            if (path.startsWith("models/item/" + XKDecoObjects.SPECIAL_WALL_PREFIX) && path.endsWith(".json")) {
                var id = new ResourceLocation(XKDeco.ID, path.substring("models/item/".length(), path.length() - 5));
                if (ForgeRegistries.BLOCKS.containsKey(id)) {
                    return new ByteArrayInputStream(ITEM_MODEL.getBytes(StandardCharsets.UTF_8));
                }
            }
        }
        throw new FileNotFoundException(pType.getDirectory() + "/" + pLocation.getNamespace() + "/" + path);
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType pType, String pNamespace,
                                                     String pPath, int pMaxDepth, Predicate<String> pFilter) {
        return Collections.emptyList(); // TODO: list resources
    }

    @Override
    public boolean hasResource(PackType pType, ResourceLocation pLocation) {
        var path = FileUtil.normalizeResourcePath(pLocation.getPath());
        if (pType == PackType.CLIENT_RESOURCES && pLocation.getNamespace().equals(XKDeco.ID)) {
            if (path.startsWith("blockstates/" + XKDecoObjects.SPECIAL_WALL_PREFIX) && path.endsWith(".json")) {
                var id = new ResourceLocation(XKDeco.ID, path.substring("blockstates/".length(), path.length() - 5));
                return ForgeRegistries.BLOCKS.containsKey(id);
            }
            if (path.startsWith("models/item/" + XKDecoObjects.SPECIAL_WALL_PREFIX) && path.endsWith(".json")) {
                var id = new ResourceLocation(XKDeco.ID, path.substring("models/item/".length(), path.length() - 5));
                return ForgeRegistries.BLOCKS.containsKey(id);
            }
        }
        return false;
    }

    @Override
    public Set<String> getNamespaces(PackType pType) {
        return Set.of(XKDeco.ID);
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
    public String getName() {
        return NAME;
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
