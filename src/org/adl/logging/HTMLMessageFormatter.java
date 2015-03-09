package org.adl.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.adl.testsuite.util.CTSEnvironmentVariable;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;

/**
 * This class formats the incoming message to html based on the type of message
 * this class has received.
 * 
 * @author ADL Technical Team
 *
 */
public class HTMLMessageFormatter extends MessageFormatter
{
   /** location of the home directory of the Test Suite */
   private static final String ADL_HOME = CTSEnvironmentVariable.getCTSEnvironmentVariable();
   
   private static final String IMG_PATH = replace(ADL_HOME, "\\", "/") + LoggingMessages.getString("HTMLMessageFormatter.imgPath");
   
   /**
    * Logger object used for debug logging
    */
   private static final Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");
   private static final Map<Integer, String> ICONS, STYLES, EXCLAMATIONS, LINK_TEXTS;
   static
   {
      ICONS = new HashMap<Integer, String>();
      ICONS.put(MessageType.INFO, LoggingMessages.getString("HTMLMessageFormatter.infoIcon"));
      ICONS.put(MessageType.WARNING, LoggingMessages.getString("HTMLMessageFormatter.warningIcon"));
      ICONS.put(MessageType.PASSED, LoggingMessages.getString("HTMLMessageFormatter.passedIcon"));
      ICONS.put(MessageType.FAILED, LoggingMessages.getString("HTMLMessageFormatter.failedIcon"));
      ICONS.put(MessageType.TERMINATE, LoggingMessages.getString("HTMLMessageFormatter.terminateIcon"));
      ICONS.put(MessageType.STOP, LoggingMessages.getString("HTMLMessageFormatter.terminateIcon"));
      ICONS.put(MessageType.CONFORMANT, LoggingMessages.getString("HTMLMessageFormatter.conformanceIcon"));
      ICONS.put(MessageType.CHECKSUM, LoggingMessages.getString("HTMLMessageFormatter.infoIcon"));
      
      STYLES = new HashMap<Integer, String>();
      STYLES.put(MessageType.INFO, "MessageFormatter.info");
      STYLES.put(MessageType.WARNING, "MessageFormatter.warn");
      STYLES.put(MessageType.PASSED, "MessageFormatter.pass");
      STYLES.put(MessageType.FAILED, "MessageFormatter.fail");
      STYLES.put(MessageType.TERMINATE, "MessageFormatter.term");
      STYLES.put(MessageType.STOP, "MessageFormatter.stop");
      STYLES.put(MessageType.CONFORMANT, "MessageFormatter.conf");
      STYLES.put(MessageType.CHECKSUM, "MessageFormatter.info");
      STYLES.put(MessageType.SUBLOGTITLE, "MessageFormatter.sublogHeading");
      STYLES.put(MessageType.SUBLOGHEAD, "MessageFormatter.sublogSubheading");
      STYLES.put(MessageType.HEADINFO, "MessageFormatter.pass");
      STYLES.put(MessageType.HEADWARN, "MessageFormatter.warn");
      
      EXCLAMATIONS = new HashMap<Integer, String>();
      EXCLAMATIONS.put(MessageType.INFO, "");
      EXCLAMATIONS.put(MessageType.WARNING, LoggingMessages.getString("MessageFormatter.warningString"));
      EXCLAMATIONS.put(MessageType.PASSED, "");
      EXCLAMATIONS.put(MessageType.FAILED, LoggingMessages.getString("MessageFormatter.errorString"));
      EXCLAMATIONS.put(MessageType.TERMINATE, LoggingMessages.getString("MessageFormatter.errorString"));
      EXCLAMATIONS.put(MessageType.STOP, "");
      EXCLAMATIONS.put(MessageType.CONFORMANT, "");
      
      LINK_TEXTS = new HashMap<Integer, String>();
      LINK_TEXTS.put(MessageType.LINKMD, "MessageFormatter.mdLinkTxt");
      LINK_TEXTS.put(MessageType.LINKSCO, "MessageFormatter.scoLinkTxt");
      LINK_TEXTS.put(MessageType.LINKCP, "MessageFormatter.cpLinkTxt");
      LINK_TEXTS.put(MessageType.LINKMANIFEST, "MessageFormatter.manifestLinkTxt");
   }
   
   /**
    * This function formats a LogMessage based on html rules and on the type of
    * LogMessage that this method received.
    * 
    * @param iMessage LogMessage object to format based on html rules
    * 
    * @return String representation of the html element used for a message
    * @see org.adl.logging.MessageFormatter#formatMessage(org.adl.util.LogMessage)
    */
   public String formatMessage(final LogMessage iMessage)
   {
      LOGGER.entering("HTMLMessageFormatter", "formatMessage()");
      final int messageType = iMessage.getMessageType();
      String messageText = iMessage.getMessageText();
      String returnString;
      
      /* If the MessageText contains any of the following chars we want
         it to fall through and be written to file. If none of these
         characters exist then go through the message and change any
         of the characters found in the formatMessageText method.
         The <br and <hr tags are intentionally left incomplete so that we 
         catch both <br> and <br />. */
      if ( isFormatted(messageText) )
      {
         messageText = formatMessageText(messageText);
      }

      if (requiresSlashSwitching(messageType))
      {
         /*
          * this was added to fix the issue with backslashes being
          * lost.. this will replace any double backslashes with a
          * forward slash
          */
         messageText = replace(messageText, "\\\\", "/");
      }
         
      
      
      if (doMessage(messageType))
      {
          returnString = getMessage(messageType, messageText);
      }
      else if (doLink(messageType))
      {
         if (messageType == MessageType.LINKLMS)
         {
            // get just the name of the testcase
            final String testcase = getTestCaseName(messageText);

            final String formatted  = 
               LoggingMessages.getString("HTMLMessageFormatter.ahrefElement",
               replace(messageText, "\\", "/"), 
               LoggingMessages.getString("MessageFormatter.lmsLinkTxt",testcase));

            returnString = getTD(formatted);
         }
         else
         {
            returnString = getLink(messageType, messageText);
         }
      }
      else if (doNothing(messageType))
      {
         returnString = "";
      }
      else //MessageType.OTHER:
      {
         if ( messageText.indexOf("HR") > -1 )
         {
            returnString = "<td>" +
               LoggingMessages.getString("HTMLMessageFormatter.hr") + "</td>";
         }
         else
         {
            returnString = getTD(getSpan("MessageFormatter.other", messageText));
         }
      }
      
      LOGGER.finest("Formatted message is: " + returnString);
      LOGGER.exiting("HTMLMessageFormatter", "formatMessage()");
      return returnString;
   }
   
