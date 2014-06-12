package com.catgarden.android.objects;

import static android.opengl.GLES20.*;
import static com.catgarden.android.Constants.BYTES_PER_FLOAT;

import com.catgarden.android.data.SpriteVertexArray;
import com.catgarden.android.programs.SpriteShaderProgram;

public class SpriteAnimation4x2 {
	private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int V_STRIDE = POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT;
    private static final int T_STRIDE = TEXTURE_COORDINATES_COMPONENT_COUNT * BYTES_PER_FLOAT;
    
    private static final float[] VERTEX_DATA = {
        // Order of coordinates: X, Y
    	   0f,    0f,  
          -0.5f, -1f,   
           0.5f, -1f,   
           0.5f,  1f,   
          -0.5f,  1f,   
          -0.5f, -1f };
    
    private static final float[] TEXTURE_DATA = {
        // Order of coordinates: S, T
    	
    	/*0.125f, 0.125f,
    	0f, 0.25f,  
    	0.25f, 0.25f, 
    	0.25f, 0f, 
    	0f, 0f, 
    	0f, 0.25f }; this one upside down*/
    
    	0.25f, 0.125f,
    	0.5f, 0f,
    	0f, 0f,
    	0f, 0.25f, 
    	0.5f, 0.25f, 
    	0.5f, 0f};
    
    private final SpriteVertexArray spriteVertexArray;
    
    public SpriteAnimation4x2() {
    	spriteVertexArray = new SpriteVertexArray(VERTEX_DATA, TEXTURE_DATA);
    }
    
    public void bindData(SpriteShaderProgram spriteProgram) {
    	spriteVertexArray.setVertexAttribPointer(
            0, 
            spriteProgram.getPositionAttributeLocation(), 
            POSITION_COMPONENT_COUNT,
            V_STRIDE,
            spriteProgram.getTextureCoordinatesAttributeLocation(),
            TEXTURE_COORDINATES_COMPONENT_COUNT,
            T_STRIDE
            ); 
    }
        
    public void draw() {                                
        //glDrawArrays(GL_TRIANGLES, 0, 6);
    	glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }    

}
