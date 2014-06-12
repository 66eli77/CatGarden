/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.catgarden.android.util;

import android.util.FloatMath;

public class Geometry {        
    public static class Point {
        public float x, y, z;
        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }        
        
        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }
        
        public void changeY(float distance) {
            y = y + distance;
        }
        
        public void changeX(float distance) {
            x = x + distance;
        }
        
        public void changeZ(float distance) {
            z = z + distance;
        }
           
        public void assignXZ(float xx, float zz){
        	x = xx;
        	z = zz;
        }
        
        public void assignY(float yy){
        	y = yy;
        }
        
        public Point translate(Vector vector){
        	return new Point(x + vector.x, y + vector.y, z + vector.z);
        }
    }
    
    public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }                      
        
        public Circle scale(float scale) {
            return new Circle(center, radius * scale);
        }
    }
    
    public static class Cylinder {
        public final Point center;
        public final float radius;
        public final float height;
        
        public Cylinder(Point center, float radius, float height) {        
            this.center = center;
            this.radius = radius;
            this.height = height;
        }                                    
    }
    
    public static class Ray {
    	public final Point point;
    	public final Vector vector;
    	
    	public Ray(Point point, Vector vector){
    		this.point = point;
    		this.vector = vector;
    	}
    }
    
    public static class Vector {
    	public final float x, y, z;
    	
    	public Vector(float x, float y, float z){
    		this.x = x;
    		this.y = y;
    		this.z = z;
    	}
    	
        public float length(){
        	return FloatMath.sqrt(x*x + y*y + z*z);
        }
        
        public Vector crossProduct(Vector other){
        	return new Vector(
        			(y * other.z) - (z * other.y),
        			(z * other.x) - (x * other.z),
        			(x * other.y) - (y * other.x));
        }
        
        public Vector scale(float f){
        	return new Vector(x*f, y*f, z*f);
        }
    }
    
    public static Vector vectorBetween(Point from, Point to){
    	return new Vector(to.x - from.x, to.y - from.y, to.z - from.z);
    }
    
    public static class Sphere {
    	public final Point center;
    	public final float radius;
    	
    	public Sphere(Point center, float radius){
    		this.center = center;
    		this.radius = radius;
    	}
    }
    
    public static float distanceBetween(Point point, Ray ray){
    	Vector p1ToPoint = vectorBetween(ray.point, point);
    	Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);
    	
    	float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
    	float lengthOfBase = ray.vector.length();
    	
    	float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
    	return distanceFromPointToRay;
    }
    
    public static boolean intersects(Sphere sphere, Ray ray){
    	return distanceBetween(sphere.center, ray) < sphere.radius;
    }
}
