#version 430 core
layout (location = 0) in vec2 vertPos;

const int size = 848;
const float heightScale = 400;
const int smoothFactor = 2;
uniform mat4 modelMat;

out vec3 fragNorm;
out vec2 fragUV;

layout (std140) uniform data3D
{
    mat4 viewProjMat;
};
layout(binding=0) uniform sampler2D heightMap;

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
float getHeight(vec2 coord) {
    vec4 col = texture(heightMap, getUV(coord));
    float h = col.r;
    return h * heightScale;
}
vec4 getPos(vec2 coord, float height) {
  return vec4(coord.x, 0, coord.y, 1);
  return vec4(
    coord.x - size/2,
    height,
    coord.y - size/2,
    1
  );
}

void main() {
  vec2 coord = getCoord(gl_InstanceID);
  vec2 UV = getUV(coord);
  float h = getHeight(coord);

  vec3 off = vec3(smoothFactor,smoothFactor, 0.0);
  float hL = getHeight(coord - off.xz);
  float hR = getHeight(coord + off.xz);
  float hD = getHeight(coord - off.zy);
  float hU = getHeight(coord + off.zy);

  vec3 normal;
  normal.x = hL - hR;
  normal.y = 2;
  normal.z = hD - hU;
  normal = normalize(normal);

  gl_Position = viewProjMat * (modelMat * getPos(coord, h));
  fragNorm = mat3(modelMat) * normal;
  fragUV = UV;
}