   /**
    * Checks if the message type is to be displayed as text.
    * 
    * @param messageType the type of message
    * @return TRUE if it is, FALSE if not
    */
   protected boolean doMessage(final int messageType)
   {
      return (messageType == MessageType.WARNING) ||
             (messageType == MessageType.PASSED) ||
             (messageType == MessageType.FAILED) ||
             (messageType == MessageType.TERMINATE) ||
             (messageType == MessageType.STOP) ||
             (messageType == MessageType.CONFORMANT) ||
             (messageType == MessageType.SUBLOGTITLE) ||
             (messageType == MessageType.SUBLOGHEAD) ||
             (messageType == MessageType.HEADINFO) ||
             (messageType == MessageType.HEADWARN) ||
             (messageType == MessageType.INFO);
   }
   
   /**
    * Checks if the message type is to be ignored.
    * 
    * @param messageType the type of message
    * @return TRUE if it is, FALSE if not
    */
   protected boolean doNothing(final int messageType)
   {
      return (messageType == MessageType.SAVE) ||
             (messageType == MessageType.RETRY) ||
             (messageType == MessageType.NEWLOG) ||
             (messageType == MessageType.ENDLOG);
   }
   
   /**
    * Checks if the message type is to be displayed as a link.
    * 
    * @param messageType the type of message
    * @return TRUE if it is, FALSE if not
    */
   protected boolean doLink(final int messageType)
   {
      return (messageType == MessageType.LINKMD) ||
             (messageType == MessageType.LINKSCO) ||
             (messageType == MessageType.LINKCP) ||
             (messageType == MessageType.LINKMANIFEST) ||
             (messageType == MessageType.LINKLMS);
   }
   
   /**
    * Checks if the message type requires its slashes switched.
    * 
    * @param messageType the type of message
    * @return TRUE if it is, FALSE if not
    */
   protected boolean requiresSlashSwitching(final int messageType)
   {
      return (messageType == MessageType.INFO) || 
             (messageType == MessageType.CHECKSUM); 
   }
   
   /**
    * Checks if the message is already formatted.
    * 
    * @param messageText the message
    * @return TRUE if it is, FALSE if not
    */
   protected boolean isFormatted(final String messageText)
   {
      return messageText.indexOf("file:///") == -1
          && messageText.indexOf("********************") == -1
          && messageText.indexOf("<br") == -1
          && messageText.indexOf("<BR") == -1
          && messageText.indexOf("<HR") == -1
          && messageText.indexOf("<hr") == -1
          && messageText.indexOf("&nbsp;") == -1;
   }
   
   /**
    * 
    * @param messageText the path to the detailed log
    * @return
    */
   protected String getTestCaseName(final String messageText)
   {
      final Pattern pattern = 
         Pattern.compile("([A-Z]{3})|([A-Z]{1,2})-\\d{2}[a-z]{0,2}");
      final Matcher matcher = pattern.matcher(messageText);
      // go to the last match
      String name = "";
      while (matcher.find())
      {
         name = matcher.group();  
      }
      return name;
   }
   
   protected String getImg(final int messageType)
   {
      return getImg(ICONS.get(messageType));
   }
   
   protected String getImg(final String icon)
   {
      final StringBuffer dir = new StringBuffer();
      dir.append(IMG_PATH);
      dir.append(icon);
      
      return LoggingMessages.getString("HTMLMessageFormatter.imgElement", 
                                          formatMessageText(dir.toString()));
   }
   
   protected String getSpan(final int messageType, final String text)
   {
      final String exc = EXCLAMATIONS.get(messageType);
      return (exc == null) ? getSpan(STYLES.get(messageType), text):
         getSpan(STYLES.get(messageType), exc, text);
   }
   
   protected String getSpan(final String classKey, final String type, final String text)
   {
      return LoggingMessages.getString("HTMLMessageFormatter.spanElement",
            LoggingMessages.getString(classKey), type, text);
   }
   
   protected String getSpan(final String classKey, final String text)
   {
      return getSpan(classKey, "", text);
   }
   
   protected String getTD(final String image, final String contents)
   {
      return LoggingMessages.getString("HTMLMessageFormatter.td", image, contents);
   }
   
   protected String getTD(final String contents)
   {
      return getTD(" ", contents);
   }
   
   protected String getMessage(final int messageType, final String message)
   {
      final String img = ICONS.get(messageType);
      return (img == null) ? getTD(getSpan(messageType, message)) :
         getTD(getImg(messageType), getSpan(messageType, message));
   }
   
   protected String getLink(final int messageType, final String href)
   {
      return getTD(LoggingMessages.getString("HTMLMessageFormatter.ahrefElement",
            replace(href, "\\", "/"), 
            LoggingMessages.getString(LINK_TEXTS.get(messageType))));
   }
}