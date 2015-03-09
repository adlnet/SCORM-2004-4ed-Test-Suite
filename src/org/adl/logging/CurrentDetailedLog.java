package org.adl.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.logging.Logger;

import org.adl.util.LogMessage;
import org.adl.util.MessageType;

/**
 * This class is used to view the data in the detailed log of the current LMS
 * testcase
 */ 
public final class CurrentDetailedLog
{
   /**
    * Object used to set up the file object tags, and to format the messages
    */ 
   private static final XMLLogMessageProcessor PROC = new XMLLogMessageProcessor("Detailed");

   /**
    * Logger object used for debug logging
    */
   private static final Logger LOGGER = Logger.getLogger("org.adl.util.debug.testsuite");

   /**
    * Singleton.
    */
   private CurrentDetailedLog()
   {
      
   }
   
   /**
    * creates the file, writes to it, and returns the URI to the calling class
    * @param folder String value of the log directory
    * @param msgs LinkedList of LogMessage objects to be written to file
    * @return String value of the URI of the currentLog.xml file
    */
   public static String viewLog(final String folder, final List<LogMessage> msgs)
   {
       LOGGER.entering("CurrentDetailedLog","viewLog()");
       LOGGER.finer("size of msgs collection is: " + msgs.size());

      // create the base path of the folder that will hold the logs + "currentLog.xml"
      final String currentLog = folder + "currentLog.xml";

      LOGGER.info("value of currentLog is: " + currentLog);
      // create the log file
      final File log = new File(currentLog);

      try
      {
         final OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(log), "UTF-16");
         
         // write the header info
         out.write(PROC.prepareFile());
         
         // write the messages in the linked list
         for(int i=0;i<msgs.size();i++)
         {
            // we don't format a newlog message
            if((msgs.get(i)).getMessageType() != MessageType.NEWLOG)
            {
               out.write(PROC.formatMessage(msgs.get(i)));
            }
         }
         
         // write the closing tags
         out.write(PROC.endFile());
         out.flush();
         out.close();
      }
      catch(IOException ioe)
      {
         LOGGER.severe("IOException occurred in CurrentDetailedLog()");
      }

      LOGGER.exiting("CurrentDetailedLog","viewLog()");

      // return the URI of the currentLog file
      return log.getPath();
   }
}
