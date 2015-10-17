package edu.uci.eecs.wukong.prclass.icsdemo;

public enum ContextStateEnumType {
	INIT(1, "INIT"), 
	RELAX(2, "RELAX"),
	TV(3, "TV"),
	PHONE(4, "PHONE"),
	TVPHONE(5, "TV+PHONE");
	 
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
