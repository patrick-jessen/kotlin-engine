#version 330 core
const int size = 848;
const float heightScale = 100;

uniform mat4 viewProjMat;
uniform mat4 modelMat;

out vec3 fragNorm;
out vec2 fragUV;

uniform sampler2D heightMap;

vec3 getPos(int triangle, int vertex, bool setUV) {
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
  vec2 UV = vec2(X, Y) / size;
  if(setUV) fragUV = UV;
  vec4 col = texture(heightMap, UV);
  return vec3(
    X - size/2,
    col.r * heightScale - heightScale/2,
    Y - size/2
  );
}

void main() {
  vec3 pos = getPos(gl_InstanceID, gl_VertexID, true);

  int otherVert1;
  int otherVert2;
  if(gl_VertexID == 0) {
    otherVert1 = 1;
    otherVert2 = 2;
  } else if(gl_VertexID == 1) {
    otherVert1 = 2;
    otherVert2 = 0;
  } else {
    otherVert1 = 0;
    otherVert2 = 1;
  }
  vec3 otherPos1 = getPos(gl_InstanceID, otherVert1, false);
  vec3 otherPos2 = getPos(gl_InstanceID, otherVert2, false);
  vec3 norm = normalize(cross(otherPos1-pos, otherPos2-pos));

  gl_Position = viewProjMat * (modelMat * vec4(pos, 1.0));
  fragNorm = mat3(modelMat) * norm;
}