package edu.uci.eecs.wukong.edge.icsdemo;

public enum ContextStateEnumType {
	INIT(0, "INIT"), 
	RELAX(1, "RELAX"),
	TV(2, "TV"),
	PHONE(3, "PHONE"),
	TVPHONE(4, "TV+PHONE");
	
	private static final int size = ContextStateEnumType.values().length;
    private int id;
    private String name;
    
 
    private ContextStateEnumType(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public int getID(){
    	return this.id;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public static int getLength(){
    	return size;
    }
    
    public static ContextStateEnumType fromString(String text) {
        if (text != null) {
            for (ContextStateEnumType b : ContextStateEnumType.values()) {
                if (text.equalsIgnoreCase(b.getName())) {
                  return b;
                }
            }
        }
        return null;
    }
 
    // standard getters and setters
}
