package edu.uci.eecs.wukong.framework.hue;

import com.philips.lighting.hue.listener.PHGroupListener;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.listener.PHSceneListener;
import com.philips.lighting.hue.listener.PHScheduleListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHBridge;

import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HueManager {
	private static Logger logger = LoggerFactory.getLogger(HueManager.class);
	private PHHueSDK hueSDK;
	private PHBridgeResourcesCache resourceCache;
	private PHLightListener lightListener = new PHLightListener() {

		@Override
		public void onError(int arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStateUpdate(Hashtable<String, String> arg0,
				List<PHHueError> arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSuccess() {
			// TODO Auto-generated method stub
			
		}
	};
	
	private PHGroupListener groupListener =  new PHGroupListener() {

		@Override
		public void onError(int arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStateUpdate(Hashtable<String, String> arg0,
				List<PHHueError> arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSuccess() {
			// TODO Auto-generated method stub
			
		}
	};
	
	private PHSceneListener sceneListener = new PHSceneListener() {

		@Override
		public void onError(int arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStateUpdate(Hashtable<String, String> arg0,
				List<PHHueError> arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSuccess() {
			// TODO Auto-generated method stub
			
		}
	};
	
	private PHScheduleListener scheduleListener = new PHScheduleListener() {

		@Override
		public void onError(int arg0, String arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStateUpdate(Hashtable<String, String> arg0,
				List<PHHueError> arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSuccess() {
			// TODO Auto-generated method stub
			
		}
	};
	
	public HueManager() {
		this.hueSDK = PHHueSDK.create(); // or call .getInstance() effectively the same
		this.hueSDK.getNotificationManager().registerSDKListener(new HueSDKListener());
	}
	
	public void connect(String ipAddress, String username) {
		PHAccessPoint accessPoint = new PHAccessPoint();
		accessPoint.setIpAddress(ipAddress);
		accessPoint.setUsername(username);
		hueSDK.connect(accessPoint);
	}
	
	public Hashtable<String, PHLight> getAllLights() {
		refreshCache();
		return resourceCache.getLights();
	}
	
	public void refreshCache() {
		resourceCache = hueSDK.getSelectedBridge().getResourceCache();
	}
	
	public void updateLight(String lightIdentifier, PHLightState lightState) {
		PHBridge bridge = PHHueSDK.getInstance().getSelectedBridge();
		if (bridge != null) {
			bridge.updateLightState(lightIdentifier, lightState, lightListener);
		} else {
			logger.error("There is no connected bridge available for update light");
		}
	}
	
	public void createGroup(String name, String[] identifiers) {
		PHBridge bridge = PHHueSDK.getInstance().getSelectedBridge();
		if (bridge != null) {
			bridge.createGroup(name, identifiers, this.groupListener);
		} else {
			logger.error("There is no connected bridge available for creating group");
		}
	}
	
	public void deleteGroup() {
		
	}
}
