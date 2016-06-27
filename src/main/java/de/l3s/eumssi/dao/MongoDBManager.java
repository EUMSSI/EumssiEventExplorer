/**
 * db manager for mongodb 
 * 
 * gtran@l3s.de
 * Nov 28 2014
 */
package de.l3s.eumssi.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import de.l3s.lemma.lemma;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;



public class MongoDBManager {
	public MongoClient mongoClient = null;
	DB db = null;
	String host = "127.0.0.1";
//	String host = "pharos.l3s.uni-hannover.de";
	//String dbname = "eumssi_db";
	String dbname = "eumssi_secondscreen_db";
//	String collection = "location";
	DBCollection coll = null;
	static MongoDBManager mongo=new MongoDBManager();
	private MongoDBManager() {
		try {
			lemma.init();
			mongoClient = new MongoClient( host , 27017);
			
			db = mongoClient.getDB(dbname);
		//	System.out.println("Connection to mongodb at "  + host  + " is successful.");
			//coll = getCollection();
		} catch (UnknownHostException e) {
			System.err.println("Connection errors");
			e.printStackTrace();
		}
	}
	
	public static MongoDBManager getInstance(){
		return mongo;
	}
	
	public void showCollectionNames() {
		Set<String> colls = db.getCollectionNames();
		for (String s : colls) {
		    System.out.println(s);
		}
	}
	
	public DBCollection getCollection(String collection) {
		return db.getCollection(collection);
	}

	private void test (String collection) {
		DBObject myDoc = getCollection(collection).findOne();
		System.out.println(myDoc);
	}
	

}
