package org.adl.testsuite.rte.sco;

import org.adl.logging.SummaryLogMessageCollection;
import org.adl.logging.DetailedLogMessageCollection;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;

import java.util.Vector;
import java.util.logging.Logger;
import org.adl.testsuite.util.ConformanceLabel;
import org.adl.util.Messages;

/**
 * <strong>Filename </strong>: SCOConformanceReporter.java <br>
 * <br>
 * 
 * <strong>Description </strong>: This class is responsible for determining the
 * level of conformance for the SCO being tested. This class uses the
 * information collected in the SCO Session and determines the appropriate
 * category of conformance. The class is also responsible for writing this
 * information to the Test Suite Log. <br>
 * <br>
 * 
 * @author ADL Technical Team
 */
public class SCOConformanceReporter
{
   /**
    * Logger object used for debug logging
    */
   private static Logger mLogger;

   /**
    * holds the URL of the detailed log file to be used to set up the link to
    * the detailed file in the summary log
    */
   private String mDetailedLogFileURL;
   
   /**
    * Current SCO session
    */
   private final SCOSession mSCOSession;

   /**
    * This is the constructor for the SCO Conformance Reporter This method sets
    * up all of the necessary attributes that will be needed to report
    * conformance.
    * 
    * @param ioTheSCOSession -
    *           Information collected for the SCO Session.
    */
   public SCOConformanceReporter(SCOSession ioTheSCOSession)
   {
      mLogger = Logger.getLogger("org.adl.util.debug.testsuite"); 

      mSCOSession = ioTheSCOSession;
   }

   /**
    * This method determines if the SCO supported the API correctly.
    * 
    * @return boolean - Flag indicating whether or not the SCO supported the
    *         minimum API calls correctly.
    *  
    */
   public boolean supportsMinAPI()
   {
      mLogger.entering("SCOConformanceReporter", "supportsMinAPI()");  

      // Check to see if the SCO supported the mandatory requirements:
      // Was able to find the API Adapter
      // LMSInitialize() was invoked correctly
      // LMSFinish() was invoked correctly
      final boolean flag = mSCOSession.supportsMinAPI();

      mLogger.finest("Minimum API Support: " + flag); 

      return flag;
   }

   /**
    * This method checks to see if the SCO supported the other API calls
    * correctly. The other API calls fall into the following SCORM categories:
    * 
    * Data Transfer and State Management.
    * 
    * @return boolean - Flag indicating whether or not the SCO supported the API
    *         correctly.
    */
   public boolean supportsAPI()
   {
      boolean dtflag = true;
      boolean smflag = true;

      // Check to see if any of the optional API functions were used
      // and used correctly

      // Check to see if the Data Transfer API functions were used and
      // used correctly (LMSGetValue(), LMSSetValue(), and LMSCommit())
      // !mSCOSession.supportsDataTransfer() - SCO Called some of the Data 
      // Transfer APIs. Determine which ones and if they were done correctly
      if ( mSCOSession.calledDataTransfer() && !mSCOSession.supportsDataTransfer() )
      {        
            dtflag = false;         
      }

      // Check to see if the State Management API functions were used and
      // used correctly (LMSGetLastError(), LMSGetErrorString(),
      // LMSGetDiagnostic())
      if ( mSCOSession.calledStateManagment() && !mSCOSession.supportsStateManagement())
      {
            smflag = false;         
      }

      return dtflag && smflag;
   }

   /**
    * This method determines whether or not the SCO supports the ability to find
    * an LMS provided API Adapter.
    * 
    * @return boolean - Flag indicating whether or not the SCO supports the
    *         ability to find an LMS provided API Adapter.
    *  
    */
   public boolean supportsFindAPI()
   {
      final boolean flag = mSCOSession.supportsFindAPI();

      mLogger.finest("Find API Support: " + flag); 

      return flag;
   }

   /**
    * This method determines whether or not the SCO supports the Execution State
    * API functions:
    * <ul>
    * <li><code>Initialize()</code></li>
    * <li><code>Terminate()</code></li>
    * </ul>
    * 
    * @return boolean - Flag indicating whether or not the SCO supports the
    *         Execution State API functions.
    */
   public boolean supportsExecutionState()
   {
	  final boolean flag = mSCOSession.supportsExecutionState();

      mLogger.finest("Session Methods Support: " + flag); 

      return flag;
   }

