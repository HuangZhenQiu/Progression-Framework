package edu.uci.eecs.wukong.prclass.icsdemo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import java.awt.geom.Point2D;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.prclass.icsdemo.ContextProgressionExtension;


public class ICSDemoHuePrClass extends PrClass {
	
	@WuProperty(name = "hue", id = 0, type = PropertyType.Output, dtype = DataType.Short)
	private short hue;
	@WuProperty(name = "saturation", id = 1, type = PropertyType.Output, dtype = DataType.Short)
	private short saturation;
	@WuProperty(name = "brightness", id = 2, type = PropertyType.Output, dtype = DataType.Short)
	private short brightness;
	@WuProperty(name = "x", id = 3, type = PropertyType.Output, dtype = DataType.Short)
	private short x;
	@WuProperty(name = "y", id = 4, type = PropertyType.Output, dtype = DataType.Short)
	private short y;
	
//    http://www.developers.meethue.com/documentation/supported-lights
// 	  1st dimension: 0: gamutA, 1: gamutB, 2: gamutC
//    2nd & 3rd dimension: 00:Red x, 01: Red y, 10: Green x, 11: Green y, 20: Blue x, 21: Blue y
	private final int GAMUT_RED = 0, GAMUT_GREEN = 1, GAMUT_BLUE=2;
	private int gamutSelection = 1;
	private final Point2D.Double[][] gamut = {
			{new Point2D.Double(0.704, 0.296), new Point2D.Double(0.2151, 0.7106), new Point2D.Double(0.138, 0.08)},
			{new Point2D.Double(0.675, 0.322), new Point2D.Double(0.409,  0.518), new Point2D.Double(0.167, 0.04)},
			{new Point2D.Double(0.675, 0.322), new Point2D.Double(0.2151, 0.7106), new Point2D.Double(0.167, 0.04)}
		};

	
	public ICSDemoHuePrClass() {
		super("ICSDemoHuePrClass");
		// TODO Auto-generated constructor stub
	}

	public ICSDemoHuePrClass(String name, String modelID) {
		super(name);
		switch(modelID){
			case "LCT001":
			case "LCT002":
			case "LCT003":
			case "LCT007":
				this.gamutSelection = 0;
				break;
			case "LLC006":
			case "LLC007":
			case "LLC010":
			case "LLC011":
			case "LLC012":
			case "LLC013":
			case "LLM001":
				this.gamutSelection = 1;
				break;
			case "LLC020":
			case "LST002":
				this.gamutSelection = 2;
				break;
			default:
				break;
		}
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new ContextProgressionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		List<String> topics = new ArrayList<String> ();
		topics.add(ICSContext.TOPIC);
		return topics;
	}
	
	//https://github.com/benknight/hue-python-rgb-converter/blob/master/rgb_cie.py
	private void setXYFromRGB(double red, double green, double blue)
	{
		double r = this.gammaCorrection(red), g = this.gammaCorrection(green), b = this.gammaCorrection(blue);
		double X, Y, Z, cx, cy;
		
//		  http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
//		  sRGB D50
//        X = r * 0.4360747 + g * 0.3850649 + b * 0.0930804;
//        Y = r * 0.2225045 + g * 0.7168786 + b * 0.0406169;
//        Z = r * 0.0139322 + g * 0.0971045 + b * 0.7141733;

//        http://www.brucelindbloom.com/index.html?Eqn_RGB_XYZ_Matrix.html
//        https://en.wikipedia.org/wiki/SRGB
//        sRGB D65
//        X = r * 0.4124564 + g * 0.3575761 + b * 0.1804375;
//        Y = r * 0.2126729 + g * 0.7151522 + b * 0.0721750;
//        Z = r * 0.0193339 + g * 0.1191920 + b * 0.9503041;
		
//        http://www.developers.meethue.com/documentation/hue-xy-values
//        http://www.developers.meethue.com/documentation/color-conversions-rgb-xy
		X = r * 0.664511 + g * 0.154324 + b * 0.162028;
        Y = r * 0.283881 + g * 0.668433 + b * 0.047685;
        Z = r * 0.000088 + g * 0.072310 + b * 0.986039;
        
        if ((X + Y + Z) == 0.0){
        	cx = cy = 0.0;
        } else {
        	cx = X / (X + Y + Z);
            cy = Y / (X + Y + Z);
        }
        if (!this.checkPointInLampsReach(cx, cy)) {
            Point2D.Double xy = this.getClosestPointToPoint(new Point2D.Double(cx, cy));
            cx = xy.x;
            cy = xy.y;
        }
    	this.x = (short)(cx * 10000);
    	this.y = (short)(cy * 10000);
	}
	
	private double gammaCorrection(double rgb)
	{
		// http://www.babelcolor.com/download/A%20review%20of%20RGB%20color%20spaces.pdf
        // http://www.developers.meethue.com/documentation/color-conversions-rgb-xy
		if (rgb > 0.04045){
			return Math.pow((rgb + 0.055) / (1.0 + 0.055), 2.4);
		} else {
			return rgb / 12.92;
	    }
	}
	
