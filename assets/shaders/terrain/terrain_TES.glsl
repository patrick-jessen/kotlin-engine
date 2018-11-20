#version 430 core
layout (triangles) in;

const float scaleFactor = 10;
const float smoothFactor = 0.1;

in vec2 tesUV[];
out vec2 fragUV;
out vec3 fragNorm;

layout(binding=0) uniform sampler2D heightMap;
uniform mat4 modelMat;

float getHeight(vec2 coord) {
    return texture(heightMap, coord).r * scaleFactor;
}

void main(void){
    fragUV = (
        gl_TessCoord.x * tesUV[0] +
        gl_TessCoord.y * tesUV[1] +
        gl_TessCoord.z * tesUV[2]
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
    //fragNorm = mat3(modelMat) * normal;
    fragNorm = normal;
    //fragNorm = vec3(hL, hR, hD);

    gl_Position.y += getHeight(fragUV);
}