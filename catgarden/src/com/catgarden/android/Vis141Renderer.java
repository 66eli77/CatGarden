package com.catgarden.android;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView.Renderer;

import com.catgarden.android.objects.Billboard;
import com.catgarden.android.objects.Skybox;
import com.catgarden.android.objects.SniperScope;
import com.catgarden.android.objects.SpriteAnimation4x4;
import com.catgarden.android.programs.SkyboxShaderProgram;
import com.catgarden.android.programs.SpriteShaderProgram;
import com.catgarden.android.programs.TextureShaderProgram;
import com.catgarden.android.util.Geometry;
import com.catgarden.android.util.MatrixHelper;
import com.catgarden.android.util.TextureHelper;
import com.catgarden.android.util.Geometry.*;
import com.catgarden.android.R;

public class Vis141Renderer implements Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] textureMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] invertedViewProjectionMatrix = new float[16];

    private SniperScope sniperSight;
    private List<Billboard> smurfs = new ArrayList<Billboard>();
    private SpriteAnimation4x4 spriteAnimationCat;
    private SpriteAnimation4x4 spriteAnimationFish;

    private TextureShaderProgram textureProgram;
    private SpriteShaderProgram spriteProgram;

    private int spriteTextureStarving;
    private int spriteTextureObese;
    private int spriteTexture0;
    private int spriteTexture1;
    private int spriteTexture2;
    private int spriteTexture3;
    private int spriteTexture4;
    private int spriteTexture5;
    private int spriteTextureFish1;
    private int spriteTextureRun;
    private int sniperSightTexture;
    
    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    private int skyboxTexture;
    
   // private boolean targetHit;
    private boolean targetHitCat;
    private boolean targetHitFish;
    private Point targetPositionCat;
    private Vector targetVectorCat; //store the direction and speed of the target
    private Point targetPositionFish;
    private Vector targetVectorFish; //store the direction and speed of the target
    
    Activity activity;
    MediaPlayer SoundMew;
    MediaPlayer SoundGun;
    MediaPlayer SoundEating;
    private int timeStart = 0;
    private int timeStart2 = 0;
    private int timeStart3 = 0;

    public Vis141Renderer(Context context) {
        this.context = context;
        activity = (Activity) context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_BLEND);
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        SoundMew = MediaPlayer.create(activity, R.raw.cat);
        SoundGun = MediaPlayer.create(activity, R.raw.m4a1gunshot);
        SoundEating = MediaPlayer.create(activity, R.raw.cateating);
        timeStart = (int)(System.currentTimeMillis());

        sniperSight = new SniperScope();
        smurfs.add(new Billboard());
        spriteAnimationCat = new SpriteAnimation4x4();
        spriteAnimationFish = new SpriteAnimation4x4();
        
        //initialize the target position and velocity
        targetPositionCat  = new Point(0f, -1.2f, -5f);
        targetVectorCat = new Vector( 0f, 0f, 0f);	//velocity
        targetPositionFish  = new Point(0f, 3f, -5f);
        targetVectorFish = new Vector( 0f, 0f, 0f);	//velocity

        textureProgram = new TextureShaderProgram(context);
        spriteProgram = new SpriteShaderProgram(context);

        spriteTextureStarving = TextureHelper.loadTexture(context, R.drawable.starvingending);
        spriteTextureObese = TextureHelper.loadTexture(context, R.drawable.sufferingending);
        spriteTexture0 = TextureHelper.loadTexture(context, R.drawable.cat0);
        spriteTexture1 = TextureHelper.loadTexture(context, R.drawable.cat1);
        spriteTexture2 = TextureHelper.loadTexture(context, R.drawable.cat2);
        spriteTexture3 = TextureHelper.loadTexture(context, R.drawable.cat3);
        spriteTexture4 = TextureHelper.loadTexture(context, R.drawable.cat4);
        spriteTexture5 = TextureHelper.loadTexture(context, R.drawable.cat5);
        spriteTextureFish1 = TextureHelper.loadTexture(context, R.drawable.flyingfish);
        spriteTextureRun = TextureHelper.loadTexture(context, R.drawable.catrun);
        sniperSightTexture = TextureHelper.loadTexture(context, R.drawable.sniperscope);
        
        //skybox
        skyboxProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox();
        skyboxTexture = TextureHelper.loadCubeMap(context, 
        		new int[] {R.drawable.left, R.drawable.right, R.drawable.bottom,
        		R.drawable.top, R.drawable.front, R.drawable.back});
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix,45,(float)width/(float)height, 1f,100f);
    }

    //cat
    private float[] texture_uv_coordinates_vertical = new float[16];
    private float[] texture_uv_coordinates_horizontal = new float[16];
    private int tickCounter = 0;
    private int fatty = 0;
    private double angleCat = 270f;
    private float xCat = 0f;
    private float zCat = 0f;
    //flying fish
    private float[] texture_uv_coordinates_vertical_fish = new float[16];
    private float[] texture_uv_coordinates_horizontal_fish = new float[16];
    private int tickCounterFish = 0;
    private float radiusFish = 5f;
    private double angleFish = 270f;
    private float xFish = 0f;
    private float zFish = 0f;
    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);
        
		int timeCurr = (int)(System.currentTimeMillis());
        
		targetPositionCat = targetPositionCat.translate(targetVectorCat);
		targetVectorCat.scale(0.99f);
                
        drawSkybox();  
        if(timeCurr - timeStart > 50000){
        	fatty --;
        	timeStart = (int)(System.currentTimeMillis());
        } 
        drawSniperScope();
