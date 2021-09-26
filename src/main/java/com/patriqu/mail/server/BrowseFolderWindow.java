package com.patriqu.mail.server;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author ethenq
 */
public class BrowseFolderWindow extends javax.swing.JFrame {

    JFileChooser fc = new JFileChooser();
    
    public BrowseFolderWindow() {
        createFileBrowser();
    }

    
    private void createFileBrowser()
    {
        JPanel pnl = new JPanel();
        pnl.setLayout(new GridLayout(2, 1));
        
        JButton btn = new JButton("JFileChooser.showOpenDialog() Demo");
        ActionListener al;
        al = new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent ae)
          {
             switch (fc.showOpenDialog(BrowseFolderWindow.this))
             {
                case JFileChooser.APPROVE_OPTION:
                   JOptionPane.showMessageDialog(BrowseFolderWindow.this, "Selected: "+
                                                 fc.getSelectedFile(),
                                                 "FCDemo",
                                                 JOptionPane.OK_OPTION);
                   break;

                case JFileChooser.CANCEL_OPTION:
                   JOptionPane.showMessageDialog(BrowseFolderWindow.this, "Cancelled",
                                                 "FCDemo",
                                                 JOptionPane.OK_OPTION);
                   break;
             
                case JFileChooser.ERROR_OPTION:
                   JOptionPane.showMessageDialog(BrowseFolderWindow.this, "Error",
                                                 "FCDemo",
                                                 JOptionPane.OK_OPTION);
             }
          }
       };
      btn.addActionListener(al);
      pnl.add(btn);

      btn = new JButton("JFileChooser.showSaveDialog() Demo");
      al = ae -> {
         switch (fc.showSaveDialog(BrowseFolderWindow.this))
         {
            case JFileChooser.APPROVE_OPTION:
               JOptionPane.showMessageDialog(BrowseFolderWindow.this, "Selected: "+
                                             fc.getSelectedFile(),
                                             "FCDemo",
                                             JOptionPane.OK_OPTION);
               break;

            case JFileChooser.CANCEL_OPTION:
               JOptionPane.showMessageDialog(BrowseFolderWindow.this, "Cancelled",
                                             "FCDemo",
                                             JOptionPane.OK_OPTION);
               break;

            case JFileChooser.ERROR_OPTION:
               JOptionPane.showMessageDialog(BrowseFolderWindow.this, "Error",
                                             "FCDemo",
                                             JOptionPane.OK_OPTION);
         }
      };
      btn.addActionListener(al);
      pnl.add(btn);
    }
    
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jMenuItem1 = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 630, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 434, Short.MAX_VALUE)
        );

        pack();
    }


    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BrowseFolderWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BrowseFolderWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BrowseFolderWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BrowseFolderWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BrowseFolderWindow().setVisible(true);
            }
        });
    }


    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JMenuItem jMenuItem1;
}
