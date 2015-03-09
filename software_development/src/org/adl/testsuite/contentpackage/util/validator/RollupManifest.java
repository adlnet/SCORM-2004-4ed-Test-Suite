package org.adl.testsuite.contentpackage.util.validator;

import org.adl.parsers.dom.DOMTreeUtility;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class will populate the manifest map and apply any xml:base elements
 * 
 * @author ADL Technical Team
 *
 */
public class RollupManifest
{
   /**
    * Document object of the test subject
    */
   Document mDocument;
   
   /**
    * Default Constructor.
    * 
    * @param iDocument - Document object from the file being tested.
    */
   public RollupManifest(Document iDocument)
   {
      mDocument = iDocument;
   }
   
   /**
    * This method is a control method which deals with rolling up sub-manifests.
    * It first populates a ManifestMap object and then calls processManifestMap.
    * It then rolls all resources in any sub-manifest to the root manifest, and
    * deletes any sub-manifest nodes in the DOM tree.
    *
    * @param isResPackage - Whether or not the package is a resource package.
    */
   public void rollupManifest( boolean isResPackage )
   {
      Node manifest = mDocument.getDocumentElement();
      ManifestMap manifestMap = new ManifestMap();

      manifestMap.populateManifestMap(manifest);
      applyXMLBase(manifest);
   }
   
   /**
    * This method will apply the value of any xml:base attributes of a root
    * manifest to any file elements in it's resource elements.
    *
    * @param iManifestNode - The root <code>&lt;manfiest$gt;</code> node of a 
    * manifest.
    */
   public void applyXMLBase( Node iManifestNode)
   {
      String x = "";
      String y = "";
      Node currentNode;
      String currentNodeName = "";
      String currentHrefValue = "";
      Attr currentHrefAttr = null;
      Node currentFileNode;
      String fileNodeName = "";
      String fileHrefValue = "";
      //Get base of manifest node
      x = getXMLBaseValue(iManifestNode);

      //get base of resources node
      Node resourcesNode = DOMTreeUtility.getNode( iManifestNode, "resources" );
      String resourcesBase = getXMLBaseValue(resourcesNode);
      if( (!x.equals( "" )) &&
          (!resourcesBase.equals( "" )) &&
          (!x.endsWith("/")) )
      {
         //x += File.separator;
         x += "/";
      }
      x += resourcesBase;

      NodeList resourceList = resourcesNode.getChildNodes();
      if( resourceList != null )
      {
         String resourceBase = "";
         for (int i = 0; i < resourceList.getLength(); i++)
         {
            currentNode = resourceList.item(i);
            currentNodeName = currentNode.getLocalName();

            //Apply to resource level
            if ( currentNodeName.equals("resource") )
            {
               resourceBase = getXMLBaseValue(currentNode);

               if( (!x.equals( "" )) &&
                   (!resourceBase.equals( "" )) &&
                   (!x.endsWith("/")) )
               {
                  //y = x + File.separator + resourceBase;
                   y = x + "/" + resourceBase;
               }
               else
               {
                  y = x + resourceBase;
               }

               currentHrefAttr = DOMTreeUtility.
                  getAttribute( currentNode, "href" );
               if( currentHrefAttr != null )
               {
                  currentHrefValue = currentHrefAttr.getValue();
                  if( (!y.equals( "" )) &&
                      (!currentHrefValue.equals( "" )) &&
                      (!y.endsWith("/")) )
                  {
                     currentHrefAttr.setValue( y + "/" + currentHrefValue );
                  }
                  else
                  {
                     currentHrefAttr.setValue( y + currentHrefValue );
                  }
               }

               NodeList fileList = currentNode.getChildNodes();
               if( fileList != null )
               {
                  for( int j = 0; j < fileList.getLength(); j++ )
                  {
                     currentFileNode = fileList.item(j);
                     fileNodeName = currentFileNode.getLocalName();
                     if( fileNodeName.equals("file") )
                     {
                        Attr fileHrefAttr = DOMTreeUtility.
                                        getAttribute( currentFileNode, "href" );
                        fileHrefValue = fileHrefAttr.getValue();
                        if( (!y.equals( "" )) &&
                            (!fileHrefValue.equals( "" )) &&
                            (!y.endsWith("/")) )
                        {
                           fileHrefAttr.setValue( y + "/" +
                                                  fileHrefValue );
                        }
                        else
                        {
                            fileHrefAttr.setValue( y + fileHrefValue );
                        }
                     }
                  }
               }
            }
         }
      }
   }
   
   /**
    * This method will find the xml:base attribute of the node passed into it
    * and return it if it has one, if it doesn't, it will return an empty
    * string.  If the node does have an xml:base attribute, this method will
    * also set that attribute to an empty string after retrieving it's value.
    *
    * @param iNode - the node whose xml:base attribute value is needed.
    * @return Returns the value of the xml:base attribute of this node.
    */
   public String getXMLBaseValue( Node iNode)
   {
      String result = "";

      if ( iNode != null )
      {
         Attr baseAttr = null;
         baseAttr = DOMTreeUtility.getAttribute( iNode, "base" );
         if( baseAttr != null )
         {
            result = baseAttr.getValue();
            DOMTreeUtility.removeAttribute( iNode, "xml:base" );
         }
      }
      return result;
   }
}