// drawing procedure for flying fish
        if(triggerPulled){
        	if(targetPositionFish.y >= -1.3){            // define fish landing Y position
        		targetPositionFish.changeY(-0.07f);    // define fish falling speed
        	}
        	setIdentityM(modelMatrix, 0);
    		rotateM(modelMatrix, 0, mPitch, 1f, 0f, 0f);
    		rotateM(modelMatrix, 0, -mYaw, 0f, 1f, 0f);
    		translateM(modelMatrix, 0, targetPositionFish.x, targetPositionFish.y, targetPositionFish.z);
    		rotateM(modelMatrix, 0, mYaw, 0f, 1f, 0f);  //make sure the picture always facing you
    		//make sure the running girl is always facing you
    		//rotateM(modelMatrix, 0, mYaw - running, 0f, 1f, 0f);  
    	
    		tickCounterFish --;
    		if(tickCounterFish < 0){
    			texture_uv_coordinates_horizontal_fish[1] += 0.25f;
    			if (texture_uv_coordinates_horizontal_fish[1] >= 1f){
    				texture_uv_coordinates_horizontal_fish[1] = 0f;
    				texture_uv_coordinates_vertical_fish[1] += 0.25f;
    				if (texture_uv_coordinates_vertical_fish[1] >= 1f)
    					texture_uv_coordinates_vertical_fish[1] = 0.5f;
    			}
    			tickCounterFish = 13;  //define the frame rate of the sprite animation !!
    		}
    		setIdentityM(textureMatrix, 0);
    		translateM(textureMatrix, 0, texture_uv_coordinates_horizontal_fish[1], 
    				texture_uv_coordinates_vertical_fish[1], 0f); 
    	
    		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    	    	        
    		spriteProgram.useProgram();
    		spriteProgram.setUniforms(viewProjectionMatrix, textureMatrix, spriteTextureFish1);
    	}else{
    		tickCounterFish --;
    		if(tickCounterFish < 0){
        		//update fish sprite sheet texture
    			texture_uv_coordinates_horizontal_fish[1] += 0.25f;
    			if (texture_uv_coordinates_horizontal_fish[1] >= 1f){
    				texture_uv_coordinates_horizontal_fish[1] = 0f;
    			}
    			tickCounterFish = 10;  //define the frame rate of the sprite animation !!
    		}
    		texture_uv_coordinates_vertical_fish[1] = 0.25f;
        	
        	//update fish position
			//angleFish += 0.4;    //define fish speed
    		angleFish += 0.3;    //define fish speed
    		xFish = (float) (radiusFish*Math.cos(Math.toRadians(angleFish)));
    		zFish = (float) (radiusFish*Math.sin(Math.toRadians(angleFish)));
    		if(angleFish >= 360){
    			angleFish = 0;
    		}
    		targetPositionFish.assignXZ(xFish, zFish);
    		
        	setIdentityM(modelMatrix, 0);
    		rotateM(modelMatrix, 0, mPitch, 1f, 0f, 0f);
    		rotateM(modelMatrix, 0, -mYaw, 0f, 1f, 0f); 
    		translateM(modelMatrix, 0, targetPositionFish.x, targetPositionFish.y, targetPositionFish.z);  
    	
    		setIdentityM(textureMatrix, 0);
    		translateM(textureMatrix, 0, texture_uv_coordinates_horizontal_fish[1], 
    				texture_uv_coordinates_vertical_fish[1], 0f); 
    		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    		
    		//put the invertM here so sphere will follow the target
    		invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
    		
    		rotateM(modelMatrix, 0, mYaw, 0f, 1f, 0f);
    		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
        
    		spriteProgram.useProgram();
    		spriteProgram.setUniforms(viewProjectionMatrix, textureMatrix, spriteTextureFish1);
    	}
        if(timeCurr - timeStart3 > 5000 || timeStart3 == 0){
        	spriteAnimationFish.bindData(spriteProgram);
        	spriteAnimationFish.draw();
        }
