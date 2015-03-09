package org.adl.testsuite.rte.sco;

import java.applet.Applet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Vector;
import java.util.logging.Logger;

import netscape.javascript.JSObject;

import org.adl.datamodels.DMErrorCodes;
import org.adl.datamodels.datatypes.DateTimeValidator;
import org.adl.datamodels.datatypes.DurationValidator;
import org.adl.logging.DetailedLogMessageCollection;
import org.adl.logging.SummaryLogMessageCollection;
import org.adl.testsuite.util.AppletList;
import org.adl.testsuite.util.CTSEnvironmentVariable;
import org.adl.testsuite.util.TestSubjectData;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;
import org.adl.util.Messages;
import org.adl.util.debug.LogConfig;

/**
 * <strong>Filename </strong>: SCORTEDriver.java <br>
 * <br>
 * 
 * <strong>Description </strong>: The SCORTEDriver is the test driver for
 * testing an SCO or a group of SCOs for conformance testing. This driver
 * encompasses the SCO launch test, the SCO to LMS API communication test and
 * the data model conformance test. <br>
 * <br>
 * 
 * @author ADL Technical Team
 *  
 */
public class SCORTEDriver extends Applet
{
   /**
    * True if the the sco test is part of a content package
    */
   protected boolean mIsScoInContentPackage = false;
   
   /**
    * The SCORTETester object
    */
   protected SCORTETester mSCORTETester = null;

   /**
    * The portal between the applet and the JavaScript found on the
    * SCOInstructions.htm
    */
   private JSObject mJsroot;

   /**
    * Logger object used for debug logging
    */
   protected static Logger mLogger;

   /**
    * true if the browser is Netscape, false otherwise
    */
   private boolean mIsBrowserNetscape = false;

   /**
    * This attribute holds the value containing the environment variable.  The
    * environment variable is set to the using systems install directory.
    */
   protected String mEnvironmentVariable;

   /**
    * The Timeout Period
    */
   private String mTimeoutPeriod;

   /**
    * The collection of Intialization Data
    */
   private Vector mInitData;
   /**
    * List of SCOs to be tested (may be a single sco)
    */
   private String[] mScoList;
   
   /**
    * the current element of the mScoList being tested, initialized to 0
    */
   protected int mScoCount = 0;
   
   /**
    * total number of SCOs to be evaluated
    */
   private int mNumberOfScos;
   
   /**
    * The type of resource (SCO or ASSET) sent for testing by the CP Test
    * SCO is set by default
    */
   private String mResourceType = "SCO";
 
   /**
    * The applet init method. This method sets up the Data Model Error Manager,
    * Log Writer, SCO Data Manager, and the SCO Session information.
    */
   public void init()
   {      
      // Initialize the JavaScript comm link
      mJsroot = JSObject.getWindow(this);

      // Configure debug logging
      mLogger = Logger.getLogger("org.adl.util.debug.testsuite");
      mEnvironmentVariable = CTSEnvironmentVariable.getCTSEnvironmentVariable();
      LogConfig myLogConfig = new LogConfig();
      myLogConfig.configure(mEnvironmentVariable, false);

      // register this applet in the list of applets running in this classloader
      AppletList.register("APIImplementation", this);      
   }

   /**
    * The applet stop method.
    */
   public void stop()
   {
      // no defined implementation
   }

   /**
    * The applet start method.
    */
   public void start()
   {      
      while ( !AppletList.hasApplet("logInterface") )
      {
         // loops until the applets load to make the continue button visible
      }
      JSObject.getWindow(this);
      mJsroot.eval("loadComplete()");
   }

   /**
    * The applet destroy method.
    */
   public void destroy()
   {
      // no defined implementation
   }

   /**
    * The applet getAppletInfo method.
    * 
    * @return String - The applet info.
    */
   public String getAppletInfo()
   {
      return "Title: SCORTEDriver Implementation \nAuthor: ADLI Project, CTC \n"
         + "The SCORTEDriver is for content SCO testing";
   }

