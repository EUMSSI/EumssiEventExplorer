package de.l3s.eumssi.action;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometProcessor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import de.l3s.eumssi.dao.MongoDBManager;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Helper class to implement Comet functionality.
 */
@WebServlet("/chat1")
public class chat1
    extends HttpServlet implements CometProcessor {

    protected ListMultimap<String,HttpServletResponse> connections =  ArrayListMultimap.create();
    protected MessageSender messageSender = null;
 //   public MongoDBManager mongo; 
    
    public void init() throws ServletException {
    //	System.out.println("in the init");
        messageSender = new MessageSender();
        Thread messageSenderThread = 
            new Thread(messageSender, "MessageSender[" + getServletContext().getContextPath() + "]");
        messageSenderThread.setDaemon(true);
        messageSenderThread.start();
     //   mongo= new MongoDBManager();
    }

    public void destroy() {
 //   	System.out.println("distroyed");
        connections.clear();
        messageSender.stop();
        messageSender = null;
    }

    /**
     * Process the given Comet event.
     * 
     * @param event The Comet event that will be processed
     * @throws IOException
     * @throws ServletException
     */
    public void event(CometEvent event)
        throws IOException, ServletException {
        
        // Note: There should really be two servlets in this example, to avoid
        // mixing Comet stuff with regular connection processing
        HttpServletRequest request = event.getHttpServletRequest();
        HttpServletResponse response = event.getHttpServletResponse();
        
        if (event.getEventType() == CometEvent.EventType.BEGIN) {
            try {
				begin(event, request, response);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else if (event.getEventType() == CometEvent.EventType.ERROR) {
            error(event, request, response);
        } else if (event.getEventType() == CometEvent.EventType.END) {
            end(event, request, response);
        } else if (event.getEventType() == CometEvent.EventType.READ) {
            read(event, request, response);
        }
    }

    protected void begin(CometEvent event, HttpServletRequest request, HttpServletResponse response) {
        log("Begin for session: " + request.getSession(true).getId());
  
     //   System.out.println("Action param: " + request.getParameter("entityName"));
        synchronized(connections) {
        	String user=(String) request.getSession().getAttribute("userId");
        	if(request.getSession().getAttribute("userId")!=null && (String)request.getParameter("requestType")!=null){
           	 System.out.println("second screen userId"+ (String) request.getSession().getAttribute("userId"));
        		connections.put((String) request.getSession().getAttribute("userId"),response);
           	 System.out.println("request type: " + request.getParameter("requestType")); 
               System.out.println("connections: " + connections);
               }
        }
        if(request.getParameter("content")!=null){
        	System.out.println("first screen userId: " +(String) request.getSession().getAttribute("userId"));
        //	Second_screen_contentAction second_screen_content=new Second_screen_contentAction();
        	 String content;
			try {
				content = request.getParameter("content");
				// System.out.println("content"+content); 
			        messageSender.send((String) request.getSession().getAttribute("userId"),content);
			} catch (Exception e) {
		//		System.out.println("error happened");
				e.printStackTrace();
			}
        	
       
        }
    }
    
    protected void end(CometEvent event, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        log("End for session: " + request.getSession(true).getId());
        synchronized(connections) {
            connections.remove((String) request.getSession().getAttribute("userId"),response);
        }
  
    //    System.out.println("in the end ..");
        
        event.close();
        
    }
    
    protected void error(CometEvent event, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        log("Error for session: " + request.getSession(true).getId());
  //      System.out.println("in the error ..");
        synchronized(connections) {
            connections.remove((String) request.getSession().getAttribute("userId"),response);
        }
        event.close();
    }
    
    protected void read(CometEvent event, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        log("Read for session: " + request.getSession(true).getId());
     //   System.out.println("in the read ..");
        /*
        InputStream is = request.getInputStream();
        byte[] buf = new byte[512];
        while (is.available() > 0) {
            log("Available: " + is.available());
            int n = is.read(buf);
            if (n > 0) {
                log("Read " + n + " bytes: " + new String(buf, 0, n) 
                        + " for session: " + request.getSession(true).getId());
            } else if (n < 0) {
                log("End of file: " + n);
                end(event, request, response);
                return;
            }
        }*/
    }
 

    /**
     * Poller class.
     */
    public class MessageSender implements Runnable {

        protected boolean running = true;
        protected Map<String,String> messages = new HashMap<String,String>();
      //  protected ArrayList<String> messages = new ArrayList<String>();
        int counter=0;
        public MessageSender() {
        }
        
        public void stop() {
            running = false;
        }

        /**
         * Add specified socket and associated pool to the poller. The socket will
         * be added to a temporary array, and polled first after a maximum amount
         * of time equal to pollTime (in most cases, latency will be much lower,
         * however).
         *
         * @param socket to add to the poller
         */
        public void send(String user, String message) {
            synchronized (messages) {
                messages.put(user,message);
                messages.notify();
                counter=counter+1;
       //         System.out.println("counter "+counter);
        //        if(counter==2){
           //     	System.out.println("got it");
        //        }
            }
        }

        /**
         * The background thread that listens for incoming TCP/IP connections and
         * hands them off to an appropriate processor.
         */
        public void run() {

            // Loop until we receive a shutdown command
            while (running) {
                // Loop if endpoint is paused
         
                if (messages.size() == 0) {
                    try {
                        synchronized (messages) {
                            messages.wait();
                        }
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
                

        //        synchronized (connections) {
                 //   String[] pendingMessages = null;
       //             synchronized (messages) {
                   //     pendingMessages = messages.toArray(new String[0]);
                   //     messages.clear();
                  //  }
                    	System.out.println("messages"+messages);
                    	for (String userId : messages.keySet()) {
                    	  try {
                        	System.out.println("connection "+connections.get(userId)+" sending " + "connection name"+ connections.get(userId));
                          if(connections.get(userId)!=null){
                        	  List<HttpServletResponse> rsps=connections.get(userId);
                        	for(int i=0; i<rsps.size();i++)  {
                        		PrintWriter writer =rsps.get(i).getWriter();
                                String message=messages.get(userId);   
                           	try{
                               writer.println(message);
                              
                               System.out.println(writer);
                               writer.flush();
                               writer.close();
                           	}
                           
                           	catch(NullPointerException e){
                           		System.out.println("connections now"+connections);
                           		connections.remove(userId,rsps.get(i));
                           		System.out.println("connections after remove"+connections);
                           		System.out.println(e.getMessage());
                           	}
                            
                        	} 
                        	messages.remove(userId);
                        	
                    	  }
                          else{
                        	  messages.remove(userId);
                        	  continue;
                          }
                             
                           
                        } catch (IOException e) {
                            log("IOExeption sending message", e);
                        }
                    	}
              //  }
              //  }

            }

        }

    }




}
