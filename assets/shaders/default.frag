#version 330 core
out vec4 fragColor;

in vec3 fragNorm;
in vec2 fragUV;
uniform sampler2D tex;

void main() {
  float power = dot(fragNorm, normalize(vec3(1, 1, 1)));
  fragColor = texture(tex, fragUV) * max(power, 0.2);
}