/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mailserverapp;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocketFactory;
import mailprotocol.MailProtocol;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 *
 * @author ethenq
 */
public class MailServerApp extends javax.swing.JFrame {

    static MailServerApp mailServerApp;
    private DataTools tools;
    
    private static int port;
    private Thread thread;
    private static Thread closeThread;
    
    private final MailProtocol mailProtocol;
    
    private final Map<String, String> listClientRequests = new HashMap<>();
    private final Map<String, String> listClientResponses = new HashMap<>();


    public MailServerApp() { 
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        mailProtocol = new MailProtocol();
        tools = new DataTools();
        initComponents();
        
        readConfigFile();
    }

    
    ///////////////////////////////////////
    //// MAIL FUNCTIONALITIES
    //////////////////////////////////////
    
    private void readConfigFile()
    {
        SAXBuilder saxBuilder = new SAXBuilder();
        
        File file = new File("usr/config.xml");
        
        try {  
            // converted file to document object  
            Document document = saxBuilder.build(file);  

            // get root node from xml  
            Element rootNode = document.getRootElement();

            String defaultPort = rootNode.getChild("default_port").getValue();
            String directory = rootNode.getChild("directory").getValue();
                
            System.out.println("Configuration file - Default port: " + defaultPort);
            System.out.println("Configuration file - Saved directory: " + directory);
            
            jTextFieldPort.setText(defaultPort);
            
            this.port = Integer.parseInt(defaultPort);
        } catch (JDOMException | IOException e) {  
        }
    }
    
    
    ///////////////////////////////////////////////
    //// THREADS AND CONNECTIONS
    ///////////////////////////////////////////////
    
    private void connectSSL()
    {
        SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
    }

