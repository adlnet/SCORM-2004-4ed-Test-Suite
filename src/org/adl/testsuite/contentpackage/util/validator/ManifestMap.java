package org.adl.testsuite.contentpackage.util.validator;

// native java imports
import java.util.Vector;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.adl.parsers.dom.DOMTreeUtility;
import org.adl.util.LogMessage;
import org.adl.util.MessageType;
import org.adl.logging.DetailedLogMessageCollection;
import org.adl.util.Messages;

/**
 *
 * <strong>Filename: </strong><br>ManifestMap.java<br><br>
 *
 * <strong>Description: </strong><br> A <code>ManifestMap</code> is a Data
 * Structure used to store manifest information that is necessary for for the
 * validation and processing of manifests.
 *
 * @author ADL Technical Team
 */

public class ManifestMap
{
   /**
    * Logger object used for debug logging.
    */
   final static private Logger LOGGER = Logger.getLogger("org.adl.util.debug.validator"); ;

   /**
    * The identifier attribute of the &lt;manifest&gt; element.
    */
   private String mManifestId;

   /**
    * The identifier attributes of all &lt;resource&gt; elements that belong to the
    * &lt;manifest&gt; element of mManifestId.
    */
   private Vector mResourceIds;

   /**
    * The identifier attributes of all &lt;item&gt; elements that belong to the
    * &lt;manifest&gt; element of mManifestId.
    */
   private Vector mItemIds;

   /**
    * The identifier reference values of all &lt;item&gt; elements that belong to the
    * &lt;manifest&gt; element of mManifestId.
    */
   private Vector mItemIdrefs;

   /**
    * The identifier reference values of all &lt;dependency&gt; elements that belong to
    * the &lt;manifest&gt; element of mManifestId.
    */
   private Vector mDependencyIdrefs;


   /**
    * The identifier determining what type of manifest is to be validated
    */
   private String mApplicationProfile;


   /**
    * The default constructor.
    */
   public ManifestMap()
   {
      mManifestId = "";
      mResourceIds                  = new Vector();
      mItemIds                      = new Vector();
      mItemIdrefs                   = new Vector();
      mApplicationProfile = "";
      mDependencyIdrefs             = new Vector();
   }


   /**
    * Gives access to the identifier value of the &lt;manifest&gt; element.
    *
    * @return - The identifier value of the &lt;manifest&gt; element.
    */
   public String getManifestId()
   {
      return mManifestId;
   }

   /**
    * Gives access to the identifier attributes of all &lt;resource&gt; elements that
    * belong to the &lt;manifest&gt; element of mManifestId.
    *
    * @return - The identifier attributes of all &lt;resource&gt; elements that
    * belong to the &lt;manifest&gt; element of mManifestId.
    */
   public Vector getResourceIds()
   {
      return mResourceIds;
   }

   /**
    * Gives access to the identifier attributes of all &lt;item&gt; elements that
    * belong to the &lt;manifest&gt; element of mManifestId.
    *
    * @return - The identifier attributes of all &lt;item&gt; elements that
    * belong to the &lt;manifest&gt; element of mManifestId.
    */
   public Vector getItemIds()
   {
      return mItemIds;
   }

   /**
    * Gives access to the identifier reference values of all &lt;item&gt; elements
    * that belong to the &lt;manifest&gt; element of mManifestId.
    *
    * @return - The identifier reference values of all &lt;item&gt; elements that
    * belong to the &lt;manifest&gt; element of mManifestId.
    */
   public Vector getItemIdrefs()
   {
      return mItemIdrefs;
   }

   /**
    * Gives access to the identifier reference values of all &lt;dependency&gt;
    * elements that belong to the &lt;manifest&gt; element of mManifestId.
    *
    * @return - The identifier reference values of all &lt;dependency&gt; elements
    * that belong to the &lt;manifest&gt; element of mManifestId.
    */
   public Vector getDependencyIdrefs()
   {
      return mDependencyIdrefs;
   }

