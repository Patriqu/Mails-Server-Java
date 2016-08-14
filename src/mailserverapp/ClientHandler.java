package mailserverapp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import mailprotocol.MailProtocol;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class ClientHandler implements Runnable {

  private DataTools tools;
  
  private BufferedInputStream reader;
  private ObjectInputStream obReader;
  
  private BufferedOutputStream writer;
  private ObjectOutputStream obWriter;
  
  private List<Object> frame;
  private Queue<String> queueMails;
  
  private String user;

  public ClientHandler(Socket client) throws IOException {
        tools = new DataTools();
        
        frame = new ArrayList<>();
        frame.add(0, null);
        frame.add(1, null);
      
        reader = new BufferedInputStream(client.getInputStream());
        obReader = new ObjectInputStream(reader);
        
        writer = new BufferedOutputStream(client.getOutputStream());
        obWriter = new ObjectOutputStream(writer);
        obWriter.flush();
        
        queueMails = new LinkedList<>();
  }

  @Override
  public void run() {
     try {
		System.out.println("Thread started with name:" + Thread.currentThread().getName());

		readResponse();
     } catch (IOException | InterruptedException e) {
     } catch (ClassNotFoundException ex) {
        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
     } catch (JDOMException ex) {
        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
     }
  }

    public void readMailFrame() throws IOException {
      try { 
          String myMessageArray[] = (String[]) obReader.readObject();
          
          System.out.println("Table element 0: " + myMessageArray[0]);
          System.out.println("Table element 1: " + myMessageArray[1]);
          System.out.println("Table element 2: " + myMessageArray[2]);
      } catch (ClassNotFoundException ex) {
          Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  
    
    private int getNumberOfNewMails(String user)
    {
        File file = new File("database/temp_mails/" + user);
        int nr = file.list().length;
        
        return nr;
    }
    
    private String saveMail(Map<String, Object> m)
    {
        String receiver = m.get("receiver").toString();
        
        if (tools.isOnline(receiver)) {
            System.out.println("Client " + receiver + " is online!");
        }
        else if (tools.isOnline(receiver)) {
            System.out.println("Client " + receiver + " is offline!");
        
            try {
                String destDir = "database/temp_mails/" + m.get("receiver") + "/" + m.get("title") + "/";
                
                //////////////////
                //// MAIL SAVING 
                //////////////////
                
                new File(destDir).mkdirs();
                
                // returns an xml element object  
                // school is passed to make it root element in document  
                Element mail = new Element("mail");

                // created an document object, all elements will be added to it  
                // passes school as parameter to make it root element of document  
                Document document = new Document(mail);

                // adding child attribute to student element
                mail.addContent(new Element("header").setText(m.get("header").toString()));
                mail.addContent(new Element("sender").setText(m.get("sender").toString()));
                mail.addContent(new Element("receiver").setText(m.get("receiver").toString()));

                mail.addContent(new Element("title").setText(m.get("title").toString()));
                mail.addContent(new Element("datetime").setText(m.get("datetime").toString()));
                mail.addContent(new Element("txt_length").setText(m.get("txt_length").toString()));
                mail.addContent(new Element("mail_txt").setText(m.get("mail_txt").toString()));

                /////////////////////////
                // ATTACHMENTS SAVING
                /////////////////////////
                
                Map<Integer, Map> attachments = (Map<Integer, Map>)m.get("attachments");
                Element xmlAtts[] = new Element[attachments.size()];
                    
                for (int i = 0; i < attachments.size(); i++)
                {
                    //// Attachments save in mail folder:
                    
                    Map<String, Object> tmpMap = attachments.get(i);
                    File file = new File(tmpMap.get("att_title").toString());

                    tmpMap.put("att_title", file.getName());

                    try ( FileOutputStream fis = new FileOutputStream(destDir + tmpMap.get("att_title").toString()) ) 
                    {
                        byte[] bytes = (byte[]) tmpMap.get("att_data");

                        fis.write(bytes, 0, bytes.length);
                        fis.flush();
                        fis.close();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    //// attachments list in xml:
                    
                    xmlAtts[i] = new Element("attachment");

                    xmlAtts[i].addContent(new Element("att_title").setText(tmpMap.get("att_title").toString()));
                    xmlAtts[i].addContent(new Element("att_length").setText(tmpMap.get("att_length").toString()));

                    // get root element and added student element as a child of it  
                    document.getRootElement().addContent(xmlAtts[i]);
                }
                
                //////////////////////
                // SAVING TO XML FILE
                //////////////////////
                
                // get object to see output of prepared document  
                XMLOutputter xmlOutput = new XMLOutputter();  

                // passsed System.out to see document content on console  
                xmlOutput.output(document, System.out);  
                
                // passed fileWriter to write content in specified file             
                xmlOutput.setFormat(Format.getPrettyFormat());  
                
                FileOutputStream fos = new FileOutputStream(  
                  destDir + m.get("title") + ".xml");
                xmlOutput.output(document, fos);
                fos.flush();
                fos.close();
            } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if (tools.isOnline(receiver))
            {
                tools.getClient(receiver).queueMails.add(m.get("title").toString());

                sendResponseOtherClient("NEW_MAIL", receiver, m);      // notify receiver about new message
            }
        }
        return "SEND_MAIL WAIT";
    }

   private void readResponse() throws IOException, InterruptedException, ClassNotFoundException, JDOMException {
	Object userInput;

	while ( (userInput = obReader.readObject()) != null ) {
            List<Object> ob = (List<Object>) userInput;
            String in = (String) ob.get(0);
            
            System.out.println("Client: " + in);
            
            if (in.equals("TIME?"))
            {
				System.out.println("Request for time sent received. Sending actual time.");
				sendTime();
				break;
            }
            else if (in.contains("HELLO LOGIN") && in.contains(", PASSWD"))
            {
                //// check if user exists
                
                int end = in.indexOf(",");
                String login = in.substring(12, end);
                
                int indx_passwd = (in.lastIndexOf("PASSWD")) + "PASSWD ".length();
                String passwd = in.substring(indx_passwd);
                
                System.out.println("Login: " + login);
                System.out.println("Password: " + passwd);
                
                String ans = readUsersDatabase(login, passwd);          // check if user exists in database
                
                //// PREPARE RESPONSE FOR THE LOGGED USER WITH INFORMATION ABOUT WAITING MESSAGES FOR HIM
                if (ans.equals("HELLO OK"))                         	// give number of new mails for logged user
                {
                    int n = getNumberOfNewMails(login);
                    
                    if (n > 0)
                        ans += ", NEW_MAILS " + n;
                    
                    this.user = login;                             		 // save user id in user handler
                    
                    //// ADD MAILS TO QUEUE
                    File file = new File("database/temp_mails/" + login);
                    String list[] = file.list();

                    queueMails.addAll(Arrays.asList(list));             // add mails from folder of waiting mails for this user to queue
                }
                
                System.out.println("Server: " + ans);
                
                sendResponse(ans);                                  	// send response to client
            }
            else if (in.equals("SEND_MAIL"))
            {
                String ans = "SEND_MAIL ALLOW";
                
                // allow client to send mail
                sendResponse(ans);
                
                System.out.println("Server: " + ans);
            }
            else if (in.equals("MAIL"))
            {               
                Map<String, Object> map = (Map<String, Object>) ob.get(1);
                System.out.println("readResponse() - Sender jest? " + map.get("sender"));
                
                // save mail in queue on server disc
                String ans = saveMail(map);
                
                // send response to sender client
                sendResponse(ans);
                
                System.out.println("Server: " + ans);
            }
            else if (in.equals("NEW_MAIL ALLOW"))
            {
                MailProtocol mailProtocol = new MailProtocol();
                
                System.out.println("User: " + user);
                System.out.println("queueMails.element(): " + queueMails.element());
                
                Map<String, Object> m = mailProtocol.generateMailFromXML(this.user, queueMails.element());

                sendResponseOtherClient("MAIL", user, m);
            }
            else if (in.equals("MAIL SUCCESS"))
            {                
                String dirName = "database/temp_mails/" + this.user + "/" + queueMails.element();
                
                File dir = new File(dirName);       	// remove mail from folder on the server
                String lista[] = dir.list();
                
                for (String name : lista)
                {
                    System.out.println("NAME: " + name);
                    
                    File file = new File(dirName + "/" + name);
                    file.delete();
                }
                dir.delete();
                
                queueMails.remove();                    // last send mail removed from the queue
                
                System.out.println("How many mails remained in queue: " + queueMails.size());
                
                if (tools.isOnline(user))
                {
                    if (!queueMails.isEmpty())
                    {
                        String ans = "NEW_MAIL";
                        sendResponse(ans);              // notify receiver about new message for him

                        System.out.println("Server: " + ans);
                    }
                }
            }
            else if (in.equals("MAIL FAIL"))
            {
                String ans = "MAIL ";
                
                sendResponse(ans);
                
                System.out.println("Server: " + ans);
            }
            else if (in.equals("BYE"))
            {
                String ans = "BYE OK";
                
                // send response acknowledging the end of connection
                sendResponse(ans);
                
                tools.deleteUser(this.user);        	// logout user from active users
                
                System.out.println("Server: " + ans);
            }
	}
   }
   
   private void sendResponse(String resp)
   {
     try {
         frame.set(0, resp);
         
         obWriter.reset();
         obWriter.writeObject(frame);
         obWriter.flush();
     } catch (IOException ex) {
         Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
     }
   }
   
   private void sendResponseOtherClient(String resp, String receiver, Map<String, Object> map)
   {
       try {
           List<Object> f = new ArrayList<>();
           f.add(0, resp);
           f.add(1, map);
         
           System.out.println("Is sender exist? " + map.get("sender"));             
           
           tools.getClient(receiver).getWriter().writeObject(f);
           tools.getClient(receiver).getWriter().flush();
       } catch (IOException ex) {
       	Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
   
  
   private void sendTime() throws IOException, InterruptedException {
		obWriter.writeUTF(new Date().toString());
		obWriter.flush();
   }
   
   private void closeBuffers() throws IOException
   {
       obWriter.close();
       writer.close();
       
       obReader.close();
       reader.close();
   }
   
           
   private void setOptionConfigFile()
   {
   	//TODO: implement
   }
   
   // Method actually contains also checking if user exists in the database
   private String readUsersDatabase(String login, String passwd)
   {
       // reading can be done using any of the two 'DOM' or 'SAX' parser  
       // we have used saxBuilder object here  
       // please note that this saxBuilder is not internal sax from jdk  
       SAXBuilder saxBuilder = new SAXBuilder();  

       // obtain file object   
       File file = new File("usr/users.xml");

       try {  
           // converted file to document object  
           Document document = saxBuilder.build(file);  

           // get root node from xml  
           Element rootNode = document.getRootElement();           
           
           System.out.println("Login in searcher: " + login);
           System.out.println("Passwd in searcher: " + passwd);
           
           for (Element node : rootNode.getChildren("user"))
           {
               // retrieve data from database
               String clogin = node.getAttribute("login").getValue();
               String cpasswd = node.getAttribute("password").getValue();
               
               System.out.println("Searched login: " + clogin);
               System.out.println("Searched password: " + cpasswd);
               
               // if login and password are found in database
               if (login.equals(clogin) && passwd.equals(cpasswd))
               {
                   System.out.println("Login and password found in database");
                   System.out.println("Login : " + login);
                   System.out.println("Password : " + passwd);
                   System.out.println("");
                   
                   // add user to online users table
                   tools.addUser(login);
                   tools.addClient(login, this);
                   
                   // allow
                   return "HELLO OK";
               }
           }
       } catch (JDOMException | IOException e) {  
       }
       
       return "HELLO FAIL";
   }
   
    public ObjectOutputStream getWriter()
    {
        return obWriter;
    }
}