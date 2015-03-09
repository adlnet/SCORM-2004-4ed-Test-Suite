package org.adl.datamodels.ieee;

import org.adl.datamodels.RequestToken;
import org.adl.datamodels.DMRequest;
import org.adl.datamodels.DMProcessingInfo;
import org.adl.datamodels.DMErrorCodes;
import org.adl.datamodels.DMElement;

/**
 * <br><br>
 * 
 * <strong>Filename:</strong> Version.java<br><br>
 * 
 * <strong>Description:</strong><br>
 * Encapsulates the _version keyword datamodel element defined in SCORM 2004.
 * <br><br> 
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
public class Version extends DMElement
{
   /**
    * Describes the dot-notation binding string for this data model element.
    */
   private String mBinding = "_version";


   /**
    * Describes the version of the data model
    */
   private String mVersion = null;


   /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
   
    Constructors
   
   -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

   /**
    * Constructs a <code>_version</code> keyword data model element describing 
    * the version of data model.
    * 
    * @param iVersion The version indicator of the data model
    */
   public Version(String iVersion)
   {
      mVersion = iVersion;
   }


   /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
   
    Public Methods
   
   -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

   /**
    * Compares the provided value to the value stored in this data model
    * element.
    * 
    * @param iValue A token (<code>RequestToken</code>) object that provides the
    *               value to be compared against the exiting value; this request
    *               may include a set of delimiters.
    * 
    * @param iValidate Describes if the value being compared should be
    *                  validated first.
    * 
    * @return An abstract data model error code indicating the result of this
    *         operation.
    */
   public int equals(RequestToken iValue, boolean iValidate)
   {

      int result = DMErrorCodes.COMPARE_NOTHING;

      if ( iValue != null )
      {
         // Make sure there are no delimiters defined
         if ( iValue.getDelimiterCount() == 0 )
         {
            if ( iValue.getValue().equals(mVersion) )
            {
               result = DMErrorCodes.COMPARE_EQUAL;  
            }
            else
            {
               result = DMErrorCodes.COMPARE_NOTEQUAL;  
            }
         }
         else
         {
            result = DMErrorCodes.COMPARE_NOTEQUAL;  
         }
      }

      return result;
   }

   /**
    * Describes this data model element's binding string.
    * 
    * @return This data model element's binding string.  Note: The <code>
    *         String</code> returned only applies in the context of its
    *         containing data model or parent data model element.
    */
   public String getDMElementBindingString()
   {
      return mBinding;
   }

   /**
    * Attempt to get the value of this data model element, which may include
    * default delimiters.
    * 
    * @param iArguments  Describes the aruguments for this getValue() call.
    * 
    * @param iAdmin      Describes if this request is an administrative action.
    * 
    * @param iDelimiters Indicates if the data model element's default
    *                    delimiters should be included in the return string.
    * 
    * @param oInfo       Provides the value of this data model element.
    *                    <b>Note: The caller of this function must provide an
    *                    initialized (new) <code>DMProcessingInfo</code> to
    *                    hold the return value.</b>
    * 
    * @return An abstract data model error code indicating the result of this
    *         operation.
    */
   public int getValue(RequestToken iArguments,
                       boolean iAdmin,
                       boolean iDelimiters,
                       DMProcessingInfo oInfo)
   {

      oInfo.mValue = mVersion;

      // Getting a keyword data model element never fails.
      return DMErrorCodes.NO_ERROR;
   }

   /**
    * Processes a data model request on this data model element.  This method
    * will enforce data model element depedencies and keyword application.
    * 
    * @param ioRequest Provides the dot-notation request being applied to this
    *                  data model element.  The <code>DMRequest</code> will be
    *                  updated to account for processing against this data
    *                  model element.
    * 
    * @param oInfo     Provides the value of this data model element.
    *                  <b>Note: The caller of this function must provide an
    *                  initialized (new) <code>DMProcessingInfo</code> to
    *                  hold the return value.</b>
    * 
    * @return An abstract data model error code indicating the result of this
    *         operation.
    */
   public int processRequest(DMRequest ioRequest, DMProcessingInfo oInfo)
   {
      // Assume no processing errors
      int error = DMErrorCodes.NO_ERROR;

      // If there are more tokens to be processed, we don't have that data
      // model element.
      if ( ioRequest.hasMoreTokens() )
      {
         error = DMErrorCodes.UNDEFINED_ELEMENT;
      }
      else
      {
         // If we here, something is wrong with the request
         error = DMErrorCodes.UNKNOWN_EXCEPTION;
      }

      return error;
   }

   /**
    * Attempt to set the value of this data model element to the value 
    * indicated by the dot-notation token.
    * 
    * @param iValue A token (<code>RequestToken</code>) object that provides the
    *               value to be set and may include a set of delimiters.
    * 
    * @param iAdmin Indicates if this operation is administrative or not.  If
    *               The operation is administrative, read/write and data type
    *               characteristics of the data model element should be
    *               ignored.
    * 
    * @return An abstract data model error code indicating the result of this
    *         operation.
    */
   public int setValue(RequestToken iValue, boolean iAdmin)
   {
      // Never are allowed to set a _version keyword element, even as an
      // admin action
      return DMErrorCodes.SET_KEYWORD;
   }

   /**
    * Validates a dot-notation token against this data model's defined data
    * type.
    * 
    * @param iValue A token (<code>RequestToken</code>) object that provides 
    *               the value to be checked, possibily including a set of
    *               delimiters.
    * 
    * @return An abstract data model error code indicating the result of this
    *         operation.     
    */
   public int validate(RequestToken iValue)
   {
      // Never have to validate _version, because it is a read-only element
      return DMErrorCodes.NO_ERROR; 
   }

}  // end Version
