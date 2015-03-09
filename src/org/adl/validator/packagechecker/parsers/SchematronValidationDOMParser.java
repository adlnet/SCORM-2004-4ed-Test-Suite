package org.adl.validator.packagechecker.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.adl.validator.util.CheckerStateData;
import org.adl.validator.util.Messages;
import org.adl.validator.util.ValidatorKeyNames;
import org.adl.validator.util.ValidatorMessage;
import org.adl.validator.util.processor.ParameterHandler;
import org.adl.validator.util.processor.URIHandler;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;

import net.sf.saxon.TransformerFactoryImpl;


/**
 * This class will parse and transform an xml instance using a schematron transform
 * The transform will perform the application profile checks and record the results
 * in an xml document object
 * 
 * @author ADL Technical Team
 *
 */
public class SchematronValidationDOMParser
{
   /**
    * A String containing the URI for the imscp namespace
    */
   static final String IMSCP = "http://www.imsglobal.org/xsd/imscp_v1p1";
   
   /**
    * A String containing the URI for the imsss namespace
    */
   static final String IMSSS = "http://www.imsglobal.org/xsd/imsss";

   /**
    * A String containing the URI for the xml namespace
    */
   static final Namespace XML = Namespace.getNamespace("xml", "http://www.w3.org/XML/1998/namespace");

   /**
    * This constant value represents the value 1024
    */
   static final int KILOBYTE = 1024;

   /**
    * A Document containing the xml instance to be validated
    */
   private static Document mXMLInstance;

   /**
    * A List of visited resources
    */
   private static List<String> mResourceList;

   /**
    * A URIHandler object used to process URI values
    */
   private static URIHandler mURIHandler = new URIHandler();

   /**
    * A list containing all identifier attributes found in the XML instance
    */
   private static List<String> mIdentiferList;

   /**
    * A list containing all sequencing element ID attributes found in the XML
    * instance
    */
   private static List<String> mIDList;
   
   /**
    * A List of error messages resulting from the testing of referencedObjectives
    */
   private static List<ValidatorMessage> mReferencedObjectiveMessages;
   
   /**
    * A List containing all error messages generated from the Schematron parse
    */
   final private List<ValidatorMessage> mErrorMessages;

   /**
    * A SAXBuilder object used to create Document Objects and execute tranforms
    */
   final private SAXBuilder mBuilder;

   /**
    * A boolean indicating whether or not to validate including the submanifest
    */
   final private boolean mValidateSubmanifest;

   /**
    * A boolean indicating whether or not a submanifest is present
    */
   private boolean mDoesSubmanifestExist;

   /**
    * An InputStream to access the schematron schema
    */
   final private InputStream mSchematronSchema = 
      this.getClass().getResourceAsStream("/org/adl/validator/packagechecker/parsers/xsd/iso_svrl.xsl");

   /**
    * An InputStream to access the schematron schema skeleton schema
    */
   final private InputStream mSchematronSkeletonSchema = 
      this.getClass().getResourceAsStream("/org/adl/validator/packagechecker/parsers/xsd/iso_schematron_skeleton.xsl");
    
   /**
    * This constructor sets the parser to use the submanifest in validation
    * 
    * @param iValidateSubmanifest A boolean indicating whether or not to validate the submanifest
    */
   public SchematronValidationDOMParser(final boolean iValidateSubmanifest)
   {
      mBuilder = new SAXBuilder(/*"org.apache.xerces.parsers.SAXParser"*/);
      mBuilder.setReuseParser(false);
      mErrorMessages = new ArrayList<ValidatorMessage>();
      mValidateSubmanifest = iValidateSubmanifest;
      mDoesSubmanifestExist = false; 
      mReferencedObjectiveMessages = new ArrayList<ValidatorMessage>();
   }
   
   /**
    * This method will execute parses and tranforms on the xml instance in an
    * attempt to validation against the application profile conditions
    * 
    * @param iSchematron The location of the schematron file containing the validation rules
    * @param iFile The location of the xml instance being validated
    * @return A boolean indicating the success of the parse
    */
   public boolean performParse(final InputStream iSchematron, final String iFile)
   {
      try
      {
         boolean success = true;
         
         // SAX Parse the XML File to get lists of its identifiers for later use
         mIdentiferList = 
            (List<String>)CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.IDENTIFIER_LIST);
         mIDList = 
            (List<String>)CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.ID_LIST);
         
