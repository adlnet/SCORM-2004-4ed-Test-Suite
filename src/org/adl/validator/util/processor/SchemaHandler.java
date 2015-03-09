package org.adl.validator.util.processor;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.adl.validator.packagechecker.parsers.RequiredFilesSaxParser;
import org.adl.validator.util.Messages;
import org.adl.validator.util.ValidatorMessage;


/**
 * This class obtains and formats all needed schema information from the XML file
 * 
 * @author ADL Technical Team
 *
 */
public class SchemaHandler
{
   /**
    * A Constant value for 2
    */
   private static final int TWO = 2;
   
   /**
    * A Constant value for STOP
    */
   private static final String STOP = "STOP";
   
   /**
    * The fully qualified name of the resource bundle.  This bundle contains
    * all of the known schemas.
    */
   private static final String BUNDLE_NAME = 
      "org.adl.validator.properties.knownSchemas";
   
   /**
    * The formal reference to the <code>ResourceBundle</code>
    */
   private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
      .getBundle( BUNDLE_NAME );
   
   /**
    * The fully qualified name of the resource bundle.  This bundle contains
    * all of the known namespaces.
    */
   private static final String NS_BUNDLE_NAME = 
      "org.adl.validator.properties.knownNamespaces";
   
   /**
    * The formal reference to the <code>ResourceBundle</code>
    */
   private static final ResourceBundle NS_RESOURCE_BUNDLE = ResourceBundle
      .getBundle( NS_BUNDLE_NAME );
   
   
   /**
    * A Constant for the space character
    */
   private static final String SPACE = " ";
   
   /**
    * Parser object used to perform various information gathering parses 
    */
   private RequiredFilesSaxParser mParser;
      
   /**
    *  String containing the name of the file to be parsed
    */
   private String mFileName;
   
   /**
    * List of errors generated during the schema setup process
    */
   private List<ValidatorMessage> mErrorMessages;

   /**
    * A URIHandler object used to tranform file paths
    */
   private URIHandler mUriHandler;
   
   /**
    * A boolean indicating if the xml instance contains unknown extensions
    */
   private boolean mIsExtensionsFound;
   
   /**
    * Default Constructor
    * 
    * @param iFileName is a String containing the file to be processed
    */
   public SchemaHandler(String iFileName)
   {      
      //default constructor 
      mFileName = iFileName;
      mUriHandler = new URIHandler();
      mErrorMessages = new ArrayList<ValidatorMessage>();
      mIsExtensionsFound = false;
   }   
  
