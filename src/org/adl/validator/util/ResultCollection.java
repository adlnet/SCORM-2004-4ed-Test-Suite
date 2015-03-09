package org.adl.validator.util;

import java.util.ArrayList;
import java.util.List;


/**
 * Holds all of the results of the package checkers during package validation.
 * 
 * @author ADL Technical Team
 *
 */
public class ResultCollection
{
   
   /**
    * List of Result objects that represent the results of the package ckeckers.
    */
   private List<Result> mPackageResultsCollection;
   
   /**
    * Constructor
    */
   public ResultCollection()
   {
      mPackageResultsCollection = new ArrayList<Result>();
      
   }

   /**
    * Provides a way to get the map of the Results.
    * 
    * @return Returns the mPackageResultsCollection.
    */
   public List<Result> getPackageResultsCollection()
   {
      return mPackageResultsCollection;
   }
   
   /**
    * Provides a way to get a specific Result based on the package checker name 
    * that is stored in the result.
    * 
    * @param iResultName - String that represents the name of a package checker.
    * @return Result - A Result object that is associated with a specific package
    * checker.
    */
   public Result getPackageResult(String iResultName)
   {
      Result returnResult = new Result();
      for ( int i = 0; i < mPackageResultsCollection.size(); i++ )
      {
         Result tempResult = (Result)mPackageResultsCollection.get(i);
         if ( tempResult.getPackageCheckerName().equals(iResultName) )
         {
            returnResult = tempResult;
            break;
         }
      }
      return returnResult;
      
   }
   
   /**
    * Adds a package checker result to the ResultCollection.
    * 
    * @param iPackageResult - A Result object that is associated with a specific 
    * package checker.
    */
   public void addPackageResult(Result iPackageResult)
   {
      mPackageResultsCollection.add( iPackageResult );
   }
   
   /**
    * Provides a way to find if all the checkers in the ResultCollection
    * combined to equal passed or failed.
    * 
    * @return boolean indicating if all combined to pass or fail.
    */
   public boolean isAllCheckerPassed()
   {
      boolean allPassed = true;
      
      for ( int i = 0; i < mPackageResultsCollection.size(); i++ )
      {
         if ( !( ( (Result)mPackageResultsCollection.get(i) ).isPackageCheckerPassed() ) )
         {
            allPassed = false;
            break;
         }
      }
      return allPassed;
   }
   
   

}