         if ( mIdentiferList == null || mIDList == null )
         {
            UniqueIDSaxParser uniqueParser = new UniqueIDSaxParser();
            success = uniqueParser.performParse(iFile);
            
            if ( success )
            {
               mIdentiferList = uniqueParser.getIdentiferList();
               mIDList = uniqueParser.getIDList();
               
               //Store the lists in CheckerStateData for later use
               CheckerStateData.getInstance().
                  setObject(ValidatorKeyNames.IDENTIFIER_LIST, mIdentiferList);
               CheckerStateData.getInstance().
                  addReservedKey(ValidatorKeyNames.IDENTIFIER_LIST);
               
               CheckerStateData.getInstance().
                  setObject(ValidatorKeyNames.ID_LIST, mIDList);
               CheckerStateData.getInstance().
                  addReservedKey(ValidatorKeyNames.ID_LIST);
           }
         }
         
         // Prepare the schematron schema
         Document newSchematronSchema = parse(mSchematronSchema);         
         newSchematronSchema = fixImports( newSchematronSchema, mSchematronSkeletonSchema );         
         
         // Prepare the schematron rule file
         Document transform = transform(iSchematron, newSchematronSchema);
         
         // Clean up unused DOM objects
         newSchematronSchema = null;
         
         // Try to get JDom object of xml instance from CheckerStateData
         mXMLInstance = (Document)CheckerStateData.getInstance().getObjectValue(ValidatorKeyNames.XML_FILE_JDOM_KEY);
         if ( mXMLInstance == null )
         {
            // The instance was not in memory, parse and create it
            mXMLInstance = parse(iFile);            
            
            // Store the JDom tree for later use
            CheckerStateData.getInstance().setObject(ValidatorKeyNames.XML_FILE_JDOM_KEY, mXMLInstance);
            CheckerStateData.getInstance().addReservedKey(ValidatorKeyNames.XML_FILE_JDOM_KEY);
         }

         // Test to see if submanifest is present
         XPath subManifest = XPath.newInstance("imscp:manifest/imscp:manifest");         
         subManifest.addNamespace("imscp", IMSCP);
         int submanifestCount = subManifest.selectNodes(mXMLInstance).size();
         if ( submanifestCount > 0 )
         {
            mDoesSubmanifestExist = true;
         }
         
         // If XML is not to be validated including submanifests, remove the submanifests
         if ( !mValidateSubmanifest && mDoesSubmanifestExist )
         {
            Namespace ns = Namespace.getNamespace(IMSCP);
            mXMLInstance.getRootElement().removeChildren("manifest", ns);
         }
         
         // Validate the xml instance against the schematron rules
         List  messageList = transform(mXMLInstance, transform);
         
         // Clean up unused DOM objects
         mXMLInstance = null;
         transform = null;
         
         // Uncomment to output full schematron results
         // XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
         // out.output(validated, System.out);
         
         mErrorMessages.addAll(messageList);
         
         mErrorMessages.addAll(mReferencedObjectiveMessages);
         
