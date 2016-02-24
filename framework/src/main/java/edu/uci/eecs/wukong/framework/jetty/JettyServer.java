package edu.uci.eecs.wukong.framework.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.jetty.rest.SettingAdjusmentService;
public class JettyServer {
	private static Logger logger = LoggerFactory.getLogger(JettyServer.class);
	private Server server;
	private ServletContextHandler context;
	
	public JettyServer() {
        this.context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		this.context.setContextPath("/");
		this.server = new Server(8080);
		this.server.setHandler(context);
	}
	
	public void start() throws Exception{
		init();
		server.setStopAtShutdown(true);
		try {
			server.start();
			server.join();
		} finally {
			server.destroy();
		}
	}
	
	private void init() {
        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                SettingAdjusmentService.class.getCanonicalName());
	}
	
	public void shutdown() {
		try {
			server.stop();
			server.destroy();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Fail to shut down jetty server.");
		}
	}
}
