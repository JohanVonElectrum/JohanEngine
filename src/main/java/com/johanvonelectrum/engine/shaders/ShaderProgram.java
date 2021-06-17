package com.johanvonelectrum.engine.shaders;

import com.johanvonelectrum.engine.io.resources.ResourceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL32.GL_VERTEX_SHADER;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ShaderProgram {

    public static final Logger LOGGER = LogManager.getLogger("Shader");

    private final int programId;
    private final int vertexId;
    private final int geometryId;
    private final int fragmentId;

    public ShaderProgram(String name) throws ShaderException {
        this.programId = glCreateProgram();
        if (this.programId == NULL)
            throw new ShaderException("Could not create shader program. " + name);

        String vertexShader;
        String geometryShader = null;
        String fragmentShader;
        try {
            vertexShader = ResourceLoader.loadResourceShader(name, GL_VERTEX_SHADER);
            fragmentShader = ResourceLoader.loadResourceShader(name, GL_FRAGMENT_SHADER);
        } catch (IOException e) {
            throw new ShaderException("IOException while trying to read shader sources. " + name);
        }

        try {
            geometryShader = ResourceLoader.loadResourceShader(name, GL_GEOMETRY_SHADER);
        } catch (IOException e) {
            LOGGER.warn("IOException while trying to read geometry shader source.", e);
        }

        if (vertexShader == null)
            throw new ShaderException("Vertex shader not found. " + name);
        else this.vertexId = createShader(vertexShader, GL_VERTEX_SHADER);
        if (geometryShader == null) {
            LOGGER.warn(new ShaderException("Geometry shader not found. " + name));
            this.geometryId = -1;
        }
        else this.geometryId = createShader(geometryShader, GL_GEOMETRY_SHADER);
        if (fragmentShader == null)
            throw new ShaderException("Fragment shader not found. " + name);
        else this.fragmentId = createShader(fragmentShader, GL_FRAGMENT_SHADER);

    }

    private int createShader(String shaderSource, int shader) throws ShaderException {
        int shaderId = glCreateShader(shader);
        if (shaderId == NULL)
            throw new ShaderException("Could not create shader of type " + shader);

        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE)
            throw new ShaderException("Error compiling shader source: " + glGetShaderInfoLog(shaderId, 1024));

        glAttachShader(this.programId, shaderId);

        return shaderId;
    }

    public void link() throws ShaderException {
        glLinkProgram(this.programId);
        if (glGetProgrami(this.programId, GL_LINK_STATUS) == GL_FALSE)
            throw new ShaderException("Error linking shader program: " + glGetProgramInfoLog(this.programId, 1024));

        if (this.vertexId != NULL)
            glDetachShader(this.programId, this.vertexId);
        if (this.geometryId != NULL)
            glDetachShader(this.programId, this.geometryId);
        if (this.fragmentId != NULL)
            glDetachShader(this.programId, this.fragmentId);

        glValidateProgram(this.programId);
        if (glGetProgrami(this.programId, GL_VALIDATE_STATUS) == GL_FALSE)
            LOGGER.warn("Warning validating shader program: " + glGetProgramInfoLog(this.programId, 1024));
    }

    public void bind() {
        glUseProgram(this.programId);
    }

    public void unbind() {
        glUseProgram(GL_FALSE);
    }

    public void dispose() {
        this.unbind();
        if (this.programId != NULL)
            glDeleteProgram(programId);
    }
}
