package edu.uci.eecs.wukong.prclass.icsdemo;

public enum MusicGenreEnumType {
	Q(-1, "?"),
	POP(1, "Pop"),
	SOUNDTRACK(2, "Soundtrack"),
	JAZZ(3, "Jazz"),
	NEWAGE(4, "New Age"),
	CLASSICAL(5, "Classical")
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
