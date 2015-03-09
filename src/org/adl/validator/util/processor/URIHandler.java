package org.adl.validator.util.processor;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class will process the given URI String value * 
 * 
 * @author ADL Technical Team
 *
 */
public class URIHandler
{
   /**
    * A constant value to be used by the entire validator to represent the encoding 
    */
   public static final String ENCODING = "UTF-16";
   
   /**
    * A Constant value of 2
    */
   private static final int TWO = 2;
   
   /**
    * A Constant value of 3
    */
   private static final int THREE = 3;
   
   /**
    * A constant value for 4
    */
   private static final int FOUR = 4;
   
   /**
    * A constant value for 5
    */
   private static final int FIVE = 5;
   
   /**
    * A constant value for 6
    */
   private static final int SIX = 6;
   
   /**
    * A Constant value of 16
    */
   private static final int SIXTEEN = 16;
   
   /**
    * The decimal value of hex number 7F
    * The last character on the Basic-Latin Unicode Code Chart
    */
   private static final int BASIC_LATIN = 127;
   
   /**
    * Integer containing the number of directories by which the root was exceeded 
    */
   private int mBeyondRoot;
   
   /**
    * The default constructor
    */
   public URIHandler()
   {      
      // default constructor
      mBeyondRoot = 0;
   }

   /**
    * This method encodes any non-English characters in the given String using the
    * specified Unicode encoding
    * 
    * @param iURIValue is a String containing the URI to be encoded
    * @param iEncoding is the Unicode encoding used to encode the given URI 
    * value
    * 
    * @return a String containing the encoded URI value
    */
   public static String encode( String iURIValue, String iEncoding )
   {     
      String encodedFileName = iURIValue;
      final String encoding = iEncoding;
      String encodedString = "";
      
      int nameLength = encodedFileName.length();
      int currentCharCode = 0;
      
      for ( int i = 0; i < nameLength; i++ )
      {
         // Obtain the ASCII code of each character in the fileName
         char currentChar =  encodedFileName.charAt(i);
         currentCharCode = (int)currentChar;
         if ( currentCharCode > BASIC_LATIN )
         {
            encodedString = encodedString + "%" + Integer.toString(currentCharCode, SIXTEEN);
         }
         else
         {
            encodedString = encodedString + currentChar;
         }
      }
      
      try
      {
         // Encode the newly encoded string in its given encoding 
         final byte[] byteString = encodedString.getBytes(encoding);
         
         return new String(byteString, encoding);
         
      }
      catch(UnsupportedEncodingException uee)
      {
         return iURIValue;
      }
   }
   
   /**
    * This method decodes any encoded characters in the given String using the
    * specified Unicode encoding
    * 
    * @param iURIValue is a String containing the URI to be decoded
    * @param iEncoding is the Unicode encoding used to decode the given URI 
    * value
    * 
    * @return a String containing the decoded URI value
    */
   public static String decode( String iURIValue, String iEncoding )
   {     
      int index = -1;      
      String charCode = "";
      int intCode;
      char decodedCharacter;
      String sectionOne = "";
      String sectionTwo = "";
      String decodedString = ""; 
      String decodedFileName = "";
      
      String encodedFileName = iURIValue;
      final String encoding = iEncoding;
      
      
      
      // Determine if an encoded character exists
      index = iURIValue.indexOf("%");    
      
      int nameLength = encodedFileName.length();
      
      if ( index > -1)
      {        
         // An encoded character is contained within the filename
         for (int i = 0; i < nameLength; i++)
         {
            if ( encodedFileName.charAt(i) == '%')
            {
               // String section before encoded character
               sectionOne = encodedFileName.substring(0, i);
               // String section after encoded character 
               sectionTwo = encodedFileName.substring(i+THREE, encodedFileName.length());
               
               // Obtain character code from string
               charCode = encodedFileName.substring(i + 1, i + THREE);
               charCode = charCode.toLowerCase();           
              
               try
               {
                  // Decode the character to its ASCII string equivalent
                  intCode = Integer.parseInt(charCode, SIXTEEN);       
                  decodedCharacter = (char)intCode;
                  decodedString = new Character( decodedCharacter ).toString();
               }
               catch(NumberFormatException nfe)
               {                  
                  return null;
               }               
               
               // Reassemble the filename with the decoded character
               encodedFileName = sectionOne + decodedString + sectionTwo;        
                      
               // Check for further encoded characters
               nameLength = nameLength - TWO;              
            }
         }
      }
      try
      {
        // Encode the newly decoded string in its given encoding 
         byte[] byteString = encodedFileName.getBytes(encoding);
         encodedFileName = new String(byteString, encoding);
         decodedFileName = encodedFileName;
         
         return decodedFileName;
         
      }
      catch(UnsupportedEncodingException uee)
      {
         return null;
      }
   }
   
