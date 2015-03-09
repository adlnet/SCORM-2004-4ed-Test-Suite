package org.adl.testsuite.rte.lms;

import java.io.File;
import java.util.List;

import org.adl.logging.LmsLogger;
import org.adl.logging.LmsLoggerInterface;
import org.adl.testsuite.rte.lms.interfaces.FileManager;
import org.adl.testsuite.rte.lms.interfaces.TestCaseInterface;
import org.adl.testsuite.rte.lms.interfaces.TestCommunication;
import org.adl.testsuite.rte.lms.interfaces.TestManager;
import org.adl.testsuite.rte.lms.testcase.LMSTestCaseInterface;
import org.adl.testsuite.rte.lms.util.Command;
import org.adl.testsuite.rte.lms.util.LMSSession;
import org.adl.testsuite.rte.lms.util.Results;
import org.adl.testsuite.util.AppletList;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;

/**
 * Controls flow during the normal testing progress.
 * 
 * @author ADL Technical Team
 *
 */
public class LMSTestManager implements LmsLoggerInterface, TestCaseInterface, TestManager, FileManager, TestCommunication
{
   /**
    * Constant to hold the String indicator to have the log message sent to both
    * logs.
    */
   private static final String BL = "Both";
   
   /**
    * the LmsLoggerInterface object
    */
   private LmsLoggerInterface mLLI;
   
   /**
    * the TestCaseInterface object
    */
   private TestCaseInterface mTCI;
   
   /**
    * the FileManager object
    */
   private FileManager mFM;
   
   /**
    * the TestCommunication object
    */
   private TestCommunication mTC;
   
   /**
    * the LMSTestApplet object
    */
   private LMSTestApplet mLTA;
   
