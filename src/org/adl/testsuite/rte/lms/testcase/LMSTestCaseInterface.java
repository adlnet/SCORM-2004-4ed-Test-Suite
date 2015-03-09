package org.adl.testsuite.rte.lms.testcase;

import java.util.Iterator;

import javax.swing.JOptionPane;

import org.adl.logging.LmsLoggerInterface;
import org.adl.logging.LogFileGenerator;
import org.adl.testsuite.rte.lms.interfaces.TestCaseInterface;
import org.adl.testsuite.rte.lms.util.Command;
import org.adl.testsuite.rte.lms.util.LMSInfo;
import org.adl.testsuite.rte.lms.util.LMSMessages;
import org.adl.testsuite.rte.lms.util.LMSParser;
import org.adl.testsuite.rte.lms.util.LMSSession;
import org.adl.testsuite.rte.lms.util.Results;
import org.adl.testsuite.util.CTSEnvironmentVariable;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;
/**
 * Interface into the TestCase Module.
 * 
 * @author ADL Technical Team
 */
public class LMSTestCaseInterface implements TestCaseInterface
{
   /**
    * Indicator to send logging messages to the detailed log
    */
   private static final String DETAILED_KEY = "Detailed";
   
   /**
    * Indicator to send logging messages to the summary log
    */
   private static final String SUMMARARY_KEY = "Summary";
   
   /**
    * Indicator to send logging messages to the both logs
    */
   private static final String BOTH_KEY = "Both";
   
   /**
    * The list of Commands being called for the current activity. This is sent 
    * to the evaluator for tracking and reporting info.
    */
   private transient Command mListOfCommands;
   
   /**
    * The expected Results. 
    */
   private transient Results mExpectedResults;
   
   /**
    * The parser object.
    */
   private transient LMSParser mPC;
   
   /**
    * The test case object
    */
   private transient LMSTestCase mTC;
   
   /**
    * The test case evaluator object
    */
   private final transient TestCaseEvaluator mTCE;
   
   /**
    * the logging object
    */
   private final transient LmsLoggerInterface mLI;
   
   /**
    * holder of the success of the ui evaluation
    */
   private transient boolean mUIEvalSuccessful = true;
   
   /**
    * holder of the success of the results evaluation
    */
   private transient boolean mResultsEvalSuccessful = true;
   
   /**
    * holder of the success of the launch eval
    */
   private transient boolean mLaunchEvalSuccessful = true;
   
   /**
    * holder of the sequencing success
    */
   private transient boolean mSequenceSuccessful = true;
   
   /**
    * the name of the current detailed log
    */
   private transient String mCurrentDLName = "";
   
   /**
    * Stores the current list of ui questions
    */
   private transient String mCurrentUIQuestions = "";
   
   /**
    * Indicates whether this testing session was saved
    */
   private transient boolean mWasSaved = false;
   
