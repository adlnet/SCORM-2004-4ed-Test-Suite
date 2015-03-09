package org.adl.logging;

import org.adl.util.LogMessage;

/**
 * Directs messages from the User Interface (UI) to the requested 
 * LogMessageCollection.
 * 
 * @author ADL Technical Team
 * 
 */
public class UIMessageProcessor
{
   /**
    * Constant log type
    */
   public static final int BOTH = 0;
   
   /**
    * Constant log type - Detailed Log
    */
   public static final int DLOG = 1;
   
   /**
    * Constant log type - Summary Log
    */
   public static final int SLOG = 2;
   
   /**
    * Pass through method to allow UI to send messages to the Detailed and
    * Summary Logs.
    * @param iLog - the log to write to
    * @param iMsgType - the type of message based on an enumerated list
    * @param iMsgTxt - the text intended to be written to log
    */
   public void writeLogEntry (final int iLog, final int iMsgType, final String iMsgTxt)
   {
      switch (iLog)
      {
         case BOTH:
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                                                            iMsgType, iMsgTxt));
            SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
                                                            iMsgType, iMsgTxt));
            break;
            
         case DLOG:
            DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
                                                            iMsgType, iMsgTxt));
            break;
            
         case SLOG:
            SummaryLogMessageCollection.getInstance().addMessage(new LogMessage(
                                                            iMsgType, iMsgTxt));
            break;
            
         default:
            System.out.println("UIMessageProcessor.writeLogEntry() - " +
                               "\tError: Hit the default case.\n" +
                               "\tLog request should have been 0, 1 or 2.\n" +
                               "\tThe log request received was: " + iLog + 
                               "\n\tThe message type received was: " + iMsgType +
                               "\n\tThe message text received was: " + iMsgTxt);
            break;
      }
   }
}
