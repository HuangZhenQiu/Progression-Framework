package edu.uci.eecs.wukong.framework.wkpf;

public enum WuClassType {
	Threshold((short)1),
	And_Gate((short)2),
	Or_Gate((short)3),
	Xor_Gate((short)4),
	Not_Gate((short)5),
	If_Short((short)6),
	If_Boolean((short)7),
	Condition_Selector_Boolean((short)8),
	Condition_Selector_Short((short)9),
	Math_Op((short)10),
	Virtual_Slider((short)11),
	Server((short)44),
	Multiplexer((short)46),
	Equal((short)12),
	GestureSwitch((short)13),
	GestureToBrightnessControl((short)14),
	GestureToColorControl((short)15),
	GestureToHangoutControl((short)16),
	GestureToVideoControl((short)17),
	Light_Sensor((short)1001),
	PIR_Sensor((short)1003),
	Binary_Sensor((short)1004),
	Temperature_Humidity_Sensor((short)1005),
	Slider((short)1006),
	Magnetic_Sensor((short)1007),
	Pressure_Sensor_0((short)1008),
	Gh_Sensor((short)1009),
	Ir_Sensor((short)1010),
	Ultrasound_Sensor((short)1011),
	Button((short)1012),
	Temperature_Sensor((short)1013),
	Sound_Sensor((short)1014),
	Touch_Sensor((short)1015),
	Makey_Gesture((short)1016),
	Binary_TestSensor((short)1901),
	Integer_TestSensor((short)1902),
	User((short)1903),
	Controller((short)1904),
	Gesture((short)1905),
	UserAware((short)1906),
	ICS_User((short)1907),
	Smart_Mug((short)1908),
	Cabinet((short)1909),
	Phone((short)1910),
	RealSense((short)1911),
	EEGServer((short)1912),
	Light_Actuator((short)2001),
	LED((short)2002),
	Fan((short)2003),
	Buzzer((short)2004),
	Sound((short)2005),
	RGBLED((short)2006),
	Dimmer((short)2007),
	Plugin((short)2008),
	Relay((short)2009),
	Grove_LCD((short)2010),
	Grove_MP3((short)2011),
	Music_LED((short)2012),
	Gesture_MP3((short)2013),
	Number((short)2014),
	Philip_Hue_Bulb_Actuator((short)2015),
	Philip_Hue_Bloom_Actuator((short)2016),
	Philip_Hue_Go_Actuator((short)2017),
	Philip_Hue_Strip_Actuator((short)2018),
	MOSFET_Fan((short)2019),
	Sonos((short)2020),
	Aroma((short)2021),
	IRremote_TV((short)2022),
	Philip_Hue_Bulb_Sensor((short)2023),
	Philip_Hue_Bloom_Sensor((short)2024),
	Philip_Hue_Go_Sensor((short)2025),
	Philip_Hue_Strip_Sensor((short)2026),
	ST_Switch((short)2027),
	ST_Presence((short)2028),
	NodeRED_InputFrom((short)2029),
	NodeRED_OutputTo((short)2030),
	Annotation((short)2031),
	Debug_Inspector((short)2032),
	Debug_Trigger((short)2033),
	HangoutControl((short)2034),
	VideoControl((short)2035),
	MOSFET_LED((short)2036),
	Intel_Sound((short)2037),
	ICSDemoFloorlampPrClass((short)9000),
	ICSDemoBloomPrClass((short)9001),
	ICSDemoGoPrClass((short)9002),
	ICSDemoStripPrClass((short)9003),
	ICSDemoFanPrClass((short)9004),
	ICSDemoAromaPrClass((short)9005),
	ICSDemoMusicPrClass((short)9006),
	ICSDemoTVPrClass((short)9007),
	ICSDemoQPrClass((short)9009),
	SwichPrClass((short)10001),
	TimerPrClass((short)10002),
	ICSDemoPrClass((short)10003),
	WeatherPrClass((short)10111),
	BufferEnabledPrClass((short)10112),
	OccupanyDetection((short)10113),
	SimpleInputPrClass((short)10115),
	ActivityPrClass((short)10116),
	UIImage((short)11001),
	LocalizationLoadTester((short)20115),
	UIButton((short)11002),
	UINumber((short)11003),
	Email((short)11004),
	URL((short)11005),
	UISlider((short)11006),
	UIScene((short)11007),
	Array_Test((short)20001),
	String_Test((short)20002),
	NodeREDSignalSender((short)21001),
	NodeREDSignalReceiver((short)21002);
	
	private final short wuClassId;
	
	WuClassType(short wuClassId) {
		this.wuClassId = wuClassId;
	}
	
	public short getWuClassId() {
		return this.wuClassId;
	}
}
