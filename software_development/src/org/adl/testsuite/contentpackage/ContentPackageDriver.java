package org.adl.testsuite.contentpackage;


import java.applet.Applet;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import netscape.javascript.JSObject;

import org.adl.util.Messages;
import org.adl.logging.DetailedLogMessageCollection;
import org.adl.logging.LoggingMessages;
import org.adl.logging.SummaryLogMessageCollection;
import org.adl.testsuite.util.AppletList;
import org.adl.testsuite.util.CTSEnvironmentVariable;
import org.adl.testsuite.util.ConformanceLabel;
import org.adl.testsuite.util.TestSubjectData;
import org.adl.testsuite.util.VersionHandler;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;
import org.adl.testsuite.checksum.ADLPackageChecksum;
import org.adl.util.debug.LogConfig;
import org.adl.testsuite.contentpackage.util.validator.DataMapData;
import org.adl.testsuite.contentpackage.util.validator.LaunchData;
import org.adl.testsuite.contentpackage.util.validator.ValidatorResult;


/**
 * <strong>Filename:</strong><br>
 * ContentPackageDriver.java<br><br>
 *
 * <strong>Description:</strong><br>
 * The <code>ContentPackageDriver</code> is the communication handler between
 * the Javascript code of the HTML and the actual content package test.<br><br>
 *
 * @author ADL Technical Team
 */

public class ContentPackageDriver extends Applet
{
   /**
    * The overall results of ADL Validation
    */
   protected ValidatorResult mADLValidatorOutcome;

   /**
    * The name and location of the test subject to be tested.  This is a zip
    * file if the type is a pif or it is a xml file if the type is non-pif.
    */
   protected String mTestSubjectFile;

   /**
    * The type of test subject being tested.  The options are "pif" or "non-pif"
    */
   protected String mTestSubjectType;

   /**
    * Content package tester object used as the pass through to the validator
    */
   protected ContentPackageTester mContentPackageTester;

   /**
    * String containing the environment variable value
    */
   protected String mEnvironmentVariable;
      
   /**
    * true if the browser is Netscape, false otherwise
    */
   protected boolean mIsBrowserNetscape = false;
   
   /**
    * Logger object used for debug logging
    */
   final static protected Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");;
   
   /**
    * String containing the objectives list
    */
   private String mObjectivesList;

   /**
    * List containing the data store list
    */
   private List mDataMapDataList;
   
   /**
    * String containing the time limit action
    */
   private String mTimeLimitAction;

   /**
    * String containing the data from lms value
    */
   private String mDataFromLMS;
   /**
    * String containing the completion threshold
    */
   private String mCompletionThreshold;
   
   /**
    * List of LaunchData records
    */
   private Vector mLaunchDataList;

   /**
    * The index to the next Launch Data record in the mLaunchDataList Vector
    */
   private int mNextLaunchData;
   
   /**
    * Holds the conformance of the Scos
    */
   private boolean mSCOsConformant = true;
   
   /**
    * Holds the checksum value of the package
    */
   private long mChecksumValue = 0;
   
   /**
    * Holds the boolean indicated CP or manifest test
    */
   private boolean mManifestOnly = false;
   
   /**
    * The Default Constructor<br>
    */
   public ContentPackageDriver()
   {
      // Configure debug logging
      LOGGER.entering("ContentPackageDriver()","ContentPackageDriver()");
      mEnvironmentVariable = CTSEnvironmentVariable.getCTSEnvironmentVariable();

      LogConfig myLogConfig = new LogConfig();
      myLogConfig.configure( mEnvironmentVariable, false );

      // initialize the outcome class to null
      mADLValidatorOutcome = new ValidatorResult();

      // initialize the list to hold SCO Launch Data
      mLaunchDataList = new Vector();

      mNextLaunchData = 0;

      mTestSubjectFile = ""; 
      mTestSubjectType = ""; 

      //clean up the temp directory that was created for pif extraction
      File packageDir = new File(mEnvironmentVariable + File.separator + 
            "PackageImport"); 
      
      deleteDir( packageDir );
      LOGGER.exiting("ContentPackageDriver()", "ContentPackageDriver()");
   }
   