    private void sendWelcomeMessage(Socket client) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()))) {
            writer.write("You have been connected to e-mail server.");
            writer.flush();
        }
    }
    
    
    ///////////////////////////////////////////////
    //// SWING USER INTERFACE
    ///////////////////////////////////////////////
    
    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButtonStart = new javax.swing.JButton();
        jLabelPort = new javax.swing.JLabel();
        jTextFieldPort = new javax.swing.JTextField();
        jButtonStop = new javax.swing.JButton();
        jLabelWarning = new javax.swing.JLabel();
        jLabelMailsDirectory = new javax.swing.JLabel();
        jTextFieldMailsDirectory = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenuFileUsersManager = new javax.swing.JMenu();
        jMenuItemRun = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItemInfo = new javax.swing.JMenuItem();
        jMenuItemQuit = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButtonStart.setText("Server start");
        jButtonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartActionPerformed(evt);
            }
        });

        jLabelPort.setText("Server port:");

        jButtonStop.setText("Stop");
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });

        jLabelMailsDirectory.setText("Mails directory:");
        jTextFieldMailsDirectory.setText("/database/temp_mails");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelPort)
                            .addComponent(jLabelMailsDirectory))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldMailsDirectory, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldPort, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonStart)
                        .addGap(28, 28, 28)
                        .addComponent(jButtonStop, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 12, Short.MAX_VALUE)
                        .addComponent(jLabelWarning, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPort))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelMailsDirectory)
                    .addComponent(jTextFieldMailsDirectory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jLabelWarning, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonStart, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(jButtonStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout gl_jPanel1 = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(gl_jPanel1);
        gl_jPanel1.setHorizontalGroup(
            gl_jPanel1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        gl_jPanel1.setVerticalGroup(
            gl_jPanel1.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gl_jPanel1.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jMenuFileUsersManager.setText("File");

        jMenuItemRun.setText("Run...");
        jMenuItemRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRunActionPerformed(evt);
            }
        });
        jMenuFileUsersManager.add(jMenuItemRun);

        jMenuItem1.setText("Manage accounts");
        jMenuFileUsersManager.add(jMenuItem1);

        jMenuItemInfo.setText("Informations");
        jMenuFileUsersManager.add(jMenuItemInfo);

        jMenuItemQuit.setText("Exit");
        jMenuItemQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemQuitActionPerformed(evt);
            }
        });
        jMenuFileUsersManager.add(jMenuItemQuit);

        jMenuBar1.add(jMenuFileUsersManager);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        
        // retrieve screen resolution
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = (int)screenSize.getWidth();
        int h = (int)screenSize.getHeight();
        
        // retrieve window size and set in the middle of the screen 
        Dimension windowSize = getSize();
        int x = (int)windowSize.getWidth();
        int y = (int)windowSize.getHeight();
        
        System.out.println("Resolution w: " + w + ", h: " + h);
        System.out.println("Window Width x: " + x + ", Height Y: " + y);
        
        setTitle("E-mail Server");
        setLocation((w/2)-(x/2), (h/2)-(y/2));
        setVisible(true);
        setResizable(false);
    }

    private void jButtonStartActionPerformed(java.awt.event.ActionEvent evt) {
        try {
                // Server start
                thread = new Thread(new Socketing());
                thread.start();
                
                jLabelWarning.setText("Server started! Waiting for clients...");
        } catch (NumberFormatException ex) {
                Logger.getLogger(MailServerApp.class.getName()).log(Level.SEVERE, null, ex);
                
                jLabelWarning.setText("Wrong port value!");
        }
    }

    private void jMenuItemQuitActionPerformed(java.awt.event.ActionEvent evt) {
        jButtonCloseActionPerformed(evt);
    }

    private void jMenuItemRunActionPerformed(java.awt.event.ActionEvent evt) {
        jButtonStartActionPerformed(evt);
    }

    private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {
    	// TODO: implement
    }

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {
    	java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(new java.awt.event.WindowEvent(this, java.awt.event.WindowEvent.WINDOW_CLOSING));
    }
    
    //////////////////////////
    //// MAIN THREAD
    //////////////////////////
    
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MailServerApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MailServerApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MailServerApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MailServerApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                mailServerApp = new MailServerApp();
                mailServerApp.setVisible(true);
            }    
        });
    }
    
    static class Closing implements Runnable {
        
        public Closing() {  
        }

        @Override
        public void run() {
            mailServerApp.addWindowListener( new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent we) {
                    System.out.println("Closing server application.");
                    
                    System.exit(0);
                }
            } );
        }
    }
    
    static class Socketing implements Runnable {
        
        public Socketing() {        
        }

        @Override
        public void run() {
	        // connectSSL();
	        
	        ServerSocket serverSocket;
	        try {
                System.out.println("Starting e-mail server, port " + jTextFieldPort.getText());  
                port = Integer.parseInt(jTextFieldPort.getText());
                
                serverSocket = new ServerSocket(port);

                while (true)
                {
                    System.out.println("Waiting for clients...");

                    // SSLSocket client = (SSLSocket) sslServerSocket.accept();

                    Socket client = serverSocket.accept();

                    System.out.println("Client " + client.getInetAddress().getCanonicalHostName() + " has connected.");

                    // A client has connected to this server. Send welcome message
                    Thread thread = new Thread(new ClientHandler(client));
                    thread.start();
                }
	        } catch (IOException ex) {
	            Logger.getLogger(MailServerApp.class.getName()).log(Level.SEVERE, null, ex);
	        }
        }
    }


    private javax.swing.JButton jButtonStart;
    private javax.swing.JButton jButtonStop;
    private javax.swing.JLabel jLabelMailsDirectory;
    private javax.swing.JLabel jLabelPort;
    private static javax.swing.JLabel jLabelWarning;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenuFileUsersManager;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItemInfo;
    private javax.swing.JMenuItem jMenuItemQuit;
    private javax.swing.JMenuItem jMenuItemRun;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextFieldMailsDirectory;
    private static javax.swing.JTextField jTextFieldPort;
}