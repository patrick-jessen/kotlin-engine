#version 330 core
layout (location = 0) in vec3 vertPos;
layout (location = 1) in vec3 vertNorm;
layout (location = 2) in vec2 vertUV;

uniform mat4 viewProjMat;
uniform mat4 modelMat;

out vec3 fragNorm;
out vec2 fragUV;

void main() {
  gl_Position = viewProjMat * modelMat * vec4(vertPos, 1.0);
  fragNorm = vec3(modelMat * vec4(vertNorm, 1.0));
  fragUV = vertUV;
}