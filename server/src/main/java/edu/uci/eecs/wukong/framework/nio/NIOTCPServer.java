package edu.uci.eecs.wukong.framework.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.mptn.MPTNMessageListener;
import edu.uci.eecs.wukong.framework.mptn.MPTNPackageParser;
import edu.uci.eecs.wukong.framework.mptn.packet.MPTNPacket;
import edu.uci.eecs.wukong.framework.mptn.packet.TCPMPTNPacket;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;

/**
 * TCP server used for receiving MPTN message from master and other gateways. Currently, it
 * is not used for connecting with other gateways.
 * 
 * 
 * @author peter
 *
 */
public class NIOTCPServer implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(NIOTCPServer.class);
	private static final int BUFFER_SIZE = 4096;
	private static int THREAD_POOL_SIZE = 5;
	private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	private ByteBuffer idBytes = ByteBuffer.allocate(MPTNUtil.MPTN_ID_LEN);
	private ByteBuffer nouceBytes = ByteBuffer.allocate(MPTNUtil.MPTN_TCP_NOUNCE_SIZE);
	private ByteBuffer lengthBytes = ByteBuffer.allocate(MPTNUtil.MPTN_TCP_PACKAGE_SIZE).order(ByteOrder.BIG_ENDIAN);
	private Map<SocketAddress, SocketChannel> activeChannels;
	private Map<SocketChannel, List<ByteBuffer>> outputQueue;
	private Map<Long, AsyncCallback<TCPMPTNPacket>> nouceCache;
	private List<MPTNMessageListener<TCPMPTNPacket>> listeners;
	private MPTNPackageParser<TCPMPTNPacket> paser;
	private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	private List<ChangeRequest> pendingChanges;
	private Selector selector;
	private ServerSocketChannel serverChannel;

	public NIOTCPServer(SocketAddress master, int port) {
		try {
			outputQueue = new HashMap<SocketChannel, List<ByteBuffer>> ();
			activeChannels = new HashMap<SocketAddress, SocketChannel> ();
			nouceCache = new HashMap<Long, AsyncCallback<TCPMPTNPacket>> ();
			pendingChanges = new ArrayList<ChangeRequest>();
			listeners = new ArrayList<MPTNMessageListener<TCPMPTNPacket>> ();
			paser = new MPTNPackageParser<TCPMPTNPacket> (TCPMPTNPacket.class);
			selector = SelectorProvider.provider().openSelector();
		    serverChannel = ServerSocketChannel.open();
		    serverChannel.configureBlocking(false);
		    InetSocketAddress isa = new InetSocketAddress(port);
		    serverChannel.socket().bind(isa);
		    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		    
		    // client channel to master
		    SocketChannel channel = SocketChannel.open(master);
			channel.configureBlocking(false);
			channel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
			channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
			channel.register(selector, SelectionKey.OP_READ);
		    activeChannels.put(master, channel);
		    
		    
		} catch (IOException e) {
			logger.error("Fail to open tcp server socket for TCP server: " + e.toString());
			System.exit(-1);
		}
	}
	
	public void addMPTNMessageListener(MPTNMessageListener<TCPMPTNPacket> listener) {
		this.listeners.add(listener);
	}
	
	public MPTNPacket send(SocketAddress socket, int destId, TCPMPTNPacket packet, boolean expectReply) {
		AsyncCallback<TCPMPTNPacket> callback = null;
		ByteBuffer buffer = MPTNUtil.createBufferFromTCPMPTNPacket(packet);
		buffer.flip();
		synchronized (this.pendingChanges) {
			SocketChannel channel = activeChannels.get(socket);
			this.pendingChanges.add(new ChangeRequest(channel, SelectionKey.OP_WRITE));
			synchronized (this.outputQueue) {
			
				List<ByteBuffer> buffers = this.outputQueue.get(channel);
				if (buffers == null) {
					buffers = new ArrayList<ByteBuffer> ();
					this.outputQueue.put(channel, buffers);
				}
				this.outputQueue.get(channel).add(buffer);
			}
			
		}
		callback = new AsyncCallback<TCPMPTNPacket>(destId, null);
		nouceCache.put(packet.getNounce(), callback);
		
		this.selector.wakeup();
		
		if (expectReply) {
			while(!callback.ready()) {
				this.selector.wakeup();
			}
			
			return new MPTNPacket(callback.get().getPayload());
		}
		
		return null;
	}


	@Override
	public void run() {
		while (true) {
	        try{
				// Process any pending changes
				synchronized (this.pendingChanges) {
					Iterator<ChangeRequest> changes = this.pendingChanges.iterator();
					while (changes.hasNext()) {
						ChangeRequest change = (ChangeRequest) changes.next();
						SelectionKey key = change.getChannel().keyFor(this.selector);
						key.interestOps(change.getOpt());
					}
					this.pendingChanges.clear();
				}
	            this.selector.select();
	            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
	            while (selectedKeys.hasNext()) {
	            	logger.info("Go into select");
	            	System.out.println("Go into select");
	                SelectionKey key = selectedKeys.next();
	                selectedKeys.remove();

	                if (!key.isValid()) {
	                    continue;
	                }

	                // Check what event is available and deal with it
	                if (key.isAcceptable()) {
	                    accept(key);
	                } else if (key.isReadable()) {
	                    read(key);
	                } else if (key.isWritable()) {
						this.write(key);
					}
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            System.exit(1);
	        }
	    }
	}
	
	private void accept(SelectionKey key) throws IOException {
	    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

	    SocketChannel socketChannel = serverSocketChannel.accept();
	    socketChannel.configureBlocking(false);
	    socketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
	    socketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
	    socketChannel.register(selector, SelectionKey.OP_READ);
	    logger.info("Client is connected");
	}

	private void read(SelectionKey key) throws IOException {
		logger.info("Start to Read");
		System.out.println("Start to Read");
	    SocketChannel socketChannel = (SocketChannel) key.channel();

	    // Clear out our read buffer so it's ready for new data
	    readBuffer.clear();
	    idBytes.clear();
	    nouceBytes.clear();
	    lengthBytes.clear();
	    // Attempt to read off the channel
	    int numRead;
	    try {
	    	int nodeId = 0;
	    	long nouce = 0;
	    	long length = 0;
	    	
	    	if (!outputQueue.containsKey(socketChannel)) {
	    		numRead = socketChannel.read(idBytes);
		        if (numRead == 4) {
		        	idBytes.flip();
		        	nodeId = idBytes.getInt();
		        } else {
		        	logger.error("Broken node id, its length is " + numRead + "rather than 4");
		        	return;
		        }
		        
		        outputQueue.put(socketChannel, new ArrayList<ByteBuffer> ());
	    	} 
	    	
	    	//TODO (Peter Huang) consider big message that need multiple read
	        numRead = socketChannel.read(nouceBytes);
	        if (numRead == 8) {
	        	nouceBytes.flip();
	        	nouce = nouceBytes.getLong();
	        } else {
	        	logger.error("Broken nouce, its length is " + numRead + "rather than 4");
	        	return;
	        }
	        
	        numRead = socketChannel.read(lengthBytes);
	        if (numRead == 4) {
	        	lengthBytes.flip();
	        	length = WKPFUtil.getBigEndianInteger(lengthBytes.array(), 0);
	        } else {
	        	return;
	        }
	        
	        numRead = 0;
	        while (numRead < length) {
	        	numRead += socketChannel.read(readBuffer);
	        }
	        
	        logger.info("Received TCPMPTN Message whose size is" + numRead);
	        readBuffer.flip();
	        
	        TCPMPTNPacket packet = new TCPMPTNPacket(nodeId, nouce, (int)length, readBuffer.array());
	        AsyncCallback<TCPMPTNPacket> callback= nouceCache.get(packet.getNounce());
	        if (callback == null) {
		        readBuffer.flip();
		        executorService.execute(
						new EventHandleThread<TCPMPTNPacket>(TCPMPTNPacket.class, socketChannel.getRemoteAddress(),
								packet, listeners));
	        } else {
	        	callback.setValue(packet);
	        }
	    } catch (IOException e) {
	        key.cancel();
	        socketChannel.close();
	        logger.error("Forceful shutdown channel " + socketChannel.getRemoteAddress().toString());
	        return;
	    }

	    if (numRead == -1) {
	        logger.error("Graceful shutdown channel " + socketChannel.getRemoteAddress().toString());
	        key.channel().close();
	        key.cancel();

	        return;
	    }
	    
	    synchronized (outputQueue) {
		    List<ByteBuffer> buffers = this.outputQueue.get(socketChannel);
			if (buffers == null) {
				buffers = new ArrayList<ByteBuffer> ();
				this.outputQueue.put(socketChannel, buffers);
			}
	    }

	    socketChannel.register(selector, SelectionKey.OP_WRITE);
	}
	
	private void write(SelectionKey key) throws IOException {
		logger.info("Start to Write");
		System.out.println("Start to Write");
		SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (this.outputQueue) {
			List<ByteBuffer> queue = this.outputQueue.get(socketChannel);

			// Write until there's not more data ...
			while (!queue.isEmpty()) {
				ByteBuffer buf = (ByteBuffer) queue.get(0);
				socketChannel.write(buf);
				if (buf.remaining() > 0) {
					// ... or the socket's buffer fills up
					break;
				}
				queue.remove(0);
			}

			if (queue.isEmpty()) {
				// We wrote away all data, so we're no longer interested
				// in writing on this socket. Switch back to waiting for
				// data.
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}
}
