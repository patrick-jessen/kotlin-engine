#version 410 core
out vec4 fragColor;

in vec3 fragNorm;
in vec2 fragUV;
uniform sampler2D diffuseTex;

void main() {
  float power = dot(fragNorm, normalize(vec3(1, 1, 1)));
  //fragColor = texture(diffuseTex, fragUV) * power;
  fragColor = vec4(fragNorm, 1);
  //fragColor = vec4(fragUV, 0, 1);
}