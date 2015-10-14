package edu.uci.eecs.wukong.prclass.icsdemo;

import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.api.FactorExecutable;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.xmpp.XMPPFactorClient;
import edu.uci.eecs.wukong.prclass.icsdemo.ContextTable;
import edu.uci.eecs.wukong.prclass.icsdemo.PreferenceTable;

public class ContextProgressionExtension extends AbstractProgressionExtension implements FactorExecutable, Channelable {
	private PreferenceTable preferenceTable = new PreferenceTable();
	private ContextTable contextTable = new ContextTable();
	private String currentContext = null;
	private short currentUser = 0;
	private ICSDemoPrClass icsDemoPlugin;
	
	public ContextProgressionExtension(PrClass plugin) {
		super(plugin);
		icsDemoPlugin = (ICSDemoPrClass) plugin;
	}

	public void execute(BaseFactor context) {
		if (context instanceof ICSContext) {
			ICSContext icsContext = (ICSContext) context;
			currentContext = icsContext.getContext();
			icsDemoPlugin.setFan(preferenceTable.lookup());
			icsDemoPlugin.setLight(preferenceTable.lookup());
			icsDemoPlugin.setMusic(preferenceTable.lookup());
		}
	}

	public void execute(ChannelData data) {
		if (data.getNpp().getPropertyId() == 1) {
			currentUser = data.getValue();
			icsDemoPlugin.setFan(preferenceTable.lookup());
			icsDemoPlugin.setLight(preferenceTable.lookup());
			icsDemoPlugin.setMusic(preferenceTable.lookup());
		}
	}

}
