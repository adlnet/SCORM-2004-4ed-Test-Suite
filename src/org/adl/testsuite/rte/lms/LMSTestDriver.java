package org.adl.testsuite.rte.lms;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.logging.Logger;

import org.adl.logging.LmsLogger;
import org.adl.testsuite.rte.lms.comm.LMSTestCommunication;
import org.adl.testsuite.rte.lms.fileman.LMSFileManager;
import org.adl.testsuite.rte.lms.interfaces.FileManager;
import org.adl.testsuite.rte.lms.interfaces.TestCaseInterface;
import org.adl.testsuite.rte.lms.interfaces.TestCommunication;
import org.adl.testsuite.rte.lms.interfaces.TestManager;
import org.adl.testsuite.rte.lms.interfaces.Initializer;
import org.adl.testsuite.rte.lms.interfaces.Finalizer;
import org.adl.testsuite.rte.lms.testcase.LMSTestCaseInterface;
import org.adl.testsuite.rte.lms.util.Command;
import org.adl.testsuite.rte.lms.util.LMSSession;
import org.adl.testsuite.rte.lms.util.Results;
import org.adl.testsuite.util.CTSEnvironmentVariable;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;

/**
 * @author ADL Technical Team
 *
 */
public class LMSTestDriver implements Initializer, TestManager
{
   /**
    * Token used to indicate to the logger that the message is to be sent to 
    * both logs.
    */
   private static final String BL = "Both";
   
   /**
    * Reference to a Logger object, which is responsible for handling all the 
    * logging for this LMS test session.
    */
   LmsLogger mLmsLogger;
   
   /**
    * Reference to an Initializer object, which is responsible for handling all 
    * of the initialization needed for this LMS test session.
    */
   Initializer mInitializer;
   
   /**
    * Reference to a Finializer object, which is responsible for handling all 
    * of the finalization needed for this LMS test session.
    */
   Finalizer mFinalizer;
   
   /**
    * Reference to a TestManager object, which is responsible for handling all 
    * testing objects, test communication and testing state.
    */
   TestManager mTestManager;
   
   /**
    * Reference to a TestCaseInterface object, which is responsible for handling 
    * all related to Test Cases and test result comparisons.
    */
   TestCaseInterface mTCInterface;
   
   /**
    * Reference to a FileManager object, which is responsible for handling all 
    * file reading and writing needs for the LMS testing session.
    */
   FileManager mFileManager;
   
   /**
    * Reference to a TestCommunication object, which is responsible for handling 
    * all communication between test (server) and sco (client) pieces.
    */
   TestCommunication mTestCommunication;
   
   /**
    * The variable of the environment.
    */
   String mEnvironmentVariable;
   
   /**
    * Indicator used to inform the system if the Initializer is set up.
    */
   private boolean mInitializerSetup = false;
   
   /**
    * Logger object used for debug logging
    */
   private Logger mLogger = Logger.getLogger("org.adl.util.debug.testsuite");
   
   /**
    * Default constructor
    * @param iEnvVar environment variable value
    */
   public LMSTestDriver(String iEnvVar)
   {
      mLogger.entering("LMSTestDriver","LMSTestDriver");
      mEnvironmentVariable = iEnvVar;
      mLogger.exiting("LMSTestDriver","LMSTestDriver");
   }
   
   public String getADLTitle()
   {
      if(!mInitializerSetup)
         setupInitializer();
      return mInitializer.getADLTitle();
   }
   
   /**
    * Returns the name of the Test Suite
    * 
    * @return the name of the Test Suite
    */
   public String getCTSName()
   {
      if(!mInitializerSetup)
         setupInitializer();
      return mInitializer.getCTSName();
   }

   /**
    * Returns the CTS version.
    * 
    * @return the CTS version
    */
   public String getCTSVersion()
   {
      if(!mInitializerSetup)
         setupInitializer();
      return mInitializer.getCTSVersion();
   }
   
   /**
    * Returns the name of the Test
    * @return the name of the Test
    */
   public String getTestName()
   {
      if(!mInitializerSetup)
         setupInitializer();
      return mInitializer.getTestName();
   }

   /* 
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#apiIsChosen()
    */
   public boolean apiIsChosen()
   {
      mLogger.entering("LMSTestDriver","apiIsChosen()");
      if(!mInitializerSetup)
         setupInitializer();
      return mInitializer.apiIsChosen();
   }
   
