package de.l3s.eumssi.action;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import de.l3s.eumssi.dao.MongoDBManager;

public class PersonContentGenerator extends ContentGenerator {
   //get the collection of all person
	public MongoDBManager mongo=MongoDBManager.getInstance();
    private DBCollection personCollection= mongo.getCollection("person");
	Map<String, String> personMapQuestion = new HashMap<String, String>();
	Map<String, String> personMapInfo = new HashMap<String, String>();
	
	ArrayList<String> questionableKeyList = new ArrayList<String>();
	ArrayList<String> infoableKeyList = new ArrayList<String>();
 //   private  MongoDBManager mongo;	



	public BasicDBObject personObject=new BasicDBObject();
	  
	PersonContentGenerator(BasicDBObject personObjectConstructor){
		 personObject= personObjectConstructor;
	//	 mongo=mongoClient;
	//	 personCollection = mongo.getCollection("person");
		/*	
		 //template for person question 
		 personMapQuestion.put("birthPlace","What is this person's birthplace?");
		 personMapQuestion.put("almaMater","Which university or college did this person attend?");
		 personMapQuestion.put("birthDate","In which year was this person born?");
		 personMapQuestion.put("deathDate","In which year was this person died?");
		 */
		 personMapQuestion.put("deathPlace","Where this person died?");
		 
		 
		//template for info of persons
		 personMapInfo.put("birthPlace","City of birth: ");
		 personMapInfo.put("almaMater","College attended: ");
		 personMapInfo.put("birthdate","Date of birth: " );
		 personMapInfo.put("spouse","Spouse: ");
		
}
	
	
	@Override
	public String makeDicision(String infoOrQues) {
		String dicision;
		boolean hasAbstract=false;

		for (Iterator iteratorForKeyIntersection = personObject.keySet()
				.iterator(); iteratorForKeyIntersection.hasNext();) {
			String keyForIntersection = (String) iteratorForKeyIntersection.next();
			if(infoOrQues.equals("question") || infoOrQues.equals("both")){
			 if (personMapQuestion.containsKey(keyForIntersection)) {
				questionableKeyList.add(keyForIntersection);
			}
			}
			if(infoOrQues.equals("info") || infoOrQues.equals("both")){
			if (personMapInfo.containsKey(keyForIntersection)) {
				infoableKeyList.add(keyForIntersection);
			}
			}
			if(keyForIntersection.equals("abstract"))
				hasAbstract=true;
			
		}
		
		ArrayList<String> dicisionList=new ArrayList<String>();
		//options for the dicision
		
	
		if(questionableKeyList.size()>0)
			dicisionList.add("question");
		
	
		if(infoableKeyList.size()>0)
			dicisionList.add("info");
		if(hasAbstract==true)
           dicisionList.add("abstract");
	//	dicisionList.add("wordGraph");
		
		// take dicision randomly
		if(dicisionList.size()==0)
		   return null;
		Random ran = new Random();

		int x = ran.nextInt(dicisionList.size());
		dicision=dicisionList.get(x);
		System.out.println("decision "+dicision);
		return dicision;
	}

	@Override
	public String questionGenerator() throws ParseException {
		String correctAns = null;
		String correctOrderAns;
		String type=(String) personObject.get("type");
		ArrayList options;
		String question ;
		Random ran = new Random();
		int questionSelectorNumber = ran.nextInt(questionableKeyList.size());
		String mainKeyForQuestion = questionableKeyList.get(questionSelectorNumber);
		String mainKeyValue = (String) personObject.get(mainKeyForQuestion);
		 options = GetPersonFalseAns(mainKeyForQuestion, mainKeyValue);
         if(mainKeyForQuestion.equals("birthDate") || mainKeyForQuestion.equals("deathDate")){
        	 DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
     	    DateFormat targetFormat = new SimpleDateFormat("d MMMM yyyy");
     	   Date keyValueDateType=df.parse(mainKeyValue);
     	  mainKeyValue=targetFormat.format(keyValueDateType);
     	    
         }
         System.out.println(options);
		 question = "<div><img src=Images" + "//" + "quiz.png><strong>" + personObject.getString("name") + "</strong><br>"
				+ personMapQuestion.get(mainKeyForQuestion) + "<br><input type='radio' name=\'"
				+ mainKeyValue + "\' value=\'" + options.get(0) + "\'>" + options.get(0)
				+ "<br><input type='radio' name=\'" + mainKeyValue + "\' value=\'" + options.get(1) + "\'>"
				+ options.get(1) + "<br><input type='radio' name=\'" + mainKeyValue + "\' value=\'"
				+ options.get(2) + "\'>" + options.get(2) + "<br><input type='radio' name=\'" + mainKeyValue
				+ "\' value=\'" + options.get(3) + "\'>" + options.get(3)
				+ "<br><input type='button' class='btn btn-primary' id='check' value='check' ></div>";

		
		return question;
	}