   /**
    * This method will apply the directory escape sequence '../' and remove
    * the corresponding directories from the URI value 
    * 
    * @param iURIValue is a String containing the URI value to processed
    * 
    * @return a String holding the escaped URI value
    */
   public String escapeDirectories( String iURIValue )
   {
      mBeyondRoot = 0;     // The number of directories escaped above root
      
      List<String> directories = new LinkedList<String>();
      
      String dirList[] = (iURIValue.replace(File.separatorChar, '/')).split("/");
      
      // Assemble a linked list of the directory structure      
      for ( int i = 0; i < dirList.length; i++ )
      {         
         directories.add(dirList[i]);
      }
      
      // Remove all '../' sequences and corresponding directories
      for ( int i = 0; i < directories.size(); i++ )
      {  
         if ( directories.get(i).toString().equals("..") )
         {
            directories.remove(i);
            if ( i > 0 )
            {
               directories.remove(i-1);
               i = i - 2;
            }
            else
            {
               mBeyondRoot++;
               i = i - 1;
            }
         }
      }
      
      // If escapes exceeded root, return the unescaped value
      if ( mBeyondRoot > 0 )
      {
         return iURIValue;
      }
      
      Iterator<String> newDirectories = directories.iterator();
            
      String escapedURIValue = "";
      String tempDirectory = "";
      
      int i = 0;
      
      while ( newDirectories.hasNext() )
      {
         tempDirectory = newDirectories.next().toString();
         
         if ( i > 0 )
         {
            escapedURIValue = escapedURIValue + "/";
         }
         
         escapedURIValue = escapedURIValue + tempDirectory;
         
         i++;
                     
      }
           
      return escapedURIValue;
   }


   /**
    * This method returns the number of folders by which the root was exceeded
    * Any number higher than zero represents an error
    * 
    * @return the number of directories by which the root was exceeded
    */
   public int wasRootExceeded()
   {
      return mBeyondRoot;
   }
   
   /**
    * This method will determine whether the given string is a URL
    * 
    * @param iFileName is String containing a possible URL
    * @return a boolean indicating whether the String is a URL
    */
   public static boolean isURL(String iFileName)
   {
      if ( iFileName != null )
      {
         if( (   (iFileName.length() > FOUR) && (iFileName.substring(0, FIVE).equals("http:")) ) || 
               ( (iFileName.length() > FIVE) && (iFileName.substring(0, SIX).equals("https:")) ) ||
               ( (iFileName.length() > THREE) && (iFileName.substring(0, FOUR).equals("ftp:")) ) || 
               ( (iFileName.length() > FOUR) && (iFileName.substring(0, FIVE).equals("ftps:")) ) )
         {
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }
   
   /**
    * This method will determine if any special characters exist in the filename
    * These characters however, may cause parser issues if left un-encoded
    * 
    * @param iURI The String containing the value to check
    * @return A boolean indicating whether or not special characters were detected
    */
   public static boolean containsSpecialCharacters( String iURI )
   {
      if ( ( iURI.indexOf("{") != -1 ) ||
           ( iURI.indexOf("}") != -1 ) ||
           ( iURI.indexOf("^") != -1 ) ||
           ( iURI.indexOf("%") != -1 ) ||
           ( iURI.indexOf("&") != -1 ) ||
           ( iURI.indexOf("`") != -1 ) ||
           ( iURI.indexOf("#") != -1 ) )
      {
         return true;
      }
      else
      {
         return false;
      }
   }
   
   /**
    * This method will escape special characters which windows allows in filenames.
    * These characters however, may cause parser issues if left un-encoded
    * 
    * @param iURI The String containing the value to escape
    * @return The escaped URI value
    */
   public static String escapeSpecialCharacters( String iURI )
   {
      String newURI = iURI.replaceAll("%", "%25");
      newURI = newURI.replaceAll("[{]", "%7B");
      newURI = newURI.replaceAll("[}]", "%7D");
      newURI = newURI.replaceAll("\\^", "%5E");
      newURI = newURI.replaceAll("&", "%26");
      newURI = newURI.replaceAll("`", "%60");            
      newURI = newURI.replaceAll("#", "%23");
      
      return newURI;
   }
   
   /**
    * This method will process the whitespace of the given String
    *  - Remove all leading and trailing whitespace
    *  - Decoded all encoded whitespace
    *  - Collapse all middle whitespace
    * 
    * @param iVal - the value to be processed
    * @return the processed value, null if input parameter was null
    */
   public static String processWhitespace( String iVal )
   {
      if ( iVal == null )
      {
         return iVal;
      }
      
      // Decode any encoded spaces
      String processedString = iVal.replaceAll("%20", " ");
   
      // Remove leading and trailing whitespace
      processedString = processedString.trim();
      
      // Collapse any middle whitespace
      while ( processedString.indexOf("  ") != -1 )
      {
         processedString = processedString.replaceAll("  ", " ");
      }
      
      return processedString;
   }
   
   /**
    * This method will determine if the given file has a valid file extension
    * 
    * @param iVal - The file path whose extension is to be checked
    * @return - boolean indicating whether or not the file has a valid extension
    */
   public static boolean isValidExtension( String iVal )
   {
      // Ensure all separators are the same direction
      String newVal = iVal.replaceAll("\\\\", "/"); 
      
      // Remove the path if it exists
      String fileName;      
      if ( newVal.indexOf("/") != -1 )
      {
         fileName = newVal.split("/")[newVal.split("/").length-1];
      }
      else
      {
         fileName = newVal;
      }
      
      // Get the extension
      String ext;
      if ( fileName.indexOf(".") == -1 )
      {
         ext = "";
      }
      else
      {
         ext = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
      }
      
      // Check the extension
      if ( ext.equals("") )
      {
         return false;         
      }
      else
      {
         return true;
      }
   }
}
