package org.adl.logging;

/**
 * <strong>Filename </strong>: SummaryLogMessageCollection.java <br>
 * <br>
 * 
 * <strong>Description </strong>: The SummaryLogMessageCollection is a singleton
 * used to hold all messages destined for the summary log <br>
 * <br>
 * 
 * @author ADL Technical Team
 *  
 */
public final class SummaryLogMessageCollection extends ADLMessageCollection
{
   /**
    * the one and only instance of the SummaryLogMessageCollection
    */
   private static final SummaryLogMessageCollection INSTANCE = 
      new SummaryLogMessageCollection();
   
   /**
    * this makes it a singleton!
    */
   private SummaryLogMessageCollection()
   {
      super();
   }

   /**
    * This method returns a reference to the instance of the 
    * SummaryLogMessageCollection
    * 
    * @return SummaryLogMessageCollection reference
    */
   public static SummaryLogMessageCollection getInstance()
   {
      return INSTANCE;
   }
}
