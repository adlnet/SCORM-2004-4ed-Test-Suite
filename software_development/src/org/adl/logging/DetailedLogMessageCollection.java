package org.adl.logging;

/**
 * Singleton used to hold all messages destined for the detailed log
 * 
 * @author ADL Technical Team
 *
 */
public final class DetailedLogMessageCollection extends ADLMessageCollection
{
   /**
    * The one and only instance of the DetailedLogMessageCollection
    */
   private static final DetailedLogMessageCollection INSTANCE = 
      new DetailedLogMessageCollection();

   /**
    * Singleton.
    */
   private DetailedLogMessageCollection()
   {
      super();
   }

   /**
    * This method returns a reference to the instance of the 
    * DetailedLogMessageCollection
    * 
    * @return DetailedLogMessageCollection reference
    */
   public static DetailedLogMessageCollection getInstance()
   {
      return INSTANCE;
   }
}