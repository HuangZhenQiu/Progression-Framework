package edu.uci.eecs.wukong.prclass.icsdemo;

public enum MusicGenreEnumType {
	Q(-1, "?"),
	CLASSICAL(5, "Classical"), 
	JAZZ(3, "Jazz"),
	POP(1, "Pop"),
	NEWAGE(4, "New Age"),
	SOUNDTRACK(2, "Soundtrack")
	;
	 
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
        return Q;
    }
    
    public static MusicGenreEnumType fromID(int id) {
        for (MusicGenreEnumType b : MusicGenreEnumType.values()) {
            if (id == (b.getID())) {
            	return b;
            }
        }
        return Q;
    }
    
    public static int getIDfromString(String text) {
        if (text != null) {
            for (MusicGenreEnumType b : MusicGenreEnumType.values()) {
                if (text.equalsIgnoreCase(b.getName())) {
                  return b.getID();
                }
            }
        }
        return Q.getID();
    }
 
    // standard getters and setters
}