   /**
    * Gives access to the String describing which Application Profile the
    * manifest adheres to.
    *
    * @return - The String describing the app profile of the manifest.
    */
   public String getApplicationProfile()
   {
      return mApplicationProfile;
   }

   /**
    * Gives access to the String describing which Application Profile the
    * manifest adheres to.
    *
    * @param iApplicationProfile The indicator of the Application Profile
    */
   public void setApplicationProfile( String iApplicationProfile )
   {
      mApplicationProfile = iApplicationProfile;
   }

   /**
    * This method populates the ManifestMap object by traversing down
    * the document node and storing all information necessary for the validation
    * of manifests.  Information stored for each manifest element includes:
    * manifest identifiers,item identifers, item identifierrefs, and
    * resource identifiers
    *
    * @param iNode the node being checked. All checks will depend on the type of node
    * being evaluated
    * 
    * @return - The boolean describing if the ManifestMap object(s) has been
    * populated properly.
    */
   public boolean populateManifestMap( Node iNode )
   {
      // looks exactly like prunetree as we walk down the tree
      LOGGER.entering( "ManifestMap", "populateManifestMap" );  

      boolean result = true;

      // is there anything to do?
      if ( iNode == null )
      {
         result = false;
         return result;
      }

      int type = iNode.getNodeType();

      switch ( type )
      {
         case Node.PROCESSING_INSTRUCTION_NODE:
         {
            break;
         }
         case Node.DOCUMENT_NODE:
         {
            Node rootNode = ((Document)iNode).getDocumentElement();

            result = populateManifestMap( rootNode ) && result;

            break;
         }
         case Node.ELEMENT_NODE:
         {
            String parentNodeName = iNode.getLocalName();

            if ( parentNodeName.equalsIgnoreCase("manifest") ) 
            {
               // We are dealing with an IMS <manifest> element, get the IMS
               // CP identifier for the <manifest> elememnt
               mManifestId =
                  DOMTreeUtility.getAttributeValue( iNode,
                                                    "identifier" ); 

               LOGGER.finest( "ManifestMap:populateManifestMap, " + 
                               "Just stored a Manifest Id value of " + 
                                mManifestId );

               // Recurse to populate mItemIdrefs and mItemIds

               // Find the <organization> elements

               Node orgsNode = DOMTreeUtility.getNode( iNode, "organizations" ); 

               if( orgsNode != null )
               {
                  Vector orgElems = DOMTreeUtility.getNodes( orgsNode, "organization" ); 

                  LOGGER.finest( "ManifestMap:populateManifestMap, " + 
                                  "Number of <organization> elements: " + 
                                   orgElems.size() );

                  if ( !orgElems.isEmpty() )
                  {
                     int orgElemsSize = orgElems.size();
                     for (int i = 0; i < orgElemsSize; i++ )
                     {
                        Vector itemElems = DOMTreeUtility.getNodes(
                                            (Node)orgElems.elementAt(i), "item" ); 

                        LOGGER.finest( "ManifestMap:populateManifestMap, " + 
                                        "Number of <item> elements: " + 
                                         itemElems.size() );

                        if ( !itemElems.isEmpty() )
                        {
                           int itemElemsSize = itemElems.size();
                           for (int j = 0; j < itemElemsSize; j++ )
                           {
                              result = populateManifestMap(
                                 (Node)(itemElems.elementAt(j)) ) && result;
                           }
                        }
                     }
                  }
               }

               //recurse to populate mResourceIds

               Node resourcesNode = DOMTreeUtility.getNode( iNode, "resources" ); 

               if( resourcesNode != null )
               {
                  Vector resourceElems = DOMTreeUtility.getNodes(
                                                  resourcesNode, "resource" ); 

                  LOGGER.finest( "ManifestMap:populateManifestMap, " + 
                               "Number of <resource> elements: " + 
                                resourceElems.size() );

                  int resourceElemsSize = resourceElems.size();
                  for (int k = 0; k < resourceElemsSize; k++ )
                  {
                     result = populateManifestMap(
                                 (Node)(resourceElems.elementAt(k)) ) && result;
                  }
               }
            }
            else if ( parentNodeName.equalsIgnoreCase("item") ) 
            {
               //store item identifier value
               String itemId =
                        DOMTreeUtility.getAttributeValue( iNode, "identifier" );
               
               mItemIds.add( itemId );

               LOGGER.finest( "ManifestMap:populateManifestMap, " + 
                                  "Just stored an Item Id value of " + 
                                   itemId );

               //store item identifier reference value
               String itemIdref =
                     DOMTreeUtility.getAttributeValue( iNode, "identifierref" );
               
               mItemIdrefs.add( itemIdref );

               LOGGER.finest( "ManifestMap:populateManifestMap, " + 
                                  "Just stored an Item Idref value of " + 
                                   itemIdref );

               //recurse to populate all child item elements
               Vector items = DOMTreeUtility.getNodes( iNode, "item" ); 
               if ( !items.isEmpty() )
               {
                  int itemsSize = items.size();
                  for ( int z = 0; z < itemsSize; z++ )
                  {
                     result = populateManifestMap(
                        (Node)items.elementAt(z) ) && result;
                  }
               }
            }
            else if ( parentNodeName.equalsIgnoreCase("resource") ) 
            {
               //store resource identifier value
               String resourceId =
                        DOMTreeUtility.getAttributeValue( iNode, "identifier" ); 
               // convert to lower so case sensativity does not play a role
               
               mResourceIds.add( resourceId  );

               LOGGER.finest( "ManifestMap:populateManifestMap, " + 
                                  "Just stored a Resource Id value of " + 
                                   resourceId );

               // populate <dependency> element

               Vector dependencyElems = DOMTreeUtility.getNodes( iNode,
                                                                 "dependency" ); 

               int dependencyElemsSize= dependencyElems.size();

               for(int w=0; w < dependencyElemsSize; w++ )
               {
                  Node dependencyElem = (Node)dependencyElems.elementAt(w);

                  //store resource identifier value
                  String dependencyIdref =
                        DOMTreeUtility.getAttributeValue( dependencyElem,
                                                          "identifierref" ); 
                  
                  mDependencyIdrefs.add( dependencyIdref );

                  LOGGER.finest( "ManifestMap:populateManifestMap, " + 
                                     "Just stored a Dependency Idref value of " + 
                                      mDependencyIdrefs );
               }
            }

            break;
         }
         // handle entity reference nodes
         case Node.ENTITY_REFERENCE_NODE:
         {
            break;
         }

         // text
         case Node.COMMENT_NODE:
         {
            break;
         }

         case Node.CDATA_SECTION_NODE:
         {
            break;
         }

         case Node.TEXT_NODE:
         {
            break;
         }
         
         default:
         {
            break;
         }
      }

      LOGGER.exiting( "ManifestMap", "populateManifestMap" );  

      return result;
   }

