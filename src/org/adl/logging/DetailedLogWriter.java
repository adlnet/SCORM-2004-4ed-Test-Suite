package org.adl.logging;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

import org.adl.util.LogMessage;
import org.adl.util.MessageType;

/**
 * This class checks the DetailedLogMessageCollection object and if there are
 * messages queued, removes them from the queue and writes them to the detailed
 * log
 * 
 * @author ADL Technical Team
 */
public class DetailedLogWriter implements Runnable
{   
   /**
    * Holds the value of the current FileWriter object
    */
   protected transient int mCurrentLogWriter = 0;
   
   /**
    * Object used to format messages.
    */
   protected transient LogMessageProcessor mLogMsgProc = new XMLLogMessageProcessor("Detailed");

   /**
    * Holds the path and name of the detailed log file
    */
   protected transient String mDetailedFileName;
   
   /**
    * Array used to hold the OutputStreamWriters that write to the
    * detailed file(s)
    */
   protected transient OutputStreamWriter[] mLogFile = new OutputStreamWriter[2];

   /**
    * Logger object used for debug logging
    */
   protected final static Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");

   /**
    * flag used to control while loop that checks the collection for messages
    * and writes them to file
    */
   protected transient boolean mTestIsRunning = true;

   /**
    * default constructor to allow the class to be extended
    */
   public DetailedLogWriter(){
      super();
   }
   
   /**
    * Starts the Thread running
    * @param iLogFile The URL of the File created that this class writes to
    */
   public DetailedLogWriter(final String iLogFile)
   {
      LOGGER.entering(getClass().getSimpleName(), "DetailedLogWriter(S)");
     
      if(ThreadSingleton.getInstance().isRunning())
      {
         // if the Thread has already been started then we just need to 
         // initialize the new log.  However we need to make sure we don't
         // interrupt the current chain of messages so we'll just add a NEWLOG
         // message to the end of the queue.
         DetailedLogMessageCollection.getInstance().addMessage(new LogMessage(
            MessageType.NEWLOG, iLogFile));
      }
      else
      {
         if(!"".equals(iLogFile))
         {
            // get the detailed log file initialized
            initializeDetailedFile(iLogFile);
         }
         ThreadSingleton.getInstance().initAndStart(this);
      }

      LOGGER.exiting(getClass().getSimpleName(), "DetailedLogWriter(S)");
   }

   /**
    * this constructor is used when an old test that was saved is restarted, 
    * and should only be called from the LMS test classes
    * @param iFw the OutputStreamWriter object already pointing to the detailed
    * log
    */
   public DetailedLogWriter(final OutputStreamWriter iFw)
   {
      LOGGER.entering(getClass().getSimpleName(), "DetailedLogWriter(OSW)");
      
      if(!ThreadSingleton.getInstance().isRunning())
      {
         mLogFile[mCurrentLogWriter] = iFw;
         ThreadSingleton.getInstance().initAndStart(this);
      }
      LOGGER.exiting(getClass().getSimpleName(), "DetailedLogWriter(OSW)");
   }
     
   /**
    * Writes the closing info (end tags) to the detailed log file
    */
   public void addClosing() 
   {
      LOGGER.entering(getClass().getSimpleName(),"addClosing()");
      if(mLogFile[mCurrentLogWriter] != null)
      {
         try
         {
            mLogFile[mCurrentLogWriter].write(mLogMsgProc.endFile());
            mLogFile[mCurrentLogWriter].flush();    
         }
         catch(Exception e)
         {
            LOGGER.severe(getClass().getSimpleName() + " occurred in DetailedLogWriter.addClosing():" +
               e + "\nmlogFile[] = " + mLogFile[mCurrentLogWriter] +
               "\nlmp = " + mLogMsgProc);
            System.out.println("mlogFile[] = " + mLogFile[mCurrentLogWriter]);
            System.out.println("lmp = " + mLogMsgProc);         
            e.printStackTrace();
         }
      }
      
      LOGGER.exiting(getClass().getSimpleName(), "addClosing()");
   }
   