	@Override
	public String infoGenerator() {
		Random ran = new Random();
		int infoSelectorNumber = ran.nextInt(infoableKeyList.size());
		String mainKeyForInfo = infoableKeyList.get(infoSelectorNumber);
		String mainKeyValue = (String) personObject.get(mainKeyForInfo);
		String info;
        info = "<img src=Images" + "/" + "Info.png><strong>" + personObject.getString("name") + "</strong>" + "<br>"
				+ personMapInfo.get(mainKeyForInfo) + mainKeyValue;
		
		return info;
	}

	@Override
	public String abstractGenerator() {
		   if ((String) personObject.get("abstract") == null)
				return null;
		   else{
		   String abs = "<strong>" + personObject.getString("name") + "</strong>" + "<br>" + (String) personObject.get("abstract");
			abs = abs.replaceAll("\\(.+?\\)\\s*", "");
		    return abs;
		   }
	}
	   public String wordGraphGenerator(String entityName){
		   entityName=entityName.replaceAll("_", " ");
		   String entityNameForId=entityName.replace(" ", "-");
		   String wordgraph= "<div id='my-genericgraph-"+entityNameForId+"' class='genericgraph'><script type='text/javascript'>"
		   +"EUMSSI.Manager.addWidget(new AjaxSolr.GenericGraphWidget({id: 'my-genericgraph-"+entityNameForId+"\',target: '#my-genericgraph-"+entityNameForId+"'})); EUMSSI.Manager.doRequest(\'"+entityName+"\'); </script></div>" ;
		   return wordgraph;
	   }
	private ArrayList GetPersonFalseAns(String keyName, String keyValue) throws ParseException {
		ArrayList<String> options = new ArrayList<String>();
		if(keyName.equals("birthDate") || keyName.equals("deathDate")){
			
	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    DateFormat targetFormat = new SimpleDateFormat("d MMMM yyyy");
		String[] birthDatesSplit=keyValue.split("-");
		int birthYear=Integer.valueOf(birthDatesSplit[0]);
		int falseBirthYear1=birthYear+2;
		int falseBirthYear2=birthYear+3;
		int falseBirthYear3=birthYear+1;
		String falseBirthDate1=falseBirthYear1+"-"+birthDatesSplit[1]+"-"+birthDatesSplit[2];
		String falseBirthDate2=falseBirthYear2+"-"+birthDatesSplit[1]+"-"+birthDatesSplit[2];
		String falseBirthDate3=falseBirthYear3+"-"+birthDatesSplit[1]+"-"+birthDatesSplit[2];
		Date falseBirthDate1DateType=df.parse(falseBirthDate1);
		falseBirthDate1=targetFormat.format(falseBirthDate1DateType);
		Date falseBirthDate2DateType=df.parse(falseBirthDate2);
		falseBirthDate2=targetFormat.format(falseBirthDate2DateType);
		Date falseBirthDate3DateType=df.parse(falseBirthDate3);
		falseBirthDate3=targetFormat.format(falseBirthDate3DateType);
		Date keyValueDateType=df.parse(keyValue);
		keyValue=targetFormat.format(keyValueDateType);
		options.add(falseBirthDate1);
		options.add(falseBirthDate2);
		options.add(falseBirthDate3);
		options.add(keyValue);
		}
		else{
		BasicDBObject whereQuery = new BasicDBObject();
        whereQuery.put(keyName, java.util.regex.Pattern.compile("."));
		BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put(keyName, 1);
        
		DBCursor randomPersonLimitCounterCursor = personCollection.find(whereQuery, projectionQuery);
		
		Random ran = new Random();
		int x = ran.nextInt(randomPersonLimitCounterCursor.count()-3) + 1;
		DBCursor randomPersonCursor = personCollection.find(whereQuery, projectionQuery).limit(3).skip(x);

		while (randomPersonCursor.hasNext()) {
			BasicDBObject entity = (BasicDBObject) randomPersonCursor.next();
			String tempValue = (String) entity.get(keyName);
			String[] value = (String[]) tempValue.split("[,]");

			options.add(value[0]);
		}
		
		options.add(keyValue);
		}
		Collections.shuffle(options);
		
		return options;
	}

}
