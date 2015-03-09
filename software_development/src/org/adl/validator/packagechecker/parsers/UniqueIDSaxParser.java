package org.adl.validator.packagechecker.parsers;

import java.util.ArrayList;
import java.util.List;

import org.adl.validator.util.ValidatorMessage;
import org.xml.sax.Attributes;

/**
 * This parser will obtain information needed to create a list of files
 * required to validate the xml instance
 * 
 * @author ADL Technical Team
 *
 */
public class UniqueIDSaxParser extends ValidatorSaxParser
{
   /**
    * A list containing all identifier attributes found in the XML instance
    */
   private List<String> mIdentiferList;
   
   /**
    * A list containing all sequencing element ID attributes found in the XML instance
    */
   private List<String> mIDList;
   
   /**
    * A List of identifier values which appear more than once in the manifest
    */
   private List<String> mIdentifierDoubles;
   
   /**
    * A List of ID values which appear more than once in the manifest
    */
   private List<String> mIDDoubles;
   
   /**
    * The default constructor
    */
   public UniqueIDSaxParser()
   {
      mIdentiferList = new ArrayList<String>();
      mIDList = new ArrayList<String>();
      mIdentifierDoubles = new ArrayList<String>();
      mIDDoubles = new ArrayList<String>();
      
      mParseSuccess = true;
      mParseMessages = new ArrayList<ValidatorMessage>();
      this.configureParser();
   }
   
   /** 
    * This method will analyze the attributes, names, and namespace of a given element
    * 
    * @param iNamespaceURI Is a String value which holds the namespace of the element
    * @param iLocalName Is a String value containing the local name of the element
    * @param iRawName Is a String value containing the raw name of the element
    * @param iAttrs Is a Attributes value containing the elements attributes
    */ 
   public void startElement(String iNamespaceURI, String iLocalName, 
         String iRawName, Attributes iAttrs) 
   {   
      // We will include sub-manifests in the identifier / ID checking
      
      boolean isManifest = mFileName.indexOf("imsmanifest.xml") != -1;
      
      String value = "";
      
      if ( isManifest )
      {
         if ( ( iLocalName.equals("manifest") && iNamespaceURI.equals(IMSCP)) || 
              ( iLocalName.equals("organization") && iNamespaceURI.equals(IMSCP)) ||
              ( iLocalName.equals("item") && iNamespaceURI.equals(IMSCP)) ||
              ( iLocalName.equals("resource") && iNamespaceURI.equals(IMSCP)) )
         {
            value = iAttrs.getValue("identifier");
            if ( value != null )
            {
               if ( mIdentiferList.contains(value) )
               {
                  mIdentifierDoubles.add(value);
               }
               else
               {
                  mIdentiferList.add(value);
               }
               value = null;
            }
         }
         else if ( iLocalName.equals("sequencing") && iNamespaceURI.equals(IMSSS) )
         {
            value = iAttrs.getValue("ID");
            if ( value != null )
            {
               if ( mIDList.contains(value) )
               {
                  mIDDoubles.add(value);
               }
               else
               {
                  mIDList.add(value);
               }
               value = null;
            }
         }
      }
   }

   /**
    * This method will return the list of manifest identifiers
    * 
    * @return List of manifest identifier attribute values
    */
   public List<String> getIdentiferList()
   {
      return mIdentifierDoubles;
   }

   /**
    * This method will return the list of manifest IDs
    * 
    * @return List of manifest ID attribute values
    */
   public List<String> getIDList()
   {
      return mIDDoubles;
   }
}
