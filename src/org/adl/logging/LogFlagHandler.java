package org.adl.logging;

/**
 * <strong>Filename</strong>:  LogFlagHandler.java<br><br>
 *
 * <strong>Description</strong>:  This class acts as an interface for tracking 
 * if a SCO has started and stopped logging information.  This is needed due 
 * to threading problems when a SCO starts up before another SCO has 
 * completed.</br><br>
 * 
 * @author ADL Technical Team
 */
public class LogFlagHandler
{
   /**
    * flag stating whether a sco is running
    */
   private static boolean mSCORunning;

   /**
    * This is the constructor for the LogFlagHandler class. It initializes
    * the SCO running flag to false.
    */
   public LogFlagHandler()
   {
      mSCORunning = false;
   }

   /**
    * Provides the ability to set the sco running flag
    * 
    * @param iSCORunning true or false
    */
   public synchronized void setSCORunning( boolean iSCORunning )
   {
      mSCORunning = iSCORunning;
   }

   /**
    * Returns the status of whether a sco is running or not
    * 
    * @return boolean of whether the sco is running
    */
   public synchronized boolean getSCORunning()
   {
      return mSCORunning;
   }
}