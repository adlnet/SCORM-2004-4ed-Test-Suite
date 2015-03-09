package org.adl.testsuite.rte.lms;

import java.applet.Applet;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import netscape.javascript.JSObject;

import org.adl.logging.LoggingMessages;
import org.adl.testsuite.rte.lms.util.LMSMessages;
import org.adl.testsuite.util.AppletList;
import org.adl.testsuite.util.CTSEnvironmentVariable;
import org.adl.testsuite.util.TestSubjectData;
import org.adl.util.MessageType;
import org.adl.util.Messages;
import org.adl.util.debug.LogConfig;
import org.adl.util.support.SupportVerifier;

/**
 * LMS Test Applet.
 * 
 * @author ADL Tech Team
 */
public class LMSTestApplet extends Applet
{
   /**
    * Boolean representing if load was successful.
    */
   protected transient boolean mLoadSuccessful;
   
   /**
    * Boolean representing if delete was successful.
    */
   protected transient boolean mDeleteSuccessful;
   
   /**
    * Stores the saved sessions found on the computer.
    */
   protected transient Object[] mSaves;
   
   /**
    * Stores the last modified date of the current file. Used to handle security 
    * issues.
    */
   protected transient long mFileModDate = 0L;
   
   /**
    * Logger object used for debug logging
    */
   protected static final Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");
   
   /**
    * The next guy in line to pass method calls
    */
   protected transient LMSTestDriver mDriver;
   
   /**
    * The jsobject object
    */
   private transient JSObject mJsroot;
   
   /**
    * holds the value of the CTS environment variable
    */
   private static final String TS_HOME = CTSEnvironmentVariable.getCTSEnvironmentVariable();
   
   /**
    * holds the name of the saved session
    */
   private transient String mSessionName = "";

   /** 
    * (non-Javadoc)
    * @see java.applet.Applet#init()
    */
   public void init()
   {
      LOGGER.entering(getClass().getSimpleName(),"init()");

      // creates instance of LMSTestDriver object
      mDriver = new LMSTestDriver(TS_HOME);
      
      // setup the config for the logger
      final LogConfig myLogConfig = new LogConfig();
      myLogConfig.configure( TS_HOME, false );
      
      mJsroot = JSObject.getWindow(this);
    
      AppletList.register(getClass().getSimpleName(),this); 
            
      super.init();
      
      LOGGER.exiting(getClass().getSimpleName(),"init()");
      //mJsroot = JSObject.getWindow(this);
   }

   /** 
    * (non-Javadoc)
    * @see java.applet.Applet#start()
    */
   public void start()
   {
      LOGGER.entering(getClass().getSimpleName(),"start()");
      
      super.start();
      
      setUI();      
      
      mJsroot.call("enableNewLoadButtons", new String[]{ "" });
      logTitles();
      LOGGER.exiting(getClass().getSimpleName(),"start()");
   }
   
   /**
    * used to start brand new logs for a brand new test
    */
   public void startNewTest()
   {
      mDriver.startNewTest();
   }
   
   /**
    * used to load logs that have already been created from a previously saved
    * test
    * @param iSessionName name of the saved session file the user wants to load
    * @return loadSuccessful
    */
   public boolean startSavedTest(final String iSessionName)
   {
      mSessionName = Messages.unNull(iSessionName);
      mSessionName = AccessController.doPrivileged(
            new PrivilegedStartSavedSession(Messages.unNull(iSessionName)));
      return mLoadSuccessful;
   }

   /**
    * (non-Javadoc)
    * @see java.applet.Applet#stop()
    */
   public void stop()
   {
      mDriver.testStopped();
      super.stop();
   }
   
   /**
    * Deletes the session object.
    * 
    * @param iSessionName the name of the session 
    * @return whether the delete was successful
    */
   public boolean deleteSession(final String iSessionName)
   {
      AccessController.doPrivileged(new PrivilegedDeleteSession(iSessionName));
      return mDeleteSuccessful;
   }

