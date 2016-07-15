package edu.uci.eecs.wukong.framework.xmpp;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.util.XmlStringBuilder;


/**
 * FactorExtensionElement defines the xml format to represent factor messages that transmitted through pub/sub channel.
 * Its type represents the name of factor java class, and its content is the json representation of the factor object 
 * in the pub/sub message.
 */

@XmlRootElement
public class FactorExtensionElement implements ExtensionElement {
    public static final String NAMESPACE = "FactorExtensionElement";
    public static final String ELEMENT = "http://wukong.org/progression/factor";
    
    private String type;
    /*Factor Class Name*/
    private String className;
    /*Factor Object json content*/
    private String content;
    
    public FactorExtensionElement() {
    	
    }
    
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
		XmlStringBuilder buf = new XmlStringBuilder();
		buf.halfOpenElement(this).attribute("type", type);
		buf.optAttribute("className", className);
		buf.optAttribute("content", content);
        buf.rightAngleBracket();
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

	@XmlAttribute
	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	@XmlAttribute
	public void setContent(String content) {
		this.content = content;
	}

	public String getClassName() {
		return className;
	}

	@XmlAttribute
	public void setClassName(String className) {
		this.className = className;
	}
}
