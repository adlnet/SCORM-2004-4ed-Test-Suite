package org.adl.testsuite.rte.lms.util;

/**
 * Failed Result object. Used when the dm request cannot be made. This is currently
 *  being used when we are trying to make calls on an objective which the LMS did not 
 *  initialize or that we cannot find an index for. It will contain an error message 
 *  that the TestCaseEvaluator can log during evaluation.
 *  
 * @author ADL Technical Team
 */
public class FailedResult extends Result
{
   /**
    * Message holder for this result.
    */
   private String failureMessage;
   
   /**
    * Constructor.
    * 
    * @param iMessage The message to be held in this Result. This will be the message 
    * printed in the log.
    */
   public FailedResult(String iMessage)
   {
      failureMessage = iMessage;
      // this makes sure we never accidentally match error code w/ expected error code
      super.mErrorCode = "-999";
   }
   
   /**
    * Returns the message contained in this Result object. 
    * 
    * @return String representation of the message contained in this Result.
    */
   public String getMessage()
   {
      return failureMessage;
   }
}
