package org.adl.util;

import java.util.logging.Logger;

/**
 * This class is used to convert Strings read from an xml log to LogMessage
 * objects. This was created to read in old logs from a previously saved test
 * session and to write them to the browser when the saved test is restarted.
 * @author ADL Tech Team
 */
public class MessageConverter
{
   /**
    * holds the path to the log folder, this is used when creating the link
    * for the html summary log when it is written from a saved xml summary log
    */
   private String mLogFolder;
   
   /**
    * Logger object used for debug logging
    */
   private Logger mLogger = Logger.getLogger("org.adl.util.debug.testsuite");
   
   /**
    * Default constructor, sets the mLogFolder variable
    * @param logFolder path to the folder holding the logs
    */
   public MessageConverter(String logFolder)
   {
      mLogFolder = logFolder;
   }
   
   /**
    * Takes in a line from an xml log, parses it, and creates a LogMessage object
    * with the correct MessageType and message text
    * @param inMsg the String read from the log file to be parsed into a LogMessage
    * @return LogMessage object constructed from the log entry
    */
   public LogMessage convertToLogMessage(String inMsg)
   {
      mLogger.entering("MessageConverter","convertToLogMessage()");
      mLogger.finest("inMsg is: " + inMsg);
      LogMessage result = null;
      
      // the message must be parsed depending on what kind of log message it is
      if(inMsg.startsWith("<header"))
         result =  parseHeader(inMsg);
      
      else if(inMsg.startsWith("<message"))
         result =  parseMessage(inMsg);
      
      else if(inMsg.startsWith("<link"))
         result =  parseLink(inMsg);
      
      else if(inMsg.startsWith("<other"))
         result =  parseOther(inMsg);
      
      mLogger.fine("returning msgType: " + result.getMessageType() +
                            " msgText: " + result.getMessageText());
      
      mLogger.exiting("MessageConverter","convertToLogMessage()");
      return result;
   }// end convertToLogMessage()
   
   /**
    * Parses the line read if from the log if it is a <header>
    * @param hdr the line read from file
    * @return LogMessage object created from the parsed log entry
    */
   private LogMessage parseHeader(String hdr)
   {
      int type = -1;
      
      if(hdr.indexOf("type=\"head") > 0)
         type = MessageType.HEADER;
      else if(hdr.indexOf("type=\"warn") > 0)
         type = MessageType.HEADWARN;
      else if(hdr.indexOf("type=\"info") > 0)
         type = MessageType.HEADINFO;
      
      String msgTxt = getMessageText(hdr, 1);
      LogMessage msg = new LogMessage(type,msgTxt);
      
      return msg;
   }// end parseHeader()
   
   /**
    * Parses the line read if from the log if it is a <message>
    * @param msg the line read from file
    * @return LogMessage object created from the parsed log entry
    */
   private LogMessage parseMessage(String msg)
   {
      int type = -1;
      
      if(msg.indexOf("type=\"warn") > 0)
         type = MessageType.WARNING;
      
      else if(msg.indexOf("type=\"other") > 0)
         type = MessageType.OTHER;
      
      else if(msg.indexOf("type=\"info") > 0)
         type = MessageType.INFO;
      
      else if(msg.indexOf("type=\"pass") > 0)
         type = MessageType.PASSED;
      
      else if(msg.indexOf("type=\"fail") > 0)
         type = MessageType.FAILED;
      
      String msgTxt = getMessageText(msg, 2);
      LogMessage logMsg = new LogMessage(type,msgTxt);
      
      return logMsg;
   }// end parseMessage()
   
   /**
    * Parses the line read if from the log if it is a <link>
    * @param link the line read from file
    * @return LogMessage object created from the parsed log entry
    */
   private LogMessage parseLink(String link)
   {
      int type = -1;
      
      if(link.indexOf("type=\"LMS") > 0)
         type = MessageType.LINKLMS;      

      String msgID = link.substring(link.indexOf("name=") + 6, link.indexOf("\">"));
      String msgTxt = mLogFolder + getMessageText(link, 3);
      LogMessage logMsg = new LogMessage(type,msgTxt, msgID);
      return logMsg;
   }// end parseLink()
   
   /**
    * Parses the line read if from the log if it is an <other>
    * @param other the line read from file
    * @return LogMessage object created from the parsed log entry
    */
   private LogMessage parseOther(String other)
   {
      int type = MessageType.XMLOTHER;      
      String msgTxt = getMessageText(other, 4);
      String msgID = other.substring(other.indexOf("\"")+1, other.lastIndexOf("\""));

      LogMessage logMsg = new LogMessage(type, msgTxt, msgID);
      
      return logMsg;
   }// end parseOther()
   
   /**
    * Parses the log entry and pull out the message text
    * @param msg the line read in from the log
    * @param typeOfMessage this will either be 1 for <header>, 2 for <message>,
    * 3 for <link>, or 4 for <other>
    * @return the message text in between the tags
    */
   private String getMessageText(String msg, int typeOfMessage)
   {
      String results = null;
      switch(typeOfMessage)
      {
         case 1:// <header>
            results = msg.substring(msg.indexOf(">")+1,msg.indexOf("</head"));
            break;
         case 2: // <message>
            results = msg.substring(msg.indexOf(">")+1,msg.indexOf("</mess"));
            break;
         case 3: // <link>
            results = msg.substring(msg.indexOf(">")+1,msg.indexOf("</link"));
            break;
         case 4: // <other>
            results = msg.substring(msg.indexOf(">")+1,msg.indexOf("</other"));
      }
      return results;
   }// end getMessageText()

}
