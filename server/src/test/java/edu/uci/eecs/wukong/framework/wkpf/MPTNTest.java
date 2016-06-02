package edu.uci.eecs.wukong.framework.wkpf;

import java.nio.ByteBuffer;

import org.junit.Test;

import edu.uci.eecs.wukong.framework.model.MPTNPackage;
import edu.uci.eecs.wukong.framework.model.WKPFPackage;
import edu.uci.eecs.wukong.framework.mptn.MPTN;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;
import junit.framework.TestCase;

public class MPTNTest extends TestCase {
	private static final int SOURCE_IP = 1;
	private static final int DEST_IP = 2;
	private MPTN mptn = new MPTN();

	@Test
	public void testHandleWritePropertyMessage() throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(12);
		buffer.putInt(SOURCE_IP);
		buffer.putInt(DEST_IP);
		buffer.put(MPTNUtil.MPTN_MSATYPE_FWDREQ);
		buffer.put(WKPFUtil.WKPF_WRITE_PROPERTY);
		buffer.put((byte)1);
		buffer.put((byte)0);
		buffer.flip();
		MPTNPackage mptnPackage = new MPTNPackage(12);
		mptnPackage.setPayload(buffer.array());
		assertEquals(WKPFUtil.WKPF_WRITE_PROPERTY, mptn.processFWDMessage(mptnPackage));
	}
}
