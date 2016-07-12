package edu.uci.eecs.wukong.framework.gateway;

import edu.uci.eecs.wukong.framework.mptn.UDPMPTN;

/**
 * Thread that periodically send routing table hash to master
 * 
 * 
 * @author peter
 *
 */
public class RTPingSender implements Runnable {
	private UDPMPTN mptn;
	public RTPingSender(UDPMPTN mptn) {
		this.mptn = mptn;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
