package com.dc.tes.monitor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.spi.LoggingEvent;

import com.dc.tes.monitor.data.Context;
import com.dc.tes.ui.server.HelperService;

public class MonitorThread extends HttpServlet {

	private static final long serialVersionUID = -1492318318833090342L;
	private static int coreflag = 0;
	private static int senderflag = 0;
	private static int receiverflag = 0;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
		        ServerSocket serverSocket;
		        Socket socket = null;
		        try
		        {   
		            serverSocket = new ServerSocket(4445);
		            while (true) {   
		                socket = serverSocket.accept();   
		                if(coreflag == 0) {
		                	new Thread(new CoreLogSocket(socket)).start(); 
		                	coreflag = 1;
		                }else if(senderflag == 0) {
		                	new Thread(new SenderLogSocket(socket)).start();  
		                	senderflag = 1;
		                }else if(receiverflag == 0) {
		                	new Thread(new ReceiverLogSocket(socket)).start(); 
		                	receiverflag = 1;
		                }
		            }   
		        }
		        catch (Exception e) 
		        {   
		        } 
			}
			
		}).start();

	}

	public class CoreLogSocket implements Runnable {
	      
	     Socket socket;  
	     ObjectInputStream ois;  
	     
	     public CoreLogSocket(Socket socket) {  
	         this.socket = socket;  
	         try {  
	             ois = new ObjectInputStream(new BufferedInputStream(  
	                     socket.getInputStream()));  
	         } catch (Exception e) {  
	         }  
	     }  
	   
	     public void run() {  
	         LoggingEvent event;  
	         try {  
	             if (ois != null) {  
	                 while (true) {  
	                      event = (LoggingEvent) ois.readObject();  
	                      Context.addCoreLog((String)event.getMessage());
	                 }  
	   
	             }  
	   
	         } catch (Exception e) {  
	        	 coreflag = 0;
	   
	         } finally {  
	             if (ois != null) {  
	                 try {  
	                     ois.close(); 
	                 } catch (Exception e) {  
	                 }  
	             }  
	             if (socket != null) {  
	                 try {  
	                     socket.close();  
	                 } catch (IOException ex) {  
	                 }  
	             }  
	             coreflag = 0;
	         }  
	     }  
	}

	public class SenderLogSocket implements Runnable {
	      
	     Socket socket;  
	     ObjectInputStream ois;  
	     
	     public SenderLogSocket(Socket socket) {  
	         this.socket = socket;  
	         try {  
	             ois = new ObjectInputStream(new BufferedInputStream(  
	                     socket.getInputStream()));  
	         } catch (Exception e) {  
	         }  
	     }  
	   
	     public void run() {  
	         LoggingEvent event;  
	         try {  
	             if (ois != null) {  
	                 while (true) {  
	                      event = (LoggingEvent) ois.readObject();  
	                      Context.addSenderLog((String)event.getMessage());
	                 }  
	   
	             }  
	   
	         } catch (Exception e) {  
	        	 senderflag = 0;
	         } finally {  
	             if (ois != null) {  
	                 try {  
	                     ois.close(); 
	                 } catch (Exception e) {  
	                 }  
	             }  
	             if (socket != null) {  
	                 try {  
	                     socket.close();  
	                 } catch (IOException ex) {  
	                 }  
	             }  
	             senderflag = 0;
	         }  
	     }  
	}
	
	public class ReceiverLogSocket implements Runnable {
	      
	     Socket socket;  
	     ObjectInputStream ois;  
	     
	     public ReceiverLogSocket(Socket socket) {  
	         this.socket = socket;  
	         try {  
	             ois = new ObjectInputStream(new BufferedInputStream(  
	                     socket.getInputStream()));  
	         } catch (Exception e) {  
	         }  
	     }  
	   
	     public void run() {  
	         LoggingEvent event;  
	         try {  
	             if (ois != null) {  
	                 while (true) {  
	                      event = (LoggingEvent) ois.readObject();  
	                      Context.addReceiverLog((String)event.getMessage());
	                 }  
	   
	             }  
	   
	         } catch (Exception e) {  
	        	 receiverflag = 0;
	         } finally {  
	             if (ois != null) {  
	                 try {  
	                     ois.close(); 
	                 } catch (Exception e) {  
	                 }  
	             }  
	             if (socket != null) {  
	                 try {  
	                     socket.close();  
	                 } catch (IOException ex) {  
	                 }  
	             }  
	             receiverflag = 0;
	         }  
	     }  
	}

}
