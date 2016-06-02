package edu.uci.eecs.wukong.framework.localization;

public class NormalDistribution {

    public static double getProbability(double mean, double sigma, double x){
        double low=0.5*(1+erf((x-0.5-mean)/(sigma*Math.sqrt(2))));
        double up=0.5*(1+erf((x+0.5-mean)/(sigma*Math.sqrt(2))));
        return up-low;
    }

    private static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));
        double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
                                            t * ( 1.00002368 +
                                            t * ( 0.37409196 +
                                            t * ( 0.09678418 +
                                            t * (-0.18628806 +
                                            t * ( 0.27886807 +
                                            t * (-1.13520398 +
                                            t * ( 1.48851587 +
                                            t * (-0.82215223 +
                                            t * ( 0.17087277))))))))));
        if (z >= 0) return  ans;
        else        return -ans;
    }
}