   /**
    * Overloaded constructor
    * 
    * @param iLLI - Instance of LmsLoggerInterface.
    * @param iTCI - Instance of TestCaseInterface.
    * @param iFM - Instance of FileManager.
    * @param iTC - Instance of TestCommunication.
    */
   public LMSTestManager(LmsLoggerInterface iLLI, TestCaseInterface iTCI, 
                          FileManager iFM, TestCommunication iTC)
   {
      mLLI = iLLI;
      mTCI = iTCI;
      mFM = iFM;
      mTC = iTC;
      registerManager(this);
      mLTA = (LMSTestApplet)AppletList.getApplet("LMSTestApplet");
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCommunication#registerManager()
    */
   public void registerManager(TestManager iTM)
   {
      mTC.registerManager(iTM);
      
   }

   /* (non-Javadoc)
    * @see org.adl.logging.LmsLoggerInterface#addMessage(java.lang.String, org.adl.util.LogMessage)
    */
   public void addMessage(String iLog, LogMessage iMsg)
   {
      if ( mLLI != null )
      {
         mLLI.addMessage(iLog, iMsg);
      }
      else
      {
         System.out.println("LMSTestManager.addMessage\n" +
                            "log: " + iLog + "\n" + 
                            "message type: " + iMsg.getMessageType() + "\n" +
                            "message text: " + iMsg.getMessageText());
      }
   }

   /* (non-Javadoc)
    * @see org.adl.logging.LmsLoggerInterface#viewCurrentLog()
    */
   public String viewCurrentLog()
   {
      return mLLI.viewCurrentLog();
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructions()
    */
   public String getInstructions()
   {
      String message = mTCI.getInstructions();
      if ( ! message.equals("") )
      {
         String instruction = mTCI.getInstructionsTitle() +
                              mTCI.getInstructionsUIR() + 
                              mTCI.getInstructionsLogin() +
                              mTCI.getInstructionsStart();
         mLTA.writeCurUserInstr(instruction);
      }
      return message;
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#commit()
    */
   public boolean commit()
   {
      return mTCI.commit();
   }
   
   /**
    * Used for commit from the save button being pushed
    * @return whether the commit was successful
    */
   public boolean commitFromSave()
   {
      return ((LMSTestCaseInterface)mTCI).commitFromSave();
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#evaluateResults(org.adl.testsuite.rte.lms.util.Results)
    */
   public boolean evaluateResults(Results iExpected)
   {
      return mTCI.evaluateResults(iExpected);
   }

   /*
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#evaluateUIQuestions(org.adl.testsuite.rte.lms.util.Results)
    */
   public boolean evaluateUIQuestions(String iExpected)
   {
      return mTCI.evaluateUIQuestions(iExpected);
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getCommands()
    */
   public Command getCommands()
   {
      return mTCI.getCommands();
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getCurrentUserInstructions()
    */
   public String getCurrentUserInstructions()
   {
      String message = mTCI.getCurrentUserInstructions();
      String uiMessage = "<br /><br />" + message;
      
      if ( mTCI.isDone())
      {
         uiMessage += "<br /><br /><br />" + mTCI.getInstructionsEnd();
      }

      mLTA.writeCurUserInstr(uiMessage);
      
      return message;
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getUIQuestions()
    */
   public String getUIQuestions()
   {
      String uiq = mTCI.getUIQuestions();
      
      if ( ! uiq.equals("") )
      {
         String message = "Please refer now to the LMS for questions regarding the LMS User Interface";
         mLTA.writeCurUserInstr(message);
      }
      
      return uiq; 
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#retry()
    */
   public boolean retry()
   {
      return mTCI.retry() && mLLI.retry();
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestManager#saveSession(java.lang.String)
    */
   public boolean saveSession(String sessionName)
   {
      commitFromSave();
      LMSSession.getInstance().setDetailedFileURI(getDetailedFileURI());
      File tempFile = new File(getDetailedFileURI());
      LMSSession.getInstance().setDetailedFileLastModified(tempFile.lastModified());
      LMSSession.getInstance().setObjName(sessionName);
      
      return save(sessionName, LMSSession.getInstance().getMementoOfSessionState());
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.FileManager#getListOfSaves()
    */
   public List<File> getListOfSaves()
   {
      return mFM.getListOfSaves();
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.FileManager#load(java.lang.String)
    */
   public Object load(String in)
   {
      return mFM.load(in);
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.FileManager#save(java.lang.Object)
    */
   public boolean save(String iName, Object in)
   {
      return mFM.save(iName, in);
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getTestCaseName()
    */
   public String getTestCaseName()
   {
      return mTCI.getTestCaseName();
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#isDone()
    */
   public boolean isDone()
   {
      return mTCI.isDone();
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#evaluateCorrectActivityLaunch(java.lang.String)
    */
   public boolean evaluateCorrectActivityLaunch(String iAct)
   {
      return mTCI.evaluateCorrectActivityLaunch(iAct);
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#evaluateCorrectTCLaunch(java.lang.String)
    */
   public boolean evaluateCorrectTCLaunch(String iPkg)
   {
      return mTCI.evaluateCorrectTCLaunch(iPkg);
   }
   
   /**
    * Returns the detailed file uri
    * 
    * @return the uri for the detailed log file
    */
   private String getDetailedFileURI()
   {
      if ( mLLI != null )
      {
         String uri = ((LmsLogger)mLLI).getDetailedFileURI();
         return uri;
      }
      return "LMSTestInitializer.getDetailedFileURI()";
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.FileManager#deleteObject(java.lang.String)
    */
   public boolean deleteObject(String iName)
   {
      return false;
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructionsEnd()
    */
   public String getInstructionsEnd()
   {
      // not used. Use getInstructions()
      return null;
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructionsLogin()
    */
   public String getInstructionsLogin()
   {
      // not used. Use getInstructions()
      return null;
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructionsStart()
    */
   public String getInstructionsStart()
   {
      // not used. Use getInstructions()
      return null;
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructionsTitle()
    */
   public String getInstructionsTitle()
   {
      // not used. Use getInstructions()
      return null;
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructionsUIR()
    */
   public String getInstructionsUIR()
   {
      // not used. Use getInstructions()
      return null;
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestManager#completed()
    */
   public void completed()
   {
      if ( mTCI.isDone())
      {
         String testPackage = LMSSession.getInstance().getCurrentPackage();
         if ( LMSSession.getInstance().wasLastTest() )
         {
            mLTA.callAllTestsCompletedWaitingState();
         }
         else
         {
            mLTA.callWaitingState();
         }
         
         if (testPackage.equals("OB-08a") ||
             testPackage.equals("OB-08b") ||
             testPackage.equals("OB-09a") ||
             testPackage.equals("OB-09b") ||
             testPackage.equals("CM-06") ||
             testPackage.equals("SX-05"))
         {
            askAboutSequencingStuff();
         }
         if ( isDone() )
         {
            if ( ((LMSTestCaseInterface)mTCI).overallTestCaseSuccessful() )
            {
               addMessage(BL, new LogMessage(MessageType.PASSED, "ADL LMS Test Content Package " + getTestCaseName() + " passed all tests"));
            }
            else
            {
               addMessage(BL, new LogMessage(MessageType.FAILED, "ADL LMS Test Content Package " + getTestCaseName() + " did not pass all tests. Please review the logs for detailed results."));
            }
            
         }
      }
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#askAboutSequencingStuff()
    */
   public void askAboutSequencingStuff()
   {
      mTCI.askAboutSequencingStuff();
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#reportActivityStatus()
    */
   public void reportActivityStatus()
   {
      mTCI.reportActivityStatus();
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestManager#writeToLog(java.lang.String)
    */
   public void writeToLog(String iMsg)
   {
      String message = iMsg;
      String[] temp1 = message.split("~");
      
      try
      {
         int msgType = Integer.parseInt(temp1[1]);
         LogMessage lm = new LogMessage(msgType, temp1[2]);
         addMessage(temp1[0], lm);
      }
      catch ( ArrayIndexOutOfBoundsException AIOBE )
      {
         System.out.println("Message from SCO failed : " +iMsg);
         AIOBE.printStackTrace();
      }
      
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestManager#terminateTest()
    */
   public void terminateTest()
   {
      LMSSession.getInstance().setOverallStatus(false);
      int numOfTest = LMSSession.getInstance().getNumOfChosenPackages();
      LMSSession.getInstance().setIndex(numOfTest);
      ((LMSTestCaseInterface)mTCI).setResultsEvalSuccessfulToFalse();
      LogMessage lm1 = new LogMessage(MessageType.FAILED, "This Activity did not pass all tests");
      addMessage("Summary", lm1);
      LogMessage lm2 = new LogMessage(MessageType.TERMINATE, "The test has been terminated");
      addMessage("Both", lm2);
      mLTA.writeCurUserInstr("The LMS Conformance Test has terminated.  Please view the log for details.");
      ((LMSTestCaseInterface)mTCI).setIsDone();
      completed();
      
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestManager#failTest()
    */
   public void failTest()
   {
      LMSSession.getInstance().setOverallStatus(false);
   }
}
