package edu.uci.eecs.wukong.prclass.icsdemo;

import edu.uci.eecs.wukong.framework.factor.BaseFactor;

public class ICSContext extends BaseFactor {
    public static final String TOPIC = "nooneknow";

    public String Context;
    public short UserA, UserB, UserG;
    public short Floorlamp_R, Floorlamp_G, Floorlamp_B, Floorlamp_Lux;
    public short Bloom_R, Bloom_G, Bloom_B, Bloom_Lux;
    public short Go_R, Go_G, Go_B, Go_Lux;
    public short Strip_R, Strip_G, Strip_B, Strip_Lux;
    public short Fan_Speed;
    public String Fan_Rotate;
    public String Mist;
    public String Music_Type;
    public short Music_Vol;
    public String TV;
    public String PHONE;

	public ICSContext(String topicId) {
		super(topicId);
		// TODO Auto-generated constructor stub
	}
	
	public ICSContext(short Floorlamp_R, short Floorlamp_G) {
		super("nooneknow");
		this.Floorlamp_R = Floorlamp_R;
		this.Floorlamp_G = Floorlamp_G;
	}
	
	public String getContext() {
		return "Floorlamp_RGBL=("+this.Floorlamp_R+","+Floorlamp_G+","+Floorlamp_B+","+Floorlamp_Lux+") "
				+"Bloom_RGBL=("+Bloom_R+","+Bloom_G+","+Bloom_B+","+Bloom_Lux+") "
				+"Go_RGBL=("+Go_R+","+Go_G+","+Go_B+","+Go_Lux+") "
				+"Strip_RGBL=("+Strip_R+","+Strip_G+","+Strip_B+","+Strip_Lux+")\n"

				+"Fan_Speed&Rotate=("+Fan_Speed+","+Fan_Rotate+") "
				+"Mist=("+Mist+") "
				+"Music_Type&Volume=("+Music_Type+","+Music_Vol+")\n"
				
				+"TV=("+TV+") "
				+"PHONE=("+PHONE+")"
				;
	}

	public String getElementName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}

}
