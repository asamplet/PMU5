package ru.kapmax.kurswork;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class ObjLoader {
    public final int numFaces;

    public final float[] normals;
    public final float[] textureCoordinates;
    public final float[] positions;

    public ObjLoader(Context context, int file, int ind) {
        Vector<Float> vertices = new Vector<>();
        Vector<Float> normals = new Vector<>();
        Vector<Float> textures = new Vector<>();
        Vector<String> faces = new Vector<>();

        BufferedReader reader = null;

        try {
            InputStreamReader in = new InputStreamReader(context.getResources().openRawResource(file));
            reader = new BufferedReader(in);

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "v": {
                        vertices.add(Float.valueOf(parts[1]));
                        vertices.add(Float.valueOf(parts[2]));
                        vertices.add(Float.valueOf(parts[3]));
                        break;
                    }
                    case "vt": {
                        textures.add(Float.valueOf(parts[1]));
                        textures.add(Float.valueOf(parts[2]));
                        break;
                    }
                    case "vn": {
                        normals.add(Float.valueOf(parts[1]));
                        normals.add(Float.valueOf(parts[2]));
                        normals.add(Float.valueOf(parts[3]));
                        break;
                    }
                    case "f": {
                        faces.add(parts[1]);
                        faces.add(parts[2]);
                        faces.add(parts[3]);
                        break;
                    }
                }
            }
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        numFaces = faces.size();
        this.normals = new float[numFaces * 3];
        textureCoordinates = new float[numFaces * 2];
        positions = new float[numFaces * 3];
        int positionIndex = 0;
        int normalIndex = 0;
        int textureIndex = 0;

        for (String face : faces) {
            String[] parts = face.split("/");

            int index = 3 * (Short.parseShort(parts[0]) - 1);
            positions[positionIndex++] = vertices.get(index++);
            positions[positionIndex++] = vertices.get(index++);
            positions[positionIndex++] = vertices.get(index);

            index = 2 * (Short.parseShort(parts[1]) - 1);
            textureCoordinates[textureIndex++] = textures.get(index++);
            textureCoordinates[textureIndex++] = textures.get(index);

            index = 3 * (Short.parseShort(parts[2]) - 1);
            this.normals[normalIndex++] = normals.get(index++);
            this.normals[normalIndex++] = normals.get(index++);
            this.normals[normalIndex++] = normals.get(index);
        }
    }
}
