package org.adl.testsuite.contentpackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.adl.logging.DetailedLogMessageCollection;
import org.adl.logging.DetailedLogWriter;
import org.adl.logging.LogFileGenerator;
import org.adl.logging.SummaryLogMessageCollection;
import org.adl.logging.SummaryLogWriter;
import org.adl.logging.UIMessageProcessor;
import org.adl.parsers.dom.ADLDOMParser;
import org.adl.testsuite.contentpackage.util.logging.ValidatorLogger;
import org.adl.testsuite.contentpackage.util.validator.ManifestHandler;
import org.adl.testsuite.contentpackage.util.validator.MetadataData;
import org.adl.testsuite.contentpackage.util.validator.MetadataLaunchData;
import org.adl.testsuite.contentpackage.util.validator.RollupManifest;
import org.adl.testsuite.contentpackage.util.validator.ValidatorResult;
import org.adl.testsuite.util.TestSubjectData;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;
import org.adl.util.Messages;
import org.adl.util.debug.DebugIndicator;
import org.adl.util.decode.decodeHandler;
import org.adl.util.zip.UnZipHandler;
import org.adl.validator.Validator;
import org.adl.validator.util.ResultCollection;
import org.adl.validator.util.ValidatorCheckerNames;
import org.w3c.dom.Document;

/**
 * This file is the main test class of the content package test module.<br>
 * <br>
 * <strong>Filename:</strong><br>
 * ContentPackageTester.java<br>
 * <br>
 * <strong>Description:</strong><br>
 * The <code>ContentPackageDriver</code> is the main test class of the content
 * package test module.
 * 
 * @author ADL Technical Team
 */
public class ContentPackageTester
{
   
   /**
    * The content package validator instance used to validate the content
    * package
    */
   private Validator mSCORMValidator;

   /**
    * Test option attribute to indicate if Metadata testing shall be performed.
    */
   private boolean mTestMetadata;

   /**
    * Environment Variable value.
    */
   private String mEnvironmentVariable;

   /**
    * Document object of the test subject
    */
   private Document mDocument;

   /**
    * processes messages sent from the User Interface
    */
   private UIMessageProcessor mUimp;

   /**
    * generates detailed log files and returns the URL
    */
   private LogFileGenerator mLfg;

   /**
    * This holds the complete URL of the detailed log file
    */
   private String mDetailedLogFileURL;

   /**
    * This holds the name of the file being tested.
    */
   private String mTestSubjectName;
   
   /**
    * This attribute serves as the data structure used to store the Launch Data
    * information of SCOs and Metadata referenced within the content package.
    */
   private ManifestHandler mManifestHandler;
   
   /**
    * This attribute contains the base directory of where the test subject is
    * located. It is used by the validator to determine the location of the
    * package resources, including the manifest, sco's, and/or metadata.
    */
   private String mBaseDirectory;
   
   /**
    * The application profile rules to be tested against. The options are 
    * "contentaggregation" or "resource".
    */
   private String mApplicationProfileType;
   
   /**
    * Default Constructor
    * 
    * @param iEnvironmentVariable - the path of the environment variable value
    */
   public ContentPackageTester(String iEnvironmentVariable)
   {
      mUimp = new UIMessageProcessor();

      // create a new LogFileGenerator
     mLfg = new LogFileGenerator(iEnvironmentVariable);

     new SummaryLogWriter(mLfg);

      mEnvironmentVariable = iEnvironmentVariable;

      // initialize to test for metadata and SCOs
      mTestMetadata = true;
      
      mManifestHandler = new ManifestHandler();
      
   }

