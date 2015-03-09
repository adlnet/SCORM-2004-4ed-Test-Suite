package org.adl.logging;

import org.adl.util.Resources;

/**
 * This class provides the ability to access the messages held within the
 * loggingMessages.properties file.
 * 
 * @author ADL Technical Team
 * 
 */
public final class LoggingMessages
{
   /**
    * Default constructor. No explicitly defined functionality for this
    * constructor.
    */
   private LoggingMessages()
   {
      // No explicitly defined functionality
   }

   /**
    * Returns the message associated with the given key.
    * 
    * @param iKey A unique identifier that identifies a message.
    * @return The message associated with the key.
    */
   public static String getString(final String iKey, final Object... args)
   {
      return Resources.getResources(LoggingMessages.class).getString(iKey, args);
   }

   /**
    * Replaces a substring with another string within a string
    * 
    * @param ioString The string which holds the substring to be replaced
    * @param iOld The substring to be replaced
    * @param iNew The substring which is to replace the old substring
    * @return The string with the old substring replaced by the new substring
    */
   public static String replace(String ioString, final String iOld, final String iNew)
   {
      int startPos = 0;
      // int stringLength = ioString.length();
      int indexPos = -1;
      String tempString;

      while ((indexPos = ioString.indexOf(iOld, startPos)) != -1)
      {
         tempString = ioString.substring(0, indexPos) + iNew
               + ioString.substring(indexPos + iOld.length());

         ioString = tempString;
         startPos = indexPos + iNew.length();
      }

      return ioString;
   }
}
