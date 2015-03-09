package org.adl.validator.util;

/**
 * Stores the detailed result messages for the validator.  
 * 
 * @author ADL Technical Team
 *
 */
public class ValidatorMessage
{

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
    * value used to represent "other" messages in the test logs
    */
   public static final int OTHER          = 9;
   
   /**
    * This attribute holds the type of message classified. 
    */
   private int mMessageType;

   /**
    * This attribute holds the actual text to be communicated by the message.
    */
   private String mMessageText = "";

   /**
    * This holds the name of the test file used.  It is used for links in the
    * Detailed log.  For example "Click here to view detailed MD test log" will
    * now read "Click here to view detailed MD test log for RA_1" or whatever 
    * the IdRef is for that particular
    */
   private String mTestID = "";

   /**
    * This constructor initializes the message type and message text attributes
    * to the specified values.
    *
    * @param iMessageType - The type of message this is. Typically, this
    * should be "INFORMATION", "WARNING", or "ERROR", but this is up to the
    * client.
    *
    * @param iMessageText - The actual error message text.
    *
    */
   public ValidatorMessage(int iMessageType, String iMessageText)
   {
      mMessageType = iMessageType;
      mMessageText = iMessageText;

   }

  /**
    * This accessor returns the message type.
    *
    * @return int - The message type classified by the <code>MessageType</code>
    * class.
    */
   public int getMessageType()
   {
      return mMessageType;
   }

   /**
    * This accessor returns the message text.
    *
    * @return String - The message text.
    */
   public String getMessageText()
   {
      return mMessageText;
   }

   /**
    * Returns test id
    * 
    * @return the test id of this message
    */
   public String getTestID()
   {
       return mTestID;
   }

   /**
    * This method takes in a takes in a String version of a message type and 
    * returns the int version.
    * 
    * @param iMsg  String version of a message type.
    * @return int Value used to represent message types. 
    */
   public static int findMessageType(String iMsg)
   {
      int retInt = -1;
      if (iMsg.toUpperCase().equals("PASSED"))
      {
         retInt = PASSED;
      }
      else if (iMsg.toUpperCase().equals("FAILED"))
      {
         retInt = FAILED;
      }
      else if (iMsg.toUpperCase().equals("WARNING"))
      {
         retInt = WARNING;
      }
      else if (iMsg.toUpperCase().equals("INFO"))
      {
         retInt = INFO;
      }
      else 
      {
         retInt = OTHER;
      }
      return retInt;
   }
   
   /**
    * This method returns a representation of this message in a
    * predefined string form.
    *
    * Overloads the toString() method of the java.lang.Object class
    *
    * @return String - The message.
    */
   public String toString()
   {
      String  result = "";

      if ( mMessageType == INFO )
      {
         result = "INFO";
      }
      else if ( mMessageType == WARNING )
      {
         result = "WARNING";
      }
      else if ( mMessageType == PASSED )
      {
         result = "PASSED";
      }
      else if ( mMessageType == FAILED )
      {
         result = "FAILED";
      }
      else
      {
         result = "OTHER";
      }

      result = result + " : " + mMessageText;

      return result;
   }
   
   
}
