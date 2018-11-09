#version 330 core
layout (location = 0) in vec2 vertPos;

const float fontWidth = 464;
const float height = 24;
const float widths[] = {
//a
12,13,12,14,13,8,14,13,
//i
6,6,13,6,21,13,15,14,
//q
14,9,10,9,12,14,19,13,13,12,
7, // space
//0
15, 12, 14, 13, 16, 14, 14, 14, 14, 13
};

uniform vec2 pos;
uniform mat4 str;
layout (std140) uniform data2D
{
    mat4 viewProjMat;
};

out vec2 fragUV;

float getCharWidth(int char) {
    return widths[char];
}

float getUVOffset(int char) {
    float x = 0;
    for(int c = char-1; c >= 0; c--) {
        x += getCharWidth(c);
    }
    return x / fontWidth;
}

int getChar(int idx) {
    int char = int(str[idx%4][idx/4]);
    if(char == 32)
        return 26;
    if(char >= 48 && char <= 57) // numbers
        return 27+char-48;
    else
        return char - 97;
}

void main() {
  int char = getChar(gl_InstanceID);
  float x = 0;
  float w = getCharWidth(char);

  for(int c = gl_InstanceID-1; c >= 0; c--) {
    int currChar = getChar(c);
    x += getCharWidth(currChar);
  }


  vec2 p = vertPos * vec2(w, height) + vec2(x, 0) + pos;
  gl_Position = viewProjMat * vec4(p, 0, 1.0);

  vec2 UV = vertPos;
  UV.x *= w/fontWidth;
  UV.x += getUVOffset(char);
  fragUV = UV;
}