   /**
    * Closes the log file.
    */
   public void closeFile()
   {     
      LOGGER.info("DetailedLogWriter:closeFile() - closing file: " 
         + mDetailedFileName);
      
      if(mLogFile[mCurrentLogWriter] != null)
      {
         try
         {
            mLogFile[mCurrentLogWriter].close();
         }
         catch ( Exception e )
         {
            System.out.println(getClass().getSimpleName() + " occurred in DetailedLogWriter." +
                  "closeFile():" + e);
            
            LOGGER.severe("Exception occurred in DetailedLogWriter." +
               "closeFile():" + e);
         }
      }
         
      // if the FileWriter array is writing to the second (i.e. sub) detailed
      //  log, close that one and return to the original detailed log
      mLogFile[mCurrentLogWriter] = null;
      if (mCurrentLogWriter == 1)
      {            
         mCurrentLogWriter = 0; 
      }
   }// end of closeFile()
   
   /**
    * This method tells the FileWriter what file to write to
    * The filename includes the location and name of file. The File has already
    * been created, the argument is the URL of the file.
    * @param iFileName The URL of the detailed log that holds the results
    * @return The name and location of the detailed log file.
    */
   public final String initializeDetailedFile(final String iFileName)
   {
      LOGGER.entering(getClass().getSimpleName(), "initializeDetailedFile()");      
      LOGGER.info("mDetailedFileName is:" + iFileName);
      
      mDetailedFileName = iFileName;      

      // creates a new FileWriter object and prepares the output file
      try
      {
         mLogFile[mCurrentLogWriter] = new OutputStreamWriter(new FileOutputStream(iFileName), "UTF-16");

         //Prepares the output file.
         prepareFile();
      }
      catch ( Exception e )
      {
         final String message = e.getClass().getSimpleName() + " occurred in "
            + "DetailedLogWriter.initializeDetailedFile():" + e;
         LOGGER.severe(message);
         System.out.println(message);
         e.printStackTrace();
         mTestIsRunning = false;
      }
      LOGGER.exiting(getClass().getSimpleName(), "initializeDetailedFile()");
      return mDetailedFileName;
   }

   /**
    * This is a sublog so increment the currentLogWriter[] one
    * @param iMessageText the type of log being initialized (MD, SCO)
    */
   public void initializeSubLog(final String iMessageText )
   {
      LOGGER.entering(getClass().getSimpleName(), "initializeSubLog()");
      
      // if this is a sublog increment the array to store the new FileWriter
      if(mCurrentLogWriter == 0)
      {
         mCurrentLogWriter = 1;
      }
      
      initializeDetailedFile(iMessageText);
      LOGGER.exiting(getClass().getSimpleName(), "initializeSubLog()");
   }
   
   /**
    * Identifies whether the thread is running
    * @return true if this class is intantiated and running, false if it's 
    * finished
    */
   public boolean isRunning()
   {
      return mTestIsRunning;
   }
   
