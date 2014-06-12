package com.catgarden.android;

import com.catgarden.android.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Vis141Activity extends Activity implements SensorEventListener{
    private GLSurfaceView glSurfaceView;
    private SensorManager sManager;
    private Sensor mGyroscope;
    
  //  private TextView tv;
    MediaPlayer SoundLooping;
    Vibrator vibra;
        
    final Vis141Renderer mRenderer = new Vis141Renderer(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        SoundLooping = MediaPlayer.create(this, R.raw.seagulls);
        SoundLooping.setLooping(true);
        SoundLooping.start();        
        
        vibra = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        
        //get a hook to the sensor service
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mGyroscope = sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        glSurfaceView = new GLSurfaceView(this);
       
        glSurfaceView.setEGLContextClientVersion(2);

        // Assign our renderer.
        glSurfaceView.setRenderer(mRenderer);

        //setContentView(glSurfaceView);
        setContentView(R.layout.textviewer);
     //   tv = (TextView) findViewById(R.id.tv);
        FrameLayout mView = (FrameLayout)findViewById(R.id.preview);
        mView.addView(glSurfaceView);
        
        glSurfaceView.setOnTouchListener(new OnTouchListener() {
        	float previousX, previousY;
        	
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event != null) {    
                	final float normalizedX = 
                            (event.getX() / (float) v.getWidth()) * 2 - 1;
                	final float normalizedY = 
                            -((event.getY() / (float) v.getHeight()) * 2 - 1);
                	
                	normalizedTouchX = normalizedX;
                	normalizedTouchY = normalizedY;
                        
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    	previousX = event.getX();
                    	previousY = event.getY();
                    	
                    	/*if(gunSound.isPlaying()){
                    		gunSound.seekTo(0);
                    	}
                    	gunSound.start();*/
                    	vibra.vibrate(60);
                    	
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                            //in the actual game, the normalizedX, and Y will always be 0, 0
                            //because the scope sight is always stay in the center of the screen
                            	//mRenderer.handleTriggerPull(0f, 0f);
                            //the code below will aim where the finger pressed
                            	mRenderer.handleTriggerPull(normalizedX, normalizedY);
                            }
                        });
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {	
                    } 
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        glSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                         //   	mRenderer.handleTouchUp(
                             //       normalizedX, normalizedY);
                            }
                        });
                    }

                    return true;                    
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    protected void onStop() {
    	super.onStop();
    	SoundLooping.setLooping(false);
    	SoundLooping.stop();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();   
        
        sManager.unregisterListener(this);
        SoundLooping.setLooping(false);
        SoundLooping.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
        SoundLooping.setLooping(true);
        
   //     sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
     //   		SensorManager.SENSOR_DELAY_FASTEST);
        sManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private float round(float f) {
        return (int)(f * 100f) / 100f;
	}
    /* for orientation sensor
    private float x, y, z, YawX, YawZ, PitchY, PitchZ, Gx, Gy, Gz;
    private float radius = 1f;
    */
    private float yaw, pitch;
    //private float roll;
    float normalizedTouchX = 0;
    float normalizedTouchY = 0;
    
	@Override
	public void onSensorChanged(SensorEvent event) {
		if(event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE){
			return;
		}
		
		// gyroscope data
		if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
			yaw += event.values[0];
			pitch += event.values[1];
		//	roll += event.values[2];
		}
		/* for orientation sensor
		//else it will output the Roll, Pitch and Yawn values   
		if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			
			yaw = event.values[0];
			pitch = event.values[1];
			if(event.values[2] > 0)
			roll = event.values[2];
			
			x = event.values[2];
			if(event.values[1] < 0){
				y = - event.values[1];
			} else{
				y = 180 + event.values[1];
			}
			z = event.values[0];
		}
		
		YawX = (float) (radius * Math.cos(z * (Math.PI/180)));
		YawZ = (float) (radius * Math.sin(z * (Math.PI/180)));
		PitchY = (float) (radius * Math.cos(y * (Math.PI/180)));
		PitchZ = (float) (radius * Math.sin(y*(Math.PI/180)));
		
		tv.setText(//"yaw : " + round(yaw) + "\n" +
					"normalizedTouchX : " + round(normalizedTouchX) + "\n" +
					"normalizedTouchY : " + round(normalizedTouchY) + "\n" +
					"GyroscopeX yaw : " + round(yaw) + "\n" +
					"GyroscopeY pitch: " + round(pitch) + "\n" 
				//	"GyroscopeZ roll: " + round(roll) 
					);
		*/
		glSurfaceView.queueEvent(new Runnable(){
			@Override
			public void run(){
				//mRenderer.handleOrientation(YawX, YawZ, PitchY, PitchZ, roll);
				mRenderer.handleGyroscope(yaw*0.7f, pitch*0.7f);
			}
		});
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {		
	}
}