   /**
    * This method drives the recursive validation of the referencing of
    * identifierref values.  It spans the validation of identifierrefs for
    * each identifierref value.
    *
    * @return - The Vector containing the identifierref value(s) that do not
    * reference valid identifers.
    *
    */
   public Vector checkAllIdReferences()
   {
     Vector resultVector = new Vector();
     String msgText = "";
     String idrefValue = "";
     boolean iItemdrefResult = false;

     if ( !mItemIdrefs.isEmpty() )
     {
        int mItemIdrefsSize = mItemIdrefs.size();
        for ( int i = 0; i < mItemIdrefsSize; i++ )
        {
           idrefValue = (String)mItemIdrefs.elementAt(i);

           if ( !idrefValue.equals("") ) 
           {
              msgText = Messages.getString("ManifestMap.40", idrefValue ); 
              LOGGER.info( "INFO: " + msgText ); 
              DetailedLogMessageCollection.getInstance().addMessage( 
                                    new LogMessage( MessageType.INFO, msgText ) );

              iItemdrefResult = checkIdReference( idrefValue );

              // track all idref values whose reference was not valid

              if ( !iItemdrefResult )
              {
                 msgText = Messages.getString("ManifestMap.43", idrefValue ); 
                 LOGGER.info( "FAILED: " + msgText ); 
                 DetailedLogMessageCollection.getInstance().addMessage( new LogMessage(
                    MessageType.FAILED, msgText ) );

                 resultVector.add( idrefValue );
              }
           }
        }
     }

     if ( !mDependencyIdrefs.isEmpty() )
     {
        int mDependencyIdrefsSize = mDependencyIdrefs.size();
        for ( int i = 0; i < mDependencyIdrefsSize; i++ )
        {
           idrefValue = (String)mDependencyIdrefs.elementAt(i);

           if ( !idrefValue.equals("") ) 
           {
              msgText = Messages.getString("ManifestMap.40", idrefValue ); 
              LOGGER.info( "INFO: " + msgText ); 
              DetailedLogMessageCollection.getInstance().addMessage( new LogMessage( MessageType.INFO,
                                                              msgText ) );
              
              boolean iDependencydrefResult = checkIdReference( idrefValue );

              // track all idref values whose reference was not valid

              if ( !iDependencydrefResult )
              {
                 msgText = Messages.getString("ManifestMap.43", idrefValue ); 
                 LOGGER.info( "FAILED: " + msgText ); 
                 DetailedLogMessageCollection.getInstance().addMessage( new LogMessage(
                    MessageType.FAILED, msgText ) );

                 resultVector.add( idrefValue );
              }
           }
        }
     }

     return resultVector;
   }

