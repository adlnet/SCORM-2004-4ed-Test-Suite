package org.adl.testsuite.rte.sco;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Logger;

import netscape.javascript.JSObject;

import org.adl.api.ecmascript.APIErrorCodes;
import org.adl.api.ecmascript.APIErrorManager;
import org.adl.datamodels.DMErrorCodes;
import org.adl.datamodels.DMFactory;
import org.adl.datamodels.DMInterface;
import org.adl.datamodels.DMProcessingInfo;
import org.adl.datamodels.SCODataManager;
import org.adl.util.Messages;
import org.adl.logging.DetailedLogMessageCollection;
import org.adl.logging.DetailedLogWriter;
import org.adl.logging.LogFileGenerator;
import org.adl.logging.SummaryLogMessageCollection;
import org.adl.logging.SummaryLogWriter;
import org.adl.logging.UIMessageProcessor;
import org.adl.testsuite.util.AppletList;
import org.adl.testsuite.util.TestSubjectData;
import org.adl.util.LogMessage;
import org.adl.util.MessageCollection;
import org.adl.util.MessageType;

/**
 * <strong>Filename</strong>:  SCORTETester.java<br><br>
 * 
 * <strong>Description:</strong><br>
 * The <code>SCORTETester</code> is the main test class of the Sharable Content
 * Object (SCO) Run-Time Environment (RTE) Conformance Utility Test module.
 *
 * @author ADL Technical Team
 */
public class SCORTETester implements Runnable
{
   /**
    * Time out period in seconds.  This value is initialized with the
    * user provided time out period (passed in on the startTest() call)
    */
   public int mTimeOutPeriod;
   
   /**
    * Time indicating the start of the thread in seconds
    */
   public int mStartTime;
   
   /**
    * Indicates if the LMS has been initialized - SCO has called Initialize()
    */
   public boolean mLMSInitializedFlag;

   /**
    * The portal between the applet and the JavaScript found on the
    * SCOInstructions.htm
    */
   private JSObject mJsroot;

   /**
    * Logger object used for debug logging
    */
   private static Logger mLogger;

   /**
    * An array of SCO Session objects.  The SCOSession objects keeps track
    * of information for each SCO Test Session
    * (errors, level of conformance,etc)
    */
   private SCOSession mSCOSession;

   /**
    * The launch location of the SCO
    */
   private String mLaunchLine;

   /**
    * Provides all LMS Error reporting
    */
   private APIErrorManager mLMSErrorManager = null;

   /**
    * The collection of Intialization Data
    */
   private Vector mInitData;
   
   /**
    * processes messages sent from the User Interface
    */
   final private UIMessageProcessor mUimp;
   
   /**
    *  Re-init - needed for Multi-SCOoc
    */
   private SCODataManager mSCOData;
   
   /**
    * generates detailed log files and returns the URL
    */
   final private LogFileGenerator mLfg;
   
   /**
    * This holds the complete URL of the detailed log file
    */
   private String mDetailedLogFileURL;
   
   /**
    * true if the the sco test is part of a content package
    */
   private boolean mIsScoInContentPackage = false;
   
   /**
    * holds the name of the test subject
    */
   private String mTestFileName;
   
   /**
    * The type of resource (SCO or ASSET) being tested
    */
   private String mResourceType;
   
   /**
    * Boolean indicating whether or not the test was stopped by some means
    * other than timeout
    */
   private boolean mTestComplete = false;
   
   /**
    * Boolean indicating whether or not the SCO was conformant
    */
   private boolean mTestConformant = true;
   
   /**
    * Default Constructor
    * @param iEnvironmentVariable The path of the environment variable value
    * used to create logs
    */
   public SCORTETester(String iEnvironmentVariable)
   {      
      mLogger = Logger.getLogger("org.adl.util.debug.testsuite"); 
      mLogger.entering("SCORTETester.java", "\tIn Constructor");
      mUimp = new UIMessageProcessor();
      
      // create new LogFileGenerator
      mLfg = new LogFileGenerator(iEnvironmentVariable); 
         
      // kick off SummaryLogWriter Thread
      new SummaryLogWriter(mLfg);
   }
   
   /**
    * Initializes the data required to start the SCO test
    * 
    * @param iJsroot A reference to the SCORTEDriver Applet 
    * @param iTestFileName The URL of the (sub)file being use in the test
    * @param iIsScoInContentPackage Boolean whether or not this is part of a 
    *                               Content Package test
    */
   public final void initializeSCOTester(JSObject iJsroot,
                                   String iTestFileName,
                                   boolean iIsScoInContentPackage)
   {	   
      mJsroot = iJsroot;
      
      mIsScoInContentPackage = iIsScoInContentPackage;

      mTestComplete = false;
      
      // make sure the slashes are correct
      if(iTestFileName.indexOf('/') != -1)
      {
         iTestFileName = iTestFileName.replace('/', File.separatorChar);
      }
      
      // This will pull the filename of the test subject minus the 
      //  extension      
      mTestFileName = iTestFileName.substring(
         iTestFileName.lastIndexOf(File.separator) + 1,
         iTestFileName.indexOf('.',iTestFileName.lastIndexOf(File.separator)) );

      // create a detailed log file
      mDetailedLogFileURL = mLfg.getLogName("SCO", mTestFileName, true);
      
      // initialize the new detailed log file
      new DetailedLogWriter(mDetailedLogFileURL);
      
      TestSubjectData.getInstance().sendToDetailedLog("SCO");
      
      if(!SummaryLogWriter.isLogFileCreated())
      {
         // add a newlog message to the SummaryLogMessageCollection so the SummaryLogWriter
         // knows to create the summary log file
         SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
                        MessageType.NEWLOG, mTestFileName));
         
