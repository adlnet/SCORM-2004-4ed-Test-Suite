package org.adl.testsuite.rte.lms.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import netscape.javascript.JSObject;

/**
 * This class is responsible for storing the commands (datamodel calls) 
 * associated with an Activity. This class also is responsible for making the 
 * calls to the LMS and compiling the Results.
 * 
 * @author ADL Technical Team
 */
public class Command implements Serializable
{
   
   /**
    * 
    * The name of the variable in the lmsrtefunctions.js file used to track the
    * value returned by the last call of the test subject LMS
    *  
    */
   private static final String LMS_RETURNED = "lmsReturned";
   
   /**
    * LiveConnect object used to communicate to the LMS.
    */
   private JSObject mJSObject;
   
   /**
    * The list of commands (datamodel calls) to be executed for an Activity.
    */
   private List mCommands;
   
   /**
    * The list of parameters for each datamodel call in the Activity.
    */
   private List mParams;
   
   /**
    * Holds the index of the current objective.
    */
   private int mObjIndex = 0;
   
   /**
    * Default constructor.
    */
   public Command()
   {
      mCommands = new ArrayList();
      mParams = new ArrayList();
   }
   
   /**
    * This method is responsible for adding a command (datamodel call) to the 
    * class.
    * 
    * @param iCommand The command to be added to the class.
    */
   public void addCommand(String iCommand)
   {
      mCommands.add(iCommand);
   }
   
   /**
    * This method is responsible for adding a params for datamodel calls to the 
    * class.
    * 
    * @param iParam The command to be added to the class.
    */
   public void addParams(String iParam)
   {
      mParams.add(iParam);
   }
   
   /**
    * Executes the collection of commands against the test LMS, represented by 
    * the JSObject, and returns the Results.
    * 
    * @param iJSObject The LiveConnect object used to communicate to the LMS.
    * 
    * @return Results object representing the collection of LMS returned results.
    */
   public Results evaluate(JSObject iJSObject)
   {
      boolean testing = false;
      Results results = new Results();
      
      mJSObject = iJSObject;
      
      // if there isn't a jsobject, then we are testing
      testing = ( mJSObject == null );
      
      String [] arg = new String[2];
      for( int i = 0; i < mCommands.size(); i++ )
      {
         //System.out.println("Command:: " + mCommands.get(i).toString());
   
         for ( int j = 0; j < arg.length; j++ )
         {
            arg[j] = "";
         }
         //System.out.println("command_index : " + i + " : "+ mCommands.get(i).toString());
         //System.out.println("params: " + mParams.get(i).toString());
         if ( mParams.size() != 0 )
         {
            if ( mParams.get(i).toString().indexOf("!") > -1 )
            {
               //System.out.println("params: " + mParams.get(i).toString());
               arg[0] = parseForIndex(mParams.get(i).toString().substring(0, mParams.get(i).toString().indexOf("!")));
               
               if( mParams.get(i).toString().indexOf("!") == mParams.get(i).toString().length()-1 )
               {            	   
            	   arg[1] = "";
               }
               else
               {
            	   arg[1] = mParams.get(i).toString().substring(mParams.get(i).toString().indexOf("!") + 1, mParams.get(i).toString().length());
               }
                              
               //System.out.println("Command= " + arg[0]);
               //System.out.println("Arg= " + arg[1]);
            }
            else
            {
               arg[0] = parseForIndex(mParams.get(i).toString());
               //System.out.println("Command= " + arg[0]);
            }
         }
         if ( mCommands.get(i).toString().equals("compareObjIds") )
         {
            if( !testing)
            {
               results.addResult(compareObjIds());
            }
            else
            {
               //System.out.println("in compareObjIds: " + arg[0]);
            }
         }
         else if ( !testing )
         {
            //System.out.println("Da Call : " + mCommands.get(i).toString());
            //System.out.println("arg[0]: " + arg[0]);
            if ( arg[0].indexOf("-1") > -1 )
            {
               //System.out.println("oh man, you got a bad result");               
               String objid = extractObjectiveId(mParams.get(i).toString());
               results.addResult(new FailedResult("Attempted to access " + 
                     "an objective with the id \"" + objid + "\" but \"" + objid +
                     "\" was not found."));
            }
            else
            {
               
               final String returned = (String)mJSObject.call(mCommands.get(i).toString(), arg);
               //String returned = (mJSObject.getMember(LMS_RETURNED)).toString();

               //System.out.println("Da Retu : " + returned);
               for ( int j = 0; j < arg.length; j++ )
               {
                  arg[j] = "";
               }
            
               final String errorCode = (String)mJSObject.call("doGetLastError", arg);
               //String errorCode = (mJSObject.getMember(LMS_RETURNED)).toString();
               //System.out.println("result: new Result(" + returned + ", " + errorCode +", " + mObjIndex + ")");            
               results.addResult(new Result(returned, errorCode, mObjIndex));
            }
         }
         else
         {
            System.out.println("jscall: " + mCommands.get(i).toString());
            for ( int e = 0; e < arg.length; e++ )
            {
               System.out.println("arg " + e + ": " + arg[e]);
            }

         }
      }

      return results;
   }
   
   /**
    * Extracts the objective id from a given string, assuming it has the &xxx& 
    * syntax. Otherwise it returns an empty string.
    * 
    * @param iParamToParse The parameter that has the &objectiveid& syntax within it.
    * 
    * @return The objective id or an empty string if the objective id is not found.
    */
   private String extractObjectiveId(String iParamToParse)
   {
      if ( iParamToParse.indexOf('&') > -1 )
      {
         return iParamToParse.substring(iParamToParse.indexOf('&') + 1, 
                                     iParamToParse.lastIndexOf('&'));
      }
      return "";
   }