   /**
    * Initializes the DetailedLogWriter
    * 
    * @param iTestSubjectFile The Content Package being tested
    * @param iManifestOnly Boolean of whether this is a manifest only test
    */
   public void initializeTest(String iTestSubjectFile, boolean iManifestOnly)
   {
      // This will pull the filename of the test subject minus the
      // extension
      mTestSubjectName = iTestSubjectFile.substring(iTestSubjectFile.lastIndexOf(File.separator) + 1, iTestSubjectFile
         .lastIndexOf("."));

      String testType = ( iManifestOnly ) ? "Manifest" : "CP";

      mDetailedLogFileURL = mLfg.getLogName(testType, mTestSubjectName, true);

      // add a newlog message to the SummaryLogMessageCollection so the
      // SummaryLogWriter knows to create the summary log file
      SummaryLogMessageCollection.getInstance().addMessage(
           new LogMessage(MessageType.NEWLOG, mTestSubjectName));

      new DetailedLogWriter(mDetailedLogFileURL);

      String testSubjectTypeName = ( iManifestOnly ) ? "Manifest" : "Content Package";

      TestSubjectData.getInstance().sendToDetailedLog(testSubjectTypeName);
      TestSubjectData.getInstance().sendToSummaryLog(testSubjectTypeName);
   }

   /**
    * The root method of the content package tester. This method is used to
    * begin the validation process
    * 
    * @param iTestSubject The name and location of the test subject to be
    *           tested. This is a zip file if the type is a pif or it is a xml
    *           file if the type is non-pif.
    * @param iTestSubjectType The type of test subject being tested. The options
    *           are "pif" or "non-pif"
    * @param iApplicationProfileType The application profile rules to be tested
    *           against. The options are "contentaggregation" or "resource".
    * @param iExtendedSchemaLocations The schema location value of the extended
    *           elements.
    * @param iTestMetadata Boolean value directing the test to test the Metadata
    *           contained in the package. Note: If this value is not true, then
    *           the package can not obtain a conformance label and will be
    *           deemed non-conformant overall
    * @param iTestSCOs Boolean value directing the test to test the SCOs
    *           contained in the package. Note: If this value is not true, then
    *           the package can not obtain a conformance label and will be
    *           deemed non-conformant overall.
    * @param iManifestOnly The boolean describing whether or not the IMS
    *           Manifest is to be the only subject validated. True implies that
    *           validation occurs only on the IMS Manifest (checks include
    *           wellformedness, schema validation, and application profile
    *           checks). False implies that the entire Content Package be
    *           validated (IMS Manifest checks with the inclusion of the
    *           required files checks, metadata, and SCO testing).
    * @return Returns an object containing the validation outcome of the test
    *         subject.
    */
   public ValidatorResult validateContentPackage(String iTestSubject,
                                        String iTestSubjectType,
                                        String iApplicationProfileType,
                                        String iExtendedSchemaLocations,
                                        boolean iTestMetadata,
                                        boolean iTestSCOs,
                                        boolean iManifestOnly)
   {
      // set the test options attributes
      mTestMetadata = iTestMetadata;
      mApplicationProfileType = iApplicationProfileType;
      ValidatorResult valResult = new ValidatorResult();
      Vector outcomeList = new Vector();
      ResultCollection validatorCollection = new ResultCollection();
      ValidatorLogger validatorLog = new ValidatorLogger();

      mBaseDirectory = importContentPackage(iTestSubject);
      
      mSCORMValidator = new Validator(mBaseDirectory+"imsmanifest.xml", iApplicationProfileType, iManifestOnly);
      
      mSCORMValidator.validate();
      validatorCollection = mSCORMValidator.getResultCollection();

      boolean validatorPassed = validatorCollection.isAllCheckerPassed();
      
      // adds the checker statuses that determine if SCO testing should be executed. 
      valResult.setExecuteScoTesting(validatorPassed);
      
      // adds the status of the checkers required to determine Manifest Conformance.
      valResult.setManifestOutcome(validatorPassed);

      // Converts the ResultCollection Messages to CTS logging messages
      validatorLog.logResultCollectionMessages(validatorCollection, iManifestOnly, iApplicationProfileType, mDetailedLogFileURL);

      // test all metadata found in the manifest
      boolean didIMSManifestExist = validatorCollection.getPackageResult(ValidatorCheckerNames.MAN_AT_ROOT).isPackageCheckerPassed();
      boolean didWellformednessPass = validatorCollection.getPackageResult(ValidatorCheckerNames.WELLFORM).isPackageCheckerPassed();
      boolean didRequiredFilesExist = validatorCollection.getPackageResult(ValidatorCheckerNames.REQ_FILES).isPackageCheckerPassed();
      boolean didValidationToSchemaPass = validatorCollection.getPackageResult(ValidatorCheckerNames.SCHEMA_VAL).isPackageCheckerPassed();
      
      // Check the value of isStopped because this checker will always pass but may stop(submanifests get a warning, not a failure)
      boolean submanifestReferenced = validatorCollection.getPackageResult(ValidatorCheckerNames.SUBMANIFEST).isTestStopped();
      valResult.setSubmanifestReferenced(submanifestReferenced);

      boolean externalFileReferenced = validatorCollection.getPackageResult(ValidatorCheckerNames.RES_HREF).isTestStopped();
      valResult.setExternalFileReferenced(externalFileReferenced);
      
      if( mTestMetadata && didRequiredFilesExist && didWellformednessPass && didIMSManifestExist && !submanifestReferenced && !externalFileReferenced)
      {
         // Creates a dom to be used in MDValidator         
         mDocument = getDocument(mBaseDirectory + "imsmanifest.xml");
         
         Vector mdOutcomeList = validateMetadata(iTestSubjectType, iTestSubject, didValidationToSchemaPass,
            iExtendedSchemaLocations);
         if( mdOutcomeList != null )
         {
            valResult.setMetadataOutcomes(mdOutcomeList);
         }
      }
      
      // Perform rollup if all checks pass
      if( ( validatorPassed ) && ( mDocument != null ) && !submanifestReferenced && !externalFileReferenced )
      {
         RollupManifest rollUpMan = new RollupManifest(mDocument);
         rollUpMan.rollupManifest(false);
      }
      
      return  valResult ;
   }

