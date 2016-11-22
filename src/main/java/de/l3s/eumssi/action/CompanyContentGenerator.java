package de.l3s.eumssi.action;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import de.l3s.eumssi.dao.MongoDBManager;

/*class for generating question
using some hardcoded question*/
public class CompanyContentGenerator extends ContentGenerator  {
	
	public MongoDBManager 	mongo				= MongoDBManager.getInstance();
    private DBCollection 	companyCollection	= mongo.getCollection("companies");
	Map<String, String> 	companyMapQuestion 	= new HashMap<String, String>();
	Map<String, String> 	companyMapInfo 		= new HashMap<String, String>();
	ArrayList<String> 		questionableKeyList = new ArrayList<String>();
	ArrayList<String> 		infoableKeyList 	= new ArrayList<String>();
	public BasicDBObject 	companyObject		= new BasicDBObject();
	


	
	/**
	 * Instantiates a new company content generator.
	 * list of question for the predicates, that are related to company
	 * 
	 * @param companyObjectConstructor the company object constructor
	 */
	public CompanyContentGenerator(BasicDBObject companyObjectConstructor) {
		companyObject= companyObjectConstructor;		
		/*template for company question*/ 
		companyMapQuestion.put("locationCity","In which city is the company located?");
		companyMapQuestion.put("location","In which city is the company located?");
		companyMapQuestion.put("industry","To which sector is the company belong?");
		companyMapQuestion.put("locationCountry","In which country is the company located?");
		companyMapQuestion.put("revenue","What is the anual revenue of the company?");		
		companyMapQuestion.put("product","What is product that is produced by this company?");
		companyMapQuestion.put("products","What are products that is produced by this company?");
		companyMapQuestion.put("slogan","What is the slogan of the company?");
	}

	
	
	
	
	
	/* (non-Javadoc)
	 * @see de.l3s.eumssi.action.ContentGenerator#makeDicision(java.lang.String)
	 *  this will make decision about, which content to generate
	    Eg: question, Info
	 */
	/*@Override
	public String makeDicision(String infoOrQues) {		 
		String dicision;
		
		iterate over the list of keys of the company object. 
		 * Whic is read from mongoDB
		 for (Iterator<?> iteratorForKeyIntersection = companyObject.keySet()
				.iterator(); iteratorForKeyIntersection.hasNext();) {
			 
			 String keyForIntersection = (String) iteratorForKeyIntersection.next();
			 
			 check the question available or not for the given kye
			  * which key read from the mongoDB
			 if (companyMapQuestion.containsKey(keyForIntersection)) {
				questionableKeyList.add(keyForIntersection);
			}
		}
		 
		ArrayList<String> dicisionList=new ArrayList<String>();			
		
		if(questionableKeyList.size()>0)
			dicisionList.add("question");
		if(dicisionList.size()==0)
		   return null;
		
		 take dicision randomly 
		Random ran = new Random();
		int x = ran.nextInt(dicisionList.size());
		dicision=dicisionList.get(x);
		System.out.println("decision "+dicision);
		return dicision;
	}*/
	
	
	
	
	
	
	/* (non-Javadoc)
	 * @see de.l3s.eumssi.action.ContentGenerator#questionGenerator()
	 * Actual function for generating the html friendly question
	 */
	@Override
	public JSONArray questionGenerator() throws ParseException {
		JSONArray questions=new JSONArray();
		JSONObject questionObj=new JSONObject();
		ArrayList<?> options;
		
		for(int i=0;i<questionableKeyList.size();i++){
		String keyForQuestion = questionableKeyList.get(i);
		String keyValue = (String) companyObject.get(keyForQuestion);
		 options = GetCompanyFalseAns(keyForQuestion, keyValue);

         System.out.println(options);
         String question=companyMapQuestion.get(questionableKeyList.get(i));
			questionObj.put("question",companyMapQuestion.get(questionableKeyList.get(i)));
			questionObj.put("options",options);
			questionObj.put("correct",keyValue);
		    //questions array will go inside content array
			questions.add(questionObj);
		}
		
		return questions;
		
		
		
		/*String question ;
		Random ran = new Random();
		int questionSelectorNumber = ran.nextInt(questionableKeyList.size());
		String mainKeyForQuestion = questionableKeyList.get(questionSelectorNumber);
		String mainKeyValue = (String) companyObject.get(mainKeyForQuestion);
		ArrayList<?> options = GetCompanyFalseAns(mainKeyForQuestion, mainKeyValue);
		
		question = "<div><img src=Images" + "//" + "quiz.png><strong>" + companyObject.getString("name") + "</strong><br>"
				+ companyMapQuestion.get(mainKeyForQuestion) 
				+ "<br><input type='radio' name=\'" + mainKeyValue + "\' value=\'" + options.get(0) + "\'>" + options.get(0)
				+ "<br><input type='radio' name=\'" + mainKeyValue + "\' value=\'" + options.get(1) + "\'>" + options.get(1) 
				+ "<br><input type='radio' name=\'" + mainKeyValue + "\' value=\'" + options.get(2) + "\'>" + options.get(2) 
				+ "<br><input type='radio' name=\'" + mainKeyValue + "\' value=\'" + options.get(3) + "\'>" + options.get(3)
				+ "<br><input type='button' class='btn btn-primary' id='check' value='check' ></div>";		
		 return question;*/
	}

	
	
	
	
	
	/**
	 * Gets the answers to the question.
	 * these correct and false answer getting from mongoDB
	 *
	 * @param keyName the key name to get the false answers
	 * @param keyValue the correct answer
	 * @return the list of answers. which is include correct and false answer.
	 * @throws ParseException the parse exception
	 */
	private JSONArray GetCompanyFalseAns(String keyName, String keyValue) throws ParseException {
		JSONArray  options = new JSONArray ();
		BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(keyName, java.util.regex.Pattern.compile("."));
		BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put(keyName, 1);
        
		DBCursor randomPersonLimitCounterCursor = companyCollection.find(whereQuery, projectionQuery);
		
		Random ran = new Random();
		int x = ran.nextInt(randomPersonLimitCounterCursor.count()-3) + 1;
		DBCursor randomPersonCursor = companyCollection.find(whereQuery, projectionQuery).limit(3).skip(x);

		while (randomPersonCursor.hasNext()) {
			BasicDBObject entity = (BasicDBObject) randomPersonCursor.next();
			String tempValue = (String) entity.get(keyName);
			String[] value = (String[]) tempValue.split("[,]");

			options.add(value[0]);
		}
		
		options.add(keyValue);
		Collections.shuffle(options);
		
		return options;
/*		ArrayList<String> options = new ArrayList<String>();

		BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(keyName, java.util.regex.Pattern.compile("."));
		BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put(keyName, 1);
        
		DBCursor randomPersonLimitCounterCursor = companyCollection.find(whereQuery, projectionQuery);
		
		Random ran = new Random();
		int x = ran.nextInt(randomPersonLimitCounterCursor.count()-3) + 1;
		DBCursor randomPersonCursor = companyCollection.find(whereQuery, projectionQuery).limit(3).skip(x);

		while (randomPersonCursor.hasNext()) {
			BasicDBObject entity = (BasicDBObject) randomPersonCursor.next();
			String tempValue = (String) entity.get(keyName);
			String[] value = (String[]) tempValue.split("[,]");

			options.add(value[0]);
		}
		
		options.add(keyValue);
		Collections.shuffle(options);
		
		return options;*/
	}

		
	
	
	
	
	/*to future use*/
	@Override
	public JSONArray infoGenerator() {
		return null;
	}
	
	/*to future use*/
	@Override
	public String abstractGenerator() {
		// TODO Auto-generated method stub
		return null;
	}






	@Override
	public ArrayList<String> makeDicision() {
		// TODO Auto-generated method stub
		return null;
	}

}