         TestSubjectData.getInstance().sendToSummaryLog("SCO");
      }
      
      // Set up the SCO Session objects
      mSCOSession = new SCOSession();

      // Indicate that Initialize() has not been called
      setLMSInitFlag( false );

      // Set up Error Manager for Version 2004
      mLMSErrorManager = new APIErrorManager(APIErrorManager.SCORM_2004_API);
      
      // reset mLaunchLine
      mLaunchLine = "";
   }
   
   
   /**
    * Pass through method to allow UI to send messages to the Detailed and
    * Summary Logs.
    * 
    * @param iLog The log to write to
    * @param iMsgType The type of message based on an enumerated list
    * @param iMsgTxt The text intended to be written to log
    */
   public void writeLogEntry (int iLog, int iMsgType, String iMsgTxt)
   {
      mUimp.writeLogEntry(iLog,iMsgType,iMsgTxt);
   }

   /**
    * The start method for the Sharable Content Object Testing
    * 
    * @param iLaunchLine The Sharable Content Object Document to use in test
    * @param iTimeoutPeriod The Timeout Period supplied by the tester.
    * @param iInitData Vector that hold the initial data for the SCO
    */
   public void validateSCO( String iLaunchLine,
                            String iTimeoutPeriod,
                            Vector iInitData)

   {
      mLogger.entering( "SCORTETester", "validateSCO()" );  
     
      if ( mResourceType.equalsIgnoreCase("ASSET") )
      {
         SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
               MessageType.INFO, Messages.getString("SCORTETester.287") ));
      }
      else
      {
         SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
               MessageType.INFO, Messages.getString("SCORTETester.7") ));
      }
      
       
      SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
         MessageType.INFO, Messages.getString("SCORTETester.8") )); 
      
      mLaunchLine = iLaunchLine;
      mLogger.info("mLaunchLine = " + mLaunchLine);

      try
      {
         // Re-init - needed for Multi-SCO
         mSCOData = new SCODataManager();
         //  Add a SCORM 2004 Data Model
         mSCOData.addDM(DMFactory.DM_SCORM_2004);
         mSCOData.addDM(DMFactory.DM_SCORM_NAV);
         // Indicate that Initialize() has not been called
         setLMSInitFlag( false );
         // Convert the iTimeoutPeriod passed in, to an integer
         final Integer tmpInt = Integer.valueOf(iTimeoutPeriod);
         setTimeOutPeriod(tmpInt.intValue());
      }
      catch ( NumberFormatException nfe )
      {
         mLogger.severe( "Value used for time out period: " + iTimeoutPeriod + 
                         " is not a valid integer representing seconds" ); 

         // Invalid value supplied in the Timeout Value field
         SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
            MessageType.TERMINATE, Messages.getString("SCORTETester.16") +  
            iTimeoutPeriod ));
         
         DetailedLogMessageCollection.getInstance().addMessage ( new LogMessage(
            MessageType.TERMINATE, Messages.getString("SCORTETester.16") +  
            iTimeoutPeriod ));

         SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
            MessageType.TERMINATE, Messages.getString("SCORTETester.17") )); 
         
         DetailedLogMessageCollection.getInstance().addMessage ( new LogMessage(
            MessageType.TERMINATE, Messages.getString("SCORTETester.17") ));
         
         abortTest();

         return;
      }

      // Initialize the data model
      setupSCOVector( iInitData );
      
      // Launch the SCO and test
      launchSCO();
      
      mLogger.exiting( "SCORTETester", "validateSCO()" );  
   }

   /**
    * This method initializies a Vector for the SCO Data Model to the values
    * set by the user on the SCO Instructions Screen.  The actual values
    * cannot be actually set until we know which version of the data model
    * we are using - this will be after either Initialize or LMSInitialize
    * is called
    *
    * @param iInitData Initialized data manager for a SCO.
    */
   private void setupSCOVector( Vector iInitData )
   {
      mLogger.finest( "### Data Model Initialization Values ###" ); 

      try
      {
         // Update those input boxes from the SCO Screen
         mInitData = new Vector();
         mInitData.add(iInitData.get(0));       //iLearnerId
         mInitData.add(iInitData.get(1));       //iLearnerName
         mInitData.add(iInitData.get(2));       //iCreditFlag
         mInitData.add(iInitData.get(3));       //iMode
         mInitData.add(iInitData.get(4));       //iLaunchData
         mInitData.add(iInitData.get(5));       //iCommFrLMSComments
         mInitData.add(iInitData.get(6));       //iObjectives
         mInitData.add(iInitData.get(7));       //iDatamaps
         mInitData.add(iInitData.get(8));       //iMasteryscore
         mInitData.add(iInitData.get(9));       //iMaxtimeallowed
         mInitData.add(iInitData.get(10));      //iTimelimitaction
         mInitData.add(iInitData.get(11));      //iCompletionThreshold
      }
      catch (ArrayIndexOutOfBoundsException ae)
      {
         mLogger.severe( "ArrayIndexOutOfBoundsException caught " + ae ); 
      }
   }

   /**
    * This method is responsible for launching the Sharable Content Object that
    * is currently being processed.   The method invokes a JavaScript function
    * (launchSCO()) written in the HTML page containing the SCORTEDriver Applet.
    * Once the method has been invoked, the this method creates a Timer Thread
    * that keeps track of the Timeout period.
    */
   private void launchSCO()
   {
      mLogger.entering( "SCORTETester", "launchSCO()" );  
      
      mLogger.info( "Launching current SCO \"" + mLaunchLine + "\"");  
      // Clear the SCOs Session information
      mSCOSession.clearSession();
      // Set up parameters for launching the SCO
      String errs = Integer.toString( mSCOSession.getErrors() );


      mLogger.finer( "SCO (" + mLaunchLine + ") has " +  
                     errs + " errors" ); 
      String args[] = { mLaunchLine, errs };
      // Invoke the JavaScript call to launch the SCOs
      // mJsroot.call( "launchSCO", args ); 
      final JSObject apiWindow = JSObject.getWindow(AppletList.getApplet("APIImplementation"));
      apiWindow.call( "launchSCO", args );

      // Determine the current time
      final Calendar cal = Calendar.getInstance();

      // Convert time(milliseconds) to seconds
      setStartTime( cal.get(Calendar.SECOND) );

      Thread timerThread = new Thread(this, "Timer"); 
      timerThread.start();
      mLogger.exiting( "SCORTETester", "launchSCO()" );  
   }

   /**
    * This method aborts the SCO RTE test.
    */
   public void abortTest()
   {
      // declare an argument to represent that there was an error
      String arg[] = {"1"}; 

      SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
         MessageType.TERMINATE, Messages.getString("SCORTETester.35") )); 

      DetailedLogMessageCollection.getInstance().addMessage ( new LogMessage(
         MessageType.TERMINATE, Messages.getString("SCORTETester.35") ));
      
      // invoke the terminateTest() function
      mJsroot.call("terminateTest",arg); 
   }

   /**
    * Ends the SCO test
    * 
    * @param iReportFullConformance true if last SCO and the full conformance 
    * @param iReportTerminateResults true if checking for Terminate call 
    * report is desired
    */
   public void endTest( boolean iReportFullConformance, boolean iReportTerminateResults )
                         
   {
      //Report on the conformance of the SCO
      SCOConformanceReporter theReporter = new SCOConformanceReporter( mSCOSession);
      try
      {
         mTestComplete = true;
         
         if ( mResourceType.equalsIgnoreCase("ASSET") && !mSCOSession.isLMSInitialized() )
         {
            // Do nothing, an asset didnt init() or term(), that is what we want
         }
         // If terminate has never been called
         else if (!mSCOSession.isLMSFinished()&& iReportTerminateResults )
         {  
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage
               ( MessageType.FAILED, Messages.getString("SCORTETester.37") )); 
            SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
               MessageType.FAILED, Messages.getString("SCORTETester.37") )); 
            mSCOSession.setError();
         }
         
         if(iReportFullConformance)
         {
            // if we want to report full conformance then this will set the URL for the
            // detailed log and call reportConformance(true) from within this method
            theReporter.reportConformance(mDetailedLogFileURL);
         }
         else
         {
            if ( mResourceType.equalsIgnoreCase("ASSET") && !mSCOSession.isLMSInitialized() )
            {
               SummaryLogMessageCollection.getInstance().addMessage(
                     new LogMessage(MessageType.PASSED, 
                           Messages.getString("SCORTETester.288")));
               DetailedLogMessageCollection.getInstance().addMessage(
                     new LogMessage(MessageType.PASSED, 
                           Messages.getString("SCORTETester.288")));
            } 
            else
            {
               theReporter.reportConformance( iReportFullConformance );
            }
            
            // provide a link to the Detailed Log
            SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage (
              MessageType.LINKSCO, mDetailedLogFileURL ));
         
            SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
              MessageType.OTHER, Messages.getString("SCORTETester.36") ));
         }         
         
         String arg[] = {""}; 
         arg[0] = arg[0] + mSCOSession.getErrors();
         // invoke the terminateTest() function
         mJsroot.call( "terminateTest", arg ); 
      }
      catch (NullPointerException npe)
      {
         //Do nothing for now...this error occurs if the user clicks
         //on completetest without first launching a sco.
      }
      finally
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage
            ( MessageType.ENDLOG, "" ));
         
         // if the sco is part of a content package then we need to send a link
         // to the cp detailed log that references this sco detailed log
         if(mIsScoInContentPackage)
         {
            DetailedLogMessageCollection.getInstance().addMessage ( new LogMessage (
               MessageType.LINKSCO, mDetailedLogFileURL, mTestFileName ));
            
            if ( theReporter.didAnyScoFail() )
            {
               mTestConformant = false;
            }
         }
      }
   }

   /**
    * This method sets the Time Out Period supplied by the tester.
    *
    * @param iTimeoutPeriod The time out period, in seconds, provide by the
    * tester.
    *
    */
   private void setTimeOutPeriod( int iTimeoutPeriod )
   {
      mTimeOutPeriod = iTimeoutPeriod;
   }

   /**
    * This method sets the Start Time (in seconds) of the Thread to the value
    * passed in.
    *
    * @param iTime Time in seconds to set the Start Time to
    */
   private void setStartTime( int iTime )
   {
      mStartTime = iTime;
   }

   /**
    * This method sets the state of the LMS to the value passed in.
    *
    * @param iValue Boolean value to set the LMS Initialized Flag to
    */
   private synchronized void setLMSInitFlag( boolean iValue )
   {
      mLMSInitializedFlag = iValue;
   }

   /**
    * This method returns the Start Time (in seconds) of the Thread.
    *
    * @return The start time of the thread that was kicked off.
    */
   private int getStartTime()
   {
      return mStartTime;
   }

   /**
    * This method returns the Time Out Period supplied by the tester.
    *
    * @return The Tester supplied Time Out Period in seconds.
    */
   private int getTimeOutPeriod()
   {
      return mTimeOutPeriod;
   }

   /**
    * This method sets the value of resourceType
    * 
    * @param iType A String representing the type (SCO or Asset) of the resource
    */
   public void setResourceType(String iType)
   {
      mResourceType = iType;
   }
   
   /**
    * This method returns the state of the LMS.  If the Initialize()
    * call has been made the state returned is true.
    *
    * @return Boolean value indicating the state of the LMS.
    */
   private synchronized boolean getLMSInitFlag()
   {
       return mLMSInitializedFlag;
   }

   /**
    * This method is used during the processing of all of the API calls other
    * than Initialize().  It is used to determine if Initialize() has been
    * invoked.
    *
    * @param iMethodBeingChecked - string indicates which method to check.
    *
    * @return Flag indicates whether or not the testing should proceed.
    */
   private boolean shallProceed( String iMethodBeingChecked )
   {
      // flag to indicate whether or not to proceed with the processing
      // must check to see if Initialize() has been called by the SCO
      
	   boolean proceed = checkInitialization();

      // Single SCO Test - Check to see if Initialize() has been called
      if ( !proceed  )
      {  
      
          // If the request is for a State Management call return true
          if ( iMethodBeingChecked.equals("Terminate") ) 
          {
              mLMSErrorManager.setCurrentErrorCode(
                 APIErrorCodes.TERMINATE_BEFORE_INIT);
          }
          else if (iMethodBeingChecked.equals("GetValue")) 
          {
             mLMSErrorManager.setCurrentErrorCode(
                APIErrorCodes.GET_BEFORE_INIT);
          }
          else if (iMethodBeingChecked.equals("SetValue")) 
          {
             mLMSErrorManager.setCurrentErrorCode(
                APIErrorCodes.SET_BEFORE_INIT);
          }
          else if (iMethodBeingChecked.equals("Commit")) 
          {
             mLMSErrorManager.setCurrentErrorCode(
                APIErrorCodes.COMMIT_BEFORE_INIT);
          }
          else
          {
             proceed = true;
             mLMSErrorManager.setCurrentErrorCode(APIErrorCodes.NO_ERROR);
          }
      }
      return proceed;
   }

   /**
    * Method determines if the LMS has been initialized.
    *
    * @return Flag indicates if initialization has been called
    */
   private boolean checkInitialization()
   {
      // Get the Initialize flag
      boolean flagValue = getLMSInitFlag();

      // Check to see if flag was set
      if ( ! flagValue )
      {
         mLMSErrorManager.setCurrentErrorCode(
            APIErrorCodes.GENERAL_INIT_FAILURE);
      }
      
      return flagValue;
   }

   /**
    * This method writes SetValue error detail to the logs.
    *
    * @param iElement SCORM Run-Time Environment Data Model Element
    *
    */
   private void writeSetValueErrorToLogs( String iElement )
   {
      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.FAILED, Messages.getString("SCORTETester.48") )); 

      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.FAILED, Messages.getString("SCORTETester.51", "false")));  

      mSCOSession.setError();

      // Write the Error and Diagnostic data to the Detailed Log
      writeCurrentErrorAndDiagnosticInfo();

      // set the SCO Session SetValueStatus flag to false
      // SetValue() was not used correctly
      mSCOSession.setSetValueStatus(false);
   }


   /**
    * This method first writes the current error condition as set by the data
    * model to the Detailed Log file.  It then gets the current diagnostic
    * information as set by the data model.  Sometimes that diagnostic
    * information data string is identical to the current error condition, so
    * it checks to see if they are indeed identical.  If they are not
    * identical, it also writes that diagnostic information to the Detailed
    * Log file
    * 
    */
   private void writeCurrentErrorAndDiagnosticInfo()
   {
      // Get correct mapping of that abstract error code
      String rtnErrorCode = mLMSErrorManager.getCurrentErrorCode();

      String errorDescription = mLMSErrorManager.getErrorDescription(rtnErrorCode);
      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.FAILED, errorDescription ));

      // Get current Diagnostic Description of last error set
      String diagnosticDescription = mLMSErrorManager.getErrorDiagnostic();
      if(errorDescription.indexOf(diagnosticDescription) == (-1) )
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, diagnosticDescription ));
      }
   }

   /**
    * This method writes GetValue error detail to the logs.
    *
    * @param iRtnVal The return value
    */
   private void writeGetValueErrorToLogs( String iRtnVal )
   {      
      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.FAILED, Messages.getString("SCORTETester.55") )); 

      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.FAILED, Messages.getString("SCORTETester.56", iRtnVal )));  

      mSCOSession.setError();

      // Write the Error and Diagnostic data to the Detailed Log
      writeCurrentErrorAndDiagnosticInfo();

      // set the SCO Session GetValueStatus flag to false
      // GetValue() was not used correctly
      mSCOSession.setGetValueStatus(false);
   }

   /**
     * The threads run() method.  The thread executes the run method until
     * a condition is met to stop the Timer thread.
     */
   public void run()
   {
      // Time test was started in milliseconds
      long startTime = System.currentTimeMillis();
      
      // Boolean indicating initialize was called
      boolean lmsInitialized = false;
      
      // The number of seconds until timeout occurs
      int timeOut = getTimeOutPeriod();
      
      // Boolean indicating when timeout was exceeded
      boolean timeOutExceeded = false;
      
      // We are just starting the test so set it to false
      mTestComplete = false;
      
      // Boolean indicating when to stop the timeout timer
      boolean stopTimer = false;
      
      // Check for external stops as well as timer expiration 
      while ( !mTestComplete && !stopTimer )
      {
         lmsInitialized = getLMSInitFlag();
         
         if ( !lmsInitialized )
         {
            // Must do math before int conversion because int cannot hold a long value
            if ( (int)((System.currentTimeMillis() - startTime)/( (long)1000 ) ) > timeOut )
            {
               // Resource is an asset, it didn't call initialize() and timed out, it passes
               if ( mResourceType.equalsIgnoreCase("ASSET") && mIsScoInContentPackage )
               {
                  // It is CP Test, we need to stop this instance of SCO Test
                  // Call endTest in SCORTEInstructions
                  String arg[] = {"true"};                  
                  mJsroot.call("endTest", arg);
               }
               // If its not an asset, its a SCO, it fails
               else
               {
                  timeOutExceeded = true;
               }
               // Timeout occurred, stop the timer
               stopTimer = true;
            }
            // Not initialized but not timed out
            else
            {
               try
               {
                  // Sleep for 1 seconds, and then try again
                  Thread.sleep(1000);
               }
               catch ( InterruptedException ie )
               {
                  mLogger.severe( "Thread was interrupted by the exception: " + ie ); 
               }
            }
         }
         else
         {
            // Initialize was called, no need to continue looking for timeout
            stopTimer = true;
         }

      }
      
      // If time out has exceeded abort the test
      if ( timeOutExceeded )
      {
         SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.60") )); 
         
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.60") )); 
         
         mLogger.info( "Aborting test due to thread timeout..." ); 

         // Set error - If SCO is part of a Content Package, we do not
         // want it to be conformant.
         mSCOSession.setError();
         // Set argument to false to indicate that we do not want conformance
         // reported when timeout value expires - test just ends.
         String arg[] = {"false"};
         // Call endTest in SCORTEInstructions
         mJsroot.call("endTest", arg);
      }      
   }

   /**
    * The SCO must call this function before calling any other API
    * function.  It indicates to the LMS System that the SCO is going
    * to communicate.
    *
    * It can not be called more than once consecutively unless Terminate
    * is called.
    *
    * @param iInParameter Must be an empty string ("").
    * 
    * @param iValidInput Flag indicating a valid input
    *
    * @return Whether or not the Initialize() method was successful
    * "false" - Initialize failed, "true" - Initialize succeeded
    */
   public String Initialize( String pInParameter, boolean iValidInput )
   {
	   String iInParameter = pInParameter;
      mLogger.finer( "******************************************" ); 
      mLogger.entering( "SCORTETester", "Initialize()" );  
      mLogger.finer( "******************************************\n" ); 
      
      if ( mResourceType.equalsIgnoreCase("ASSET") )
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.WARNING, Messages.getString("SCORTETester.286") )); 
         SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.WARNING, Messages.getString("SCORTETester.286") ));
         // Set an error, there technically wasnt one, but we dont want the SCO
         // to be deemed conformant         
         mSCOSession.setError();
      }
      
      //Need to Initialize the data as set by the user on the
      //Instructions Screen
      setSCOValue( mSCOData );
      
      // Clear current error codes
      mLMSErrorManager.clearCurrentErrorCode();

      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.INFO, Messages.getString("SCORTETester.69") )); 
      
      mLogger.info( "INFO: SCO is searching for the API Adapter" ); 

      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.PASSED, Messages.getString("SCORTETester.71") )); 
      
      mLogger.info( "PASSED: SCO was able to find the API Adapter<br>" ); 

      // Set a flag to indicate that the API was found
      mSCOSession.setFindAPI();

      // check to see if the in parameter is null
      String tempiInParameter = String.valueOf(iInParameter);
      
      if ( tempiInParameter.equals("null") ) 
      {
         // Set to empty string to avoid an exception
         iInParameter = ""; 
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.74"))); 
      }
      else
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.73", iInParameter))); 
      }
      
      mLogger.info( "INFO: Initialize() has been invoked" ); 

      // Value to return, indicates whether or not the method was successful
      String result = "false"; 

      
      // Make sure the in parameter is null or empty
      mLogger.finest( "inParameter: [" + iInParameter + "]" );  

      // Make sure the in parameter is null or empty
      if ( iValidInput )
      {
         // Check to see if the LMS is already initialized
         if ( ! mSCOSession.isLMSInitialized() )
         {
            // Set the flag to indicate that the LMS is initialized
            setLMSInitFlag(true);

            // Set the SCO Session to indicate that Initialize() was called
            mSCOSession.lmsInitializedCalled();

            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.PASSED, Messages.getString("SCORTETester.79") )); 
            
            mLogger.info( "PASSED: Initialize() finished successfully<br>" ); 

            // Set the result to "true" - successful
            result = "true"; 

            mLogger.finer( "******************************************" ); 
            mLogger.finer( "Initialize() successfull" ); 
            mLogger.finer( "******************************************\n" ); 
         }
         else
         {
            // Terminate has been called
            if ( mSCOSession.isLMSFinished() )
            {
               // Invalid use of data model
               mLogger.finest("Initialize called after Terminate"); 

               mLMSErrorManager.setCurrentErrorCode(
                  APIErrorCodes.CONTENT_INSTANCE_TERMINATED);
               
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.FAILED, Messages.getString("SCORTETester.86"))); 
               
               mSCOSession.setError();

               // Write the Error and Diagnostic data to the Detailed Log
               writeCurrentErrorAndDiagnosticInfo();

               // set the SCO Session InitializeStatus flag to false
               // Initialize() was not used correctly
               mSCOSession.setInitializeStatus();
            }
            else
            {
               // In Version 2004 Initialize is allowed to be called more than
               // once
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.PASSED, Messages.getString("SCORTETester.88"))); 
               
               mLogger.info( "PASSED: Initialize() called more than once" ); 

               mLMSErrorManager.setCurrentErrorCode(
                  APIErrorCodes.ALREADY_INITIALIZED);

               // Get correct mapping of that abstract error code
               String rtnErrorCode = mLMSErrorManager.getCurrentErrorCode();

               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.PASSED, mLMSErrorManager.getErrorDescription(rtnErrorCode))); 
               
               // Set the result to "true" - successful
               result = "true"; 

               mLogger.finer( "******************************************" ); 
               mLogger.finer( "Initialize() successfull" ); 
               mLogger.finer( "******************************************\n" ); 
            }            
         }
      }
      else
      {
         // Invalid argument error
         mLMSErrorManager.setCurrentErrorCode(DMErrorCodes.GEN_ARGUMENT_ERROR);

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.95") )); 
         
         mLogger.info( "FAILED: LMS was NOT initialized" ); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.97") )); 
         
         mLogger.info( "FAILED: Invalid argument passed to Initialize()" ); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.99", iInParameter ))); 
         
         mLogger.info( "FAILED: Argument received: " + iInParameter ); 

         mSCOSession.setError();

         // Write the Error and Diagnostic data to the Detailed Log
         writeCurrentErrorAndDiagnosticInfo();

         // set the SCO Session InitializeStatus flag to false
         // Initialize() was not used correctly
         mSCOSession.setInitializeStatus();
      }

      return result;
   }

   /**
    * The SCO must call this function before it terminates, if it successfully
    * called Initialize() at any point. It signals to the LMS that the SCO has
    * finished communicating.
    * 
    * @param iInParameter String parameter must be the empty string ("").
    * 
    * @param iValidInput Flag indicating a valid input
    * 
    * @return Value indicating whether call was successful. ("true" or "false").
    */
   public String Terminate( String iInParameter, boolean iValidInput )
   {
      mLogger.finer( "******************************************" ); 
      mLogger.entering( "SCORTETester", "Terminate()" );  
      mLogger.finer( "******************************************\r\n" ); 
 
      // Clear current error codes
      mLMSErrorManager.clearCurrentErrorCode();

      // check to see if the in parameter is null
      String tempiInParameter = String.valueOf(iInParameter);
      
      if ( tempiInParameter.equals("null") ) 
      {
         // Set to empty string to avoid an exception
         iInParameter = ""; 
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.106") )); 
      }
      else
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.107", iInParameter) )); 
      }

      // Initialize the return value to "false"
      String result = "false"; 

      // Declare a return value for the error code
      String rtnErrorCode = ""; 
      
      mLogger.finest( "In parameter: [" + iInParameter + "]" );  

      // Make sure the in parameter is null or empty
      if ( iValidInput )
      {
         // True if Initialize has been called
         if ( shallProceed("Terminate") ) 
         {
            // Terminate has already been called
            if ( mSCOSession.isLMSFinished() )
            {
               // Invalid use of data model
               mLogger.finest("Terminate called after Terminate"); 

               mLMSErrorManager.setCurrentErrorCode(
                  APIErrorCodes.TERMINATE_AFTER_TERMINATE);
               
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.WARNING, Messages.getString("SCORTETester.113") )); 
               
               // Get correct mapping of that abstract error code
               rtnErrorCode = mLMSErrorManager.getCurrentErrorCode();

               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.WARNING, mLMSErrorManager.getErrorDescription(rtnErrorCode))); 
               
            }
            else
            {
               // Set the flag on the SCO Session to indicate
               // that Terminate() was invoked
               mSCOSession.lmsFinishCalled();

               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.PASSED, Messages.getString("SCORTETester.115") )); 
               
               mLogger.info( "PASSED: Terminate() finished successfully<br>" ); 

               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.INFO, Messages.getString("SCORTETester.117") )); 
               
               mLogger.info( "INFO: SCO is no longer initialized<br>" ); 
            }

            // Set the return value to "true"
            result = "true"; 

            String arg[] = {""}; 

            arg[0] = arg[0] + mSCOSession.getErrors();

            mLogger.finest( "Errors from SCO: " + arg[0] ); 
         }
         else
         {
            mLogger.finer("LMS Not initialized - Calling API request out " + 
                          "of order"); 

            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.FAILED, Messages.getString("SCORTETester.95") )); 
            
            mLogger.info( "TERMINATE: LMS not initialized" ); 

            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.FAILED, Messages.getString("SCORTETester.126") )); 
            
            mLogger.info( "TERMINATE: Invalid Terminate() call" ); 

            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.FAILED, Messages.getString("SCORTETester.128") )); 
            
            mLogger.info( "TERMINATE: SCO invoked API calls out of order" ); 

            mSCOSession.setError();

            // Write the Error and Diagnostic data to the Detailed Log
            writeCurrentErrorAndDiagnosticInfo();

            // Change the TerminateStatus to false.  SCO used the Terminate()
            // API incorrectly
            mSCOSession.setFinishStatus();
         }
      }
      else
      {
         // Invalid argument error
         mLMSErrorManager.setCurrentErrorCode(DMErrorCodes.GEN_ARGUMENT_ERROR);

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.130") )); 
         
         mLogger.info( "FAILED: Terminate() was NOT successful" ); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.132") )); 
         
         mLogger.info( "FAILED: Invalid argument passed to Terminate()" ); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.99", iInParameter ))); 
         
         mLogger.info( "FAILED: Argument received: " + iInParameter ); 

         mLogger.info( "INFO: LMS still initialized" ); 

         mSCOSession.setError();

         // Write the Error and Diagnostic data to the Detailed Log
         writeCurrentErrorAndDiagnosticInfo();

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.136") )); 
         
         // Change the TerminateStatus to false.  SCO used the Terminate()
         // API incorrectly
         mSCOSession.setFinishStatus();
      }

      mLogger.finer( "******************************************" ); 
      mLogger.finer( "Terminate() successful" ); 
      mLogger.finer( "******************************************\n" ); 

      return result;
   }

   /**
    * This method is used to determine values for various categories and
    * elements in the Data Model.  ONLY one value is returned for each call.
    * The category and/or element is named in the argument.
    *
    * @param iElement SCORM Run-Time Environment Data Mode Element.
    * 
    * @param iValidInput - whether it is valid input i.e. the correct number of parameters
    *
    * @return Value of the cmi datamodel element named "element".
    */
   public String GetValue( String pElement, boolean iValidInput )
   {
	   String iElement = pElement;
      mLogger.finer( "******************************************" ); 
      mLogger.entering( "SCORTETester", "GetValue()" );  
      mLogger.finer( "******************************************\n" ); 

      // Clear current error codes
      mLMSErrorManager.clearCurrentErrorCode();

      // check to see if the value parameter is null
      String tempiElement = String.valueOf(iElement);
      
      if ( tempiElement.equals("null") ) 
      {
         // Set to empty string to avoid an exception
         iElement = ""; 
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.147")));  
      }
      else
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.146", iElement)));  
      }

      // Declare a return value
      String rtnVal = ""; 

      // Set the flag on the SCO Session to indicate that GetValue() was invoked
      mSCOSession.lmsGetValueCalled();

      // Invoke the shallProceed() method to check to see if Initialize()
      // has been called.
      if ( shallProceed("GetValue") ) 
      {
         mLogger.finest( "Looking for the element " + iElement ); 
         
         DMProcessingInfo dmInfo = new DMProcessingInfo();
         int dmErrorCode = DMInterface.processGetValue(iElement, false, 
                                                   mSCOData, dmInfo);

         // Set the LMS Error Manager from the Data Model Error Manager
         mLMSErrorManager.setCurrentErrorCode(dmErrorCode);

         // Get the actual value of the data model element
         rtnVal = dmInfo.mValue;
         // need to reset to empty string if the value is null.  
         if (rtnVal == null) 
         {
             rtnVal = "";
         }

         // Will be true if the correct number of arguments exist in the
         // GetValue call
         if ( iValidInput )
         {
            // Terminate has been called
            if ( mSCOSession.isLMSFinished() )
            {
               // Invalid use of data model
               mLogger.finest("GetValue called after Terminate"); 
            
               mLMSErrorManager.setCurrentErrorCode(
                  APIErrorCodes.GET_AFTER_TERMINATE);
               
               writeGetValueErrorToLogs(rtnVal);
            }
            else if ( dmErrorCode == APIErrorCodes.NO_ERROR )
            {
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.INFO, Messages.getString("SCORTETester.56", rtnVal))); 
   
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.PASSED, Messages.getString("SCORTETester.155"))); 
   
               mSCOSession.setGetValueStatus(true);
               mSCOSession.setDataModel(iElement);
               mSCOSession.setDataTransferCall("GetValue()"); 
            }
            else if ( dmErrorCode == DMErrorCodes.NOT_INITIALIZED )
            {
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.INFO, Messages.getString("SCORTETester.157"))); 
   
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.PASSED, Messages.getString("SCORTETester.155"))); 
   
               mSCOSession.setGetValueStatus(true);
               mSCOSession.setDataModel(iElement);
               mSCOSession.setDataTransferCall("GetValue()"); 
            }
            else if ( dmErrorCode == DMErrorCodes.UNDEFINED_ELEMENT )
            {
               String model = iElement.substring(0,4);
   
               // Send these to the LogWriter
               if ( (!( model.equals("cmi."))) && 
                     (!( model.equals("adl.")))) 
               {
                  DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                     MessageType.WARNING, Messages.getString("SCORTETester.162") )); 
   
                  DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                     MessageType.WARNING, Messages.getString("SCORTETester.146", iElement )));  
   
                  DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                     MessageType.WARNING, Messages.getString("SCORTETester.56", rtnVal)));  
               }
               else
               {
   
                  DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                     MessageType.FAILED, Messages.getString("SCORTETester.167") )); 
   
                  writeGetValueErrorToLogs(rtnVal);
               }
            }
            else
            {
               // Invalid use of data model
               mLogger.finest("Invalid use of the Data model"); 
   
               writeGetValueErrorToLogs(rtnVal);
            }
         }
         else
         {
            // Invalid use of data model
            mLogger.finest("GetValue called with invalid number of arguments");

            mLMSErrorManager.setCurrentErrorCode(
               DMErrorCodes.GEN_GET_FAILURE);

            writeGetValueErrorToLogs(rtnVal);
         }

      }
      else
      {
         mLogger.finest( "LMS Not initialized - Calling API request out of " + 
                         "order" ); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.95") )); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.172") )); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.128") )); 

         mSCOSession.setError();

         // Write the Error and Diagnostic data to the Detailed Log
         writeCurrentErrorAndDiagnosticInfo();

         // set the SCO Session GetValueStatus flag to false
         // GetValue() was not used correctly
         mSCOSession.setGetValueStatus(false);
      }

      mLogger.finest( "*************************************************" ); 
      mLogger.finest( "Processing done for SCORTETester::GetValue" ); 
      mLogger.finest( "Returning: [" + rtnVal + "]" );  
      mLogger.finest( "*************************************************" ); 

      return rtnVal;
   }

   /**
    * This method is responsible for setting values on each of the categories
    * and elements.  The argument indicates which category or element is
    * being set.  Only one value may be set with a single method call.
    *
    * @param iElement SCORM Run-Time Environment Data Model Element
    * 
    * @param iValue The value to be evaluated
    * 
    * @param iValidInput - whether it is valid input i.e. the correct number of parameters
    *
    * @return Value to apply to the data element.
    */
   public String SetValue( String iElement, String iValue, boolean iValidInput )
   {
      mLogger.finer( "******************************************" ); 
      mLogger.entering( "SCORTETester", "SetValue()" );  
      mLogger.finer( "******************************************\n" ); 

      mLogger.finest( "Element being set: " + iElement); 
      mLogger.finest( "Value being used: " + iValue + "\n" );  

      String result = "false"; 
      
      // Clear current error codes
      mLMSErrorManager.clearCurrentErrorCode();

      // Set the flag on the SCO Session to indicate that SetValue() was invoked
      mSCOSession.lmsSetValueCalled();

      String setValue;

      // check to see if the value parameter is null
      String tempValue = String.valueOf(iValue);
      
      if ( tempValue.equals("null") ) 
      {
         setValue = ""; 
      }
      else
      {
         setValue = tempValue;
      }

      // check to see if the value parameter is null
      String tempiElement = String.valueOf(iElement);
      
      if (( tempiElement.equals("null") ) && (tempValue.equals("null"))) 
      {
         iElement = ""; 
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.3"))); 
      }
      else if ( tempValue.equals("null") ) 
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.2", iElement ))); 
      }
      else
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.1", iElement, setValue ))); 
      }

      // True if Initialize has been called
      if ( shallProceed("SetValue") ) 
      {
         mLogger.finest( "Request being processed: SetValue(" + iElement + 
                         "," + setValue + ")" );  
         
         // Process the Set Request
         int dmErrorCode = DMInterface.processSetValue(iElement, setValue, 
                                                      false, mSCOData);

         // Set the LMS Error Manager from the Data Model Error Manager
         mLMSErrorManager.setCurrentErrorCode(dmErrorCode);

         // Will be true if the correct number of arguments exist in the
         // SetValue call
         if ( iValidInput )
         {
            // Warn if cmi.exit equals logout as it's deprecated
            if ((iValue.equals("logout")) && (iElement.equals("cmi.exit")))
            {
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.WARNING, 
               Messages.getString("SCORTETester.214", iElement ))); 
            }
   
            // Terminate has been called
            if ( mSCOSession.isLMSFinished() )
            {
               // Invalid use of data model
               mLogger.finest("SetValue called after Terminate"); 
            
               mLMSErrorManager.setCurrentErrorCode(
                  APIErrorCodes.SET_AFTER_TERMINATE);
               
               writeSetValueErrorToLogs(iElement);
            }
            else if ( dmErrorCode == APIErrorCodes.NO_ERROR )
            {
   
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.PASSED, Messages.getString("SCORTETester.200") )); 
               
               result = "true"; 
   
               mSCOSession.setDataModel( iElement );
               mSCOSession.setDataTransferCall( "SetValue()" ); 
               
            }
            else if ( dmErrorCode == DMErrorCodes.UNDEFINED_ELEMENT )
            {
               
               // Get the first 3 characters from the request - this should
               // map to "cmi." or "adl".  This indicates that we are working
               // with the CMI Data Model.
               String model = iElement.substring(0,4);
   
               // Send these to the LogWriter
               if ( (!( model.equals("cmi."))) && 
                     (!( model.equals("adl.")))) 
               {
                  DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                     MessageType.WARNING, Messages.getString("SCORTETester.162") )); 
   
                  DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                     MessageType.WARNING, Messages.getString("SCORTETester.206", iElement )));  
   
                  DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                     MessageType.WARNING, Messages.getString("SCORTETester.208", result )));  
               }
               else
               {
                  writeSetValueErrorToLogs(iElement);
               }   
            }
            // Any other error code, not already handled - is handled here
            else
            {
               writeSetValueErrorToLogs(iElement);
            }
         }
         else
         {
            // Invalid use of data model
            mLogger.finest("SetValue called with invalid number of arguments");

            mLMSErrorManager.setCurrentErrorCode(
               DMErrorCodes.GEN_SET_FAILURE);

            writeSetValueErrorToLogs(iElement);
         }
      }
      else
      {
         mLogger.finest("LMS Not initialized - Calling API request out " + 
                        "of order"); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.95") )); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.213") )); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.128") )); 

         mSCOSession.setError();

         // Write the Error and Diagnostic data to the Detailed Log
         writeCurrentErrorAndDiagnosticInfo();

         // set the SCO Session SetValueStatus flag to false
         // SetValue() was not used correctly
         mSCOSession.setSetValueStatus(false);
      }

      mLogger.finest("*************************************************"); 
      mLogger.finest("Processing done for SCORTETester::SetValue"); 
      mLogger.finest("*************************************************"); 

      return result;
   }

   /**
    * This method commits all of the data to some persistent means.
    *
    * @param iInParameter String parameter must be the empty string ("").
    * 
    * @param iValidInput Flag indicating a valid input
    * 
    * @return Value ("true" or "false") to indicate success or failure.
    */
   public String Commit( String iInParameter, boolean iValidInput )
   {
      mLogger.finer( "******************************************" ); 
      mLogger.entering( "SCORTETester", "Commit()" );  
      mLogger.finer( "******************************************\n" ); 

      // Clear current error codes
      mLMSErrorManager.clearCurrentErrorCode();

      // check to see if the in parameter is null
      String tempiInParameter = String.valueOf(iInParameter);
      
      if ( tempiInParameter.equals("null") ) 
      {
         // Set to empty string to avoid an exception
         iInParameter = ""; 
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.224") )); 
      }
      else
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.223", iInParameter) )); 
      }

      mSCOSession.lmsCommitCalled();

      // Initialize return value to "false"
      String result = "false"; 
      
      mLogger.finest("In parameter: [" + iInParameter + "]");  

      // If the parameter is valid empty string
      if ( iValidInput )
      {
         // True if Initialize has been called
         if ( shallProceed("Commit") ) 
         {
            // Terminate has been called
            if ( mSCOSession.isLMSFinished() )
            {
               // Invalid use of data model
               mLogger.finest("Commit called after Terminate"); 

               mLMSErrorManager.setCurrentErrorCode(
                  APIErrorCodes.COMMIT_AFTER_TERMINATE);
            
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.FAILED, Messages.getString("SCORTETester.230"))); 

               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.FAILED, Messages.getString("SCORTETester.99", iInParameter )));  

               mSCOSession.setError();

               // Write the Error and Diagnostic data to the Detailed Log
               writeCurrentErrorAndDiagnosticInfo();

               // Change the lmsCommitStatus to false.  SCO used the Commit()
               // API incorrectly
               mSCOSession.setCommitStatus(false);
            }
            else
            {
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.PASSED, Messages.getString("SCORTETester.234"))); 

               mSCOSession.setCommitStatus(true);
               mSCOSession.setDataTransferCall("Commit()"); 

               // Set return value to "true"
               result = "true"; 
            }
         }
         else
         {
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.FAILED, Messages.getString("SCORTETester.95") )); 

            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.FAILED, Messages.getString("SCORTETester.128") )); 

            mSCOSession.setError();

            // Write the Error and Diagnostic data to the Detailed Log
            writeCurrentErrorAndDiagnosticInfo();

            // Change the lmsCommitStatus to false.  SCO used the Commit()
            // API incorrectly
            mSCOSession.setCommitStatus(false);
         }
      }
      else
      {
         // Invalid argument error
         mLMSErrorManager.setCurrentErrorCode(DMErrorCodes.GEN_ARGUMENT_ERROR);

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.230") )); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.240") )); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.99", iInParameter ))); 

         mSCOSession.setError();

         // Write the Error and Diagnostic data to the Detailed Log
         writeCurrentErrorAndDiagnosticInfo();

         // Change the lmsCommitStatus to false.  SCO used the Commit()
         // API incorrectly
         mSCOSession.setCommitStatus(false);
      }

      mLogger.finest( "***********************************************" ); 
      mLogger.finest( "Processing done for SCORTETester::Commit" ); 
      mLogger.finest( "***********************************************\n" ); 

      return result;
   }

   /**
    * Retrieves the error code set by the most recently executed SCORTEDriver
    * function (Note:  Each SCORTEDriver function sets or clears the
    * error code.)
    *
    * @return String - Error code set by the last SCORTEDriver function .
    */
   public String GetLastError()
   {
      mLogger.finer( "******************************************" ); 
      mLogger.entering( "SCORTETester", "GetLastError()" );  
      mLogger.finer( "******************************************\n" ); 

      String value = ""; 
      mSCOSession.lmsGetLastErrorCalled();

      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.INFO, Messages.getString("SCORTETester.251") )); 

      value = mLMSErrorManager.getCurrentErrorCode();

      // check to see if the value parameter is null
      String tempValue = String.valueOf(value);

      if ( tempValue.equals("null") ) 
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.253") )); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.PASSED, Messages.getString("SCORTETester.254") )); 
      }
      else
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.255", value ))); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.PASSED, Messages.getString("SCORTETester.254") )); 
      }

      mSCOSession.setStateMgmtCall("GetLastError()"); 
      mSCOSession.setGetLastErrorStatus(true);

      return value;
   }

   /**
    * This function returns the text associated with an error code.
    *
    * @param iErrorCode The error code to lookup.
    * 
    * @param iValidInput - whether it is valid input i.e. the correct number of parameters
    *
    * @return String The text associated with the errorCode.
    */
   public String GetErrorString( String iErrorCode, boolean iValidInput )
   {
      mLogger.finer( "******************************************" ); 
      mLogger.entering( "SCORTETester", "GetErrorString()" );  
      mLogger.finer( "******************************************\n" ); 

      String value = "";       
      mSCOSession.lmsGetErrorStringCalled();

      // check to see if the value parameter is null
      String tempiErrorCode = String.valueOf(iErrorCode);
      
      if ( tempiErrorCode.equals("null") ) 
      {
         iErrorCode = "";
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.264" )));  
      }
      else 
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.263", iErrorCode )));  
      }

      // Verify that the error code passed in is a valid SCORM Error Code
      boolean validErrorCode = mLMSErrorManager.isValidErrorCode(iErrorCode);

      // Will be true if the correct number of arguments exist in the
      // GetErrorString call
      if ( iValidInput )
      {
         if ( validErrorCode )
         {
            value = mLMSErrorManager.getErrorDescription(iErrorCode);
   
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.INFO, Messages.getString("SCORTETester.265", value ))); 
   
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.PASSED, Messages.getString("SCORTETester.266"))); 
   
            mSCOSession.setGetErrorStringStatus(true);
            mSCOSession.setStateMgmtCall("GetErrorString()"); 
   
         }
         else
         {
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.WARNING, Messages.getString("SCORTETester.268", iErrorCode )));  
            
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.WARNING, Messages.getString("SCORTETester.270") )); 
         }
      }
      else
      {
         // Invalid use of data model
         mLogger.finest("GetErrorString called with invalid number of arguments");

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.271") )); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.272") ));  

         // Fail the session - but do not setCurrentErrorCode for this Support Method
         mSCOSession.setError();

         // set the SCO Session GetErrorStringStatus flag to false
         // GetErrorString() was not used correctly
         mSCOSession.setGetErrorStringStatus(false);
      }

      return value;
   }

   /**
    * This method returns whether the SCO was conformant
    * 
    * @return a boolean indicating whether the SCO was conformant
    */
   public boolean isTestConformant()
   {
      return mTestConformant;
   }
   
   /**
    * This function returns the vendor specific diagnostic text associated
    * with an error code.
    *
    * @param iErrorCode The error code to lookup.
    * 
    * @param iValidInput - whether it is valid input i.e. the correct number of parameters
    *
    * @return String The vendor specific diagnostic text associated with the
    *                errorCode.
    */
   public String GetDiagnostic( String iErrorCode, boolean iValidInput )
   {
      mLogger.finer( "******************************************" ); 
      mLogger.entering( "SCORTETester", "GetDiagnostic()" );  
      mLogger.finer( "******************************************\n" ); 

      boolean validErrorCode = false;
      String value = ""; 
      String err = ""; 
      mSCOSession.lmsGetDiagnosticCalled();

      String tempParm = String.valueOf(iErrorCode);

      if ( tempParm.equals("null") ) 
      {
         iErrorCode = "";
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.278" )));  
      }
      else 
      {
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("SCORTETester.277", iErrorCode )));  
      }

      // Verify that the error code passed in is a valid SCORM Error Code
      validErrorCode = mLMSErrorManager.isValidErrorCode(iErrorCode);
      
      // Will be true if the correct number of arguments exist in the
      // GetDiagnostic call
      if ( iValidInput )
      {
         // No longer fail a SCO if invalid error code passed
         if ( !validErrorCode )
         {
            // Special Case if empty string ("")
            if ( iErrorCode.equals("") ) 
            {
               String lastError = mLMSErrorManager.getCurrentErrorCode();
   
               value = mLMSErrorManager.getErrorDiagnostic(lastError);
               
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.INFO,  Messages.getString("SCORTETester.281", value ))); 
            
               String diagnostic = mLMSErrorManager.getErrorDiagnostic();
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.INFO, Messages.getString("SCORTETester.284", diagnostic)));  

               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.PASSED, Messages.getString("SCORTETester.282") )); 
            }
            else
            {
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.WARNING, Messages.getString("SCORTETester.283", iErrorCode )));  
   
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.WARNING, Messages.getString("SCORTETester.285") )); 
            }
         }
         else
         {
            err = tempParm;
   
            value = mLMSErrorManager.getErrorDiagnostic(err);
            mSCOSession.setStateMgmtCall("GetDiagnostic()"); 
            mSCOSession.setGetDiagnosticStatus(true);
   
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.INFO,  Messages.getString("SCORTETester.281", value ))); 
   
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.PASSED, Messages.getString("SCORTETester.282") )); 
         }
      }
      else
      {
         // Invalid use of data model
         mLogger.finest("GetDiagnostic called with invalid number of arguments");

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.279") )); 

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, Messages.getString("SCORTETester.280") ));  

         // Fail the session - but do not setCurrentErrorCode for this Support Method
         mSCOSession.setError();

         // set the SCO Session GetDiagnosticStatus flag to false
         // GetDiagnostic() was not used correctly
         mSCOSession.setGetDiagnosticStatus(false);
      }

      return value;
   }
   
   /**
    * Set the values as input by the user on the Instructions Screen
    *
    * @param iScoData Re-init, needed for Multi-SCOoc
    */
   public void setSCOValue( SCODataManager iScoData )
   {
      mLogger.finer( "******************************************" ); 
      mLogger.entering( "SCORTETester", "setSCOValue()" ); 
      mLogger.finer( "******************************************\n" );
      
      DMInterface.processSetValue("cmi.learner_id",
                                    (String)mInitData.get(0), true, iScoData);
      DMInterface.processSetValue("cmi.learner_name",
                                    (String)mInitData.get(1), true, iScoData);
      DMInterface.processSetValue("cmi.credit",
                                    (String)mInitData.get(2), true, iScoData);
      DMInterface.processSetValue("cmi.mode",
                                    (String)mInitData.get(3), true, iScoData);
      DMInterface.processSetValue("cmi.launch_data",
                                    (String)mInitData.get(4), true, iScoData);
      setupCommentsFromLMS((String)mInitData.get(5));
      setupObjectiveData((String)mInitData.get(6));
      setupDatamapData((String)mInitData.get(7));
      DMInterface.processSetValue("cmi.scaled_passing_score",
                                    (String)mInitData.get(8), true, iScoData);
      DMInterface.processSetValue("cmi.max_time_allowed",
                                    (String)mInitData.get(9), true, iScoData);
      DMInterface.processSetValue("cmi.time_limit_action",
                                    (String)mInitData.get(10), true, iScoData);
      DMInterface.processSetValue("cmi.completion_threshold",
                                    (String)mInitData.get(11), true, iScoData);
      mLogger.exiting( "SCORTETester", "setSCOValue()" );
   }  

   /**
    * Set the values for the Comments From LMS array as input by the user on the
    * Instructions Screen
    *
    * @param iCommentsFromLMS The values, still in String form.
    */
   private void setupCommentsFromLMS(String iCommentsFromLMS)
   {
      mLogger.finer( "******************************************" );
      mLogger.entering( "SCORTETester", "setupCommentsFromLMS()" );
      mLogger.finer( "******************************************\n" );
      
      if( iCommentsFromLMS.equals("") )
      {
         return;
      }
      int index = 0;

      // "iCommentsFromLMS" contains all of the "Comments From LMS" in one long
      // string that were set in the Test Suite UI.
      // [EOC] Indicates the end of each group of comments.
      // "result" array, by spliting on the End of Comment delimter ([EOC]),
      // holds each group of comments in array form. Loop through the results
      // and Set the Comments From LMS
      String[] result = iCommentsFromLMS.split("(\\[EOC\\])");
      
      for ( int x = 0; x < result.length; x++ )
      {  
         // if result[x] equals only "[1c][2l][3t]" this indicates that they
         // clicked the "Set Comments From LMS" button without entering any
         // data - assume this is unintentional - process record only if
         // something entered
         if ( !result[x].equals("[1c][2l][3t]") )
         {
            String[] result1 = result[x].split("(\\[2l\\])");
            result1[1] = "[2l]" + result1[1];

            // typeIndicator: "1c" indicates comment; "2l" location; "3t"
            // timestamp
            String typeIndicator = result1[0].substring(0,4);
            String value = result1[0].substring(4);
            
            // Call setValueCommentsFromLMS with the value and index
            setValueCommentsFromLMS(index, value, typeIndicator);
            
            String[] result2 = result1[1].split("(\\[3t\\])");
            
            // If length is '1' - indicates that the timestamp box was empty
            if ( result2.length > 1 )
            {
               // Need to add the delimeter back on
               result2[1] = "[3t]" + result2[1];
            }
            
            typeIndicator = result2[0].substring(0,4);
            value = result2[0].substring(4);
   
            // Call setValueCommentsFromLMS with the value, index and result
            setValueCommentsFromLMS(index, value, typeIndicator);
            
            value = "";
            
            // Special case for timestamp due to last element of array
            // if result is less than or equal to 1, indicates nothing entered
            // for timestamp
            if ( result2.length > 1 )
            {
               typeIndicator = result2[1].substring(0,4);
               value = result2[1].substring(4);
            }
            else
            {
               typeIndicator = "[3t]";
            }
   
            // Call setValueCommentsFromLMS with the index, value and
            // typeIndicator
            setValueCommentsFromLMS(index, value, typeIndicator);
         
            index++;
         }// end if(!result[x].equals("[1c][2l][3t]"))
      }// end for loop      
      mLogger.exiting( "SCORTETester", "setupCommentsFromLMS()" );
   }

   /**
    * Calls SetValue for Comments From LMS with the values input by the user on
    * the Instructions Screen
    *
    * @param iIndex The indexed value of the array
    * @param iValue Value being set
    * @param iToken If "1c" setting comment; If "2l" setting location; and if
    *                 "3t" setting timestamp
    *
    */
   private void setValueCommentsFromLMS(int iIndex, String iValue, String iToken)
   {
      mLogger.finer( "******************************************" );
      mLogger.entering( "SCORTETester", "setValueCommentsFromLMS()" );
      mLogger.finer( "******************************************\n" );
      
      if ( iToken.equals("[1c]") )
      {
         DMInterface.processSetValue("cmi.comments_from_lms." + 
                                 iIndex + ".comment", iValue, true, mSCOData);
      }
      else if ( iToken.equals("[2l]") )
      {
         DMInterface.processSetValue("cmi.comments_from_lms." + 
                                 iIndex + ".location", iValue, true, mSCOData);
      }
      else if ( iToken.equals("[3t]") )
      {
         DMInterface.processSetValue("cmi.comments_from_lms." + 
                                 iIndex + ".timestamp", iValue, true, mSCOData);
      }
      
      // If SPM Exceeded, send a Warning
      int numCollections = MessageCollection.getInstance().size();
      int myMessageType = 0;
      String myMessageText = "";

      for ( int i = 0; i < numCollections; i++ )
      {
         LogMessage myMessage =  MessageCollection.getInstance().get(i);
         myMessageType = myMessage.getMessageType();
         myMessageText = myMessage.getMessageText();
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                                        myMessageType, myMessageText));
      }

      //Clears out the message collection
      MessageCollection.getInstance().clear();
      mLogger.exiting( "SCORTETester", "setValueCommentsFromLMS()" );
   }


   /**
    * Set the values for the Objective Data array as input by the user on the
    * Instructions Screen
    *
    * @param iObjectiveData The values, still in String form.
    */
   private void setupObjectiveData(String iObjectiveData)
   {
      mLogger.finer( "******************************************" );
      mLogger.entering( "SCORTETester", "setupObjectiveData()" );
      mLogger.finer( "******************************************\n" );
      
      int index = 0;
      int numOfObjectives = 0;

      // Result array contains all of the Objectives that were set in the Test
      // Suite UI.  Loop through the results and set up Objectives
      
      // [EOO] Indicates the end of each group of Objectives
      
      // "iObjectiveData" contains all of the Objective Data, in one long
      // string, that were set in the Test Suite UI.
      // [EOO] Indicates the end of each group of objectives.
      // "result" array, by spliting on the End of Objectives delimter ([EOO]),
      // holds each group of Objectives in array form. Loop through the results
      // and Set the Objectives
      String[] result = iObjectiveData.split("(\\[EOO\\])");
      
      // Determine Number of Times to Loop - if nothing entered - don't loop
      if ( iObjectiveData.equals("") )
      {
         numOfObjectives = 0;
      }
      else
      {
         numOfObjectives = result.length;
      }

      for ( int x = 0; x < numOfObjectives; x++ )
      {
         String[] result1 = result[x].split("(\\[5c\\])");
         result1[1] = "[5c]" + result1[1];
         
        

         // typeIndicator: "4i" indicates id; "5c" score.scaled; "6u" success_status
         String typeIndicator = result1[0].substring(0,4);
         String value = result1[0].substring(4);
         
         // Call setValueObjectiveData with the value and index
         setValueObjectiveData(index,value,typeIndicator);
         
         String[] result2 = result1[1].split("(\\[6u\\])");
         
         // If length is '1' - indicates that the success_status box was empty
         if ( result2.length > 1 )
         {
            // Need to add the delimeter back on
            result2[1] = "[6u]" + result2[1];
         }
                  
         typeIndicator = result2[0].substring(0,4);
         value = result2[0].substring(4);

         // Call setValueObjectiveData with the value, index and result
         setValueObjectiveData(index,value,typeIndicator);
         
         value = "";
         
         // Special case for success_status due to last element of array
         // if result is less than or equal to 1, indicates nothing entered
         // for success_status
         if ( result2.length > 1 )
         {
            typeIndicator = result2[1].substring(0,4);
            value = result2[1].substring(4);
         }
         else
         {
            typeIndicator = "[6u]"; 
         }

         // Call setValueObjectiveData with the index, value and typeIndicator
         setValueObjectiveData(index,value,typeIndicator);
         
         index++;
      }
      
      mLogger.exiting( "SCORTETester", "setupObjectiveData()" );
   }


   /**
    * Calls SetValue for Objectives with the values input by the user on
    * the Instructions Screen
    *
    * @param iIndex The indexed value of the array
    * @param iValue Value being set
    * @param iToken If "4i" setting id; If "5c" setting score.scaled; and if
    *                 "6u" setting success_status
    */
   private void setValueObjectiveData(int iIndex, String iValue, String iToken)
   {
      mLogger.finer( "******************************************" );
      mLogger.entering( "SCORTETester", "setValueObjectiveData()" );
      mLogger.finer( "******************************************\n" );
      
      if ( iToken.equals("[4i]") )
      {
         DMInterface.processSetValue("cmi.objectives." + 
                                 iIndex + ".id", iValue, true, mSCOData);
      }
      else if ( iToken.equals("[5c]") )
      {
         DMInterface.processSetValue("cmi.objectives." + 
                                 iIndex + ".score.scaled", iValue, true, mSCOData);
      }
      else if ( iToken.equals("[6u]") )
      {
         DMInterface.processSetValue("cmi.objectives." + 
                                 iIndex + ".success_status", iValue, true,  mSCOData);
      }
      // If SPM Exceeded, send a Warning
      int numCollections = MessageCollection.getInstance().size();
      int myMessageType = 0;
      String myMessageText = ""; 

      for ( int i = 0; i < numCollections; i++ )
      {
         LogMessage myMessage =  MessageCollection.getInstance().get(i);
         myMessageType = myMessage.getMessageType();
         myMessageText = myMessage.getMessageText();
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                                        myMessageType, myMessageText));
      }

      //Clears out the message collection
      MessageCollection.getInstance().clear();
      mLogger.exiting( "SCORTETester", "setValueObjectiveData()" );
   }

   /**
    * Set the values for the Datamap data array as input by the user on the
    * Instructions Screen
    *
    * @param iDatamapData The values, still in String form.
    */
   public void setupDatamapData(String iDatamapData)
   {
      mLogger.finer( "******************************************" );
      mLogger.entering( "SCORTETester", "setupDatamapData()" );
      mLogger.finer( "******************************************\n" );
      
      int index = 0;
      int numOfDatamaps = 0;

      // Result array contains all of the Datamaps that were set in the Test
      // Suite UI.  Loop through the results and set up Datamaps
      
      // [EODM] Indicates the end of each group of Datamaps
      
      // "iDatamapData" contains all of the Datamap Data, in one long
      // string, that were set in the Test Suite UI.
      // [EODM] Indicates the end of each group of datamap.
      // "result" array, by spliting on the End of Datamaps delimter ([EODM]),
      // holds each group of Datamaps in array form. Loop through the results
      // and Set the Datamaps
      String[] result = iDatamapData.split("(\\[EODM\\])");
      
      // Determine Number of Times to Loop - if nothing entered - don't loop
      if ( !iDatamapData.equals("") )
      {
         numOfDatamaps = result.length;
      }

      for ( int x = 0; x < numOfDatamaps; x++ )
      {
         // Set the targetID
         String targetID = (result[x].split("\\[6a\\]")[0]).split("\\[5i\\]")[1];
         setValueDatamapData(index, targetID, "[5i]");
         
         // Set the read/write access
         String readWrite = result[x].split("\\[6a\\]")[1];
         setValueDatamapData(index, readWrite, "[6a]");

         // Increment the index
         index++;
      }
      
      mLogger.exiting( "SCORTETester", "setupDatamapData()" );
   }


   /**
    * Calls SetValue for Datamaps with the values input by the user on
    * the Instructions Screen
    *
    * @param iIndex The indexed value of the array
    * @param iValue Value being set
    * @param iToken If "5i" setting id; If "6a" setting read/write access
    */
   private void setValueDatamapData(int iIndex, String iValue, String iToken)
   {
      mLogger.finer( "******************************************" );
      mLogger.entering( "SCORTETester", "setValueDataMapData()" );
      mLogger.finer( "******************************************\n" );
      
      if ( iToken.equals("[5i]") )
      {
         DMInterface.processSetValue("adl.data." + 
                                 iIndex + ".id", iValue, true, mSCOData);
      }
      else if ( iToken.equals("[6a]") )
      {
         DMInterface.processSetValue("adl.data." + 
                                 iIndex + ".store._access", iValue, true, mSCOData);
      }

      // If SPM Exceeded, send a Warning
      int numCollections = MessageCollection.getInstance().size();
      int myMessageType = 0;
      String myMessageText = ""; 

      for ( int i = 0; i < numCollections; i++ )
      {
         LogMessage myMessage =  MessageCollection.getInstance().get(i);
         myMessageType = myMessage.getMessageType();
         myMessageText = myMessage.getMessageText();
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                                        myMessageType, myMessageText));
      }

      //Clears out the message collection
      MessageCollection.getInstance().clear();
      mLogger.exiting( "SCORTETester", "setValueDatamapData()" );
   }

}