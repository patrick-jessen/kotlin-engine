#version 430 core
layout (vertices = 3) out;

uniform float tessLevel;

in VSOut {
    vec2 UV;
} tcsIn[];

out TCSOut {
    vec2 UV;
} tcsOut[];

void main(void){
    if (gl_InvocationID == 0){
        gl_TessLevelInner[0] = tessLevel;
        gl_TessLevelOuter[0] = tessLevel;
        gl_TessLevelOuter[1] = tessLevel;
        gl_TessLevelOuter[2] = tessLevel;
    }
    tcsOut[gl_InvocationID].UV = tcsIn[gl_InvocationID].UV;
    gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
}