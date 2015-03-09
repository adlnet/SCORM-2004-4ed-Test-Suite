package org.adl.util.decode;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

/**
 *
 * <strong>Filename:</strong><br>decodeHandler.java<br><br>
 *
 * <strong>Description:</strong><br>
 * A <code>decodeHandler</code> provides the ability to decode Unicode encoded
 * characters from the form %xy into their ASCII equivalents.<br><br>
 * 
 * @author ADL Technical Team
 */
public class decodeHandler
{
   /**
    * Logger object used for debug logging.
    */
   private Logger mLogger;
   
   /**
   * The filename to be decoded.
   */
   private String mEncodedFileName;
   
   /**
    * The decoded filename.
    */
    private String mDecodedFileName;

   /**
   * The encoding in which the filename should be decoded.
   */
   private String mEncoding;
   
   /**
    * Constructor for the <code>decodeHandler</code> class.
    *
    * @param iEncodedFileName FileName to be decoded.
    * @param iEncoding The encoding in which the filename should be decoded.
    */
   public decodeHandler(String iEncodedFileName, String iEncoding)
   {
      setFileName( iEncodedFileName );
      setEncoding( iEncoding );
   }
   
   /**
    * Default Constructor
    */
   public decodeHandler()
   {
      setFileName("");
      setEncoding("");
   }

   /**
    * Set the filename of the file to be decoded.
    *
    * @param iEncodedFileName FileName to be decoded.
    */
   private void setFileName(String iEncodedFileName)
   {
      try
      {
         mEncodedFileName = iEncodedFileName;
      }
      catch ( NullPointerException npe )
      {
         npe.printStackTrace();
      }     
   }

   /**
    * Set the encoding in which the filename should be decoded.
    *
    * @param iFileNameEncoding The encoding of the filename.
    *
    */
   private void setEncoding( String iFileNameEncoding )
   {
      try
      {
         mEncoding = iFileNameEncoding;
      }
      catch ( NullPointerException npe )
      {
         npe.printStackTrace();
      }
   }

   /**
    * Returns the decoded filename
    *
    * @return String The decoded filename containing decoded ASCII characters.
    */
   public String getDecodedFileName()
   {
      return mDecodedFileName;
   }
   
   /**
    * This method will take a given fileName decode using the specified encoding
    * 
    * @param iFileName - file name to be decoded
    * @param iEncoding - encoding in which the file name is encoded
    * @return - the decoded file name
    */
   public String decode(String iFileName, String iEncoding)
   {
      setFileName(iFileName);
      setEncoding(iEncoding);
      decodeName();
      return getDecodedFileName();      
   }
   
    /**
     *     
     * Decodes the filename to its ASCII equivalent
     */
	public void decodeName()
  	{	   
	   int index = -1;	   
	   String charCode = "";
	   int intCode;
	   char decodedCharacter;
	   String sectionOne = "";
	   String sectionTwo = "";
	   String decodedString = "";	
	   
	   String encodedFileName = new String ( mEncodedFileName );
	   String encoding = new String ( mEncoding );
	   
	   // Determine if an encoded character exists
	   index = mEncodedFileName.indexOf("%");	   
	   
	   int nameLength = encodedFileName.length();
	   
	   if ( index > -1)
	   {		   
		   // An encoded character is containing within the filename
		   for (int i = 0; i < nameLength; i++)
		   {
			   if ( encodedFileName.charAt(i) == '%')
			   {
				   // String section before encoded character
				   sectionOne = encodedFileName.substring(0, i);
				   // String section after encoded character 
				   sectionTwo = encodedFileName.substring(i+3, encodedFileName.length());
				   
				   // Obtain character code from string
				   charCode = encodedFileName.substring(i + 1, i + 3);
				   charCode = charCode.toLowerCase();			   
				  
				   // Decode the character to its ASCII string equivalent
				   intCode = Integer.parseInt(charCode, 16);		   
				   decodedCharacter = (char)intCode;
				   decodedString = new Character( decodedCharacter ).toString();
				   
				   // Reassemble the filename with the decoded character
				   encodedFileName = sectionOne + decodedString + sectionTwo;		   
				   		 
				   // Check for further encoded characters
				   nameLength = nameLength - 2;				   
			   }
		   }
	   }
	   try
	   {
		  // Encode the newly decoded string in its given encoding 
	      byte[] byteString = (new String(encodedFileName)).getBytes(encoding);
	      encodedFileName = new String(byteString, encoding);
	      mDecodedFileName = new String( encodedFileName );
	      
	   }
	   catch(UnsupportedEncodingException uee)
	   {
		   mLogger.severe("UnsupportedEncodingException thrown while " + "decoding the file path.");
	          uee.printStackTrace();
	   }
	} 		
	
	/**
    * This method will process the whitespace of the given String
    *  - Remove all leading and trailing whitespace
    *  - Decoded all encoded whitespace
    *  - Collapse all middle whitespace
    * 
    * @param iVal - the value to be processed
    * @return the processed value, null if input parameter is null
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
    * This method encodes the ids of objective values.  The
    * ids must be encoded to protect the case of the value.
    * The following scheme was used for encoding:
    * 
    * All lowercase letters preceded by 'L'
    * All uppercase letters preceded by 'U'
    * All other characters preceded by 'O'
    * 
    * @param iID - The id to be encoded
    * @return - The encoded objective id
    */
   public static String encodeObjectiveID( String iID )
   {
      String escapedID = "";
      for ( int i = 0; i < iID.length(); i++ )
      {
         char val = iID.charAt(i);
         
         // Uppercase
         if ( Character.isUpperCase(val) )
         {
            escapedID = escapedID + "U";
         }
         // Lowercase
         else if ( Character.isLowerCase(val) )
         {
            escapedID = escapedID + "L";
         }
         // Other
         else
         {
            escapedID = escapedID + "O";
         }         
      }
      
      return escapedID + "~" + iID;
   }
   
   /**
    * This method decodes the ids of objective values.  The
    * ids must be decoded to obtain the correct case of the value.
    * The following scheme was used for decoding:
    * 
    * All lowercase letters preceded by 'L'
    * All uppercase letters preceded by 'U'
    * All other characters preceded by 'O'
    * 
    * @param iID - The id to be decoded
    * @return - The decoded objective id, empty string if value was stored incorrectly
    */
   public static String decodeObjectiveID( String iID )
   {
      String code = iID.substring(0, iID.indexOf("~"));
      String val = iID.substring(iID.indexOf("~")+1, iID.length());
      String decodedVal = "";
      
      if ( code.length() != val.length())
      {
         return "";
      }
      
      for ( int i = 0; i < code.length(); i++ )
      {
         char codeChar = code.charAt(i);
         String valChar = val.substring(i,i+1);
         String temp;
         
         // Uppercase
         if ( codeChar == 'U' )
         {
            decodedVal = decodedVal + valChar.toUpperCase();
         }
         // Lowercase
         else if ( codeChar == 'L' )
         {           
            decodedVal = decodedVal + valChar.toLowerCase();
         }
         // Other
         else
         {
            decodedVal = decodedVal + valChar;
         }
      }
      return decodedVal;
   }
}