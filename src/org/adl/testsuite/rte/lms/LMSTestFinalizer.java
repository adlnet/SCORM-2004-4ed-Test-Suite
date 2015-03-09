package org.adl.testsuite.rte.lms;

import java.util.logging.Logger;

import org.adl.logging.LmsLogger;
import org.adl.testsuite.rte.lms.fileman.LMSFileManager;
import org.adl.testsuite.rte.lms.interfaces.FileManager;
import org.adl.testsuite.rte.lms.interfaces.Finalizer;
import org.adl.testsuite.rte.lms.util.LMSInfo;
import org.adl.testsuite.rte.lms.util.LMSSession;
import org.adl.testsuite.util.ConformanceLabel;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;
import org.adl.util.Messages;

/**
 * This class is responsible for handling all of the final steps of an LMS test
 * session.
 * 
 * @author ADL Technical Team
 *
 */
public class LMSTestFinalizer implements Finalizer
{
   /**
    * String representing the logtype Summary. 
    */
   private static final String SL = "Summary";
   
   /**
    * The file manager object - use this to load and save stuff
    */
   FileManager mFileMan;
   
   /**
    * Logger object used for debug logging
    */
   private Logger mLogger = Logger.getLogger("org.adl.util.debug.testsuite");
   
   /**
    * Constructor
    */
   public LMSTestFinalizer()
   {
   }
   
   /**
    * Creates an finalizer object with a specified file manager.
    * 
    * @param ifm The file management object to use to save and load files
    */
   public LMSTestFinalizer(FileManager ifm)
   {
      mFileMan = ifm;
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Finalizer#LMSfinalize()
    */
   public void lmsFinalize(LmsLogger iLogr)
   {
      mLogger.entering("LMSTestFinalizer","lmsFinalize()");
      if ( LMSSession.getInstance().getOverallStatus() )
      {
         if ( LMSInfo.getInstance().getNumOfTestCases() == 
               LMSSession.getInstance().getNumOfChosenPackages() )
         {
            iLogr.addMessage(SL, new LogMessage(MessageType.SUBLOGTITLE, 
               Messages.getString("ScormConformance.1") )); 
         
            iLogr.addMessage(SL, new LogMessage(MessageType.CONFORMANT, 
                  ConformanceLabel.getConformanceText(ConformanceLabel.SCORM2004, "LMS")));
            
            iLogr.addMessage(SL, new LogMessage(MessageType.CONFORMANT, 
                  ConformanceLabel.getConformanceLabel(ConformanceLabel.LMSCAM1)));
            
            iLogr.addMessage(SL, new LogMessage(MessageType.CONFORMANT, 
                  ConformanceLabel.getConformanceLabel(ConformanceLabel.LMSRTE1)));
            
            iLogr.addMessage(SL, new LogMessage(MessageType.CONFORMANT, 
                  ConformanceLabel.getConformanceLabel(ConformanceLabel.LMSSN1)));
            
            iLogr.addMessage(SL, new LogMessage(MessageType.SUBLOGHEAD, 
                                 ConformanceLabel.getCertificationStatement()));
         }
         else
         {
            iLogr.addMessage(SL, new LogMessage(MessageType.PASSED, 
                                             "All tests selected have passed."));
         }
         mFileMan.deleteObject(LMSSession.getInstance().getObjName());
      }
      else
      {
         if ( LMSInfo.getInstance().getNumOfTestCases() == 
               LMSSession.getInstance().getNumOfChosenPackages() )
         {
            iLogr.addMessage(SL, new LogMessage(MessageType.FAILED, 
                  ConformanceLabel.getConformanceLabel(ConformanceLabel.NONCONFORMANT)));
         }
         else
         {
            iLogr.addMessage(SL, new LogMessage(MessageType.FAILED, 
               "One or more tests have failed. Please review the logs for more information."));
         }
      }
      iLogr.addMessage(SL, new LogMessage(MessageType.ENDLOG, ""));
      ((LMSFileManager)mFileMan).deleteTempLog(iLogr.getLogFolder());
      mLogger.exiting("LMSTestFinalizer","lmsFinalize()");
   }
}
