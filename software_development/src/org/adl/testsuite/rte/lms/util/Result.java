package org.adl.testsuite.rte.lms.util;

import java.io.Serializable;

/**
 * General Result object. This object contains the result of a certian dm request.
 * @author ADL Technical Team
 *
 */
public class Result implements Serializable
{
   /**
    * A value returned or expected result.
    */
   String mValue;
   
   /**
    * A error code returned or expected result.
    */
   String mErrorCode;
   
   /**
    * The type of interaction.
    */
   String mType;
   
   /**
    * Holds the index of where we found the objective we were looking for.
    */
   int mIndex = 0;
   
   /**
    * The default constructor.
    */
   public Result()
   {
      mValue = new String();
      mErrorCode = new String();
   }
   
   /**
    * Overloaded constructor
    * 
    * @param iValue string representing a result value.
    * @param iErrorCode string representing a result error code.
    */
   public Result(String iValue, String iErrorCode)
   {
      this.mValue = iValue;
      this.mErrorCode = iErrorCode;
   }

   /**
    * Overloaded constructor
    * 
    * @param iValue string representing a result value.
    * @param iErrorCode string representing a result error code.
    * @param iType string representing the type of interaction.
    */
   public Result(String iValue, String iErrorCode, String iType)
   {
      this.mValue = iValue;
      this.mErrorCode = iErrorCode;
      this.mType = iType;
   }
   
   /**
    * Overloaded constructor used when we had to find the index from the LMS.
    * 
    * @param iValue string representing a result value.
    * @param iErrorCode string representing a result error code.
    * @param index string representing the index where this result came from - 
    * such as cmi.objectives.<b>0</b>.success_status, the "0" would be the index. 
    */
   public Result(String iValue, String iErrorCode, int index)
   {
      mValue = iValue;
      mErrorCode = iErrorCode;
      mIndex = index;
   }
   
   /**
    * @return String representing a result value.
    */
   public String getValue()
   {
      return mValue;
   }
   
   /**
    * @return String representing a error code value.
    */
   public String getErrorCode()
   {
      return mErrorCode;
   }
   
   /**
    * @return String representing the type of interaction.
    */
   public String getType()
   {
      return mType;
   }
   
   /**
    * Returns the index of the objective we were looking for
    * 
    * @return index
    */
   public int getIndex()
   {
      return mIndex;
   }

}
