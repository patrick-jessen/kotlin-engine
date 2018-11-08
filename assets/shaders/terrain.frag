#version 330 core
out vec4 fragColor;

in vec3 fragNorm;
in vec2 fragUV;

in float height;
uniform sampler2D tex;

void main() {
  float power = dot(fragNorm, normalize(vec3(1, 1, 1)));
  fragColor = vec4(height, height, height, 1);// * power;
  //fragColor = vec4(fragUV, 0, 1);
  //fragColor = texture2D(tex, fragUV);
}