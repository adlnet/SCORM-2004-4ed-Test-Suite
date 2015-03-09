package org.adl.testsuite.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.adl.util.EnvironmentVariable;


/**
 
 * <strong>Filename:</strong><br>
 * EnvironmentVariable.java<br><br>
 *
 * <strong>Description:</strong><br>
 * A <code>EnvironmentVariable</code> is used to access a specified
 * environment variable.
 * 
 * @author ADL Technical Team
 */
public final class CTSEnvironmentVariable
{
   /**
    * Stores the Environment Variable Key that describes where the Test Suite is
    * installed to.
    */
   public static final String CTS_ENV = "SCORM4ED_TS111_HOME";

   /**
    * set to true once the call has been made to the system to get the value
    * of the data, false if the call has not yet been made
    */
   private static boolean dataAlreadyLookedUp = false;
   
   /**
    * holds the value of the environment variable
    */
   private static String mEnvironmentVariable;

   /**
    * This method returns the actual environment variable value.  The location
    * of the installed Test Suite.
    * 
    * @return  A string representation of the location of the installed 
    * Test Suite.
    */
   public static String getCTSEnvironmentVariable()
   {
      if(!dataAlreadyLookedUp)
      {
    	  mEnvironmentVariable = EnvironmentVariable.getValue(CTS_ENV);
    	  dataAlreadyLookedUp = true;
      }
      
      //returns the enviroment variable
      return mEnvironmentVariable;
   }
}