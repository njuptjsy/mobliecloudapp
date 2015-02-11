package com.njuptjsy.cloudclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class SendEmail extends javax.mail.Authenticator implements Runnable
{
	private String strUser;
	private String strPwd;
	public String str_curDate = "";
	public String WifiInformation="";
	public String mac_mobile = "";
	public int upload_max=20;
	public static String[] myFileList;
	public String file_of_File_name_list="Problem.txt";// a file to save file name
	public String LogFile_name="";
	public String filename = "Problem.txt";
	public File File_up_gzip;
	boolean flag = false;
	public  Handler _handler;
	public Context context;
	public static boolean state = false;
	private String callfunction;
	private String problemString;
	
	public static SendEmail instance =null;
	
	public static SendEmail getInstance()
	{
		synchronized (SendEmail.class)
		{
			if(instance == null)
			{
				instance = new SendEmail("njuptjsy@163.com","916358");
			}
		}
		return instance;
	}

	private SendEmail(String user, String password) 
	{
		this.strUser = user;
		this.strPwd = password;
	}


	protected PasswordAuthentication getPasswordAuthentication() 
	{
		return new PasswordAuthentication(strUser, strPwd);
	}

	

	@SuppressLint("SdCardPath")
	public void  run()
	{
		state = true;

		if (ping()==false)
		{
			flag = false;
			//info();
			state = false;
			return;
		}
		else
		{						
			systemTime();
			stringToFile(problemString);
			send_mail_file("13073507@njupt.edu.cn","njuptjsy@163.com","smtp.163.com","njuptjsy","916358","CloudClientFaceback_"+mac_mobile+"_"+str_curDate,
					"","/data/data/com.njuptjsy.cloudclient/"+filename);
			flag = true;
			//info();
		}
		state = false;
	}

	public void send_mail_file(String str_to_mail,String str_from_mail,String str_smtp,String str_user,String str_pass,String str_title,String str_body,String str_file_path)
	{
		Log.v("jsy","send_mail_file");

		String host = str_smtp;   //The sender use email E-mail server send email
		String from = str_from_mail;    //Email origin (the sender email address)
		String to 	= str_to_mail;   //Email destination (the recipient mailbox)
		Log.v("jsy",str_smtp);
		Log.v("jsy",str_from_mail);
		Log.v("jsy",str_to_mail);

		// add handlers for main MIME types
		MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);
		Properties props = System.getProperties();// Get system properties

		props.put("mail.smtp.host", host);// Setup mail server

		props.put("mail.smtp.auth", "true"); //This can be verified

		SendEmail myauth = new SendEmail(str_user, str_pass);// Get session

		Session session = Session.getDefaultInstance(props, myauth);

		MimeMessage message = new MimeMessage(session); // Define message

		try 
		{
			message.setFrom(new InternetAddress(from)); // Set the from address
		} 
		catch (AddressException e)
		{
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}



		try 
		{
			message.addRecipient(javax.mail.Message.RecipientType.TO,new InternetAddress(to));// Set the to address
		} 
		catch (AddressException e) 
		{
			e.printStackTrace();
		}
		catch (MessagingException e)
		{
			e.printStackTrace();
		}



		try 
		{
			message.setSubject(str_title);// Set the subject
		}
		catch (MessagingException e) 
		{
			e.printStackTrace();
		}



		try 
		{
			message.setText(str_body);// Set the content
		} 
		catch (MessagingException e) 
		{
			e.printStackTrace();
		}



		MimeBodyPart attachPart = new MimeBodyPart(); 
		FileDataSource fds = new FileDataSource(str_file_path); //Open the file you wish to send
		try {
			attachPart.setDataHandler(new DataHandler(fds));
		} catch (MessagingException e) {
			e.printStackTrace();
		} 
		try {
			attachPart.setFileName(fds.getName());
		} catch (MessagingException e) {
			e.printStackTrace();
		} 


		MimeMultipart allMultipart = new MimeMultipart("mixed"); //accessory
		try {
			allMultipart.addBodyPart(attachPart);//add 
		} catch (MessagingException e) {
			e.printStackTrace();
		} 
		try {
			message.setContent(allMultipart);
		} catch (MessagingException e) {
			e.printStackTrace();
		} 
		try {
			message.saveChanges();
		} catch (MessagingException e) {
			e.printStackTrace();
		} 


		try {
			Transport.send(message);//begin sending
		} catch (MessagingException e) {
			e.printStackTrace();
		}        

	}

	public void systemTime()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");  
		//SimpleDateFormat formatter_file = new SimpleDateFormat("yyyyMMdd_HHmmss");   
		Date curDate = new Date(System.currentTimeMillis());//get current time
		str_curDate = formatter.format(curDate); 

	}

	public String getMacAddress(Context context)
	{
		this.context = context;
		WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);  
		WifiInfo info = wifiMan.getConnectionInfo(); 
		mac_mobile = info.getMacAddress();// telephone mac address
		return mac_mobile;
	}

	@SuppressLint("SdCardPath")
	public void FileCompress(String file,String macAddress)
	{  
		/*compress file*/
		String tag="FileCompress()";
		FileInputStream inStream=null;
		FileOutputStream outStream=null;
		GZIPOutputStream gos=null;
		try
		{
			File File_up = new File("/data/data/com.njuptjsy.cloudclient/",file);
			if(!File_up.exists())
			{

				return;
			}
			inStream = new FileInputStream(File_up);
			Log.v(tag,"bbbbbbbbb");
			filename = macAddress+file+".gz";
			File_up_gzip = new File("/data/data/com.njuptjsy.cloudclient/",filename);
			Log.v(tag,filename);
			String path = File_up_gzip.getAbsolutePath();//absolute path
			String path1 = File_up_gzip.getPath();//relative path
			Log.v(tag,path);
			Log.v(tag,path1);
			outStream = new FileOutputStream(File_up_gzip);
			gos = new GZIPOutputStream(outStream);  
			int count;  
			byte data[] = new byte[1024];  
			while ((count = inStream.read(data, 0, 1024)) != -1) {  
				gos.write(data, 0, count);  
			}  
			gos.finish();  
			gos.flush();  
			Log.v("jsy","=====compress finish=====");

		}catch(IOException e) {
			e.printStackTrace();
			Log.v("jsy","=====compress error=====");

			//log_Function.log(enumLog.Logv,tag, "compress file error!"+e);   
		}finally{
			if(outStream!=null)
			{
				try {
					outStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(inStream!=null)
			{
				try {
					inStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(gos!=null)
			{
				try {
					gos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Log.v("jsy","Email has send");

	}  

	public boolean ping()
	{
		/*function ：ping IP*/
		String result = null;
		String tag="ping ";
		try {
			String ip = "www.baidu.com";
			Process p = Runtime.getRuntime().exec("ping -c 3 -w 4 " + ip);//ping 3 times
			// PING status
			int status = p.waitFor();
			if (status == 0) 
			{
				result = "successful~";
				return true;
			} 
			else 
			{
				result = "failed~ cannot reach the IP address";
			}
		} catch (IOException e) 
		{
			result = "failed~ IOException";
		} catch (InterruptedException e) 
		{
			result = "failed~ InterruptedException";
		} finally 
		{
			//  log_Function.log(enumLog.Logv,tag, result);
		}
		return false;
	}

	public void getData(String moblieMac,String problemString )
	{
		this.mac_mobile = moblieMac;
		this.problemString = problemString;	
	}
	
	private void stringToFile (String problem)
	{
		FileWriter writer;
		try {
			File problemFile = new File("/data/data/com.njuptjsy.cloudclient/"+filename);
			if (!problemFile.exists())
			{
				problemFile.createNewFile();
			}
			writer = new FileWriter("/data/data/com.njuptjsy.cloudclient/"+filename);//也可以是文件对象
			writer.write(problem);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

		
	
}