#version 430 core
layout (location = 0) in vec2 vertPos;

layout (std140) uniform data3D
{
    mat4 viewProjMat;
};
uniform mat4 modelMat;

void main() {
    gl_Position = viewProjMat * modelMat * vec4(vertPos.x, 0, vertPos.y, 1);
}