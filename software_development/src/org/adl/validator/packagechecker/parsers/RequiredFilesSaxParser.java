package org.adl.validator.packagechecker.parsers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.adl.validator.util.ValidatorMessage;
import org.xml.sax.Attributes;

/**
 * This parser will obtain information needed to create a list of files
 * required to validate the xml instance
 * 
 * @author ADL Technical Team
 *
 */
public class RequiredFilesSaxParser extends ValidatorSaxParser
{   
   /**
    * String representing the current element on which the parse is located
    */
   protected String mCurrentElement;
   
   /**
    * String representing the name-space of the current element
    */
   protected String mCurrentElementNS;
   
   /**
    * A String containing the text value of the element
    */
   private String mCharData;
   
   /**
    * Contains a Set of all used name-spaces
    */
   final private Set<String> mNamespaceURIs;
   
   /**
    * Holds the number of manifest elements found in the XML file 
    * ( > 1 equals a sub-manifest )
    */
   private int mManifestCount;
   
   /**
    * Contains a set of meta-data files listed in adlcp:location
    */
   final private Set<String> mMetadataFiles;
   
   /**
    * Contains the value of the schemaLocation element
    */
   private String mSchemaLocation;
   
   /**
    * Contains the value of the noSchemaLocation element
    */
   private String mNoSchemaLocation;
      
   /**
    * Array to hold the three possible xml:base values which can be applied to
    * a given HREF value
    */
   private String[] mXMLBase;

   /**
    * This list will act as a tree to hold the parents of the given elements
    */
   final private List<String> mElementList;
   
   /**
    * The default constructor
    */
   public RequiredFilesSaxParser()
   {
      super();
      mParseSuccess = true;
      mParseMessages = new ArrayList<ValidatorMessage>();
      mMetadataFiles = new HashSet<String>();
      this.configureParser();
      mXMLBase = new String[BASE_SIZE];
      mXMLBase[BASE_ONE] = "";
      mXMLBase[BASE_TWO] = "";
      mXMLBase[BASE_THREE] = "";
      mElementList = new ArrayList<String>();
      
      mNamespaceURIs = new HashSet<String>();
      
      mSchemaLocation = "";
      mNoSchemaLocation = "";
      mCharData = "";
   }
   
   /**
    * Obtains the set of all used names-paces
    * 
    * @return a Set of all used name-spaces
    */
   public Set<String> getNamespaceURIs()
   {
      return mNamespaceURIs;
   }
   
   /**
    * Obtains the value of the noSchemaLocation element
    * 
    * @return a String containing the value of the schemaLocation element
    */
   public String getNoSchemaLocation()
   {
      return mNoSchemaLocation.replaceAll("\\b\\s{2,}\\b", " ").trim();
   }

   /**
    * Obtains the value of the schemaLocation element
    * 
    * @return a String containing the value of the noSchemaLocation element
    */
   public String getSchemaLocation()
   {
      return mSchemaLocation.replaceAll("\\b\\s{2,}\\b", " ").trim();
   }
   
   /**
    * Obtains a list of all meta-data files referenced in the XML instance
    * 
    * @return the Set of meta-data files referenced in the XML instance
    */
   public Set<String> getMetadataFiles()
   {
      return mMetadataFiles;
   }

