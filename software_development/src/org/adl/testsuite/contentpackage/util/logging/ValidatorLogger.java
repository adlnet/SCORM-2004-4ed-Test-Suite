package org.adl.testsuite.contentpackage.util.logging;

import java.util.List;

import org.adl.util.LogMessage;
import org.adl.util.MessageType;
import org.adl.util.Messages;
import org.adl.validator.util.Result;
import org.adl.validator.util.ResultCollection;
import org.adl.validator.util.ValidatorCheckerNames;
import org.adl.validator.util.ValidatorMessage;

/**
 * Converts ResultCollections returned by the Validator to Test Suite Log messages.
 * 
 * @author ADL Technical Team
 *
 */
public class ValidatorLogger
{

   /**
    * This holds the complete URL of the detailed log file
    */
   private transient String mDetailedLog;
   
   /**
    * String used for logging a line space.
    */
   private static final LogMessage LINESPACE = new LogMessage(MessageType.OTHER, "");
   
   /**
    * HR?
    */
   private static final LogMessage HR_MESSAGE = new LogMessage(MessageType.OTHER, Messages.getString("ContentPackageTester.36"));
   
   /**
    * Logs the Result messages to CTS Logging interface.
    */
   private final transient ValidatorWriter mValidatorWriter; 
   
   /**
    * Default Constructor
    */
   public ValidatorLogger() 
   {
      mValidatorWriter = new ValidatorWriter();
   }
   
   /**
    * Overloaded constructor
    * 
    * @param iValidatorMessWtr logs the Result messages as Strings in a List. 
    */
   public ValidatorLogger(final ValidatorMessageWriter iValidatorMessWtr)
   {
      mValidatorWriter = iValidatorMessWtr;
   }
   
   /**
    * Logs the messages from the ResultCollection returned by the Validator to 
    * the Test Suite.
    * 
    * @param iResultCollection - Object that holds all of the results of the 
    *                            package checkers during package validation.
    * @param iManifestOnly - The boolean describing whether or not the IMS
    *                        Manifest is to be the only subject validated.
    * @param iAppProfileType - The Application Profile type of the test
    *                                subject (content aggregation or resource ).
    * @param iDetailedMessages - String that holds the complete URL of the 
    *                            detailed log file. 
    */
   public void logResultCollectionMessages(final ResultCollection iResultCollection, 
                                           final boolean iManifestOnly, 
                                           final String iAppProfileType,
                                           final String iDetailedMessages)
   {
      setSummaryHeader(iAppProfileType, iManifestOnly);
      
      //Gets the list of checker Results from the ResultCollection
      final List results = iResultCollection.getPackageResultsCollection();
      final boolean validResult = logCheckerResults(results);

      addSummaryLogMessage(MessageType.LINKCP, iDetailedMessages + "~ ");

      // place a horizontal line in summary logs to seperate test
      addSummaryLogMessage(MessageType.OTHER, Messages.getString("ContentPackageTester.36"));

      // If test was stopped, a (sub)manifest was used, and therefore we do not 
      // display a pass or fail message
      LogMessage valid;
      if ( iResultCollection.getPackageResult(ValidatorCheckerNames.SUBMANIFEST).isTestStopped() ||
           iResultCollection.getPackageResult(ValidatorCheckerNames.RES_HREF).isTestStopped() )
      {
         valid = new LogMessage(MessageType.STOP, Messages.getString("ContentPackageTester.46"));
      }
      else
      {
         // report to detailed log if the manifest passed or failed
         valid = validResult ? new LogMessage(MessageType.PASSED,Messages.getString("ContentPackageTester.96"))

            : new LogMessage(MessageType.FAILED, Messages.getString("ContentPackageTester.100"));
      }
      addDetailedLogMessage( valid );
   }
   
   /**
    * Logs the messages from the ResultCollection returned by the Validator to 
    * the Test Suite when validation is done for referenced metadata.
    * 
    * @param iResultCollection - Object that holds all of the results of the 
    *                            package checkers during package validation.
    * @param iDetailedMessages - String that holds the complete URL of the 
    *                            detailed log file.
    * @param iIdentifier - The identifier value of the parent of the metadata.
    */
   public void logMDResultCollectionMessages(final ResultCollection iResultCollection,
                                             final String iDetailedMessages,
                                             final String iIdentifier)
   {
      final List results = iResultCollection.getPackageResultsCollection();
      mDetailedLog = iDetailedMessages;
      
      logCheckerResults(results);
      
      addSummaryLogMessage(MessageType.LINKMD, mDetailedLog + "~" + iIdentifier );
      
   }
   