   /**
    * why not return a string array?
    * @return String value of a ~ delimited list of saves
    */
   public String getListOfSaves()
   {
      AccessController.doPrivileged(new PrivilegedGetSavedSessions());
      final StringBuffer saveList = new StringBuffer(25);
      
      for(int i = 0; i < mSaves.length; i++)
      {
         String tempname = ((File)mSaves[i]).getName();
         String fileName = tempname.substring(0, tempname.lastIndexOf('.'));
         AccessController.doPrivileged(new PrivilegedGetModDate((File)mSaves[i]));
         final Date date = new Date(mFileModDate);
         
         if ( i != 0 )
         {
            saveList.append('~');
         }
         
         tempname = tempname.replaceAll("\'", "\\\\\'");
         tempname = tempname.replaceAll(" ", "%20");
         // the - and ~ are used to split on the client side... gotta escape them
         tempname = tempname.replaceAll("-", "&#45;");
         fileName = fileName.replaceAll("-", "&#45;");
         
         // used the URL escape code for the get file one, used the hmtl escape for the file name
         tempname = tempname.replaceAll("~", "%7E");
         fileName = fileName.replaceAll("~", "&#126;");
         
         saveList.append( "javascript:getFile('" + tempname + "')-" + fileName + "-" + date.toString());
      }
      return saveList.toString();
   }
   
   /**
    * This method will determine the text of a button depending on its 'id' 
    * value
    * @param buttonID String representing id of a button.
    * @return String associated with the id value passed in
    */
   public String buttonText(final String buttonID)
   {
      String text = LMSMessages.Instructions.getString("b." + buttonID);
      
      if ("".equals(text))
      {
         text = "___";
      }
      
      return text;
   }
   
   /**
    * Sets the chosen test packages.
    * @param iStringList - list of strings that contain the test packages.
    */
   public void setChosenTestPackages(final String iStringList)
   {
      final String[] splitChosen = Messages.unNull(iStringList).split(",");
      mDriver.setChosenTestPackages(Arrays.asList(splitChosen));
   }
   /**
    * This method sets the Names and IDs for the system users 
    * @param iStu1Name Name of student 1
    * @param iStu1ID ID of student 1
    * @param iStu2Name name of student 2
    * @param iStu2ID  ID of student 2
    * 
    */
   public void setLearnerInfo(final String iStu1Name, final String iStu1ID, 
         final String iStu2Name, final String iStu2ID)
   {
      mDriver.setLearnerIDInfo(Messages.unNull(iStu1Name), Messages.unNull(iStu1ID), Messages.unNull(iStu2Name), Messages.unNull(iStu2ID));
   }
   
   /**
    * This method returns the Name given to Learner (or Student) 1 
    * @return  String entered for Learner 1 Name
    */
   public String getLearner1Info()
   {
      return mDriver.getL1Name();
   }
   
   /**
    * This method returns the Name given to Learner (or Student) 2 
    * @return  String entered for Learner 2 Name
    */
   public String getLearner2Info()
   {
      return mDriver.getL2Name();
   }
   
   /**
    * This method returns the name value for a Test Case 
    * @return  String value for Test Case Name
    */
   public String getTCName()
   {
      return mDriver.getTestCaseName();
   }
   
   /**
    * @return boolean value: true if Test Package API has been chosen, false
    * otherwise
    */
   public boolean apiIsChosen()
   {
      return mDriver.apiIsChosen();
   }
   
   /**
    * @return boolean value: true if Test Package DMI has been chosen, false
    * otherwise
    */
   public boolean dmiIsChosen()
   {
      return mDriver.dmiIsChosen();
   }

   /**
    * Writes the message to the summary log in the browser
    * @param iMsg the message to be written to the log
    */
   public void writeToBrowser(final String iMsg)
   {
      mJsroot.call("logWriter", new String[]{Messages.unNull(iMsg)});
   }
   
   /**
    * clears the summary log in the browser window
    */
   public void clearLog()
   {
      LOGGER.entering(getClass().getSimpleName(),"clearLog()");
      mJsroot.call("logEraser", new String[]{""});
      LOGGER.exiting(getClass().getSimpleName(),"clearLog()");
   }
   
   /**
    * This method calls the 'waitingState' JavaScript function 
    * to change the state of the control buttons
    */
   public void callWaitingState()
   {
      mJsroot.call("waitingState", new String[]{""});
   }
   
   /**
    * Calls the JavaScript function within LMSControls to set the buttons 
    * related to the save state (retry and save disabled).
    */
   public void callSaveState()
   {
      mJsroot.call("saveState", new String[]{""});
   }
   
   /**
    * This method calls the 'waitingState' JavaScript function 
    * to change the state of the control buttons
    */
   public void callAllTestsCompletedWaitingState()
   {
      mJsroot.call("allTestsCompletedWaitingState", new String[]{""});
   }
   
   /**
    * This method is called when a user clicks the 'Continue' 
    * button on the test package instructions page
    * @return true if the commit went off without a hitch, false otherwise
    */
   public boolean commit()
   {
      return mDriver.commit();
   }

   /**
    * method called when the user desires to retry a testcase
    */
   public void retry()
   {
       mDriver.retry();
   }
   
