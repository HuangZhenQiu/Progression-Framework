package edu.uci.eecs.wukong.prclass.icsdemo;

public enum MusicGenreEnumType {
	Q(-1, "?"),
	CLASSICAL(1, "Classical"), 
	JAZZ(2, "Jazz"),
	POP(3, "Pop"),
	WARMING(4, "Warming"),
	FIVETH(5, "Fiveth");
	 
	private int id;
    private String name;
 
    private MusicGenreEnumType(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getID(){
    	return this.id;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public static MusicGenreEnumType fromString(String text) {
        if (text != null) {
            for (MusicGenreEnumType b : MusicGenreEnumType.values()) {
                if (text.equalsIgnoreCase(b.getName())) {
                  return b;
                }
            }
        }
        return null;
    }
 
    // standard getters and setters
}
