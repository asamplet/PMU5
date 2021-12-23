package ru.kapmax.kurswork;

import android.opengl.GLES20;
import java.nio.FloatBuffer;

public class Shader {
    private int programHandel;

    public Shader(String vertexShaderString, String fragmentShaderString) {
        this.createShader(vertexShaderString, fragmentShaderString);
    }

    public void createShader(String vertexShaderString, String fragmentShaderString) {
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShaderHandle, vertexShaderString);
        GLES20.glCompileShader(vertexShaderHandle);

        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShaderHandle, fragmentShaderString);
        GLES20.glCompileShader(fragmentShaderHandle);

        this.programHandel = GLES20.glCreateProgram();
        GLES20.glAttachShader(this.programHandel, vertexShaderHandle);
        GLES20.glAttachShader(this.programHandel, fragmentShaderHandle);
        GLES20.glLinkProgram(this.programHandel);
    }

    public void linkVertex(FloatBuffer vertex, String varName, int size) {
        GLES20.glUseProgram(this.programHandel);
        int vHandel = GLES20.glGetAttribLocation(this.programHandel, varName);
        GLES20.glEnableVertexAttribArray(vHandel);
        GLES20.glVertexAttribPointer(vHandel, size, GLES20.GL_FLOAT, false, 0, vertex);
    }

    public void linkMatrix(float[] MVPMatrix, String varName) {
        GLES20.glUseProgram(this.programHandel);
        int vHandel = GLES20.glGetUniformLocation(this.programHandel, varName);
        GLES20.glUniformMatrix4fv(vHandel, 1, false, MVPMatrix, 0);
    }

    public void linkUniform3f(float[] arr, String varName) {
        GLES20.glUseProgram(this.programHandel);
        int vHandle = GLES20.glGetUniformLocation(this.programHandel, varName);
        GLES20.glUniform3f(vHandle, arr[0], arr[1], arr[2]);
    }

    public void linkUniform1f(float val, String varName) {
        GLES20.glUseProgram(this.programHandel);
        int vHandle = GLES20.glGetUniformLocation(this.programHandel, varName);
        GLES20.glUniform1f(vHandle, val);
    }

    public void linkUniform1i(int id, String varName) {
        GLES20.glUseProgram(this.programHandel);
        int vHandle = GLES20.glGetUniformLocation(this.programHandel, varName);
        GLES20.glUniform1i(vHandle, id);
    }
}