   /**
    * This method determines whether or not the SCO supports the State
    * Management API functions:
    * <ul>
    * <li><code>GetLastError()</code></li>
    * <li><code>GetErrorString()</code></li>
    * <li>GetDiagnostic()</li>
    * </ul>
    * 
    * @return boolean - Flag indicating whether or not the SCO supports the
    *         State Management API Functions.
    */
   public boolean supportsStateManagement()
   {
	   final boolean flag = mSCOSession.supportsStateManagement();

      mLogger.finest("Support Methods: " + flag); 

      return flag;
   }

   /**
    * This method determines whether or not the SCO called any State Management
    * API functions:
    * <ul>
    * <li><code>GetLastError()</code></li>
    * <li><code>GetErrorString()</code></li>
    * <li><code>GetDiagnostic()</code></li>
    * </ul>
    * 
    * @return boolean - Flag indicating whether or not the SCO called any State
    *         Management API Functions.
    *  
    */
   public boolean getStateManagementState()
   {
      return mSCOSession.getStateManagement();
   }

   /**
    * This method checks with the current SCO to determine if there were any
    * errors encountered with the conformance test.
    * 
    * @return boolean - Value that indicates whether there were any errors
    *         encountered in the conformance test.
    */
   public boolean isAnyErrors()
   {
	   return mSCOSession.getErrors() > 0;
   }

   /**
    * This method is overloaded to set up the link to the detailed log file when
    * presented in the summary log file. This is necessary to differentiate
    * between SCOs validated in a Content Package and a single SCO in the SCO
    * Test
    * 
    * @param iTestFileURL
    *           the URL of the detailed log. This method is only called if
    *           ReportFullConformance is desired
    */
   public void reportConformance(String iTestFileURL)
   {
      mDetailedLogFileURL = iTestFileURL;
      reportConformance(true);
   }
   
   /**
    * Returns if a sco failed
    * 
    * @return true if a sco failed
    */
   public boolean didAnyScoFail()
   {
      return  mSCOSession.getErrors() > 0 ;
   }

   /**
    * This function determines the type of conformance and reports that
    * conformance to the Test Suite Log. This method is also responsible for
    * building the feature set supported by the SCO.
    * 
    * @param iReportFullConformance
    *           true if last SCO and the full conformance report is desired
    *           (i.e. conformance labels)
    */

