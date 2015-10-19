package edu.uci.eecs.wukong.prclass.icsdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

@WuClass(id = 9006)
public class ICSDemoMusicPrClass extends PrClass {

	@WuProperty(name = "speaker_onoff", id = 0, type = PropertyType.Output)
	private short speaker_onoff;

	@WuProperty(name = "music_genre", id = 1, type = PropertyType.Output)
	private short music_genre;
	
	@WuProperty(name = "speaker_volume", id = 2, type = PropertyType.Output)
	private short speaker_volume;
	
	public ICSDemoMusicPrClass() {
		super("ICSDemoMusicPrClass");
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new ContextProgressionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		List<String> topics = new ArrayList<String> ();
		topics.add(ICSContext.TOPIC);
		return topics;
	}

	public short getMusicGenre() {
		return music_genre;
	}

	public void setMusicGenre(short music_genre) {
		music_genre = (short) MusicGenreEnumType.fromID(music_genre).getID();
		if(music_genre < 0) return;
		this.support.firePropertyChange("music_genre", this.music_genre, music_genre);
		this.music_genre = music_genre;
	}

	public short getSpeakerVolume() {
		return speaker_volume;
	}

	public void setSpeakerVolume(short speaker_volume) {
		if(speaker_volume > 255 || speaker_volume < 0) return;
		this.support.firePropertyChange("speaker_volume", this.speaker_volume, speaker_volume);
		this.speaker_volume = speaker_volume;
	}
	
	public short getSpeakerOnOff() {
		return speaker_volume;
	}

	public void setSpeakerOnOff(short speaker_onoff) {
		if(speaker_onoff > 1 || speaker_onoff < 0) return;
		this.support.firePropertyChange("speaker_onoff", this.speaker_onoff, speaker_onoff);
		this.speaker_onoff = speaker_onoff;
	}
}
