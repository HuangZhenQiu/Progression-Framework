package edu.uci.eecs.wukong.prclass.icsdemo;

import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.api.FactorExecutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.xmpp.XMPPFactorClient;
import edu.uci.eecs.wukong.prclass.demo.DemoProgressionExtension;
import edu.uci.eecs.wukong.prclass.icsdemo.ContextTable;
import edu.uci.eecs.wukong.prclass.icsdemo.PreferenceTable;

public class ContextProgressionExtension extends AbstractProgressionExtension implements FactorExecutable, Channelable {
	private static Logger logger = LoggerFactory.getLogger(ContextProgressionExtension.class);
	private PreferenceTable preferenceTable = new PreferenceTable();
	private ContextTable contextTable = new ContextTable();
	private String currentContext = null;
	private short currentUser = 0;
	private PrClass icsDemoPlugin;
	
	public ContextProgressionExtension(PrClass plugin) {
		super(plugin);
		icsDemoPlugin = plugin;
	}

	public void execute(BaseFactor context) {
		if (context instanceof ICSContext) {
			ICSContext icsContext = (ICSContext) context;
//			preferenceTable.lookup();
			currentContext = icsContext.getContext();
			if (icsDemoPlugin instanceof ICSDemoHuePrClass){
				if (icsDemoPlugin instanceof ICSDemoFloorlampPrClass){
					((ICSDemoHuePrClass)icsDemoPlugin).setColorFromRGB(icsContext.Floorlamp_R, icsContext.Floorlamp_G, icsContext.Floorlamp_B);
				} else if (icsDemoPlugin instanceof ICSDemoGoPrClass){
					((ICSDemoHuePrClass)icsDemoPlugin).setColorFromRGB(icsContext.Go_R, icsContext.Go_G, icsContext.Go_B);
				} else if (icsDemoPlugin instanceof ICSDemoBloomPrClass){
					((ICSDemoHuePrClass)icsDemoPlugin).setColorFromRGB(icsContext.Bloom_R, icsContext.Bloom_G, icsContext.Bloom_B);
				} else if (icsDemoPlugin instanceof ICSDemoStripPrClass){
					((ICSDemoHuePrClass)icsDemoPlugin).setColorFromRGB(icsContext.Strip_R, icsContext.Strip_G, icsContext.Strip_B);
				}
			} else if (icsDemoPlugin instanceof ICSDemoFanPrClass){
				((ICSDemoFanPrClass)icsDemoPlugin).setFanSpeed(icsContext.Fan_Speed);
				((ICSDemoFanPrClass)icsDemoPlugin).setFanRotation(icsContext.Fan_Rotate);
			} else if (icsDemoPlugin instanceof ICSDemoMusicPrClass){
				((ICSDemoMusicPrClass)icsDemoPlugin).setMusicGenre(icsContext.Music_Type);
				((ICSDemoMusicPrClass)icsDemoPlugin).setSpeakerVolume(icsContext.Music_Vol);
			} else if (icsDemoPlugin instanceof ICSDemoAromaPrClass){
				((ICSDemoAromaPrClass)icsDemoPlugin).setAromaOnOff(icsContext.Mist);
			} else if (icsDemoPlugin instanceof ICSDemoTVPrClass){
				((ICSDemoTVPrClass)icsDemoPlugin).setTVState(icsContext.TV);
			} else if (icsDemoPlugin instanceof ICSDemoQPrClass){
				((ICSDemoQPrClass)icsDemoPlugin).setQuestion(icsContext.Context);
			} 
		}
	}

	public void execute(ChannelData data) {
		if (data.getNpp().getPropertyId() == 1) {
			currentUser = data.getValue();
			logger.info(""+currentUser);
		}
	}

}
