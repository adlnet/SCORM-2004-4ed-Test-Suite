package org.adl.logging;


/**
 * This class extends DetailedLogWriter and is used for the LMS test. It is used
 * because the other tests utilize a master detailed log and sub-logs.  This test
 * only uses sub-logs so the logic had to be slightly modified.
 * @author ADL Tech Team
 *
 */
public class LmsDetailedLogWriter extends DetailedLogWriter implements Runnable
{
   /**
    * Creates the LmsDetailedLogWriter object and kicks off the Thread
    * @param fileName the name and path of the detailed file to be written to
    */
   public LmsDetailedLogWriter(final String fileName)
   {
      super(fileName);
   }
   
   
   /**
    * All of the logs in the LMS test could be considered 'sublogs' because
    * there isnt a single 'master' detailed log
    * @param iMessageText the type of log being initialized (LMS)
    */
   @Override
   public void initializeSubLog(final String iMessageText )
   {
      LOGGER.entering(getClass().getSimpleName(), "initializeSubLog()");      
      initializeDetailedFile(iMessageText);
      LOGGER.exiting(getClass().getSimpleName(), "initializeSubLog()");
   }
   
   /**
    * Closes the log file. Because the closeFile() method of the parent class
    * decrements the mCurrentLogWriter index, this method has been overwritten
    */
   @Override
   public void closeFile()
   {
      LOGGER.entering(getClass().getSimpleName(),"closeFile()");
      LOGGER.info("LmsDetailedLogWriter:closeFile() - closing file: " 
         + mDetailedFileName);
      
      try
      {
         mLogFile[mCurrentLogWriter].close();         
      }
      catch ( Exception e )
      {
         final String message = e.getClass().getSimpleName() + " occurred in LmsDetailedLogWriter." +
         "closeFile():" + e;
         LOGGER.severe(message);
         System.out.println(message);
      }
      
      LOGGER.exiting(getClass().getSimpleName(),"closeFile()");
   }// end of closeFile()   
}