   /**
    * Creates the namespace string used for schema validation. 
    * 
    * @return a String that contains the schemaLocation values contained
    * in the xml instance
    */
   public String createSchemaLocationList()
   {
      Set<String> schemaValidationList = new HashSet<String>();      // A list of the files and namespaces needed for validation
                  
      Set<String> namespaceURIs = new HashSet<String>();
      
      String schemaLocation = "";            // All the schemalocation values used in the package      
      Set<String> metadata = new HashSet<String>();          // The list of metadata files referenced by the xml file
      
      // Get the root of the package to determine the location of 
      // all files referenced in the xml file
      String rootDir = (new File(mFileName)).getParent();
      
      // Create requiredFilesParser and pass in the xml file
      mParser = new RequiredFilesSaxParser();      
      
      boolean success = mParser.performParse(mFileName);
      
      if ( !success )
      {         
         mErrorMessages.add(new ValidatorMessage(ValidatorMessage.FAILED, 
                      "An error occurred during imsmanifest.xml parsing"));
         mErrorMessages.addAll(mParser.getParseMessages());
         return setToString(schemaValidationList);
      }
            
      // Get the namespace and schemalocation information from the xml file
      namespaceURIs = mParser.getNamespaceURIs();
      schemaLocation = mParser.getSchemaLocation();
      // Get namespace and schemalocation information from the metadata files
      metadata = mParser.getMetadataFiles();
      
      // If noSchemaLocation exists add its information to schemalocation
      if ( !mParser.getNoSchemaLocation().equals("") )
      {
         schemaLocation = schemaLocation + SPACE + mParser.getNoSchemaLocation();
      }
      
      // Truncate everything but filename
      String tempFileName = mFileName.split(File.separator + File.separator)[mFileName.split(File.separator + File.separator).length - 1];
      
      // Process the information from the main xml document
      String tempList[] = assembleValidationList(tempFileName, setToString(namespaceURIs), schemaLocation).split(SPACE);
      if ( tempList.length >= TWO )
      {
         String tempSchemaLocation = "";
         for ( int i = 0; i < tempList.length; i=i+TWO )
         {
            tempSchemaLocation = tempList[i+1];            
            tempSchemaLocation = mUriHandler.escapeDirectories(tempSchemaLocation);
            
            int rootExceeded = mUriHandler.wasRootExceeded();
            
            if ( rootExceeded > 0 )
            {
               mErrorMessages.add(new ValidatorMessage(ValidatorMessage.FAILED, 
                     Messages.getString("SchemaHandler.1", tempSchemaLocation)));
            }            
            schemaValidationList.add(tempList[i] + SPACE + tempSchemaLocation);
         }
      }      
      
      // Check current schema list for extensions
      mIsExtensionsFound = findExtensions(schemaValidationList);
      
      // --------------------- Metadata file processing -------------------- //
      
      // If external metadata is present in the xml file, parse each file
      if ( metadata != null )
      {
         // Set up iterator and required files parser
         Iterator<String> metadataIter = metadata.iterator();
         RequiredFilesSaxParser metadataParser = new RequiredFilesSaxParser();
         
         while ( metadataIter.hasNext() )
         {
            // reset parser
            metadataParser = new RequiredFilesSaxParser();
            // Create variables to hold info on current metadata file
            Set<String> metaNamespaceURIs = new HashSet<String>();
            String metaSchemaLocation = "";
               
            // Create the full path to the file and execute the parse
            String filePath = metadataIter.next().toString();
            String fileRoot = (new File(filePath)).getParent();
            
            // If file is at root, the fileRoot is null so it must be handled correctly
            if ( fileRoot ==  null )
            {
               fileRoot = "";
            }
            else
            {
               fileRoot = fileRoot + "/";
            }
            
            // Reset URIHandler
            mUriHandler = new URIHandler();
            
            String currentFile = rootDir + File.separator + filePath;
            currentFile = mUriHandler.escapeDirectories(currentFile);
            currentFile = URIHandler.decode(currentFile, URIHandler.ENCODING);
            
            boolean directoryExceeded = mUriHandler.wasRootExceeded() > 0;
            File metaFile = new File( currentFile );
            
            boolean fileExists = metaFile.exists();
            
            boolean metadataSuccess = true;
            
            
            if ( directoryExceeded || !fileExists )
            {
               metadataSuccess = false;
               
               // Output a different error if the instance was empty
               if ( filePath == null || filePath.trim().equals("") )
               {
                  mErrorMessages.add(new ValidatorMessage(ValidatorMessage.FAILED, 
                        Messages.getString("SchemaHandler.2", filePath)));
               }
               else
               {
                  mErrorMessages.add(new ValidatorMessage(ValidatorMessage.FAILED, 
                     Messages.getString("SchemaHandler.3", filePath, filePath)));
               }
            }
            else
            {
               // We know the file path is correctly encoded and formatted, we will reset it so 
               // the parser can use it
               currentFile = rootDir + File.separator + filePath;
               currentFile = mUriHandler.escapeDirectories(currentFile);
               
               metadataSuccess = metadataParser.performParse(currentFile);
               
               // The metadata is wellformed, gather all schemalocation information
               if ( metadataSuccess )
               {                        
                  // Get the set of all namespaces from metadata file
                  metaNamespaceURIs = metadataParser.getNamespaceURIs();
      
                  // Get metadata schemaLocation
                  metaSchemaLocation = metadataParser.getSchemaLocation();
                  
                  // Add metadata noSchemaLocation if it exists
                  if ( !metadataParser.getNoSchemaLocation().equals("") )
                  {
                     metaSchemaLocation = metaSchemaLocation + SPACE + metadataParser.getNoSchemaLocation();
                  }
                  
                  String metaSchemaValidationList = assembleValidationList(filePath, setToString(metaNamespaceURIs),metaSchemaLocation);
                  
                  String metaTempList[] = metaSchemaValidationList.split(SPACE);
                  
                  if ( metaTempList.length >= TWO )
                  {
                     String tempMetaSchemaLocation = "";
                     for ( int i = 0; i < metaTempList.length; i=i+TWO)
                     {                        
                        if ( URIHandler.isURL(metaTempList[i+1]) )
                        {
                           tempMetaSchemaLocation = metaTempList[i+1];
                        }
                        else
                        {
                           tempMetaSchemaLocation = fileRoot + metaTempList[i+1];
                           URIHandler handler = new URIHandler();
                           tempMetaSchemaLocation = handler.escapeDirectories(tempMetaSchemaLocation);
                           
                           int rootExceeded = handler.wasRootExceeded();
                           
                           if ( rootExceeded > 0 )
                           {
                              mErrorMessages.add(new ValidatorMessage(ValidatorMessage.FAILED, 
                                    Messages.getString("SchemaHandler.1", tempMetaSchemaLocation)));
                           }                  
                        }
                        schemaValidationList.add(metaTempList[i] + SPACE + tempMetaSchemaLocation);                  
                     }
                  }
               }
               // Metadata is not wellformed, add an error message
               else
               {               
                  mErrorMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
                        Messages.getString("SchemaHandler.4", filePath)));
                  // Add parser wellformedness error messages
                  Iterator<ValidatorMessage> metaErrorIter = metadataParser.getParseMessages().iterator();
                  while ( metaErrorIter.hasNext() )
                  {
                     mErrorMessages.add(metaErrorIter.next());
                  }
               }
            }
         }      
      }      
      return setToString(schemaValidationList);
   }
   
   /**
    * This utility method converts a Set to a string seperated by spaces
    * 
    * @param iSetName is the Set to be converted to a string
    * @return the String form of the given Set
    */
   private String setToString(Set<String> iSetName)
   {
      Iterator<String> setIter = iSetName.iterator();      
      String tempString = "";      
      boolean first = true;
      
      while ( setIter.hasNext() )
      {
         if ( !first )
         {
            tempString = tempString + SPACE;
         }
         first = false;
         tempString = tempString + setIter.next().toString();
      }
      
      return tempString;
   }
   
   /**
    * This method takes the namespaces and schemalocations of an xml file and
    * matches any possible matches in a String
    * 
    * @param iNamespaces is a String holding the list of known namespaces
    * @param iSchemaLocation is a String holding the known value of schemalocation
    * @param iCurrentFile is a String pointing to the file which will be parsed
    * @return a String containing the paired namespaces and schemalocations in the 
    * format 'namespace schemalocation namesapce schemalocation ....'
    */
   private String assembleValidationList(String iCurrentFile, String iNamespaces, String iSchemaLocation)
   {
      // Boolean value to determine if schemaLocation was used
      boolean schemaLocationUsed = true;
      
      // The list which will hold the completed validation values
      String schemaValidationList = "";
      
      // Split the values around the blank spaces
      String namespaces[] = iNamespaces.split(SPACE);      
      String schemaLocations[] = iSchemaLocation.split(SPACE); 
      
      // Check to ensure the schemaLocation was wellformed (e.g. even pairs)
      if ( !iSchemaLocation.equals("") && schemaLocations.length %2 != 0 )
      {
         mErrorMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("SchemaHandler.0")));
      }
      
      // Check to see if schemaLocation was used
      if ( schemaLocations.length <= 1 )
      {
         mErrorMessages.add(new ValidatorMessage(ValidatorMessage.WARNING,
                           Messages.getString("SchemaHandler.5", iCurrentFile)));
         schemaLocationUsed = false;
      }
    
      // At least one namespace was referenced
      if ( namespaces.length > 0 )      
      {
         // Create a 2-Dimensional array holding the known namespaces
         String validationList[][] = new String[namespaces.length][TWO];

         for ( int i = 0; i < namespaces.length; i++ )
         {
            validationList[i][0] = namespaces[i];    
         }
         
         if ( schemaLocationUsed )
         {
            if ( (schemaLocations.length % TWO) > 0 )
            {
               // Malformed schemaLocation - should be in pairs of 2            
               return "";
            }
            
            // Pair the schemas and schemaLocations in a Set
            Set<String> schemaSet = new HashSet<String>();
            
            for ( int i = 0; i < schemaLocations.length; i=i+TWO)
            {
               schemaSet.add(schemaLocations[i] + SPACE + schemaLocations[i+1]);            
            }
                   
            // Compare the namespace and schemaLocation arrays and match each
            // namespace with with a schemaLocation if possible        
            for ( int i = 0; i < validationList.length; i++)
            {
               if ( validationList[i][1] == null )
               {
                  Iterator<String> schemaIterator = schemaSet.iterator();
                  boolean match = false;
                  while ( schemaIterator.hasNext() && !match)
                  {
                     String currentSchema[] = (schemaIterator.next()).toString().split(SPACE);
                     if ( validationList[i][0].equals(currentSchema[0]) &&
                         validationList[i][1]==null)
                      {
                         validationList[i][1] = currentSchema[1];                         
                         match = true;
                      }
                  }                 
               }            
            }
         }
         
         // Verfiy every namespace has a schema, if not check known schemas
         String knownSchemas[] = getKnownSchemaLocations().split(SPACE);    
         
         for ( int i = 0; i < validationList.length; i++ )
         {            
            if ( validationList[i][1] == null )
            {
               if ( schemaLocationUsed )
               {
                  mErrorMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
                        Messages.getString("SchemaHandler.6", validationList[i][0])));
               }
               
               for ( int j = 0; j < knownSchemas.length; j=j+TWO )
               {
                  if ( validationList[i][0].equals(knownSchemas[j]))
                  {
                     validationList[i][1] = knownSchemas[j+1];
                     break;
                  }
               }
            }
         }
         
         // Assemble the validation String but also produce errors for any misssing schemas
         for( int i = 0; i < validationList.length; i++)
         {            
            if ( validationList[i][1] != null )
            {
               if ( i > 0 )
               {
                  schemaValidationList = schemaValidationList + SPACE;
               }
               schemaValidationList = schemaValidationList + validationList[i][0] +
               SPACE + validationList[i][1];
            }
            else
            {
               mErrorMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
                     Messages.getString("SchemaHandler.7", validationList[i][0])));
            }
         }         
      }      
      return schemaValidationList.trim();      
   }
   
   /**
    * Creates the list of schemaLocation values
    * 
    * @return - List of the schemaLocation values
    */
   private String getKnownSchemaLocations()
   {
      String schemaList = "";
      String tempSchemaName = "";      
      
      int i = 0;
      while ( !tempSchemaName.equals(STOP) )
      {
         tempSchemaName = getString("knownSchema" + Integer.toString(i));
         if ( !tempSchemaName.equals(STOP) )
         {                    
            schemaList = schemaList + SPACE + tempSchemaName;
         }
         i++;
      }      
      return schemaList.trim();
   }
   
   /**
    * Creates the list of namespaces known to ADL
    * 
    * @return - List of the known namespaces
    */
   private static String getKnownNamespaces()
   {
      String namespaceList = "";
      String tempNamespace = "";      
      
      int i = 0;
      while ( !tempNamespace.equals(STOP) )
      {
         tempNamespace = getNSString("knownNamespace" + Integer.toString(i));
         if ( !tempNamespace.equals(STOP) )
         {                    
            namespaceList = namespaceList + SPACE + tempNamespace;
         }
         i++;
      }      
      return namespaceList.trim();
   }
   
   /**
    * Retrieves the schemaLocation associated with the given key.
    * 
    * @param iKey - A unique identifier that identifies known schemas. 
    * @return String - The schemaLocation value of the known schema.
    */
   private static String getString( String iKey )
   {
      try
      {
         return RESOURCE_BUNDLE.getString( iKey );
      }
      catch ( MissingResourceException e )
      {
         return STOP;
      }
   }
   
   /**
    * Retrieves the namespace associated with the given key.
    * 
    * @param iKey - A unique identifier that identifies known namespaces. 
    * @return String - The namesapce value of the known namespace.
    */
   private static String getNSString( String iKey )
   {
      try
      {
         return NS_RESOURCE_BUNDLE.getString( iKey );
      }
      catch ( MissingResourceException e )
      {
         return STOP;
      }
   }
   
   /**
    * This method creates a String containing the list of files required for
    *  validation
    * 
    * @return a String containing the names of the files needed for validation
    */
   public String createRequiredFilesList()
   {
      String requiredFiles = "";
            
      String schemaValidationList = createSchemaLocationList();
      
      String tempList[] = schemaValidationList.split(SPACE);
      
      for ( int i = 1; i < tempList.length; i=i+TWO)
      {
         if ( i > 1 )
         {
            requiredFiles = requiredFiles + SPACE; 
         }            
         requiredFiles = requiredFiles + tempList[i];
      }
            
      return requiredFiles;      
   }

   /** This method takes the required files list and compares it to the known schema list.
    *  All matching values are then added to the schema validation list.
    * 
    * @return a String containing the list of files required for schema validation
    */
   public String createSchemaValidationList()
   {
      // Create arrays of each schemaLocation source
      String[] knownSchemaLocations = getKnownSchemaLocations().split(SPACE);
      String[] currentSchemaLocations = createSchemaLocationList().split(SPACE);

      String schemaValidationList = "";
      
      // Compare each schemaLocation to the known schemas to ensure only known
      // schemas are used for validation
      for ( int i = 0; i < currentSchemaLocations.length; i=i+TWO )
      {
         for ( int j = 0; j < knownSchemaLocations.length; j=j+TWO )
         {
            if ( currentSchemaLocations[i].equals(knownSchemaLocations[j]) &&
                 currentSchemaLocations[i+1].equals(knownSchemaLocations[j+1]))
            {               
               schemaValidationList = schemaValidationList + SPACE + 
                  currentSchemaLocations[i] + SPACE + currentSchemaLocations[i+1];
               break;
            }
         }
      }
      
      // Remove any excess whitespace added to the schema list
      return schemaValidationList.trim();      
   }
   
   /** This method takes the required files list and compares it to the known schema list.
    *  If a non-matching value is present we know extensions are present
    * 
    * @param iSchemaValidationSet The Set containing the schemaLocation values
    * @return a boolean containing whether or not extensions exist
    */
   public boolean findExtensions(Set<String> iSchemaValidationSet)
   {
      // Create arrays of each schemaLocation source
      String[] currentSchemaLocations = setToString(iSchemaValidationSet).split(SPACE);
      String[] knownNamespaces = getKnownNamespaces().split(SPACE);
      boolean extensionsFound = false;
      
      // Compare each schemaLocation to the known schemas to ensure only known
      // schemas are matched
      for ( int i = 0; i < currentSchemaLocations.length; i=i+TWO )
      {
         boolean matchFound = false;
         
         for ( int j = 0; j < knownNamespaces.length; j++ )
         {
            if ( currentSchemaLocations[i].equals(knownNamespaces[j]) )
            {
               matchFound = true;
               break;
            }
         }
         if ( !matchFound )
         {
            extensionsFound = true;
         }         
      }
      
      return extensionsFound;      
   }
   
   /**
    * This methods determines if extensions are present in the xml instance
    * 
    * @return a boolean indicating if extensions are present in the xml instance
    */
   public boolean isExtensionsFound()
   {
      return mIsExtensionsFound;
   }
   
   /**
    * This method returns a list of errors thrown during the schema processing
    * 
    * @return a List of error messages
    */
   public List<ValidatorMessage> getErrorMessages()
   {
      return mErrorMessages;
   }   
   
   /**
    * This method determines if the given namespace is a known namespace
    * 
    * @param iNamespace is a String representing the namespace to be tested
    * @return a boolean indicating whether the given namespace is known
    */
   public static boolean isKnownNamespace( String iNamespace )
   {
      String knownNS = getKnownNamespaces();
      if ( knownNS.indexOf(iNamespace) != -1 )
      {
         return true;
      }
      else
      {
         return false;
      }
   }
}
