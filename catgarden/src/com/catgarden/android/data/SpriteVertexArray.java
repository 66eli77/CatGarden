/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.catgarden.android.data;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.catgarden.android.Constants.BYTES_PER_FLOAT;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SpriteVertexArray {
    private final FloatBuffer vertexFloatBuffer;
    private final FloatBuffer textureCoordinatesFloatBuffer;
    
    public SpriteVertexArray(float[] vertexData, float[] textureData) {
    	vertexFloatBuffer = ByteBuffer
            .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData);
    	
    	textureCoordinatesFloatBuffer = ByteBuffer
                .allocateDirect(textureData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
    }
        
    public void setVertexAttribPointer(int dataOffset, int V_attributeLocation,
            int vertexComponentCount, int vertexStride, int T_attributeLocation,
            int textureComponentCount, int textureStride) {        
        	vertexFloatBuffer.position(dataOffset);        
            glVertexAttribPointer(V_attributeLocation, vertexComponentCount,
                GL_FLOAT, false, vertexStride, vertexFloatBuffer);
            glEnableVertexAttribArray(V_attributeLocation);
            vertexFloatBuffer.position(0);
            
            textureCoordinatesFloatBuffer.position(dataOffset);	
            glVertexAttribPointer(T_attributeLocation, textureComponentCount,
                    GL_FLOAT, false, textureStride, textureCoordinatesFloatBuffer);
            glEnableVertexAttribArray(T_attributeLocation);
            textureCoordinatesFloatBuffer.position(0);
        }
}
