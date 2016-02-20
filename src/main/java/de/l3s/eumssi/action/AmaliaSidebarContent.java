package de.l3s.eumssi.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.Action;

public class AmaliaSidebarContent {
	
	public int hour = 00;
	public int min = 00;
	public int sec = 00;
//	HttpServletRequest request;
	public String dataContent = null;
	public String content1 = "{\"localisation\": [{ \"sublocalisations\": { \"localisation\": [";
	public String content8 = "]},\"type\": \"text\",\"tcin\": \"00:00:00.0000\",\"tcout\": \"02:00:00.0000\",\"tclevel\": 0}],\"id\": \"text-amalia01\",\"type\": \"text\",\"algorithm\": \"demo-video-generator\",\"processor\": \"Ina Research Department - N. HERVE\", \"processed\": 1421141589288, \"version\": 1}";
	
	public void makeData(String data, String thumbNailUrl) {
		if(thumbNailUrl==null){
			thumbNailUrl="Images/blank_image.png";
		}
		String content2 = "{\"data\": {\"text\": [";
		String content3 = "]},";
		String content4 = "\"tcin\":";
		String content5 = ",\"tcout\":";

		String content6 = ",\"tclevel\": 1}";
		String content7 = ",";
		String dataContentDemo = null;
		String tcin;
		String tcout;

		if (sec == 60) {
			sec = 00;
			sec = sec + 10;
			min = min + 1;
		} else
			sec = sec + 10;
		if (min == 60) {
			sec = 00;
			min = 00;
			hour = hour + 1;
		}
		tcin = new DecimalFormat("00").format(hour) + ":" + new DecimalFormat("00").format(min) + ":"
				+ new DecimalFormat("00").format(sec) + "." + "0000";
		// tcin=hour+":"+min+":"+sec+"."+"0000";
		if (sec < 60)
			sec = sec + 10;
		else {
			sec = 00;
			min = min + 1;
		}
		tcout = new DecimalFormat("00").format(hour) + ":" + new DecimalFormat("00").format(min) + ":"
				+ new DecimalFormat("00").format(sec) + "." + "0000";
		// System.out.println(data);

		if (dataContent != null) {
			dataContentDemo = content7 + content2 + "\"" + data + "\"" + content3 + content4 + "\"" + tcin + "\""
					+ content5 + "\"" + tcout + "\"" + ",\"thumb\"" + ":" + "\"" + thumbNailUrl + "\"" + content6;
			dataContent = dataContent + dataContentDemo;
		} else {
			dataContentDemo = content2 + "\"" + data + "\"" + content3 + content4 + "\"" + tcin + "\"" + content5 + "\""
					+ tcout + "\"" + ",\"thumb\"" + ":" + "\"" + thumbNailUrl + "\"" + content6;
			dataContent = dataContentDemo;
		}

	//	System.out.println(dataContent);
	}
	
	public void writeContent(String jsonFileName,HttpServletRequest request ) throws IOException{
       String mainContent;
		mainContent = content1 + dataContent + content8;
		// File file = new
		// File("G:\\workspace\\eventsense\\WebContent\\scripts\\"+jsonFileName+".json");

		ServletContext context = request.getServletContext();
		String path = context.getRealPath("/");
		// System.out.println(path);

		File file = new File(path + File.separator + "scripts" + File.separator + jsonFileName + ".json");
		System.out.println(file);
	//	System.out.println("Local filename to write: " + file.getAbsolutePath());
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(mainContent);
		bw.close();


	}
/*
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

*/


}