   /**
    */
   public void saveSession()
   {
      callSaveState();
      // if the savename is set in the inner class we need to make sure
      // the outer class is aware of the session name
      mSessionName = AccessController.doPrivileged(new PrivilegedSaveSession(mSessionName));
   }

   /**
    * method called when the 'abort' button is pressed on the webpage
    */
   public void abort()
   {
      mDriver.abort();
   }
   
   /**
    * allows the user to view the current detailed log
    * @return String path to the current detailed log file
    */
   public String viewLog()
   {
      LOGGER.entering(getClass().getSimpleName(),"viewLog()");
      String temp = "file:///" + mDriver.viewLog();
      
      int startPos = 0;
      int indexPos = -1;
      String tempString;

      // Netscape isnt as forgiving as IE so we need to go through
      // and make sure that any backslashes are replaced with fwd slashes
      while ( (indexPos = temp.indexOf('\\', startPos)) != -1 )
      {
         tempString = temp.substring(0, indexPos) + "/"
            + temp.substring(indexPos + 1);

         temp = tempString;
         startPos = indexPos + 1;
      }
      
      LOGGER.exiting(getClass().getSimpleName(),"viewLog() - returning: " + temp);
      return temp;
   }
   
   /**
    * This method displays instructions to a LMS Test Package Instructions page 
    * @return String containing instructions received from the driver
    */
   public String getInstr()
   {
      return mDriver.getInstructions();
   }
   
   /**
    */
   public void logTitles()
   {
      final String[] titles = { 
            getADLTitle(),
            getCTSName(),
            getCTSVersion(),
            getTestName()
                        };
      
      mJsroot.call("displayInterface", titles);
   }
 
   /**
    * Creates a String value from the array of test content packages from 
    * LMSTestPackageManager 
    * @return String containing all content packages separated with '|'
    */
   public String getCPArrayString()
   {            
      final Object[] pkgArray = getPackageArray();
      final StringBuffer stringBuffer = new StringBuffer("~");
      for ( int i = 0; i < pkgArray.length; i++ )
      {
         stringBuffer.append(pkgArray[i]);
         stringBuffer.append('~');
      }
      
      return stringBuffer.toString();
   }
   
   /**
    * Provides a way to return the LMSSTRINGS array containing the IDs of 
    * all of the test packages
   * @return String array of all test package IDs
   */
   public Object [] getPackageArray()
   {
      return mDriver.getTestPackages().toArray();
   }
   
   /**
    * Sets the values of LMSName, LMSVersion, and LMSVendor. Comes from user input
    * and is needed for both summary and detailed logs
    * @param iLMSName String value of the LMS Name
    * @param iLMSVersion String value of the LMS Version
    * @param iLMSVendor String value of the Vendor Name
    */
   public void setTestingInfo(final String iLMSName, final String iLMSVersion,
         final String iLMSVendor)
   {
      LOGGER.entering(getClass().getSimpleName(),"setTestingInfo()");
      LOGGER.finer("LMS Name is: " + Messages.unNull(iLMSName) +
                  "\tLMSVersion is: " + Messages.unNull(iLMSVersion) +
                  "\tLMSVendor is: " + Messages.unNull(iLMSVendor));
      TestSubjectData.getInstance().setProduct(Messages.unNull(iLMSName));
      TestSubjectData.getInstance().setVendor(Messages.unNull(iLMSVendor));
      TestSubjectData.getInstance().setVersion(Messages.unNull(iLMSVersion));
      LOGGER.exiting(getClass().getSimpleName(),"setTestingInfo()");
   }
   
   /**
    * Sets the enrollment success for the first learner 
    * @param iL1EnrollSuccess String value representing whether the tester was
    * able to successfully enroll the learner in the test LMS ("true" or "false")
    */
   public void setL1EnrollSuccess(final String iL1EnrollSuccess)
   {
      mDriver.setL1EnrollSuccess("true".equals(iL1EnrollSuccess));
   }
   
   /**
    * Sets the enrollment success for the second learner 
    * @param iL2EnrollSuccess String value representing whether the tester was
    * able to successfully enroll the learner in the test LMS. ("true" or "false")
    */
   public void setL2EnrollSuccess(final String iL2EnrollSuccess)
   {
      mDriver.setL2EnrollSuccess("true".equals(iL2EnrollSuccess));
   }
   
