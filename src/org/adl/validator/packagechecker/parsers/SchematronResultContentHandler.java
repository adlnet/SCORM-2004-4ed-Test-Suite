package org.adl.validator.packagechecker.parsers;

import java.util.ArrayList;
import java.util.List;

import org.adl.validator.util.ValidatorMessage;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This parser will obtain information needed to create a list of files
 * required to validate the xml instance
 * 
 * @author ADL Technical Team
 *
 */
public class SchematronResultContentHandler extends DefaultHandler
{
   /**
    * A List containing the messages resulting from the schematron transform
    */
   final private List<ValidatorMessage> mMessageList;
   
   /**
    * The default constructor
    */
   public SchematronResultContentHandler()
   {
      super();
      mMessageList = new ArrayList<ValidatorMessage>();
   }
   
   /** 
    * This method will analyze the attributes, names, and namespace of a given element
    * 
    * @param iNamespaceURI Is a String value which holds the namespace of the element
    * @param iLocalName Is a String value containing the local name of the element
    * @param iRawName Is a String value containing the raw name of the element
    * @param iAttrs Is an Attributes value containing the elements attributes
    */ 
   public void startElement(final String iNamespaceURI, final String iLocalName, 
         final String iRawName, final Attributes iAttrs) 
   {         
      // Add element namespace to Set
      // Ignore http://www.w3.org/2001/XMLSchema-instance namespace
      // as it only defines the schemaLocation or noSchemaLocation element
      
      if ( "svrl:successful-assert".equals(iRawName) && (iAttrs != null) )
      {
         final int len = iAttrs.getLength();
         for (int i = 0; i < len; i++)
         {               
            if ( iAttrs.getQName(i).equals("result"))
            {      
               final String message[] = iAttrs.getValue(i).split("~");
               mMessageList.add(new ValidatorMessage(ValidatorMessage.findMessageType(message[0]),
                     message[1])); 
            }
         }
      }
   }
   
   /**
    * This method will return of List of Result Messages
    * 
    * @return A List of Result Messages
    */
   public List<ValidatorMessage> getResultMessages()
   {
      return mMessageList;
   }
   
   /* (non-Javadoc)
    * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
    */
   public void warning(final SAXParseException spe)
   {
      mMessageList.add(new ValidatorMessage(ValidatorMessage.WARNING,
            spe.getLocalizedMessage()));
   }
   
   /* (non-Javadoc)
    * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
    */
   public void error(final SAXParseException spe)
   {
      mMessageList.add(new ValidatorMessage(ValidatorMessage.FAILED,
            spe.getLocalizedMessage()));
   }
   
   /* (non-Javadoc)
    * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
    */
   public void fatalError(final SAXParseException spe)
   {
      mMessageList.add(new ValidatorMessage(ValidatorMessage.FAILED,
            spe.getLocalizedMessage()));
   }
}
