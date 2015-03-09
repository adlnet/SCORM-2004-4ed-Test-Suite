package org.adl.util.support;

import java.security.PrivilegedAction;
import java.security.AccessController;

import org.adl.util.MessageBox;
import org.adl.util.EnvironmentVariable;

/**
 * <strong>Filename:</strong>SupportVerifier.java<br><br>
 *
 * <strong>Description:</strong><br>
 * A <code>SupportVerifier</code> provides the ability to display support 
 * errors.  If the software was not tested for support, messages will be 
 * displayed in the following manner:
 * <ul>
 *    <li><strong>Operating System</strong> - If the user is running the software
 *     on an unsupported or untested Operating System, a warning is displayed 
 *     and the user is allowed to continue.</li>
 *    <li><strong>Java Version</strong> - If the user is running the software 
 *    with an unsupported or untested Java Version, an error is displayed and 
 *    the program is terminated.</li>
 * </ul>
 *
 * <strong>Design Issues:</strong><br><br>
 *
 * <strong>Implementation Issues:</strong><br><br>
 *
 * <strong>Known Problems:</strong><br><br>
 *
 * <strong>Side Effects:</strong><br>
 * If an unsupported Java version is detected, the program will terminate.
 * <br><br>
 *
 * <strong>References:</strong><br><br>
 *
 * @author ADL Technical Team
 */
public class SupportVerifier
{
   private static final int FIRST_SUPPORTED_JRE5_SUB = 10;
   private static final int FIRST_SUPPORTED_JRE5_MINOR = 0;

   private static final int FIRST_SUPPORTED_JRE14_SUB = 13;
   private static final int FIRST_SUPPORTED_JRE14_MINOR = 2;

   /**
    * Array of non-supported Operating Systems.
    */
   private static final String[] mNonSupportedOS = { "Windows 95",
                                                  "Windows 98",
                                                  "Windows NT" };
   
   /**
    * Default constructor.  The default constructor does nothing explicitly
    *
    */
   public SupportVerifier()
   {
      // Does nothing explicitly
   }

   /**
    * This method verifies that the environment variable was set up properly.
    * If not a messages is sent to the user.
    * 
    * @param iKey The environment variable that is being verified.
    */
   public void verifyEnvironmentVariable( String iKey )
   {
      String value = EnvironmentVariable.getValue( iKey );

      if ( "".equals(value) )
      {
         String title = "Environment Error";
         String messageText = "The \"" + iKey + "\" Environment Variable could " +
                              "not be detected.  This Environment\n Variable " +
                              "must be set correctly for successful " +
                              "operation of this software.";
         MessageBox.showMessage( MessageBox.SEVERE, messageText, title );
      }
   }

   /**
    * This method handles all Java Version support verification sequences.
    * 
    * @return A boolean that indicates whether or not the appropriate Java 
    * Run-Time Environment is being used.
    */
   public boolean verifyJRESupportBoolean()
   {
      return verifyJRESupportBoolean(null);
   }

   /**
    * This method handles all Java Version support verification sequences.
    * 
    * @param iVersion The version of java to verify. Expecting a String representation of
    * numbers separated by '.' and a '_' to separate sub from minor verson... ex
    * 1.4.2_11
    * 
    * @return A boolean that indicates whether or not the appropriate Java 
    * Run-Time Environment is being used.
    */
   public boolean verifyJRESupportBoolean(String iVersion)
   {
      String jreVersion = "";
      
      if ( iVersion == null || iVersion.equals("") )
      {
         jreVersion = System.getProperty("java.version");
      }
      else
      {
         jreVersion = iVersion;
      }

      boolean jreSupported = false;
      String[] jreTokens = parseToArray(jreVersion, '.');
      
      if ( jreTokens.length == 1 )
      {
         return Integer.parseInt(jreTokens[0]) > 1;
      }      
      
      if ( Integer.parseInt(jreTokens[0]) < 1 )
      {
         // the first token was less than 1 ex: 0.4.2 - don't think this will happen
         jreSupported = false;
      }
      else if ( Integer.parseInt(jreTokens[0]) > 1 )
      {
         // it must be 2.x or greater
         jreSupported = true;
      }
      else
      {
         if ( ! jreTokens.equals("") )
         {
            // i'm looking to see what the second number is.. 
            // ex 1.4.2_11 <- I'm looking at the 4
            switch ( Integer.parseInt(jreTokens[1]) )
            {
               // we don't like anything under 1.4...
               case 0: case 1: case 2: case 3:
               {
                  jreSupported = false;
                  break;
               }
               case 4:
               {
                  jreSupported = verifyMinorVersion(jreTokens,FIRST_SUPPORTED_JRE14_MINOR, FIRST_SUPPORTED_JRE14_SUB);
                  break;
               }
               case 5:
               {
                  jreSupported = verifyMinorVersion(jreTokens,FIRST_SUPPORTED_JRE5_MINOR, FIRST_SUPPORTED_JRE5_SUB);
                  break;
               }
               default:
               {
                  // this should cover all the new ones like 1.6.0, 1.7.0 and so on
                  jreSupported = true;
               }
            }
         }
      }

      return jreSupported;
   }

