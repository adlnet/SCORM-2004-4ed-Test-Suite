package org.adl.logging;

import org.adl.util.LogMessage;

/**
 * Class for logging to define the functionality of message formatting.
 * 
 * @author ADL Technical Team
 */
public abstract class MessageFormatter
{
   /**
    * Function accepts a LogMessage object and returns a string based on
    * formatting rules defined within.
    * @param iMessage Incoming message to be formatted
    * @return String formatted to rules defined
    */
   public abstract String formatMessage(LogMessage iMessage);
   
   
   /**
    * Replaces special characters with escape sequences
    * @param ioMsg - LogMessage to be processed
    * @return - LogMessage with special characters replaced
    */
   protected String formatMessageText(String ioMsg)
   {
      // call replace to deal with special characters
      if ( ioMsg.indexOf("&") != -1 )
      {
         // if this is the horizontal line, we want to leave it unaltered
         if(ioMsg.equals("&lt;HR&gt;"))
            return ioMsg;
         
         ioMsg = this.replace(ioMsg, "&", "&amp;");
      }

      if ( ioMsg.indexOf("\"") != -1 )
      {
         ioMsg = this.replace(ioMsg, "\"", "&quot;");
      }

      if ( ioMsg.indexOf("<") != -1 )
      {
         ioMsg = this.replace(ioMsg, "<", "&lt;");
      }

      if ( ioMsg.indexOf(">") != -1 )
      {
         ioMsg = this.replace(ioMsg, ">", "&gt;");
      }

      if ( ioMsg.indexOf("[") != -1 )
      {
         ioMsg = this.replace(ioMsg, "[", "&#91;");
      }

      if ( ioMsg.indexOf("]") != -1 )
      {
         ioMsg = this.replace(ioMsg, "]", "&#93;");
      }

      if ( ioMsg.indexOf("'") != -1 )
      {
         ioMsg = this.replace(ioMsg, "'", "&#39;");
      }

      if ( ioMsg.indexOf("\\") != -1 )
      {
         //ioMsg = this.replace(ioMsg, "\\", "\\\\");
      }

      return ioMsg;
   }
   
   /**
    * Replaces instances of one sub string with another sub string
    * @param ioString - The string of which contains sub strings to be replaced
    * @param iOld - The sub string to be replaced
    * @param iNew - The sub string to replace
    * @return The string with the replacements
    */
   protected static String replace(String ioString, String iOld, String iNew)
   {
      int startPos = 0;
      int indexPos = -1;
      String tempString;

      while ( (indexPos = ioString.indexOf(iOld, startPos)) != -1 )
      {
         tempString = ioString.substring(0, indexPos) + iNew
            + ioString.substring(indexPos + iOld.length());

         ioString = tempString;
         startPos = indexPos + iNew.length();
      }
      return ioString;
   }
}