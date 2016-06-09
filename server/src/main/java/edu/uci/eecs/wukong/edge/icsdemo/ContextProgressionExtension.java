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
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public class ContextProgressionExtension extends AbstractExecutionExtension<PipelinePrClass> implements FactorExecutable, Channelable<Short> {
	private static Logger logger = LoggerFactory.getLogger(ContextProgressionExtension.class);
	private PreferenceTable preferenceTable = new PreferenceTable();
	private ContextTable contextTable = new ContextTable();
	private String currentContext = null;
	private short currentUser = 0;
	private PipelinePrClass icsDemoPlugin;
	
	public ContextProgressionExtension(PipelinePrClass plugin) {
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

			if (icsDemoPlugin instanceof ICSDemoFloorlampPrClass){
				if(icsContext.Floorlamp == 1){
					((ICSDemoFloorlampPrClass)icsDemoPlugin).setColorFromRGB(icsContext.Floorlamp_R, icsContext.Floorlamp_G, icsContext.Floorlamp_B);
					((ICSDemoFloorlampPrClass)icsDemoPlugin).setBrightness(icsContext.Floorlamp_Lux);
				}
				((ICSDemoFloorlampPrClass)icsDemoPlugin).setOnOff(icsContext.Floorlamp);
			} else if (icsDemoPlugin instanceof ICSDemoGoPrClass){
				if(icsContext.Go == 1){
					((ICSDemoGoPrClass)icsDemoPlugin).setColorFromRGB(icsContext.Go_R, icsContext.Go_G, icsContext.Go_B);
					((ICSDemoGoPrClass)icsDemoPlugin).setBrightness(icsContext.Go_Lux);
				}
				((ICSDemoGoPrClass)icsDemoPlugin).setOnOff(icsContext.Go);
			} else if (icsDemoPlugin instanceof ICSDemoBloomPrClass){
				if(icsContext.Bloom == 1){
					((ICSDemoBloomPrClass)icsDemoPlugin).setColorFromRGB(icsContext.Bloom_R, icsContext.Bloom_G, icsContext.Bloom_B);
					((ICSDemoBloomPrClass)icsDemoPlugin).setBrightness(icsContext.Bloom_Lux);
				}
				((ICSDemoBloomPrClass)icsDemoPlugin).setOnOff(icsContext.Bloom);
			} else if (icsDemoPlugin instanceof ICSDemoStripPrClass){
				if(icsContext.Strip == 1){
					((ICSDemoStripPrClass)icsDemoPlugin).setColorFromRGB(icsContext.Strip_R, icsContext.Strip_G, icsContext.Strip_B);
					((ICSDemoStripPrClass)icsDemoPlugin).setBrightness(icsContext.Strip_Lux);
				}
				((ICSDemoStripPrClass)icsDemoPlugin).setOnOff(icsContext.Strip);
			} else if (icsDemoPlugin instanceof ICSDemoFanPrClass){
				if(icsContext.Fan == 1){
					((ICSDemoFanPrClass)icsDemoPlugin).setFanSpeed(icsContext.Fan_Speed);
					((ICSDemoFanPrClass)icsDemoPlugin).setFanRotation(icsContext.Fan_Rotate);
				} 
				((ICSDemoFanPrClass)icsDemoPlugin).setFanOnOff(icsContext.Fan);
			} else if (icsDemoPlugin instanceof ICSDemoMusicPrClass){
				if(icsContext.Music == 1){
					((ICSDemoMusicPrClass)icsDemoPlugin).setMusicGenre(icsContext.Music_Type);
					((ICSDemoMusicPrClass)icsDemoPlugin).setSpeakerVolume(icsContext.Music_Vol);
				}
				((ICSDemoMusicPrClass)icsDemoPlugin).setSpeakerOnOff(icsContext.Fan);
			} else if (icsDemoPlugin instanceof ICSDemoAromaPrClass){
				((ICSDemoAromaPrClass)icsDemoPlugin).setAromaOnOff(icsContext.Mist);
			} else if (icsDemoPlugin instanceof ICSDemoTVPrClass){
				if(icsContext.TV == 1){
					((ICSDemoTVPrClass)icsDemoPlugin).setTVMute(icsContext.TV_Mute);
				}
				((ICSDemoTVPrClass)icsDemoPlugin).setTVOnOff(icsContext.TV);
			} else if (icsDemoPlugin instanceof ICSDemoQPrClass){
				if(icsContext.Command_Mode != 3){
					((ICSDemoQPrClass)icsDemoPlugin).setQuestion(icsContext.isConflict());
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
