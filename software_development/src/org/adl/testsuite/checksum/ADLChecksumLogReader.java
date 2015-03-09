package org.adl.testsuite.checksum;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.adl.util.decode.decodeHandler;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

public class ADLChecksumLogReader
{
   /**
    * This method will parse and log file to obtain the checksum value
    * 
    * @param iPath The path of the log file to be parsed
    * @return A long value containing the checksum from the log or 0 if no checksum exists
    */
   public static long getChecksumValue(final String iPath )
   {
      long checksum = 0;      
      final Document log = parse(iPath);
      
      try
      {
         final XPath checkPath = XPath.newInstance("logmessages/message[@type='checksum']");         
         final List checksums = checkPath.selectNodes(log);
         
         if ( !checksums.isEmpty())
         {
            final Element e = (Element)checksums.get(0);
            
            // We have to split the value from the rest of the message
            checksum = Long.parseLong(e.getValue().split(": ")[1]);
         }
      }
      catch ( JDOMException jde )
      {
         // jde.printStackTrace();
         return checksum;
      }
      catch ( Exception e )
      {
         // e.printStackTrace();
         return checksum;
      }
      
      return checksum;
   }
   
   /**
    * This method parses a file to create a Document object
    * 
    * @param iFile The file to be parsed
    * @return A Document object containing the file
    */
   private static Document parse(String iFile)
   {
      decodeHandler handler = new decodeHandler();
      
      try
      {
         FileInputStream fis = new FileInputStream(handler.decode(iFile, "UTF-16"));
         
         SAXBuilder builder = new SAXBuilder(/*"org.apache.xerces.parsers.SAXParser"*/);
         builder.setReuseParser(false);
         
         return builder.build(fis);
      }
      catch ( IOException ioe )
      {
         //ioe.printStackTrace();
         return null;
      }
      catch ( JDOMException jde )
      {
         //jde.printStackTrace();
         return null;
      }
   }

}
