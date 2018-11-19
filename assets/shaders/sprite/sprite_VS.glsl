#version 330 core
layout (location = 0) in vec2 vertPos;

void handleSlicedSprite();

uniform vec2 pos;
uniform vec2 size;
uniform vec4 slicePoints;
uniform sampler2D tex;

layout (std140) uniform data2D
{
    mat4 viewProjMat;
};
out vec2 fragUV;

bool isSliced() {
    return (slicePoints.x*slicePoints.x + slicePoints.y*slicePoints.y + slicePoints.z*slicePoints.z + slicePoints.w*slicePoints.w) > 0;
}

void main() {
  if(isSliced()) {
    handleSlicedSprite();
    return;
  }

  gl_Position = viewProjMat * vec4(vertPos * size + pos, 0, 1);
  fragUV = vertPos;
}

void handleSlicedSprite() {
  vec2 texSize = textureSize(tex, 0);
  vec4 texSlicePoints = vec4(
    slicePoints.x/texSize.x,
    slicePoints.y/texSize.x,
    slicePoints.z/texSize.y,
    slicePoints.w/texSize.y
  );
  vec2 s;
  vec2 p;

  if(gl_InstanceID == 0) {
    s = slicePoints.xz;

    fragUV = vertPos * texSlicePoints.xz;
  }
  else if(gl_InstanceID == 1) {
    s = vec2(slicePoints.x, size.y-slicePoints.z-slicePoints.w);
    p = vec2(0, slicePoints.z);

    float Vs = 1 - texSlicePoints.z - texSlicePoints.w;
    fragUV = vertPos * vec2(texSlicePoints.x, Vs) + vec2(0, texSlicePoints.z);
  }
  else if(gl_InstanceID == 2) {
    s = slicePoints.xw;
    p = vec2(0, size.y-slicePoints.w);

    fragUV = vertPos * texSlicePoints.xw + vec2(0, 1-texSlicePoints.w);
  }
  else if(gl_InstanceID == 3) {
    s = vec2(size.x-slicePoints.x-slicePoints.y, slicePoints.w);
    p = vec2(slicePoints.x, 0);

    float Us = 1 - texSlicePoints.x - texSlicePoints.y;
    fragUV = vertPos * vec2(Us, texSlicePoints.z) + vec2(texSlicePoints.x, 0);
  }
  else if(gl_InstanceID == 4) {
    s = vec2(
      size.x-slicePoints.x-slicePoints.y,
      size.y-slicePoints.z-slicePoints.w
    );
    p = vec2(slicePoints.x, slicePoints.w);

    float Us = 1 - texSlicePoints.x - texSlicePoints.y;
    float Vs = 1 - texSlicePoints.z - texSlicePoints.w;
    fragUV = vertPos * vec2(Us,Vs) + texSlicePoints.xz;
  }
  else if(gl_InstanceID == 5) {
    s = vec2(size.x-slicePoints.x-slicePoints.y, slicePoints.w);
    p = vec2(slicePoints.x, size.y - slicePoints.w);

    float Us = 1 - texSlicePoints.x - texSlicePoints.y;
    fragUV = vertPos * vec2(Us, texSlicePoints.w) + vec2(texSlicePoints.x, 1-texSlicePoints.w);
  }
  else if(gl_InstanceID == 6) {
    s = slicePoints.yz;
    p = vec2(size.x - slicePoints.y, 0);

    fragUV = vertPos * texSlicePoints.yz + vec2(1-texSlicePoints.y, 0);
  }
  else if(gl_InstanceID == 7) {
    s = vec2(slicePoints.y, size.y-slicePoints.z-slicePoints.w);
    p = vec2(size.x - slicePoints.y, slicePoints.z);

    float Vs = 1 - texSlicePoints.y - texSlicePoints.w;
    fragUV = vertPos * vec2(texSlicePoints.y, Vs) + vec2(1-texSlicePoints.y, texSlicePoints.z);
  }
  else if(gl_InstanceID == 8) {
    s = slicePoints.yw;
    p = vec2(size.x - slicePoints.y, size.y - slicePoints.w);

    fragUV = vertPos * texSlicePoints.yw + vec2(1-texSlicePoints.y, 1-texSlicePoints.w);
  }

  gl_Position = viewProjMat * vec4(vertPos * s + pos + p, 0, 1);
}