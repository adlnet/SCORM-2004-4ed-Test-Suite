package org.adl.logging;

import java.applet.Applet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.List;

import netscape.javascript.JSObject;

import org.adl.testsuite.util.AppletList;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;

/**
 * This class is a Thread that pulls messages from the
 * SummaryLogMessageCollection and writes them to the Summary Log... 
 * writes them to the summary log window in the test suite browser).
 * 
 * @author ADL Technical Team
 */
public class SummaryLogWriter implements Runnable
{
   /**
    * The Thread object the SummaryLogWriter hands itself off to in order to
    * run
    */
   protected static Thread mSummaryLogWriterThread;

   /**
    * The class used to format the messages
    */
   protected transient MessageFormatter formatter;
   
   /**
    * Object used to format messages.
    */
   private final transient LogMessageProcessor mLogMsgProc = new XMLLogMessageProcessor("Summary");
   
   /**
    * Holds a reference to the web page/frame that kicked off the Applet that
    * started this thread.  It's used to write to the browser using the JS
    * method
    */
   private transient JSObject mJsroot;

   /**
    * Logger object used for debug logging
    */
   protected static final Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");

   /**
    * The flag that controls the while loop in the run method, defaults to true
    */
   protected transient boolean mTestIsRunning = true;

   /**
    * The flag to be set once the summary log FILE has been created, defaults to false
    */
   private static boolean mSummaryLogFileCreated = false;

   /**
    * holds summary log messages already written to the browser, but not yet to 
    * file.
    */
   private transient List<LogMessage> backlog = new LinkedList<LogMessage>();
   
   /**
    * used to write to the summary log file
    */
   protected transient OutputStreamWriter summaryStream;
   
   /**
    * used to create the Log File and return the name
    */
   protected transient LogFileGenerator mLfg;

   /**
    * Default constructor required for class to be extended
    */
   public SummaryLogWriter()
   {
      // oh really?
   }
   
