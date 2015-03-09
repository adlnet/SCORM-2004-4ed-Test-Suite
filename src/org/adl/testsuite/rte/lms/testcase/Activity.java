package org.adl.testsuite.rte.lms.testcase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is used to represent the content of an activity.
 *  
 * @author ADL Technical Team
 *
 */
public class Activity implements Serializable
{

   /**
    * Name of the Activity. 
    * 
    * Default initialization to empty string to prevent null pointer exceptions 
    * in the case where an empty Activity is made.
    */
   private final String mName;
   
   /**
    * Key to the UIQuestions for the current Activity. 
    * 
    * Default initialization to empty string to prevent null pointer exceptions.
    */
   private final String mUIQuestionsKey;
   
   /**
    * Answers to the UIQuestions for the current Activity. 
    * 
    * Default initialization to empty string to prevent null pointer exceptions.
    */
   private final String mUIAnswers;
   
   /**
    * Key to the Commands for the current Activity. 
    * 
    * Default initialization to empty string to prevent null pointer exceptions.
    */
   private final List<String> mCommandsKey;
   
   /**
    * Key to the instructions to tell the user what to do for the next activity. 
    * User to inform the user what to do for the manual tests. 
    * 
    * Default initialization to empty string to prevent null pointer exceptions.
    */
   private final String mInstructions;
   
   /**
    * Default Constructor. This constructor can be used to create an empty 
    * Activity. This can be used to identify to the system that there are no 
    * more Activities expected.
    */
   public Activity()
   {
      this("", "", "",  new ArrayList<String>(), "");
   }
   
   /**
    * Overloaded Constructor. Used to create an Activity for a Test Case.
    * 
    * @param iName - Name of the activity.
    * @param iUIQuestionsKey - String key that maps to UI questions for the activity.
    * @param iCommandsKey - List that holds string keys that maps to the commands for the activity.
    * @param iInstructions - The instructions that tell the user how 
    *                                   move to the next step. 
    */
   public Activity(final String iName, final String iUIQuestionsKey, 
         final String iUIAnswers, final List<String> iCommandsKey, 
         final String iInstructions)
   {
      mName = iName;
      mUIQuestionsKey = iUIQuestionsKey;
      mUIAnswers = iUIAnswers;
      mCommandsKey = iCommandsKey;
      mInstructions = iInstructions;
   }
   
   /**
    * Gets the name of the activity.
    * 
    * @return String -  The string representation of the test package activity 
    * name or an empty string if there was no Activity.
    */
   public String getName()
   {
      return mName;
   }
   
   /**
    * Gets the UI question key for the specific activity and returns it to the SCO.
    * @return String - The string representation of a UI questions key or an 
    * empty string if there was no Activity.
    */
   public String getUIQuestionsKey()
   {
      return mUIQuestionsKey;
   }
   
   /**
    * Gets the UI answers for the specific activity and returns it to the SCO.
    * @return String - The string representation of a UI answers or an 
    * empty string if there was no Activity.
    */
   public String getUIAnswers()
   {
      return mUIAnswers;
   }
   
   /**
    * Gets the commands key for a specific activity and returns it to the SCO>.
    * @return String - The string representation of a commands key or an empty 
    * string if there was no Activity.
    */
   public Iterator<String> getCommandsKey()
   {
      return mCommandsKey.iterator();
   }
   
   /**
    * Gets the user instructions for a specific activity to be displayed in the
    * SCO and on the CTS current instructions frame. 
    * @return String - The String of the user instructions or an empty string if 
    * there was no Activity.
    */
   public String getCurrentUserInstructions()
   {
      return mInstructions;
   }

}
