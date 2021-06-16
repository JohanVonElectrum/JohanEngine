package com.johanvonelectrum.engine.io.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;

import static org.lwjgl.stb.STBImage.stbi_load;

public class ResourceLoader {

    private static final Logger LOGGER = LogManager.getLogger("ResourceLoader");
    private static final ClassLoader CLASS_LOADER = ResourceLoader.class.getClassLoader();

    public static Texture loadResourceTexture(String texture) throws URISyntaxException, FileNotFoundException {
        int[] width = new int[1], height = new int[1], nrChannels = new int[1];

        URL resource = CLASS_LOADER.getResource("textures/" + texture + ".png");
        if (resource == null)
            throw new FileNotFoundException();
        String path = new File(resource.toURI()).toString();
        LOGGER.trace(path);
        ByteBuffer data = stbi_load(path, width, height, nrChannels, 4);

        if(data == null)
            throw new RuntimeException("Failed to load texture."); //I get this exception.

        return new Texture(data, width[0], height[0], nrChannels[0]);
    }

}
