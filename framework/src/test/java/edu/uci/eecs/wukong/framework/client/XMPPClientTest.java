package edu.uci.eecs.wukong.framework.client;

public class XMPPClientTest {

	public static void main(String[] args) {
		XMPPContextClient client = XMPPContextClient.getInstance();
		client.subscribe("", null);
	}
}
