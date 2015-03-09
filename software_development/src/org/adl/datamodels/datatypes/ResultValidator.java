package org.adl.datamodels.datatypes;

import org.adl.datamodels.DMTypeValidator;
import org.adl.datamodels.DMErrorCodes;

import java.io.Serializable;
import java.util.Vector;

/**
 * Provides support for Valid Vocabulary tokens and if the input is valid based
 * on a Vocablist, as defined in the SCORM 2004 RTE Book<br><br>
 * 
 * <strong>Filename:</strong> ResultValidator.java<br><br>
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
public class ResultValidator extends DMTypeValidator implements Serializable
{

   /**
    * Describes the set of valid vocabulary tokens
    */
   private String [] mVocabList = null;


   /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
   
    Constructors
   
   -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

   /**
    * Default constructor required for serialization.
    */
   public ResultValidator() 
   {
     // The default constructor does not define any explicit functionality   
   }


   /**
    * Constructor required for vocabulary initialization.
    * @param iVocab An array of vocabulary string values to used for 
    * initialization
    */
   public ResultValidator(String [] iVocab) 
   { 
      mVocabList = iVocab;
   } 


   /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
   
    Public Methods
   
   -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

   /**
    * Compares two valid data model elements for equality.
    * 
    * @param iFirst The first value being compared.
    * @param iSecond The second value being compared.
    * 
    * @return Returns <code>true</code> if the two values are equal, otherwise
    *         <code>false</code>.
    */
   public boolean compare(String iFirst, String iSecond)
   {
      boolean equal = true;
      boolean done = false;

      double val1 = Double.NaN;
      double val2 = Double.NaN;

      try
      {
         // Try to make both values into reals
         val1 = Double.parseDouble(iFirst);
         val2 = Double.parseDouble(iSecond);
      }
      catch ( NumberFormatException nfe )
      {
         // At least one of these must be a strings... Compare
         equal = iFirst.equals(iSecond);
         done = true;
      }

      if ( !done )
      {
         // Only allow 7 signifigant digits -- truncate after that...
         val1 = Math.floor(val1 * 1000000.0) / 1000000.0;
         val2 = Math.floor(val2 * 1000000.0) / 1000000.0;

         equal = Double.compare(val1, val2) == 0;
      }

      return equal;
   }

   /**
    * Compares two valid data model elements for equality. If that returns false it 
    * then calls compare(String, String) to check for matching real values.
    * and returns that final result.
    * 
    * @param iFirst  The first value being compared.
    * 
    * @param iSecond The second value being compared.
    * 
    * @param iDelimiters
    *                The common set of delimiters associated with the
    *                values being compared.
    * 
    * @return Returns <code>true</code> if the two values are equal, otherwise
    *         <code>false</code>.
    */
   public boolean compare(String iFirst, String iSecond, Vector iDelimiters)
   {
      boolean equal = super.compare(iFirst, iSecond, iDelimiters);
      if(!equal)
      {
         equal = compare(iFirst, iSecond);
      }      
      return equal;
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

      boolean done = false;

      // Check to see if constructor is null
      if ( mVocabList == null || iValue == null )
      {
         valid = DMErrorCodes.UNKNOWN_EXCEPTION;
         done = true;        
      }

      // See if this value is in the provided vocabulary
      if ( !done )
      {
         for ( int i = 0; i < mVocabList.length; i++ )
         {
            String tmpVocab = mVocabList[i];

            // Check to see if this element equals the input value
            if ( tmpVocab.equals(iValue) )
            {
               valid = DMErrorCodes.NO_ERROR;
               done = true;

               // done
               break;
            }
         }

         // There is no matching vocabulary item, see if the value is a real
         if ( !done )
         {
            try
            {
               // TODO:  The value variable is never read - only used to hold a return.  Should we remove the declaration?
               double value = Double.parseDouble(iValue);

               // The value is a valid real number
               valid = DMErrorCodes.NO_ERROR;
            }
            catch ( NumberFormatException nfe )
            {
               // The value is invalid
               valid = DMErrorCodes.TYPE_MISMATCH;
            }
         }
      }

      return valid;
   }

} // end ResultValidator

