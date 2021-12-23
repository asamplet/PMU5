uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform float time;
uniform float size;
attribute vec3 position;
varying float v_time;

void main() {
    v_time = time;
    gl_Position = projection * view * model * vec4(position.xyz, 1.0);
    gl_PointSize = size;
}