   /**
    * The applet getParameterInfo method
    * 
    * @return String[][] - The parameter info.
    */
   public String[][] getParameterInfo()
   {
      String[][] info = { { "None", "", "This applet requires no parameters." } };
      return info;
   }
   
   /**
    * Takes testing information from the UI and stores it as test subject data,
    * which allows this information to be retrieved at a later time during
    * the test.
    * 
    * @param iDate Date sent in from UI
    * @param iProduct Product name provided by the user
    * @param iVersion Version number provided by the user
    * @param iVendor Vendor/Developer name provided by the user
    */
   public void setTestIDInfo(String iDate, String iProduct, 
                             String iVersion, String iVendor)
   {
      TestSubjectData.getInstance().setDate(Messages.unNull(iDate));
      TestSubjectData.getInstance().setProduct(Messages.unNull(iProduct));
      TestSubjectData.getInstance().setVersion(Messages.unNull(iVersion));
      TestSubjectData.getInstance().setVendor(Messages.unNull(iVendor));
   }


   /**
    * This marks the beginning of the SCO RTE Conformance Testing software. The
    * method sets up the Timeout Period supplied by the tester. Once this is all
    * setup the method then proceeds to launch the first Sharable Content
    * Object.
    * 
    * @param iScoDocument -
    *           The Sharable Content Object Document to use in test
    * @param iTimeoutPeriod -
    *           The Timeout Period supplied by the tester.
    * @param iLearnerId -
    *          The learner id
    * @param iLearnerName - 
    *          The learner name
    * @param iCreditFlag -
    *          the credit flag
    * @param iMode - 
    *          The mode
    * @param iLaunchData -
    *          The launch data
    * @param iCommFrLMSComments -
    *          the comments from lms
    * @param iObjectives - 
    *          the objectives
    * @param iDatamaps - 
    *          the datamaps
    * @param iMasteryscore -
    *          the mastery score
    * @param iMaxtimeallowed - 
    *          the max time allowed
    * @param iTimelimitaction - 
    *          the time limit
    * @param iCompletionThreshold - 
    *          the completion threshold
    */
   public void startTest(String iScoDocument, 
                         String iTimeoutPeriod,
                         String iLearnerId, 
                         String iLearnerName,
                         String iCreditFlag, 
                         String iMode, 
                         String iLaunchData,
                         String iCommFrLMSComments, 
                         String iObjectives, 
			 String iDatamaps,
                         String iMasteryscore,
                         String iMaxtimeallowed, 
                         String iTimelimitaction,
                         String iCompletionThreshold)
   {
      mLogger.entering("SCORTEDriver", "startTest()");
      mTimeoutPeriod = Messages.unNull(iTimeoutPeriod);

      mInitData = new Vector(12);
      mInitData.add(Messages.unNull(iLearnerId));             //0
      mInitData.add(Messages.unNull(iLearnerName));           //1
      mInitData.add(Messages.unNull(iCreditFlag));            //2
      mInitData.add(Messages.unNull(iMode));                  //3
      mInitData.add(Messages.unNull(iLaunchData));            //4
      mInitData.add(Messages.unNull(iCommFrLMSComments));     //5
      mInitData.add(Messages.unNull(iObjectives));            //6
      mInitData.add(Messages.unNull(iDatamaps));              //7
      mInitData.add(Messages.unNull(iMasteryscore));          //8
      mInitData.add(Messages.unNull(iMaxtimeallowed));        //9
      mInitData.add(Messages.unNull(iTimelimitaction));       //10
      mInitData.add(Messages.unNull(iCompletionThreshold));   //11

      if(Messages.unNull(iScoDocument).indexOf(",") == -1)
      {
         if(mScoList == null)
            mScoList = new String[1];
         
         mScoList[0] = Messages.unNull(iScoDocument);
         
         // these next two lines are primarily for SCOs being tested from the
         // Content Package Test. SCOs are sent into this method one at a time
         // and that will not change (because of the info passed in with them).
         // In the interest of NOT stopping the SummaryLogWritingThread we need
         // to keep mScoCount < mNumberOfScos for the check in endTest()
         mScoCount = 0;
         mNumberOfScos = 2;
      }
      else
      {         
         // seperate the comma delimited string into individual SCO strings
         
         mScoList = Messages.unNull(iScoDocument).split(",",0);         
         
         for(int i=0;i<mScoList.length;i++){
            mScoList[i]=mScoList[i].replace('|', ',');            
         }
         
         // get the number of elements in the array
         mNumberOfScos = mScoList.length;
      }

      PrivilegedLogSetup pls = new PrivilegedLogSetup(this, mScoList[mScoCount]);
      AccessController.doPrivileged(pls);

      mLogger.exiting("SCORTEDriver", "startTest()");
   }