   /**
    * overwritten init() function
    * 
    * used to register this applet in an applet registry to allow the software
    * to access it at a later time
    */
   public void init()
   {
      AppletList.register("contentPackageDriver",this);
      if ( mContentPackageTester == null )
      {
         intializeTesterClass();
      }
   }
   
   /**
    * Overwritten start() function used to activate the continue button is step 
    * one after the log applet loads
    */
   public void start()
   {
      while ( !AppletList.hasApplet("logInterface") )
      {
         // loops until the applets load to make the continue button visiable
      }
      JSObject mJsroot = JSObject.getWindow(this);
      mJsroot.eval("loadComplete()");

   }
   
   /**
    * information about the applet. is used when "getAppletInfo()" is called
    * @return String of the applet info - Title: + Author: + Name: + Description
    */
   public String getAppletInfo()
   {
      return "Title: ContentPackageDriver \n" +
             "Author: ADLI Project, CTC \n" +
             "Name: contentPackageDriver \n" +
             "The contentPackageDriver is for connecting to the " +
             "Content Package Instructions. This provides an ability to " +
             "communicate between the web UI and the business logic";
   }

   /**
    *
    * The applet <code>deleteDir()</code> method deletes the package Import
    * directory to ensure that no old data exists when testing
    *
    * @param iDir dirctory to delete
    * @return whether the deletion of the folder was successful
    */
    public static boolean deleteDir(File iDir)
    {
       if ( iDir.isDirectory() )
       {
          String[] children = iDir.list();
          for ( int i=0; i<children.length; i++ )
          {
             boolean success = deleteDir(new File(iDir, children[i]));
             if (!success)
             {
                return false;
             }
          }
       }

       // The directory is now empty so delete it
       return iDir.delete();
    }

   /**
    *
    * This method is the entry point to the Test Suite modules test.
    *
    * @param iTestSubjectFile - The name and location of the test subject to be
    *                           tested.  This is a zip file if the type is a pif
    *                           or it is a xml file if the type is non-pif.
    *
    * @param iTestSubjectType - The type of test subject being tested.  The
    *                           options are "pif" or "non-pif"
    *
    * @param iApplicationProfileType - The application profile rules to be
    *                                  tested against.  The options are
    *                                  "contentaggregation" or "resource".
    *
    * @param iExtensionSchemaLocation - The schema location value of the
    *                                   extended elements.
    *
    * @param iTestMetadata - Boolean value directing the test to test the
    *                        Metadata contained in the package.  Note:  If this
    *                        value is not true, then the package can not obtain
    *                        a conformance label and will be deemed
    *                        non-conformant overall
    *
    * @param iTestSCOs - Boolean value directing the test to test the SCOs
    *                    contained in the package.  Note:  If this value is not
    *                    true, then the package can not obtain a conformance
    *                    label and will be deemed non-conformant overall.
    *
    * @param iTestManifestOnly -
    *           The boolean describing whether or not the IMS Manifest is to be
    *           the only subject validated.  True implies that validation occurs
    *           only on the IMS Manifest (checks include wellformedness, schema
    *           validation, and application profile checks).  False implies
    *           that the entire Content Package be validated (IMS Manifest
    *           checks with the inclusion of the required files checks,
    *           metadata, and sco testing).
    *
    */
   public void startValidateTest( String iTestSubjectFile,
                                  String iTestSubjectType,
                                  String iApplicationProfileType,
                                  String iExtensionSchemaLocation,
                                  boolean iTestMetadata,
                                  boolean iTestSCOs,
                                  boolean iTestManifestOnly)
   {

      // set the test options attributes
      mTestSubjectFile = Messages.unNull(iTestSubjectFile);
      mTestSubjectType = Messages.unNull(iTestSubjectType);
      
      PrivilegedCPTest ps = new PrivilegedCPTest(Messages.unNull(iApplicationProfileType),
                                                 Messages.unNull(iExtensionSchemaLocation),
                                                 iTestMetadata,
                                                 iTestSCOs,
                                                 iTestManifestOnly,
                                                 mIsBrowserNetscape);
      AccessController.doPrivileged(ps);
   }
   
