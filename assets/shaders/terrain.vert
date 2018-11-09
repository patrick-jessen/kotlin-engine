#version 330 core
const int size = 848;
const float heightScale = 50;
const int smoothFactor = 2;

uniform mat4 modelMat;

out vec3 fragNorm;
out vec2 fragUV;

layout (std140) uniform data3D
{
    mat4 viewProjMat;
};

uniform sampler2D heightMap;

vec2 getUV(int triangle, int vertex) {
  int X = (triangle / 2) % size;
  int Y = (triangle / 2) / size;
  if(triangle % 2 == 0) {
    if(vertex == 1) Y++;
    else if(vertex == 2) X++;
  }
  else {
    if(vertex == 0) {
        X++;
        Y++;
    }
    else if(vertex == 1) X++;
    else if(vertex == 2) Y++;
  }
  return vec2(X, Y) / size;
}
vec3 getPos(vec2 UV) {
  vec4 col = texture(heightMap, UV);
  return vec3(
    UV.x*size - size/2,
    col.r * heightScale - heightScale/2,
    UV.y*size - size/2
  );
}

void main() {
  vec2 UV = getUV(gl_InstanceID, gl_VertexID);
  vec3 pos = getPos(UV);

  vec3 otherPos1 = getPos(UV + vec2(1,0));
  vec3 otherPos2 = getPos(UV + vec2(0,1));
  vec3 prevPos1 = getPos(UV + vec2(-1,0));
  vec3 prevPos2 = getPos(UV + vec2(0,-1));
  vec3 norm = normalize(cross(otherPos2-pos, otherPos1-pos));
  //norm = cross(vec3(0, 0, 1), otherPos1 - prevPos1);

  gl_Position = viewProjMat * (modelMat * vec4(pos, 1.0));
  fragNorm = mat3(modelMat) * norm;
  fragUV = UV;
}