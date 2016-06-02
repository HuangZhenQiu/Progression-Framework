package edu.uci.eecs.wukong.prclass.icsdemo;

public enum BinaryEnumType {
	Q(-1, "?"),
	ON(1, "On"),
	OFF(2, "Off");
	 
	private int id;
    private String name;
 
    private BinaryEnumType(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getID(){
    	return this.id;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public static BinaryEnumType fromString(String text) {
        if (text != null) {
            for (BinaryEnumType b : BinaryEnumType.values()) {
                if (text.equalsIgnoreCase(b.getName())) {
                  return b;
                }
            }
        }
        return null;
    }
 
    // standard getters and setters
}
