#version 430 core
layout (triangles) in;

const float scaleFactor = 10;
const float smoothFactor = 0.1;

out vec2 fragUV;
out vec3 fragNorm;

in TCSOut {
    vec2 UV;
} tesIn[];

layout(binding=0) uniform sampler2D heightMap;
layout (std140) uniform data3D
{
    mat4 viewProjMat;
};
uniform mat4 modelMat;

float getHeight(vec2 coord) {
    return texture(heightMap, coord).r * scaleFactor;
}

void main(void){
    fragUV = (
        gl_TessCoord.x * tesIn[0].UV +
        gl_TessCoord.y * tesIn[1].UV +
        gl_TessCoord.z * tesIn[2].UV
    );

    vec3 off = vec3(smoothFactor,smoothFactor, 0.0);
    float hL = getHeight(fragUV - off.xz);
    float hR = getHeight(fragUV + off.xz);
    float hD = getHeight(fragUV - off.zy);
    float hU = getHeight(fragUV + off.zy);

    vec3 normal;
    normal.x = hL - hR;
    normal.y = 2;
    normal.z = hD - hU;
    normal = normalize(normal);

    gl_Position = (
        gl_TessCoord.x * gl_in[0].gl_Position +
        gl_TessCoord.y * gl_in[1].gl_Position +
        gl_TessCoord.z * gl_in[2].gl_Position
    );
    fragNorm = mat3(modelMat) * normal;

    gl_Position.y += getHeight(fragUV);
    gl_Position = viewProjMat * modelMat * gl_Position;
}