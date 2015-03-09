package org.adl.validator.util.processor;

import java.util.StringTokenizer;

/**
 * <strong>Filename: </strong>ParameterHandler.java<br>
 *
 * <strong>Description: </strong>The <code>ParameterHandler</code> checks to 
 * make sure the parameter attribute's value (of the &lt;item&gt; element) correctly 
 * adheres to the requirements defined by the IMS Content Packaging 
 * Specification V1.1.4 and SCORM 2004.  
 *
 * @author ADL Technical Team
 */
public class ParameterHandler
{

   /**
    * Regular expression representing all unreserved values
    */
   private static String mUnreservedValues = "[\u0030-\u0039\u0061-\u007A\u0041-\u005A]"; 

   /**
    * Regular expression represending the following characters:
    *                       "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
    */
   private static String mMark = "[-_.!*'()]";

   /**
    * Regular expression representing how to encode reserved characters
    */
   private static String mEscaped = "[%[[0-9a-fA-F](2)]]";

   /**
    * Regular expression to be compared to for  valid URIs
    */
   private static String mPattern = "[" + mUnreservedValues + "|" + mMark + "|" + mEscaped +  "]*";

   /**
    * Default Constructor 
    */
   public ParameterHandler()
   {
   }

    /**
     * This method checks to make sure the parameter attribute's value (of the 
     * &lt;item&gt; element) correctly adheres to the requirements defined by the 
     * IMS Content Packaging Specification V1.1.4 and SCORM 2004.
     * 
     * The required syntax of the value shall be:
     * <ul> 
     *   <li>#&lt;parameter&gt;</li>
     *   <li>&lt;name&gt;=&lt;value&gt;(&&lt;name&gt;=&lt;value&gt;)*(#&lt;parameter&gt;)</li>
     *   <li>?&lt;name&gt;=&lt;value&gt;(&&lt;name&gt;=&lt;value&gt;)*(#&lt;parameter&gt;)</li>
     * </ul>
     * 
     * From RFC 2396:
     * 3.4. Query Component 
     * The query component is a string of information to be interpreted by the 
     * resource. 
     * 
     *       query         = \*uric 
     * 
     * Within a query component, the characters ";", "/", "?", ":", "@", "&", 
     * "=", "+", ",", and "$" are reserved.
     * 
     *      uric          = reserved | unreserved | escaped
     * 
     * reserved    = ";" | "/" | "?" | ":" | "@" | "&" | "=" | "+" | "$" | ","
     *    (reserved values must be escaped)
     * 
     * unreserved  = alphanum | mark 
     * 
     * alphanum = alpha | digit
     * alpha    = lowalpha | upalpha 
     * lowalpha = "a" | "b" | "c" | "d" | "e" | "f" | "g" | "h" | "i" | "j" | 
     *            "k" | "l" | "m" | "n" | "o" | "p" | "q" | "r" | "s" | "t" | 
     *            "u" | "v" | "w" | "x" | "y" | "z" 
     * upalpha  = "A" | "B" | "C" | "D" | "E" | "F" | "G" | "H" | "I" | "J" | 
     *            "K" | "L" | "M" | "N" | "O" | "P" | "Q" | "R" | "S" | "T" | 
     *            "U" | "V" | "W" | "X" | "Y" | "Z" 
     * digit    = "0" | "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9"
     * 
     * mark        = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")"
     * 
     * escaped     = "%" hex hex 
     * hex         = digit | "A" | "B" | "C" | "D" | "E" | "F" | "a" | "b" | 
     *               "c" | "d" | "e" | "f"
     * 
     * 
     *  @param iParameterString The parameter attribute's value 
     *  @return Returns <code>true</code> if the parameter attribute's value 
     *          is valid to the syntax described above, <code>false</code> 
     *          otherwise 
     */              
    public static boolean checkParameters(String iParameterString)
    {  
       if ( iParameterString == null )
       {
          return false;
       }
       
       boolean result = true;
       
       // First, ensure the value is not empty or all whitespace
       if ( iParameterString.trim().equals("") )
       {
          return false;
       }

       if ( iParameterString.startsWith("#") )
       {
          result = checkPoundSyntax(iParameterString.substring(1));
       }
       else if ( iParameterString.startsWith("?") )
       {
          result = checkQuestionMarkSyntax(iParameterString.substring(1));
       }
       else
       {


          // CHECK TO SEE if the string has a crosshatch in it.  If so, validate
          // that is is of the proper syntax

          int crossHatchIndex = -1;
          crossHatchIndex = iParameterString.lastIndexOf("#");
          String processedParams = iParameterString;
          
          if ( crossHatchIndex != -1) 
          {
             String crossHatchString = iParameterString.substring( crossHatchIndex );
              // we have found a crosshatch, remove this from original string
             processedParams = iParameterString.substring(0,crossHatchIndex);
              if ( !crossHatchString.equals("") ) 
              {
                 result = validateCrossHatch( crossHatchString ) && result;
              }
          }

          StringTokenizer tok = new StringTokenizer(processedParams,"&");

          if (tok.countTokens() == 1)
          {
             String tempToken = tok.nextToken();
             result = performEqualSplit(tempToken);

          }
          else
          {
             while (tok.hasMoreTokens())
             {
                String temp = tok.nextToken();
                if ( temp.indexOf('=') != -1 )
                {
                   result = performEqualSplit( temp ) && result;
                                   }
                else
                {
                    // must have ?<name>=<value>
                    result = false;
                    break;
                }
             }
          }
       }

       return result;
    }   

