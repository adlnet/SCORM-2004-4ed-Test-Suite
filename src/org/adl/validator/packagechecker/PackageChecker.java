package org.adl.validator.packagechecker;

import org.adl.validator.util.Result;
/**
 * This class is an abstract implementation which will be used as a
 * template for all other checkers
 * 
 * @author ADL Technical Team
 *
 */
public abstract class PackageChecker
{      
      /**
       * Result object which contains the results of the tests performed by the
       * checker
       */
      protected Result mResult;
      
      /**
       * Default Constructor. Sets the attributes to their initial values.
       */
      public PackageChecker()
      {
         // default constructor
      }      
      
      /**
       * Performs the given check
       * 
       * @return Result object containing the results and other information
       * relating to the given check
       */
      public abstract Result check();
      
}
