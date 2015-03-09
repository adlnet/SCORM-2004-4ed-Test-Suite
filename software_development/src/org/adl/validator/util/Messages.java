package org.adl.validator.util;

import org.adl.util.Resources;

/**
 * <strong>Filename: </strong>Messages.java<br><br>
 *
 * <strong>Description: </strong> <br>
 * The <code>Messages</code> creates a resource bundle that contains all of the
 * messages for the logging of the ADL CP Validator.    <br>
 *
 * @author ADL Technical Team<br><br>
 */
public final class Messages
{
   /**
    * Default constructor.  No explicitly defined functionality for this 
    * constructor.
    */
   private Messages()
   {
      // No explicitly defined functionality
   }

   public static String getString(String key, Object... args)
   {
      return Resources.getResources(Messages.class).getString(key, args);
   }
}
