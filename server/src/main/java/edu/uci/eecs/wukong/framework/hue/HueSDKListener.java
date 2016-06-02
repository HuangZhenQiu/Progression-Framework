package edu.uci.eecs.wukong.framework.hue;

import java.util.List;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager;
import com.philips.lighting.model.PHBridge;

public class HueSDKListener implements PHSDKListener {
	private PHHeartbeatManager heartbeatManager;
	private PHHueSDK hueSDK;
	
	public HueSDKListener() {
		this.heartbeatManager = PHHeartbeatManager.getInstance();
	}
	
	public void setHueSDK(PHHueSDK sdk) {
		this.hueSDK = sdk;
	}
	
	@Override
	public void onAccessPointsFound(List<PHAccessPoint> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAuthenticationRequired(PHAccessPoint bridge) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBridgeConnected(PHBridge bridge) {
		this.hueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
	}

	@Override
	public void onCacheUpdated(int arg0, PHBridge bridge) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionLost(PHAccessPoint bridge) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionResumed(PHBridge bridge) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(int arg0, String arg1) {
		// TODO Auto-generated method stub

	}

}
