package org.adl.testsuite.rte.lms.util;

import java.util.Iterator;

/**
 * This class is responsible for parsing the command keys from the test package 
 * properties files.
 * 
 * @author ADL Technical Team
 */
public class LMSParser
{
   /**
    * The property file containing the values for the commands keys.
    */
   private static final String COMMAND_RESOURCE_BUNDLE = "commands";

   /**
    * The property file containing the values for the userinterface keys.
    */
   private static final String UI_RESOURCE_BUNDLE = "userinterface";
   
   /**
    * Iterator object used to hold commands key.
    */
   Iterator mIter;

   /**
    * Command object used to hold the datamodel calls and the calls parameters.
    */
   Command mComm;

   /**
    * Result object used to hold the expected result value and errorcode for a 
    * particular command.
    */
   Result mRes = new Result();

   /**
    * SpecialResult object used to hold the expected result for commands that 
    * have objectives.
    */
   SpecialResult mResSpecial = new SpecialResult();

   /**
    * Results object that holds the Result objects for an Activity.
    */
   Results mRess = new Results();

   /**
    * String Array that holds the User Interface question keys.
    */
   private String[] mUIQuestionArray;

   /**
    * Constructor
    */
   public LMSParser()
   {

   }

   /**
    * Overloaded Constructor
    * 
    * @param iIter
    *           the iterator that holds commands key.
    * @param iComm
    *           the command object from LMSTestCaseInterface.
    */
   public LMSParser(Iterator iIter, Command iComm)
   {
      mIter = iIter;
      mComm = iComm;
   }

   /**
    * Method that controls the parsing of the command keys.
    */
   public void parseCommands()
   {
      String currentCmdKey = "";

      while ( mIter.hasNext() )
      {
         currentCmdKey = (String) mIter.next();
         String[] mCmdKeySplit = currentCmdKey.split("->");
         String dmCommand = mCmdKeySplit[0];  
         if ( (dmCommand.equals("GET")) || (dmCommand.equals("I")) || (dmCommand.equals("C")) )
         {
            parseGetCommands(mCmdKeySplit);
         }
         else if ( dmCommand.equals("SET") )
         {
            parseSetCommands(mCmdKeySplit);
         }
         else if ( (dmCommand.equals("GES")) || (dmCommand.equals("GeDi")) )
         {
            parseGESCommands(mCmdKeySplit);
         }
         else if ( dmCommand.equals("GLE") )
         {
            parseGLECommands(mCmdKeySplit);
         }
         else if ( dmCommand.equals("T") )
         {
            parseTerminateCommands(mCmdKeySplit);
         }
         else if ( dmCommand.equals("COI") )
         {
            parseCompareObjIdCommands(mCmdKeySplit);
         }
      }
   }

   /**
    * Takes a User Interface question key of questions keys and parse it to a 
    * String of questions in sentence form separated by ~.
    * 
    * @param iQuestionKey - Key of question keys.
    * @return String of questions in sentence form separated by ~.
    */
   public String parseUIKeys(String iQuestionKey)
   {
      String returnString = "";

      mUIQuestionArray = iQuestionKey.split("~");
      StringBuffer questions = new StringBuffer();
      String tempQuestion = "";
      for ( int i = 0; i < mUIQuestionArray.length; i++ )
      {
         tempQuestion = getUIMessage(mUIQuestionArray[i]);
         if ( i == 0 )
         {
            questions.append(tempQuestion);
         }
         else
         {
            questions.append("~" + tempQuestion);
         }
      }
      returnString = questions.toString();
      return returnString;
   }

   /**
    * Takes the UI message key and makes it the actual questions.
    * 
    * @param iQuestionKey - Simplified string representation of the UI questions.
    * @return String in sentence form of UI Questions.
    */
   private String getUIMessage(String iQuestionKey)
   {
      String returnString = "";
      if ( iQuestionKey.indexOf("@") != -1 )
      {
         String[] keyVarSplit = iQuestionKey.split("@");

         if ( keyVarSplit[1].indexOf(".") != -1 )
         {
            int idx = keyVarSplit[1].indexOf(".");
            String messageKey = keyVarSplit[0];
            String variable = keyVarSplit[1].substring(0, idx);
            messageKey += keyVarSplit[1].substring(idx);
            returnString = LMSMessages.UserInterface.getString(messageKey, variable);
         }
         else
         {
            returnString = LMSMessages.UserInterface.getString(keyVarSplit[0], keyVarSplit[1]);
         }
      }
      else
      {
         returnString = LMSMessages.UserInterface.getString(iQuestionKey);
      }
      return returnString;
   }

