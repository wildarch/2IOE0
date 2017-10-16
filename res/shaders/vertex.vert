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

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

//shadows
out vec4 mlightviewVertexPos;
out mat4 outModelViewMatrix;

uniform mat4 modelLightViewMatrix;
uniform mat4 orthoProjectionMatrix;

// a value that is approximately 0, but large enough to prevent rounding errors
float fragmentSize = 0.001;
vec3 diffVec = boundingMax - boundingMin;
vec3 gravityMiddle = boundingMin + (0.5 * boundingMax);

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

// calculates the normal given the UNCHANGED position vector and both unchanged and changed position
// @param O: unchanged position of the fragment
// @param N: unchanged normal vector
// @param newO position of fragment after translation (to prevent recalculation)
vec3 normalVector(vec3 O, vec3 N, vec3 newO) {

	vec3 Henk = vec3(0.0, 1.0, 1.0);	// Henk, random vector;
	if (N == Henk){
		Henk = vec3(1.0, 1.0, 0.0); // make sure it is not equal to N
	}
	
	vec3 OP = cross(N, Henk); // some vector orthogonal to N
	vec3 OQ = cross(N, OP); // some vector orthogonal to N and OP
	
	OP = normalize(OP) * fragmentSize; // make length of them "approximately 0"
	OQ = normalize(OQ) * fragmentSize;

	// if OPxOQ is not in the direction of the normal, s is false
	// after translation, OPxOQ should be multiplied with -1 if s is false to receive new normal
	bool s = dot(cross(OP, OQ),N) > 0.0;
	
	vec3 P = O + OP; // positions on the hitplane on O
	vec3 Q = O + OQ;
	
	P = revert(bendOut(mapToUnit(P))); // apply transformations to hitplane
	Q = revert(bendOut(mapToUnit(Q)));

	OP = P - newO;
	OQ = Q - newO;
	
	vec3 NewNormal = cross(OP, OQ); // calculate new normal of this hitplane
	
	if (!s){
		NewNormal *= -1.0;
	}
	
	return normalize(NewNormal);
}

void main() {

    vec3 transformedPosition;
    vec3 mvNormal;

	// calculate normal and transform to view space
	if (bounceDegree == 0.0 ){
	    transformedPosition = position;
		mvNormal = vertexNormal;
	} else {
        // calculate new vector
        transformedPosition = revert(bendOut(mapToUnit(position)));
		mvNormal = vertexNormal;
//		mvNormal = normalVector(position, vertexNormal, mvVertexPosition);
	}

	vec4 mvPosition = modelViewMatrix * vec4(transformedPosition, 1.0);
    gl_Position = projectionMatrix * mvPosition;

	mvVertexNormal = normalize(modelViewMatrix * vec4(mvNormal, 0.0)).xyz;
    mvVertexPosition = mvPosition.xyz;

	// pass texture
	outTexture = inTexture;

	// shadow calculations
	mlightviewVertexPos = orthoProjectionMatrix * modelLightViewMatrix * vec4(position, 1.0);
    outModelViewMatrix = modelViewMatrix;
}