   /**
    * Adds the summary log and detailed log messages for each checker in the 
    * ResultCollection.
    * 
    * @param iResults - List of checker Results from the ResultCollection
    * @return - boolean indicating if the checkers passed
    * */
   private boolean logCheckerResults(final List iResults)
   {
      boolean returnResult = true;
      Result currentResult;
      
      for ( int i = 0; i < iResults.size(); i++ )
      {
         currentResult = (Result)iResults.get(i);
         returnResult = returnResult && currentResult.isPackageCheckerPassed();
         
         final List summaryMsg = currentResult.getOverallStatusMessage();
         for ( int k = 0; k < summaryMsg.size(); k++ )
         {
            addSummaryLogMessage(convertToLogMessage((ValidatorMessage)summaryMsg.get(k)));
         }
         
         if ( !currentResult.isCheckerSkipped() )
         {
            final List detailedMsg = currentResult.getPackageCheckerMessages();

            addDetailedLogMessage(LINESPACE);
            
            for ( int j = 0; j < detailedMsg.size(); j++ )
            {
               addDetailedLogMessage(convertToLogMessage((ValidatorMessage)detailedMsg.get(j)));
            }

            if ( detailedMsg.isEmpty() )
            {
               addDetailedLogMessage(LINESPACE);
               // place a horizontal line in detailed logs to seperate checks
               addDetailedLogMessage(HR_MESSAGE);              
            }

         }
         
         if (currentResult.isTestStopped())
         { 
            if  (ValidatorCheckerNames.SUBMANIFEST.equals(currentResult.getPackageCheckerName()) ||
                     ValidatorCheckerNames.RES_HREF.equals(currentResult.getPackageCheckerName()))
            {
               addSummaryLogMessage(MessageType.STOP, Messages.getString("ContentPackageTester.46"));
            }
            else
            {
               addSummaryLogMessage(MessageType.TERMINATE, Messages.getString("ContentPackageTester.46"));
            }
         }
      }
      
      return returnResult;
   }
   
   /**
    * This method converts a ValidatorMessage object to a LogMessage object.
    * 
    * @param iValidatorMsg ValidatorMessage object.
    * @return LogMessage object.
    */
   protected LogMessage convertToLogMessage(final ValidatorMessage iValidatorMsg)
   {
      return new LogMessage(iValidatorMsg.getMessageType(), iValidatorMsg.getMessageText());
   }
   
   /**
    * Adds a LogMessage object to the Summary Log Collection.
    * 
    * @param iLogMessage is the object to be added to the Summary Log Collection.
    */
   protected void addSummaryLogMessage(final LogMessage iLogMessage)
   {
      mValidatorWriter.addSummaryLogMessage(iLogMessage);
   }
   
   /**
    * Adds the reported message to the appropriate Summary Log Collection. 
    * 
    * @param iMsgType Value used to represent message types.
    * @param iLogMessage  String to be logged as a SummaryLogMessage.
    */
   protected void addSummaryLogMessage(final int iMsgType, final String iLogMessage)
   {
      mValidatorWriter.addSummaryLogMessage(iMsgType, iLogMessage);
   }
   
   /**
    * Adds the reported message to the appropriate Detailed Log Collection.
    * 
    * @param iLogMessage - String to be logged as a DetailedLogMessage.
    */
   protected void addDetailedLogMessage(final LogMessage iLogMessage)
   {
      mValidatorWriter.addDetailedLogMessage(iLogMessage);
   }
   
   /**
    * Sets up the Summary log header and info log messages.
    * 
    * @param iManifestOnly - The boolean describing whether or not the IMS
    *                        Manifest is to be the only subject validated.
    * @param iAppProfileType - The Application Profile type of the test
    *                                subject (content aggregation or resource )
    */
   private void setSummaryHeader(final String iAppProfileType, final boolean iManifestOnly)
   {
      String appProfileType;
      // Sets up the Summary log header and info log messages
      if( "contentaggregation".equals(iAppProfileType) )
      {
         appProfileType = "Content Aggregation";
      }
      else
      {
         appProfileType = "Resource";
      }
      
      final String testType =  iManifestOnly  ? "Manifest" : "Content Package";
      // report results for summary and detailed logs
      addSummaryLogMessage(MessageType.SUBLOGHEAD, Messages.getString("ContentPackageTester.62", testType));

      addSummaryLogMessage(MessageType.INFO, Messages.getString("ContentPackageTester.63", appProfileType));
      
   }
}
