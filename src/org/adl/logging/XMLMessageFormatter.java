package org.adl.logging;

import java.io.File;
import java.util.logging.Logger;

import org.adl.util.LogMessage;
import org.adl.util.MessageType;

/**
 * This class formats a message based on xml rules defined by the ADL Technical
 * Team and based on the type of LogMessage this class receives.
 * 
 * @author ADL Technical Team
 * 
 */
public class XMLMessageFormatter extends MessageFormatter
{
   /**
    * holds the list of messages used for formatting the message
    */
   private static final String[] MESSAGES = new String[27];
   static
   {
   // element [0]
      MESSAGES[MessageType.INFO] = 
                             LoggingMessages.getString("MessageFormatter.info");      
      // element [1]
      MESSAGES[MessageType.WARNING] = 
                             LoggingMessages.getString("MessageFormatter.warn");
      // element [2]
      MESSAGES[MessageType.PASSED] = 
                             LoggingMessages.getString("MessageFormatter.pass");
      // element [3]
      MESSAGES[MessageType.FAILED] = 
                             LoggingMessages.getString("MessageFormatter.fail");
      // element [4]
      MESSAGES[MessageType.TERMINATE] = 
                             LoggingMessages.getString("MessageFormatter.term");
      // element [5]
      MESSAGES[MessageType.CONFORMANT] = 
                             LoggingMessages.getString("MessageFormatter.conf");      
      // element [9]
      MESSAGES[MessageType.OTHER] = 
                            LoggingMessages.getString("MessageFormatter.other");
      // element [10]
      MESSAGES[MessageType.HEADER] = 
                             LoggingMessages.getString("MessageFormatter.head");
      // element [11]
      MESSAGES[MessageType.TITLE] = 
                            LoggingMessages.getString("MessageFormatter.title");
      // element [12]
      MESSAGES[MessageType.SUBTITLE] = 
                              LoggingMessages.getString("MessageFormatter.sub");
      // element [13]
      MESSAGES[MessageType.HEADINFO] = 
                         LoggingMessages.getString("MessageFormatter.headinfo");
      // element [14]
      MESSAGES[MessageType.HEADWARN] = 
                         LoggingMessages.getString("MessageFormatter.headwarn");
      // element [15]
      MESSAGES[MessageType.SUBLOGTITLE] =
                         LoggingMessages.getString("MessageFormatter.sublogSubheading");
//      msgTypes[MessageType.XMLOTHER] =
//                         LoggingMessages.getString();
      // element [17]
      MESSAGES[MessageType.LINKSCO] = 
                         LoggingMessages.getString("MessageFormatter.scoLinkTxt");
      // element [18]
      MESSAGES[MessageType.LINKMD] =
                         LoggingMessages.getString("MessageFormatter.mdLinkTxt");
      // element [19]
      MESSAGES[MessageType.LINKCP] =
                         LoggingMessages.getString("MessageFormatter.cpLinkTxt");
      // element [20]
      MESSAGES[MessageType.LINKMANIFEST] =
                         LoggingMessages.getString("MessageFormatter.manifestLinkTxt");
     // element [21]
      MESSAGES[MessageType.SUBLOGHEAD] =
                         LoggingMessages.getString("MessageFormatter.head");
     // element [22]
      MESSAGES[MessageType.SAVE] =
                         LoggingMessages.getString("MessageFormatter.save");
     // element [23]
      MESSAGES[MessageType.RETRY] =
                         LoggingMessages.getString("MessageFormatter.retry");
     // element [24]
      MESSAGES[MessageType.LINKLMS] =
                         LoggingMessages.getString("MessageFormatter.lmsLinkTxt", "p1");
     // element [25]
     MESSAGES[MessageType.ABORT] =
                         LoggingMessages.getString("MessageFormatter.abort");
     // element [26]
     MESSAGES[MessageType.STOP] =
                         LoggingMessages.getString("MessageFormatter.stop");
   }
   
   /**
    * Logger object used for debug logging
    */
   private static final Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");
   
