package edu.uci.eecs.wukong.plugin.icsdemo;

import edu.uci.eecs.wukong.framework.context.BaseContext;
import edu.uci.eecs.wukong.framework.context.ICSContext;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.api.ContextExecutable;
import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

import edu.uci.eecs.wukong.plugin.icsdemo.PreferenceTable;
import edu.uci.eecs.wukong.plugin.icsdemo.ContextTable;

public class ContextProgressionExtension extends AbstractProgressionExtension implements ContextExecutable, Channelable {
	private PreferenceTable preferenceTable = new PreferenceTable();
	private ContextTable contextTable = new ContextTable();
	private String currentContext = null;
	private short currentUser = 0;
	private ICSDemoPlugin icsDemoPlugin;
	
	public ContextProgressionExtension(Plugin plugin) {
		super(plugin);
		icsDemoPlugin = (ICSDemoPlugin) plugin;
	}

	public void execute(BaseContext context) {
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