   /**
    * allows the JavaScript that launches this Applet to set whether or not the
    * browser is Netscape
    * 
    * @param isnetscape true if the browser is Netscape, false otherwise
    */
   public void setIsBrowserNetscape(boolean isnetscape)
   {
      mIsBrowserNetscape = isnetscape;      
   }
   
   /**
    * Returns the objective list to the instruction page for SCO init
    * Must be called in instruction page after getNextLaunchLine()
    *
    * @return String - objectivesList
    *
    */
   public String getObjectivesList()
   {
      return mObjectivesList;
   }

   /**
    * Returns the time limit action to the instruction page for SCO init
    * Must be called in instruction page after getNextLaunchLine()
    *
    * @return String - timeLimitAction
    *
    */
   public String getTimeLimitAction()
   {
      return mTimeLimitAction;
   }

   /**
    * Returns the data from LMS to the instruction page for SCO init
    * Must be called in instruction page after getNextLaunchLine()
    *
    * @return String - dataFromLMS
    *
    */
   public String getDataFromLMS()
   {
      return mDataFromLMS;
   }

   /**
    * Returns the time completionthreshold to the instruction page for SCO init
    * Must be called in instruction page after getNextLaunchLine()
    *
    * @return String - completionThreshold
    *
    */
   public String getCompletionThreshold()
   {
      return mCompletionThreshold;
   }

   /**
    * This method gives access to the next SCO LauchLine in the mLaunchLineList
    * based on the current state of the mNextLaunchData attribute.
    *
    * @return String representation of the absolute launch line of the next
    *         SCO and its type.
    */
   public String getNextLaunchLine()
   {
      String result = "";
      String type = "";
      String packageLocation = "";
      LaunchData launchData = getNextLaunchData();

      //Sets parameters, to be returned to the instructions page
      mObjectivesList = launchData.getObjectivesList();
      mTimeLimitAction = launchData.getTimeLimitAction();
      mDataFromLMS = launchData.getDataFromLMS();
      mCompletionThreshold = launchData.getCompletionThreshold();
      mDataMapDataList = launchData.getDataMapDataList();
      
      if ( launchData != null)
      {
            packageLocation = mEnvironmentVariable + File.separator +
                              "PackageImport"; 

         type = launchData.getSCORMType();
         
         result = packageLocation + File.separator + launchData.getLaunchLine() + "~" + type;

         if ( result.indexOf("/") != -1 )
         {
            // Replace forward slash with a backslash to make it consistent
            // with the rest of the launch line
            result = LoggingMessages.replace(result, "/", "\\");
         }
      }
      
      return result;
   }

   /**
    * Returns a string describing if more scos need to be tested.
    * 
    * @return String indicating whether or not all there are more SCOs to test
    */
   public String allScosTested()
   {     
      if ( mNextLaunchData >= mLaunchDataList.size() )
      {         
         return "true"; 
      }

      return "false";
   }

   /**
    * Returns a boolean describing if scos have been found in the package.
    * True implies they have been found, false implies no scos exist.
    *
    * @return Boolean value indicating whether or not there are SCOs to test.
    */
   public boolean doSCOsExist()
   {
      if ( mLaunchDataList.isEmpty() )
      {
         return false;
      }

      return true;
   }
   
