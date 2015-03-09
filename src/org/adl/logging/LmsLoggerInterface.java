package org.adl.logging;

import org.adl.util.LogMessage;

/**
 * interface intended to be extended by classes used for logging the results
 * of the ADL SCORM Test Suite LMS Test
 */
public interface LmsLoggerInterface
{   
   /**
    * adds a LogMessage object to the summary or detailed log message collections
    * @param msgType String representing the log type, either "Detailed" or "Summary"
    * @param msg the LogMessage being added
    */
   void addMessage(String msgType, LogMessage msg);
   
   /**
    * Allows the current detailed log to be presented to the user
    * @return Uri to the current log location
    */
   String viewCurrentLog();
   
   /**
    * retry method to be overwritten, this method is intended to wipe the
    * summary log clean and allow the testcase to be re-run
    * @return boolean true if successful, false otherwise
    */
   boolean retry();
}
