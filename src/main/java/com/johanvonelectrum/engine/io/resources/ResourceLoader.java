package com.johanvonelectrum.engine.io.resources;

import com.johanvonelectrum.engine.shaders.ShaderException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Scanner;

import static org.lwjgl.opengl.GL32.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL32.GL_VERTEX_SHADER;
import static org.lwjgl.stb.STBImage.stbi_load;

public class ResourceLoader {

    private static final Logger LOGGER = LogManager.getLogger("ResourceLoader");
    private static final ClassLoader CLASS_LOADER = ResourceLoader.class.getClassLoader();

    public static String loadResourceText(String path) throws IOException {
        String result;
        try (InputStream in = CLASS_LOADER.getResourceAsStream(path);
             Scanner scanner = new Scanner(in, java.nio.charset.StandardCharsets.UTF_8.name())) {
            result = scanner.useDelimiter("\\A").next();
        }
        return result;
    }

    public static Texture loadResourceTexture(String path) throws URISyntaxException, FileNotFoundException {
        int[] width = new int[1], height = new int[1], nrChannels = new int[1];

        URL resource = CLASS_LOADER.getResource("textures/" + path + ".png");
        if (resource == null)
            throw new FileNotFoundException();
        String filePath = new File(resource.toURI()).toString();
        LOGGER.trace(filePath);
        ByteBuffer data = stbi_load(filePath, width, height, nrChannels, 4);

        if (data == null)
            throw new RuntimeException("Failed to load texture."); //I get this exception.

        return new Texture(data, width[0], height[0], nrChannels[0]);
    }

    public static String loadResourceShader(String name, int shader) throws ShaderException, IOException {
        String extension;

        if (shader == GL_VERTEX_SHADER)
            extension = ".vs";
        else if (shader == GL_GEOMETRY_SHADER)
            extension = ".gs";
        else if (shader == GL_FRAGMENT_SHADER)
            extension = ".fs";
        else
            throw new ShaderException("Incorrect shader type.");

        return loadResourceText("shaders/" + name + extension);
    }
}
