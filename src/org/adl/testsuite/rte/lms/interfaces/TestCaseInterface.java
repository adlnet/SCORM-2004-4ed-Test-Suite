package org.adl.testsuite.rte.lms.interfaces;

import org.adl.testsuite.rte.lms.util.Command;
import org.adl.testsuite.rte.lms.util.Results;

/**
 * @author ADL Technical Team
 */
public interface TestCaseInterface
{

   /**
    * Gets the test package instructions for the current test package to be displayed
    * in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   String getInstructions();
   
   /**
    * Gets the end portion of the test package instructions.
    *  
    * @return String represting the end protion of the instructions.
    */
   String getInstructionsEnd();

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
    * Gets a string key that represents the UI questions for the SCO.
    * 
    * @return String - Key that represents UI questions.
    */
   String getUIQuestions();

   /**
    * Evaluates the expected UI question Results to the returned UI question
    * Results.
    * 
    * @param iReturned -
    *           The returned string of results for the UI Questions.
    * @return boolean representing the correctness of the returned UI Results.
    */
   boolean evaluateUIQuestions(String iReturned);

   /**
    * Gets a string key that represents the commands for the SCO.
    * 
    * @return Commands - Key that represents commands.
    */

   Command getCommands();
   /**
    * Evaluates the expected call Results to the returned call Results.
    * 
    * @param iReturned -
    *           The returned Results for the calls made by the SCOs.
    * @return boolean representing the correctness of the returned call Results.
    */
   boolean evaluateResults(Results iReturned);

   /**
    * Gets the user instructions for a specific activity to be displayed in the
    * SCO and on the CTS current instructions frame.
    * 
    * @return String - The String of the user instructions.
    */
   String getCurrentUserInstructions();
   
   /**
    * Indicates to the system if the current test case is done.
    * @return whether the current test case is done or not.
    */
   boolean isDone();

   /**
    * Returns the name of the current Test Case.
    * 
    * @return String representing the name of the current Test Case.
    */
   String getTestCaseName();
   
   /**
    * Rollbacks state information to the begining of the test case.
    * 
    * @return boolean if the retry was successful.
    */
   boolean retry();

   /**
    * Stores the state information for the test case.
    * 
    * @return boolean if the commit was successful.
    */
   boolean commit();
   
   /**
    * Prints the success status of the current Activity
    *
    */
   void reportActivityStatus();

   /**
    * Returns the Title
    * 
    * @return the title
    */
   String getInstructionsTitle();
   
   /**
    * Gets the instructions related to user interactions for the current test
    * package to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   String getInstructionsUIR();
   
   /**
    * Gets the instructions related to login for the current test
    * package to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   String getInstructionsLogin();
   
   /**
    * Gets the instructions related to how to start for the current test package
    * to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   String getInstructionsStart();
   
   /**
    * Asks the user for sequencing results.
    */
   void askAboutSequencingStuff();
}