// drawing procedure for flying fish
        
// drawing procedure for the cat
        if(triggerPulled){
        	if(angleCat >= 360){
    			angleCat = 0;
    		}
        	if(angleCat != angleFish){
        		angleCat += 0.3;    //define fish speed
        		xCat = (float) (radiusFish*Math.cos(Math.toRadians(angleCat - 18f)));
        		zCat = (float) (radiusFish*Math.sin(Math.toRadians(angleCat - 18f)));
        		targetPositionCat.assignXZ(xCat, zCat);
        		tickCounter --;
        		if(tickCounter < 0){
        			texture_uv_coordinates_horizontal[1] += 0.25f;
        			if (texture_uv_coordinates_horizontal[1] >= 1f){
        				texture_uv_coordinates_horizontal[1] = 0f;
        			}
        			texture_uv_coordinates_vertical[1] = 0.75f;
        			tickCounter = 10;  //define the frame rate of the sprite animation !!
        		}
        		timeStart2 = (int)(System.currentTimeMillis());
        	}else{
        		tickCounter --;
        		if(tickCounter < 0){
        			texture_uv_coordinates_horizontal[1] += 0.25f;
        			if (texture_uv_coordinates_horizontal[1] >= 1f){
        				texture_uv_coordinates_horizontal[1] = 0f;
        				texture_uv_coordinates_vertical[1] += 0.25f;
        				if (texture_uv_coordinates_vertical[1] >= 0.75f)
        					texture_uv_coordinates_vertical[1] = 0.25f;
        			}
        			tickCounter = 10;  //define the frame rate of the sprite animation !!
        		}
        		if(timeCurr - timeStart2 > 5000){
        			triggerPulled = false;
        		    targetPositionFish.assignY(3f);
        		    if(SoundMew.isPlaying()){
        		    	SoundMew.seekTo(0);
                	}
        		    SoundMew.start();
                	fatty++;
                	timeStart3 = (int)(System.currentTimeMillis());
                }
        		SoundEating.start();
        	}
        	
        	setIdentityM(modelMatrix, 0);
    		rotateM(modelMatrix, 0, mPitch, 1f, 0f, 0f);
    		rotateM(modelMatrix, 0, -mYaw, 0f, 1f, 0f);
    		translateM(modelMatrix, 0, targetPositionCat.x, targetPositionCat.y, targetPositionCat.z);
    		rotateM(modelMatrix, 0, mYaw, 0f, 1f, 0f);  
    		scaleM(modelMatrix, 0, 2.5f, 2.5f, 1f);
    	
    		setIdentityM(textureMatrix, 0);
    		translateM(textureMatrix, 0, texture_uv_coordinates_horizontal[1], 
    				texture_uv_coordinates_vertical[1], 0f); 
    	
    		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    	    	        
    		spriteProgram.useProgram();
    		spriteProgram.setUniforms(viewProjectionMatrix, textureMatrix, spriteTextureRun);
    	}else{
    		setIdentityM(modelMatrix, 0);
    		rotateM(modelMatrix, 0, mPitch, 1f, 0f, 0f);
    		rotateM(modelMatrix, 0, -mYaw, 0f, 1f, 0f);
    		translateM(modelMatrix, 0, targetPositionCat.x, targetPositionCat.y, targetPositionCat.z);
    		
    		rotateM(modelMatrix, 0, mYaw, 0f, 1f, 0f);  //make sure the picture always facing you
    		scaleM(modelMatrix, 0, 2.5f, 2.5f, 1f);
    		//make sure the running girl is always facing you
    		//rotateM(modelMatrix, 0, mYaw - running, 0f, 1f, 0f);  
    	
    		tickCounter --;
    		if(tickCounter < 0){
    			texture_uv_coordinates_horizontal[1] += 0.25f;
    			if (texture_uv_coordinates_horizontal[1] >= 1f){
    				texture_uv_coordinates_horizontal[1] = 0f;
    				texture_uv_coordinates_vertical[1] += 0.25f;
    				if (texture_uv_coordinates_vertical[1] >= 1f)
    					texture_uv_coordinates_vertical[1] = 0f;
    			}
    			tickCounter = 10;  //define the frame rate of the sprite animation !!
    		}
    		setIdentityM(textureMatrix, 0);
    		translateM(textureMatrix, 0, texture_uv_coordinates_horizontal[1], 
    				texture_uv_coordinates_vertical[1], 0f); 
    	
    		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    	    	        
    		spriteProgram.useProgram();
    	
    		if(fatty >= 15){
    			spriteProgram.setUniforms(viewProjectionMatrix, textureMatrix, spriteTextureObese);
    		}else if(fatty < 15 && fatty >= 10){
    			spriteProgram.setUniforms(viewProjectionMatrix, textureMatrix, spriteTexture5);
    		}else if(fatty < 10 && fatty >= 7){
    			spriteProgram.setUniforms(viewProjectionMatrix, textureMatrix, spriteTexture4);
    		}else if(fatty < 7 && fatty >= 4){
    			spriteProgram.setUniforms(viewProjectionMatrix, textureMatrix, spriteTexture3);
    		}else if(fatty < 4 && fatty >= 1){
    			spriteProgram.setUniforms(viewProjectionMatrix, textureMatrix, spriteTexture2);
    		}else if(fatty < 1 && fatty >= -3){
    			spriteProgram.setUniforms(viewProjectionMatrix, textureMatrix, spriteTexture1);
    		}else if(fatty < -3 && fatty >= -5){
    			spriteProgram.setUniforms(viewProjectionMatrix, textureMatrix, spriteTexture0);
    		}else if(fatty < -5){
    			spriteProgram.setUniforms(viewProjectionMatrix, textureMatrix, spriteTextureStarving);
    		}    		
    	}
        spriteAnimationCat.bindData(spriteProgram);
    	spriteAnimationCat.draw();
