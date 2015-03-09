package org.adl.testsuite.rte.lms.testcase;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class holds the information related to an LMS Test Case.
 * 
 * @author ADL Technical Team
 *
 */
public class LMSTestCase implements Serializable
{
   /**
    * Object representing an Activity.
    */
   private Activity mAct;
   
   /**
    * Indicator used to inform the system when all the activities are done. 
    */
   private boolean mDone = false;
   
   /**
    * Name of the LMSTestCase.
    * 
    * Default initialization to empty string to prevent null pointer exceptions 
    * in the case where an empty LMSTestCase is made.
    */
   private String mName = "";
   
   /**
    * List of Activity objects.
    */
   private List<Activity> mActivities;
   
   /**
    * A List that represent the instructions for the current test package 
    * to be displayed in the instructions frame in the CTS.
    * 
    * Default initialization to empty string to prevent null pointer exceptions 
    * in the case where an empty LMSTestCase is made.
    */
   private Map<String, String> mInstructionsKey;
   
   /**
    * Current location within the list of activities. 
    */
   private int mIndex = -1;

   /**
    * Constructor
    */
   public LMSTestCase()
   {
   }
   
   /**
    * Overload constructor
    * 
    * @param iName - Name of the LMSTestCase.
    * @param iInstructionsKey - A string key that represent the instructions for
    * the current test package to be displayed in the instructions frame in the CTS.
    * @param iActivities - List of Activity objects.
    */
   public LMSTestCase(final String iName, final Map<String, String> iInstructionsKey, 
         final List<Activity> iActivities)
   {
      mName = iName;
      mInstructionsKey = iInstructionsKey;
      mActivities = iActivities;
      nextActivity();
   }

   /**
    * Gets the name of the LMSTestCase.
    * 
    * @return String - The String represents the name of the LMSTestCase.
    */
   public String getName()
   {
      return mName;
   }
   
   /**
    * Gets the instructions related to user interactions for the current test
    * package to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   public String getInstructionsUIR()
   {
      return getKey("UserInterAct");
   }
   /**
    * Gets the instructions related to how to start for the current test package
    * to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   public String getInstructionsStart()
   {
      return getKey("start");
   }
   
   /**
    * Gets the instructions related to the purpose of the current test package
    * to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   public String getInstructionsPurpose()
   {
      return getKey("purpose");
   }
   
   /**
    * Gets the instructions related to how to start for the current test package
    * to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   public String getInstructionsRTDInit()
   {
      return getKey("RTDInit");
   }
   
   /**
    * Gets the instructions related to how to start for the current test package
    * to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   public String getInstructionsSeqInfo()
   {
      return getKey("seqInfo");
   }
   
   /**
    * Gets the instructions related to how to start for the current test package
    * to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   public String getInstructionsTestType()
   {
      return getKey("TT");
   }
   
   /**
    * Gets the instructions related to how to start for the current test package
    * to be displayed in the instructions frame in the CTS.
    * 
    * @return String represting the test package instructions.
    */
   public String getInstructionsEnd()
   {
      return getKey("end");
   }
   /**
    * Gets the name of the current expected Activity.
    * 
    * @return The name of the current Activity or empty string if no Activity 
    * was expected.
    */
   public String getActivityName()
   {
      return mAct.getName();
   }
   
   /**
    * Gets a string key that represents the UI questions for the SCO.
    * 
    * @return String - Key that represents UI questions.
    */
   public String getUIQuestionsKey()
   {
      return mAct.getUIQuestionsKey();
   }
   
   /**
    * Gets a string key that represents the commands for the SCO.
    * 
    * @return String - Key that represents commands.
    */
   public Iterator<String> getCommandsKey()
   {
      return mAct.getCommandsKey();
   }
   
   /**
    * Gets the user instructions for a specific activity to be displayed in the
    * SCO and on the CTS current instructions frame. 
    * 
    * This is expected to be the last call made for an Activity. Thus, this 
    * method also increments to the next Activity for this LMSTestCase.
    *  
    * @return String - The String of the user instructions.
    */
   public String getCurrentUserInstructions()
   {
      final String instructions = mAct.getCurrentUserInstructions();
      nextActivity();
      return instructions;
   }
   
   /**
    * Gets the UI questions results for the current LMSTestCase.
    * 
    * @return String representing the Results.
    */
   public String getUIAnswers()
   {
      return mAct.getUIAnswers();
   }
   
   /**
    * Increments the list of Activities to the next Activity or creates an empty 
    * Activity if it reached the end of the list.
    */
   protected final void nextActivity()
   {
      mIndex++;
      if( mIndex < 0 || mIndex >= mActivities.size() )
      {
         mDone = true;
         mAct = new Activity();
      }
      else
      {
         mAct = (Activity)mActivities.get(mIndex);
      }
   }
   
   /**
    * Resets the state of the LMS Test Case.
    */
   public void resetState()
   {
      mIndex = -1;
      mDone = false;
      nextActivity();
   }
   
   /**
    * Returns the value of the iKey from the Test Package.
    * @param iKey is the key in the test package instructions hash map. 
    * @return a string that represents a part of the instructions page.
    */
   public String getKey(final String iKey)
   {
      String temp;
      if (mInstructionsKey.get(iKey) == null)
      {
         temp="";
      }
      else
      {
         temp = mInstructionsKey.get(iKey);
      }
      return temp;
   }
   
   public boolean isDone()
   {
      return mDone;
   }
   
   public void setIsDone()
   {
      mDone = true;
   }

}
