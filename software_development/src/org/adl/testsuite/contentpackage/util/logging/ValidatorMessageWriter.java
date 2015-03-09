package org.adl.testsuite.contentpackage.util.logging;

import java.util.ArrayList;
import java.util.List;

import org.adl.util.LogMessage;

/**
 * Takes the messages from the Validator results and stores them in List so that 
 * the messages can be accessed outside of the CTS logging if needed. 
 * 
 * @author ADL Technical Team
 *
 */
public class ValidatorMessageWriter extends ValidatorWriter
{
   /**
    * List used to store Summary log messages.
    */
   private final transient List mSummaryMessages = new ArrayList();
   
   /**
    * List used to store Detailed log messages. 
    */
   private final transient List mDetailedMessages = new ArrayList();

   /* (non-Javadoc)
    * @see org.adl.testsuite.contentpackage.util.logging.ValidatorWriter#addSummaryLogMessage(int, java.lang.String)
    */
   public void addSummaryLogMessage(final int iMsgType, final String iLogMessage)
   {
      mSummaryMessages.add(iLogMessage);
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.contentpackage.util.logging.ValidatorWriter#addSummaryLogMessage(org.adl.util.LogMessage)
    */
   public void addSummaryLogMessage(final LogMessage iLogMessage)
   {
      mSummaryMessages.add(iLogMessage);
   }
   
   /* (non-Javadoc)
    * @see org.adl.testsuite.contentpackage.util.logging.ValidatorWriter#addDetailedLogMessage(org.adl.util.LogMessage)
    */
   public void addDetailedLogMessage(final LogMessage iLogMessage)
   {
      mDetailedMessages.add(iLogMessage);
   }
   
   
   /**
    * Provides a way to retrieve the List of SummaryLogMessages.
    * 
    * @return List of the SummaryLogMessages.
    */
   protected List getSummaryLogMessages()
   {
      return mSummaryMessages;
   }
   
   /**
    * Provides a way to retrieve the List of DetailedLogMessages.
    * 
    * @return List of the DetailedLogMessages.
    */
   protected List getDetailedLogMessages()
   {
      return mDetailedMessages;
   }
}
