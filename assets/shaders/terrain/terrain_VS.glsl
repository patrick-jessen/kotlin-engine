#version 430 core
layout (location = 0) in vec2 vertPos;

uniform mat4 modelMat;
layout(binding=0) uniform sampler2D heightMap;

out VSOut {
    vec2 UV;
} vsOut;
int size = 5;

vec2 getCoord(int quad) {
  float X = quad % size;
  float Y = quad / size;
  X += vertPos.x;
  Y += vertPos.y;
  return vec2(X, Y);
}
vec2 getUV(vec2 coord) {
    return coord / size;
}

void main() {
    vec2 coord = getCoord(gl_InstanceID);
    vec2 UV = getUV(coord);

    gl_Position = vec4(coord.x, 0, coord.y, 1);
    vsOut.UV = UV;
}