   /**
    * Sends the environment information - OS, JRE, Browser - to the detailed log
    * will have to change the messages from "LogManagerInterfaceApplet" to
    * something else
    * @param iBrowserVersion version of the internet browser
    * @param iBrowserName vendor name of the internet browser
    * @param iBrowserOK indicates whether the browser is supported by this 
    * version of the test suite
    */
   public void writeEnvInfo(final String iBrowserVersion, 
                            final String iBrowserName, 
                            final boolean iBrowserOK)
   {        
      LOGGER.entering(getClass().getSimpleName(), "writeEnvInfo()");
      LOGGER.finer("BrowserVersion is: " + Messages.unNull(iBrowserVersion) +
                  "\tBrowserName is: " + Messages.unNull(iBrowserName) +
                  "\tBrowserOK is: " + iBrowserOK);
      // [0] is current JRE 
      // [1] is current OS
      final String[] info = getSystemInfo();

      // [0] is JRE supported 
      // [1] is OS supported
      final boolean[] isSupported = getVersions();

      final DateFormat dateFormat = 
         DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG);
      TestSubjectData.getInstance().setDate(dateFormat.format(new Date()));
      if ( isSupported[1] )
      {
         TestSubjectData.getInstance().setCurrentOS(LoggingMessages.getString(
            "LogManagerInterfaceApplet.OSGood", info[1]));
         TestSubjectData.getInstance().setOSMsgType(MessageType.HEADINFO);
      }
      else
      {            
         TestSubjectData.getInstance().setCurrentOS(LoggingMessages.getString(
            "LogManagerInterfaceApplet.OSBad", info[1]));
         TestSubjectData.getInstance().setOSMsgType(MessageType.HEADINFO);
      }

      if ( isSupported[0] )
      {
         TestSubjectData.getInstance().setCurrentJRE(LoggingMessages.getString(
            "LogManagerInterfaceApplet.JREGood", info[0]));
         
         TestSubjectData.getInstance().setJREMsgType(MessageType.HEADINFO);
      }
      else
      {
         TestSubjectData.getInstance().setCurrentJRE(LoggingMessages.getString(
            "LogManagerInterfaceApplet.JREBad", info[0]));
         
         TestSubjectData.getInstance().setJREMsgType(MessageType.HEADINFO);
      }
      
      if ( iBrowserOK )
      {
         TestSubjectData.getInstance().setCurrentBrowser(LoggingMessages.getString(
            "LogManagerInterfaceApplet.BrowGood", Messages.unNull(iBrowserName), Messages.unNull(iBrowserVersion)));
         
         TestSubjectData.getInstance().setBrowMsgType(MessageType.HEADINFO);
      }
      else
      {
         TestSubjectData.getInstance().setCurrentBrowser(LoggingMessages.getString(
            "LogManagerInterfaceApplet.BrowBad", Messages.unNull(iBrowserName), Messages.unNull(iBrowserVersion)));
         
         TestSubjectData.getInstance().setBrowMsgType(MessageType.HEADINFO);
      }

