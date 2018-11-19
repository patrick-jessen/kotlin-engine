#version 330 core
out vec4 fragColor;
in vec2 fragUV;

uniform vec4 color;
uniform sampler2D font;

void main() {
  vec4 col = texture(font, fragUV);
  if(col.a == 0) discard;
  fragColor = col * color;

//  fragColor.a = 0.3;
  //fragColor = vec4(1,0,0,1);
}