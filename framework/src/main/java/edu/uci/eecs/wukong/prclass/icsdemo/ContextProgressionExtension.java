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
			currentContext = icsContext.getContext();
			if (icsDemoPlugin instanceof ICSDemoHuePrClass){
//				((ICSDemoHuePrClass)icsDemoPlugin).setLight(preferenceTable.lookup());
			} else if (icsDemoPlugin instanceof ICSDemoHuePrClass){
//				((ICSDemoFanPrClass)icsDemoPlugin).setFan(preferenceTable.lookup());
			} else if (icsDemoPlugin instanceof ICSDemoHuePrClass){
//				((ICSDemoMusicPrClass)icsDemoPlugin).setMusic(preferenceTable.lookup());
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
