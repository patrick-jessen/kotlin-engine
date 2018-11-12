#version 330 core
out vec4 fragColor;
in vec2 fragUV;
uniform sampler2D tex;

uniform vec4 color;

void main() {
  vec4 col = texture(tex, fragUV);
  fragColor = col * color;
}