      mDriver.writePreliminaryInfo();
      LOGGER.exiting(getClass().getSimpleName(), "writeEnvInfo()");
   }
   
   /**
    * @return results
    */
   private String[] getSystemInfo()
   {
      final SupportVerifier mSupportVerifier = new SupportVerifier();
      return new String[]{mSupportVerifier.getCurrentJRE(), 
            mSupportVerifier.getCurrentOS()};
   }
   
   /**
    * @return temp
    */
   private boolean[] getVersions()
   {
      final SupportVerifier mSupportVerifier = new SupportVerifier();
      return new boolean[]{mSupportVerifier.verifyJRESupportBoolean(),
         mSupportVerifier.verifyOSSupportBoolean()};
   }
   /**
    * This method will write the Current User Instructions to the appropriate
    * frame in the Test Suite.
    * @param iMsg - String to be written in Current User Instructions.
    */
   public void writeCurUserInstr(final String iMsg)
   {
      mJsroot.call("writeCurUserInstr", new String[]{ Messages.unNull(iMsg) });      
   }
   
   /**
    * Returns the ADL title
    * @return the ADL title
    */
   public String getADLTitle()
   {
      return mDriver.getADLTitle();
   }
   
   /**
    * Returns the name of the Test Suite
    * @return the name of the Test Suite
    */
   public String getCTSName()
   {
      return mDriver.getCTSName();
   }
   
   /**
    * Returns the CTS version
    * @return the CTS version
    */
   public String getCTSVersion()
   {
      return mDriver.getCTSVersion();
   }
   
   /**
    */
   private void setUI()
   {
      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch (Exception e) 
      {
         e.printStackTrace();
      }
   }
   
   /**
    * Returns the name of the Test
    * @return the name of the Test
    */
   public String getTestName()
   {
      return mDriver.getTestName();
   }
   
   /**
    */
   public void lmsFinalize()
   {
      AccessController.doPrivileged(new PrivilegedFinalize());
   }
   
   // *****************BEGIN INNER CLASSES**************************
   
   /**
    * This inner class is used to grant permission to the code in this applet to
    * allow writing to the summary log.
    */
   private class PrivilegedSaveSession implements PrivilegedAction<String>
   {      
      /**
       * holds the user entered name of the saved session
       */
      private transient String mSessName;
      
      /**
       * Constructor of the inner class
       * @param iSessionName The LMSRTESession object
       */
      PrivilegedSaveSession(final String iSessionName)
      {
         mSessName = iSessionName;
      }

      /**
       * This run method grants privileged applet code access to write to the
       * summary log. This allows the applet to work in Netscape 6.
       * @return sessionName
       */
      public String run()
      {
         // if the session hasnt been saved yet prompt the user for the filename
         // to save it under
         if("".equals(mSessName))
         {
            while("".equals(mSessName) || ".obj".equals(mSessName.trim()))
            {
               mSessName = JOptionPane.showInputDialog("Please enter the name of the saved session") + ".obj";
            }
         }

         if(mDriver.saveSession(mSessName))
         {
            JOptionPane.showMessageDialog(null,
               mSessName.substring(0,mSessName.lastIndexOf('.')) + " saved");
         }   
         
         return mSessName;
      }
   }

   /**
    * This inner class is used to grant permission to the code in this applet to
    * allow writing to the summary log.
    */
   private class PrivilegedStartSavedSession implements PrivilegedAction<String>
   {
      /**
       * String representing the session name.
       */
      private transient String mSessionName;
      
      /**
       * Constructor of the inner class
       * @param iSessionName The LMSRTESession object
       */
      PrivilegedStartSavedSession(final String iSessionName)
      {
         mSessionName = iSessionName;
      }
      
      /**
       * This run method grants privileged applet code access to write to the
       * summary log. This allows the applet to work in Netscape 6.
       * @return null
       */
      public String run()
      {
         mLoadSuccessful = mDriver.startSavedTest(mSessionName);
         
         // if the load failed reset the name
         if(!mLoadSuccessful)
         {
            mSessionName = "";
         }

         return mSessionName;
      }
   }
   
   /**
    * This inner class is used to grant permission to the code in this applet to
    * allow writing to the summary log.
    */
   private class PrivilegedGetSavedSessions implements PrivilegedAction<Void>
   {
      /**
       * 
       * This run method grants privileged applet code access to write to the
       * summary log. This allows the applet to work in Netscape 6.
       * 
       * @return Object
       *  
       */
      public Void run()
      {
         mSaves = mDriver.getListOfSaves().toArray(new File[0]);

         return null;
      }
   }
   
   /**
    * This inner class is used to grant permission to the code in this applet to
    * allow writing to the summary log.
    */
   private class PrivilegedGetModDate implements PrivilegedAction<Void>
   {
      /**
       * Variable representing a file.
       */
      private transient final File mFile;
      /**
       * Constructor of the inner class
       * @param iFile
       *  
       */
      PrivilegedGetModDate(final File iFile)
      {
         mFile = iFile;
      }
      
      /**
       * 
       * This run method grants privileged applet code access to write to the
       * summary log. This allows the applet to work in Netscape 6.
       * @return Object null  
       */
      public Void run()
      {
         mFileModDate = mFile.lastModified();

         return null;
      }
   }
   
   /**
    * This inner class is used to grant permission to the code in this applet to
    * allow writing to the summary log.
    */
   private class PrivilegedDeleteSession implements PrivilegedAction<Object>
   {
      /**
       * String representing the session name.
       */
      private transient final String mSessionName;
      /**
       * Constructor of the inner class
       * @param iSessionName The LMSRTESession object
       */
      PrivilegedDeleteSession(final String iSessionName)
      {
         mSessionName = iSessionName;
      }
      
      /**
       * 
       * This run method grants privileged applet code access to write to the
       * summary log. This allows the applet to work in Netscape 6.
       * @return null  
       */
      public Object run()
      {
         mDeleteSuccessful = mDriver.deleteSession(mSessionName);

         return null;
      }
   }
   
   /**
    * This inner class is used to grant permission to the code in this applet to
    * allow writing to the summary log.
    */
   private class PrivilegedFinalize implements PrivilegedAction<Void>
   {
      /**
       * 
       * This run method grants privileged applet code access to write to the
       * summary log. This allows the applet to work in Netscape 6.
       * @return null
       */
      public Void run()
      {
         mDriver.lmsFinalize();

         return null;
      }
   }
}
