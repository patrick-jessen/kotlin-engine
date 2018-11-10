#version 330 core
layout (location = 0) in vec2 vertPos;

const int numCharsInFont = 95;
const float ratios[] = {
  // symbols
  0.28,0.28,0.43,0.57,0.57,0.77,0.77,0.28,0.3,0.3,0.43,0.6,0.28,0.37,0.28,0.4,
  // numbers
  0.57,0.57,0.57,0.57,0.57,0.57,0.57,0.57,0.57,0.57,
  // symbols
  0.28,0.28,0.6,0.6,0.6,0.5,0.7,
  // uppercase letters
  0.73,0.57,0.6,0.7,0.53,0.43,0.7,0.73,0.27,0.27,0.6,0.53,0.9,0.7,0.8,0.53,0.8,0.54,0.53,0.47,0.7,0.6,0.93,0.63,0.54,0.6,
  // symbols
  0.3,0.54,0.3,0.6,0.5,0.4,
  // lowercase letters
  0.47,0.54,0.46,0.58,0.53,0.26,0.58,0.54,0.24,0.24,0.48,0.24,0.87,0.54,0.57,0.59,0.59,0.33,0.43,0.33,0.54,0.53,0.77,0.53,0.5,0.47,
  // symbols
  0.3,0.6,0.3,0.6
};

uniform int size;
uniform vec2 pos;
uniform int str[64];
uniform sampler2D font;
layout (std140) uniform data2D
{
    mat4 viewProjMat;
};

out vec2 fragUV;

float getCharRatio(int char) {
    return ratios[char];
}

int getChar(int idx) {
    int dataIdx = idx / 4;
    int shift = 8 * (3 - (idx % 4));
    return ((str[dataIdx]>>shift) & 0xff) - 32;
}

void main() {
  vec2 texSize = textureSize(font, 0);
  float height = texSize.y / numCharsInFont;

  int char = getChar(gl_InstanceID);
  float x = 0;
  for(int c = gl_InstanceID-1; c >= 0; c--) {
    int currChar = getChar(c);
    x += height * getCharRatio(currChar);
  }

  vec2 p = vertPos * vec2(texSize.x, height) + vec2(x, 0);
  p *= float(size)/height;
  p += pos;
  gl_Position = viewProjMat * vec4(p, 0, 1.0);

  vec2 UV = vertPos;
  UV.y /= numCharsInFont;
  UV.y += float(char)/numCharsInFont;
  fragUV = UV;
}