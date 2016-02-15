package edu.uci.eecs.wukong.framework.localization;

import java.util.Random;

public class ParticleFilter {
    
	// the map
	private Map map;

	// the set of particles
    private Particle[] particles;
	
	// random number generator
	private Random rnd;
	
	// two possible actions
    public static final int ACTION_MOVE=0;
    public static final int ACTION_ROTATE=1;

	// angles of the sensors relative to the orientation of the robot
    private double[] sensors;
	
	// noise for movement, rotation and sensors (standard deviation)
    private double movnoise;
	private double rotnoise;
	private double sensnoise;
	// maximal range of the sensors
	private int maxr;

    public ParticleFilter(Map map, int particlecount, double[] sensors, int movnoise, int rotnoise, int sensnoise, int maxr){
        this.map=map;
        this.sensors=sensors;
        this.movnoise=movnoise/10.0;
        this.rotnoise=rotnoise/90.0;
        this.sensnoise=sensnoise;
        this.maxr=maxr;
        rnd=new Random();
        this.particles=new Particle[particlecount];
		// TODO: initialize particles (uniform distribution over the map)
        double unitHeight =  map.getHeight() / Math.sqrt(particlecount);
        double unitWidth =  map.getWidth() / Math.sqrt(particlecount);
        
        int i = 0;
        double weight = 1 / particlecount;
        for (double y = unitHeight / 2; y < map.getHeight(); y += unitHeight) {
        	for (double x = unitWidth / 2; x < map.getWidth(); x += unitWidth) {
        		if (i < particlecount) {
        			this.particles[i] = new Particle(x, y, 0, weight);
        		}
        		i++;
        	}
        }
    }
	
	// this is the main particle filter function that is called after each step
	public void step(double[] sensorvalues,int action, double value, double noise){
		applyAction(action, value);
		applyObservation(sensorvalues, noise);
		this.particles = resample(this.particles, 1.0);
    }

	// apply the transition model to all particles
    private void applyAction(int action, double value){
		for (int i=0;i<particles.length;i++){
			sampleFromTransitionModel(particles[i], action, value);
		}
    }
	
	// change the particle p according to the transition model
	private void sampleFromTransitionModel(Particle p, int action, double value) {
        if (action==ACTION_MOVE){
			// value is the distance of the movement
        	double newX = p.getX() + value * Math.cos(p.getA());
        	double newY = p.getY() + value * Math.sin(p.getA()); 
        	p.setX(newX);
        	p.setY(newY);
        } else if (action==ACTION_ROTATE){
        	p.setA(value);
        }
		
	}

	// apply the sensor model to all particles
    private void applyObservation(double[] sensorvalues, double noise){
		double total = 0;
		for (int i = 0; i < this.particles.length; i++) {
			double probability = getObservationProbability(sensorvalues, this.particles[i]);
			this.particles[i].setW(probability);
			total += probability;
		}
		// TODO: normalize weights to 1
		if (total != 0.0) {
			for (int i = 0; i < this.particles.length; i++) {
				this.particles[i].setW(this.particles[i].getW() / total);
			}
		}
    }

	// returns P(e|x)
    private double getObservationProbability(double[] sensorvalues, Particle p){
        return map.getObservationProbability(p.getX(),p.getY(),p.getA(),sensors,sensorvalues,sensnoise,maxr);
    }

	// resample the list of particles
    private Particle[] resample(Particle[] s, double confusion){
        int n=s.length;
        Particle[] out=new Particle[n];
        double[] c=new double[n];
        c[0]=s[0].getW();
        for (int i=1;i<n;i++){
            c[i]=c[i-1]+s[i].getW();
        }
        double[] u=new double[n];
        u[0]=rnd.nextDouble()/n;
        int i=0;
        int outindex=0;
        for (int j=0;j<n;j++){
            while (u[j]>c[i]){
                i=i+1;
            }

            // if (rnd.nextDouble()>confusion)

            if (rnd.nextDouble()>confusion) {
				// without noise
				// out[outindex++]=new Particle(s[i].getX(),s[i].getY(),s[i].getA(),1.0/n);

				// adding some noise 
				out[outindex++]=new Particle(s[i].getX()+0.5*movnoise*rnd.nextGaussian(),s[i].getY()+0.5*movnoise*rnd.nextGaussian(),s[i].getA()+0.005*rotnoise*rnd.nextGaussian(),1.0/n);
            }else{ // add some random poses
                int px=rnd.nextInt(map.getWidth());
                int py=rnd.nextInt(map.getHeight());
                while (!map.getData(px,py)){
                    px=rnd.nextInt(map.getWidth());
                    py=rnd.nextInt(map.getHeight());
                }
                out[outindex++]=new Particle(px,py,rnd.nextDouble()*2*Math.PI,1.0/n);
            }
            if (j+1<n)u[j+1]=u[j]+(1.0/n);
        }
        return out;
    }


	// ignore every thing below this point

    public void applyCompassData(double angle){
        for (int i=0;i<particles.length;i++){
            particles[i].setA(angle);
        }
    }

    public Particle[] getParticles(){
        return particles;
    }
}
