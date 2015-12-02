package edu.uci.eecs.wukong.framework.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
public class JettyServer {
	private Server server;
	private ServletContextHandler context;
	
	public JettyServer() {
		server = new Server(8080);
		ServletContextHandler context = new ServletContextHandler();
		context.setContextPath("/");
	}
	
	public void start() throws Exception{
		init();
		server.setHandler(context);
		server.setStopAtShutdown(true);
		server.start();
		server.join();
	}
	
	private void init() {
		// Place to initialize servlet
	}
	
	private void addServlet(ServletHolder servlet, String path) {
		context.addServlet(servlet, path);
	}
	
	public void shutdown() throws Exception {
		server.stop();
	}
}