   /*  (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#dmiIsChosen()
    */
   public boolean dmiIsChosen()
   {
      mLogger.entering("LMSTestDriver","dmiIsChosen()");
      if(!mInitializerSetup)
         setupInitializer();
      return mInitializer.dmiIsChosen();
   }

   /*  (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#checkEnvironmentInfo()
    */
   public void checkEnvironmentInfo()
   {
      mLogger.entering("LMSTestDriver","checkEnvironmentInfo()");
      if(!mInitializerSetup)
         setupInitializer();
      mInitializer.checkEnvironmentInfo();
   }

   /*  (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#getTestPackages()
    */
   public List getTestPackages()
   {
      mLogger.entering("LMSTestDriver","getTestPackages()");
      if(!mInitializerSetup)
         setupInitializer();
      return mInitializer.getTestPackages();
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#setChosenTestPackages(java.util.List)
    */
   public void setChosenTestPackages(List iList)
   {
      mLogger.entering("LMSTestDriver","setChosenTestPackages()");
      if(!mInitializerSetup)
         setupInitializer();
      mInitializer.setChosenTestPackages(iList);
   }

   public void setL1EnrollSuccess(boolean iL1EnrollSuccess)
   {
      mLogger.entering("LMSTestDriver","setL1EnrollSuccess()");
      if(!mInitializerSetup)
         setupInitializer();
      mInitializer.setL1EnrollSuccess(iL1EnrollSuccess);
   }
   
   public void setL2EnrollSuccess(boolean iL2EnrollSuccess)
   {
      mLogger.entering("LMSTestDriver","setL2EnrollSuccess()");
      if(!mInitializerSetup)
         setupInitializer();
      mInitializer.setL2EnrollSuccess(iL2EnrollSuccess);
   }

   /*  (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#setLearnerIDInfo(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   public void setLearnerIDInfo(String iL1Name, String iL1ID, String iL2Name, String iL2ID)
   {
      mLogger.entering("LMSTestDriver","setLearnerIDInfo()");
      if(!mInitializerSetup)
         setupInitializer();
      mInitializer.setLearnerIDInfo(iL1Name, iL1ID, iL2Name, iL2ID);
   }

   /*  (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#setTestingInfo(java.lang.String, java.lang.String, java.lang.String)
    */
   public void setTestingInfo(String iLMSName, String iLMSVersion, String iLMSVendor)
   {
      mLogger.entering("LMSTestDriver","setTestingInfo()");
      if(!mInitializerSetup)
         setupInitializer();
      mInitializer.setTestingInfo(iLMSName, iLMSVersion, iLMSVendor);
   }
   
   /*  (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Initializer#getListOfSaves()
    */
   public List getListOfSaves()
   {
      mLogger.entering("LMSTestDriver","getListOfSaves()");
      if(!mInitializerSetup)
         setupInitializer();
      return mInitializer.getListOfSaves();
   }

   public String getL1Name()
   {
      mLogger.entering("LMSTestDriver","getL1Name()");
      if(!mInitializerSetup)
         setupInitializer();
      return mInitializer.getL1Name();
   }

   public String getL2Name()
   {
      mLogger.entering("LMSTestDriver","getL2Name()");
      if(!mInitializerSetup)
         setupInitializer();
      return mInitializer.getL2Name();
   }
   
   /*  (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.Finalizer#lmsFinalize()
    */
   public void lmsFinalize()
   {
      mLogger.entering("LMSTestDriver","lmsFinalize()");
      mFinalizer = new LMSTestFinalizer(mFileManager);
      mFinalizer.lmsFinalize(mLmsLogger);
      mLogger.exiting("LMSTestDriver","lmsFinalize()");
   }

   /*  (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestManager#evaluateResults(org.adl.testsuite.rte.lms.util.Results)
    */
   public boolean evaluateResults(Results in)
   {
      mLogger.entering("LMSTestDriver","evaluateResults()");
      setupTestManager();
      return mTestManager.evaluateResults(in);
   }

   public boolean evaluateCorrectTCLaunch(String iPkg)
   {
      mLogger.entering("LMSTestDriver", "evaluateCorrectTCLaunch()");
      setupTestManager();
      return mTestManager.evaluateCorrectTCLaunch(iPkg);
   }
   
   public boolean evaluateCorrectActivityLaunch(String iAct)
   {
      mLogger.entering("LMSTestDriver", "evaluateCorrectActivityLaunch()");
      setupTestManager();
      return mTestManager.evaluateCorrectTCLaunch(iAct);
   }

   /*  (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestManager#getCommands()
    */
   public Command getCommands()
   {
      mLogger.entering("LMSTestDriver","getCommands()");
      setupTestManager();
      return mTestManager.getCommands();
   }

   /*  (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestManager#getCurrentUserInstructions()
    */
   public String getCurrentUserInstructions()
   {
      mLogger.entering("LMSTestDriver","getCurrentUserInstructions()");
      setupTestManager();
      return mTestManager.getCurrentUserInstructions();
   }

   /* 
    * @see org.adl.testsuite.rte.lms.interfaces.TestManager#getUIQuestions()
    */
   public String getUIQuestions()
   {
      mLogger.entering("LMSTestDriver","getUIQuestions()");
      setupTestManager();
      return mTestManager.getUIQuestions();
   }

   public String getTestCaseName()
   {
      mLogger.entering("LMSTestDriver","getTestCaseName()");
      return mTCInterface.getTestCaseName();
   }
   
   /**
    * called when the Applet's stop() method or the abort() method in this class
    */
   public void testStopped()
   {
      mLmsLogger.testStopped();
      ((LMSFileManager)mFileManager).deleteTempLog(mLmsLogger.getLogFolder());
   }
   
   /*
    */
   public boolean commit()
   {
      mLogger.entering("LMSTestDriver","commit()");
      // can't commit if test manager isn't started
      if (mTestManager == null)
      {
         mLogger.exiting("LMSTestDriver","commit()");
         return false;
      }
      mLogger.exiting("LMSTestDriver","commit()");
      return mTestManager.commit();
   }

   public String getInstructions()
   {
      setupTestManager();
      return mTestManager.getInstructions();
   }

   public boolean evaluateUIQuestions(String in)
   {
      setupTestManager();
      return mTestManager.evaluateUIQuestions(in);
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestManager#writeToLog(java.lang.String)
    */
   public void writeToLog(String iMsg)
   {
      setupTestManager();
      mTestManager.writeToLog(iMsg);
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestManager#terminateTest()
    */
   public void terminateTest()
   {
      setupTestManager();
      mTestManager.terminateTest();
      testStopped();
      lmsFinalize();
   }

   /**
    * Writes the prelim info to the logs.
    */
   public void writePreliminaryInfo()
   {
      mLogger.entering("LMSTestDriver","writePreliminaryInfo()");
      mLmsLogger.writePreliminaryInfo(true);
      mLogger.exiting("LMSTestDriver","writePreliminaryInfo()");
   }
   
   /**
    * allows the user to view the current detailed log
    * @return String path to the current detailed log file
    */
   public String viewLog()
   {
      return mLmsLogger.viewCurrentLog();
   }
   
   public boolean retry()
   {
       return mTestManager.retry();
   }
   
   public boolean saveSession(String sessionName)
   {
      boolean result = false;
      mLogger.entering("LMSTestDriver", "saveSession()");
      result = mTestManager.saveSession(sessionName);
      mLogger.exiting("LMSTestDriver", "saveSession() result is: " + result);
      return result;
   }
   
   /**
    * Deletes the session object.
    * 
    * @param iSessionName the name of the session.
    * 
    * @return whether the delete was successful
    */
   public boolean deleteSession(String iSessionName)
   {
      return mFileManager.deleteObject(iSessionName);
   }

   /**
    * method invoked when the user has hit the 'abort' button on the browser
    * interface
    */
   public void abort()
   {
      mLogger.entering("LMSTestDriver","abort()");
      LMSSession.getInstance().setOverallStatus(false);
      addMessage("Summary", MessageType.ABORT, "The user has aborted the test");
   }
   
   /**
    * used to start brand new logs for a brand new test
    */
   public void startNewTest()
   {
      mLogger.entering("LMSTestDriver","startNewTest()");
      startNewTest(mEnvironmentVariable);
      mLogger.exiting("LMSTestDriver","startNewTest()");
   }
   
   /**
    * used to load logs that have already been created from a previously saved
    * test
    * @param iSessionName name of the saved session file the user wants to load
    * @return true if the load is successful, false otherwise
    */
   public boolean startSavedTest(String iSessionName)
   {
      mLogger.entering("LMSTestDriver","startSavedTest(S)");
      boolean loadSuccessful = startSavedTest(mEnvironmentVariable, iSessionName);
      mLogger.exiting("LMSTestDriver","startSavedTest()");
      
      return loadSuccessful;
   }
   
   /*
    * These are here so that we don't hurt the interface.
    * @see org.adl.testsuite.rte.lms.interfaces.Initializer#startNewTest(java.lang.String)
    */
   public void startNewTest(String iEnvVar)
   {
      mLogger.entering("LMSTestDriver","startNewTest(L,S)");
      if(!mInitializerSetup)
         setupInitializer();
      mInitializer.startNewTest(iEnvVar);
      mLogger.exiting("LMSTestDriver","startNewTest(L,S)");
   }

   /*
    * These are here so that we don't hurt the interface.
    * @see org.adl.testsuite.rte.lms.interfaces.Initializer#startSavedTest(java.lang.String, java.lang.String)
    */
   public boolean startSavedTest(String iEnvVar, String iSessionName)
   {
      mLogger.entering("LMSTestDriver","startSavedTest(L,S,S)");
      if(!mInitializerSetup)
         setupInitializer();
      boolean loadSuccessful = mInitializer.startSavedTest(iEnvVar, iSessionName);
      mLogger.exiting("LMSTestDriver","startSavedTest(L,S,S)");
      
      return loadSuccessful;
   }
   
   private void addMessage(String iLog, int iMsgType, String iMsg)
   {
      if ( mLmsLogger == null )
      {
         PrivilegedStartLogger psl = new PrivilegedStartLogger();
         mLmsLogger = (LmsLogger)AccessController.doPrivileged(psl);
      }
      
      mLmsLogger.addMessage(iLog, new LogMessage(iMsgType, iMsg));
   }
   
   /**
    * Sets up the Initializer 
    */
   private void setupInitializer()
   {
      mLogger.entering("LMSTestDriver","setupInitializer()");
      if ( mInitializer == null )
      {
         if ( mLmsLogger == null )
         {
            PrivilegedStartLogger psl = new PrivilegedStartLogger();
            mLmsLogger = (LmsLogger)AccessController.doPrivileged(psl);
         }
         if ( mFileManager == null )
         {
            mFileManager = new LMSFileManager(mEnvironmentVariable);
         }
         mInitializer = new LMSTestInitializer(mLmsLogger, mFileManager);
      }
      mInitializerSetup = true;
      mLogger.exiting("LMSTestDriver","setupInitializer()");
   }
   
   /**
    * TODO
    */
   private void setupTestManager()
   {
      mLogger.entering("LMSTestDriver","setupTestManager()");
      if ( mTestManager == null )
      {
         if ( mLmsLogger == null )
         {
            // I dont understand why simply instantiating LmsLogger requires
            // the privileged action class...
            PrivilegedStartLogger psl = new PrivilegedStartLogger();
            mLmsLogger = (LmsLogger)AccessController.doPrivileged(psl);
         }
         if ( mTCInterface == null )
         {
            mTCInterface = new LMSTestCaseInterface(mLmsLogger);
         }
         if ( mFileManager == null )
         {
            mFileManager = new LMSFileManager(CTSEnvironmentVariable.getCTSEnvironmentVariable());
         }
         if ( mTestCommunication == null )
         {
            mTestCommunication = new LMSTestCommunication();
         }
         mTestManager = new LMSTestManager(mLmsLogger, mTCInterface, mFileManager, mTestCommunication);
      }
      mLogger.exiting("LMSTestDriver","setupTestManager()");   
   }
   
   private class PrivilegedStartLogger implements PrivilegedAction
   {      
      PrivilegedStartLogger()
      {
      }
      
      public Object run()
      {
         return new LmsLogger();
      }
      
   }// end class PrivilegedViewLog

   public void completed()
   {
      // TODO Auto-generated method stub
      
   }
   
   public void failTest()
   {
      mLogger.entering("LMSTestDriver","failTest()");
      // can't commit if test manager isn't started
      if (mTestManager == null)
      {
         mLogger.exiting("LMSTestDriver","failTest()");
      }
      mLogger.exiting("LMSTestDriver","failTest()");
   }

}
