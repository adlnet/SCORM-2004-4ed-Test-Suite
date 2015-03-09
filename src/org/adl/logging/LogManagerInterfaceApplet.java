package org.adl.logging;

import java.applet.Applet;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

import netscape.javascript.JSObject;

import org.adl.testsuite.util.AppletList;
import org.adl.testsuite.util.CTSEnvironmentVariable;
import org.adl.testsuite.util.TestSubjectData;
import org.adl.testsuite.util.VersionHandler;
import org.adl.util.MessageType;
import org.adl.util.Messages;
import org.adl.util.support.SupportVerifier;

/**
 * <strong>Filename</strong>:  LogManagerInterfaceApplet.java<br><br>
 *
 * <strong>Description</strong>:  Provides an interface to allow the test log 
 * to be saved to a file.
 * 
 * @author ADL Technical Team
 */
public class LogManagerInterfaceApplet extends Applet
{
   /**
    * The logger object
    */
   protected static final Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");
   
   /**
    * The jsobject object
    */
   protected transient JSObject mJsroot;
   
   /**
    * Holder of html content
    */
   protected transient String mHTMLContent;
   
   /**
    * The log time
    */
   protected transient String mLogTime;
   
   private final transient SupportVerifier mSupportVerifier = new SupportVerifier();
   
   /**
    * Applet init() method.
    */
   public void init()
   {
      LOGGER.entering("LogManagerInterfaceApplet", "init()");
      
      // register this applet so that we can reference it in Summary Log Writer
      AppletList.register("logInterface",this);
      mSupportVerifier.verifyEnvironmentVariable(CTSEnvironmentVariable.CTS_ENV);
      
      mJsroot = JSObject.getWindow(this);
   }
   
   /**
    * Information about the applet. Is used when "getAppletInfo()" is called
    * 
    * @return String of the applet info - Title: + Author: + Name: + Description
    */
   public String getAppletInfo()
   {
      return "Title: LogManagerInterfaceApplet \n" +
             "Author: ADLI Project, CTC \n" +
             "Name: logInterface \n" +
             "The LogManagerInterfaceApplet is for connecting to the " +
             "Summary Log. This provides an ability to send messages to the " +
             "Summary Log and allows the log to be saved.";
   }
   
   /**
    * Called by the browser or applet viewer to inform this applet that it is 
    * being reclaimed and that it should destroy any resources that it has 
    * allocated.
    */
   public void destroy()
   {
      // no defined funtionality
   }

   /**
    * This method is responsible for saving the LOG contents by invoking the 
    * saveLog() method on the Log File Manager.
    *
    * @param iContent String representing the contents of the LOG.
    * @param iCurrentTime String representing the current time.
    */
   public void saveLogContentIE( final String iContent, final String iCurrentTime )
   {
      LOGGER.entering("LogManagerInterfaceApplet", 
                       "saveLogContentIE(String, String)");

      mHTMLContent = Messages.unNull(iContent);
      mLogTime = Messages.unNull(iCurrentTime);
   }

   /**
    * This method is responsible for saving the LOG contents by invoking the 
    * saveLog() method on the Log File Manager.
    *
    * @param iContent String representing the contents of the LOG.
    */
//   public void saveLogContentNS( String iContent )
//   {
//      LOGGER.entering("LogManagerInterfaceApplet", "saveLogContentNS(String)");

    //  mLogManager.saveLog(iContent);
//   }

   /**
    * Returns the current app path.
    *
    * @return String path to testsuite home.
    */
   public String getPath()
   {
      return AccessController.doPrivileged(new PrivilegedGetPath());
   }

   /**
    * Returns the version of SCORM.
    *
    * @return String Representing the version of SCORM.
    */
   public String getSCORMVersion()
   {
      return VersionHandler.getSCORMVersion();
   }

   /**
    * Returns the version of the Test Suite.
    *
    * @return String Representing the version of the Test Suite.
    */
   public String getTestsuiteVersion()
   {
      return VersionHandler.getTestsuiteVersion();
   }