   /**
    * Uses the reference to the Applet passed in to write to the summary pane in
    * the test suite browser
    * @param iLfg the reference to the LogFileGenerator used to create the summary log file
    */
   public SummaryLogWriter(final LogFileGenerator iLfg)
   {
      LOGGER.entering(getClass().getSimpleName(), "SummaryLogWriter()");

      if ( mSummaryLogWriterThread == null )
      {
         LOGGER.info("Starting SummaryLogWriterThread");
         mSummaryLogWriterThread = new Thread(this, getClass().getSimpleName());
         mSummaryLogWriterThread.start();
         Applet a = AppletList.getApplet("logInterface");
         mJsroot = JSObject.getWindow(a);
         formatter = new HTMLMessageFormatter();
         mLfg = iLfg;
      }
      
      LOGGER.exiting(getClass().getSimpleName(), "SummaryLogWriter()");
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

      if(formatter == null)
      {
         formatter = new HTMLMessageFormatter();
      }
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
               if(messageType == MessageType.NEWLOG)
               {
                  createSummaryLogFile(tempMessage.getMessageText());
               }
               else if (messageType == MessageType.ENDLOG)
               {
                  LOGGER.finest("ENDLOG message received in SummaryLogWriter.run()");
                  stopLogWritingThread();
                  break;
               }
               else
               {               
                  final String tempMsgText = formatter.formatMessage(tempMessage);
                  // Populate the array to pass to the javacript method to write
                  // the messages in the summary log pane on the browser
                  final String arguments[] = 
                     {Integer.toString(tempMessage.getMessageType()), tempMsgText};
   
                  // Invoke the javascript method in the instructions pane which
                  //  invokes a write method in the summary log pane.
                  mJsroot.call("writeLogEntry", arguments);
                  
                  // if the summary log file has been created echo the message to the file,
                  // otherwise store it until the file has been created.
                  if(mSummaryLogFileCreated)
                  {
                     writeToFile(tempMessage);
                  }
                  else
                  {
                     // store in a message collection
                     backlog.add(tempMessage);                     
                  }
               }
            }//end while
         }
         catch ( NullPointerException npe )
         {
            final String message = "NullPointerException in SummaryLogWriter.run() \n" + npe;
            System.out.println(message);
            LOGGER.severe(message);
            mTestIsRunning = false;
         }

      }// end while

      addClosing();
      closeFile();
      
      LOGGER.exiting(getClass().getSimpleName(), "run()");
   }// end of run()
   
   /**
    * Sets the mTestIsRunning attribute to false, this will exit the loop in the
    * run() method that checks the SummaryLogMessageCollection and writes to the
    * summary log pane
    */
   protected void stopLogWritingThread()
   {     
      mTestIsRunning = false;
      LOGGER.info("stopping SummaryLogWriter Thread");
   }
   
   /**
    * Once the xml file has been created this method will write incoming messages
    * to file
    * @param iMessage the message to be written to file
    */
   protected void writeToFile(final LogMessage iMessage)
   {      
      // this is where we need to check to see if the message is a link, if it is
      // then we need to reference the file locally.
      try
      {
         final String temp = mLogMsgProc.formatMessage(iMessage);
         summaryStream.write(temp);
         summaryStream.flush();
      }
      catch(IOException ioe)
      {
         LOGGER.severe("IOException in SummaryLogWriter.writeToFile():\n" + ioe);
         System.out.println("IOException in SummaryLogWriter.writeToFile():\n" + ioe);
      }      
   }// end writeToFile()
   
   /**
    * Creates the file object for the summary log and writes all messages generated
    * (backlogged) to this point
    * @param msgTxt
    */
   protected void createSummaryLogFile(final String msgTxt)
   {
      try
      {         
         // create the summary log file
         final File summaryLog = new File(mLfg.getLogName("LMS", msgTxt, false));
         
         // get the OutputStreamWriter to write to the summaryLog         
         summaryStream = new OutputStreamWriter(new FileOutputStream(summaryLog), "UTF-16");
         
         // write the header information to file 
         summaryStream.write(mLogMsgProc.prepareFile());
         summaryStream.flush();

         if(!backlog.isEmpty())
         {
            // write the messages in the backlog collection to the file
            for(int i=0;i < backlog.size() + 1;i++)
            {
              summaryStream.write(mLogMsgProc.formatMessage(backlog.remove(0)));
            }
            summaryStream.flush();
         }         
         
         // backlog's purpose has been served, free up the space
         backlog = null;
         
         // set the flag to true
         mSummaryLogFileCreated = true;
      }
      catch(IOException ioe)
      {
         LOGGER.severe("IOException in SummaryLogWriter.createSummaryLogFile():\n" + ioe);
         System.out.println("IOException in SummaryLogWriter.createSummaryLogFile():\n" + ioe);
      }      
   }// end createSummaryLogFile()
   
   /**
    * Writes the closing info to the Summary log file
    */
   protected void addClosing() 
   {
      LOGGER.entering(getClass().getSimpleName(),"addClosing()");
      try
      {
         // Depending on how the test ended, the summary log may never
         // have been created, if its stream is null, we do not need to
         // close it
         if ( summaryStream != null )
         {
            summaryStream.write(mLogMsgProc.endFile());
            summaryStream.flush();
         }
      }
      catch(IOException ioe)
      {
         LOGGER.severe("IOException occurred in SummaryLogWriter.addClosing():" + ioe);
         System.out.println("IOException occurred in SummaryLogWriter.addClosing():" + ioe);
      }
      catch (NullPointerException npe)
      {
         // Occurs if "Abort Test" pressed before the SCO is launched
         LOGGER.severe("NullPointerException in " +
            "SummaryLogWriter.addClosing():\n" + npe);
      }
      LOGGER.exiting(getClass().getSimpleName(),"addClosing()");
   }
   
   /**
    * closes the summary log file and the file writer
    */
   protected void closeFile()
   {
      try
      {
         // Depending on how the test ended, the summary log may never
         // have been created, if its stream is null, we do not need to
         // close it
         if ( summaryStream != null )
         {
            summaryStream.close();
         }
      }
      catch(IOException ioe)
      {
         LOGGER.severe("IOException occurred in SummaryLogWriter.closeFile():" + ioe);
         System.out.println("IOException occurred in SummaryLogWriter.closeFile():" + ioe);
      }
      catch (NullPointerException npe)
      {
         // Occurs if "Abort Test" pressed before the SCO is launched
         LOGGER.severe("NullPointerException in " +
            "SummaryLogWriter.closeFile():\n" + npe);
      }
   }
   
   /**
    * called to determine if summary log messages should be queued or written to
    * file
    * @return true if the summary log file has been created, false otherwise
    */
   public static boolean isLogFileCreated()
   {
      return mSummaryLogFileCreated;
   }
}
