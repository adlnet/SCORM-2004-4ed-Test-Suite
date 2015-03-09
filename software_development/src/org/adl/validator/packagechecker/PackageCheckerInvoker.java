package org.adl.validator.packagechecker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.adl.validator.util.CheckerStateData;
import org.adl.validator.util.Messages;
import org.adl.validator.util.Result;
import org.adl.validator.util.ResultCollection;
import org.adl.validator.util.ValidatorKeyNames;
import org.adl.validator.util.ValidatorMessage;

/**
 * Unit test for PackageCheckerInvoker
 * 
 * @author ADL Technical Team
 *
 */
public class PackageCheckerInvoker
{
   
   /**
    * The fully qualified name of the resource bundle.  This bundle contains
    * all of the package checkers.
    */
   private static final String BUNDLE_NAME = 
      "org.adl.validator.properties.packageCheckers";
   
   /**
    * Constant value used to read from properties file
    */
   private static final String STOP = "STOP";
   
   /**
    * The formal reference to the <code>ResourceBundle</code>
    */
   private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
      .getBundle( BUNDLE_NAME );
   
   /**
    * Collection to hold the results objects returned by each checker
    */
   private ResultCollection mResults;
   
   /**
    * String that represent the key names of the Checkers in 
    * packageChecker.properties file.
    */
   private String mValidatorType = "ADLChecker";
   
   /**
    * Default Constructor. Sets the attributes to their initial values.
    * 
    * @param iPackageResultCollection contains a collection of the Results
    * objects returned by checkers
    */
   public PackageCheckerInvoker( ResultCollection iPackageResultCollection)
   {
      mResults = iPackageResultCollection;
   }
   
   /**
    * Creates and executes each checker object based on the checker list 
    * read from the properties file
    * 
    * @return ResultCollection containing the Results objects returned by each
    * checker
    */
   public ResultCollection performPackageChecks()
   {      
      Result currentResult = new Result();
      List checkerList = new ArrayList();
      String checkerName = "";
      
      checkerList = (ArrayList)CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.CHECKER_LIST_KEY);
      if ( checkerList.size() <= 0 || checkerList == null)
      {         
         checkerList = createCheckerList();
      }
      
      Iterator checkerIter = checkerList.iterator();
      
      try
      {
         while ( checkerIter.hasNext() )
         {
            Object checkerObject = checkerIter.next();
            Class checkerClass;
            if(checkerObject instanceof String)
            {
               checkerName = checkerObject.toString();
               checkerClass = Class.forName(checkerName);
            }
            else
            {
               checkerClass = (Class)checkerObject;
            }
            
            PackageChecker checker = (PackageChecker)checkerClass.newInstance();
            
            currentResult = checker.check();
            
            if ( currentResult == null )
            {
               currentResult = new Result();
               currentResult.setPackageCheckerName(checkerName);
               currentResult.setPackageCheckerPassed(false);
               currentResult.addPackageCheckerMessage(new ValidatorMessage(ValidatorMessage.FAILED,Messages.getString("PackageCheckerInvoker.0")));
               mResults.addPackageResult(currentResult);
            }
            else if ( currentResult.isTestStopped() )
            {
               mResults.addPackageResult(currentResult);        
               return mResults;
            }
            else if ( !currentResult.isCheckerSkipped() )
            {
               mResults.addPackageResult(currentResult);
            }            
         }
      }

      catch ( ClassNotFoundException cnfe )
      {
         System.out.println(Messages.getString("PackageCheckerInvoker.1"));
      }
      catch ( IllegalAccessException iae )
      {
         System.out.println(Messages.getString("PackageCheckerInvoker.2"));
      }
      catch ( InstantiationException ie )
      {
         System.out.println(Messages.getString("PackageCheckerInvoker.3"));
      }
      
      return mResults;      
   }
   
   /**
    * Creates the list of Checkers to be ran based off of the 
    * packageCheckers.properties file.
    * 
    * @return - List of the Checkers file names.
    */
   private List createCheckerList()
   {
      List checkerList = new ArrayList();
      String tempCheckerName = "";
      
      int i = 0;
      while ( !tempCheckerName.equals(STOP))
      {
         tempCheckerName = getString(mValidatorType + i);
         if ( !tempCheckerName.equals(STOP))
         {
            checkerList.add(tempCheckerName);
         }
         i++;
      }
      
      return checkerList;
   }
   
   /**
    * Retrieves the checker location associated with the given key.
    * 
    * @param iKey - A unique identifier that identifies checkers to be ran. 
    * @return String - The name of the the package checker files.
    */
   private String getString( String iKey )
   {
      try
      {
         return RESOURCE_BUNDLE.getString( iKey );
      }
      catch ( MissingResourceException e )
      {
         return STOP;
      }
   }
   
}