   /**
    * Parses command keys when comparing objectives ids.
    * 
    * @param iCmdKeySplit
    *           a string array of the split command key.
    */
   private void parseCompareObjIdCommands(String[] iCmdKeySplit)
   {
      String params = "";
      String[] objectives;
      int size;

      setCommandDMCall(iCmdKeySplit[0]);
      
      params = iCmdKeySplit[1];
      setCommandParams(params);

      objectives = params.split(",");
      size = objectives.length;

      mResSpecial = new SpecialResult();
      for ( int i = 0; i < size; i++ )
      {
         mResSpecial.addObj(objectives[i]);
      }
      mRess.addResult(mResSpecial);

   }

   /**
    * Parses command keys when the API methond is getLastError.
    * 
    * @param iCmdKeySplit
    *           a string array of the split command key.
    */
   private void parseGLECommands(String[] iCmdKeySplit)
   {
      String errorCode = "";
      String value = "";

      setCommandDMCall(iCmdKeySplit[0]);
      setCommandParams();
      
      value = iCmdKeySplit[2];

      errorCode = iCmdKeySplit[3];
      mRes = new Result(value, errorCode);
      mRess.addResult(mRes);
   }

   /**
    * Parses command keys when the API methond is getErrorString and
    * getDiagnostic.
    * 
    * @param iCmdKeySplit
    *           a string array of the split command key.
    */
   private void parseGESCommands(String[] iCmdKeySplit)
   {
      String temp = "";
      String value = "";
      String errorCode = "";
      for ( int i = 0; i < iCmdKeySplit.length; i++ )
      {
         temp = iCmdKeySplit[i];
         if ( i == 0 )
         {
            setCommandDMCall(temp);
         }
         else if ( i == 1 )
         {
            setCommandParams(temp);
         }
         else if ( i == 2 )
         {
            value = temp;
         }        
      }
      errorCode = iCmdKeySplit[3];
      mRes = new Result(value, errorCode);
      mRess.addResult(mRes);

   }

   /**
    * Parses command keys when the API methond is terminate.
    * 
    * @param iCmdKeySplit
    *           a string array of the split command key.
    */
   private void parseTerminateCommands(String[] iCmdKeySplit)
   {
      setCommandDMCall(iCmdKeySplit[0]);
      setCommandParams();

      mRes = new Result("", "");
      mRess.addResult(mRes);
   }

   /**
    * Parses command keys when the API methond is setValue.
    * 
    * @param iCmdKeySplit
    *           a string array of the split command key.
    */
   private void parseSetCommands(String[] iCmdKeySplit)
   {
      String temp1 = "";
      String temp = "";
      String value = "";
      String errorCode = "";
      for ( int i = 0; i < iCmdKeySplit.length; i++ )
      {
         temp = iCmdKeySplit[i];
         if ( i == 0 )
         {
            setCommandDMCall(temp);
         }
         else if ( i == 1 )
         {
            parseSetCommandsDMParams(temp);
         }

         else if ( i == ((iCmdKeySplit.length) - 1) )
         {
            errorCode = temp;
         }
         else
         {
            temp1 = LMSMessages.Commands.getString(temp);
            value = temp1;
         }
      }
      mRes = new Result(value, errorCode);
      mRess.addResult(mRes);
   }

   /**
    * Parses command keys when the API methond is getValue, initialize and
    * commit.
    * 
    * @param iCmdKeySplit
    *           a string array of the split command key.
    */
   private void parseGetCommands(String[] iCmdKeySplit)
   {

      String temp1 = "";
      String temp = "";
      String value = "";
      String errorCode = "";
      String expectedType = "";
      
      
      for ( int i = 0; i < iCmdKeySplit.length; i++ )
      {
         temp = iCmdKeySplit[i];
         if ( i == 0 )
         {
            setCommandDMCall(temp);
         }
         else if ( i == 1 )
         {
            expectedType = parseGetCommandsDMParams(temp);
         }
         else if ( i == ((iCmdKeySplit.length) - 1) )
         {
            errorCode = temp;
         }
         else
         {
            if ( (LMSMessages.Commands.getString(temp).equals("")) && !(temp.equals("")) )
            {
               temp1 = temp;
            }
            else
            {
               temp1 = LMSMessages.Commands.getString(temp);
            }
            value = temp1;
         }
      }
      if ( (iCmdKeySplit[1].indexOf("LN") > -1) || (iCmdKeySplit[1].indexOf("LI") > -1) )
      {
         String learnerKey = iCmdKeySplit[1];
         value = getLearnerInfoForResultValue(learnerKey);
      }
      
      if ( expectedType.equals("") )
      {
         mRes = new Result(value, errorCode);
      }
      else
      {
         mRes = new Result(value, errorCode, expectedType);
         expectedType = "";
      }
      mRess.addResult(mRes);
   }
   
