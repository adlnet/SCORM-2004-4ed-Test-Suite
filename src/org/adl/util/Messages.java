package org.adl.util;


/**
 * <strong>Filename: </strong>Messages.java<br><br>
 *
 * <strong>Description: </strong> <br>
 * The <code>Messages</code> creates a resource bundle that contains all of the
 * messages for the logging of the ADL Test Suite.    <br>
 *
 * @author ADL Technical Team<br><br>
 */
public class Messages
{
   /**
    * Default constructor.  No explicitly defined functionality for this 
    * constructor.
    */
   private Messages()
   {
      // No explicitly defined functionality
   }

   /**
    * Returns the CTS logging message associated with the given key. The method 
    * retrieves a parameterized message based on the input key and replaces the 
    * parameter with the input parameter. If the key is not found a default 
    * message based on the key will be returned.
    * 
    * @param iKey A unique identifier that identifies a message.
    * @return The message associated with the key.
    */
   public static String getString( String iKey, Object... args )
   {
      return Resources.getResources(Messages.class).getString(iKey, args);
   }
   
   /**
    * Utility to convert potential null values to empty strings.
    * 
    * @param iString String that if null should be converted to empty string.
    * 
    * @return The string or empty string if string was null.
    */
   public static String unNull(String iString)
   {
      return (iString == null) ? "" : iString;
   }
}
