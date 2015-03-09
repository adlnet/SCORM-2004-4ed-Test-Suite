package org.adl.testsuite.rte.lms.comm;

import java.io.Serializable;
import java.util.Date;

import org.adl.testsuite.rte.lms.util.Command;
import org.adl.testsuite.rte.lms.util.Results;

/**
 * holds a timestamp, a function name, and a value
 */
public class LMSMessageObject implements Serializable
{
   /**
    * removes warning
    */
   private static final long serialVersionUID = 2;
   
   /**
    * timestamp for the creation of the message object
    */
   private long mTimestamp;
   
   /**
    * the function
    */
   private String mFunction;
   
   /**
    * the value
    */
   private Object mValue;
   
   /**
    * Constructor 
    * @param iFunction function name
    * @param iValue value
    */
   public LMSMessageObject(final String iFunction, final Object iValue)
   {
      mValue = iValue;
      mFunction = iFunction;
      mTimestamp = new Date().getTime();
   }
   
   /**
    * Constructor for boolean value
    * @param iFunction function
    * @param iValue boolean value
    */
   public LMSMessageObject(final String iFunction, final boolean iValue)
   {
      this(iFunction, Boolean.toString(iValue));
   }
   
   /**
    * Constructor for value-less call
    * @param iFunction function
    */
   public LMSMessageObject(final String iFunction)
   {
      this(iFunction, "");
   }
   
   /**
    * Constructor for command
    * @param iCommand command
    */
   public LMSMessageObject(final Command iCommand)
   {
      this("", iCommand);
   }
   
   /**
    * Constructor for results
    * @param iResults results
    */
   public LMSMessageObject(final Results iResults)
   {
      this("", iResults);
   }
   
   /**
    * get the function
    * @return the function
    */
   public String getFunction()
   {
      return mFunction;
   }
   
   /**
    * get the timestamp
    * @return the timestamp
    */
   public long getTimestamp()
   {
      return mTimestamp;
   }
   
   /**
    * get the value
    * @return the value
    */
   public Object getValue()
   {
      return mValue;
   }
}
