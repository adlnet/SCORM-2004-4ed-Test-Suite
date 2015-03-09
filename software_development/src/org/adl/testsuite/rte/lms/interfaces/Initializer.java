package org.adl.testsuite.rte.lms.interfaces;

import java.io.File;
import java.util.List;

/**
 * Interface for any initializer objects.
 * 
 * @author ADL Technical Team
 */
public interface Initializer
{

   /**
    * Checks the environment info such as browser, OS and JRE versions
    */
   void checkEnvironmentInfo();

   /**
    * Sets the testing information about the LMS inputted by the user.
    * 
    * @param iLMSName The String that holds the name of the LMS.
    * @param iLMSVersion The String that holds the LMS Version.
    * @param iLMSVendor The String that holds the LMs Vender.
    */
   void setTestingInfo(String iLMSName, String iLMSVersion, String iLMSVendor);

   /**
    * Returns the list of available saved sessions.
    * 
    * @return List containing the String name for the available saved sessions.
    */
   List<File> getListOfSaves();
   
   /**
    * Returns the list of available Test Packages.
    * 
    * @return List containing the String name for the available Test Packages.
    */
   List<String> getTestPackages();

   /**
    * Accepts a list containin the String name for the chosen Test Packages.
    * 
    * @param iList The list of String names for each Test Package chosen.
    */
   void setChosenTestPackages(List<String> iList);

   /**
    * Returns a boolean representing whether the user chose to run the API Test 
    * Package.
    * 
    * @return boolean representing whether or not the user chose to run API.
    */
   boolean apiIsChosen();
   
   /**
    * Returns a boolean representing whether the user chose to run the DMI Test 
    * Package.
    * 
    * @return boolean representing whether or not the user chose to run DMI.
    */
   boolean dmiIsChosen();

   /**
    * Accepts the user input for the two Learners and IDs needed for the LMS tests.
    * 
    * @param iL1Name The name of the first learner.
    * 
    * @param iL1ID The ID of the first learner.
    * 
    * @param iL2Name The name of the second learner.
    * 
    * @param iL2ID The ID of the second learner.
    */
   void setLearnerIDInfo(String iL1Name, String iL1ID, String iL2Name, String iL2ID);

   /**
    * Accepts whether or not learner 1 was successfully enrolled for the 
    * appropriate LMS Test Packages in the test LMS system.
    * 
    * @param iL1EnrollSuccess boolean of whether learner 1 was successfully enrolled.
    */
   void setL1EnrollSuccess(boolean iL1EnrollSuccess);
   
   /**
    * Accepts whether or not learner 2 was successfully enrolled for the 
    * appropriate LMS Test Packages in the test LMS system.
    * 
    * @param iL2EnrollSuccess boolean of whether learner 2 was successfully enrolled.
    */
   void setL2EnrollSuccess(boolean iL2EnrollSuccess);

   /**
    * Returns the name associated with learner 1.
    * 
    * @return String representing the name of learner 1.
    */
   String getL1Name();
   
   /**
    * Returns the name associated with learner 2.
    * 
    * @return String representing the name of learner 2.
    */
   String getL2Name();
   
   
   /**
    * Returns the ADL title.
    * 
    * @return the ADL title.
    */
   String getADLTitle();
   
   /**
    * Returns the name of the Test Suite
    * 
    * @return the name of the Test Suite
    */
   String getCTSName();
   
   /**
    * Returns the CTS version.
    * 
    * @return the CTS version
    */
   String getCTSVersion();
   
   /**
    * Returns the Test name
    * @return the Test name
    */
   String getTestName();
   
   /**
    * Starts a new LMS test with default session info.
    * 
    * @param iEnvVar String holding value of environment variable
    */
   void startNewTest(String iEnvVar);
   
   /**
    * Used to load logs that have already been created from a previously saved
    * test
    * @param iEnvVar String holding value of environment variable
    * @param iSessionName name of the saved session file the user wants to load
    */
   boolean startSavedTest(String iEnvVar, String iSessionName);
}