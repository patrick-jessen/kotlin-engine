#version 330 core
layout (location = 0) in vec3 vertPos;
layout (location = 1) in vec3 vertNorm;
layout (location = 2) in vec2 vertUV;

uniform mat4 viewProjMat;
uniform mat4 modelMat;

out vec3 fragNorm;
out vec2 fragUV;

uniform sampler2D tex;

out float height;

void main() {
  const int size = 2048;

  int X = (gl_InstanceID / 2) % size;
  int Y = (gl_InstanceID / 2) / size;
  if(gl_InstanceID % 2 == 0) {
    if(gl_VertexID == 1) X++;
    else if(gl_VertexID == 2) Y++;
  }
  else {
    if(gl_VertexID == 0) Y++;
    else if(gl_VertexID == 1) X++;
    else if(gl_VertexID == 2) {
        X++;
        Y++;
    }
  }

  vec2 UV = vec2(float(X)/size, float(Y)/size);
  vec4 col = texture2D(tex, UV);
  vec3 pos = vec3(X-size/2, col.r*400-200, Y-size/2);


  gl_Position = viewProjMat * (modelMat * vec4(pos, 1.0));
  fragNorm = vec3(modelMat * vec4(vertNorm, 1.0));
  fragUV = UV;
  height = col.r;
}