   /**
    * Parses the first part of the commands keys in each Activity to the correct 
    * Data Model call and adds it to the command object for the current command.
    * 
    * @param iCall - String represting the Data Model call key.
    */
   private void setCommandDMCall(String iCall)
   {
      String temp = "";
      temp = LMSMessages.Commands.getString(iCall);
      mComm.addCommand(temp);
   }
   
   /**
    * Sets the parameter for the command object for teh current command to "".
    */
   private void setCommandParams()
   {
      mComm.addParams("");
   }
   
   /**
    * Sets the parameters parsed for the command object for the current command.
    * 
    * @param iParam - String parsed that represent the parameters for the Data 
    * Model call.
    */
   private void setCommandParams(String iParam)
   {
      mComm.addParams(iParam);
   }

   /**
    * Parses the SetValue Data Model call parameter key and sets the parsed 
    * parameter for the command object.
    * 
    * @param iKey - String that represtents DM call parameters 
    */
   private void parseSetCommandsDMParams(String iKey)
   {
      String key = iKey;
      String[] keySplit = key.split("~");
      String dmStart = LMSMessages.Commands.getString(keySplit[0]);
      if ( keySplit.length == 2 )
      {
         String[] finalSplit = setFinalSplitArray(keySplit);

         dmStart = dmStart + "." + LMSMessages.Commands.getString(finalSplit[0])
                       + "!" + LMSMessages.Commands.getString(finalSplit[1]);

      }
      else if ( keySplit.length == 3 )
      {
         String[] finalSplit = setFinalSplitArray(keySplit);

         dmStart = dmStart + "." + LMSMessages.Commands.getString(keySplit[1])
                       + "." + LMSMessages.Commands.getString(finalSplit[0])
                       + "!" + LMSMessages.Commands.getString(finalSplit[1]);
      }
      // Handles "cmi.objectives.index.id","id_name", "adl.data.n.store"
      else if ( keySplit.length == 4 )
      {
         String[] finalSplit = setFinalSplitArray(keySplit);

         String setValue = LMSMessages.Commands.getString(finalSplit[1]);
         
         dmStart = dmStart + "." + LMSMessages.Commands.getString(keySplit[1]) 
                     + "." + keySplit[2] + "." + LMSMessages.Commands.getString(finalSplit[0]) 
                     + "!" + setValue;
      }
      // Handles "adl.nav.request", "{target=activity_name}choice"
      else if ( keySplit.length == 5 )
      {
         if ( key.indexOf("a~n~r") > -1 )
         {
            String[] temp3 = keySplit[2].split("!");
            dmStart = dmStart + "." + LMSMessages.Commands.getString(keySplit[1]) + "." + LMSMessages.Commands.getString(temp3[0]) + "!" + LMSMessages.Commands.getString(temp3[1], keySplit[3]) + LMSMessages.Commands.getString(keySplit[4]);
         }
         else
         {
            String[] temp3 = keySplit[4].split("!");
            dmStart = dmStart + "." + LMSMessages.Commands.getString(keySplit[1]) + "." + LMSMessages.Commands.getString(keySplit[2]) + "." + LMSMessages.Commands.getString(keySplit[3]) + "." + LMSMessages.Commands.getString(temp3[0]) + "!" + LMSMessages.Commands.getString(temp3[1]);
         }
      }
      else if ( keySplit.length == 6 )
      {
         String[] finalSplit = setFinalSplitArray(keySplit);
         
         dmStart = dmStart + "." + LMSMessages.Commands.getString(keySplit[1]) 
                     + "." + LMSMessages.Commands.getString(keySplit[2]) 
                     + "." + LMSMessages.Commands.getString(keySplit[3]) 
                     + "." + LMSMessages.Commands.getString(keySplit[4]) 
                     + "." + LMSMessages.Commands.getString(finalSplit[0]) 
                     + "!" + LMSMessages.Commands.getString(finalSplit[1]);
      }
      setCommandParams(dmStart);
   }
   
