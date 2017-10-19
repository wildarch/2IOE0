// @author Geert van Ieperen

#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 inTexture;
layout (location = 2) in vec3 vertexNormal;

// current spread
uniform float bounceDegree;

// minimum coordinates of the bounding box
uniform vec3 boundingMin;
// idem maximum
uniform vec3 boundingMax;


out vec2 outTexture;
// normal of the vertex
out vec3 mvVertexNormal;
// position of the vertex
out vec3 mvVertexPosition;

//shadows
out vec4 mlightviewVertexPos;
out mat4 outModelViewMatrix;

uniform mat4 modelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;

// a value that is approximately 0, but large enough to prevent rounding errors
float fragmentSize = 0.001;
vec3 diffVec = boundingMax - boundingMin;
vec3 gravityMiddle = 0.5 * (boundingMin + boundingMax);

// bends out the vector of a mapToUnitd system
vec3 bendOut(vec3 p) {
	// bend-out factor
	float factor = 1.0 + (1.0 - (p.y * p.y)) * bounceDegree;
	// V = (1 + 4/3 * B); to keep V constant we reduce the size with the inverse
	float yBend = (1.0 / (1.0 + ((2.0/3.0) * bounceDegree)));
	// bounce out the horizontal edges
	vec3 result = vec3(p.x * factor, ((p.y + 1.0) * yBend) - 1.0, p.z * factor);

	return result;
}

// pushes the object to a 2*2*2 cube
vec3 mapToUnit(vec3 p) {
	// move gravitymiddle to 0,0,0
	p = (p - gravityMiddle);
	// multiply with the inverse of the difference vector
	return vec3(p.x / diffVec.x, p.y / diffVec.y, p.z / diffVec.z) * 2;

}

// reverts the action of mapToUnit(p)
vec3 revert(vec3 p){
	// multiply with difference vector
	// move middle back to gravitymiddle
	return (vec3(p.x * diffVec.x, p.y * diffVec.y, p.z * diffVec.z) + gravityMiddle) * 0.5;
}

void main() {

    vec3 transformedPosition;

	// calculate normal and transform to view space
	if (bounceDegree == 0.0){
	    transformedPosition = position;
	} else {
        // calculate new vector
        transformedPosition = revert(bendOut(mapToUnit(position)));
	}

    gl_Position = orthoProjectionMatrix * modelLightViewMatrix * vec4(transformedPosition, 1.0f);
}
