package org.adl.util;


// xerces imports

// adl imports

/**
 * <strong>Filename:</strong><br>
 * EnvironmentVariable.java<br><br>
 *
 * <strong>Description:</strong><br>
 * A <code>EnvironmentVariable</code> is used to access a specified
 * environment variable.
 
 * @author ADL Technical Team
 */
public final class EnvironmentVariable
{
   private EnvironmentVariable()
   {
      // now it's a singleton
   }
   
   /**
    * Retrieves the value of the specified environment variable.
    *
    * @param iKey   Name of the environment variable.
    *
    * @return Value of the specified environment variable or an empty string
    *  if the value was not found
    */
   public static String getValue( final String iKey )
   {
      String value = System.getenv(iKey);
      if (value == null)
      {
         value = "";
      }
      return value;
   }
}