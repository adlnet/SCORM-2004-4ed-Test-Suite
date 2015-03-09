package org.adl.datamodels.datatypes;

import org.adl.datamodels.DMErrorCodes;
import org.adl.datamodels.DMTypeValidator;
import java.io.Serializable;

/**
 * 
 * 
 * <strong>Filename:</strong> VocabularyValidator.java<br><br>
 * 
 * <strong>Description:</strong><br>Provides support for the Vocab data 
 * type, as defined in the SCORM 2004 RTE Book<br><br>
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
public class VocabularyValidator extends DMTypeValidator implements Serializable
{

   /**
    * A array of vocabularies values
    */
   String [] mVocabList = null;

   /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
   
    Public Methods
   
   -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

   /**
    * Constructor required for vocabulary initialization.
    * 
    * @param iVocab The array of vocabulary strings to be used in 
    * initialization.
    */
   public VocabularyValidator(String [] iVocab) 
   { 
      mVocabList = iVocab;
   } 

   /**
    * Validates the provided string against a known format.
    * 
    * @param iValue The value being validated.
    * 
    * @return An abstract data model error code indicating the result of this
    *         operation.
    */
   public int validate(String iValue)
   {
      // Assume the value is not valid
      int valid = DMErrorCodes.TYPE_MISMATCH;
      
      // Check first if mVocablist is null
      if ( mVocabList != null )
      {
         for ( int i = 0; i < mVocabList.length; i++ )
         {
            String tmpVocab = mVocabList[i];

            // Check if tmpVocab is null
            if ( tmpVocab != null )
            {
               // Check to see if the element equals the input value
               if ( tmpVocab.equals(iValue) )
               {
                  valid = DMErrorCodes.NO_ERROR;
                  break;
               }
            }
         }
      }
      else
      {
         // A null value can never be valid
         valid = DMErrorCodes.UNKNOWN_EXCEPTION;
      }

      return valid;
   }

} // end VocabularyValidator
