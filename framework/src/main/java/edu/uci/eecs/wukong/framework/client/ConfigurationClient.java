package edu.uci.eecs.wukong.framework.client;

import edu.uci.eecs.wukong.framework.entity.ConfigurationEntity;
import edu.uci.eecs.wukong.framework.util.Configuration;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Consts;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import com.google.gson.Gson;

public class ConfigurationClient {
	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationClient.class);
	private final static Configuration configuration = Configuration.getInstance();
	private static ConfigurationClient configurationClient;
	private static Gson gson = new Gson();
	private PoolingHttpClientConnectionManager connectionManager;
	private CloseableHttpClient client;
	private final static String CONFIG_METHOD = "/config/ConfigService";
	private final static String CONTENT_TYPE_VALUE = "application/json";
	private final static String CONFIG_URL = "http://" + configuration.getMasterAddress() + "/"+ CONFIG_METHOD;

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

	private ConfigurationClient() {
		// Use default connection parameters setting, 20 connections 2 routs per
		// connection.
		connectionManager = new PoolingHttpClientConnectionManager();
		client = HttpClients.custom().setRetryHandler(retryHandler)
				.setConnectionManager(connectionManager).build();
	}

	public static synchronized ConfigurationClient getInstance() {
		if (configurationClient == null) {
			configurationClient = new ConfigurationClient();
			LOGGER.info("Configuration client is initialized in progression server.");
		}

		return configurationClient;
	}
	
	public void sendConfigurationMessage(List<ConfigurationEntity> entities)
			throws ClientProtocolException, IOException {
		send(CONFIG_URL, null, gson.toJson(entities));
	}

	private void send(String url, Map<String, String> paramters, String content)
			throws ClientProtocolException, IOException {
		StringBuffer buffer = new StringBuffer(url);
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		for (Entry<String, String> entry : paramters.entrySet()) {
			 qparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		buffer.append("?");
		buffer.append(URLEncodedUtils.format(qparams, "UTF-8"));
		HttpPost httpPost = new HttpPost(buffer.toString());
		httpPost.setHeader(HTTP.CONTENT_TYPE, CONTENT_TYPE_VALUE);
		StringEntity se = new StringEntity(content, ContentType.create(CONTENT_TYPE_VALUE, Consts.UTF_8));
		httpPost.setEntity(se);
		CloseableHttpResponse response = client.execute(httpPost);
		try {
			StatusLine statusLine = response.getStatusLine();
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				LOGGER.error("Master is not accessible.");
			}
		} finally {
			response.close();
		}
	}
}
