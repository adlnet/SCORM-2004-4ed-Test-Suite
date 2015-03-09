package org.adl.testsuite.rte.lms.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents the collection of returned results from the test LMS. 
 * This class is created for each Command evaluation on an LMS for each Activity.
 * 
 * @author ADL Technical Team
 */
public class Results implements Serializable
{
   /**
    * List of results objects for a Activity.
    */
   List<Result> mResults;

   /**
    * The default constructor.
    */
   public Results()
   {
      mResults = new ArrayList<Result>();
   }

   /**
    * Adds a result object to the results list.
    * 
    * @param iResult is an object that holds a result value and error code. 
    */
   public void addResult(Result iResult)
   {
      mResults.add(iResult);
   }
 
   
   /**
    * Returns an Iterator of the collection of results.
    * 
    * @return Iterator of the results collection.
    */
   public Iterator<Result> iterator()
   {
      return mResults.iterator();
   }
   
   /**
    * Returns a list of results objects.
    * 
    * @return List of results.
    */
   public List<Result> getResults()
   {
      return mResults;
   }
   
   /**
    * Returns a result object from a index in the list.
    * 
    * @param iIndex location of the result.
    * @return a result object.
    */
   public Result getResult(int iIndex)
   {
      return mResults.get(iIndex);
   }
   
   ////////////////////////
   // Methods for testing
   ///////////////////////
   
   /**
    * Returns the value of a result object.
    * 
    * @return value of a result object.
    */
   public String getResultsValue()
   {
      return (mResults.get(mResults.size()-1)).getValue();
   }
   
   /**
    * Returns a value of a result from a specific index.
    * 
    * @param iIndex the location of the result.
    * @return value of a specific result.
    */
   public String getResultsValue(int iIndex)
   {
      return (mResults.get(iIndex)).getValue();
   }
   
   /**
    * Returns the error code of a result object.
    * 
    * @return error code of a result.
    */
   public String getResultsErrorCode()
   {
      return (mResults.get(mResults.size()-1)).getErrorCode();
   }
   
   /**
    * Returns a error cod of a result from a specific index.
    * 
    * @param iIndex the location of the result.
    * @return error code of a specific result.
    */
   public String getResultsErrorCode(int iIndex)
   {
      return (mResults.get(iIndex)).getErrorCode();
   }
}
