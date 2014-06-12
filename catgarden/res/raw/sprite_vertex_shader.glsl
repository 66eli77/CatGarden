attribute vec4 a_position;
attribute vec2 a_TextureCoordinates;
varying vec2 v_TextureCoordinates;
uniform mat4 u_Matrix;
uniform mat4 u_texture_matrix;

void main(){
	v_TextureCoordinates = (u_texture_matrix * vec4(a_TextureCoordinates, 0.0, 1.0)).xy;
	gl_Position = u_Matrix * a_position;
}