// drawing procedure for the cat    	
    }
    
    private void drawSniperScope(){
    	setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, .3f, .15f, .3f); 
        //rotateM(modelMatrix, 0, 0f, 1f, 0f, 0f);
        translateM(modelMatrix, 0, 0f, 0f, -5f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
        textureProgram.useProgram();
        textureProgram.setUniforms(viewProjectionMatrix, sniperSightTexture);
        sniperSight.bindData(textureProgram);
        sniperSight.draw();
    }
    
    private void drawSkybox(){
    	setIdentityM(modelMatrix, 0);
    	//rotateM(modelMatrix, 0, -yRotation, 1f, 0f, 0f);
    	//rotateM(modelMatrix, 0, -xRotation, 0f, 1f, 0f);
    	rotateM(modelMatrix, 0, mPitch, 1f, 0f, 0f);
    	rotateM(modelMatrix, 0, -mYaw, 0f, 1f, 0f);
    	//rotateM(modelMatrix, 0, -mRoll, 0f, 0f, 1f);
    	multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    	
    	//if I put the invertM here, the camera angles matters.
    //	invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
    	
    	skyboxProgram.useProgram();
    	skyboxProgram.setUniforms(viewProjectionMatrix, skyboxTexture);
    	skybox.bindData(skyboxProgram);
    	skybox.draw();
    }
	
	private void divideByW(float[] vector)
	{
		vector[0] /= vector[3];
		vector[1] /= vector[3];
		vector[2] /= vector[3];
	}
	
	private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY){
		// convert these normalized device coordinates into world-space
		// coordinates. pick a point on the near and far planes, and draw a 
		// line between them. To do this transform, first multiply by 
		// the inverse matrix, and then undo the perspective divide.
		final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
		final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
		
		final float[] nearPointWorld = new float[4];
		final float[] farPointWorld = new float[4];
		
		multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
		multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
		
		divideByW(nearPointWorld);
		divideByW(farPointWorld);
		
		Point nearPointRay = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
		Point farPointRay = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
		
		return new Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
	}
	
	//private float radius = 0.5f;
	private float radius = 1f;
	private boolean triggerPulled = false;
	public void handleTriggerPull(float normalizedX, float normalizedY){
		Ray ray = convertNormalized2DPointToRay(normalizedX - 0.25f, normalizedY - 0.2f);		
		//now test if this ray intersects with the target by creating a
		// bounding sphere that wraps the target.
		/*Sphere targetBoundingSphereGirl = new Sphere(new Point(
				targetPositionGirl.x + 1f,
				targetPositionGirl.y + 1f,
				targetPositionGirl.z),
				radius);*/
		
		Sphere targetBoundingSphereFish = new Sphere(new Point(
				xFish,
				targetPositionFish.y,
				zFish),
				radius);
		
		//if they intersects
		/*targetHitGirl = Geometry.intersects(targetBoundingSphereGirl, ray);*/
		targetHitFish = Geometry.intersects(targetBoundingSphereFish, ray);
		
		// testing
		if(targetHitFish){
        	triggerPulled = true;
        	if(SoundGun.isPlaying()){
        		SoundGun.seekTo(0);
        	}
        	SoundGun.start();
		}else{
			System.out.println("eli hit NO !");
		}
	}
	
	private float mYaw, mPitch;
	public void handleGyroscope(float yaw, float pitch){
		mYaw = yaw;
		mPitch = pitch;
	}
}