package edu.uci.eecs.wukong.prclass.icsdemo;

public enum TVStateEnumType {
	Q(-1, "?"),
	ON(1, "On"),
	OFF(2, "Off"),
	MUTE(2, "Mute");
	 
	private int id;
    private String name;
 
    private TVStateEnumType(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getID(){
    	return this.id;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public static TVStateEnumType fromString(String text) {
        if (text != null) {
            for (TVStateEnumType b : TVStateEnumType.values()) {
                if (text.equalsIgnoreCase(b.getName())) {
                  return b;
                }
            }
        }
        return null;
    }
 
    // standard getters and setters
}
