package edu.uci.eecs.wukong.framework.monitor;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.MonitorDataModel;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class MongoDBMonitorService extends TimerTask implements MonitorService {
	private static Logger logger = LoggerFactory.getLogger(MongoDBMonitorService.class);
	private static Configuration configuration = Configuration.getInstance();
	private static MongoDBMonitorService service;
	private static Gson gson = new Gson();
	private static long BUFFER_SEND_INTERVAL = 1000 * 30;
	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection collection;
	private List<MonitorDataModel> buffer;
	private Timer timer;
	
	private MongoDBMonitorService() {
		
		if (configuration.getMonitorMongoURL() != null &&
				configuration.getMonitorMongoDataBase() != null &&
				configuration.getMonitorMongoCollection() != null) {
			client = new MongoClient(configuration.getMonitorMongoURL());
			database = client.getDatabase(configuration.getMonitorMongoDataBase());
			collection = database.getCollection(configuration.getMonitorMongoCollection());
			buffer = new ArrayList<MonitorDataModel>();
			timer = new Timer();
		} else {
			logger.error("Fail to initialize monitoring service, because of missing mongoDB connection information" 
					+ " mongoURL = " + configuration.getMonitorMongoURL()
					+ " mongoDatbase = " + configuration.getMonitorMongoDataBase()
					+ " mongoCollection = " + configuration.getMonitorMongoCollection());
			System.exit(-1);
		}
	}
	
	public static synchronized MongoDBMonitorService getInstance() {
		if (service == null) {
			service = new MongoDBMonitorService();
		}
		
		return service;
	}

	@Override
	public void init() {
		timer.schedule(this, BUFFER_SEND_INTERVAL);
	}

	@Override
	public void send(MonitorDataModel model) {
		if (model != null) {
			synchronized (buffer) {
				buffer.add(model);
			}
		}
	}

	@Override
	public void close() {
		timer.cancel();
	}

	@Override
	public void run() {
		synchronized (buffer) {
			if (buffer.size() > 0) {
				List<Document> documents = new ArrayList<Document>();
				for (MonitorDataModel model : buffer) {
					documents.add(Document.parse(gson.toJson(model)));
				}
				collection.bulkWrite(documents);
				buffer.clear();
			}
		}
		
	}
}