   /**
    * Interface to SupportVerifier to determine if current JRE is supported.
    * 
    * @return true if supported, false if not.
    */
   public boolean verifyJRESupportBoolean()
   {
      return mSupportVerifier.verifyJRESupportBoolean();
   }
   
   /**
    * Interface to SupportVerifier to determine if current OS is support
    * 
    * @return true if supported, false if not.
    */
   public boolean verifyOSSupportBoolean()
   {
      return mSupportVerifier.verifyOSSupportBoolean();
   }

   /**
    * Interface to SupportVerifier to return Current OS
    * 
    * @return Name of current Operation System.
    */
   public String getCurrentOS()
   {
      return mSupportVerifier.getCurrentOS();
   }

   /**
    * Interface to SupportVerifier to return Current JRE
    * 
    * @return Name of current Java Runtime Environment.
    */
   public String getCurrentJRE()
   {
      return mSupportVerifier.getCurrentJRE();
   }
   
   /**
    * Supplies the title of ADL
    * 
    * @return ADL title
    */
   public String getADLTitle()
   {
      return LoggingMessages.getString("LogManagerInterfaceApplet.ADLTitle");
   }
   
   /**
    * Supplies the title of SCORM
    * 
    * @return SCORM title
    */
   public String getSCORMTitle()
   {
      return LoggingMessages.getString("LogManagerInterfaceApplet.SCORMTitle", 
                                                             getSCORMVersion());
   }
   
   /**
    * Supplies the title of the Test Suite
    * 
    * @return Test Suite title and version
    */
   public String getTSTitle()
   {
      return LoggingMessages.getString("LogManagerInterfaceApplet.TSTitle",
                                                         getTestsuiteVersion());
   }
   
   /**
    * Sends the environment information - OS, JRE, Browser - to the detailed log
    * 
    * @param iBrowserVersion version of the internet browser
    * @param iBrowserName vendor name of the internet browser
    * @param iBrowserOK indicates whether the browser is supported by this 
    * version of the test suite
    */
   public void writeEnvInfo(final String iBrowserVersion, 
                            final String iBrowserName, 
                            final boolean iBrowserOK)
   {  
      if ( verifyOSSupportBoolean() )
      {
         TestSubjectData.getInstance().setCurrentOS(LoggingMessages.getString(
            "LogManagerInterfaceApplet.OSGood", getCurrentOS()));
         
         TestSubjectData.getInstance().setOSMsgType(MessageType.HEADINFO);
      }
      else
      {            
         TestSubjectData.getInstance().setCurrentOS(LoggingMessages.getString(
            "LogManagerInterfaceApplet.OSBad", getCurrentOS()));
         TestSubjectData.getInstance().setOSMsgType(MessageType.HEADINFO);
      }
      if ( verifyJRESupportBoolean() )
      {
         TestSubjectData.getInstance().setCurrentJRE(LoggingMessages.getString(
            "LogManagerInterfaceApplet.JREGood", getCurrentJRE()));
         
         TestSubjectData.getInstance().setJREMsgType(MessageType.HEADINFO);
      }
      else
      {
         TestSubjectData.getInstance().setCurrentJRE(LoggingMessages.getString(
            "LogManagerInterfaceApplet.JREBad", getCurrentJRE()));
         
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
   }
   
   /**
    * This inner class is used to grant permission to the code in this applet 
    * to allow read/write to files on the local disk.
    */
   private class PrivilegedGetPath implements PrivilegedAction<String>
   {
      /**
       * This run method grants privileged applet code access to read/write
       * to the local disk.  This allows the applet to work in Netscape 6.
       *
       * @return Object
       */
      public String run()
      {
         String path = null;
         try
         {
            path = CTSEnvironmentVariable.getCTSEnvironmentVariable();
         }
         catch(Exception e)
         {
            e.printStackTrace();
         }
         return path;
      }
   } // End of Class PrivilegedGetPath
}