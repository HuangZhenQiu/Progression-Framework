package edu.uci.eecs.wukong.framework.localization;

public class Particle {
    private double x,y; // x and y position
    private double a; // orientation (angle in radians: 0<=a<2*PI)
    private double w; // weight (0<=weight<=1)

    public Particle(double x, double y, double a,double w) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.a = a;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        while(a<0)a+=2*Math.PI;
        while(a>2*Math.PI)a-=2*Math.PI;
        this.a = a;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }
}
