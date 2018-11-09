#version 330 core
out vec4 fragColor;
in vec2 fragUV;
uniform sampler2D tex;

void main() {
  vec4 col = texture(tex, fragUV);
  if(col.a == 0) discard;
  fragColor = col;
}