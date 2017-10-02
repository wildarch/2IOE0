#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 inTexture;
layout (location=2) in vec3 vertexNormal;

out vec2 outTexture;
out vec3 mvVertexNormal;
out vec3 mvVertexPosition;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    vec4 mvPosition = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * mvPosition;
    outTexture = inTexture;
    mvVertexNormal = normalize(modelViewMatrix * vec4(vertexNormal, 0.0)).xyz;
    mvVertexPosition = mvPosition.xyz;
}