   /**
    * Overloaded constructor
    * @param iLI - Instance of LmsLoggerInterface.
    */
   public LMSTestCaseInterface(final LmsLoggerInterface iLI)
   {
      mLI = iLI;
      mTCE = new TestCaseEvaluator(iLI);
      nextLMSTestCase();
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.testcase.TestCaseInterface#evaluateCorrectTCLaunch(java.lang.String)
    */
   public boolean evaluateCorrectTCLaunch(final String iPkg)
   {
      return mTCE.evaluateCorrectTCLaunch(mTC.getName(),iPkg);
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.testcase.TestCaseInterface#evaluateCorrectActivityLaunch(java.lang.String)
    */
   public boolean evaluateCorrectActivityLaunch(final String iAct)
   {
      final boolean launchOK = mTCE.evaluateCorrectActivityLaunch(mTC.getActivityName(), iAct);
      mLaunchEvalSuccessful &= launchOK;
      
      return mLaunchEvalSuccessful;
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.testcase.TestCaseInterface#getUIQuestions()
    */
   public String getUIQuestions()
   {
      mPC = new LMSParser();
      mCurrentUIQuestions = mPC.parseUIKeys(mTC.getUIQuestionsKey());
      return mCurrentUIQuestions;
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.testcase.TestCaseInterface#getCommands()
    */
   public Command getCommands()
   {
      final Iterator<String> cmdKeyItr = mTC.getCommandsKey();
      Command currCommands = new Command();
      mPC = new LMSParser(cmdKeyItr, currCommands);
      mPC.parseCommands();
      mExpectedResults = mPC.getExpectedResults();

      currCommands = mPC.getCommands();
      mListOfCommands = currCommands;
      return currCommands;
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.testcase.TestCaseInterface#evaluateUIQuestions(org.adl.testsuite.rte.lms.util.Results)
    */
   public boolean evaluateUIQuestions(final String iReturned)
   {
      mUIEvalSuccessful = mUIEvalSuccessful &&
         mTCE.evaluateUIQuestions(mCurrentUIQuestions, mTC.getUIAnswers(), iReturned);
      return mUIEvalSuccessful;
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.testcase.TestCaseInterface#evaluateResults(org.adl.testsuite.rte.lms.util.Results)
    */
   public boolean evaluateResults(final Results iReturned)
   {
      mResultsEvalSuccessful &= mTCE.evaluateResults(mListOfCommands, mExpectedResults, iReturned);
      return mResultsEvalSuccessful;
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.testcase.TestCaseInterface#getCurrentUserInstructions()
    */
   public String getCurrentUserInstructions()
   {
      String instructions = mTC.getCurrentUserInstructions();
      if(instructions.indexOf('~') == -1)
      {
         instructions = LMSMessages.Instructions.getString(instructions);
      }
      else
      {
         
         final String[] cuiSplit = instructions.split("~");
         final int minusInstr = cuiSplit.length - 1;
         final Object[] params = new String[minusInstr];
         System.arraycopy(cuiSplit, 1, params, 0, minusInstr);
         
         instructions = LMSMessages.Instructions.getString(cuiSplit[0], params);         
      }
      
      reportActivityStatus();
      
      return instructions;
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.testcase.TestCaseInterface#isDone()
    */
   public boolean isDone()
   {
      return mTC.isDone();
   }
   
   public void setIsDone()
   {
      mTC.setIsDone();
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.testcase.TestCaseInterface#retry()
    */
   public boolean retry()
   {
      mTC.resetState();
      resetSucessStatus();
      return true;
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.testcase.TestCaseInterface#commit()
    */
   public boolean commit()
   {
      final boolean commit = doCommit(false);
      nextLMSTestCase();
      return commit;
   }
   
   /**
    * Method called from Save so that the system can handle logging specific to 
    * saving a session.
    * 
    * @return boolean of the success of the save
    */
   public boolean commitFromSave()
   {
      return doCommit(true);
   }
   
   /**
    * the generic commit call that will handle both requests from a continue and 
    * from a save
    * 
    * @param iFromSave indicator of if it was from a save
    * 
    * @return save success
    */
   private boolean doCommit(final boolean iFromSave)
   {
      if ( ! mWasSaved )
      {
         LMSSession.getInstance().setOverallStatus(mLaunchEvalSuccessful && 
                                                   mUIEvalSuccessful && 
                                                   mResultsEvalSuccessful);
         addMessage(SUMMARARY_KEY, MessageType.LINKLMS, mCurrentDLName);
         addMessage(SUMMARARY_KEY, MessageType.OTHER, "&lt;HR&gt;");
         addMessage(DETAILED_KEY, MessageType.ENDLOG, "");
         
         resetSucessStatus();
      }
      
      if(iFromSave)
      {
         addMessage(SUMMARARY_KEY, MessageType.SAVE, "");
      }
      
      mWasSaved = iFromSave;
      
      return true;
   }
   
   /**
    * Moves to the next test packages. Also responsible for handling logging 
    * starts and ends for detailed logs.
    */
   private final void nextLMSTestCase()
   {
      final String nextTCtoGet = LMSSession.getInstance().getTestCase();
      
      if( "".equals(nextTCtoGet) ) //$NON-NLS-1$
      {
         mTC = new LMSTestCase();
      }
      else
      {
         mTC = LMSInfo.getInstance().getTestCase(nextTCtoGet);
         final LogFileGenerator mLfg = new LogFileGenerator(CTSEnvironmentVariable.getCTSEnvironmentVariable()); 
         mCurrentDLName = mLfg.getLogName("LMS",getTestCaseName(), true);
         addMessage(DETAILED_KEY, MessageType.NEWLOG, mCurrentDLName);
      }      
   }
   
   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructions()
    */
   public String getInstructions()
   {
      String instructions;
      String testCaseName = getTestCaseName();
      try
      {
         instructions = getInstructionsPurpose() + getInstructionsSeqInfo() 
                      + getInstructionsTestType();
         
      }
      catch(NullPointerException e)
      {
         instructions = "";
      }
      
      if ( !"".equals(instructions) )
      {
         //addMessage(BL, MessageType.OTHER, "");
         if ("DDM".equals(testCaseName))
         {
            testCaseName = "DDMa";
         }
         addMessage(BOTH_KEY, MessageType.INFO, "Start ADL LMS Test Content Package " + testCaseName);
      }
      
      return instructions;
   }

   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructionsTitle()
    */
   public String getInstructionsTitle()
   {
      final String packageName = mTC.getName();
      final String packageUser = getPackageUserName(packageName);
      return LMSMessages.Instructions.getString("title", packageName, packageUser);
   }
   
   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructionsUIR()
    */
   public String getInstructionsUIR()
   {
      String instUIR = mTC.getInstructionsUIR();
      
      if (!"".equals(instUIR))
      {
         instUIR = LMSMessages.Instructions.getString(mTC.getInstructionsUIR());
      }
      
      return instUIR;
   }

   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructionsLogin()
    */
   public String getInstructionsLogin()
   {
      final String packageName = mTC.getName();
      final String packageUser = getPackageUserName(packageName);
      return LMSMessages.Instructions.getString("login", packageUser);
   }

   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructionsStart()
    */
   public String getInstructionsStart()
   {
      String packageName = mTC.getName();
      String endParam = mTC.getInstructionsStart();
      if ( ( !"DDMa".equals(endParam) ) &&
           ( !".".equals(endParam) ) )
      {
         final String temp = LMSMessages.Instructions.getString("startTOC", endParam);
         endParam = " " + temp;
      }
      if("DDMa".equals(endParam))
      {
         packageName = "<b>" + endParam + "</b>";
         endParam = ".";
      }
      return LMSMessages.Instructions.getString("start", 
             packageName, endParam);
   }

   /**
    * Provides a way for the SCO to terminate the test and change the ResultsEvalSuccessful status.
    */
   public void setResultsEvalSuccessfulToFalse()
   {
      mResultsEvalSuccessful = false;
   }
   /**
    * helper method used to get the purpose section of the instructions
    * 
    * @return the string of the purpose
    */
   private String getInstructionsPurpose()
   {
      String purpose = mTC.getInstructionsPurpose();
      if(purpose.indexOf('~') != -1)
      {
         final String[] purposeSplit = purpose.split("~");
         final String seqEnd = LMSMessages.Instructions.getString(purposeSplit[1]);
   
         purpose = LMSMessages.Instructions.getString("purpose", 
                     LMSMessages.Instructions.getString(purposeSplit[0], seqEnd));
       
      }
      else
      {
         purpose = LMSMessages.Instructions.getString("purpose", 
               LMSMessages.Instructions.getString(purpose));
       
      }
      return purpose;
   }

   /**
    * Gets the instructions related to how to start for the current test package
    * to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   private String getInstructionsRTDInit()
   {
      return LMSMessages.Instructions.getString(mTC.getInstructionsRTDInit());
   }

   /**
    * Gets the instructions related to how to start for the current test package
    * to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   private String getInstructionsSeqInfo()
   {
      String seqInfo = mTC.getInstructionsSeqInfo();
      
      if (!"".equals(seqInfo))
      {
         seqInfo = LMSMessages.Instructions.getString("sequencingBehavior",
               getInstructionsRTDInit(), mTC.getInstructionsSeqInfo());
      }
      return seqInfo;
   }

   /**
    * Gets the instructions related to how to start for the current test package
    * to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   private String getInstructionsTestType()
   {
      String testType = mTC.getInstructionsTestType();
      if(testType.indexOf('~')!= -1)
      {
         final String[] testTypeSplit = testType.split("~");
         testType = LMSMessages.Instructions.getString(testTypeSplit[0])
                  + LMSMessages.Instructions.getString(testTypeSplit[1]);
      }
      else
      {
         testType = LMSMessages.Instructions.getString(testType);
      }
      
      return testType;
   }
   
   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getInstructionsEnd()
    */
   public String getInstructionsEnd()
   {
      final String packageName = mTC.getName();
      String endInstruction = mTC.getInstructionsEnd();
     
      if(endInstruction.indexOf('~')!= -1)
      {
         final String[] endInstSplit = endInstruction.split("~");
         final String seqEnd = LMSMessages.Instructions.getString(endInstSplit[1]);
   
         endInstruction = LMSMessages.Instructions.getString(endInstSplit[0], 
                          seqEnd, packageName);
       
      }
      else
      {
         endInstruction = LMSMessages.Instructions.getString(endInstruction,
                          packageName);
       
      }
      
      return endInstruction;
   }
   
   /*
    * (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#reportActivityStatus()
    */
   public void reportActivityStatus()
   {
      if ( mTCE.reportActivityStatus() )
      {
         addMessage(SUMMARARY_KEY, MessageType.PASSED, "This Activity passed all tests");
      }
      else
      {
         addMessage(SUMMARARY_KEY, MessageType.FAILED, "This Activity did not pass all tests");
      }
   }
   
   /**
    * gets the name of the package
    * 
    * @param iPackName The name of the package.
    * 
    * @return The user name for the given package
    */
   private String getPackageUserName(final String iPackName)
   {
      String userName;
      if ("API".equals(iPackName))
      {
         userName = LMSSession.getInstance().getL1Name();
      }
      else
      {
         userName = LMSSession.getInstance().getL2Name();
      }
      return userName;
   }

   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#getTestCaseName()
    */
   public String getTestCaseName()
   {
      return mTC.getName();
   }
   
   /**
    * Resets the success status. Returns the value to true.
    */
   private void resetSucessStatus()
   {
      mLaunchEvalSuccessful = true;
      mUIEvalSuccessful = true;
      mResultsEvalSuccessful = true;
   }
   
   /**
    * The overall success of the current test case.
    * 
    * @return the success of the current test case.
    */
   public boolean overallTestCaseSuccessful()
   {
      return mLaunchEvalSuccessful && mUIEvalSuccessful && mResultsEvalSuccessful && mSequenceSuccessful;
   }
   
   /**
    * Adds a message to CTS logs.
    * 
    * @param iLog The log the we want to add the message to... "Detailed", "Summary", "Both"
    * 
    * @param iType The type of message to add. Use the MessageType object.
    * 
    * @param iMessage The message to be sent to the log(s).
    */
   private void addMessage(final String iLog, final int iType, final String iMessage)
   {
      if ( mLI != null )
      {
         mLI.addMessage(iLog, new LogMessage(iType, iMessage));
      }
      else
      {
         System.out.println("LMSTestManager.addMessage\n" +
                            "log: " + iLog + "\n" + 
                            "message type: " + iType + "\n" +
                            "message text: " + iMessage);
      }
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.rte.lms.interfaces.TestCaseInterface#askAboutSequencingStuff()
    */
   public void askAboutSequencingStuff()
   {
      final int userResponse = JOptionPane.showConfirmDialog(null,"Has the Sequencing " +
            "Session ended?", "", JOptionPane.YES_NO_OPTION);
           
      if ( userResponse == 0 )
      {
         addMessage(BOTH_KEY, MessageType.PASSED, "The tester indicated  " +
                                            "that the Sequencing Session has ended");
      }
      else
      {
         addMessage(BOTH_KEY, MessageType.FAILED, "The tester indicated  " +
                                            "that the Sequencing Session has not ended");
         mSequenceSuccessful = false;
      }
   
      LMSSession.getInstance().setOverallStatus(mSequenceSuccessful);
   }
}