   /**
    * This function formats a LogMessage based on xml version 1.0 rules and the 
    * schema designed by the ADL Technical Team.
    * 
    * @param iMessage LogMessage object to format based on xml rules
    * 
    * @return String representation of the xml element for a message
    * @see org.adl.logging.MessageFormatter#formatMessage(org.adl.util.LogMessage)
    */
   public String formatMessage(final LogMessage iMessage)
   {
      LOGGER.entering(getClass().getSimpleName(),"formatMessage");
      
      final int messageType = iMessage.getMessageType();
      final String messageText = formatMessageText(iMessage.getMessageText().replaceAll("&nbsp;"," "));
      String temp;
      switch (messageType)
      {
         case MessageType.HEADER:
         case MessageType.HEADINFO:
         case MessageType.HEADWARN:
            temp = LoggingMessages.getString("XMLMessageFormatter.headerElement",
               MESSAGES[messageType],messageText) + "\n";
            LOGGER.exiting(getClass().getSimpleName(),"formatMessage");
            break;
            
         case MessageType.TITLE:
         case MessageType.SUBTITLE:
            temp = LoggingMessages.getString("XMLMessageFormatter.titleElement",
               MESSAGES[messageType],messageText) + "\n";
            LOGGER.exiting(getClass().getSimpleName(),"formatMessage");
            break;
            
         case MessageType.LINKMD:
         {
            // pull just the name of the log, as it is in the same directory as the
            // SummaryLog we'll pass a local reference into the SummaryLog link
            final String tempLogName = messageText.substring(messageText.lastIndexOf(File.separator) + 1,
               messageText.length());
            
            temp = LoggingMessages.getString("XMLMessageFormatter.linkElement",
               "MD",iMessage.getTestID(),
               tempLogName) + "\n";
            LOGGER.exiting(getClass().getSimpleName(),"formatMessage");
            break;
         }
         case MessageType.LINKSCO:
         {
            // pull just the name of the log, as it is in the same directory as the
            // SummaryLog we'll pass a local reference into the SummaryLog link
            final String tempLogName = messageText.substring(messageText.lastIndexOf(File.separator) + 1,
               messageText.length());
            
            temp = LoggingMessages.getString("XMLMessageFormatter.linkElement",
               "SCO", iMessage.getTestID(),
               tempLogName) + "\n";
            LOGGER.exiting(getClass().getSimpleName(),"formatMessage");
            break;
         }
         case MessageType.LINKCP:
         {
            // pull just the name of the log, as it is in the same directory as the
            // SummaryLog we'll pass a local reference into the SummaryLog link
            final String tempLogName = messageText.substring(messageText.lastIndexOf(File.separator) + 1,
               messageText.length());
            
            temp = LoggingMessages.getString("XMLMessageFormatter.linkElement",
               "CP", iMessage.getTestID(),
               tempLogName) + "\n";
            
            LOGGER.exiting(getClass().getSimpleName(),"formatMessage");
            break;
         }
         case MessageType.LINKLMS:
         {
            // pull just the name of the log, as it is in the same directory as the
            // SummaryLog we'll pass a local reference into the SummaryLog link
            final String tempLogName = messageText.substring(messageText.lastIndexOf(File.separator) + 1,
               messageText.length());
            
            temp = LoggingMessages.getString("XMLMessageFormatter.linkElement",
               "LMS", iMessage.getTestID(),
               tempLogName) + "\n";
            
            LOGGER.exiting(getClass().getSimpleName(),"formatMessage");
            break;
         }
         case MessageType.XMLOTHER:
            temp = LoggingMessages.getString("XMLMessageFormatter.otherElement",
               messageText, iMessage.getTestID()) + "\n";
            LOGGER.exiting(getClass().getSimpleName(),"formatMessage");
            break;
            
         case MessageType.RETRY:
            temp = LoggingMessages.getString("XMLMessageFormatter.otherElement",
               "retry", iMessage.getMessageText()) + "\n";
            LOGGER.exiting(getClass().getSimpleName(),"formatMessage");
            break;
            
         case MessageType.CHECKSUM:
            temp = LoggingMessages.getString("XMLMessageFormatter.msgElement",
                  "checksum", iMessage.getMessageText()) + "\n";
               LOGGER.exiting(getClass().getSimpleName(),"formatMessage");
               break;
            
         default:
            temp = LoggingMessages.getString("XMLMessageFormatter.msgElement",
               MESSAGES[messageType],messageText) + "\n";
            
            LOGGER.exiting(getClass().getSimpleName(),"formatMessage");
            break;
      }// end switch
      return temp;
   }// end formatMessage()
}