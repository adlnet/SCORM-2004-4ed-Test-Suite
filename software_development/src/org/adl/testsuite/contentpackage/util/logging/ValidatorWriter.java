package org.adl.testsuite.contentpackage.util.logging;

import org.adl.logging.DetailedLogMessageCollection;
import org.adl.logging.SummaryLogMessageCollection;
import org.adl.util.LogMessage;

/**
 * Takes the messages from the Validator results and passes them to the
 * Test Suite Logging.
 * 
 * @author ADL Technical Team
 *
 */
public class ValidatorWriter
{

   /**
    * Adds the reported message to the SummaryLogMessageCollection.
    * 
    * @param iLogMessage - Object to be logged as a DetailedLogMessage.
    */
   public void addSummaryLogMessage(final LogMessage iLogMessage)
   {
      SummaryLogMessageCollection.getInstance().addMessage(iLogMessage);
   }
   
   /**
    * Adds the reported message to the SummaryLogMessageCollection.
    * 
    * @param iMsgType Value used to represent message types.
    * @param iLogMessage  String to be logged as a SummaryLogMessage.
    */
   public void addSummaryLogMessage(final int iMsgType, final String iLogMessage)
   {
      final String[] message = iLogMessage.split("~");
      
      if ( message.length == 1 )
      {
         SummaryLogMessageCollection.getInstance().addMessage(
               new LogMessage(iMsgType, message[0]));
      }
      else if ( message.length == 2)
      {
         SummaryLogMessageCollection.getInstance().addMessage(
               new LogMessage(iMsgType, message[0], message[1]));
      }
      
   }

   /**
    * Adds the reported message to the DetailedLogMessageCollection.
    * 
    * @param iLogMessage - Object to be logged as a DetailedLogMessage.
    */
   public void addDetailedLogMessage(final LogMessage iLogMessage)
   {
      DetailedLogMessageCollection.getInstance().addMessage(iLogMessage);
   }
   
   

}
