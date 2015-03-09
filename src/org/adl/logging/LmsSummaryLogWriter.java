package org.adl.logging;

import java.io.OutputStreamWriter;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;

/**
 * This class extends SummaryLogWriter and it used for the LMS test only. Because
 * the other tests use SummaryLogWriter to write to the browser as well as to
 * file, and the LMS test writes to the browser in LmsLogger, this class had to
 * be written to extend SummaryLogWriter
 * @author ADL Tech Team
 */
public class LmsSummaryLogWriter extends SummaryLogWriter
{
   /**
    * Constructor used if the file has not been created yet
    * @param iLfg LogFileGenerator used to set the local attribute
    */
   public LmsSummaryLogWriter(final LogFileGenerator iLfg)
   {
      mLfg = iLfg;
      init();
   }// end of LmsSummaryLogWriter
   
   /**
    * Constructor used if the file has been created
    * @param iFw FileWriter used to write to the summary log file
    */
   public LmsSummaryLogWriter(final OutputStreamWriter iFw)
   {
      LOGGER.entering(getClass().getSimpleName(), "LmsSummaryLogWriter()");
      
      // hand off this to a new Thread object and kick it off
      init();
      
      summaryStream = iFw;
      LOGGER.exiting(getClass().getSimpleName(), "LmsSummaryLogWriter()");
   }
   
   /**
    * Declares the Thread and kicks it off
    */
   private void init()
   {
      if ( mSummaryLogWriterThread == null )
      {
         LOGGER.info("Starting LmsSummaryLogWriterThread");
         mSummaryLogWriterThread = new Thread(this, getClass().getSimpleName());
         mSummaryLogWriterThread.start();
      }
   }
   
   /**
    * Overwrites the Thread run() method. When this is called it checks the
    * SummaryLogMessageCollection for queued messages, if no messages are in the
    * collection then it waits until notified. When notified it pulls messages
    * from the collection and writes them to the summary log pane until there
    * are no messages left in the collection.
    */
   public void run()
   {
      LOGGER.entering(getClass().getSimpleName(), "run()");

      while ( mTestIsRunning )
      {
         try
         {
            // As long as there are messages queued, pull and process them
            while( SummaryLogMessageCollection.getInstance().hasMessages() )
            {
               // Get the first LogMessage in the collection
               final LogMessage tempMessage = SummaryLogMessageCollection.getInstance().getMessage();
               final int messageType = tempMessage.getMessageType();

               // if this is a newlog message, we dont send it to the browser
               if(messageType == MessageType.NEWLOG ||
                  messageType == MessageType.ENDLOG)
               {
                  if(messageType == MessageType.NEWLOG)
                  {
                     createSummaryLogFile("LMS");
                  }
                  else
                  {
                     stopLogWritingThread();
                     break;
                  }
               }
               else
               {               
                  writeToFile(tempMessage);
               }
            }//end while
         }
         catch ( NullPointerException npe )
         {
            System.out.println("NullPointerException in LmsSummaryLogWriter.run()");
            LOGGER.severe("NullPointerException in LmsSummaryLogWriter.run()");
            mTestIsRunning = false;
         }
      }// end while

      // add the end tags
      addClosing();
      
      // close the file
      closeFile();
      
      LOGGER.exiting(getClass().getSimpleName(), "run()");
   }// end of run()

}