   /**
    * Takes the data model element section of a SetValue command key and splits
    * if necessary. 
    * 
    * @param iKeySplit - String array that represents the sections of the data 
    * model parameters
    * @return String array that contains the appropriate strings for the parameters.
    */
   private String[] setFinalSplitArray( String[] iKeySplit)
   {
      int index = iKeySplit.length - 1;
      String[] temp = new String[2];
      if ( iKeySplit[index].indexOf("!") == iKeySplit[index].length() - 1 )
      {
         temp[0] = iKeySplit[index].substring(0, iKeySplit[index].length() - 1);
         temp[1] = "";
      }
      else
      {
         temp[0] = iKeySplit[index].substring(0, iKeySplit[index].indexOf("!"));
         temp[1] = iKeySplit[index].substring(iKeySplit[index].indexOf("!") + 1, iKeySplit[index].length( ));
      }
      return temp;
   }
   
   /**
    * Parses the GetValue Data Model call parameter key and sets the parsed 
    * parameter for the command object.
    * 
    * @param iKey - String that represents DM call parameters 
    * @return String that indicates if a Result has an extra parameter.
    */
   private String parseGetCommandsDMParams(String iKey)
   {
      String element = ""; 
      String expType = "";
      String temp = iKey;
      if ( temp.toString().indexOf("~") != -1 )
      {
         String[] temp2 = temp.split("~");

         if ( temp2[1].equals("INTR") && temp.indexOf("!") > -1 )
         {
            for (int j = 0; j < temp2.length; j++)
            {
               if (temp2[j].indexOf("!") > -1)
               {
                  String[] type = temp2[j].split("!");
                  expType = LMSMessages.Commands.getString(type[1]);
                  temp2[j] = type[0];
               }
            }
         }
         
         if (( temp2.length == 6) && ( temp.indexOf("a~n~r") > -1 ))
         {                  
            element = LMSMessages.Commands.getString(temp2[0])
            + "." + LMSMessages.Commands.getString(temp2[1])
            + "." + LMSMessages.Commands.getString(temp2[2])
            + "." + LMSMessages.Commands.getString(temp2[3])
            + "." + LMSMessages.Commands.getString(temp2[4], temp2[5]);
         }
         else if (( temp2.length == 4) && ( temp.indexOf("a~n~r") > -1 ))
         {                  
            element = LMSMessages.Commands.getString(temp2[0])
            + "." + LMSMessages.Commands.getString(temp2[1])
            + "." + LMSMessages.Commands.getString(temp2[2])
            + "." + LMSMessages.Commands.getString(temp2[3]);
         }
         else
         {
            StringBuffer sb = new StringBuffer();
            for ( int j = 0; j < temp2.length; j++ )
            {
               if ( j == 0 )
               {
                  sb.append(LMSMessages.Commands.getString(temp2[j]));
               }
               else
               {
                  sb.append("." + LMSMessages.Commands.getString(temp2[j]));
               }
            }
            element = sb.toString();
         }
      }
      else
      {
         element = temp;
      }
      setCommandParams(element);
      return expType;
   }
   
   /**
    * Gets the learner_id or learner_name set for a particualar test package.
    * 
    * @param iLearnerKey - String representing a key that contains LI (learner_id)
    * or LN (learner_name).
    * 
    * @return a String represents the Result value to be set for the package, 
    * either the learner_id or learner_name.
    */
   private String getLearnerInfoForResultValue(String iLearnerKey)
   {
      String value = "";
      String learnerKey = iLearnerKey;
      if ( LMSSession.getInstance().getCurrentPackage().equals("API") )
      {
         if ( learnerKey.indexOf("LN") > -1 )
         {
            value = LMSSession.getInstance().getL1Name();
         }
         else
         {
            value = LMSSession.getInstance().getL1ID();
         }
      }
      else
      {
         if ( LMSSession.getInstance().getL2Name().equals("") )
         {
            if ( learnerKey.indexOf("LN") > -1 )
            {
               value = LMSSession.getInstance().getL1Name();
            }
            else
            {
               value = LMSSession.getInstance().getL1ID();
            }
         }
         else
         {
            if ( learnerKey.indexOf("LN") > -1 )
            {
               value = LMSSession.getInstance().getL2Name();
            }
            else
            {
               value = LMSSession.getInstance().getL2ID();
            }
         }
      }
      return value;
   }
   /**
    * Gets and returns the expected results.
    * 
    * @return the expected results object.
    */
   public Results getExpectedResults()
   {
      return mRess;

   }

   /**
    * Gets and returns the command.
    * 
    * @return the command object.
    */
   public Command getCommands()
   {
      return mComm;

   }

}
