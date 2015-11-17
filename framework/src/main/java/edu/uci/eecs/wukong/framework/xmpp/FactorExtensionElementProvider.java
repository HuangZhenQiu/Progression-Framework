package edu.uci.eecs.wukong.framework.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.xmlpull.v1.XmlPullParser;

import edu.uci.eecs.wukong.framework.xmpp.FactorExtensionElement;

public class FactorExtensionElementProvider extends ExtensionElementProvider<FactorExtensionElement> {

	@Override
	public FactorExtensionElement parse(XmlPullParser parser, int initialDepth)
			throws Exception {
		String type = null;
        String className = null;
        String content = null;
        if (parser.getEventType() == XmlPullParser.START_TAG
                && parser.getName().equalsIgnoreCase(FactorExtensionElement.ELEMENT)) {
            type = parser.getAttributeValue(null, "type");
            className = parser.getAttributeValue(null, "className");
        } else {
            throw new SmackException("Malformed wukong factor element");
        }

        content = parser.nextText();

        if (!(parser.getEventType() == XmlPullParser.END_TAG
                && parser.getName().equalsIgnoreCase(FactorExtensionElement.ELEMENT))) {
            throw new SmackException("Malformed wukong factor element");
        }

        if (type != null && className != null && content != null) {
            return new FactorExtensionElement(type, className, content);
        } else {
            throw new SmackException("Factor elment with missing attributes. Attributes: type=" + type + " className="
                            + className + " content=" + content);
        }
	}

}