         return success;
      }
      catch ( JDOMException jde )
      {
         jde.printStackTrace();
         mErrorMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
               Messages.getString("SchematronValidationDOMParser.5")));
         return false;
      }
      catch ( NullPointerException npe )
      {
         npe.printStackTrace();
         mErrorMessages.add(new ValidatorMessage(ValidatorMessage.FAILED,
                Messages.getString("SchematronValidationDOMParser.6")));
         return false;
      }
      // catch ( IOException ioe )
      // {
      //    ioe.printStackTrace();
      //    return false;
      // }
   }
   
   /**
    * This method parses a file to create a Document object
    * 
    * @param iFile The file to be parsed
    * @return A Document object containing the file
    */
   private Document parse(String iFile)
   {
      String decodedFile = URIHandler.decode(iFile, URIHandler.ENCODING);
      try
      {
         FileInputStream fis = new FileInputStream(decodedFile);
         return parse(fis);
      }
      catch ( IOException ioe )
      {
         //ioe.printStackTrace();
         String msg = 
            Messages.getString("SchematronValidationDOMParser.7");
         ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
         mErrorMessages.add(message);
         return null;
      }      
   }
   
   /**
    * This method parses an inputStream to create a Document object
    * 
    * @param iStream Is an inputStream connected to the file to be parsed
    * @return A Document object containing the file
    */
   private Document parse( InputStream iStream )
   {
      try
      {
         return mBuilder.build(iStream);
      }
      catch ( IOException ioe )
      {
         //ioe.printStackTrace();
         String msg = 
            Messages.getString("SchematronValidationDOMParser.7");
         ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
         mErrorMessages.add(message);
         return null;
      }
      catch ( JDOMException jde )
      {
         //jde.printStackTrace();
         String msg = 
            Messages.getString("SchematronValidationDOMParser.7");
         ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
         mErrorMessages.add(message);
         return null;
      }     
   }
   

   /**
    * This method will execute a transform on the given Document using the given
    * transform
    * 
    * @param iDoc The Document to be transformed
    * @param iTransform The transformed to be used to transform the document
    * @return A new Document object containing the transformed Document
    */
   private Document transform(InputStream iDoc, Document iTransform)
   {
      try
      {
         // Create sources from transform and xml instance
         StreamSource docSource = new StreamSource(iDoc);         
         JDOMSource transformSource = new JDOMSource(iTransform);
         
         // Create result to hold transformed xml document
         JDOMResult result = new JDOMResult();
         
         // Create and call a transformer from the saxon 8 parser
         TransformerFactoryImpl transformFactory = new TransformerFactoryImpl();
         Transformer tranformer = transformFactory.newTransformer(transformSource);
         
         tranformer.transform(docSource, result);
         
         return result.getDocument();
      }
      catch ( TransformerConfigurationException tce )
      {
         //tce.printStackTrace();
         String msg = 
            Messages.getString("SchematronValidationDOMParser.8");
         ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
         mErrorMessages.add(message);
         return null;
      }
      catch ( TransformerException te )
      {
         //te.printStackTrace();
         String msg = 
            Messages.getString("SchematronValidationDOMParser.8");
         ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
         mErrorMessages.add(message);
         return null;
      }
   }
   
   /**
    * This method will execute a transform on the given Document using the given
    * transform
    * 
    * @param iDoc The Document to be transformed
    * @param iTransform The transformed to be used to transform the document
    * @return A new Document object containing the transformed Document
    */
   private List transform(Document iDoc, Document iTransform)
   {
      try
      {
         // Create sources from transform and xml instance
         JDOMSource transformSource = new JDOMSource(iTransform);
         JDOMSource docSource = new JDOMSource(iDoc);
         
         // Create and call a transformer from the saxon 8 parser
         TransformerFactoryImpl transformFactory = new TransformerFactoryImpl();
         Transformer tranformer = transformFactory.newTransformer(transformSource);
         
         // Create ContentHandler to parse the schematron results
         SchematronResultContentHandler handler = new SchematronResultContentHandler();
         SAXResult transformResult = new SAXResult(handler);
         
         tranformer.transform(docSource, transformResult);
         
         return handler.getResultMessages();
      }
      catch ( TransformerConfigurationException tce )
      {
         //tce.printStackTrace();
         String msg = 
            Messages.getString("SchematronValidationDOMParser.8");
         ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
         mErrorMessages.add(message);
         return null;
      }
      catch ( TransformerException te )
      {
         //te.printStackTrace();
         String msg = 
            Messages.getString("SchematronValidationDOMParser.8");
         ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
         mErrorMessages.add(message);
         return null;
      }
   }
   
   /**
    * This method will create a local copy of a file from an inputStream
    * 
    * @param iFilePath An inputStream pointing to a file
    * @return A String containing the path of the local copy
    */
   private String createLocalCopy( InputStream iFilePath )   
   {      
      try
      {
         InputStream in = iFilePath;
         
         File outFile = File.createTempFile("iso_schematron_skeleton", ".xsl");
         outFile.deleteOnExit();
         FileOutputStream out = new FileOutputStream( outFile );
         
         byte[] buf = new byte[KILOBYTE];
         int len;
         while ((len = in.read(buf)) > 0)
         {
            out.write(buf, 0, len);
         }
         out.flush();
         in.close();
         out.close();
         
         return "file:///" + URLEncoder.encode(outFile.getAbsolutePath(), "UTF-8");
      }
      catch ( IOException ioe )
      {
         //ioe.printStackTrace();
         String msg = 
            Messages.getString("SchematronValidationDOMParser.7");
         ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
         mErrorMessages.add(message);
         return null;
      }
   }
   
   /**
    * This method will return a list of error messages
    * 
    * @return a List of error messages
    */
   public List<ValidatorMessage> getErrorMessages ( )
   {
      return mErrorMessages;
   }
   
   /**
    * This method returns a boolean indicating whether a submanifest is present
    * 
    * @return a boolean indicating whether a submanifest is present
    */
   public boolean isSubmanifestPresent()
   {
      return mDoesSubmanifestExist;
   }
   
   /**
    * This method will updated all imports from a given xml file to point to
    * equivalent local copies of the imports
    * 
    * @param iDoc The Document to be updated
    * @param iExternalSchema The location of the import item to be made local
    * @return The updated Document with local imports
    */
   protected Document fixImports(Document iDoc, InputStream iExternalSchema)
   {
      List imports; 
      try
      {
         XPath importPath = XPath.newInstance("xsl:stylesheet/xsl:import");
         importPath.addNamespace("xsl", "http://www.w3.org/1999/XSL/Transform");
         imports = importPath.selectNodes(iDoc);
      }
      catch (JDOMException jde)
      {
         return iDoc;
      }
      
      Iterator importIter = imports.iterator();
      
      while ( importIter.hasNext() )
      {
         Object o = importIter.next();
         Element e = (Element)o;
         Attribute a = e.getAttribute("href");

         String skeletonFilePath = createLocalCopy(iExternalSchema);
         a.setValue(skeletonFilePath);
      }

      return iDoc;
   }

   /**
    * This method searches resource nodes for a file element containing the
    * given href
    * 
    * @param iResourceID
    *           A String representing the resource on which we start the search
    * @return A boolean indicating if the href of the resource has been found on
    *         a file element
    */
   public static String checkHref(String iResourceID)
   {
      if ( iResourceID == null )
      {
         return "false";
      }
      
      mResourceList = new ArrayList();
      
      try
      {
         XPath resourcePath = XPath.newInstance("imscp:manifest/imscp:resources/imscp:resource[normalize-space(@identifier)='" + iResourceID.trim() + "']");
         resourcePath.addNamespace("imscp", IMSCP);
         List resources = resourcePath.selectNodes(mXMLInstance);

         // If the resource identifier, we do not want to test it
         if( resources.size() > 1 )
         {
            return "null";
         }
         
         // Get the href value of the resource
         Element resourceElement = (Element)resources.get(0);
         String href = resourceElement.getAttributeValue("href");

         String xmlBase = resourceElement.getAttributeValue("base", XML);

         if ( xmlBase == null )
         {
            xmlBase = "";
         }

         href = mURIHandler.escapeDirectories(xmlBase + href);

         // Remove any parameters if present
         href = ParameterHandler.removeParameters(href);
         
         if ( checkResource(iResourceID, href) )
         {
            return "true";
         }
         else
         {
            return "false";
         }

      }
      catch ( JDOMException jde )
      {
         jde.printStackTrace();
         return "false";
      }
   }

   /**
    * This method checks for the existence of the given href on a child file
    * element
    * 
    * @param iResource
    *           A String representing the resource element to be examined
    * @param iHref
    *           A String representing the href we are looking for
    * @return A boolean indicating if the given href has been found
    */
   public static boolean checkResource(String iResource, String iHref)   
   {
      try
      {
         // Check for circular dependency
         if ( mResourceList.contains(iResource) )
         {
            return false;
         }
         else
         {
            mResourceList.add(iResource);
         }
         
         XPath resourcePath = XPath.newInstance("imscp:manifest/imscp:resources/imscp:resource[normalize-space(@identifier)='" + iResource.trim() + "']");
         resourcePath.addNamespace("imscp", IMSCP);
         List resources = resourcePath.selectNodes(mXMLInstance);
         boolean matchFound = false;
         
         // Return false, all identifiers must be unique
         if ( resources.size() > 1 )
         {
            return false;
         }
         else
         {
            Element resourceElement = (Element)resources.get(0);
            
            String resourceXMLBase = resourceElement.getAttributeValue("base", XML);
            if ( resourceXMLBase == null )
            {
               resourceXMLBase = "";
            }
            
            
            // Check file hrefs
            XPath filePath = XPath.newInstance("imscp:file[@href]");
            filePath.addNamespace("imscp", IMSCP);
            List files = filePath.selectNodes(resourceElement);
            
            Iterator fileIter = files.iterator();
            while ( fileIter.hasNext() && !matchFound )
            {
               Element fileElement = (Element)fileIter.next();
               String fileHref = 
                  mURIHandler.escapeDirectories(resourceXMLBase + fileElement.getAttributeValue("href")); 
               if ( fileHref.equals(iHref) )
               {
                  matchFound = true;
               }
            }
            
            // Check for dependencies if no file match was found
            if ( !matchFound )
            {
               XPath dependencyPath = XPath.newInstance("imscp:dependency");
               dependencyPath.addNamespace("imscp", IMSCP);
               List dependencies = dependencyPath.selectNodes(resourceElement);
               
               // no dependencies and no match so the check has failed
               if ( dependencies.size() < 1 )
               {
                  return false;
               }
               // check dependencies
               else
               {
                  Iterator dependencyIter = dependencies.iterator();
                  boolean hrefLocated = false;
                  while ( dependencyIter.hasNext() )
                  {
                     String resourceID = ((Element)dependencyIter.next()).getAttributeValue("identifierref");
                     hrefLocated = checkResource(resourceID, iHref);
                     if ( hrefLocated )
                     {
                        break;                        
                     }
                  }
                  return hrefLocated;
               }
            }
            return matchFound;
         }         
      }
      catch ( JDOMException jde )
      {
         //jde.printStackTrace();
         return false;
      }
   }

   /**
    * static method called from Schematron to verify that the given ID is unique
    * 
    * @param iID
    *           The ID value to be validated
    * @return A boolean indicating whether or not the given ID is unique
    */
   public static boolean isIdentifierUnique( String iID )
   {
      if ( iID != null )
      {
         return !mIdentiferList.contains(iID);
      }
      else
      {
         return false;
      }
   }
   
   /**
    * static method called from Schematron to verify that the given identifier is unique
    * 
    * @param iID The identifier value to be validated
    * @return A boolean indicating whether or not the given identifier is unique
    */
   public static boolean isIDUnique( String iID )
   {
      if ( iID != null )
      {
         return !mIDList.contains(iID);
      }
      else
      {
         return false;
      }
   }
   
   /**
    * This method is called from the schematron transform to verify if the given
    * referencedObjectiveID correctly references an objective 
    * 
    * @param iRefObID - The referencedObjectiveID under the sequencingCollection/sequencing element
    * @param iID - The id of the sequencingCollection sequencing element
    * @return - A boolean indicating whether or not all referencedObjectiveIDS were used correctly
    */
   public static boolean isSQReferencedObjectiveValid( String iRefObID, String iID )
   {
      try
      {
         String refObjID = URIHandler.processWhitespace(iRefObID);
         
         // Get list of all sequencing elements which reference the given sequencingCollection
         XPath resourcePath = 
            XPath.newInstance(
                  "imscp:manifest/imscp:organizations/imscp:organization//" +
                  "imsss:sequencing[normalize-space(@IDRef)=normalize-space('" + iID + "')]");
         resourcePath.addNamespace("imscp", IMSCP);
         resourcePath.addNamespace("imsss", IMSSS);
         List sequencing = resourcePath.selectNodes(mXMLInstance);
         
        boolean result = true;
         
        if ( sequencing.size() > 0 )
        {
           Iterator seqIter = sequencing.iterator();
           while ( seqIter.hasNext() )
           {              
              Element e = (Element)seqIter.next();
              
              // Get just the objectives element regardless of objectiveID
              XPath objectivesPath = 
                 XPath.newInstance(
                       "imsss:objectives/imsss:primaryObjective[@objectiveID] | imsss:objectives/imsss:objective[@objectiveID]");
              objectivesPath.addNamespace("imscp", IMSCP);
              objectivesPath.addNamespace("imsss", IMSSS);
              List objectives = objectivesPath.selectNodes(e);
              
              Iterator objIter = objectives.iterator();
              boolean objFound = false;
              while ( !objFound && objIter.hasNext() )
              {
                 Element objElement = (Element)objIter.next();
                 String objID = URIHandler.processWhitespace(objElement.getAttributeValue("objectiveID"));
                 if ( objID.equals(refObjID) )
                 {
                    objFound = true;
                 }
              }
              
              // It has the objective we need, Pass
              if ( objFound )
              {
                 String msg = Messages.getString("SchematronValidationDOMParser.0", iRefObID, e.getParentElement().getAttributeValue("identifier"));
                 ValidatorMessage message = new ValidatorMessage(ValidatorMessage.PASSED, msg);
                 mReferencedObjectiveMessages.add(message);
              }
              // It has objectives but not the one we need, Fail
              else if ( objectives.size() > 0 )
              {
                 String msg = Messages.getString("SchematronValidationDOMParser.1", iRefObID, e.getParentElement().getAttributeValue("identifier"));
                 ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
                 mReferencedObjectiveMessages.add(message);
                 result = false;
              }
              // It has no objectives, look at the collection 
              else
              {
                 XPath sqObjectivesPath = 
                    XPath.newInstance(
                          "imscp:manifest/imsss:sequencingCollection/imsss:sequencing" +
                          "[normalize-space(@ID)=normalize-space('"+iID+"')]/imsss:objectives/imsss:objective[@objectiveID] | " +
                          "imscp:manifest/imsss:sequencingCollection/imsss:sequencing" +
                          "[normalize-space(@ID)=normalize-space('"+iID+"')]/imsss:objectives/imsss:primaryObjective[@objectiveID]");
                 sqObjectivesPath.addNamespace("imscp", IMSCP);
                 sqObjectivesPath.addNamespace("imsss", IMSSS);
                 List sqObjectives = sqObjectivesPath.selectNodes(mXMLInstance);
                 
                 Iterator sqObjIter = sqObjectives.iterator();
                 boolean sqObjFound = false;
                 while ( !sqObjFound && sqObjIter.hasNext() )
                 {
                    Element sqObjElement = (Element)sqObjIter.next();
                    String sqObjID = URIHandler.processWhitespace(sqObjElement.getAttributeValue("objectiveID"));
                    if ( sqObjID.equals(refObjID) )
                    {
                       sqObjFound = true;
                    }
                 }
                 
                 // The objective is on the collection, Pass
                 if ( sqObjFound )
                 {
                    String msg = Messages.getString("SchematronValidationDOMParser.2", iRefObID, iID);
                    ValidatorMessage message = new ValidatorMessage(ValidatorMessage.PASSED, msg);
                    mReferencedObjectiveMessages.add(message);
                 }
                 // The objective was not on the collection, Fail
                 else
                 {
                    String msg = Messages.getString("SchematronValidationDOMParser.1", iRefObID, e.getParentElement().getAttributeValue("identifier"));
                    ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
                    mReferencedObjectiveMessages.add(message);
                    result = false;
                    
                 }
              }
           }
           
        }
        // No elements reference this collection, check its referencedObjective value locally
        else 
        {
           XPath sqObjectivesPath = 
              XPath.newInstance(
                    "imscp:manifest/imsss:sequencingCollection/imsss:sequencing" +
                    "[normalize-space(@ID)=normalize-space('"+iID+"')]/imsss:objectives/imsss:objective[@objectiveID] | " +
                    "imscp:manifest/imsss:sequencingCollection/imsss:sequencing" +
                    "[normalize-space(@ID)=normalize-space('"+iID+"')]/imsss:objectives/imsss:primaryObjective[@objectiveID]");
           sqObjectivesPath.addNamespace("imscp", IMSCP);
           sqObjectivesPath.addNamespace("imsss", IMSSS);
           List sqObjectives = sqObjectivesPath.selectNodes(mXMLInstance);
           
           Iterator sqObjIter = sqObjectives.iterator();
           boolean sqObjFound = false;
           while ( !sqObjFound && sqObjIter.hasNext() )
           {
              Element sqObjElement = (Element)sqObjIter.next();
              String sqObjID = URIHandler.processWhitespace(sqObjElement.getAttributeValue("objectiveID"));
              if ( sqObjID.equals(refObjID) )
              {
                 sqObjFound = true;
              }
           }
           
           // Pass
           if ( sqObjFound )
           {
              String msg = Messages.getString("SchematronValidationDOMParser.2", iRefObID, iID);
              ValidatorMessage message = new ValidatorMessage(ValidatorMessage.PASSED, msg);
              mReferencedObjectiveMessages.add(message);
           }
           // Fail
           else
           {
              String msg = Messages.getString("SchematronValidationDOMParser.1", iRefObID, iID);
              ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
              mReferencedObjectiveMessages.add(message);
              result = false;
           }
        }
        
        return result;
      }
      catch ( JDOMException jde )
      {
         jde.printStackTrace();
         return false;
      }
   }
   
   /**
    * This method is called from the schematron transform to verify if the given
    * objectiveID correctly references an objective 
    * 
    * @param iAdlObID - The objectiveID under the sequencingCollection/sequencing element
    * @param iID - The id of the sequencingCollection sequencing element
    * @return - A boolean indicating whether or not all objectiveIDS were used correctly
    */
   public static boolean isSCObjectiveIDValid( String iAdlObID, String iID )
   {
      try
      {
         String adlObjID = URIHandler.processWhitespace(iAdlObID);
         
         // Get list of all sequencing elements which reference the given sequencingCollection
         XPath resourcePath = 
            XPath.newInstance(
                  "imscp:manifest/imscp:organizations/imscp:organization//" +
                  "imsss:sequencing[normalize-space(@IDRef)=normalize-space('" + iID + "')]");
         resourcePath.addNamespace("imscp", IMSCP);
         resourcePath.addNamespace("imsss", IMSSS);
         List sequencing = resourcePath.selectNodes(mXMLInstance);
         
        boolean result = true;
         
        if ( sequencing.size() > 0 )
        {
           Iterator seqIter = sequencing.iterator();
           while ( seqIter.hasNext() )
           {              
              Element e = (Element)seqIter.next();
              
              // Get just the objectives element regardless of objectiveID
              XPath objectivesPath = 
                 XPath.newInstance(
                       "imsss:objectives/imsss:primaryObjective[@objectiveID] | imsss:objectives/imsss:objective[@objectiveID]");
              objectivesPath.addNamespace("imscp", IMSCP);
              objectivesPath.addNamespace("imsss", IMSSS);
              List objectives = objectivesPath.selectNodes(e);
              
              Iterator objIter = objectives.iterator();
              boolean objFound = false;
              while ( !objFound && objIter.hasNext() )
              {
                 Element objElement = (Element)objIter.next();
                 String objID = URIHandler.processWhitespace(objElement.getAttributeValue("objectiveID"));
                 if ( objID.equals(adlObjID) )
                 {
                    objFound = true;
                 }
              }
              
              // It has the objective we need, Pass
              if ( objFound )
              {
                 String msg = Messages.getString("SchematronValidationDOMParser.3", adlObjID);
                 ValidatorMessage message = new ValidatorMessage(ValidatorMessage.PASSED, msg);
                 mReferencedObjectiveMessages.add(message);
              }
              // It has objectives but not the one we need, Fail
              else if ( objectives.size() > 0 )
              {
                 String msg = Messages.getString("SchematronValidationDOMParser.4", adlObjID);
                 ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
                 mReferencedObjectiveMessages.add(message);
                 result = false;
              }
              // It has no objectives, look at the collection 
              else
              {
                 XPath sqObjectivesPath = 
                    XPath.newInstance(
                          "imscp:manifest/imsss:sequencingCollection/imsss:sequencing" +
                          "[normalize-space(@ID)=normalize-space('"+iID+"')]/imsss:objectives/imsss:objective[@objectiveID] | " +
                          "imscp:manifest/imsss:sequencingCollection/imsss:sequencing" +
                          "[normalize-space(@ID)=normalize-space('"+iID+"')]/imsss:objectives/imsss:primaryObjective[@objectiveID]");
                 sqObjectivesPath.addNamespace("imscp", IMSCP);
                 sqObjectivesPath.addNamespace("imsss", IMSSS);
                 List sqObjectives = sqObjectivesPath.selectNodes(mXMLInstance);
                 
                 Iterator sqObjIter = sqObjectives.iterator();
                 boolean sqObjFound = false;
                 while ( !sqObjFound && sqObjIter.hasNext() )
                 {
                    Element sqObjElement = (Element)sqObjIter.next();
                    String sqObjID = URIHandler.processWhitespace(sqObjElement.getAttributeValue("objectiveID"));
                    if ( sqObjID.equals(adlObjID) )
                    {
                       sqObjFound = true;
                    }
                 }
                 
                 // The objective is on the collection, Pass
                 if ( sqObjFound )
                 {
                    String msg = Messages.getString("SchematronValidationDOMParser.3", adlObjID);
                    ValidatorMessage message = new ValidatorMessage(ValidatorMessage.PASSED, msg);
                    mReferencedObjectiveMessages.add(message);
                 }
                 // The objective was not on the collection, Fail
                 else
                 {
                    String msg = Messages.getString("SchematronValidationDOMParser.4", adlObjID);
                    ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
                    mReferencedObjectiveMessages.add(message);
                    result = false;
                    
                 }
              }
           }
           
        }
        // No elements reference this collection, check its referencedObjective value locally
        else 
        {
           XPath sqObjectivesPath = 
              XPath.newInstance(
                    "imscp:manifest/imsss:sequencingCollection/imsss:sequencing" +
                    "[normalize-space(@ID)=normalize-space('"+iID+"')]/imsss:objectives/imsss:objective[@objectiveID] | " +
                    "imscp:manifest/imsss:sequencingCollection/imsss:sequencing" +
                    "[normalize-space(@ID)=normalize-space('"+iID+"')]/imsss:objectives/imsss:primaryObjective[@objectiveID]");
           sqObjectivesPath.addNamespace("imscp", IMSCP);
           sqObjectivesPath.addNamespace("imsss", IMSSS);
           List sqObjectives = sqObjectivesPath.selectNodes(mXMLInstance);
           
           Iterator sqObjIter = sqObjectives.iterator();
           boolean sqObjFound = false;
           while ( !sqObjFound && sqObjIter.hasNext() )
           {
              Element sqObjElement = (Element)sqObjIter.next();
              String sqObjID = URIHandler.processWhitespace(sqObjElement.getAttributeValue("objectiveID"));
              if ( sqObjID.equals(adlObjID) )
              {
                 sqObjFound = true;
              }
           }
           
           // Pass
           if ( sqObjFound )
           {
              String msg = Messages.getString("SchematronValidationDOMParser.3", adlObjID);
              ValidatorMessage message = new ValidatorMessage(ValidatorMessage.PASSED, msg);
              mReferencedObjectiveMessages.add(message);
           }
           // Fail
           else
           {
              String msg = Messages.getString("SchematronValidationDOMParser.4", adlObjID);
              ValidatorMessage message = new ValidatorMessage(ValidatorMessage.FAILED, msg);
              mReferencedObjectiveMessages.add(message);
              result = false;
           }
        }
        
        return result;
      }
      catch ( JDOMException jde )
      {
         jde.printStackTrace();
         return false;
      }
   }
   
   
}
