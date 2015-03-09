package org.adl.util.xmltransform;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

/**
 * <strong>Filename:</strong> XMLTransform<br><br>
 *
 * <strong>Module/Package Name:</strong> org.adl.util.transform<br><br>
 *
 * <strong>Description:</strong><br>
 * The purpose of the <code>XMLTransform</code> is to provide a simple GUI
 * interface to perform an XML Transformation on any XML file.  The
 * Transformation Tool utilizes the Xalan parser developed by the Apache
 * organizations (xml.apache.org).
 *
 * The tool presents a simple GUI that allows the user to:
 * <ol>
 *   <li>Select a single XML file to transform (must have a .xlm extension)</li>
 *   <li>Select a single Ouput file and location (must have a .xml
 *       extension</li>
 *   <li>Select a single XSL Transformation file (must have a .xsl
 *       extension</li>
 * </ol>
 *
 * Once all of the information is provide the 'Start Transformation' button
 * should be pressed to start the transformation.
 *
 * All messages (successful or error conditions) will be displayed in the Status
 * Text Field.
 *
 * <strong>Design Issues:</strong><br>
 *
 * <strong>Implementation Issues:</strong><br>
 *
 * <strong>Known Problems:</strong><br>
 *
 * <strong>Side Effects:</strong><br>
 *
 * <strong>References:</strong><br>
 *
 * @author ADL Technical Team
 * @version 1.0
 */
public class XMLTransform extends JFrame
{
   /**
    * Flag to indicate whether or not the Input XML file was set correctly.
    */
   private boolean mInput = false;

   /**
    * Flag to indicate whether or not the Output XML file was set correctly.
    */
   private boolean mOutput = false;

   /**
    * Flag to indicate whether or not the Transformation file was set correctly.
    */
   private boolean mTransform = false;

   /**
    * Component to hold the individual menus
    */
   private JMenuBar mMenuBar;

   /**
    * Component to represent the Menu
    */
   private JMenu mMenu;

   /**
    * Component to represent the About this tool Sub-Menu
    */
   private JMenuItem mAboutMenuItem;

   /**
    * Component to represent the Tool Usage Sub-Menu
    */
   private JMenuItem mHowToMenuItem;

   /**
    * Component use to host the Text Area for displaying status information
    */
   private JScrollPane mTextArea;

   /**
    * JButton used to select the input XML file to be transformed.
    */
   private JButton mSelectXMLFileButton;

   /**
    * The read-only text field to display the XML file being transformed.
    */
   private JTextField mXMLFileTextField;

   /**
    * JButton used to select the tranformed XML output file name and location.
    */
   private JButton mSelectOutputXMLFileButton;

   /**
    * The read-only text field to display the location and name of the
    * tranformed XML File.
    */
   private JTextField mXMLOutputFileTextField;

   /**
    * JButton used to select the XSL Transformation file.
    */
   private JButton mSelectXSLFileButton;

   /**
    * The read-only text field to display the location and name of the
    * XSL Transformation File
    */
   private JTextField mXSLTransformFileTextField;

   /**
    * The read-only text field to display the status of the Transformation
    */
   private JTextArea mStatusTextField;

   /**
    * JButton used to begin the tranformation process.
    */
   private JButton mStartTransformationButton;

   /**
    * JLabel used to label the status field
    */
   private JLabel mStatusLabel;

   /**
    * Constructor for the XML Transformer.  The constructor builds the Swing GUI
    * that is presented to the user.
    */
   public XMLTransform()
   {
      initComponents();
   }

