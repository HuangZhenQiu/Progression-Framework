package edu.uci.eecs.wukong.framework.xmpp;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;


/**
 * FactorExtensionElement defines the xml format to represent factor messages that transmitted through pub/sub channel.
 * Its type represents the name of factor java class, and its content is the json representation of the factor object 
 * in the pub/sub message.
 */
public class FactorExtensionElement implements ExtensionElement {
    public static final String NAMESPACE = "http://wukong.org/progression/factor";
    public static final String ELEMENT = "factor";
    
    private String type;
    /*Factor Class Name*/
    private String className;
    /*Factor Object json content*/
    private String content;
    
    public FactorExtensionElement(String type, String className, String content) {
    	this.type = type;
    	this.className = className;
    	this.content = content;
    }
    
	@Override
	public String getElementName() {
		return NAMESPACE;
	}

	@Override
	public XmlStringBuilder toXML() {
		XmlStringBuilder buf = new XmlStringBuilder(this);
		buf.halfOpenElement(this).attribute("type", type);
		buf.optAttribute("className", className);
        buf.rightAngleBracket();
        buf.append(content);
        buf.closeElement(this);
        return buf;
	}

	@Override
	public String getNamespace() {
		// TODO Auto-generated method stub
		return ELEMENT;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}