   /**
    * Pass through method to allow UI to send messages to the Detailed and
    * Summary Logs.
    * 
    * @param iLog The log to write to
    * @param iMsgType The type of message based on an enumerated list
    * @param iMsgTxt The text intended to be written to log
    */
   public void writeLogEntry(int iLog, int iMsgType, String iMsgTxt)
   {
      mUimp.writeLogEntry(iLog, iMsgType, iMsgTxt);
   }

   /**
    * This method gives access to a list of the LaunchData data structure.
    * 
    * @param iDefaultOrganizationOnly Boolean describing the organization to be
    *           searched for launch data.
    * @param iRemoveAssets - boolean indicating whether or not to include assets
    *           in the launch data          
    * @return The list of LaunchData represented as a Vector.
    */
   protected Vector getLaunchData(boolean iDefaultOrganizationOnly, boolean iRemoveAssets)
   {
      return mManifestHandler.getLaunchData(mDocument.getDocumentElement(),
                                            iDefaultOrganizationOnly, iRemoveAssets);
   }

   /**
    * This method is used to validate all Metadata found in the content package
    * 
    * @param iTestSubjectType Test subject format.Valid valued = "pif" or
    *           "non-pif".
    * @param iTestSubjectFile XML test subject to be validated
    * @param iDidValidationToSchemaPass Boolean describing if the XML was valid
    *           to the schema.
    * @param iExtendedSchemaLocations String containing the extension schema
    *           required for XML parsing.
    * @return Returns an object containing the validation outcome of the test
    *         subject.
    */
   private Vector validateMetadata(String iTestSubjectType,
                                   String iTestSubjectFile,
                                   boolean iDidValidationToSchemaPass,
                                   String iExtendedSchemaLocations)
   {
      
      Vector metadataValidateOutcomeList = new Vector();
      ResultCollection metadataResultCollection = new ResultCollection();
      
      boolean result;

      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.SUBLOGTITLE, Messages.getString("ContentPackageTester.18")));

      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.SUBLOGHEAD, Messages.getString("ContentPackageTester.19")));
      
      // add a divider
      SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
         MessageType.OTHER, Messages.getString("ContentPackageTester.36")));
      
      // get the list of metadata to be tested
      // test the metadata
      Vector metadataDataList = mManifestHandler.getMetadata(mDocument.getDocumentElement(),
                                                             mBaseDirectory);
      // Clear any duplicate files out of the vector
      List uniqueMetadataList = new ArrayList();
      Iterator iter = metadataDataList.iterator();
      while ( iter.hasNext() )
      {
         MetadataData mdd = (MetadataData)iter.next();
         // Check to see if the given metadata is already in the list
         boolean inList = false;
         
         // We want to validate every inline metadata
         if ( !mdd.isInlineMetadata() )
         {
            for ( int i = 0; i < uniqueMetadataList.size(); i++ )
            {
               if ( ((MetadataLaunchData)uniqueMetadataList.get(i)).getLocation().equals(mdd.getLocation()))
               {
                  inList = true;
                  ((MetadataLaunchData)uniqueMetadataList.get(i)).addIdentifier(mdd.getIdentifier());
               }
            }
         }
         
         if ( !inList )
         {
            MetadataLaunchData mdld = new MetadataLaunchData();
            mdld.setLocation(mdd.getLocation());
            mdld.setRootLOMNode(mdd.getRootLOMNode());
            mdld.addIdentifier(mdd.getIdentifier());
            uniqueMetadataList.add(mdld);
         }
         
         
      }
      
      String packageLocation = mEnvironmentVariable + File.separator + "PackageImport";

      if ( uniqueMetadataList.size() > 0 )
      {
         Iterator metadataIter = uniqueMetadataList.iterator();
         int count = 1;
         while ( metadataIter.hasNext() )
         {
            MetadataLaunchData currentMetadataData =(MetadataLaunchData)metadataIter.next();
           
            // creates the sublog and returns the URL
            mDetailedLogFileURL = mLfg.getLogName("MD" + ( count ), "", true);
            count++;

            // create a new sublog
            DetailedLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.NEWLOG, mDetailedLogFileURL));

            TestSubjectData.getInstance().sendToDetailedLog("Metadata");

            String metadataLocation = currentMetadataData.getLocation();
            metadataLocation = metadataLocation.replaceAll("/", "\\\\");

            // Because the metadata location came from the manifest, it may be encoded
            // we must decode the location before we try to locate it
            decodeHandler handler = new decodeHandler(metadataLocation,"UTF-16");
            handler.decodeName();
            metadataLocation = mBaseDirectory + handler.getDecodedFileName();
            
            Validator mSCORMMDValidator = new Validator(metadataLocation, 
                                                         mApplicationProfileType, 
                                                         mTestMetadata);
            List checkerList = new ArrayList();
            checkerList.add("org.adl.validator.packagechecker.checks.WellformednessChecker");
            checkerList.add("org.adl.validator.packagechecker.checks.SchemaValidationChecker");
            mSCORMMDValidator.setCheckerList(checkerList);
            
            String location = "";

            String identifier = currentMetadataData.getIdentifiers();

            if( currentMetadataData.isInlineMetadata() )
            {
               location = "inline";

               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                  MessageType.INFO, Messages.getString("ContentPackageTester.33", location,
                        identifier)));
               
               SummaryLogMessageCollection.getInstance().addMessage(
                     new LogMessage(MessageType.INFO, Messages.getString("ContentPackageTester.32")));
               
               SummaryLogMessageCollection.getInstance().addMessage(
                     new LogMessage(MessageType.INFO, Messages.getString("ContentPackageTester.33", location,
                        identifier)));
               
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                     MessageType.OTHER, Messages.getString("ContentPackageTester.36")));
               
               String msgText;

               msgText = Messages.getString("MDValidator.14");
               DetailedLogMessageCollection.getInstance().addMessage( new LogMessage (
                  MessageType.INFO, msgText ) );
               
               msgText = Messages.getString("MDValidator.22"); 
               DetailedLogMessageCollection.getInstance().addMessage( new LogMessage (
                  MessageType.PASSED, msgText ) );
               SummaryLogMessageCollection.getInstance().addMessage( new LogMessage (
                     MessageType.PASSED, msgText ) );
               
               DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                     MessageType.OTHER, Messages.getString("ContentPackageTester.36")));
               
               msgText = Messages.getString("MDValidator.15");
               DetailedLogMessageCollection.getInstance().addMessage( new LogMessage (
                  MessageType.INFO, msgText ) );
               
               if ( iDidValidationToSchemaPass )
               {
                  msgText = Messages.getString("MDValidator.23"); 
                  DetailedLogMessageCollection.getInstance().addMessage( new LogMessage (
                        MessageType.PASSED, msgText ) );
                  SummaryLogMessageCollection.getInstance().addMessage( new LogMessage (
                        MessageType.PASSED, msgText ) );
               }
               else
               {
                  msgText = Messages.getString("MDValidator.24"); 
                  DetailedLogMessageCollection.getInstance().addMessage( new LogMessage (
                        MessageType.FAILED, msgText ) );
                  SummaryLogMessageCollection.getInstance().addMessage( new LogMessage (
                        MessageType.FAILED, msgText ) );
               }
               
               SummaryLogMessageCollection.getInstance().addMessage( new LogMessage (
                     MessageType.LINKMD, mDetailedLogFileURL, identifier ));
               // retrieve object that stores the results of the validation
               // activites needed for logging an conformance.
               metadataValidateOutcomeList.add(Boolean.valueOf(iDidValidationToSchemaPass));
            }
            else
            {
               if( "pif".equals(iTestSubjectType) )
               {
                  location = packageLocation + File.separator + currentMetadataData.getLocation();
               }
               else
               // ( iTestSubjectType.equals("non-pif") )
               {
                  // the size of substring can be calculated by removing the 16
                  // characters "/imsmanifest.xml" from the end
                  int sizeOfSubstring = iTestSubjectFile.length() - 16;
                  location = iTestSubjectFile.substring(0, sizeOfSubstring) + File.separator
                     + currentMetadataData.getLocation();
               }

               String logTextLocation = location.replace(File.separatorChar, '/');

               // This adds the "Testing Metadata with location..." to the
               // detailed log
               DetailedLogMessageCollection.getInstance().addMessage(
                  new LogMessage(MessageType.INFO, Messages.getString("ContentPackageTester.33", logTextLocation,
                     identifier)));

               SummaryLogMessageCollection.getInstance().addMessage(
                     new LogMessage(MessageType.INFO, Messages.getString("ContentPackageTester.32")));
               
               SummaryLogMessageCollection.getInstance().addMessage(
                     new LogMessage(MessageType.INFO, Messages.getString("ContentPackageTester.33", logTextLocation,
                        identifier)));
               
               mSCORMMDValidator.validate();
               metadataResultCollection = mSCORMMDValidator.getResultCollection();

               result = metadataResultCollection.isAllCheckerPassed();
               // retrieve object that stores the results of the validation
               // activites need for logging and conformance
               metadataValidateOutcomeList.add(Boolean.valueOf(result));
               
               ValidatorLogger validatorLog = new ValidatorLogger();
               validatorLog.logMDResultCollectionMessages(metadataResultCollection, mDetailedLogFileURL, identifier);              

            }


            
            // close the sublog
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(MessageType.ENDLOG, ""));

            // add link to child log to be displayed in the detailed log
            DetailedLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.LINKMD, mDetailedLogFileURL, identifier));
            
            // add a divider
            SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(MessageType.OTHER, Messages.getString("ContentPackageTester.36")));

         }// end for loop

      }
      else
      {
         // no metadata found
         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.INFO, Messages.getString("ContentPackageTester.29")));

         // add a divider
         SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.OTHER, Messages.getString("ContentPackageTester.36")));
      }
      return metadataValidateOutcomeList;
   }

   /**
    * Adds an ENDLOG message to the Summary LogMessageCollection
    */
   public void testComplete()
   {
      SummaryLogMessageCollection.getInstance().addMessage(
                    new LogMessage(MessageType.ENDLOG,""));


   }
   /**
    * This method extracts the selected test subject, in the form of a zip file,
    * to a temporary "/PackageImport" directory in order to perform validation.
    * 
    * @param iFile The content package zip test file URI
    * @return extractDir returnst the path of the directory the file is
    *         extracted to
    */
   private String importContentPackage(String iFile)
   {      
      // get the extract dir
      String extractDir = mEnvironmentVariable + File.separator + "PackageImport" + File.separator;
      
      // Unzip the content package into a local directory for processing
      if ( iFile.toLowerCase().endsWith(".zip"))
      {         
         UnZipHandler uzh = new UnZipHandler(iFile, extractDir);
         uzh.extract();
      }
      // Copy the content package into a local directory for processing
      else
      {
         copyCourse(getPathOfFile(iFile), extractDir);
      }     

      return extractDir;
   }
   
   /**
    * This method will copy a course from the specified directory where it
    * already exists, to a new specified directory where it is to be copied to.
    * 
    * @param iInFilePath - The path of the current file or directory that needs
    *                      to be copied.
    * @param iOutFilePath - The path of the directory that the file is to be
    *                       copied to.
    * @return boolen - A boolean indicating the success of the course copying                      
    */
   private boolean copyCourse( String iInFilePath, String iOutFilePath ) 
   {
      boolean result = true;
      try
      {
         String inDirName = iInFilePath;
         inDirName.replace('/',java.io.File.separatorChar);

         File tempFile = new File(inDirName);
         File[] fileNames = tempFile.listFiles();

         String outDirName = iOutFilePath;

         outDirName = outDirName.replace('/',java.io.File.separatorChar);
         File tempDir = new File(outDirName);
         tempDir.mkdirs();
         
         FileInputStream fi = null;
         FileOutputStream fo = null;
         BufferedInputStream in = null;
         BufferedOutputStream out = null;
           
         for ( int i=0; i < fileNames.length; i++ )
         {
            String tempString = outDirName + java.io.File.separatorChar + 
                                                fileNames[i].getName();
            if ( fileNames[i].isDirectory() )
            {
               File dirToCreate = new File(tempString);
               dirToCreate.mkdirs();
               result = copyCourse( fileNames[i].getAbsolutePath(), tempString );
            }
            else
            {
               fi = new FileInputStream(fileNames[i]);
               fo = new FileOutputStream(tempString);
               in = new BufferedInputStream( fi );
               out = new BufferedOutputStream( fo );
               int c;
               while ((c = in.read()) != -1) 
               {
                  out.write(c);
               }

               in.close();
               fi.close();
               out.close();
               fo.close();
            }
         }
      }
      catch ( IOException ioe )
      {
         result = false;
         if ( DebugIndicator.ON )
         {
            ioe.printStackTrace(); 
         }
      }
      return result;
         
   }
   
   /**
    * Creates a DOM to find the metadata references and the SCO references.
    * 
    * @param iURI - String that represents the location of the xml file.
    * @return Document the represents the xml file being validated.
    */
   private Document getDocument(String iURI)
   {
      Document doc = null;

      try
      {
        ADLDOMParser adlParser = new ADLDOMParser();
        adlParser.parseForWellformedness(iURI, true, false);  
        doc = adlParser.getDocument();
      }
      catch (Exception e)
      {
        System.err.println("Sorry, an error occurred: " + e);
      }
      return doc;
   }
   
   /**
    * This method retrieves the directory location of the test subject by
    * truncating the filename off of the URL passed in.
    * 
    * @param iFileName The absolute path of the test subject file
    * @return String - the directory that the file is located
    */
   private String getPathOfFile(String iFileName)
   {
      String result = "";
      String tmp = "";

      try
      {
         StringTokenizer token = new StringTokenizer(iFileName, File.separator, true);

         int numTokens = token.countTokens();

         // We want all but the last token added
         numTokens--;

         for( int i = 0; i < numTokens; i++ )
         {
            tmp = token.nextToken();
            result = result + tmp;
         }
      }
      catch( NullPointerException npe )
      {
         npe.printStackTrace();
      }

      return result;
   }
   
   /**
    * This method returns the location of the extracted pacakge
    * 
    * @return Path of extracted package
    */
   public String getPackageExtractionDir()
   {
      return mBaseDirectory;
   }

}