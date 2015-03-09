package org.adl.logging;

import org.adl.util.LogMessage;

/**
 * Interface defining the basic requests of a Log LogMessage Processor
 * @author ADL Technical Team
 */
public interface LogMessageProcessor
{
   /**
    * Returns the string "header" for the chosen output file format.
    * @return String of the default "heading" of the file.
    */
   String prepareFile();
   
   /**
    * Formats incoming message object based on rules of chosen output file 
    * format
    * @param ioM Incoming message object to be formatted
    * @return String of the formatted object
    */
   String formatMessage(LogMessage ioM);
   
   /**
    * Returns the string "footer" for the chosen output file format.
    * @return String of the default "footer" of the file
    */
   String endFile();
}
