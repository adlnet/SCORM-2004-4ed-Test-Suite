package org.adl.datamodels;

/**
 * Encapsulates one delimiter found in a dot notation bound string defined by a
 * value token (<code>DMToken</code>).
 * 
 * <strong>Filename:</strong> RequestDelimiter.java<br><br>
 * 
 * <strong>Description:</strong><br><br>
 * 
 * <strong>Design Issues:</strong><br><br>
 * 
 * <strong>Implementation Issues:</strong><br><br>
 * 
 * <strong>Known Problems:</strong><br><br>
 * 
 * <strong>Side Effects:</strong><br><br>
 * 
 * <strong>References:</strong><br>
 * <ul>
 *     <li>SCORM 2004
 * </ul>
 * 
 * @author ADL Technical Team
 */
public class RequestDelimiter
{

   /**
    * Describes the name of this delimiter.
    */
   private String mName = null;


   /**
    * Describes the value of this delimiter.
    */
   private String mValue = null;

   /**
    * Creates one delimter from a dot-notation bound data model request.  This
    * method may throw the following Java <code>RuntimeExceptions</code>:
    * <ul>
    *   <li><code>NullPointerException</code>: If the data is omitted</li>
    *   <li><code>IllegalArgumentException</code>:  If the element name or value
    *       is improperly formatted</li>
    * </ul>
    * 
    * @param iName  The name of the delimiter
    * 
    * @param iValue The value of the delimiter
    * 
    */
   public RequestDelimiter(String iName, String iValue) 
   {
      // Make sure none of the parameters are null
      if ( iName == null || iValue == null )
      {
         throw new NullPointerException("Delimiter not specified");
      }

      // Make sure the name parameters has non-zero length
      if ( iName.length() == 0 )
      {
         throw new IllegalArgumentException("Delimiter name is blank");
      }

      mName = iName;
      mValue = iValue;
   }


   /**
    * Describes the name of this delimiter.
    * 
    * @return The name of this delimter.
    */
   public String getName()
   {
      return mName;
   }


   /**
    * Describes the value of this delimter.
    * 
    * @return The value of this delimiter.
    */
   public String getValue()
   {
      return mValue;
   }


   /**
    * Provides the dot-notation binding for this delimiter.
    * 
    * @return The dot-notation <code>String</code> corresponding to this
    *         delimiter.
    */
   public String showDotNotation()
   {
      String dot = "";

      dot = "{" + mName + "=" + mValue + "}";

      return dot;
   }

} // end RequestDelimiter