   /**
    * tells the driver to end this test and report conformance
    * 
    * @param iReportFullConformance - value of whether full conformance is to
    *                                  be reported
    * @param isPartOfCPTest - true if call was made from ContentPackageDriver,
    * @param iReportTerminateResults true if checking for Terminate call 
    * false if made from SCORTEInstructions
    */

   public void endTest(boolean iReportFullConformance, 
                        boolean isPartOfCPTest, 
                        boolean iReportTerminateResults)
   {
      mLogger.entering("SCORTEDriver", "endTest()");
      mSCORTETester.endTest(iReportFullConformance, iReportTerminateResults);

      // if there are more scos to test, start the test for the next one
      if(mScoCount < mNumberOfScos)
      {
         mLogger.finer("In SCORTEDriver.endTest(): More SCOs to launch, launching next SCO");
         
         // if this is part of a SCO test (non-contentpackage) call step4 in the javascript
         if(!isPartOfCPTest)
            mJsroot.eval("step4()");
      }
      else
      {
         mLogger.finer("In SCORTEDriver.endTest(): No more SCOs to launch, sending ENDLOG message");
         
         // no further scos, send the endlog message
         SummaryLogMessageCollection.getInstance().addMessage( new LogMessage (
            MessageType.ENDLOG,""));
         
         // Test is done, hide the Abort Button and show the check
         mJsroot.eval("showCheck()");
      }
      mLogger.exiting("SCORTEDriver", "endTest()");

   }