	//https://github.com/benknight/hue-python-rgb-converter/blob/master/rgb_cie.py
	private boolean checkPointInLampsReach(double x, double y)
	{
		Point2D.Double v1 = new Point2D.Double(gamut[gamutSelection][GAMUT_GREEN].x - gamut[gamutSelection][GAMUT_RED].x, gamut[gamutSelection][GAMUT_GREEN].y - gamut[gamutSelection][GAMUT_RED].y), 
			v2 = new Point2D.Double(gamut[gamutSelection][GAMUT_BLUE].x - gamut[gamutSelection][GAMUT_RED].x, gamut[gamutSelection][GAMUT_BLUE].y - gamut[gamutSelection][GAMUT_RED].y),
			q =  new Point2D.Double(x - gamut[gamutSelection][GAMUT_RED].x, y - gamut[gamutSelection][GAMUT_RED].y);
		double v1Xv2 = this.crossProduct(v1, v2);
		double s = this.crossProduct(q, v2) / v1Xv2, t = this.crossProduct(v1, q) / v1Xv2;
		if ((s >= 0.0) && (t >= 0.0) && (s+t <= 1.0)){
			return true;
		}
		return false;
	}
	
	//https://github.com/benknight/hue-python-rgb-converter/blob/master/rgb_cie.py
	private double crossProduct(Point2D.Double p1, Point2D.Double p2){
		return (p1.x * p2.y - p1.y * p2.x);
	}
	
	//https://github.com/benknight/hue-python-rgb-converter/blob/master/rgb_cie.py
	private Point2D.Double getClosestPointToPoint(Point2D.Double xy){
		Point2D.Double pAB = this.getClosestPointToLine(gamut[gamutSelection][GAMUT_RED], gamut[gamutSelection][GAMUT_GREEN], xy),
				pAC = this.getClosestPointToLine(gamut[gamutSelection][GAMUT_BLUE], gamut[gamutSelection][GAMUT_RED], xy),
				pBC = this.getClosestPointToLine(gamut[gamutSelection][GAMUT_GREEN], gamut[gamutSelection][GAMUT_BLUE], xy);
		double dAB = xy.distance(pAB), dAC = xy.distance(pAC), dBC = xy.distance(pBC);
		Point2D.Double closetPoint = pAB;
		double lowest = dAB;
		if (dAC < lowest){
            lowest = dAC;
            closetPoint = pAC;
		}

        if (dBC < lowest){
            lowest = dBC;
            closetPoint = pBC;
        }
        return closetPoint;
	}
	
	//https://github.com/benknight/hue-python-rgb-converter/blob/master/rgb_cie.py
	private Point2D.Double getClosestPointToLine(Point2D.Double A, Point2D.Double B, Point2D.Double P){
		Point2D.Double AP = new Point2D.Double(P.x - A.x, P.y - A.y), AB = new Point2D.Double(B.x - A.x, B.y - A.y);
		double ab2 = AB.x * AB.x + AB.y * AB.y, ap_ab = AP.x * AB.x + AP.y * AB.y;
		double t = ap_ab / ab2;
		if (t < 0.0){
			t = 0.0;
		} else if (t > 1.0) {
			t = 1.0;
		}
		return new Point2D.Double(A.x + AB.x * t, A.y + AB.y * t);
	}
	
	private void setHSBFromRGB(double red, double green, double blue)
	{
		//https://www.cs.rit.edu/~ncs/color/t_convert.html
		//https://en.wikipedia.org/wiki/HSL_and_HSV#Converting_to_RGB
		double[] da = new double[] {red, green, blue};
        List b = Arrays.asList(ArrayUtils.toObject(da));
        double min = Collections.min(b), max = Collections.max(b);
        double delta = max - min;
        this.brightness = ((short)(max*255));
        if (max != 0.0){
        	this.saturation = ((short)((delta / max)*255));
        } else {
        	this.saturation = 0;
        	this.hue = -1;
        	return;
        }
        if (red == max){
        	this.hue = (short)((green - blue) / delta);
        } else if (green == max) {
        	this.hue = (short)(2 + (blue - red) / delta);
        } else {
        	this.hue = (short)(4 + (red - green) / delta);
        }
        this.hue *= 60;
        if (this.hue < 0){
        	this.hue += 360;
        }
	}
	
	public void setColorFromRGB(short red, short green, short blue)
	{
		double r = red/255.0, g = green/255.0, b = blue/255.0;
		this.setXYFromRGB(r, g, b);
		this.setHSBFromRGB(r, g, b);
	}
	
	public short getX(){
		return x;
	}
	
	public void setX(short x){
		this.x = x;
	}
	
	public short getY(){
		return y;
	}
	
	public void setY(short y){
		this.y = y;
	}

	public short getHue() {
		return hue;
	}

	public void setHue(short hue) {
		this.hue = hue;
	}

	public short getSaturation() {
		return saturation;
	}

	public void setSaturation(short saturation) {
		this.saturation = saturation;
	}

	public short getBrightness() {
		return brightness;
	}

	public void setBrightness(short brightness) {
		this.brightness = brightness;
	}
}