    /**
     * This method validates a parameter string in the form of #&lt;parameter&gt;
     * 
     * @param iParameterString the URI being validated
     * 
     * @return boolean result describing if the URI was of valid syntax (true),
     * false implies otherwise 
     */
    private static boolean checkPoundSyntax(String iParameterString)
    {
       return iParameterString.matches(mPattern);
    }

    /**
     * This method validates a parameter string in the form of
     * ?&lt;name&gt;=&lt;value&gt;
     * 
     * @param iParameterString the URI being validated
     * 
     * @return boolean result describing if the URI was of valid syntax (true),
     * false implies otherwise
     */
    private static boolean checkQuestionMarkSyntax(String iParameterString)
    {
       boolean result = true;
       
       // CHECK TO SEE if the string has a crosshatch in it.  If so, validate
       // that is is of the proper syntax

       int crossHatchIndex = -1;
       crossHatchIndex = iParameterString.lastIndexOf("#");
       String processedParams = iParameterString;
       
       if ( crossHatchIndex != -1) 
       {
          String crossHatchString = iParameterString.substring(crossHatchIndex);
           // we have found a crosshatch, remove this from original string
           processedParams = iParameterString.substring(0,crossHatchIndex);
           if ( !crossHatchString.equals("") ) 
           {
              result = validateCrossHatch( crossHatchString ) && result;
           }
       }

       // Check to see if there is multiple parameters separated by an '&'
       // symbol
       StringTokenizer tok = new StringTokenizer(processedParams,"&");

       if (tok.countTokens() == 1)
       {
          // There is only 1 set of tokens after the '&' token
          if ( processedParams.indexOf('=') == -1 )
          {
              // must have ?<name>=<value>
              result = false;
             //result = iParameterString.matches(mPattern);
          }
          else
          {
             result = performEqualSplit( processedParams ) && result;
          }
       }
       else
       {
          while (tok.hasMoreTokens())
          {
             // Get the first token
             String temp = tok.nextToken();

             // Split the token on the '=' sign, if there is one
             if ( temp.indexOf('=') != -1 )
             {
                 result = performEqualSplit(temp) && result;
             }
             else
             {
                // malformed query component - no '=' character found
                result = false;
                break;
             }
          }
       }
       return result;
    }

    /**
     *  This method performs a string split on the "=" 
     * 
     * @param iToken the string value to be split
     * 
     * @return result describing if the URI was of valid syntax (true),
     * false implies otherwise
     */
    private static boolean performEqualSplit( String iToken ) 
    {
       boolean result = true;

       String[] tempHolder = iToken.split("=");
       if ( tempHolder.length > 1 ) 
       {
          if ( tempHolder[0].equals("")) 
          {
             result = false;
          }
          else
          {
              result = (tempHolder[0]).matches(mPattern) && result;
          }
          if ( tempHolder[1].equals("")) 
          {
             result = false;
          }
          else
          {
             result = (tempHolder[1]).matches(mPattern) && result;       
          }
       }
       else
       {
          result = false;
       }

       return result;
    }

    /**
     * This method validates that if the ?&lt;name&gt;=&lt;value&gt; and 
     * &lt;name&gt;=&lt;value&gt; ends with #&lt;parameter&gt; that the sytax is
     * correct.
     * 
     * @param iCrossHatchString the value being checked for proper syntax
     * 
     * @return result describing if the proper syntax is used or not
     */
    private static boolean validateCrossHatch( String iCrossHatchString )
    {
       boolean result = true;

       if ( iCrossHatchString.startsWith("#") )
       {
          result = checkPoundSyntax( iCrossHatchString.substring(1));
       }
       else
       {
           // must start with #<parameter>
           result = false;
       }
       return result;
    }
    
    /**
     * This method will remove parameters from a given String
     * 
    * @param iParam - The String value possibly containing parameters
    * @return - A String with the parameters value removed
    */
   public static String removeParameters(String iParam)
    {
       String fileURI = "";
       
       if ( iParam == null )
       {
          return null;
       }
       
       if ( iParam.indexOf("?") != -1 )
       {
          fileURI = iParam.split("[?]")[0];
       }
       else if ( iParam.indexOf("#") != -1 )
       {
          fileURI = iParam.split("#")[0];
       }
       else
       {
          fileURI = iParam;
       }
       return fileURI;
    }
}