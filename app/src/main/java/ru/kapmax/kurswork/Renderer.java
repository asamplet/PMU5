package ru.kapmax.kurswork;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.os.SystemClock;

import androidx.annotation.RequiresApi;

import java.util.Timer;
import java.util.TimerTask;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Renderer implements GLSurfaceView.Renderer {

    private final Context context;
    private final float[] cameraPos, lightPos;
    private float[] mMVPMatrix = new float[16];
    private float[] mMMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] mProjMatrix = new float[16];
    private float p = 0;
    private float time = 0.0f;
    private Shader shader;
    private Shader fire;
    private Shader liquid;
    private Vector<Vector<FloatBuffer>> models;
    private Vector<Integer> numFaces;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Renderer(Context context) {
        this.context = context;
        cameraPos = new float[] {0.0f, 1.5f, 2.7f};
        lightPos = new float[] {-0.07f, 0.55f, 1.025f};

        Matrix.setLookAtM(mVMatrix, 0, cameraPos[0], cameraPos[1], cameraPos[2], 0, 0, 0, 0, 1, 0);
        models = new Vector<>();
        numFaces = new Vector<>();
        loadModels(new int[]{R.raw.table2, R.raw.can, R.raw.cup, R.raw.drink,
                R.raw.apple, R.raw.banana, R.raw.pumpkin, R.raw.watermelon,
        R.raw.lemon, R.raw.orange});
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        shader = new Shader(loadShaderFromFile(context, R.raw.vertex), loadShaderFromFile(context, R.raw.fragment));
        fire = new Shader(loadShaderFromFile(context, R.raw.firef), loadShaderFromFile(context, R.raw.firev));
        liquid = new Shader(loadShaderFromFile(context, R.raw.liquidv), loadShaderFromFile(context, R.raw.liquidf));
        loadTextures(new int[] {R.drawable.mramor, R.drawable.candle, R.drawable.cup,
                R.drawable.tea, R.drawable.apple2, R.drawable.banana, R.drawable.pumpkin, R.drawable.watermelon,
        R.drawable.orange, R.drawable.coconut}, gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        float k=0.055f;
        float left = -k*ratio;
        float right = k*ratio;
        float bottom = -k;
        float top = k;
        float near = 0.1f;
        float far = 10.0f;
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        int n = 3;

        // Draw Table
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.scaleM(mMMatrix, 0, 0.015f, 0.015f, 0.015f);
        Matrix.translateM(mMMatrix, 0, 0.0f, -35.5f, 60.0f);
        drawModel(0, 0, gl);

        // Draw Candle
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.scaleM(mMMatrix, 0, 0.25f, 0.2f, 0.25f);
        Matrix.translateM(mMMatrix, 0, -0.3f, 1.3f,4.0f);
        drawModel(1, 5, gl);

        // Draw Cup
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.scaleM(mMMatrix, 0, 0.03f, 0.03f, 0.03f);
        Matrix.translateM(mMMatrix, 0, -5.5f, 4.7f, 27.0f);
        drawModel(2, 2, gl);
        drawModel(3, 3, gl);

        // Draw Apple
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.scaleM(mMMatrix, 0, 0.03f, 0.03f, 0.03f);
        Matrix.translateM(mMMatrix, 0, -11.5f, 4.7f, 27.0f);
        drawModel(4, 4, gl);

        // Draw Banana/Lemon
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.scaleM(mMMatrix, 0, 0.02f, 0.02f, 0.02f);
        Matrix.translateM(mMMatrix, 0, 10.0f, 6.00f, 45.0f);
        Matrix.rotateM(mMMatrix, 0, 180, 0, 1, 0);
        drawModel(8, 5, gl);

        // Draw Orange
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.scaleM(mMMatrix, 0, 0.06f, 0.06f, 0.06f);
        Matrix.translateM(mMMatrix, 0, 5.0f, 1.5f, 16.0f);
        Matrix.rotateM(mMMatrix, 0, 180, 180, 1, 0);
        drawModel(9, 8, gl);

        // Draw Coconut
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.scaleM(mMMatrix, 0, 0.03f, 0.034f, 0.03f);
        Matrix.translateM(mMMatrix, 0, -2.0f, 5.00f, 40.0f);
        Matrix.rotateM(mMMatrix, 0, 180, 0, 1, 0);
        drawModel(8, 9, gl);

        Matrix.setIdentityM(mMMatrix, 0);

        liquid.linkVertex(models.get(n).get(0), "a_vertex", 3);
        liquid.linkMatrix(mMMatrix, "model");
        liquid.linkMatrix(mVMatrix, "view");
        liquid.linkMatrix(mProjMatrix, "projection");
        liquid.linkVertex(models.get(n).get(1), "a_TexCord", 2);
        liquid.linkVertex(models.get(n).get(2), "a_normal", 3);
        liquid.linkUniform3f(cameraPos, "u_camera");
        liquid.linkUniform3f(lightPos, "u_lightPosition");
        liquid.linkUniform3f(new float[] {1, 1, 1}, "u_lightColor");
        liquid.linkUniform1i(9, "u_TextureUnit");
        gl.glDrawArrays(GLES20.GL_TRIANGLES, 0, numFaces.get(n));

        // Draw Fire
        Matrix.setIdentityM(mMMatrix, 0);
        FloatBuffer p = ByteBuffer.allocateDirect(lightPos.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        p.put(lightPos).position(0);
        fire.linkVertex(p, "position", 4);
        fire.linkUniform1f(time, "time");
        fire.linkUniform1f(80.0f, "size"); // 50
        fire.linkMatrix(mMMatrix, "model");
        fire.linkMatrix(mVMatrix, "view");
        fire.linkMatrix(mProjMatrix, "projection");
        time += 1.0f;
        gl.glEnable(gl.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        gl.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    private String loadShaderFromFile(Context context, int idRes) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            InputStream input = context.getResources().openRawResource(idRes);
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(input));
            String line;

            while ((line = bufReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }

            bufReader.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (Resources.NotFoundException ex) {
            ex.printStackTrace();
        }

        return stringBuilder.toString();
    }

    private void loadModels(int[] modelsId) {
        int ind = 0;
        for (int i : modelsId) {
            ObjLoader objLoader = new ObjLoader(context, i, ind);
            ind++;
            FloatBuffer vBuf = ByteBuffer.allocateDirect(objLoader.positions.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            vBuf.put(objLoader.positions).position(0);

            FloatBuffer tBuf = ByteBuffer.allocateDirect(objLoader.textureCoordinates.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            tBuf.put(objLoader.textureCoordinates).position(0);

            FloatBuffer nBuf = ByteBuffer.allocateDirect(objLoader.normals.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            nBuf.put(objLoader.normals).position(0);

            Vector<FloatBuffer> t = new Vector<>();
            t.add(vBuf);
            t.add(tBuf);
            t.add(nBuf);

            models.add(t);
            numFaces.add(objLoader.numFaces);
        }
    }

    private void loadTextures(int[] resourceId, GL10 gl) {
        final int[] textureIds = new int[resourceId.length];

        gl.glGenTextures(resourceId.length, textureIds, 0);

        for (int i = 0; i < textureIds.length; i++) {
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resourceId[i]);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[i]);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

            bmp.recycle();
        }
    }

    private void drawModel(int ModelId, int TextureId, GL10 gl) {
        float[] lightColor = {1, 1, 1};
        shader.linkVertex(models.get(ModelId).get(0), "a_vertex", 3);
        shader.linkMatrix(mMMatrix, "model");
        shader.linkMatrix(mVMatrix, "view");
        shader.linkMatrix(mProjMatrix, "projection");
        shader.linkVertex(models.get(ModelId).get(1), "a_TexCord", 2);
        shader.linkVertex(models.get(ModelId).get(2), "a_normal", 3);
        shader.linkUniform3f(cameraPos, "u_camera");
        shader.linkUniform3f(lightPos, "u_lightPosition");
        shader.linkUniform3f(lightColor, "u_lightColor");
        shader.linkUniform1i(TextureId, "u_TextureUnit");

        gl.glDrawArrays(GLES20.GL_TRIANGLES, 0, numFaces.get(ModelId));
    }
}