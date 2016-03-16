package de.l3s.eumssi.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class PersonContentGenerator extends ContentGenerator {
   //get the collection of all person
	DBCollection personCollection = mongo.getCollection("person");
	Map<String, String> personMapQuestion = new HashMap<String, String>();
	Map<String, String> personMapInfo = new HashMap<String, String>();
	
	ArrayList<String> questionableKeyList = new ArrayList<String>();
	ArrayList<String> infoableKeyList = new ArrayList<String>();
	



	public BasicDBObject personObject=new BasicDBObject();
	  
	PersonContentGenerator(BasicDBObject personObjectConstructor){
		 personObject= personObjectConstructor;
			
		 personMapQuestion.put("currency", "What is the name of the currency?");
		 personMapQuestion.put("officialLanguage", "What is the official language spoken here?");
	//	 personMapQuestion.put("languages", "Which language/languages are spoken?");
		 personMapQuestion.put("neighbours", "Which countries are the neighbours?");
		 personMapQuestion.put("timezone", "In which timezone is this city located?");
		 personMapQuestion.put("capital", "What is the name of the capital?");
		 personMapQuestion.put("country", "In which country is this city located?");
		 personMapQuestion.put("adminArea", "In which region is this city located?");
		 personMapQuestion.put("population", "Which is the most populated city in this country?");
		 
		//template for info of persons
	     personMapInfo.put("currency", "The local currency is ");
		 personMapInfo.put("officialLanguage", "The language spoken is ");
		 personMapInfo.put("capital", "The capital is ");
		 personMapInfo.put("country", "This city is located in ");
		 personMapInfo.put("languages", "language spoken ");
		
}
	
	
	@Override
	public String makeDicision() {
		String dicision;
		boolean hasAbstract=false;

		for (Iterator iteratorForKeyIntersection = personObject.keySet()
				.iterator(); iteratorForKeyIntersection.hasNext();) {
			String keyForIntersection = (String) iteratorForKeyIntersection.next();
			if (personMapQuestion.containsKey(keyForIntersection)) {
				questionableKeyList.add(keyForIntersection);
			}
			
			if (personMapInfo.containsKey(keyForIntersection)) {
				infoableKeyList.add(keyForIntersection);
			}
			if(keyForIntersection.equals("abstract"))
				hasAbstract=true;
			
		}
		ArrayList<String> dicisionList=new ArrayList<String>();
		//options for the dicision
		
	
		if(questionableKeyList.size()>0 && personObject.containsKey("longitude"))
			dicisionList.add("question");
		
	
		if(infoableKeyList.size()>0)
			dicisionList.add("info");
		if(hasAbstract==true)
           dicisionList.add("abstract");
		dicisionList.add("wordGraph");
		// take dicision randomly
		if(dicisionList.size()==0)
		   return null;
		Random ran = new Random();
		System.out.println(dicisionList);
		int x = ran.nextInt(dicisionList.size());
		dicision=dicisionList.get(x);
		
		return dicision;
	}

	@Override
	public String questionGenerator() {
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

		 question = "<div><img src=Images" + "//" + "quiz.png><strong>" + personObject.getString("name") + "</strong><br>"
				+ personMapQuestion.get(mainKeyForQuestion) + "<br><input type='radio' name=\'"
				+ mainKeyValue + "\' value=\'" + options.get(0) + "\'>" + options.get(0)
				+ "<br><input type='radio' name=\'" + mainKeyValue + "\' value=\'" + options.get(1) + "\'>"
				+ options.get(1) + "<br><input type='radio' name=\'" + mainKeyValue + "\' value=\'"
				+ options.get(2) + "\'>" + options.get(2) + "<br><input type='radio' name=\'" + mainKeyValue
				+ "\' value=\'" + options.get(3) + "\'>" + options.get(3)
				+ "<br><input type='button'  value='check'></div>";

		
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
	private ArrayList GetPersonFalseAns(String keyName, String keyValue) {
		ArrayList<String> options = new ArrayList<String>();
		BasicDBObject whereQuery = new BasicDBObject();

		whereQuery.put(keyName, java.util.regex.Pattern.compile("."));
		BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put(keyName, 1);

		Random ran = new Random();
		int x = ran.nextInt(440) + 1;
		DBCursor randomPersonCursor = personCollection.find(whereQuery, projectionQuery).limit(3).skip(x);

		// System.out.println("location cursor size:"+locationCursor);
		while (randomPersonCursor.hasNext()) {
			BasicDBObject entity = (BasicDBObject) randomPersonCursor.next();
			String tempValue = (String) entity.get(keyName);
			String[] value = (String[]) tempValue.split("[,]");

			options.add(value[0]);
		}
		options.add(keyValue);
		Collections.shuffle(options);
		return options;
	}

}
