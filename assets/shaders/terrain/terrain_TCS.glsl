#version 410 core
layout (vertices = 3) out;

uniform float tessLevel;

in VSOut {
    vec2 UV;
} tcsIn[];

out TCSOut {
    vec2 UV;
} tcsOut[];

layout (std140) uniform data3D
{
    mat4 viewProjMat;
    vec3 cameraPos;
};

float level (vec4 poz1, vec4 poz2) {
    float lod=1;
	float d=distance(poz1, poz2);


	if(d < 5) lod = 10;
	else if(d < 7) lod = 5;
	else if(d < 10) lod=2;
	return lod;
}

void main(void){
    if (gl_InvocationID == 0){
        vec3 d1=gl_in[1].gl_Position.xyz+(gl_in[2].gl_Position.xyz-gl_in[1].gl_Position.xyz)/2;
        vec3 d2=gl_in[0].gl_Position.xyz+(gl_in[2].gl_Position.xyz-gl_in[0].gl_Position.xyz)/2;
        vec3 d3=gl_in[0].gl_Position.xyz+(gl_in[1].gl_Position.xyz-gl_in[0].gl_Position.xyz)/2;

        float e0=level(vec4(d1,1.0),vec4(cameraPos,1.0));
        float e1=level(vec4(d2,1.0),vec4(cameraPos,1.0));
        float e2=level(vec4(d3,1.0),vec4(cameraPos,1.0));
        float m=min(e0,min(e1,e2));

        gl_TessLevelInner[0] = floor((min(e0,min(e1,e2))+max(e0,max(e1,e2)))/2);
        gl_TessLevelOuter[0] = e0;
        gl_TessLevelOuter[1] = e1;
        gl_TessLevelOuter[2] = e2;
    }
    tcsOut[gl_InvocationID].UV = tcsIn[gl_InvocationID].UV;
    gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
}