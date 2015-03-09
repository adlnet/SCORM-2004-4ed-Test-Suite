package org.adl.testsuite.rte.lms.util;

import org.adl.util.Resources;

/**
 * This class is responsible for accessing strings stored in property files 
 * related to the LMS Test. This is implemented as a singleton and as such it is 
 * expected to be accessed in a static way.
 * 
 * @author ADL Technical Team
 */
public class LMSMessages
{
   /**
    * Private constructor
    */
   private LMSMessages()
   {
    //  mLocales = new HashMap();
   }
   
   public static class Commands
   {
      public static String getString( String iKey, Object... args )
      {
         return Resources.getResources(Commands.class).getString(iKey, args);
      } 
   }
   
   public static class Instructions
   {
      public static String getString( String iKey, Object... args )
      {
         return Resources.getResources(Instructions.class).getString(iKey, args);
      } 
   }
   
   public static class UserInterface
   {
      public static String getString( String iKey, Object... args )
      {
         return Resources.getResources(UserInterface.class).getString(iKey, args);
      } 
   }
}