   /**
    * This method validates that the incoming identifierref value properly
    * references a valid identifier.  An error is thrown for identifierref
    * values that perform backwards or sideward referencing, or does not
    * reference an identifier value at all.
    *
    * @param iIdref the identifier reference being checked
    * 
    * @return - The Vector containing the identifierref value(s) that do not
    * reference valid identifers.
    *
    */
   public boolean checkIdReference( String iIdref )
   {
      boolean result = false;
      String msgText = ""; 

      // loop through resourceIds and compare to incoming idref value
      if ( !mResourceIds.isEmpty() )
      {
         int mResourceIdsSize = mResourceIds.size();
         for ( int i = 0; i < mResourceIdsSize; i++ )
         {
            String resourceId = (String)mResourceIds.elementAt(i);
            msgText = "Comparing " + iIdref + " to " + resourceId;
            LOGGER.info( msgText );

            if ( iIdref.equals( resourceId ) )
            {
               result = true;

               msgText = Messages.getString("ManifestMap.55", iIdref);
               LOGGER.info( "PASSED: " + msgText ); 
               DetailedLogMessageCollection.getInstance().addMessage( new LogMessage(
                                                            MessageType.PASSED,
                                                            msgText ) );
               // set application profile to other only if it does not already
               // equal content aggregation.  Other triggers the need for
               // an additional check that will allow 0 or more orgs.
               String currentAppProfile = getApplicationProfile();

               if ( !currentAppProfile.equals("contentaggregation") )
               {
                  setApplicationProfile("other"); 
               }
               else
               {
                  setApplicationProfile("contentaggregation"); 
               }

               msgText = "IDRef " + iIdref + " points to a resource " +  
                          resourceId + " , app profile is " + 
                          getApplicationProfile() + " for " + getManifestId(); 
               LOGGER.info(msgText);

               break;
            }
         }
      }

      msgText = "Returning " + result + "from checkIdReference";
      LOGGER.info( msgText );

      return result;
   }
}