#version 430 core
layout (triangles) in;

in vec2 tesUV[];
out vec2 fragUV;

layout(binding=0) uniform sampler2D heightMap;

void main(void){
    gl_Position = (
        gl_TessCoord.x * gl_in[0].gl_Position +
        gl_TessCoord.y * gl_in[1].gl_Position +
        gl_TessCoord.z * gl_in[2].gl_Position
    );

    fragUV = (
        gl_TessCoord.x * tesUV[0] +
        gl_TessCoord.y * tesUV[1] +
        gl_TessCoord.z * tesUV[2]
    );

    gl_Position.y += texture(heightMap, fragUV).r * 10;
}