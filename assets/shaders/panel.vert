#version 330 core
layout (location = 0) in vec2 vertPos;

uniform vec2 pos;
uniform vec2 size;
layout (std140) uniform data2D
{
    mat4 viewProjMat;
};

out vec2 fragUV;

void main() {
  gl_Position = viewProjMat * vec4(vertPos * size + pos, 0, 1);
  fragUV = vertPos;
}