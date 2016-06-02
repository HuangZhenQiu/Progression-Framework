package edu.uci.eecs.wukong.framework.service;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Consts;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHttpService {
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractHttpService.class);
	private final static String CONTENT_TYPE_VALUE = "application/json";
	private String name;
	private String ip;
	private String port;
	private String method;
	private PoolingHttpClientConnectionManager connectionManager;
	private CloseableHttpClient client;
	protected String CONFIG_URL;
	
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
	
	public AbstractHttpService(String name, String ip, String port, String method) {
		// Use default connection parameters setting, 20 connections 2 routs per
		// connection.
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.method = method;
		this.CONFIG_URL = "http://" + ip
				+ ":" + port + "/"+ method;
		this.connectionManager = new PoolingHttpClientConnectionManager();
		
		LOGGER.info("Created Service with URL: " + this.CONFIG_URL);
		this.client = HttpClients.custom().setRetryHandler(retryHandler)
				.setConnectionManager(connectionManager).build();
	}
	
	protected String send(HttpRequestBase method, String content) {
		method.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_VALUE);
		if (content != null) {
			if (method instanceof HttpPost) {
				HttpPost post = (HttpPost) method;
				StringEntity se = new StringEntity(content, ContentType.create(CONTENT_TYPE_VALUE, Consts.UTF_8));
				post.setEntity(se);
			}
		}
		CloseableHttpResponse response = null;
		try {
			response = client.execute(method);
			HttpEntity entity = response.getEntity();
			StatusLine statusLine = response.getStatusLine();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(entity, "UTF-8");
			}
		} catch (Exception e) {
			LOGGER.error("Can't talk to service " + name + " because of error:" + e.toString());
		} finally {
			try {
				response.close();
			} catch (Exception e) {
				LOGGER.error("Can't close response");
			}
		}
		
		return null;
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
