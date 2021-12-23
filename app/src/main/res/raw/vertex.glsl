uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
attribute vec3 a_vertex;
attribute vec2 a_TexCord;
attribute vec3 a_normal;
varying vec3 v_normal;
varying vec2 v_TexCord;
varying vec3 v_vertex;

void main() {
    v_vertex = vec3(model * vec4(a_vertex, 1.0));
    v_TexCord = a_TexCord;

    vec3 n_normal = normalize(a_normal);
    v_normal=n_normal;

    gl_Position = projection * view * vec4(v_vertex, 1.0);
}