   /**
    * Called by SCORTEInstructions when the "Abort Test" button has been 
    * pressed. This makes the call to have the log file's end tags written.
    */
   public void abortSCOTest()
   {
      mLogger.entering("SCORTEDriver", "abortSCOTest()");
      
      try
      {
         // SCO is aborted, send the endlog message
         SummaryLogMessageCollection.getInstance().addMessage( new LogMessage (
            MessageType.ENDLOG,""));
         DetailedLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.ENDLOG, ""));
      }
      catch (NullPointerException npe)
      {
         // Could occur if "Abort Test" pressed before the SCO is launched
         mLogger.severe( "NullPointerException thrown " + npe ); 
      }

      mLogger.exiting("SCORTEDriver", "abortSCOTest()");
   }

   public void setResourceType(String iType)
   {
      mResourceType = iType;
   }
   
   public String getResourceType()
   {
      return mResourceType;
   }
   
   /**
    * allows the JavaScript that launches this Applet to set whether or not the
    * browser is Netscape
    * 
    * @param isnetscape
    *           true if the browser is Netscape, false otherwise
    */
   public void setIsBrowserNetscape(boolean isnetscape)
   {
      mIsBrowserNetscape = isnetscape;
   }

   /**
    * checks if the duration is valid
    * 
    * @param iValue - the value to be evaluated
    * @return boolean of the result of the test
    */
   public boolean checkDurationType(String iValue)
   {
      DurationValidator validator = new DurationValidator();
      return validator.validate(iValue) == DMErrorCodes.NO_ERROR;     
   }

   /**
    * verifies the validity of the time span type
    * 
    * @param iValue - the value to be validated
    * @return whether it was valid or not
    */
   public boolean checkTimeSpanType(String iValue)
   {      
      DateTimeValidator validator = new DateTimeValidator(true);
      return validator.validate(iValue) == DMErrorCodes.NO_ERROR;      
   }

   /**
    * API Initialize call
    * 
    * @param iInParameter - the in parameter
    * @param iValidInput - whether it is a valid input or not
    * @return String of either true or false
    */
   public String Initialize(String iInParameter, boolean iValidInput)
   {
      mLogger.entering("SCORTEDriver", "initialize()");
      return mSCORTETester.Initialize(iInParameter, iValidInput);
   }

   /**
    * API Terminate call
    * 
    * @param iInParameter - the in parameter
    * @param iValidInput - wheter it is a valid input or not
    * @return String of either true or false
    */
   public String Terminate(String iInParameter, boolean iValidInput)
   {
      mLogger.entering("SCORTEDriver", "terminate()");
      return mSCORTETester.Terminate(iInParameter, iValidInput);
   }

   /**
    * API GetValue call
    * 
    * @param iElement - the datamodel element for which value to get
    * @param iValidInput - whether it is valid input i.e. the correct number of parameters
    * @return the value or false if the element is not recognized or write-only
    */
   public String GetValue(String iElement, boolean iValidInput)
   {
      mLogger.entering("SCORTEDriver", "getValue()");
      return mSCORTETester.GetValue(iElement, iValidInput);
   }

   /**
    * API SetValue call
    * 
    * @param iElement - the datamodel element for which value to set
    * @param iValue - the value to be set for said element
    * @param iValidInput - whether it is valid input i.e. the correct number of parameters
    * @return String of either true or false to identify whether the set was
    *          successful or not
    */
   public String SetValue(String iElement, String iValue, boolean iValidInput)
   {
      mLogger.entering("SCORTEDriver", "setValue()");
      return mSCORTETester.SetValue(iElement, iValue, iValidInput);
   }

   /**
    * API Commit call
    * 
    * @param iInParameter - the in parameter
    * @param iValidInput - whether it is a valid input or not
    * @return String of true or false
    */
   public String Commit(String iInParameter, boolean iValidInput)
   {
      mLogger.entering("SCORTEDriver", "commit()");
      return mSCORTETester.Commit(iInParameter, iValidInput);
   }

   /**
    * API GetLastError call
    * 
    * @return returns the last error encountered
    */
   public String GetLastError()
   {
      mLogger.entering("SCORTEDriver", "getLastError()");
      return mSCORTETester.GetLastError();
   }

   /**
    * API GetErrorString call
    * 
    * @param iErrorCode - the error code for which error string is desired
    * @param iValidInput - whether it is valid input i.e. the correct number of parameters
    * @return the error string associated to said error code
    */
   public String GetErrorString(String iErrorCode, boolean iValidInput)
   {
      mLogger.entering("SCORTEDriver", "getErrorString()");
      return mSCORTETester.GetErrorString(iErrorCode, iValidInput);
   }

   /**
    * API GetDiagnostic call
    * 
    * @param iErrorCode - error code for which diagnostic is desired
    * @param iValidInput - whether it is valid input i.e. the correct number of parameters
    * @return the diagnostic associated with the associated error code
    */
   public String GetDiagnostic(String iErrorCode, boolean iValidInput)
   {
      mLogger.entering("SCORTEDriver", "getDiagnostic()");
      return mSCORTETester.GetDiagnostic(iErrorCode, iValidInput);
   }

   /**
    * This method returns whether the SCO was conformant
    * 
    * @return a boolean indicating whether the SCO was conformant
    */
   public boolean isTestConformant()
   {
      return mSCORTETester.isTestConformant();
   }
   
   /**
    * Pass through message to allow the User Interface(UI) to write to logs
    * 
    * @param iLog -
    *           which log to write to
    * @param iMsgType -
    *           type of message based on enumerated list
    * @param iMsgTxt -
    *           the text to be written to the log
    */
   public void doWriteLogEntry(String iLog, String iMsgType, String iMsgTxt)
   {
      if ( mSCORTETester == null )
      {
         initializeSCOTesterClass();
      }
      mSCORTETester.writeLogEntry(Integer.parseInt(Messages.unNull(iLog)), Integer.parseInt(Messages.unNull(iMsgType)), Messages.unNull(iMsgTxt));
   }
   
   /**
    * allows the boolean mIsScoInContentPackage to be set
    * @param iCP used to set mIsScoInContentPackage
    */
   public void setIsScoPartOfCP(boolean iCP)
   {
      mIsScoInContentPackage = iCP;
   }

   /**
    *  Initializes the sco tester class
    */
   protected void initializeSCOTesterClass()
   {
      PrivilegedInitSCOTestClass pitc = new PrivilegedInitSCOTestClass();
      AccessController.doPrivileged(pitc);
   }

   public void doFileOutput(String iPath, String iTxt)
   {
      //System.out.println(iPath + " : " + iTxt);
      PrivilegedFileOutput pfo = new PrivilegedFileOutput(this,Messages.unNull(iPath),Messages.unNull(iTxt));
      AccessController.doPrivileged(pfo);
   }
   
   public String doFileInput(String iPath)
   {      
      PrivilegedFileInput pfi = new PrivilegedFileInput(this,iPath);
      AccessController.doPrivileged(pfi);
      return pfi.getFileTxt();
   }
   
   /**
    * This inner class is used to grant permission to the code in this applet to
    * allow the deleting of the temparary files from the local hard disk.
    */
   private class PrivilegedLogSetup implements PrivilegedAction
   {
      /**
       * The applet registered
       */
      private Applet mApplet = null;

      /**
       * The SCO document
       */
      private String scoDocument;

      /**
       * Used to grant permission to the code in this applet to allow read/write
       * to files on the local disk.
       * 
       * @param iApplet The applet registered
       * @param iScoDocument The SCO document

       */
      PrivilegedLogSetup(Applet iApplet, String iScoDocument)
      {
         mApplet = iApplet;
         scoDocument = iScoDocument;
      }

      /**
       * 
       * Performs the computation. This method will be called by
       * <code>AccessController.doPrivileged</code> after enabling privileges.
       * 
       * @return a class-dependent value that may represent the results of the
       *         computation. Each class that implements
       *         <code>PrivilegedAction</code> should document what (if
       *         anything) this value represents.
       * 
       * @see AccessController#doPrivileged(PrivilegedAction)
       *  
       */
      public Object run()
      {
         mLogger.entering("SCORTEDriver.PrivilegedLogSetup", "run()");
         if ( mSCORTETester == null )
         {   
            initializeSCOTesterClass();
         }  

         mSCORTETester.setResourceType(mResourceType);
         
         mSCORTETester.initializeSCOTester(mJsroot, scoDocument, mIsScoInContentPackage );
            
         mSCORTETester.validateSCO(scoDocument, mTimeoutPeriod, mInitData);
         
         mScoCount++;        

         mLogger.exiting("SCORTEDriver.PrivilegedLogSetup", "run()");
         return null;
      }
      
   }// end inner class

   /**
    * Does privileged initialization of test class
    */
   private class PrivilegedInitSCOTestClass implements PrivilegedAction
   {

      /**
       * Constructor of the inner class
       *
       */
      PrivilegedInitSCOTestClass()
      {
         //left empty intentionally
      }

      /**
      *
      * Performs the computation.  This method will be called by
      * <code>AccessController.doPrivileged</code> after enabling privileges.
      *
      * @return a class-dependent value that may represent the results of the
      *        computation. Each class that implements
      *         <code>PrivilegedAction</code>
      *        should document what (if anything) this value represents.
      *
      * @see AccessController#doPrivileged(PrivilegedAction)
      *
      */
      public Object run()
      {
         mSCORTETester = new SCORTETester(mEnvironmentVariable);
         return null;
      }
   }
   
   
   /**
    * This inner class is used to grant permission to the code in this applet to
    * allow the saving of text files on the local hard disk.
    */
   private class PrivilegedFileOutput implements PrivilegedAction
   {
      /**
       * The applet registered
       */
      private Applet mApplet = null;

      /**
       * The Text document
       */
      private String mTxtDocument;

      /**
       * The Text file
       */
      private File mFile;
      
      /**
       * Used to grant permission to the code in this applet to allow read/write
       * to files on the local disk.
       * 
       * @param iApplet The applet registered
       * @param iFile The SCO document

       */
      PrivilegedFileOutput(Applet iApplet, String iFile, String iTxtDocument)
      {
         mFile = new File(iFile);
         mApplet = iApplet;
         mTxtDocument = iTxtDocument;
      }

      /**
       * 
       * Performs the computation. This method will be called by
       * <code>AccessController.doPrivileged</code> after enabling privileges.
       * 
       * @return a class-dependent value that may represent the results of the
       *         computation. Each class that implements
       *         <code>PrivilegedAction</code> should document what (if
       *         anything) this value represents.
       * 
       * @see AccessController#doPrivileged(PrivilegedAction)
       *  
       */
      public Object run()
      {
         
         mLogger.entering("SCORTEDriver.PrivilegedFileIO", "run()");
         try {
            
            BufferedWriter fileBW = new BufferedWriter(new FileWriter(mFile));
            if (mFile.toString().endsWith(".txt"))
            {
               String[] lines = mTxtDocument.split(",");                         
               for(int i=0; i<lines.length;i++){
                  lines[i] = lines[i].replace('|',',');
                  
                  fileBW.write(lines[i]);
                  if (i != lines.length){
                     fileBW.newLine();
                  }
               }
            }
            fileBW.close();
       }
       catch (Exception e) {
        e.printStackTrace();
       }
         

                

         mLogger.exiting("SCORTEDriver.PrivilegedFileOoutput", "run()");
         return null;
      }
      
   }// end inner class
   
   
   /**
    * This inner class is used to grant permission to the code in this applet to
    * allow the loading of text files on the local hard disk.
    */
   private class PrivilegedFileInput implements PrivilegedAction
   {
      /**
       * The applet registered
       */
      Applet mApplet = null;

      /**
       * The Text document
       */
      String mTxtDocument="";

      /**
       * The Text file
       */
      File mFile;
      
      /**
       * Used to grant permission to the code in this applet to allow read/write
       * to files on the local disk.
       * 
       * @param iApplet The applet registered
       * @param iFile The SCO document

       */
      PrivilegedFileInput(Applet iApplet, String iFile)
      {
         mFile = new File(iFile);
         mApplet = iApplet;        
      }

      /**
       * 
       * Performs the computation. This method will be called by
       * <code>AccessController.doPrivileged</code> after enabling privileges.
       * 
       * @return a class-dependent value that may represent the results of the
       *         computation. Each class that implements
       *         <code>PrivilegedAction</code> should document what (if
       *         anything) this value represents.
       * 
       * @see AccessController#doPrivileged(PrivilegedAction)
       *  
       */
      public Object run()
      {
         mTxtDocument = "";
         String line= "";
         mLogger.entering("SCORTEDriver.PrivilegedFileInput", "run()");
         try {
            
            BufferedReader fileBR = new BufferedReader(new FileReader(mFile));
            //we don't want the chance that someone might try to edit a .exe or some such file. 
            if (mFile.toString().endsWith(".txt")){
               while(fileBR.ready())
               {               
                  line = fileBR.readLine();
                  if (line != "")//ignore blank lines of the txt file
                  {
                     mTxtDocument += line.replace(',', '|') + ',';
                  }
               }            
            }
            fileBR.close();     
       }
       catch (Exception e) {
          e.printStackTrace();
       }
         
         mLogger.exiting("SCORTEDriver.PrivilegedFileInput", "run()");
         return null;
      }
      
      public String getFileTxt(){        
         return mTxtDocument;
      }     
      
   }// end inner class
   
}
