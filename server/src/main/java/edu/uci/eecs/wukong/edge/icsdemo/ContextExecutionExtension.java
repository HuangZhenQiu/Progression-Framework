package edu.uci.eecs.wukong.edge.icsdemo;

import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.api.FactorExecutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.edge.icsdemo.ContextTable;
import edu.uci.eecs.wukong.edge.icsdemo.PreferenceTable;
import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;

public class ContextExecutionExtension extends AbstractExecutionExtension<EdgePrClass> implements FactorExecutable, Channelable<Short> {
	private static Logger logger = LoggerFactory.getLogger(ContextExecutionExtension.class);
	private PreferenceTable preferenceTable = new PreferenceTable();
	private ContextTable contextTable = new ContextTable();
	private String currentContext = null;
	private short currentUser = 0;
	private EdgePrClass icsDemoPlugin;
	
	public ContextExecutionExtension(EdgePrClass plugin) {
		super(plugin);
		icsDemoPlugin = plugin;
	}

	public void execute(BaseFactor context) {
		if (context instanceof ICSContext) {
			ICSContext icsContext = (ICSContext) context;
			currentContext = icsContext.getContext();
			logger.info("Before lookup"+currentContext);
			icsContext = preferenceTable.lookup(icsContext);
			logger.info("After lookup"+icsContext.getContext());

			if (icsDemoPlugin instanceof ICSDemoFloorlampEdgeClass){
				if(icsContext.Floorlamp == 1){
					((ICSDemoFloorlampEdgeClass)icsDemoPlugin).setColorFromRGB(icsContext.Floorlamp_R, icsContext.Floorlamp_G, icsContext.Floorlamp_B);
					((ICSDemoFloorlampEdgeClass)icsDemoPlugin).setBrightness(icsContext.Floorlamp_Lux);
				}
				((ICSDemoFloorlampEdgeClass)icsDemoPlugin).setOnOff(icsContext.Floorlamp);
			} else if (icsDemoPlugin instanceof ICSDemoGoEdgeClass){
				if(icsContext.Go == 1){
					((ICSDemoGoEdgeClass)icsDemoPlugin).setColorFromRGB(icsContext.Go_R, icsContext.Go_G, icsContext.Go_B);
					((ICSDemoGoEdgeClass)icsDemoPlugin).setBrightness(icsContext.Go_Lux);
				}
				((ICSDemoGoEdgeClass)icsDemoPlugin).setOnOff(icsContext.Go);
			} else if (icsDemoPlugin instanceof ICSDemoBloomEdgeClass){
				if(icsContext.Bloom == 1){
					((ICSDemoBloomEdgeClass)icsDemoPlugin).setColorFromRGB(icsContext.Bloom_R, icsContext.Bloom_G, icsContext.Bloom_B);
					((ICSDemoBloomEdgeClass)icsDemoPlugin).setBrightness(icsContext.Bloom_Lux);
				}
				((ICSDemoBloomEdgeClass)icsDemoPlugin).setOnOff(icsContext.Bloom);
			} else if (icsDemoPlugin instanceof ICSDemoStripEdgeClass){
				if(icsContext.Strip == 1){
					((ICSDemoStripEdgeClass)icsDemoPlugin).setColorFromRGB(icsContext.Strip_R, icsContext.Strip_G, icsContext.Strip_B);
					((ICSDemoStripEdgeClass)icsDemoPlugin).setBrightness(icsContext.Strip_Lux);
				}
				((ICSDemoStripEdgeClass)icsDemoPlugin).setOnOff(icsContext.Strip);
			} else if (icsDemoPlugin instanceof ICSDemoFanEdgeClass){
				if(icsContext.Fan == 1){
					((ICSDemoFanEdgeClass)icsDemoPlugin).setFanSpeed(icsContext.Fan_Speed);
					((ICSDemoFanEdgeClass)icsDemoPlugin).setFanRotation(icsContext.Fan_Rotate);
				} 
				((ICSDemoFanEdgeClass)icsDemoPlugin).setFanOnOff(icsContext.Fan);
			} else if (icsDemoPlugin instanceof ICSDemoMusicEdgeClass){
				if(icsContext.Music == 1){
					((ICSDemoMusicEdgeClass)icsDemoPlugin).setMusicGenre(icsContext.Music_Type);
					((ICSDemoMusicEdgeClass)icsDemoPlugin).setSpeakerVolume(icsContext.Music_Vol);
				}
				((ICSDemoMusicEdgeClass)icsDemoPlugin).setSpeakerOnOff(icsContext.Fan);
			} else if (icsDemoPlugin instanceof ICSDemoAromaEdgeClass){
				((ICSDemoAromaEdgeClass)icsDemoPlugin).setAromaOnOff(icsContext.Mist);
			} else if (icsDemoPlugin instanceof ICSDemoTVEdgeClass){
				if(icsContext.TV == 1){
					((ICSDemoTVEdgeClass)icsDemoPlugin).setTVMute(icsContext.TV_Mute);
				}
				((ICSDemoTVEdgeClass)icsDemoPlugin).setTVOnOff(icsContext.TV);
			} else if (icsDemoPlugin instanceof ICSDemoQEdgeClass){
				if(icsContext.Command_Mode != 3){
					((ICSDemoQEdgeClass)icsDemoPlugin).setQuestion(icsContext.isConflict());
				}
			} 
		}
	}

	public void execute(ChannelData<Short> data) {
		if (data.getNpp().getPropertyId() == 1) {
			currentUser = data.getValue();
			logger.info(""+currentUser);
		}
	}

}
