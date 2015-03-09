package org.adl.testsuite.rte.lms.interfaces;

import org.adl.testsuite.rte.lms.util.Command;
import org.adl.testsuite.rte.lms.util.Results;

public interface TestCommunication
{
   void registerManager(TestManager iTM);
   
   String getUIQuestions();
   
   boolean evaluateUIQuestions(String iResults);
   
   Command getCommands();
   
   boolean evaluateResults(Results iResults);
   
   String getCurrentUserInstructions();
   
   boolean commit();
   
   boolean evaluateCorrectTCLaunch(String iPkg);
   
   boolean evaluateCorrectActivityLaunch(String iAct);
   
   void completed();
   
   void writeToLog(String iMsg);
   
   void terminateTest();
   
   void failTest();
}