   /**
    * When this is called it checks the MessageCollection for queued messages,
    * if no messages are in the collection then this Thread waits until 
    * notified. When notified it pulls messages from the collection and 
    * writes them to the detailed log until there are no messages left in 
    * the collection.
    */
   public void run()
   {
      LOGGER.entering(getClass().getSimpleName(), "run()");

      while ( mTestIsRunning )
      {
         // Local variable to hold the LogMessage Object
         LogMessage tempMessage;
         String tempMsgText;

         try
         {
            // As long as there are messages queued, pull and process them
            while ( DetailedLogMessageCollection.getInstance().hasMessages() )
            {
               // Get the first LogMessage in the collection
               tempMessage = DetailedLogMessageCollection.getInstance().getMessage();

               // Get the MessageText of the message
               tempMsgText = tempMessage.getMessageText();

               try
               {
                  // pulls the message type to discern how to process the 
                  // message text
                  final int msgType = tempMessage.getMessageType();

                  // if the msgType is ENDLOG
                  if(msgType == MessageType.ENDLOG)
                  {                       
                     addClosing();
                     closeFile();                        
                  }
                  else if (msgType == MessageType.NEWLOG)
                  {
                     // this is a NEWLOG message
                     initializeSubLog(tempMsgText);
                  }
                  else
                  {
                     // if the msgType is not NEWLOG or ENDLOG write it to the log
                     if(mLogFile[mCurrentLogWriter] != null)
                     {
                        mLogFile[mCurrentLogWriter].write(mLogMsgProc.formatMessage(tempMessage));
                        mLogFile[mCurrentLogWriter].flush();
                     }  
                  }
               }
               catch ( Exception e )
               {
                  final String message = "An " + e.getClass().getName() + " occurred in "
                     + "DetailedLogWriter.run() inner catch:" + e +
                     "\nmessage type is: " + tempMessage.getMessageType() +
                     "\ncurrent log is: " + mDetailedFileName + 
                     "\ncurrent message is: " + tempMsgText;
                  
                  LOGGER.severe(message);
                  System.out.println(message);
                  e.printStackTrace();
               }
            }//end while
         }
         catch ( Exception e )
         {
            LOGGER.severe("Exception in DetailedLogWriter.run() outer " +
               "catch:" + e);
            
            System.out.println("Exception in DetailedLogWriter.run() outer " +
                  "catch:" + e);
            
            e.printStackTrace();

            // stop the while loop
            mTestIsRunning = false;
         }
      }// end while

      try
      {
         // flush to file anything left in the pipe
         mLogFile[mCurrentLogWriter].flush();
      }
      catch ( Exception e )
      {
         final String message = e.getClass().getSimpleName() + 
            " occurred in DetailedLogWriter.run() " +
            "when attempting to flush after stopping thread"; 
         LOGGER.severe(message);
         System.out.println(message);
      }
      finally
      {
         // now that everything has been flushed to file and the while loop
         // has ended, close the file
         if ( mLogFile[mCurrentLogWriter] != null )
         {
            addClosing();
            closeFile();
         }
      }
      LOGGER.exiting(getClass().getSimpleName(), "run()");
   }// end of run()

   /**
    * Sets the mTestIsRunning attribute to false, this will exit the loop in the
    * run() method that checks the MessageCollection and writes to the detailed
    * log
    */
   public void stopLogWritingThread()
   {
      mTestIsRunning = false;
      LOGGER.info("stopping DetailedLogWriter Thread");
   }

   /**
    * Writes the initial info to the detailed file
    */
   private void prepareFile()
   {
      LOGGER.entering(getClass().getSimpleName(), "prepareFile()");
      // Create the header part of the file
      try
      {         
         mLogFile[mCurrentLogWriter].write(mLogMsgProc.prepareFile());
         mLogFile[mCurrentLogWriter].flush();
      }
      catch ( Exception e )
      {
         final String message = e.getClass().getSimpleName() + " occurred in "
            + "DetailedLogWriter.prepareFile():" + e; 
         LOGGER.severe(message);
         System.out.println(message);
         e.printStackTrace();
         mTestIsRunning = false;
      }
      LOGGER.exiting(getClass().getSimpleName(), "prepareFile()");
   }// end of prepareFile()
   
   /**
    * A Singleton that makes sure only one Thread is ever in existence.
    */
   private static class ThreadSingleton
   {
      /**
       * the thread
       */
      private transient Thread thread;
      
      /**
       * the instance
       */
      private final static ThreadSingleton INSTANCE = new ThreadSingleton();
      
      private ThreadSingleton()
      {
         
      }
      
      public static ThreadSingleton getInstance()
      {
         return INSTANCE;
      }
      
      public boolean isRunning()
      {
         synchronized(this)
         {
            return thread != null;
         }
      }
      
      public void initAndStart(final Runnable runnable)
      {
         synchronized(this)
         {
            if (!isRunning())
            {
               LOGGER.info("Starting DetailedLogWriterThread");
               thread = new Thread(runnable, runnable.getClass().getSimpleName());
               
               // kick off the Thread
               thread.start();
            }
         }
      }
   }
}