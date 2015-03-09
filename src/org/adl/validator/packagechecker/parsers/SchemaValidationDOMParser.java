package org.adl.validator.packagechecker.parsers;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.adl.validator.util.Messages;
import org.adl.validator.util.ValidatorMessage;
import org.adl.validator.util.processor.FileHandler;
import org.adl.validator.util.processor.URIHandler;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.xerces.parsers.DOMParser;

/**
 * This parser will perform a schema validation parse using a Xerces DOMParser 
 * object
 * 
 * @author ADL Technical Team
 */
public class SchemaValidationDOMParser implements ErrorHandler
{
   /**
    * Constant value of 2
    */
   private static final int TWO = 2;
      
   /**
    * Constant value of a blank string 
    */
   private static final String BLANK = " ";
   
   /**
    * A DOMParser object used to validate the XML instance
    */
   private DOMParser mParser;

   /**
    * A String containing the schemalocation to be used for validation
    */
   private String mSchemaLocation;

   /**
    * A String containing the location of the package root
    */
   private String mRootDirectory;
   
   /**
    * URIHanlder object to decode and directory escape file paths
    */
   private URIHandler mURIHandler;

   /**
    * Result object which contains the results of the tests performed by the
    * checker
    */
   private List<ValidatorMessage> mParseMessages;

   /**
    * boolean indicating the outcome of the given parse
    */
   private boolean mParseSuccess = true;

   /**
    * The default constructor
    * 
    * @param iSchemaLocation is a String containing the schemas to be used for
    * validation
    * @param iRootDirectory is the location of the xml instance being 
    * validated
    */
   public SchemaValidationDOMParser(String iSchemaLocation, String iRootDirectory)
   {
      mURIHandler = new URIHandler();
      mSchemaLocation = iSchemaLocation;
      mParseMessages = new ArrayList<ValidatorMessage>();
      mRootDirectory = iRootDirectory;
      configureParser();
   }

   /**
    * This method will setup the DOMParser with the necessary schemas and
    * properties
    */
   private void configureParser()
   {
      // The default configuration is to allow xml 1.0 and 1.1 parsing
      mParser = new DOMParser();
      
      if ( mParser != null )
      {
         if ( !mSchemaLocation.equals("") )
         {
            // Add full path on to the location of each schema
            String newSchemaLocation = "";
            String schemaLocation[] = mSchemaLocation.split(BLANK);
            
            String nameSpace = "";
            String schema = "";
            
            for ( int i = 0; i < schemaLocation.length; i = i + TWO)
            {
               nameSpace = schemaLocation[i];               
               if ( URIHandler.isURL(schemaLocation[i+1]) )
               {
                  schema = schemaLocation[i+1];
               }
               else
               {                  
                  schema = "file:" + File.separator + mRootDirectory + schemaLocation[i+1];
                  schema = mURIHandler.escapeDirectories(schema);
                  schema = schema.replaceAll(" ", "%20");
                  schema = schema.replaceAll("/", File.separator + File.separator);
               }
               newSchemaLocation = newSchemaLocation + nameSpace + BLANK + schema + BLANK;
            }
                  
            newSchemaLocation = newSchemaLocation.trim();
            
            mSchemaLocation = newSchemaLocation;           
            
            try
            {
               mParser.setFeature("http://xml.org/sax/features/validation", true);
               mParser.setFeature("http://apache.org/xml/features/validation/schema", true);
               mParser.setFeature("http://xml.org/sax/features/namespaces", true);
               mParser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);               
               
               mParser.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", mSchemaLocation);

               mParser.setErrorHandler(this);
            }
            catch ( SAXException se )
            {
               // Exception handled in Error, Warning, Fatal Error methods
               mParseSuccess = false;
            }
                        
         }
         else
         {
            mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
                  Messages.getString("SchemaValidationDOMParser.0")));
            mParseSuccess = false;
         }
      }
      else
      {
         mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("SchemaValidationDOMParser.1")));
         mParseSuccess = false;
      }
   }

   /** Error. */
   /**
    * This method handles errors thrown by the SAXParser
    * 
    * @param iEx A SAXParseException object containing the details of the error
    */
   public void error(SAXParseException iEx) // throws SAXException
   {
      mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
            getErrorString(iEx)));
      mParseSuccess = false;
   }

   /** SAX Parser Utility Methods * */
   /** ErrorHandler methods * */

   /** Fatal error. */
   /**
    * This method handles fatal errors thrown by the SAXParser
    * 
    * @param iEx A SAXParseException object containing the details of the fatal error
    */
   public void fatalError(SAXParseException iEx) // throws SAXException
   {
      mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
            getErrorString(iEx)));
      mParseSuccess = false;
      // throw ex;
   }

   /**
    * Returns a string of the error.
    * 
    * @param iException
    *           contains the location and cause of the parse error
    * @return a String containing the location and cause in the correct format
    */
   private String getErrorString(SAXParseException iException)
   {
      String error = "";

      error = Messages.getString("SchemaValidationDOMParser.7", iException.getMessage(), Integer.toString(iException.getLineNumber()), Integer.toString(iException.getColumnNumber()));

      return error;
   }

   /**
    * Gets the value of the schemaLocation element
    * 
    * @return a String containing the value of the noSchemaLocation element
    */
   public List<ValidatorMessage> getParseMessages()
   {
      return mParseMessages;
   }

   /**
    * This method executes a parse on the given xml instance
    * 
    * @param iXMLFile is a String representing the location of the XML instance to be validated
    * @return a boolean indicating the results of the validation parse
    */
   public boolean performParse(String iXMLFile)
   {
      try
      {  
         String fileName = URIHandler.decode(iXMLFile, URIHandler.ENCODING);
         
         mParser.parse(setUpInputSource(fileName));
      }
      catch ( SAXException se )

      {
         mParseSuccess = false;
         mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("SchemaValidationDOMParser.2")));
      }
      catch ( IOException ioe )
      {
         mParseSuccess = false;
         mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("SchemaValidationDOMParser.3")));
         // ioe.printStackTrace();
      }
      catch ( NullPointerException npe )
      {
         npe.printStackTrace();
         mParseSuccess = false;
         mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("SchemaValidationDOMParser.4")));
      }
      catch ( IllegalArgumentException iae )
      {
         mParseSuccess = false;
         mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
            Messages.getString("SchemaValidationDOMParser.5")));
         // iae.printStackTrace();
      }

      return mParseSuccess;
   }

   /**
    * This method sets up the input source for the test subject file.
    * 
    * @param iFileName
    *           The name of the file for which we are are setting up the input source.
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
               Messages.getString("SchemaValidationDOMParser.6")));
      }      
      
      return is;
   }

   /** Warning. */
   /**
    * This method handles warnings thrown by the SAXParser
    * 
    * @param iEx A SAXParseException object containing the details of the warning
    */
   public void warning(SAXParseException iEx) // throws SAXException
   {
      // mParseErrors.add(arg0);
      mParseMessages.add(new ValidatorMessage(ValidatorMessage.FAILED, getErrorString(iEx)));
      mParseSuccess = false;
   }
}