   public void reportConformance(final boolean iReportFullConformance)
   {
      mLogger.entering("SCOConformanceReporter", "reportConformance()");  

      final String cat = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"; 
      final String subCat = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"; 
      final String subCat1 = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"; 

      boolean apiSupp = true;

      // If the SCO:
      //    1. Found the API
      //    2. Called LMSInitialize() and LMSFinish() successfully
      //       2.1 And if the SCO called other API functions successfully
      //       2.2 And if the SCO called LMSGetValue() and LMSSetValue() with
      //           in the constraints of the Data Model
      // Then the SCO is given the following Conformance Label:
      // SCORM 1.2 Run-time Environment Conformant
      // Else if the SCO fails any of the above then it is deemed Non-Conformant

      // Check to see if the SCO was able to find the API and call
      // LMSInitialize() and
      // LMSFinish() successfully (1 and 2)
      //
      // If this is true, then the SCO gets the SCO RTE 1 Category Label
      final boolean minAPI = supportsMinAPI();

      // Check to see if other API calls were made and made successfully
      if ( mSCOSession.calledOtherAPIs() )
      {
         // Other API functions were called check to make
         // sure they were called correctly
         apiSupp = supportsAPI();
      }

      // check to see if the data model elements were used
      final boolean dmUsed = mSCOSession.isDMElementsUsed();
      boolean dmSupp = dmUsed;

      // if the data model elements were used, were they used correctly
      if ( dmUsed )
      {
         dmSupp = mSCOSession.isDataModelConformant();
      }
      // send the summary info to the log
      if ( minAPI && apiSupp )
      {
         // <hr>
         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.OTHER, 
               Messages.getString("SCOConformanceReporter.36")));
         
         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.SUBLOGHEAD, 
               Messages.getString("SCOConformanceReporter.14")));
         
         // <hr>
         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.OTHER, 
               Messages.getString("SCOConformanceReporter.36")));

         // Display features supported
         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.INFO, 
               Messages.getString("SCOConformanceReporter.17")));
         DetailedLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.INFO, 
               Messages.getString("SCOConformanceReporter.17")));
 
         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.INFO, 
               Messages.getString("SCOConformanceReporter.18")));
         DetailedLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.INFO, 
               Messages.getString("SCOConformanceReporter.18")));

         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.PASSED, 
               cat + Messages.getString("SCOConformanceReporter.19")));
         DetailedLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.PASSED, Messages
               .getString("SCOConformanceReporter.19")));


         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.INFO, cat + 
               Messages.getString("SCOConformanceReporter.20")));
         DetailedLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.INFO, Messages
               .getString("SCOConformanceReporter.20")));

         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.PASSED, 
               subCat + Messages.getString("SCOConformanceReporter.21")));
         DetailedLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.PASSED, Messages
               .getString("SCOConformanceReporter.21")));

         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.PASSED, 
               subCat + Messages.getString("SCOConformanceReporter.22")));
         DetailedLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.PASSED, Messages
               .getString("SCOConformanceReporter.22")));

         if ( apiSupp )
         {
            // Check to see if Data Tranfer API functions were supported
            if ( mSCOSession.supportsDataTransfer() )
            {
               SummaryLogMessageCollection.getInstance().addMessage(
                  new LogMessage(MessageType.INFO, 
                     cat + Messages.getString("SCOConformanceReporter.23")));
               DetailedLogMessageCollection.getInstance().addMessage(
                  new LogMessage(MessageType.INFO, Messages
                     .getString("SCOConformanceReporter.23")));

               final Vector dataXfer = mSCOSession.getDataTransferCalls();               
               if ( dataXfer.size() > 0 )
               {
                  for ( int i = 0; i < dataXfer.size(); i++ )
                  {
                     SummaryLogMessageCollection.getInstance().addMessage(
                        new LogMessage(MessageType.PASSED, 
                           subCat + (String) dataXfer.elementAt(i)));
                     
                     DetailedLogMessageCollection.getInstance().addMessage(
                        new LogMessage(MessageType.PASSED, (String) dataXfer
                           .elementAt(i)));
                  }
               }
               else
               {
                  SummaryLogMessageCollection.getInstance().addMessage(
                     new LogMessage(MessageType.INFO, 
                        subCat1 + Messages.getString("SCOConformanceReporter.24")));
                  
                  DetailedLogMessageCollection.getInstance().addMessage(
                     new LogMessage(MessageType.INFO, Messages
                        .getString("SCOConformanceReporter.24")));
               }
            }

            if ( mSCOSession.supportsStateManagement() )
            {
               SummaryLogMessageCollection.getInstance().addMessage(
                  new LogMessage(MessageType.INFO, 
                     cat + Messages.getString("SCOConformanceReporter.25")));
               
               DetailedLogMessageCollection.getInstance().addMessage(
                  new LogMessage(MessageType.INFO, Messages
                     .getString("SCOConformanceReporter.25")));

               Vector stateMgmt = mSCOSession.getStateMgmtCalls();               
               if ( stateMgmt.size() > 0 )
               {
                  for ( int i = 0; i < stateMgmt.size(); i++ )
                  {
                     SummaryLogMessageCollection.getInstance().addMessage(
                        new LogMessage(MessageType.PASSED, 
                           subCat + (String) stateMgmt.elementAt(i)));
                     
                     DetailedLogMessageCollection.getInstance().addMessage(
                        new LogMessage(MessageType.PASSED, 
                           (String) stateMgmt.elementAt(i)));
                  }
               }
               else
               { 
                  SummaryLogMessageCollection.getInstance().addMessage(
                     new LogMessage(MessageType.INFO, 
                        subCat1 + Messages.getString("SCOConformanceReporter.24")));
                  
                  DetailedLogMessageCollection.getInstance().addMessage(
                     new LogMessage(MessageType.INFO, Messages
                        .getString("SCOConformanceReporter.24")));
               }
            }

         }

         if ( dmUsed && dmSupp )
         {           
            SummaryLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.INFO, 
                  Messages.getString("SCOConformanceReporter.27")));

            DetailedLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.INFO, 
                  Messages.getString("SCOConformanceReporter.27")));
           
            SummaryLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.PASSED, 
                  cat + Messages.getString("SCOConformanceReporter.28")));

            DetailedLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.PASSED, 
                  Messages.getString("SCOConformanceReporter.28")));

            Vector dmItems = mSCOSession.getDataModelItems();

            for ( int i = 0; i < dmItems.size(); i++ )
            {
               SummaryLogMessageCollection.getInstance().addMessage(
                  new LogMessage(MessageType.PASSED, 
                     subCat + (String) dmItems.elementAt(i)));

               DetailedLogMessageCollection.getInstance().addMessage(
                     new LogMessage(MessageType.PASSED, 
                        (String) dmItems.elementAt(i)));
            }
         }

      }
      //    if we need to report full conformance we need to send the detailed
      // file link right here, before we send conformance labels to the
      // summary log
      if ( iReportFullConformance )
      {
        // SummaryLogMessageCollection.getInstance().addMessage(
        //    new LogMessage(MessageType.OTHER, ""));
         
         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.LINKSCO, mDetailedLogFileURL));

         SummaryLogMessageCollection.getInstance().addMessage(
            new LogMessage(MessageType.OTHER, 
               Messages.getString("SCOConformanceReporter.36")));
      }

      // send the correct label to the log
      // The "if" part is used by SCO Test
      if ( minAPI && apiSupp )
      {
         if ( ((!dmUsed) || (dmUsed && dmSupp)) && iReportFullConformance )
         {
            SummaryLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.SUBLOGTITLE,
                  Messages.getString("ScormConformance.1")));
            
            DetailedLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.OTHER,
                  Messages.getString("ScormConformance.1")));
             
            SummaryLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.CONFORMANT, 
                  ConformanceLabel.getConformanceText(
                  ConformanceLabel.SCORM2004, "SCO")));

            DetailedLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.CONFORMANT, 
                  ConformanceLabel.getConformanceText(
                  ConformanceLabel.SCORM2004, "SCO")));
             
            SummaryLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.CONFORMANT, 
                  ConformanceLabel.getConformanceText(
                  ConformanceLabel.SCORTE1, "SCO")));
            
            DetailedLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.CONFORMANT, 
                  ConformanceLabel.getConformanceText(
                  ConformanceLabel.SCORTE1, "SCO")));

         }
         else if ( dmUsed && !dmSupp )
         {
            if ( iReportFullConformance )
            {
               SummaryLogMessageCollection.getInstance().addMessage(
                  new LogMessage(MessageType.TERMINATE, 
                     ConformanceLabel.getConformanceText(
                     ConformanceLabel.SCORTE0, "SCO")));

               DetailedLogMessageCollection.getInstance().addMessage(
                  new LogMessage(MessageType.TERMINATE, 
                     ConformanceLabel.getConformanceText(
                     ConformanceLabel.SCORTE0, "SCO")));
            }
            else
            {
               SummaryLogMessageCollection.getInstance().addMessage(
                  new LogMessage(MessageType.FAILED, 
                     Messages.getString("SCOConformanceReporter.38")));

               DetailedLogMessageCollection.getInstance().addMessage(
                  new LogMessage(MessageType.FAILED, 
                     Messages.getString("SCOConformanceReporter.38")));
            }
         }
      }
      else
      {
         if ( iReportFullConformance )
         {
            SummaryLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.TERMINATE, 
                  ConformanceLabel.getConformanceText(
                  ConformanceLabel.SCORTE0, "SCO")));

            DetailedLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.TERMINATE, 
                  ConformanceLabel.getConformanceText(
                  ConformanceLabel.SCORTE0, "SCO")));
         }
         else
         { 
            SummaryLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.FAILED, 
                  Messages.getString("SCOConformanceReporter.38")));

            DetailedLogMessageCollection.getInstance().addMessage(
               new LogMessage(MessageType.FAILED, 
                  Messages.getString("SCOConformanceReporter.38")));
         }
      }
      
      SummaryLogMessageCollection.getInstance().addMessage(
         new LogMessage(MessageType.OTHER, "" ));
   }
}