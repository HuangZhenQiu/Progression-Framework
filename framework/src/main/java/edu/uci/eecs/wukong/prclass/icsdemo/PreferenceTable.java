package edu.uci.eecs.wukong.prclass.icsdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreferenceTable {

	private short[][][] Table_Context_User_Devices;
	private final short number_of_user = 3;
	private int user_index, context_state;
	private static Logger logger = LoggerFactory.getLogger(PreferenceTable.class);
	
	public PreferenceTable(){
		int x = ContextStateEnumType.getLength();
		int y = (int)Math.pow(2, number_of_user);
		int z = Column.getLength();
		Table_Context_User_Devices = new short[x][y][z];
		int i, j, k;
		for (i = 0; i < x; i++){
			for (j = 0; j < y; j++){
				for (k = 0; k < z; k++){
					Table_Context_User_Devices[i][j][k] = -2; // -2: NONE, -1: ?, 0<=: value
				}
			}
		}
		this.setValues(x, y, z);
		context_state = (short) ContextStateEnumType.INIT.getID();
		user_index = (short) getUserIndexFromExistence(0, 0, 0);
	}
	
	public ICSContext lookup(ICSContext icsContext) {
		int i, j;
		switch (icsContext.Command_Mode){
			case 1:
				// user change
				logger.info("PrefTable lookup for User change");
				i = context_state;
				j = user_index = getUserIndexFromExistence(icsContext.UserA, icsContext.UserB, icsContext.UserG);
				icsContext.Floorlamp = Table_Context_User_Devices[i][j][Column.FLOORLAMP_ONOFF.ordinal()];
				icsContext.Floorlamp_R = Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()];
				icsContext.Floorlamp_G = Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()];
				icsContext.Floorlamp_B = Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()];
				icsContext.Floorlamp_Lux = Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()];
				icsContext.Bloom = Table_Context_User_Devices[i][j][Column.BLOOM_ONOFF.ordinal()];
				icsContext.Bloom_R = Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()];
				icsContext.Bloom_G = Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()];
				icsContext.Bloom_B = Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()];
				icsContext.Bloom_Lux = Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()];
				icsContext.Go = Table_Context_User_Devices[i][j][Column.GO_ONOFF.ordinal()];
				icsContext.Go_R = Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()];
				icsContext.Go_G = Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()];
				icsContext.Go_B = Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()];
				icsContext.Go_Lux = Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()];
				icsContext.Strip = Table_Context_User_Devices[i][j][Column.STRIP_ONOFF.ordinal()];
				icsContext.Strip_R = Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()];
				icsContext.Strip_G = Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()];
				icsContext.Strip_B = Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()];
				icsContext.Strip_Lux = Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()];
				icsContext.Fan = Table_Context_User_Devices[i][j][Column.FAN_ONOFF.ordinal()];
				icsContext.Fan_Speed = Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()];
				icsContext.Fan_Rotate = Table_Context_User_Devices[i][j][Column.FAN_ROTATE.ordinal()];
				icsContext.Mist = Table_Context_User_Devices[i][j][Column.MIST_ONOFF.ordinal()];
				icsContext.Music = Table_Context_User_Devices[i][j][Column.MUSIC_ONOFF.ordinal()];
				icsContext.Music_Type = Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()];
				icsContext.Music_Vol = Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()];
				icsContext.TV =  Table_Context_User_Devices[i][j][Column.TV_ONOFF.ordinal()];
				icsContext.TV_Mute = Table_Context_User_Devices[i][j][Column.TV_MUTE.ordinal()];
				break;
			case 2:
				// context state change
				logger.info("PrefTable lookup for Context state change");
				i = context_state = ContextStateEnumType.fromString(icsContext.Context).getID();
				j = user_index;
				icsContext.Floorlamp = Table_Context_User_Devices[i][j][Column.FLOORLAMP_ONOFF.ordinal()];
				icsContext.Floorlamp_R = Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()];
				icsContext.Floorlamp_G = Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()];
				icsContext.Floorlamp_B = Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()];
				icsContext.Floorlamp_Lux = Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()];
				icsContext.Bloom = Table_Context_User_Devices[i][j][Column.BLOOM_ONOFF.ordinal()];
				icsContext.Bloom_R = Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()];
				icsContext.Bloom_G = Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()];
				icsContext.Bloom_B = Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()];
				icsContext.Bloom_Lux = Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()];
				icsContext.Go = Table_Context_User_Devices[i][j][Column.GO_ONOFF.ordinal()];
				icsContext.Go_R = Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()];
				icsContext.Go_G = Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()];
				icsContext.Go_B = Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()];
				icsContext.Go_Lux = Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()];
				icsContext.Strip = Table_Context_User_Devices[i][j][Column.STRIP_ONOFF.ordinal()];
				icsContext.Strip_R = Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()];
				icsContext.Strip_G = Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()];
				icsContext.Strip_B = Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()];
				icsContext.Strip_Lux = Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()];
				icsContext.Fan = Table_Context_User_Devices[i][j][Column.FAN_ONOFF.ordinal()];
				icsContext.Fan_Speed = Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()];
				icsContext.Fan_Rotate = Table_Context_User_Devices[i][j][Column.FAN_ROTATE.ordinal()];
				icsContext.Mist = Table_Context_User_Devices[i][j][Column.MIST_ONOFF.ordinal()];
				icsContext.Music = Table_Context_User_Devices[i][j][Column.MUSIC_ONOFF.ordinal()];
				icsContext.Music_Type = Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()];
				icsContext.Music_Vol = Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()];
				icsContext.TV =  Table_Context_User_Devices[i][j][Column.TV_ONOFF.ordinal()];
				icsContext.TV_Mute = Table_Context_User_Devices[i][j][Column.TV_MUTE.ordinal()];
				break;
			case 3:
				// conflict resolution, preview
				logger.info("PrefTable lookup for Preview");
				i = context_state;
				j = user_index;
				icsContext.Floorlamp = (Table_Context_User_Devices[i][j][Column.FLOORLAMP_ONOFF.ordinal()] == -1 ? icsContext.Floorlamp : Table_Context_User_Devices[i][j][Column.FLOORLAMP_ONOFF.ordinal()]);
				icsContext.Floorlamp_R = (Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] == -1 ? icsContext.Floorlamp_R : Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()]);
				icsContext.Floorlamp_G = (Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] == -1 ? icsContext.Floorlamp_G : Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()]);
				icsContext.Floorlamp_B = (Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] == -1 ? icsContext.Floorlamp_B : Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()]);
				icsContext.Floorlamp_Lux = (Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] == -1 ? icsContext.Floorlamp_Lux : Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()]);
				icsContext.Bloom = (Table_Context_User_Devices[i][j][Column.BLOOM_ONOFF.ordinal()] == -1 ? icsContext.Bloom : Table_Context_User_Devices[i][j][Column.BLOOM_ONOFF.ordinal()]);
				icsContext.Bloom_R = (Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] == -1 ? icsContext.Bloom_R : Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()]);
				icsContext.Bloom_G = (Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] == -1 ? icsContext.Bloom_G : Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()]);
				icsContext.Bloom_B = (Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] == -1 ? icsContext.Bloom_B : Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()]);
				icsContext.Bloom_Lux = (Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] == -1 ? icsContext.Bloom_Lux : Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()]);
				icsContext.Go = (Table_Context_User_Devices[i][j][Column.GO_ONOFF.ordinal()] == -1 ? icsContext.Go : Table_Context_User_Devices[i][j][Column.GO_ONOFF.ordinal()]);
				icsContext.Go_R = (Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] == -1 ? icsContext.Go_R : Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()]);
				icsContext.Go_G = (Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] == -1 ? icsContext.Go_G : Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()]);
				icsContext.Go_B = (Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] == -1 ? icsContext.Go_B : Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()]);
				icsContext.Go_Lux = (Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] == -1 ? icsContext.Go_Lux : Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()]);
				icsContext.Strip = (Table_Context_User_Devices[i][j][Column.STRIP_ONOFF.ordinal()] == -1 ? icsContext.Strip : Table_Context_User_Devices[i][j][Column.STRIP_ONOFF.ordinal()]);
				icsContext.Strip_R = (Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] == -1 ? icsContext.Strip_R : Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()]);
				icsContext.Strip_G = (Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] == -1 ? icsContext.Strip_G : Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()]);
				icsContext.Strip_B = (Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] == -1 ? icsContext.Strip_B : Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()]);
				icsContext.Strip_Lux = (Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] == -1 ? icsContext.Strip_Lux : Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()]);
				icsContext.Fan = (Table_Context_User_Devices[i][j][Column.FAN_ONOFF.ordinal()] == -1 ? icsContext.Fan : Table_Context_User_Devices[i][j][Column.FAN_ONOFF.ordinal()]);
				icsContext.Fan_Speed = (Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] == -1 ? icsContext.Fan_Speed : Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()]);
				icsContext.Fan_Rotate = (Table_Context_User_Devices[i][j][Column.FAN_ROTATE.ordinal()] == -1 ? icsContext.Fan_Rotate : Table_Context_User_Devices[i][j][Column.FAN_ROTATE.ordinal()]);
				icsContext.Mist = (Table_Context_User_Devices[i][j][Column.MIST_ONOFF.ordinal()] == -1 ? icsContext.Mist : Table_Context_User_Devices[i][j][Column.MIST_ONOFF.ordinal()]);
				icsContext.Music = (Table_Context_User_Devices[i][j][Column.MUSIC_ONOFF.ordinal()] == -1 ? icsContext.Music : Table_Context_User_Devices[i][j][Column.MUSIC_ONOFF.ordinal()]);
				icsContext.Music_Type = (Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()] == -1 ? icsContext.Music_Type : Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()]);
				icsContext.Music_Vol = (Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()] == -1 ? icsContext.Music_Vol : Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()]);
				icsContext.TV =  Table_Context_User_Devices[i][j][Column.TV_ONOFF.ordinal()];
				icsContext.TV_Mute = (Table_Context_User_Devices[i][j][Column.TV_MUTE.ordinal()] == -1 ? icsContext.TV_Mute : Table_Context_User_Devices[i][j][Column.TV_MUTE.ordinal()]);
				break;
			default:
				logger.error("PrefTable lookup with unknown command");
				break;
		}
		return icsContext;
	}
	
	private int getUserIndexFromExistence(int UserA, int UserB, int UserG){
		return (UserG << 2) + (UserB << 1) + UserA;
	}
	
	private void setValues(int x, int y, int z){
		int i, j;
		// INIT
		i = ContextStateEnumType.INIT.getID();
		for (j = 0; j < y; j++){
			Table_Context_User_Devices[i][j][Column.MUSIC_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.MIST_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.FAN_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.FLOORLAMP_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.BLOOM_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.GO_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.STRIP_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.TV_ONOFF.ordinal()] = 0;
		}
		
		// RELAX
		i = ContextStateEnumType.RELAX.getID();
		for (j = 0; j < y; j++){
			Table_Context_User_Devices[i][j][Column.MUSIC_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.MIST_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.FAN_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.FAN_ROTATE.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.FLOORLAMP_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.BLOOM_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.GO_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.STRIP_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.TV_ONOFF.ordinal()] = 0;
		}
		// RELAX USERA
		j = getUserIndexFromExistence(1,0,0);
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 150;
		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 225;
		Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()] = (short) MusicGenreEnumType.getIDfromString("Classical");
		Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()] = 200;
		// RELAX USERB
		j = getUserIndexFromExistence(0,1,0);
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 175;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 180;
		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 127;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 50;
		Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()] = (short) MusicGenreEnumType.getIDfromString("Jazz");
		Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()] = 175;
		// RELAX USERG
		j = getUserIndexFromExistence(0,0,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 125;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 125;
		Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()] = (short) MusicGenreEnumType.getIDfromString("Pop");
		Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()] = 125;
		// RELAX USERA+B
		j = getUserIndexFromExistence(1,1,0);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 180;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = -1;
		Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()] = (short) MusicGenreEnumType.getIDfromString("?");
		Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()] = 175;
		// RELAX USERA+G
		j = getUserIndexFromExistence(1,0,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 150;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 225;
		Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()] = (short) MusicGenreEnumType.getIDfromString("Classical");
		Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()] = 200;
		// RELAX USERB+G
		j = getUserIndexFromExistence(0,1,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 180;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 50;
		Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()] = (short) MusicGenreEnumType.getIDfromString("Jazz");
		Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()] = 175;
		// RELAX USERA+B+G
		j = getUserIndexFromExistence(1,1,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = -1;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = -1;
		Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()] = (short) MusicGenreEnumType.getIDfromString("?");
		Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()] = -1;
		// RELAX UNKNOWN
		j = getUserIndexFromExistence(0,0,0);
		Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()] = (short) MusicGenreEnumType.getIDfromString("New Age");
		Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()] = 200;
		
		
		// PHONE
		i = ContextStateEnumType.PHONE.getID();
		for (j = 0; j < y; j++){
			Table_Context_User_Devices[i][j][Column.MUSIC_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.MIST_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.FAN_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.FLOORLAMP_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.BLOOM_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.GO_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.STRIP_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.TV_ONOFF.ordinal()] = 0;
		}
		// PHONE USERA
		j = getUserIndexFromExistence(1,0,0);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 150;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 225;
		// PHONE USERB
		j = getUserIndexFromExistence(0,1,0);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 180;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 50;
		// PHONE USERG
		j = getUserIndexFromExistence(0,0,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 125;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 125;
		// PHONE USERA+B
		j = getUserIndexFromExistence(1,1,0);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = -1;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = -1;
		// PHONE USERA+G
		j = getUserIndexFromExistence(1,0,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 150;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 225;
		// PHONE USERB+G
		j = getUserIndexFromExistence(0,1,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 180;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 50;
		// PHONE USERA+B+G
		j = getUserIndexFromExistence(1,1,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = -1;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = -1;
		// RELAX UNKNOWN
		j = getUserIndexFromExistence(0,0,0);
//		Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()] = (short) MusicGenreEnumType.getIDfromString("New Age");
//		Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()] = 200;
		
		
		i = ContextStateEnumType.TV.getID();
		for (j = 0; j < y; j++){
			Table_Context_User_Devices[i][j][Column.MUSIC_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.MIST_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.FAN_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.FLOORLAMP_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.BLOOM_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.GO_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.STRIP_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.TV_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.TV_MUTE.ordinal()] = 0;
		}
		// TV USERA
		j = getUserIndexFromExistence(1,0,0);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 100;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 225;
		// TV USERB
		j = getUserIndexFromExistence(0,1,0);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 100;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 225;
		// TV USERG
		j = getUserIndexFromExistence(0,0,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 100;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 225;
		// TV USERA+B
		j = getUserIndexFromExistence(1,1,0);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = -1;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 225;
		// TV USERA+G
		j = getUserIndexFromExistence(1,0,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 100;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 225;
		// TV USERB+G
		j = getUserIndexFromExistence(0,1,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = 100;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 225;
		// TV USERA+B+G
		j = getUserIndexFromExistence(1,1,1);
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_RED.ordinal()] = 200;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.FLOORLAMP_LUX.ordinal()] = -1;
//		Table_Context_User_Devices[i][j][Column.BLOOM_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.BLOOM_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.GO_LUX.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_RED.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_GREEN.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_BLUE.ordinal()] = 255;
//		Table_Context_User_Devices[i][j][Column.STRIP_LUX.ordinal()] = 255;
		Table_Context_User_Devices[i][j][Column.FAN_SPEED.ordinal()] = 225;
		// RELAX UNKNOWN
		j = getUserIndexFromExistence(0,0,0);
//		Table_Context_User_Devices[i][j][Column.MUSIC_TYPE.ordinal()] = (short) MusicGenreEnumType.getIDfromString("New Age");
//		Table_Context_User_Devices[i][j][Column.MUSIC_VOLUME.ordinal()] = 200;
		
		
		i = ContextStateEnumType.TVPHONE.getID();
		for (j = 0; j < y; j++){
			Table_Context_User_Devices[i][j][Column.MUSIC_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.MIST_ONOFF.ordinal()] = 0;
			Table_Context_User_Devices[i][j][Column.FAN_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.FLOORLAMP_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.BLOOM_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.GO_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.STRIP_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.TV_ONOFF.ordinal()] = 1;
			Table_Context_User_Devices[i][j][Column.TV_MUTE.ordinal()] = 1;
		}
		int i2 = ContextStateEnumType.TV.getID(), k;
		for (j = 0; j < y; j++){
			k = Column.FLOORLAMP_RED.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.FLOORLAMP_GREEN.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.FLOORLAMP_BLUE.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.FLOORLAMP_LUX.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.BLOOM_RED.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.BLOOM_GREEN.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.BLOOM_BLUE.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.BLOOM_LUX.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.GO_RED.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.GO_GREEN.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.GO_BLUE.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.GO_LUX.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.STRIP_RED.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.STRIP_GREEN.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.STRIP_BLUE.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.STRIP_LUX.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
			k = Column.FAN_SPEED.ordinal(); Table_Context_User_Devices[i][j][k] = Table_Context_User_Devices[i2][j][k];
		}
	}
}

enum Column{
	FLOORLAMP_ONOFF,
	FLOORLAMP_RED,
	FLOORLAMP_GREEN,
	FLOORLAMP_BLUE,
	FLOORLAMP_LUX,
	BLOOM_ONOFF,
	BLOOM_RED,
	BLOOM_GREEN,
	BLOOM_BLUE,
	BLOOM_LUX,
	GO_ONOFF,
	GO_RED,
	GO_GREEN,
	GO_BLUE,
	GO_LUX,
	STRIP_ONOFF,
	STRIP_RED,
	STRIP_GREEN,
	STRIP_BLUE,
	STRIP_LUX,
	FAN_ONOFF,
	FAN_SPEED,
	FAN_ROTATE,
	MIST_ONOFF,
	MUSIC_ONOFF,
	MUSIC_TYPE,
	MUSIC_VOLUME,
	TV_ONOFF,
	TV_MUTE;
	private static final int size = Column.values().length;
	public static int getLength(){
		return size;
	}
}
