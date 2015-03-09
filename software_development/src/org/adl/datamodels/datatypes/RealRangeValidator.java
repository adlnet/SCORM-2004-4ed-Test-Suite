package org.adl.datamodels.datatypes;

import org.adl.datamodels.DMTypeValidator;
import org.adl.datamodels.DMErrorCodes;

import java.io.Serializable;
import java.util.Vector;

/**
 * Provides support for the Real data type within a specified range, 
 * as defined in the SCORM 2004 RTE Book<br><br>
 * 
 * <strong>Filename:</strong> RealRangeValidator.java<br><br>
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
public class RealRangeValidator extends DMTypeValidator implements Serializable
{
   /**
    * Describes the maximum value allowed in the range.
    */
   private Double mMax = null;

   /** 
    * Describes the minimum value allowed in the range.
    */
   private Double mMin = null;


   /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
   
    Constructors
   
   -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

   /**
    * Default constructor required for serialization.
    */
   public RealRangeValidator() 
   {
      // The default constructor does not define any explicity functionality   
   }


   /**
    * Defines an real-valued range.
    * 
    * @param iMin  Defines the lower bound for this range.
    * 
    * @param iMax  Defines the upper bound for this range.
    */
   public RealRangeValidator(Double iMin, Double iMax) 
   { 
      mMax = iMax;
      mMin = iMin;
   }


   /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
   
    Public Methods
   
   -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

   /**
    * Compares two valid data model elements for equality.
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

      boolean equal = true;
      boolean done = false;

      double val1 = Double.NaN;
      double val2 = Double.NaN;

      try
      {
         val1 = Double.parseDouble(iFirst);
         val2 = Double.parseDouble(iSecond);
      }
      catch ( NumberFormatException nfe )
      {
         equal = false;
         done = true;
      }

      if ( !done )
      {
         val1 = Math.floor(val1 * 1000000.0) / 1000000.0;
         val2 = Math.floor(val2 * 1000000.0) / 1000000.0;

         equal = Double.compare(val1, val2) == 0;

         // If the floor at 7 digits didn't work, try rounding
         if ( !equal )
         {
            val1 = Double.parseDouble(iFirst);
            val2 = Double.parseDouble(iSecond);

            val1 = Math.round(val1 * 1000000.0) / 1000000.0;
            val2 = Math.round(val2 * 1000000.0) / 1000000.0;
   
            equal = Double.compare(val1, val2) == 0;
         }
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
      // Assume the value is valid
      int valid = DMErrorCodes.NO_ERROR;

      boolean done = false;

      if ( iValue == null )
      {
         // A null value can never be valid
         return DMErrorCodes.UNKNOWN_EXCEPTION;
      }

      try
      {
         double value = Double.parseDouble(iValue);

         if ( mMin == null && mMax == null )
         {
            done = true;
         }

         // Just testing to see if the value is a real number
         if ( done != true )
         {
            // Test with a min and no max; this defaults the max to infinity
            if ( mMin != null && mMax == null )
            {
               if ( value < mMin.doubleValue() )
               {
                  valid = DMErrorCodes.VALUE_OUT_OF_RANGE;
               }
            }
            // Test with a max and not min; this defaults the min to 0
            else if ( mMin == null && mMax != null )
            {
               if ( value < 0 || value > mMax.doubleValue() )
               {
                  valid = DMErrorCodes.VALUE_OUT_OF_RANGE;
               }
            }
            // Test with a max and a min
            else if ( mMin != null && mMax != null )
            {
               if ( value < mMin.doubleValue() || 
                    value > mMax.doubleValue() )
               {
                  valid = DMErrorCodes.VALUE_OUT_OF_RANGE;
               }
            }
         }
      }
      catch ( NumberFormatException nfe )
      {
         valid = DMErrorCodes.TYPE_MISMATCH;
      }

      return valid;
   }

} // end RealRangeValidator