   /** 
    * This method will analyze the attributes, names, and name-space of a given element
    * 
    * @param iNamespaceURI Is a String value which holds the name-space of the element
    * @param iLocalName Is a String value containing the local name of the element
    * @param iRawName Is a String value containing the raw name of the element
    * @param iAttrs Is a Attributes value containing the elements attributes
    */ 
   public void startElement(final String iNamespaceURI, final String iLocalName, 
         final String iRawName, final Attributes iAttrs) 
   {         
      // Add element name-space to Set
      // Ignore http://www.w3.org/2001/XMLSchema-instance name-space
      // as it only defines the schemaLocation or noSchemaLocation element

      // add current element to parent list
      mElementList.add(iRawName);

      mCurrentElement = iRawName;
      mCurrentElementNS = iNamespaceURI;

      if ( iLocalName.equals(MANIFEST) && iNamespaceURI.equals(IMSCP) )
      {
         mManifestCount++;
      }

      String xmlBase = "";


      if ( !"".equals(iNamespaceURI) && !iNamespaceURI.equals(XSI))
      {
         mNamespaceURIs.add(iNamespaceURI);
      }

      // Only get the XML:bases from the main manifest because they only apply to metadata which
      // we only get from the main manifest
      if ( iLocalName.equals(RESOURCE) && iNamespaceURI.equals(IMSCP) && mManifestCount <= 1 )
      {
         mXMLBase[BASE_THREE] = "";
      }

      if (iAttrs != null)
      {            
         final int len = iAttrs.getLength();
         for (int i = 0; i < len; i++)
         {               
            if ( iAttrs.getLocalName(i).equals("base") && iAttrs.getURI(i).equals(XML))
            {
               xmlBase = iAttrs.getValue(i);

               if ( iLocalName.equals(MANIFEST) && iNamespaceURI.equals(IMSCP) && mManifestCount <= 1 )
               {
                  mXMLBase[BASE_ONE] = xmlBase;
               }
               else if ( iLocalName.equals(RESOURCES) && iNamespaceURI.equals(IMSCP) && mManifestCount <= 1 )
               {
                  mXMLBase[BASE_TWO] = xmlBase;
               }
               else if ( iLocalName.equals(RESOURCE) && iNamespaceURI.equals(IMSCP) && mManifestCount <= 1 )
               {
                  mXMLBase[BASE_THREE] = xmlBase;              
               }

               xmlBase = "";
            }

            // Save schemaLocation to variable
            if ( iAttrs.getLocalName(i).equals("schemaLocation") && iAttrs.getURI(i).equals(XSI) )
            {
               mSchemaLocation = mSchemaLocation + iAttrs.getValue(i) + " ";
            }

            // Save noSchemaLocation to variable
            if ( iAttrs.getLocalName(i).equals("noSchemaLocation") && iAttrs.getURI(i).equals(XSI) )
            {
               mNoSchemaLocation = mNoSchemaLocation + iAttrs.getValue(i) + " ";
            }

            // Add attribute name-space to Set
            if ( !iAttrs.getURI(i).equals("") &&
                  !iAttrs.getURI(i).equals("http://www.w3.org/2001/XMLSchema-instance") && 
                  !iAttrs.getURI(i).equals("http://www.w3.org/XML/1998/namespace"))
            {
               mNamespaceURIs.add(iAttrs.getURI(i));
            }
         }
      }
   }

   /** 
    * This method is called when we reach the end tag of an element
    * 
    * @param iNamespaceURI Is a String value which holds the name-space of the element
    * @param iLocalName Is a String value containing the local name of the element
    * @param iName Is a String value containing the raw name of the element
    */
   public void endElement(final String iNamespaceURI, final String iLocalName, final String iName)
   {
      // Remove last element added to element list
      mElementList.remove(mElementList.size() - 1);
      // The element is done, clear it out
      mCurrentElement = "";
      
      // If character data exists, add the path to the list
      if ( !"".equals(mCharData) )
      {
         mCharData = mXMLBase[BASE_ONE] + mXMLBase[BASE_TWO] + mXMLBase[BASE_THREE] + mCharData;
         mMetadataFiles.add(mCharData);
         mCharData = "";
      }
   }
   
   /** Characters. */
   /** This method will obtain the value of an element during a given parse
    * 
    * @param iCh Contains the element value
    * @param iStart Contains the starting index
    * @param iLength Contains the value length
    * 
    */
   public void characters(final char iCh[], final int iStart, final int iLength) 
   {
      // We only want the metadata files for the main manifest b/c we ignore submanifests
      if ( ( mManifestCount <= 1 ) && 
           ( "adlcp:location".equals(mCurrentElement) || ( mCurrentElement.equals(LOCATION) 
                  && mCurrentElementNS.equals(ADLCP) ) ) &&  
           ( mElementList.size() > 1 && 
                 mElementList.get(mElementList.size()-2).toString().equals("metadata")))
      {
         // Make sure adlcp:location is under meta-data before adding it to the list
         // We must have at least 2 elements in the list, the element itself and its
         // parent.  If the count < 2, then the current element has no parent so we 
         // cannot check it.
         mCharData = mCharData + (new String(iCh, iStart, iLength));
      }     
   }
}
