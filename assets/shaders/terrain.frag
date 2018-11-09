#version 330 core
out vec4 fragColor;

in vec3 fragNorm;
in vec2 fragUV;

in float height;
uniform sampler2D diffuseTex;
uniform sampler2D heightMap;

void main() {
  float power = dot(fragNorm, normalize(vec3(1, 1, 1)));
  fragColor = texture(diffuseTex, fragUV) * power;
  //fragColor = vec4(fragNorm, 1);
}