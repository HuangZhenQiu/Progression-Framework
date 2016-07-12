package edu.uci.eecs.wukong.framework.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
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
import edu.uci.eecs.wukong.framework.mptn.TCPMPTNPackage;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;

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
	private Map<SocketAddress, SocketChannel> activeChannels;
	private Map<SocketChannel, List<ByteBuffer>> outputQueue;
	private Map<Long, AsyncCallback<TCPMPTNPackage>> nouceCache;
	private List<MPTNMessageListener<TCPMPTNPackage>> listeners;
	private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	private List<ChangeRequest> pendingChanges;
	private Selector selector;
	private ServerSocketChannel serverChannel;
	private Long currentNouce;

	public NIOTCPServer(int port) {
		try {
			outputQueue = new HashMap<SocketChannel, List<ByteBuffer>> ();
			activeChannels = new HashMap<SocketAddress, SocketChannel> ();
			nouceCache = new HashMap<Long, AsyncCallback<TCPMPTNPackage>> ();
			pendingChanges = new ArrayList<ChangeRequest>();
			listeners = new ArrayList<MPTNMessageListener<TCPMPTNPackage>> ();
			selector = SelectorProvider.provider().openSelector();
		    serverChannel = ServerSocketChannel.open();
		    serverChannel.configureBlocking(false);
	
		    InetSocketAddress isa = new InetSocketAddress(port);
		    serverChannel.socket().bind(isa);
		    serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			logger.error("Fail to open tcp server socket for progression server: " + e.toString());
			System.exit(-1);
		}
	}
	
	public void addMPTNMessageListener(MPTNMessageListener<TCPMPTNPackage> listener) {
		this.listeners.add(listener);
	}
	
	public void Send(SocketAddress socket, int destId, ByteBuffer buffer, boolean expectReply) {
		AsyncCallback<TCPMPTNPackage> callback;
		synchronized (this.pendingChanges) {
			SocketChannel channel = activeChannels.get(socket);
			this.pendingChanges.add(new ChangeRequest(channel, SelectionKey.OP_WRITE));
			synchronized (this.outputQueue) {
				
				currentNouce ++;
				callback = new AsyncCallback<TCPMPTNPackage>(destId, null);
				nouceCache.put(currentNouce, callback);
				List<ByteBuffer> buffers = this.outputQueue.get(socket);
				if (buffers == null) {
					buffers = new ArrayList<ByteBuffer> ();
					this.outputQueue.put(channel, buffers);
				}
				this.outputQueue.get(socket).add(buffer);

			}
		}
		this.selector.wakeup();
		
		if (expectReply) {
			while(!callback.ready()) {
				this.selector.wakeup();
			}
		}
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

	    System.out.println("Client is connected");
	}

	private void read(SelectionKey key) throws IOException {
	    SocketChannel socketChannel = (SocketChannel) key.channel();

	    // Clear out our read buffer so it's ready for new data
	    readBuffer.clear();

	    // Attempt to read off the channel
	    int numRead;
	    try {
	    	//TODO (Peter Huang) consider big message that need multiple read
	        numRead = socketChannel.read(readBuffer);
	        executorService.execute(
					new EventHandleThread<TCPMPTNPackage>(TCPMPTNPackage.class, socketChannel.getRemoteAddress(),
							MPTNUtil.deepCopy(readBuffer), listeners));
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

	    socketChannel.register(selector, SelectionKey.OP_WRITE);
	}
	
	private void write(SelectionKey key) throws IOException {
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