   /**
    * Checks the String for the & symbol which is being used to indicate that
    * an objective's or data's index/location in the LMS needs to be 
    * found... the String should look something like this: "cmi.objectives.&obj2&.id" or
    * "adl.data.&foo&.stire". This method will remove the &obj2& or &foo& part and replace 
    * it with the index/location of this objective or data within the objectives or stores
    * collection in the test LMS.<br />
    * This will work for any datamodel element. If this method doesn't find the 
    * & symbol, this method will just return the String passed in.
    * 
    * @param iToParse The string to parse.
    * 
    * @return Either the string with no change if it didn't find an &, or the 
    * string with the &xxx& replaced with the location in the collection.
    */
   public String parseForIndex(String iToParse)
   {
      if( mJSObject != null )
      {
         if ( iToParse.indexOf("&")!= -1 )
         {
            String[] tempArray = iToParse.split("&");
            // Determine if we need to find a data index or an objective index
            if ( tempArray[0].indexOf("data") != -1 )
            {
               return tempArray[0] + findData(tempArray[1]) + tempArray[2];
            }
            else if ( tempArray[0].indexOf("objective") != -1 )
            {
               return tempArray[0] + findObjective(tempArray[1]) + tempArray[2];
            }
         }
      }
      return iToParse;
   }
   
   /**
    * Provides the ability to get the index associated with a given objective 
    * id.
    * @param iId The identifier of the objective for which this index search is
    *          being performed.
    * 
    * @return The String representation of the index value where the objective
    *       referred is found. This value is used to replace the "n" of a 
    *       objectives datamodel call.
    */
   private String findObjective(String iId)
   {
      String result = "";

      // make the call to GetValue() to the LMS
      String[] paramCount = { "cmi.objectives._count" };
      mJSObject.call("doGetValue", paramCount);
      int numObjectives = 0;
      try
      {
         numObjectives = Integer.parseInt((mJSObject.getMember(LMS_RETURNED)).toString());
      }
      catch (Exception e){/*if we can't parse it, leave it alone*/}

      int objectiveLocation = -1;

      for ( int i = 0; i < numObjectives; i++ )
      {
         // make the call to GetValue() to the LMS
         String[] params1 = { "cmi.objectives." + i + ".id" };
         mJSObject.call("doGetValue", params1);
         String identifier = (mJSObject.getMember(LMS_RETURNED)).toString();

         if ( identifier.equals(iId) )
         {
            objectiveLocation = i;
            break;
         }
      }

      mObjIndex = objectiveLocation;
      result = "" + objectiveLocation;

      return result;
   }
   
   /**
    * Provides the ability to get the index associated with a given data 
    * id.
    * @param iId The identifier of the data for which this index search is
    *          being performed.
    * 
    * @return The String representation of the index value where the data
    *       referred is found. This value is used to replace the "n" of a 
    *       data datamodel call.
    */
   private String findData(String iId)
   {
      String result = "";

      // make the call to GetValue() to the LMS
      String[] paramCount = { "adl.data._count" };
      mJSObject.call("doGetValue", paramCount);
      int numData = 0;
      try
      {
         numData = Integer.parseInt((mJSObject.getMember(LMS_RETURNED)).toString());
      }
      catch (Exception e){/*if we can't parse it, leave it alone*/}

      int dataLocation = -1;

      for ( int i = 0; i < numData; i++ )
      {
         // make the call to GetValue() to the LMS
         String[] params1 = { "adl.data." + i + ".id" };
         mJSObject.call("doGetValue", params1);
         String identifier = (mJSObject.getMember(LMS_RETURNED)).toString();

         if ( identifier.equals(iId) )
         {
            dataLocation = i;
            break;
         }
      }

      mObjIndex = dataLocation;
      result = "" + dataLocation;

      return result;
   }
   
   /**
    * Compares objective Ids for an activity. 
    * 
    * @return Boolean represtenting the success of the compare. True if 
    *          the compare was successful, otherwise false.
    */
   private Result compareObjIds()
   {
      SpecialResult result = new SpecialResult();
      
      /* Get the number of objectives from the LMS */
      String element = "cmi.objectives._count";
      String[] params = { element };
      mJSObject.call("doGetValue", params);
      String numObjectivesStr = (mJSObject.getMember(LMS_RETURNED)).toString();
      int numObjectives = Integer.parseInt(numObjectivesStr);
      
      for( int i = 0; i < numObjectives; i++ )
      {
         // call GetValue() to the LMS to get the id.
         element = "cmi.objectives." + i + ".id";

         // make the call to GetValue() to the LMS
         String[] params1 = { element };
         mJSObject.call("doGetValue", params1);
         String identifier = (mJSObject.getMember(LMS_RETURNED)).toString();
            
         result.addObj(identifier);
      }
      
      return result;
   }
   
   //////////////////////////////////////
   // Methods for diagnostics and testing
   //////////////////////////////////////
   
   /**
    * Returns an Iterator of the command list stored in this object. This is 
    * used for unit testing and diagnostics and should not be called by the 
    * final product.
    * 
    * @return Iterator of the list of commands stored in this object.
    */
   public Iterator commandIterator()
   {
      return mCommands.iterator();
   }
   
   /**
    * Returns a list of commands, such as doSetValue, to be called on the LMS.
    * 
    * @return The list of commands requests.
    */
   public List getCommList()
   {
      return mCommands;
   }
   
   /**
    * Returns a list of datamodel requests to be called on the LMS.
    * 
    * @return The list of datamodel requests.
    */
   public List getParamList()
   {
      return mParams;
   }

}
