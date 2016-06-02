package edu.uci.eecs.wukong.framework.monitor;

import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.MonitorDataModel;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class MongoDBMonitorService extends MonitorService {
	private static Logger logger = LoggerFactory.getLogger(MongoDBMonitorService.class);
	private static Configuration configuration = Configuration.getInstance();
	private static MongoDBMonitorService service;
	private static Gson gson = new Gson();
	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Document> collection;
	private List<MonitorDataModel> buffer;
	
	private MongoDBMonitorService() {
		
		if (configuration.getMonitorMongoURL() != null &&
				configuration.getMonitorMongoDataBase() != null &&
				configuration.getMonitorMongoCollection() != null) {
			client = new MongoClient(configuration.getMonitorMongoURL());
			database = client.getDatabase(configuration.getMonitorMongoDataBase());
			collection = database.getCollection(configuration.getMonitorMongoCollection());
			buffer = new ArrayList<MonitorDataModel>();
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
	public void send(MonitorDataModel model) {
		if (model != null) {
			synchronized (buffer) {
				buffer.add(model);
			}
		}
	}
	
	@Override
	public void bulkPush() {
		synchronized (buffer) {
			if (buffer.size() > 0) {
				List<WriteModel<Document>> documents = new ArrayList<WriteModel<Document>>();
				for (MonitorDataModel model : buffer) {
					documents.add(new InsertOneModel<Document>(Document.parse(gson.toJson(model))));
				}
				collection.bulkWrite(documents);
			}
			
			logger.info("Sending " + buffer.size() + " monitored sensor data to mongoDB by bulk writing");
			buffer.clear();
		}
	}

	@Override
	public void close() {
		client.close();
	}
}
