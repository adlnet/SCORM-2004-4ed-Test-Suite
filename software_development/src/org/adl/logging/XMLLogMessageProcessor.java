package org.adl.logging;

import java.util.logging.Logger;

import org.adl.testsuite.util.CTSEnvironmentVariable;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;

/**
 * XMLLogMessageProcessor is responsible for handling all messages requested to
 * be formatted in XML. This class also provides functionality to "prepare" the
 * file by producing the necessary opening XML tags such as the opening root
 * element and link to the xslt.
 * 
 * @author ADL Technical Team
 * 
 */
public class XMLLogMessageProcessor implements LogMessageProcessor
{
   /** Object used to format the message to be printed out */
   private final transient MessageFormatter formatter;
   
   /** holds whether a link for metadata has already been found */
   private transient boolean mMDLinkFound = false;
   
   /** holds whether a link for SCO has already been found */
   private transient boolean mSCOLinkFound = false;
   
   /** holds whether a link for LMS has already been found */
   private transient boolean mLMSLinkFound;
   
   /** The url of the stylesheet for this xml */
   private transient String mStylesheet;
   
   /** The default "header" information used */
   private final transient String mXMLHeader;
   
   /**
    * Logger object used for debug logging
    */
   private static final Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");
   
   /**
    * Default constructor
    * @param logType holds the value "Summary" or "Detailed"
    */
   public XMLLogMessageProcessor(final String logType)
   {
      formatter = new XMLMessageFormatter();
      
      if("Summary".equals(logType))
      {
         mStylesheet = LoggingMessages.getString(
                                 "XMLLogMessageProcessor.xmlStylesheetSummary");
      }
      else
      {
         mStylesheet = LoggingMessages.getString(
                                "XMLLogMessageProcessor.xmlStylesheetDetailed");
      }
     
      mXMLHeader = 
         LoggingMessages.getString("XMLLogMessageProcessor.xmlDeclaration") + 
         "\n" +
         mStylesheet
         + "\n" +
         LoggingMessages.getString("XMLLogMessageProcessor.openingRootElement")
         + "\n" +
         LoggingMessages.getString("XMLLogMessageProcessor.envVar", 
            formatter.formatMessageText(
               CTSEnvironmentVariable.getCTSEnvironmentVariable())) +"\n";
   }
  
   /**
    * Returns the necessary opening element need to ensure well-formedness
    * 
    * @return String of the opening elements
    */
   public String prepareFile()
   {
      return mXMLHeader;
   }

   /**
    * Formats a message based on defined set of rules
    * 
    * @param iMessage - LogMessage object to be formatted
    * 
    * @return Formatted message string
    */
   public String formatMessage(final LogMessage iMessage)
   {
      LOGGER.entering("XMLLogMessageProcessor","formatMessage");
      LOGGER.finest("messageType is: " + iMessage.getMessageType() + 
                   "\tmessageText is: " + iMessage.getMessageText());
      String mRtnMsg = "";
      if ( (iMessage.getMessageType() == MessageType.LINKMD) && !mMDLinkFound )
      {
         mRtnMsg = formatter.formatMessage(new LogMessage(MessageType.XMLOTHER, "TH", "MD"));
         mMDLinkFound = true;
      }
      else if ( (iMessage.getMessageType() == MessageType.LINKSCO) && !mSCOLinkFound )
      {
         mRtnMsg = formatter.formatMessage(new LogMessage(MessageType.XMLOTHER, "TH", "SCO"));
         mSCOLinkFound = true;
      }
      else if ( (iMessage.getMessageType() == MessageType.LINKLMS) && !mLMSLinkFound )
      {
         mRtnMsg = formatter.formatMessage(new LogMessage(MessageType.XMLOTHER, "TH", "LMS"));
         mLMSLinkFound = true;
      }
  
      LOGGER.exiting("XMLLogMessageProcessor","formatMessage");
      return mRtnMsg + formatter.formatMessage(iMessage);
   }

   /**
    * Returns the necessary closing elements needed to ensure well-formedness
    * 
    * @return String of the closing elements.
    */
   public String endFile()
   {
      return LoggingMessages.getString("XMLLogMessageProcessor.closingRootElement");
   }

}