   /**
   * This method is called from within the constructor to initialize the
   * components of the GUI
   */
   private void initComponents()
   {
      // Initialize all of the components of the GUI
      mMenuBar = new JMenuBar();
      mMenu = new JMenu();
      mAboutMenuItem = new JMenuItem();
      mHowToMenuItem = new JMenuItem();
      mSelectXMLFileButton = new JButton();
      mXMLFileTextField = new JTextField();
      mSelectOutputXMLFileButton = new JButton();
      mXMLOutputFileTextField = new JTextField();
      mSelectXSLFileButton = new JButton();
      mXSLTransformFileTextField = new JTextField();
      mStatusTextField = new JTextArea(5,10);
      mTextArea = new JScrollPane(mStatusTextField);
      mTextArea.setVerticalScrollBarPolicy(
         JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      mTextArea.setPreferredSize(new Dimension(250,100));

      mStartTransformationButton = new JButton();
      mStatusLabel = new JLabel();

      // Build the Help Menu
      mMenu.setText("Help");
      mMenu.setHorizontalTextPosition(SwingConstants.LEADING);
      mMenu.setRolloverEnabled(true);

      // Build the About Sub-Menu item
      mAboutMenuItem.setText("About");

      // Associate an Action Listner to the About Menu Item
      mAboutMenuItem.addActionListener(
         new ActionListener()
         {
            public void actionPerformed( ActionEvent iEvt )
            {
               processAboutSelection(iEvt);
            }
         });

      // Add the About Sub-Menu Item to the Help Menu
      mMenu.add(mAboutMenuItem);

      // Build the How To Use Sub-Menu Item
      mHowToMenuItem.setText("Tool Usage");

      // Associate an Action Listner to the How To Use Menu Item
      mHowToMenuItem.addActionListener(
           new ActionListener()
           {
              public void actionPerformed( ActionEvent iEvt )
              {
                 processHowToSelection(iEvt);
              }
           });

      // Add the How To Use Sub-Menu Item to the Help Menu
      mMenu.add(mHowToMenuItem);

      // Add the Help Menu to the Menu bar
      mMenuBar.add(mMenu);

      // Build the Control Panel
      getContentPane().setLayout(new GridBagLayout());
      GridBagConstraints gridBagConstraints1;

      // Set up the Window properties
      setTitle("XML Transform Utility");
      getContentPane().setForeground(Color.red);
      setResizable(false);
      getContentPane().setBackground(Color.blue);
      addWindowListener(
           new WindowAdapter()
           {
              public void windowClosing( WindowEvent IEvt )
              {
                 exitForm(IEvt);
              }
           });

      // Create the Select Input File button
      mSelectXMLFileButton.setText("Select Input File (XML)");
      mSelectXMLFileButton.setBackground(Color.lightGray);
      mSelectXMLFileButton.setToolTipText("Used to select Input XML File");
      mSelectXMLFileButton.setBorder(new SoftBevelBorder(BevelBorder.RAISED));

      // Add the action listener to process when the button was pressed
      mSelectXMLFileButton.addActionListener(
           new ActionListener()
           {
              public void actionPerformed( ActionEvent iEvt )
              {
                 processInputXMLFileSelection(iEvt);
              }
           });

      // Add the Select Input File button to the GUI
      gridBagConstraints1 = new GridBagConstraints();
      gridBagConstraints1.ipadx = 50;
      gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
      getContentPane().add(mSelectXMLFileButton, gridBagConstraints1);

      // Create the XML Input File Text Field and add it to the GUI
      mXMLFileTextField.setPreferredSize(new Dimension (250,17));
      mXMLFileTextField.setEditable(false);
      mXMLFileTextField.setHorizontalAlignment(JTextField.LEFT);
      mXMLFileTextField.setBackground(Color.white);
      mXMLFileTextField.setToolTipText("Indicates the selected input XML File");
      gridBagConstraints1 = new GridBagConstraints();
      gridBagConstraints1.gridwidth = 2;
      gridBagConstraints1.ipadx = 400;
      gridBagConstraints1.ipady = 6;
      gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
      gridBagConstraints1.weightx = 1.0;
      getContentPane().add(mXMLFileTextField, gridBagConstraints1);

      // Create the Select Output File button
      mSelectOutputXMLFileButton.setText("Select Output File (XML)");
      mSelectOutputXMLFileButton.setBackground(Color.lightGray);
      mSelectOutputXMLFileButton.setBorder(
         new SoftBevelBorder(BevelBorder.RAISED));
      mSelectOutputXMLFileButton.setToolTipText("Used to select Output XML " +
         "File Name and Location");

      // Add the action listener to process when the button was pressed
      mSelectOutputXMLFileButton.addActionListener(
         new ActionListener()
         {
            public void actionPerformed( ActionEvent iEvt )
            {
               processOutputXMLFileSelection(iEvt);
            }
         });

      // Add the Select Output File Loction to the GUI
      gridBagConstraints1 = new java.awt.GridBagConstraints();
      gridBagConstraints1.gridx = 0;
      gridBagConstraints1.gridy = 1;
      gridBagConstraints1.ipadx = 41;
      gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
      getContentPane().add(mSelectOutputXMLFileButton, gridBagConstraints1);

      // Create the Select Output File Text Field and add it to the GUI
      mXMLOutputFileTextField.setPreferredSize(new Dimension (250,17));
      mXMLOutputFileTextField.setEditable(false);
      mXMLOutputFileTextField.setHorizontalAlignment(JTextField.LEFT);
      mXMLOutputFileTextField.setBackground(Color.white);
      mXMLOutputFileTextField.setToolTipText("Indicates the Output XML File " +
         "Name and Locaiton");
      gridBagConstraints1 = new GridBagConstraints();
      gridBagConstraints1.gridx = 1;
      gridBagConstraints1.gridy = 1;
      gridBagConstraints1.gridwidth = GridBagConstraints.REMAINDER;
      gridBagConstraints1.ipadx = 400;
      gridBagConstraints1.ipady = 6;
      gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
      gridBagConstraints1.weightx = 1.0;
      getContentPane().add(mXMLOutputFileTextField, gridBagConstraints1);

      // Create the Select Transformatin File Button
      mSelectXSLFileButton.setText("Select Transform File (XSL)");
      mSelectXSLFileButton.setBackground(Color.lightGray);
      mSelectXSLFileButton.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
      mSelectXSLFileButton.setToolTipText("Used to select XSL Transformation" +
         " File");

      // Add the action listener to process when the button was pressed
      mSelectXSLFileButton.addActionListener(
         new ActionListener()
         {
            public void actionPerformed( ActionEvent iEvt )
            {
               processXSLFileSelection(iEvt);
            }
         });

      // Add the button to the GUI
      gridBagConstraints1 = new GridBagConstraints();
      gridBagConstraints1.gridx = 0;
      gridBagConstraints1.gridy = 2;
      gridBagConstraints1.ipadx = 19;
      gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
      getContentPane().add(mSelectXSLFileButton, gridBagConstraints1);

      // Create the Select Transformation File Text Field and add it to the GUI
      mXSLTransformFileTextField.setPreferredSize(new Dimension (250,17));
      mXSLTransformFileTextField.setEditable(false);
      mXSLTransformFileTextField.setHorizontalAlignment(JTextField.LEFT);
      mXSLTransformFileTextField.setBackground(Color.white);
      mXSLTransformFileTextField.setToolTipText("Indicates the XSL " +
         "Transformation File");
      gridBagConstraints1 = new GridBagConstraints();
      gridBagConstraints1.gridx = 1;
      gridBagConstraints1.gridy = 2;
      gridBagConstraints1.gridwidth = GridBagConstraints.REMAINDER;
      gridBagConstraints1.ipadx = 400;
      gridBagConstraints1.ipady = 6;
      gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
      gridBagConstraints1.weightx = 1.0;
      getContentPane().add(mXSLTransformFileTextField, gridBagConstraints1);

      // Add the Status Label to the GUI
      mStatusLabel.setForeground(Color.black);
      mStatusLabel.setFont(new Font("Tahoma", Font.BOLD,16));
      mStatusLabel.setText("Status:");
      gridBagConstraints1 = new GridBagConstraints();
      gridBagConstraints1.gridx = 0;
      gridBagConstraints1.gridy = 3;
      getContentPane().add(mStatusLabel, gridBagConstraints1);

      // Create the Status Text Field and add it to the GUI
      mStatusTextField.setEditable(false);
      mStatusTextField.setAutoscrolls(true);
      mStatusTextField.setFont(new Font("Tahoma", Font.BOLD, 11));
      mStatusTextField.setLineWrap(true);
      mStatusTextField.setWrapStyleWord(true);
      mStatusTextField.setToolTipText("Transformation Tool status message " +
         "indicator");
      gridBagConstraints1 = new GridBagConstraints();
      gridBagConstraints1.gridx = 1;
      gridBagConstraints1.gridy = 3;
      gridBagConstraints1.gridwidth = GridBagConstraints.REMAINDER;
      gridBagConstraints1.ipadx = 400;
      gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
      gridBagConstraints1.weightx = 1.0;
      getContentPane().add(mTextArea, gridBagConstraints1);

      // Create the Start Transformation Button
      mStartTransformationButton.setBackground(Color.lightGray);
      mStartTransformationButton.setText("Start Transformation");
      mStartTransformationButton.setBorder(new
         SoftBevelBorder(BevelBorder.RAISED));
      mStartTransformationButton.setToolTipText("Starts the Transformation " +
         "Process");

      // Add the action listener to process when the button was pressed
      mStartTransformationButton.addActionListener(
         new ActionListener()
         {
            public void actionPerformed( ActionEvent iEvt )
            {
               processTransformationStart(iEvt);
            }
         });

      // Add the Button to the GUI
      gridBagConstraints1 = new GridBagConstraints();
      gridBagConstraints1.gridx = 0;
      gridBagConstraints1.gridy = 4;
      gridBagConstraints1.gridwidth = GridBagConstraints.REMAINDER;
      gridBagConstraints1.ipadx = 30;
      gridBagConstraints1.insets = new Insets(5, 5, 5, 5);
      getContentPane().add(mStartTransformationButton, gridBagConstraints1);

      setJMenuBar(mMenuBar);
      pack();
   }

   /**
    * This method processes the How To Use action found in the Help Menu.
    * This method displays a dialog box describing How To Use the
    * Transformation Utility.
    *
    * @param iEvt   The Action Event that signaled the method to be invoked.
    */
   private void processHowToSelection( ActionEvent iEvt )
   {
      String messageText = "Tool Usage: \n\n";
      messageText += "1. Click on the \"Select Input File (XML)\" button and " +
                     "select a valid XML file for input.\n\n";
      messageText += "2. Click on the \"Select Output File (XML)\" button" +
                     " to select or create a new output XML file.\n\n";
      messageText += "3. Click on the \"Select Transform File (XSL)\" button" +
                     " to select the XSL Transformation file to use for the " +
                     "transformation.\n\n";
      messageText += "4. Click on the \"Start Transformation\" button to" +
                     " perform the transformation.\n\n\n";
      messageText += "The status messages will be displayed in the Status " +
                     " Text Field";
      JOptionPane.showMessageDialog(mHowToMenuItem,  messageText,
                                    "How to use", JOptionPane.PLAIN_MESSAGE);
   }

   /**
    * This method processes the About action found in the Help Menu.  This
    * method displays a dialog box describing information reqarding the
    * Tranformation Utility.
    *
    * @param iEvt   The Action Event that signaled the method to be invoked.
    */
   private void processAboutSelection( ActionEvent iEvt )
   {
      String messageText = "XML Transform Utility.\nWritten By Cornelius " +
                           "Sybrandy of\nConcurrent Technologies " +
                           "Corporation for the\nAdvanced Distributed " +
                           "Learning (ADL) program.";
      JOptionPane.showMessageDialog(mAboutMenuItem,  messageText,
                                      "About", JOptionPane.PLAIN_MESSAGE);
   }

   /**
    * This method begins the transformation process.  The method takes the
    * Input XML file and applies the transformation defined in the Input XSL
    * file.  The output XML file is saved according the Output XML file
    * location.
    *
    * @param iEvt   The Action Event that signaled the method to be invoked.
    */
   private void processTransformationStart( ActionEvent iEvt )
   {
      // Make sure of the following:
      //  -  The Input XML file was supplied
      //  -  The Output XML file was supplied
      //  -  The Input XSL file was supplied
      if ( mInput && mOutput && mTransform )
      {
         // All necessary files were supplied
         try
         {
            // Start the transformation process
            mStatusTextField.append("Starting...\n");

            // Create the Transformer
            TransformerFactory tFactory = TransformerFactory.newInstance();

            Transformer transformer =
                tFactory.newTransformer(new StreamSource(
                mXSLTransformFileTextField.getText()));

            transformer.setOutputProperty(OutputKeys.INDENT,"yes");

            // Using the Transformer, transform the input XML file using
            // the supplied XSL
            transformer.transform(new StreamSource(mXMLFileTextField.getText()),
                     new StreamResult(new FileOutputStream(
                                      mXMLOutputFileTextField.getText())));

            // Tranformation is complete
            mStatusTextField.setBackground(Color.green);
            mStatusTextField.append("Done!!!\n");
         }
         catch ( Exception ex )
         {
            // Exception was thrown stop the transformation process
            mStatusTextField.append("ERROR: " + ex.getMessage());
         }
      }
      else
      {
         // The user did not supply the specific files needed
         mStatusTextField.append(
            "ERROR: Please ensure you have selected your input, output, and" +
            " transform files.\n");
      }
   }

   /**
    * This method processes the XSL File Selection action.  This method
    * opens a File Chooser selection box.  The purpose is to allow the user
    * to select the input XSL Transformation file.
    *
    * @param iEvt   The Action Event that signaled the method to be invoked.
    */
   private void processXSLFileSelection( ActionEvent iEvt )
   {
      // Create and open a File Chooser
      JFileChooser c = new JFileChooser();
      int rVal = c.showOpenDialog(XSLTransform.this);

      // Process the file choosen
      if ( rVal == JFileChooser.APPROVE_OPTION )
      {
         StringTokenizer temp =
                    new StringTokenizer(c.getSelectedFile().getName(), ".");

         if ( temp.countTokens() > 1 )
         {
            int i;
            for ( i = 1; i < temp.countTokens(); i++ )
            {
               temp.nextToken();
            }
         }

         // Make sure the file selected has a .xsl file extension
         if ( temp.nextToken().equals("xsl") )
         {
            mXSLTransformFileTextField.setText(
                c.getCurrentDirectory().toString() + "\\" +
                c.getSelectedFile().getName());

            // Set the attribute to indicate that the XSL file was selected
            mTransform = true;
            mStatusTextField.setBackground(Color.white);
            mStatusTextField.append("XSL Transformation File Selected.\n");
         }
         else
         {
            // File choosen was not a XSL Transformation File
            mStatusTextField.append(
               "ERROR: You must select an XSL file for your transformation.\n");
         }
      }
   }

   /**
    * This method processes the Output XML File Selection action.  This method
    * opens a File Chooser selection box.  The purpose is to allow the user
    * to select the location and file name for the transformed XML
    *
    * @param iEvt   The Action Event that signaled the method to be invoked.
    */
   private void processOutputXMLFileSelection( ActionEvent evt )
   {
      // Create and open a File Chooser
      JFileChooser c = new JFileChooser();
      int rVal = c.showOpenDialog(XMLTransform.this);

      // Process the file choosen
      if ( rVal == JFileChooser.APPROVE_OPTION )
      {
         StringTokenizer temp =
                    new StringTokenizer(c.getSelectedFile().getName(), ".");

         if ( temp.countTokens() > 1 )
         {
            int i;
            for ( i = 1; i < temp.countTokens(); i++ )
            {
               temp.nextToken();
            }
         }

         // Make sure the file choosen is an XML file
         if ( temp.nextToken().equals("xml") )
         {
            mXMLOutputFileTextField.setText(
                c.getCurrentDirectory().toString() + "\\" +
                c.getSelectedFile().getName());

            // Set the attribute to indicate that the XML file was selected
            mOutput = true;
            mStatusTextField.setBackground(Color.white);
            mStatusTextField.append(
               "Output XML Location and Filename Selected\n");
         }
         else
         {
            // File choosen was not a XSL Transformation File
            mStatusTextField.append(
               "ERROR: You must select an XML file for your output.\n");
         }
      }
   }

   /**
    * This method processes the Input XML File Selection action.  This method
    * opens a File Chooser selection box.  The purpose is to allow the user
    * to select the input XML File that needs the transformation.
    *
    * @param iEvt   The Action Event that signaled the method to be invoked.
    */
   private void processInputXMLFileSelection( ActionEvent evt )
   {
      // Create and open a File Chooser
      JFileChooser c = new JFileChooser();
      int rVal = c.showOpenDialog(XMLTransform.this);

      // Process the file choosen
      if ( rVal == JFileChooser.APPROVE_OPTION )
      {
         StringTokenizer temp =
                    new StringTokenizer(c.getSelectedFile().getName(), ".");

         if ( temp.countTokens() > 1 )
         {
            int i;
            for ( i = 1; i < temp.countTokens(); i++ )
            {
               temp.nextToken();
            }
         }

         // Make sure the file selected is an XML file.
         if ( temp.nextToken().equals("xml") )
         {
            mXMLFileTextField.setText(
                c.getCurrentDirectory().toString() + "\\" +
                c.getSelectedFile().getName());

            // Set the attribute to indicate that the XML file was selected
            mInput = true;
            mStatusTextField.setBackground(Color.white);
            mStatusTextField.append("Input XML Selected\n");
         }
         else
         {
            mStatusTextField.append(
               "ERROR: You must select an XML file for your input.\n");
         }
      }
   }

   /**
    * Exit the Application
    */
   private void exitForm( WindowEvent evt )
   {
      System.exit(0);
   }

   /**
    *
    * @param Arguments to the start the program.  No arguments required.
    */
   public static void main( String args[] )
   {
      new XMLTransform().show();
   }

}
