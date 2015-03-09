package org.adl.validator.packagechecker.parsers;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.adl.validator.util.Messages;
import org.adl.validator.util.ValidatorMessage;
import org.adl.validator.util.processor.FileHandler;
import org.adl.validator.util.processor.URIHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class performs all validation and information gathering SAX parses
 * 
 * @author ADL Technical Team
 *
 */
public abstract class ValidatorSaxParser extends DefaultHandler
{
   /**
    * Constant value of 0
    */
   protected static final int BASE_ONE = 0;
   
   /**
    * Constant value of 1
    */
   protected static final int BASE_TWO = 1;
   
   /**
    * Constant value of 2
    */
   protected static final int BASE_THREE = 2;
   
   /**
    * Constant value of 3
    */
   protected static final int BASE_SIZE = 3;
   
   /**
    * A constant holding the name of the manifest element
    */
   protected static final String MANIFEST = "manifest"; 
   
   /**
    * A constant holding the name of the resources element
    */
   protected static final String RESOURCES = "resources"; 
   
   /**
    * A constant holding the name of the resource element
    */
   protected static final String RESOURCE = "resource";
   
   /**
    * A constant holding the name of the location element
    */
   protected static final String LOCATION = "location";
   
   /**
    * A constant holding the name of the FILE element
    */
   protected static final String FILE = "file";
   
   /**
    * A constant holding the name of the href element
    */
   protected static final String HREF = "href";
   
   /**
    * This String contains the IMSCP name-space 
    */
   protected static final String IMSCP = "http://www.imsglobal.org/xsd/imscp_v1p1";
   
   /**
    * This String contains the ADLCP name-space 
    */
   protected static final String ADLCP = "http://www.adlnet.org/xsd/adlcp_v1p3";
   
   /**
    * This String contains the ADLSEQ name-space 
    */
   protected static final String ADLSEQ = "http://www.adlnet.org/xsd/adlseq_v1p3";
   
   /**
    * This String contains the IMSSS name-space 
    */
   protected static final String IMSSS = "http://www.imsglobal.org/xsd/imsss";
   
   /**
    * This String contains the ADLNAV name-space 
    */
   protected static final String ADLNAV = "http://www.adlnet.org/xsd/adlnav_v1p3";
   
   /**
    * This String contains the XML name-space 
    */
   protected static final String XML = "http://www.w3.org/XML/1998/namespace";
   
   /**
    * This String contains the XSI name-space 
    */
   protected static final String XSI = "http://www.w3.org/2001/XMLSchema-instance";
   
   /**
    * A string containing the location of the XML file to be parsed
    */
   protected String mXMLFile;
   
   /**
    * A string holding the name of the XML file to be parsed
    */
   protected String mFileName;
   
   /**
    * Result object which contains the results of the tests performed by the
    * checker
    */
   protected List<ValidatorMessage> mParseMessages;
   
   /**
    * A Boolean indicating the outcome of the given parse
    */
   protected boolean mParseSuccess = true;
   
   /**
    * SAXParserFactory object used to configure the SAX parser
    */
   protected SAXParserFactory mParserFactory;
   
   /**
    * SAXParser used to peform the necessary parses
    * The default constructor
    */
   protected SAXParser mParser;
   
   /**
    * Schema Validation Constructor. Sets the attributes to their initial 
    * values.
    */
   public ValidatorSaxParser()
   {      
      
   }
   
   /**
    * Method used to configure the parser in the default, information gathering,
    * format
    */
   protected void configureParser()
   {      
      // Sets the location of the XML file to be parsed      
      mParseMessages = new ArrayList<ValidatorMessage>();      
      
      try
      {
         // Configure Parser
         mParserFactory = SAXParserFactory.newInstance();
         mParserFactory.setNamespaceAware(true);         
         mParser = mParserFactory.newSAXParser(); 
      }
      catch ( ParserConfigurationException exceptionPCE )
      {
         // Exception handled in Error, Warning, Fatal Error methods
         mParseSuccess = false;
      }
      catch ( SAXException exceptionSE )
      {
         // Exception handled in Error, Warning, Fatal Error methods
         mParseSuccess = false;
      }
   }
   
   /**
    * This method initializes and executes the parser
    * 
    * @param iFileName A String containing the location of the xml instance to be parsed
    * @return a boolean indicating the status of the parse
    */
   public boolean performParse(String iFileName)
   {
      mFileName = iFileName;
      
      // Encode any whitespace in the filename to be parsed
      String fName = iFileName.replaceAll(" ", "%20");
      fName = fName.replaceAll("/", File.separator + File.separator);
      
      try
      {                  
         // Execute SAX Parse
         String fileName = URIHandler.decode(iFileName, URIHandler.ENCODING);
         mParser.parse(setUpInputSource(fileName), this);
         
      }      
      catch ( SAXException exceptionSE )
      {
         // Exception handled in Error, Warning, Fatal Error methods         
         mParseSuccess = false;
      }
      catch ( IOException exceptionIOE )
      {
         // Exception handled in Error, Warning, Fatal Error methods         
         mParseSuccess = false;
         mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("ValidatorSaxParser.0")));
      }  
      return mParseSuccess;
   }
   
   /**
    * Gets the parse messages from a given parse
    * 
    * @return a List containing all parser messages
    */
   public List<ValidatorMessage> getParseMessages()   
   {      
      return mParseMessages;
   }

   
   /** SAX Parser Utility Methods **/     
   /** ErrorHandler methods **/
   
   /** Warning. */
   /**
    * This method handles warnings thrown by the SAXParser
    * 
    * @param iEx A SAXParseException object containing the details of the warning
    */   
   public void warning(SAXParseException iEx) //throws SAXException
   {
     mParseMessages.add(new ValidatorMessage(ValidatorMessage.WARNING, getErrorString(iEx)));
     mParseSuccess = false;
   }

   /** Error. */
   /**
    * This method handles errors thrown by the SAXParser
    * 
    * @param iEx A SAXParseException object containing the details of the error
    */
   public void error(SAXParseException iEx) //throws SAXException
   {      
      mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,getErrorString(iEx)));
      mParseSuccess = false;
   }

   /** Fatal error. */
   /**
    * This method handles fatal errors thrown by the SAXParser
    * 
    * @param iEx A SAXParseException object containing the details of the fatal error
    */
   public void fatalError(SAXParseException iEx) //throws SAXException 
   {
      mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,getErrorString(iEx)));
      mParseSuccess = false;
     //throw ex;
   }
   
   /**
    * Returns a string of the error.
    * 
    * @param iException contains the location and cause of the parse error
    * @return a String containing the location and cause in the correct format
    */
   private String getErrorString(SAXParseException iException) 
   {
      String error = "";      
      
      error = Messages.getString("ValidatorSaxParser.2", iException.getMessage(), 
            Integer.toString(iException.getLineNumber()), 
            Integer.toString(iException.getColumnNumber()));
      
      return error;
   }
   
   /**
    * This method sets up the input source for the test subject file.
    * 
    * @param iFileName
    *           The name of the file we are are setting up the input source for.
    * @return Returns an input source needed for parsing
    */
   private InputSource setUpInputSource(String iFileName)
   {
      InputSource is = new InputSource();
      FileHandler fHandler = new FileHandler();

      is = fHandler.setupFileSource(iFileName);
      
      if ( fHandler.getErrorMessages().size() > 0 )
      {
         mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED, 
               Messages.getString("ValidatorSaxParser.1")));
      }
      
      return is;
   }   
}