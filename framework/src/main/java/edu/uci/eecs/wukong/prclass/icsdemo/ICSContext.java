package edu.uci.eecs.wukong.prclass.icsdemo;


import edu.uci.eecs.wukong.framework.factor.BaseFactor;

public class ICSContext extends BaseFactor {
    public static final String TOPIC = "nooneknow";

    public short Command_Mode;
    public String Context;
    public short UserA, UserB, UserG;
    public short Floorlamp, Floorlamp_R, Floorlamp_G, Floorlamp_B, Floorlamp_Lux;
    public short Bloom, Bloom_R, Bloom_G, Bloom_B, Bloom_Lux;
    public short Go, Go_R, Go_G, Go_B, Go_Lux;
    public short Strip, Strip_R, Strip_G, Strip_B, Strip_Lux;
    public short Fan, Fan_Speed, Fan_Rotate;
    public short Mist;
    public short Music, Music_Type, Music_Vol;
    public short TV, TV_Mute;
    public short PHONE;
    
    private final int CONFLICT_VALUE = -1;

	public ICSContext(String topicId) {
		super(topicId);
		// TODO Auto-generated constructor stub
	}
	
	public ICSContext(short Command_Mode, String Context, 
			short UserA, short UserB, short UserG,
			short Floorlamp, short Floorlamp_R, short Floorlamp_G, short Floorlamp_B, short Floorlamp_Lux,
			short Bloom, short Bloom_R, short Bloom_G, short Bloom_B, short Bloom_Lux,
			short Go, short Go_R, short Go_G, short Go_B, short Go_Lux,
			short Strip, short Strip_R, short Strip_G, short Strip_B, short Strip_Lux,
			short Fan, short Fan_Speed, short Fan_Rotate,
			short Mist, short Music, short Music_Type, short Music_Vol,
			short TV, short TV_Mute, short PHONE)
	{
		super("nooneknow");
		this.Command_Mode = Command_Mode;
		this.Context = Context;
		this.UserA = UserA;
		this.UserB = UserB;
		this.UserG = UserG;
		this.Floorlamp = Floorlamp;
		this.Floorlamp_R = Floorlamp_R;
		this.Floorlamp_G = Floorlamp_G;
		this.Floorlamp_B = Floorlamp_B;
		this.Floorlamp_Lux = Floorlamp_Lux;
		this.Bloom = Bloom;
		this.Bloom_R = Bloom_R;
		this.Bloom_G = Bloom_G;
		this.Bloom_B = Bloom_B;
		this.Bloom_Lux = Bloom_Lux;
		this.Go = Go;
		this.Go_R = Go_R;
		this.Go_G = Go_G;
		this.Go_B = Go_B;
		this.Go_Lux = Go_Lux;
		this.Strip = Strip;
		this.Strip_R = Strip_R;
		this.Strip_G = Strip_G;
		this.Strip_B = Strip_B;
		this.Strip_Lux = Strip_Lux;
		this.Fan = Fan;
		this.Fan_Speed = Fan_Speed;
		this.Fan_Rotate = Fan_Rotate;
		this.Mist = Mist;
		this.Music = Music;
		this.Music_Type = Music_Type;
		this.Music_Vol = Music_Vol;
		this.TV = TV;
		this.TV_Mute = TV_Mute;
		this.PHONE = PHONE;
	}
	
	public String getContext() {
		return "ICSContext:%n"
				+"Floorlamp(On,R,G,B,Lux)=("+Floorlamp+","+Floorlamp_R+","+Floorlamp_G+","+Floorlamp_B+","+Floorlamp_Lux+")%n"
				+"Bloom(On,R,G,B,Lux)=("+Bloom+","+Bloom_R+","+Bloom_G+","+Bloom_B+","+Bloom_Lux+")%n"
				+"Go(On,R,G,B,Lux)=("+Go+","+Go_R+","+Go_G+","+Go_B+","+Go_Lux+")%n"
				+"Strip(On,R,G,B,Lux)=("+Strip+","+Strip_R+","+Strip_G+","+Strip_B+","+Strip_Lux+")%n"
				+"Fan(On,Speed,Rotate)=("+Fan+","+Fan_Speed+","+Fan_Rotate+")%n"
				+"Mist(On)=("+Mist+")%n"
				+"Music(On,Type,Volume)=("+Music+","+Music_Type+","+Music_Vol+")%n"
				
				+"TV(On,Mute)=("+TV+","+TV_Mute+")%n"
				+"PHONE(On)=("+PHONE+")"
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

	public boolean isConflict(){
		return (this.Floorlamp_R == CONFLICT_VALUE)
				|| (this.Floorlamp_G == CONFLICT_VALUE)
				|| (this.Floorlamp_B == CONFLICT_VALUE)
				|| (this.Floorlamp_Lux == CONFLICT_VALUE)
				|| (this.Bloom_R == CONFLICT_VALUE)
				|| (this.Bloom_G == CONFLICT_VALUE)
				|| (this.Bloom_B == CONFLICT_VALUE)
				|| (this.Bloom_Lux == CONFLICT_VALUE)
				|| (this.Go_R == CONFLICT_VALUE)
				|| (this.Go_G == CONFLICT_VALUE)
				|| (this.Go_B == CONFLICT_VALUE)
				|| (this.Go_Lux == CONFLICT_VALUE)
				|| (this.Strip_R == CONFLICT_VALUE)
				|| (this.Strip_G == CONFLICT_VALUE)
				|| (this.Strip_B == CONFLICT_VALUE)
				|| (this.Strip_Lux == CONFLICT_VALUE)
				|| (this.Fan == CONFLICT_VALUE)
				|| (this.Fan_Speed == CONFLICT_VALUE)
				|| (this.Fan_Rotate == CONFLICT_VALUE)
				|| (this.Mist == CONFLICT_VALUE)
				|| (this.Music == CONFLICT_VALUE)
				|| (this.Music_Type == CONFLICT_VALUE)
				|| (this.Music_Vol == CONFLICT_VALUE)
				|| (this.TV == CONFLICT_VALUE)
				|| (this.TV_Mute == CONFLICT_VALUE);
	}
}
