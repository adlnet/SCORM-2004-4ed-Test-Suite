package org.adl.util.debug;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * <strong>Filename: </strong> <br>
 * ADLSimpleFormatter.java <br>
 * <br>
 * <strong>Description: </strong> <br>
 * A <code>ADLSimpleFormatter</code> extends Java's SimpleFormatter class and
 * overrides that class's format function. This is so we can modify the messages
 * that are output using Java's logging output messages. Specifically, we do not
 * want the date/timestamp written on each and every message written to the
 * Console. <br>
 * <br>
 * 
 * @author ADL Technical Team <br>
 */
public class ADLSimpleFormatter extends SimpleFormatter
{
   /**
    * A line separator used to separate messages sent to the log.
    */
   private String mLineSeparator = (String)java.security.AccessController
      .doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

   /**
    * Overrides SimpleFormatter format function. Writes the output without
    * displaying the date/timestamp.
    * 
    * @param iRecord The log record that needs formatted.
    * @return A string formatted for a logging message
    */
   public synchronized String format(LogRecord iRecord)
   {
      StringBuffer sb = new StringBuffer();

      if( iRecord.getSourceClassName() != null )
      {
         sb.append(iRecord.getSourceClassName());
      }
      else
      {
         sb.append(iRecord.getLoggerName());
      }
      if( iRecord.getSourceMethodName() != null )
      {
         sb.append(" ");
         sb.append(iRecord.getSourceMethodName());
      }
      sb.append(" ");

      String message = formatMessage(iRecord);
      sb.append(iRecord.getLevel().getLocalizedName());
      sb.append(": ");
      sb.append(message);
      sb.append(mLineSeparator);

      return sb.toString();
   }
}