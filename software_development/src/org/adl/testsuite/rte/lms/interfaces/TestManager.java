package org.adl.testsuite.rte.lms.interfaces;

import org.adl.testsuite.rte.lms.util.Command;
import org.adl.testsuite.rte.lms.util.Results;

/**
 * This interface is responsible for defining the default behavior of a Test 
 * Manager.
 * @author ADL Technical Team
 */
public interface TestManager
{
   /**
    * Evaluates the Results sent back from the LMS.
    * @param in Results of the current LMS Command tests.
    * @return boolean representing whether or not the tests passed.
    */
   boolean evaluateResults(Results in);
   
   /**
    * Evaluates that the test case id passed in matches the expected test case id.
    * @param iPkg The id of the test case.
    * @return boolean representing if the id matched the expected id.
    */
   boolean evaluateCorrectTCLaunch(String iPkg);
   
   /**
    * Evaluates that the activity id passed in matches the expected activity id.
    * @param iAct The id of the activity.
    * @return boolean representing if the id matched the expected id.
    */
   boolean evaluateCorrectActivityLaunch(String iAct);
   
   /**
    * Evaluates the UI questions results.
    * @param in String of the yes or no answers to the UI questions represented
    *         by Y and N.
    * @return boolean representing whether the correct answers were completed.
    */
   boolean evaluateUIQuestions(String in);
   
   /**
    * Gets the title instructions for the current test package to be displayed
    * in the instructions frame in the CTS.
    * @return String represting the test package instructions.
    */
   String getInstructions();
    
   /**
    * Returns the UI Questions specific for the current Activity.
    * @return The UI Questions for the current Activity.
    */
   String getUIQuestions();
   
   /**
    * Returns the User Instructions for the current Test Case.
    * @return The User Instructions for the current Test Case.
    */
   String getCurrentUserInstructions();
   
   /**
    * Returns the Commands for the current Activity.
    * @return The Commands for the current Activity.
    */
   Command getCommands();
   
   /**
    * Returns the name of the current Test Case.
    * @return String reprentation of the name of the current Test Case.
    */
   String getTestCaseName();
   
   /**
    * Returns the indication that the test case as been committed
    * @return If the test case has been committed
    */
   boolean commit();
   
   /**
    * Rolls back state information to give the ability to rerun the current 
    * Test Case.
    * @return Information to roll back the state info to rerun the test
    */
   boolean retry();
   
   /**
    * Saves the session information of the current test.
    * 
    * @param iSessionName - String with the Saved session name
    * @return  Information to save thetesting session 
    */
   boolean saveSession(String iSessionName);
   
   /**
    * Indicates that the test session has been completed
    */
   void completed();
   
   /**
    * Writes the messages to the test suite log
    * @param iMsg - String messages to be written
    */
   void writeToLog(String iMsg);
   
   /**
    * Indicates that the test has been terminated
    */
   void terminateTest();
   
   /**
    * Indicates that the test has failed
    */
   void failTest();
}
