package edu.uci.eecs.wukong.framework.client;

import edu.uci.eecs.wukong.framework.manager.ConfigurationManager.ConfigurationType;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Consts;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.google.gson.Gson;

public class ConfigurationClient {
	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationClient.class);
	private final static String CONTENT_TYPE_VALUE = "application/json";
	private String name;
	private String ip;
	private String port;
	private String method;
	private PoolingHttpClientConnectionManager connectionManager;
	private CloseableHttpClient client;
	private String CONFIG_URL;

	private static HttpRequestRetryHandler retryHandler = new HttpRequestRetryHandler() {
		public boolean retryRequest(
	            IOException exception,
	            int executionCount,
	            HttpContext context) {
	        if (executionCount >= 5) {
	            // Do not retry if over max retry count
	            return false;
	        }
	        if (exception instanceof InterruptedIOException) {
	            // Timeout
	            return false;
	        }
	        if (exception instanceof UnknownHostException) {
	            // Unknown host
	            return false;
	        }
	        if (exception instanceof ConnectTimeoutException) {
	            // Connection refused
	            return false;
	        }
	        if (exception instanceof SSLException) {
	            // SSL handshake exception
	            return false;
	        }
	        HttpClientContext clientContext = HttpClientContext.adapt(context);
	        HttpRequest request = clientContext.getRequest();
	        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
	        if (idempotent) {
	            // Retry if the request is considered idempotent
	            return true;
	        }
	        return false;
	    }
	};

	public ConfigurationClient(String name, String ip, String port, String method) {
		// Use default connection parameters setting, 20 connections 2 routs per
		// connection.
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.method = method;
		this.CONFIG_URL = "http://" + ip
				+ ":" + port + "/"+ method;
		this.connectionManager = new PoolingHttpClientConnectionManager();
		this.client = HttpClients.custom().setRetryHandler(retryHandler)
				.setConnectionManager(connectionManager).build();
	}
	
	public void sendTestMessage()
			throws ClientProtocolException, IOException {
		sendConfigurationMessage(ConfigurationType.POST, "TEST MESSAGE");
	}
	
	public void sendConfigurationMessage(ConfigurationType type, String content)
			throws ClientProtocolException, IOException {
		LOGGER.info("Send out configuration report: " + content);
		switch (type) {
			case POST:
				send(new HttpPost(CONFIG_URL), content);
			case PUT:
				send(new HttpPut(CONFIG_URL), content);
				
		}
		
	}

	private void send(HttpEntityEnclosingRequestBase method, String content) {
		method.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_VALUE);
		StringEntity se = new StringEntity(content, ContentType.create(CONTENT_TYPE_VALUE, Consts.UTF_8));
		method.setEntity(se);
		CloseableHttpResponse response = null;
		try {
			response = client.execute(method);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				LOGGER.error("Master is not accessible.");
			}
		} catch (Exception e) {
			LOGGER.error("Can't open configuration client " + name);
		} finally {
			try {
				response.close();
			} catch (Exception e) {
				LOGGER.error("Can't close response");
			}
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}