   /**
    * Confirms that the JRE version is supported.
    * 
    * @param iTokens An array containing the parts of the version number.
    * @param iFirstSupportedMinor The first minor version supported
    * @param iFirstSupportedSub The first sub version supported
    * @return boolean whether the version is supported or not
    */
   private boolean verifyMinorVersion(final String[] iTokens, 
                                      final int iFirstSupportedMinor, 
                                      final int iFirstSupportedSub)
   {
      boolean jreOK = false;
      
      // java seems to think that the second number is the major version. for example
      // 1.5 is Java 5. so, that should have already been verified. This is checking 
      // that there is another token, such as 1.5.0. if not, then we fail them.. I
      // haven't seen a java version that is just 1.4 or 1.5 or 1.6; they put the 0
      // on the end, ex 1.4.0, 1.5.0, 1.6.0
      if ( iTokens.length > 2 )
      {
         // if it has the underscore thingy (1.4.2_11) then we need to parse that 
         // and see if it's ok. The "2" in this case would be the minor version and 
         // the "11" would be the sub version
         if ( iTokens[2].indexOf('_') > -1 )
         {
            int testThis = Integer.parseInt((iTokens[2].split("_")[0]));
            if ( testThis == iFirstSupportedMinor )
            {
               jreOK = Integer.parseInt((iTokens[2].split("_")[1])) >= iFirstSupportedSub;
            }
            else if ( testThis > iFirstSupportedMinor )
            {
               jreOK = true;
            }
            else
            {
               jreOK = false;
            }
         }
         else
         {
            jreOK = Integer.parseInt(iTokens[2]) > iFirstSupportedMinor;
         }
      }
      return jreOK;
   }

   /**
    * This method handles all Operating System support verification sequences.
    * 
    * @return A boolean that indicates whether or not the operating system
    * being used is one that is supported.
    */
   public boolean verifyOSSupportBoolean()
   {
      String osName = System.getProperty("os.name");

      // Default to true - only looking for non-supported OS
      boolean osSupported = true;
      int arrayLength = mNonSupportedOS.length;

      for ( int i = 0; i < arrayLength; i++ )
      {
         if ( osName.equalsIgnoreCase( mNonSupportedOS[i] ) )
         {
            osSupported = false;
            break;
         }
      }

      return osSupported;
   }

   /**
    * This method handles all Operating System support verification sequences.
    * 
    * @return A string representing the current Operating System that the 
    * system is running on.
    */
   public String getCurrentOS()
   {
      String osName = System.getProperty("os.name");
      PrivilegedGetSP psp = new PrivilegedGetSP("sun.os.patch.level");
      
      String patch = (AccessController.doPrivileged(psp)).toString();
      String abbreviatedPatch = patch.replaceAll("Service Pack", "SP");
      String os = osName + " - " + abbreviatedPatch;
      

      return os;
   }

   /**
    * This method handles all Java Version support verification sequences.
    * 
    * @return A string representing the current Java Run-Time Environment that
    * the system is using.
    */
   public String getCurrentJRE()
   {
      String jreVersion = System.getProperty("java.version");

      return jreVersion;
   }
   
   /**
    * Parses a string into an array based on the given character. This was 
    * written to bypass issues found using the split() function.
    * 
    * @param iString The string to parse.
    * 
    * @param iCharOnWhichToParse The character on which to parse.
    * 
    * @return The parsed segments of the String stored in a String[].
    */
   private String[] parseToArray(String iString, char iCharOnWhichToParse)
   {
      int arraySize = 1;
      String countingStr = iString;
      while ( countingStr.indexOf(iCharOnWhichToParse) != -1 )
      {
         countingStr = countingStr.substring(countingStr.indexOf(iCharOnWhichToParse) + 1, countingStr.length());
         arraySize++;
      }
      String[] elementTokens = new String[arraySize];
      
      int idx = 0;
      while ( !iString.equals("") )
      {
         if ( iString.indexOf(iCharOnWhichToParse) != -1 )
         {
            elementTokens[idx++] = iString.substring(0, iString.indexOf(iCharOnWhichToParse));
            iString = iString.substring(iString.indexOf(iCharOnWhichToParse) + 1, iString.length());
         }
         else
         {
            elementTokens[idx++] = iString;
            iString = "";
         }
      }

      return elementTokens;
   }
   
   /**
    *
    * <strong>Description:</strong><br>This is a inner class that permits
    * the ability to retrieve information about the system.
    *
    */
   private class PrivilegedGetSP implements PrivilegedAction
   {
      /**
       * The string representation of the service pack
       */
      String mSPKey;
      
      /**
       * The value of the service pack
       */
      Object mSPValue;
      
      /**
       * Constructor for the inner class
       * 
       * @param iSPKey The service pack key
       */
      PrivilegedGetSP( String iSPKey )
      {
         mSPKey     = iSPKey;
      }

      /**
       * This run method grants privileged applet code access to write
       * to the summary log.  This allows the applet to work in Netscape 6.
       *
       * @return Object
       *
       */
      public Object run()
      {
         try
         {
            mSPValue = System.getProperty(mSPKey);
         }
         catch(Exception e)
         {
            e.printStackTrace();
         }

         return mSPValue;
      }
   }
}