   /**
    * This method writes the header section of the long
    * 
    * @param iMsg - message to be written to the log
    * @param iFileLocation - file location to be used in the log message
    */
   public void writeHeaderInfo(String iMsg, String iFileLocation)
   {
      SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.SUBLOGHEAD, Messages.unNull(iMsg) +  Messages.unNull(iFileLocation)));
      SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.OTHER, ""));
   }
   
   /**
    * This method will obtain the objectives data for the given resource
    * 
    * @return - A String of objectives data to be used by SCODriver
    */
   public String getObjectivesData()
   {
      String[] temp = mObjectivesList.split(",");
      final StringBuffer objectivesData = new StringBuffer();

      for (int j=0;j < temp.length; j++)
      {
         if (temp[j] != "")
         {
            objectivesData.append("[4i]" + temp[j] + "[5c][6u][EOO]");
         }
      }
      
      return objectivesData.toString();
   }
   
   /**
    * This method will obtain the data store information for the given resource
    * 
    * @return - A String of Data Data Store information to be used by SCODriver
    */
   public String getDataData()
   {
      // Create the Data Map String
      final StringBuffer dataData = new StringBuffer();
      Iterator datamapIter = mDataMapDataList.iterator();
      while ( datamapIter.hasNext() )
      {
         DataMapData data = (DataMapData) datamapIter.next();
         dataData.append("[5i]" + data.getTargetID() + "[6a]" + data.getReadSharedData() + "<>" + data.getWriteSharedData() + "[EODM]");
      }
      
      return dataData.toString();
   }

   /**
    * This method replaces all white space with the '%20'.
    * 
    * @param iSchemaFileName the schema file name
    *
    * @return String that has been updated to replace whitespace with '%20' 
    */
   public String replaceWhiteSpace( String iSchemaFileName )
   {
      return iSchemaFileName.replaceAll( " ", "%20");  
   }
   
   /**
    * Pass through message to allow UI to write to logs
    * 
    * @param iLog - which log to write to
    * @param iMsgType - type of message based on enumerated list
    * @param iMsgTxt - the text to be written to the log
    */
   public void doWriteLogEntry(String iLog, String iMsgType, String iMsgTxt)
   {
      mContentPackageTester.writeLogEntry(Integer.parseInt(Messages.unNull(iLog)),
                                          Integer.parseInt(Messages.unNull(iMsgType)),Messages.unNull(iMsgTxt));
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
    * This completes the testing process and ties up all loose ends by
    * calling calling the conformance label method
    * and the cleanTempImportDirectory() method of the validator.
    * 
    * @param iDoSCOTest identifies if the sco test was performed
    * @param iDoMDTest identifies if the metadata test was performed
    * @param iSupressLabel identifies if we want to supress the label
    */
   public void completeTest( boolean iDoSCOTest, boolean iDoMDTest, boolean iSupressLabel )
   {
      LOGGER.entering("ContentPackageDriver", "completeTest()");
      LOGGER.finest("parameters coming in are..." + 
         "\niDoSCOTest: " + iDoSCOTest + "\niDoMDTest: " + iDoMDTest);
      // determine if the content package is conformant, if manifest is 
      // conformant, all metadata is conformant and all SCOs are conformant 
      // then the content package is conformant.
      boolean conformantPackage = isManifestConformant() &&
                                  isMetadataConformant() &&
                                  mSCOsConformant;

      
      if ( !iSupressLabel )
      {
         // display the content package conformance label
         displayConformanceLabel( conformantPackage, iDoSCOTest, iDoMDTest );
   
         // display the conformance to the SCORM Books
         // but only if we are not dealing with manifest utility
         if ( iDoMDTest && iDoSCOTest )
         {
            displaySCORMBookConformance( isManifestConformant(),
                                         isMetadataConformant(),
                                         mSCOsConformant );
         }
      }

      String certText; 
      if ( iDoSCOTest && iDoMDTest )
      {
         certText = VersionHandler.getCertStmt();
      }
      else
      {
         certText = VersionHandler.getCertStmtUtil();
      }

      LOGGER.info( "CERT STMT: " + certText ); 
      SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.SUBLOGHEAD, certText));

      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.OTHER, certText));
      
      // Only write the Checksum if it is a conformant CP Test
      if ( !mManifestOnly && conformantPackage && !iSupressLabel )
      {
         // add a seperator in the log
         SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
            MessageType.OTHER, Messages.getString("ContentPackageTester.36") ));
         
         // Write checksum information to logs
         SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.CHECKSUM, "Checksum Value: " + Long.toString(mChecksumValue)));
      }
      
      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.ENDLOG, ""));
      
      SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.ENDLOG, ""));
      
      mContentPackageTester.testComplete();
      
      // clean up the temp directory that was created for pif extraction
      tempDirCleanup( mEnvironmentVariable + File.separator + "PackageImport" ); 
   }

   /**
    * Called from the Instructions page.  This method writes the end
    * tags to the log files.
    */
   public void abortCPTest()
   {
      LOGGER.entering("ContentPackageDriver", "abortCPTest()");
      SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.ENDLOG,""));
      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.ENDLOG, ""));
      LOGGER.exiting("ContentPackageDriver", "abortCPTest()");
   }
   
   /**
    * Gives access to the overall outcome of the manifest conformance test.
    *
    * @return - Boolean representing the overall outcome of the manifest
    *           conformance test.
    */
   public boolean isManifestConformant()
   {
      return mADLValidatorOutcome.getManifestOutcome();
//      boolean result = false;
//      if ( !mADLValidatorOutcomeList.isEmpty() )
//      {
//         // first object in the vector is the manifest outcome object
//         Boolean manifestOutcome =
//                   (Boolean)mADLValidatorOutcomeList.elementAt( 1 );
//
//         result = manifestOutcome.booleanValue();
//      }
//      return result;
   }
   
   /**
    * Determines if label should be printed
    * 
    * @return boolean indicating if label should be shown
    */
   public boolean isLabelShown()
   {
      if ( mADLValidatorOutcome.getSubmanifestReferenced() || 
             mADLValidatorOutcome.getExternalFileReferenced() )
      {
         return false;
      }
      else
      {
         return true;
      }
   }   

   /**
    * Gives access to the overall outcome of the metadata conformance found
    * in the package.
    *
    * @return - Boolean representing the overall outcome of the metadata
    *           conformance test.
    */
   public boolean isMetadataConformant()
   {
      boolean result = true;
      // first object in the vector is the manifest outcome object, remainder
      // is metadata outcome objects
      int size = mADLValidatorOutcome.getMetadataOutcomes().size();
      for ( int i = 2; i < size; i++ )
      {
         Boolean currentMetadataOutcome =
                  (Boolean)mADLValidatorOutcome.getMetadataOutcomes().get(i);

         result = currentMetadataOutcome.booleanValue();
      }
      return result;
   }
   
   /**
    * Sets the conformance for the SCOs
    * 
    * @param iScoConformance - true if scos were conformant
    */
   public void setSCOConformance(boolean iScoConformance)
   {
      // We must make sure once it goes false, it stays false
      mSCOsConformant = mSCOsConformant && iScoConformance;
   }
   
   /**
    * Intializes tester class
    */
   private void intializeTesterClass()
   {
      PrivilegedInitTestClass pitc = new PrivilegedInitTestClass();
      AccessController.doPrivileged(pitc);      
   }
   
   /**
    * This method is used to display the begining SCO messages to the log to
    * setup for the SCO test integration.
    */
   private void setupSCOTest()
   {
      LOGGER.entering("ContentPackageTester", "setupSCOTest()");

      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.SUBLOGTITLE, Messages.getString("ContentPackageTester.53")));

      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.SUBLOGHEAD, Messages.getString("ContentPackageTester.54")));

      // set the list of SCOs referenced to test the SCOs <-- What is this here for?
      LOGGER.exiting("ContentPackageTester", "setupSCOTest()");
   }
   
   /**
    * Method used to assign the mLaunchDataList attribute a Vector value
    * 
    * @param iLaunchData - Vector to be assigned to mLaunchDataList attribute
    * @param iDoSCOTest - Boolean indicating whether or not there are SCOs
    *                     to test.
    */
   protected void setLaunchDataList( Vector iLaunchData, boolean iDoSCOTest )
   {      
      mLaunchDataList = iLaunchData;
      
      setupSCOTest();

      if ( mLaunchDataList.isEmpty() && iDoSCOTest )
      {
         LOGGER.info( "The Package does not contain SCOs" ); 
         SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.INFO, Messages.getString("ContentPackageDriver.5")));

         // add a seperator in the log
         SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
            MessageType.OTHER, Messages.getString("ContentPackageTester.36") ));
      }
      else
      {
         // add a seperator in the log
         SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
            MessageType.OTHER, Messages.getString("ContentPackageTester.36") ));
      }
   }
   
   /**
    * This method removes the PackageImport folder and all its contents when
    * close of the applet
    *
    * @param iPath - The absolute path of the temp director to clear
    */
   private void tempDirCleanup( String iPath )
   {
      PrivilegedTempDirCleanup ps = new PrivilegedTempDirCleanup( iPath );
      AccessController.doPrivileged(ps);
   }
   
   /**
    * Display the Content Package conformance result to the Test Suite log.
    *
    * @param iValidPackage - Boolean representing the overall outcome of the
    *                        content package conformance test.
    * @param iDoSCOTest - Boolean indicating whether or not there are SCOs
    *                     to test.
    * @param iDoMDTest -  Boolean indicating whether or not there is metadata
    *                     to test.
    */
   private void displayConformanceLabel( boolean iValidPackage,
                                         boolean iDoSCOTest,
                                         boolean iDoMDTest )
   {
      String msgText = ""; 

      LOGGER.entering( "ContentPackageDriver", "displayConformanceLabel()" );
      LOGGER.finest("parameters coming in are..." + 
         "\niValidPackage: " + iValidPackage +
         "\niDoSCOTest: " + iDoSCOTest +
         "\niDoMDTest: " + iDoMDTest);
      
      SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.SUBLOGTITLE, Messages.getString("ScormConformance.1") )); 

      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.OTHER, Messages.getString("ContentPackageTester.36")));
      
      DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.OTHER, Messages.getString("ScormConformance.1") ));      
      
      if ( iDoSCOTest && iDoMDTest )
      {
         // only report overall conformance for CP Test
         // CP test is the only place where doSCOTest and doMDTest are true

         if ( iValidPackage )
         {
            msgText = ConformanceLabel.getConformanceText( ConformanceLabel.SCORM2004,
                                                           "Content Package" ); 
            LOGGER.info( "CONFORMANT: " + msgText ); 
            SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.CONFORMANT, msgText ));
            
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.CONFORMANT, msgText ));            
         }
         else
         {
            msgText = ConformanceLabel.getConformanceText(
               ConformanceLabel.NONCONFORMANT, "Content Package" ); 
            
            LOGGER.info( "NONCONFORMANT: " + msgText ); 
            SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.FAILED, msgText ));
            
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.FAILED, msgText ));            
         }
      }
      else
      {
         // report manifest utility test results
         if ( iValidPackage )
         {
            msgText = ConformanceLabel.getConformanceText(
               ConformanceLabel.MANIFEST1, "IMS Manifest" ); 
            
            LOGGER.info( "CONFORMANT: " + msgText ); 
            SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.CONFORMANT, msgText));
            
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.CONFORMANT, msgText));            
         }
         else
         {
            msgText = ConformanceLabel.getConformanceText(
                                                  ConformanceLabel.MANIFEST0,
                                                  "IMS Manifest" );
            LOGGER.info( "NONCONFORMANT: " + msgText ); 
            SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.FAILED, msgText ));
            
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.FAILED, msgText ));
         }
      }
      LOGGER.exiting( "ContentPackageDriver", "displayConformanceLabel()" );
   }

    /**
    * Display the conformance to the Test Suite log of the SCORM Books.
    *
    * @param iManifestValid - Boolean representing the outcome of the
    *                         manifest validation.
    * @param iMetadataValid - Boolean representing the outcome of the
    *                         metadata found in the content package.
    * @param iSCOsValid - Boolean representing the outcome of the
    *                     scos found in the content package.
    *
    */
   private void displaySCORMBookConformance( boolean iManifestValid,
                                             boolean iMetadataValid,
                                             boolean iSCOsValid )
   {
      LOGGER.entering("ContentPackageDriver", "displaySCORMBookConformance()");
      LOGGER.finest("parameters coming in are..." +
                     "\niManifestValid: " + iManifestValid + "\niMetadataValid: " +
                      iMetadataValid + "\niSCOsValid: " + iSCOsValid);
      
      String msgText; 
      
      if ( iManifestValid && iMetadataValid )
      {
         // display CAM book conformance
         msgText = ConformanceLabel.getConformanceText( ConformanceLabel.CPCAM1,
                                                        "Content Package" ); 
         LOGGER.info( "CONFORMANT: " + msgText ); 
         SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.CONFORMANT, msgText));

         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.CONFORMANT, msgText));         
      }
      else
      {
         msgText = ConformanceLabel.getConformanceText( ConformanceLabel.CPCAM0,
                                                        "Content Package" ); 
         LOGGER.info( "NON-CONFORMANT: " + msgText ); 
         SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, msgText));
         
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.FAILED, msgText));         
      }

      if ( doSCOsExist() && iManifestValid )
      {
         if ( iSCOsValid )
         {
            // display RTE book conformance
            msgText = ConformanceLabel.getConformanceText(
               ConformanceLabel.CPRTE1,"Content Package" );
            
            LOGGER.info( "CONFORMANT: " + msgText ); 
            
            SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.CONFORMANT, msgText));
            
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.CONFORMANT, msgText));            
         }
         else
         {
            // display RTE book non-conformance
            msgText = ConformanceLabel.getConformanceText(
               ConformanceLabel.CPRTE0, "Content Package" );
            
            LOGGER.info( "NON-CONFORMANT: " + msgText );
            
            SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.FAILED, msgText));
            
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
               MessageType.FAILED, msgText));
         }
      }
      LOGGER.exiting("ContentPackageDriver", "displaySCORMBookConformance()");
   }

   /**
    * This method gives access to a particular LaunchData instance from the
    * mLaunchDataList based on the current state of the mNextLaunchData
    * attribute.  The mNextLaunchData will be incremented upon return of the
    * instance.
    *
    * @return LaunchData instance from the mLaunchDataList based on the current
    *         state of the mNextLauchData attribute.
    *
    */
   private LaunchData getNextLaunchData()
   {
      LaunchData result = null;

      if ( mNextLaunchData != mLaunchDataList.size() )
      {
         result = (LaunchData)mLaunchDataList.elementAt(mNextLaunchData);
         mNextLaunchData++; // increment to the next Launch Data in the list
      }

      return result;
   }
   
   /**
   *
   * Class PrivilegedCPTest
   *
   * Implements:  PrivilegedAction
   *
   * Description:
   *
   *     This inner class is used to grant permission to the code in
   *     this applet to allow read/write to files on the local disk.
   *
   */
  private class PrivilegedCPTest implements PrivilegedAction
  {
   
     /**
      * The type of test to be performed.  This information is specified by
      * the use on the Instructions page.
      */
     String mApplicationProfileType;
     
     /**
      * The schema location value of the extended elements.
      */
     String mExtensionSchemaLocation;
     
     /**
      * Boolean value directing the test to test the Metadata contained in 
      * the package.
      */
     boolean mTestMetadata;
     
     /**
      * Boolean value directing the test to test the SCOs contained in the 
      * package.
      */
     boolean mTestSCOs;
     
     /**
      * Boolean value directing the manifest test.
      */
     boolean mTestManifestOnly;
   
     /**
      * Constructor of the inner class
      *
      * @param iApplicationProfileType - The application profile rules to be
      *                                  tested against.  The options are
      *                                  "contentaggregation" or "resource".
      *
      * @param iExtensionSchemaLocation - The schema location value of the
      *                                   extended elements.
      *
      * @param iTestMetadata - Boolean value directing the test to test the
      *                        Metadata contained in the package.  Note:  If
      *                        this value is not true, then the package can not
      *                        obtain a conformance label and will be deemed
      *                        non-conformant overall
      *
      * @param iTestSCOs - Boolean value directing the test to test the SCOs
      *                    contained in the package.  Note:  If this value is
      *                    not true, then the package can not obtain a
      *                    conformance label and will be deemed non-conformant
      *                    overall.
      * 
      * @param iTestManifestOnly - Boolean value directiong the manifest test.
      * @param iNetscape true if browser is Netscape, false otherwise
      *
      */
     PrivilegedCPTest( String iApplicationProfileType,
                       String iExtensionSchemaLocation,
                       boolean iTestMetadata,
                       boolean iTestSCOs,
                       boolean iTestManifestOnly,
                       boolean iNetscape)
     {
        mApplicationProfileType = iApplicationProfileType;
        mExtensionSchemaLocation = iExtensionSchemaLocation;
        mTestMetadata = iTestMetadata;
        mTestSCOs = iTestSCOs;
        mTestManifestOnly = iTestManifestOnly;
        mIsBrowserNetscape = iNetscape;
        
        mManifestOnly = iTestManifestOnly;
     }

     /**
      *
      * This run method grants privileged applet code access to read/write
      * to the local disk.  This allows the applet to work in Netscape 6.
      *
      * @return Object
      *
      */
     public Object run()
     {
        try
        {
           // call the modules tester class
           mContentPackageTester.initializeTest(mTestSubjectFile, mTestManifestOnly);
           mADLValidatorOutcome =
                     mContentPackageTester.validateContentPackage(
                                                      mTestSubjectFile,
                                                      mTestSubjectType,
                                                      mApplicationProfileType,
                                                      mExtensionSchemaLocation,
                                                      mTestMetadata,
                                                      mTestSCOs,
                                                      mTestManifestOnly);
           // this value will indicate if only the default organization of the
           // manifest shall be tested.  Currently this value should be false.
           boolean testDefaultOrganizationOnly = false;

           // The manifest has been tested, now is a good time to calcuate the checksum
           // Calculate and store the checksum value
           mChecksumValue = ADLPackageChecksum.createChecksum(mTestSubjectFile);
                      
           // the first outcome object in the list is the manifest outcome.
           //Boolean manifestOutcome = (Boolean)mADLValidatorOutcomeList.elementAt( 0 );
           boolean appProfilePass = mADLValidatorOutcome.getManifestOutcome();
           if ( appProfilePass && mTestSCOs && !mADLValidatorOutcome.getSubmanifestReferenced() && !mADLValidatorOutcome.getExternalFileReferenced())
           {
              // set the sco information found in the package only if the
              // has been found to be valid to the schema.
              Vector tempV = new Vector();
              Vector launchVector = new Vector();
              
              // We do not want to test assets for resource packages
              boolean removeAssets = false;
              if ( mApplicationProfileType.equals("resource") )
              {
                 removeAssets = true;
              }
              tempV = mContentPackageTester.getLaunchData(testDefaultOrganizationOnly, removeAssets);
              
              // Loop through the SCOs to make sure none are null, 
              // remove them if they are null and fail
              List emptyHrefs = new ArrayList();
              for ( int i = 0; i < tempV.size(); i++ )
              {
                 LaunchData tempData = (LaunchData)tempV.get(i);
                 String launchLine = tempData.getLaunchLine();
                 
                 if (tempData.getLaunchLine().equals("") )
                 {
                    emptyHrefs.add(tempData.getResourceIdentifier());                    
                 }                 
                 else
                 {
                    launchVector.add(tempData);
                 }                 
              }
             
              setLaunchDataList( launchVector, mTestSCOs );

              // If the failed hrefs exist, we want them at the end
              if ( emptyHrefs.size() > 0 )
              {
                 mSCOsConformant = false;
                 SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
                    MessageType.WARNING, Messages.getString("ContentPackageDriver.6")));
                 Iterator iter = emptyHrefs.iterator();
                 while ( iter.hasNext() )
                 {
                    SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
                          MessageType.OTHER, iter.next().toString()));
                 }
                 
                 // add a seperator in the log
                 SummaryLogMessageCollection.getInstance().addMessage ( new LogMessage(
                    MessageType.OTHER, Messages.getString("ContentPackageTester.36") ));
              }
           }
        }
        catch(Exception e)
        {
           LOGGER.severe( "Exception occurred in ContentPackageDriver" +
              ".getNextLaunchLine(). It was thrown while decoding the file path.\n" + 
              e.getMessage() );
           e.printStackTrace();
        } 

        return null;
     }
  }

  /**
   * <strong>Inner Class</strong>: PrivilegedTempDirCleanup<br><br>
   *
   * <strong>Implements</strong>:  PrivilegedAction<br><br>
   *
   * This class is used to grant permission to the code in
   * this applet to allow the deleting of the temparary files from the
   * local hard disk.
   */
  private class PrivilegedTempDirCleanup implements PrivilegedAction
  {
     /**
      * The path of the temp directory to clear
      */
     private String mPath;

     /**
      *
      * Constructor of the inner class
      *
      * @param iPath - The absolute path of the temp directory to clear
      *
      */
     PrivilegedTempDirCleanup( String iPath )
     {
        mPath = iPath;
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
        return null;
     }
  }
  
  /**
   * Does privileged initialization of test class
   */
  private class PrivilegedInitTestClass implements PrivilegedAction
  {     
     /**
      *
      * Constructor of the inner class
      *
      */
     PrivilegedInitTestClass()
     {
        //left blank intentionally
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
        mContentPackageTester = new ContentPackageTester(mEnvironmentVariable);
        return null;
     }
  }
}