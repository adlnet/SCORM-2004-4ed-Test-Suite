package org.adl.util;

/**
 * The MessageType data
 * structure serves as the classification system for the messages.
 * This data structure determines which  <br><br>
 * 
 * @author ADL Technical Team
 */
public class MessageType
{
   // Public Data Members
   /**
    * value used to represent informational messages in the test logs
    */
   public static final int INFO           = 0;
   
   /**
    * value used to represent warning messages in the test logs
    */
   public static final int WARNING        = 1;
   
   /**
    * value used to represent passed messages in the test logs
    */
   public static final int PASSED         = 2;
   
   /**
    * value used to represent failed messages in the test logs
    */
   public static final int FAILED         = 3;
   
   /**
    * value used to represent terminate messages in the test logs
    */
   public static final int TERMINATE      = 4;
   
   /**
    * value used to represent conformant messages in the test logs
    */
   public static final int CONFORMANT     = 5;
   
   /**
    * value used to send a new log message to the log writers to create a new 
    * log
    */
   public static final int NEWLOG         = 7;
   
   /**
    * value used to send an end log message to the log writers to close out
    * the current log
    */
   public static final int ENDLOG         = 8;
   
   /**
    * value used to represent "other" messages in the test logs
    */
   public static final int OTHER          = 9;
   
   /**
    * value used to identify the message as not part of the test, instead this 
    * message is the heading for the test logs
    */
   public static final int HEADER         = 10;
   
   /**
    * value used to identify the message as not part of the test, instead this 
    * message is the title for the test logs
    */
   public static final int TITLE          = 11;
   
   /**
    * value used to identify the message as not part of the test, instead this 
    * message is the sub title for the test logs
    */
   public static final int SUBTITLE       = 12;
   
   /**
    * value used to represent informational messages in the heading of
    * the test logs
    */
   public static final int HEADINFO       = 13;
   
   /**
    * value used to represent warning messages in the heading of the test logs
    */
   public static final int HEADWARN       = 14;
   
   /**
    * value used to identify the message as not part of the test, instead this 
    * message is the title for the sub logs, such as md logs in the cp test
    */
   public static final int SUBLOGTITLE    = 15;
   
   /**
    * value used to represent other xml messages being sent to the test logs
    */
   public static final int XMLOTHER       = 16;
   
   /**
    * value used to identify a SCO detailed log link
    */
   public static final int LINKSCO        = 17;
   
   /**
    * value used to identify a MD detailed log link
    */
   public static final int LINKMD         = 18;
   
   /**
    * value used to identify a CP detailed log link
    */
   public static final int LINKCP         = 19;
   
   /**
    * value used to identify a manifest detailed log link
    */
   public static final int LINKMANIFEST   = 20;
   
   /**
    * value used to represent heading messages in the test sub logs, such as a 
    * MD log in the CP test
    */
   public static final int SUBLOGHEAD     = 21;
   
   /**
    * value used to represent the Save option for logs (as far as saving the logs
    * and restarting the test)
    */
   public static final int SAVE           = 22;
   
   /**
    * value used to represent the Retry option of a testcase
    */
   public static final int RETRY          = 23;
   
   /**
    * value used to represent the LINK portion of an LMS summary log
    */
   public static final int LINKLMS        = 24;
   
   /**
    * value used to represent an ABORT message
    */
   public static final int ABORT        = 25;
   
   /**
    * value used to represent an STOP message
    */
   public static final int STOP        = 26;

   /**
    * value used to represent a checksum value
    */
   public static final int CHECKSUM        = 27;
   
   /**
    * value used to represent informational messages in the test logs
    */
   public static final String _INFO          = "0";
 
   /**
    * value used to represent warning messages in the test logs
    */
   public static final String _WARNING       = "1";
   
   /**
    * value used to represent passed messages in the test logs
    */
   public static final String _PASSED        = "2";
   
   /**
    * value used to represent failed messages in the test logs
    */
   public static final String _FAILED        = "3";
   
   /**
    * value used to represent terminate messages in the test logs
    */
   public static final String _TERMINATE     = "4";
   
   /**
    * value used to represent conformant messages in the test logs
    */
   public static final String _CONFORMANT    = "5";
   
   /**
    * value used to represent "other" messages in the test logs
    */
   public static final String _OTHER         = "9";
   
   /**
    * value used to identify the message as not part of the test, instead this 
    * message is the heading for the test logs
    */
   public static final String _HEADER        = "10";
   
   /**
    * value used to identify the message as not part of the test, instead this 
    * message is the title for the test logs
    */
   public static final String _TITLE         = "11";
   
   /**
    * value used to identify the message as not part of the test, instead this 
    * message is the sub title for the test logs
    */
   public static final String _SUBTITLE      = "12";
   
   /**
    * value used to represent informational messages in the heading of
    * the test logs
    */
   public static final String _HEADINFO      = "13";
   
   /**
    * value used to represent warning messages in the heading of the test logs
    */
   public static final String _HEADWARN      = "14";
   
   /**
    * value used to identify the message as not part of the test, instead this 
    * message is the title for the sub logs, such as md logs in the cp test
    */
   public static final String _SUBLOGTITLE   = "15";
   
   /**
    * value used to represent other xml messages being sent to the test logs
    */
   public static final String _XMLOTHER      = "16";
   
   /**
    * value used to identify a SCO detailed log link
    */
   public static final String _LINKSCO       = "17";
   
   /**
    * value used to identify a MD detailed log link
    */
   public static final String _LINKMD        = "18";
   
   /**
    * value used to identify a CP detailed log link
    */
   public static final String _LINKCP        = "19";
   
   /**
    * value used to identify a manifest detailed log link
    */
   public static final String _LINKMANIFEST  = "20";
   
   /**
    * value used to represent heading messages in the test sub logs, such as a 
    * MD log in the CP test
    */
   public static final String _SUBLOGHEAD    = "21";
   
   /**
    * value used to represent the Save option for logs (as far as saving the logs
    * and restarting the test)
    */
   public static final String _SAVE          = "22";
   
   /**
    * value used to represent the Retry option of a testcase
    */
   public static final String _RETRY         = "23";
   
   /**
    * value used to represent the LINK portion of an LMS summary log
    */
   public static final String _LINKLMS       = "24";
   
   /**
    * value used to represent an ABORT message
    */
   public static final String _ABORT        = "25";
   
   /**
    * value used to represent a STOP message
    */
   public static final String _STOP        = "26";
   
   /**
    * value used to represent a checksum value
    */
   public static final String _CHECKSUM        = "27";
}
