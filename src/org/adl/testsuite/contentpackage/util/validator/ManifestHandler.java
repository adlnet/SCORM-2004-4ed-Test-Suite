package org.adl.testsuite.contentpackage.util.validator;

// native java imports
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

// xerces imports
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

// adl imports
import org.adl.parsers.dom.DOMTreeUtility;
import org.adl.util.decode.decodeHandler;


/**
 * <strong>Filename: </strong><br>ManifestHandler.java<br><br>
 *
 * <strong>Description: </strong>This method tracks, stores and retrieves  the
 * Launch Data information of SCOs and the Metadata information, all of which is
 * found or referenced from within the content package test subject.
 *
 * @author ADL Technical Team
 */
public class ManifestHandler
{
   /**
    * This attribute serves as the Logger object used for debug logging.
    */
   final static private Logger LOGGER  = Logger.getLogger("org.adl.util.debug.validator");

   /**
    * This attribute describes whether or not SCO launch data has been
    * tracked.
    */
   private boolean mLaunchDataTracked;

   /**
    * This attribute describes whether or not the metadata information was
    * tracked. 
    */
   private boolean mMetadataTracked;

   /**
    * This attribute serves as the storage list of the tracked metadata
    * information. This list will contain the following information:  If inline
    * metadata, than the root node will be stored here along with the
    * metadata application profile type.  If external metadata, than the URI to
    * the metadata will be stored along with the metadata application profile
    * type.
    */
   private Vector mMetadataDataList;

   /**
    * This attribute serves as the storage list of the tracked SCO launch data.
    * This list uses the default organization and does not conform with given
    * sequencing rules.  This list can be used for default behavior and
    * testing purposes.
    */
   private Vector mLaunchDataList;

   /**
    * This attribute contains the xml:base value created from the &lt;manifest&gt;
    * and &lt;resources&gt; elements.
    *
    */
   private String mManifestResourcesXMLBase;

   /**
    * This attribute contains the xml:base value created from each &lt;resource&gt;
    * element.  It will complete the xml:base value after being appended to
    * the mManifestResourceXMLBase attribute.
    */
   private String mResourceXMLBase;

   /**
    * This attribute contains a list of the metadata as referenced in the 
    * IMS Manifest by the adlcp:location element.  
    */
   private Vector mLocationList;
   
   /**
    * used in all areas where an empty string was checked for, or a string was set to ""
    */
   private String mEmptyString = "";

   /**
    * Default Constructor.  Sets the attributes to their initial values.
    */
   public ManifestHandler()
   {
      mLaunchDataTracked = false;
      mMetadataTracked = false;
      mMetadataDataList = new Vector();
      mLaunchDataList = new Vector();
      mManifestResourcesXMLBase = "";
      mResourceXMLBase = "";
      mLocationList = new Vector();
   }

   /**
    * This method initiates the retrieval of the SCO launch data
    * information, if this information exists in the content package test
    * subject.
    *
    * @param iRootNode root node manipulated for retrieval of launch data.
    * 
    * @param iDefaultOrganizationOnly boolean describing the scope of the
    *        organization that should be traversed for SCO launch data. Specific 
    *        to SRTE uses - will no longer be needed in future development.
    * 
    * @param iRemoveAssets boolean describing whether or not to remove assets
    *        from the LaunchData list.  The SRTE needs this to be false in
    *        in order to import assets as well.
    *
    * @return Vector containing the launch data information for SCOs.
    */
   public Vector getLaunchData( Node iRootNode,
                                boolean iDefaultOrganizationOnly,
                                boolean iRemoveAssets )
   {
      if ( ! mLaunchDataTracked )
      {
         setLaunchData( iRootNode, iDefaultOrganizationOnly, iRemoveAssets );
      }
      return mLaunchDataList;
   }

   /**
    * This method initiates the retrieval of the metadata information,
    * if this information exists in the content package test subject.
    *
    * @param iRootNode root node manipulated for retrieval of metadata info.
    *
    * @param iBaseDirectory base directory for location of test subject
    *
    * @return Vector containing the metadata information.
    */
   public Vector getMetadata( Node iRootNode,
                              String iBaseDirectory )
   {
      if ( ! mMetadataTracked )
      {
         setMetadata( iRootNode, iBaseDirectory );
      }

      return mMetadataDataList;
   }

   /**
    * This method initiates the retrieval of a list of adlcp:location element 
    * values only.
    * 
    * @param iRootNode root node manipulated for retrieval of adlcp:location
    * metadata info.
    * 
    * @return Vector containing the adlcp:location metadata information.
    */
   public Vector getLocationMD( Node iRootNode )
   {
       checkForAdlcpLocationMD( iRootNode );
       return mLocationList;
   }

   /**
    * This method performs the actual retrieval of the SCO launch data
    * information, if this information exists in the content package test
    * subject.  This method walks through the test subject dom, storing all the
    * SCO launch data information to the LaunchData data structure.
    *
    * @param iRootNode root node of test subject dom.
    * 
    * @param iDefaultOrganizationOnly boolean describing the scope of the
    *        organization that should be traversed for SCO launch data. Specific 
    *        to SRTE uses - will no longer be needed in future development.
    * 
    * @param iRemoveAssets boolean describing whether or not to remove assets
    *        from the LaunchData list.  The SRTE needs this to be false in
    *        in order to get LaunchData for assets as well.
    * 
    */
   private void setLaunchData( Node iRootNode,
                               boolean iDefaultOrganizationOnly,
                               boolean iRemoveAssets )
   {
      LOGGER.entering("ManifestHandler", "SetLaunchData(iRootNode)");
      Vector organizationNodes = getOrganizationNodes( iRootNode,
                                                     iDefaultOrganizationOnly );

      int size = organizationNodes.size();

      // populate the Launch Data for the Organization level
      for ( int i = 0; i < size; i++ )
      {
         Node currentOrganization = (Node)organizationNodes.elementAt(i);
         String orgIdentifier = DOMTreeUtility.getAttributeValue( currentOrganization,
                                                           "identifier" );
         addItemInfo( currentOrganization, orgIdentifier );
      }

      Node xmlBaseNode = null;
      String manifestXMLBase = mEmptyString;
      String resourcesXMLBase = mEmptyString;

      // calculate the <manifest>s xml:base
      NamedNodeMap attributes = iRootNode.getAttributes();
      xmlBaseNode = attributes.getNamedItem("xml:base");
      if ( xmlBaseNode != null )
      {
         manifestXMLBase = xmlBaseNode.getNodeValue();
      }

      // calculate the <resources> xml:base
      Node resources = DOMTreeUtility.getNode( iRootNode, "resources" );
      attributes = resources.getAttributes();
      xmlBaseNode = attributes.getNamedItem("xml:base");
      if ( xmlBaseNode != null )
      {
         resourcesXMLBase = xmlBaseNode.getNodeValue();
      }

      // populate all Launch Data with the xml:base values
      size = mLaunchDataList.size();
      LaunchData currentLaunchData = null;

      for ( int j = 0; j < size; j++ )
      {
         currentLaunchData = (LaunchData)mLaunchDataList.elementAt(j);
         // update the xml:base data
         currentLaunchData.setManifestXMLBase( manifestXMLBase );
         currentLaunchData.setResourcesXMLBase( resourcesXMLBase );

         // replace the old LaunchData Object with the updated one
         mLaunchDataList.removeElementAt(j);
         mLaunchDataList.insertElementAt( currentLaunchData, j );
      }
      // populate the Launch Data for the Resource level
      addResourceInfo( iRootNode, iRemoveAssets );

      removeDuplicateLaunchData();

      mLaunchDataTracked = true;
   }

   /**
    * This method performs the actual retrieval of the metadata information,
    * if this information exists in the content package test
    * subject.  This method walks through the test subject dom, storing all
    * metadata information to the MetadataData data structure.  xml:base is
    * also being tracked for the &lt;adlcp:location&gt; element.
    *
    * @param iNode element nodes traversed for metadata element.
    *
    * @param iBaseDirectory base directory for location of test subject
    *
    **/
   private void setMetadata( Node iNode,
                             String iBaseDirectory )
   {

      if ( iNode != null )
      {
         String nodeName = iNode.getLocalName();

         if ( nodeName != null )
         {
            if ( nodeName.equals( "manifest" ) )
            {
               // set and retrieve xml:base of manifest if it exists

               // must first clear out xml:base values if dealing with a sub
               if ( !mManifestResourcesXMLBase.equals(mEmptyString) )
               {
                  mManifestResourcesXMLBase = mEmptyString;
                  mResourceXMLBase = mEmptyString;
               }

               String manifestXMLBase =
                             DOMTreeUtility.getAttributeValue( iNode, "base" );

               if ( !manifestXMLBase.equals(mEmptyString) )
               {
                  mManifestResourcesXMLBase = manifestXMLBase;
               }


               trackMetadata( iNode, "adlreg", iBaseDirectory );
            }
            else if ( nodeName.equals( "organization" ) )
            {
               trackMetadata( iNode, "adlreg", iBaseDirectory );
            }
            else if ( nodeName.equals ( "item" ) )
            {
               trackMetadata( iNode, "adlreg", iBaseDirectory );
            }
            else if ( nodeName.equals("resources") )
            {
               // set and retrieve xml:base of resources if it exists
               String resourcesXMLBase = DOMTreeUtility.getAttributeValue(
                                                                iNode, "base" );

               if ( !resourcesXMLBase.equals(mEmptyString) )
               {
                  mManifestResourcesXMLBase = 
                                  mManifestResourcesXMLBase + resourcesXMLBase;
               }

            }
            else if ( nodeName.equals( "resource" ) )
            {
               // retrieve xml:base of resource if it exists
               // cannot set classattribute - applies to specified resource only

               mResourceXMLBase = DOMTreeUtility.getAttributeValue(
                                                                iNode, "base" );

               trackMetadata( iNode, "adlreg", iBaseDirectory );
            }
            else if ( nodeName.equals( "file" ) )
            {
               trackMetadata( iNode, "adlreg", iBaseDirectory );
            }

            NodeList nodeChildren = iNode.getChildNodes();
            int size = nodeChildren.getLength();
            if ( nodeChildren != null )
            {
               for ( int i=0; i < size; i++ )
               {
                   // special check for SCORM 4th Edition to eliminate 
                   //(sub)manifest from having metadata set

                   String childNodeName = nodeChildren.item(i).getLocalName();
                   
                   if ( childNodeName != null) 
                   {
                       if (!childNodeName.equals("manifest") ) 
                       {
                         setMetadata( nodeChildren.item(i), iBaseDirectory );
                       }
                   }
                   else
                   {
                       setMetadata( nodeChildren.item(i), iBaseDirectory );
                   }
               }
            }
         }
         mMetadataTracked = true;

      }
   }

   /**
    *
    * This method tracks the metadata information contained in the
    * metadata element and saves the information in the MetadataData object.
    * Such information saved includes the metaddata application profile type,
    * URI if the metadata is external stand-alone metadata, or the root node if
    * the metadata is inline in the form of extensions to the content package
    * manifest.
    *
    * @param iNode - node tracked for Metadata
    * 
    * @param iApplicationProfileType Metadata Application Profile Typy (asset,
    *                 sco, activity, contentaggregation).
    * 
    * @param iBaseDirectory - base directory for location of test subject
    *
    */
   private void trackMetadata( Node iNode,
                               String iApplicationProfileType,
                               String iBaseDirectory )
   {
      Node metadataNode = DOMTreeUtility.getNode( iNode, "metadata");

      if ( metadataNode != null )
      {
         String identifier = DOMTreeUtility.getAttributeValue( iNode,
                                                               "identifier" );

         // Element does not have an identifier, checks its parent for one
         if ( identifier.equals("") )
         {
            identifier = DOMTreeUtility.getAttributeValue( iNode.getParentNode(), "identifier" );
         }

         //Gets all the location metadata
         Vector locationNodeList =DOMTreeUtility.getNodes( metadataNode,
                                                           "location");

         // iterate through the vector and get the attribute names and values

         int locationNodeListSize = locationNodeList.size();
         for ( int i = 0; i < locationNodeListSize; i++ )
         {
            MetadataData metadataData = new MetadataData();
            metadataData.setApplicationProfileType( iApplicationProfileType );

            //Gets the location value of each node
            String locationValue =
               DOMTreeUtility.getNodeValue((Node)locationNodeList.elementAt(i));
            locationValue = mManifestResourcesXMLBase + mResourceXMLBase +
                           locationValue;

            metadataData.setIdentifier( identifier );
            metadataData.setLocation( locationValue );

            mMetadataDataList.add( metadataData );

         }


         //Gets all the inline metadata from the current node
         Vector lomNodelist = DOMTreeUtility.getNodes(metadataNode, "lom");

         // iterate through the vector and get the attribute names and values
         int lomNodeListSize = lomNodelist.size();
         for ( int j = 0; j < lomNodeListSize; j++ )
         {
            MetadataData metadataData = new MetadataData();
            metadataData.setApplicationProfileType( iApplicationProfileType );

            //Gets the location value of each node
            metadataData.setIdentifier( identifier );

            Node lomNode = (Node)lomNodelist.elementAt(j);
            metadataData.setRootLOMNode(lomNode);
            metadataData.setLocation( "inline" );

            mMetadataDataList.add( metadataData );
 
         }

          //metadataData.printToConsole();
      }
   }


   /**
    * This method returns a list of all the metadata that is referenced
    * via the &lt;adlcp:location&gt; element in an IMS Manifest.
    *
    * @param iNode element nodes traversed for metadata element.
    *    
    **/
   private void checkForAdlcpLocationMD( Node iNode )
   {

      if ( iNode != null )
      {
         String nodeName = iNode.getLocalName();

         if ( nodeName != null )
         {
            if ( nodeName.equals( "manifest" ) )
            {
               // set and retrieve xml:base of manifest if it exists

               // must first clear out xml:base values if dealing with a sub
               if ( !mManifestResourcesXMLBase.equals(mEmptyString) )
               {
                  mManifestResourcesXMLBase = mEmptyString;
                  mResourceXMLBase = mEmptyString;
               }

               String manifestXMLBase =
                             DOMTreeUtility.getAttributeValue( iNode, "base" );

               if ( !manifestXMLBase.equals(mEmptyString) )
               {
                  mManifestResourcesXMLBase = manifestXMLBase;
               }

               getAdlcpLocationMD( iNode );
            }
            else if ( nodeName.equals( "organization" ) )
            {
               getAdlcpLocationMD( iNode );
            }
            else if ( nodeName.equals ( "item" ) )
            {
               getAdlcpLocationMD( iNode );
            }
            else if ( nodeName.equals("resources") )
            {
               // set and retrieve xml:base of resources if it exists
               String resourcesXMLBase = DOMTreeUtility.getAttributeValue(
                                                                iNode, "base" );

               if ( !resourcesXMLBase.equals(mEmptyString) )
               {
                  mManifestResourcesXMLBase = 
                                   mManifestResourcesXMLBase + resourcesXMLBase;
               }

            }
            else if ( nodeName.equals( "resource" ) )
            {

               // retrieve xml:base of resource if it exists
               // cannot set classattribute - applies to specified resource only

               mResourceXMLBase = DOMTreeUtility.getAttributeValue(
                                                                iNode, "base" );

               getAdlcpLocationMD( iNode );
            }
            else if ( nodeName.equals( "file" ) )
            {
               getAdlcpLocationMD( iNode );
            }

            NodeList nodeChildren = iNode.getChildNodes();
            int size = nodeChildren.getLength();
            if ( nodeChildren != null )
            {
               for ( int i=0; i < size; i++ )
               {
                  checkForAdlcpLocationMD( nodeChildren.item(i) );
               }
            }
         }
      }
   } 

   /**
    * This method retrieves only the adlcp:location value and stores it in a 
    * vector
    * 
    * @param iNode - element nodes traversed for metadata element
    * 
    */
   private void getAdlcpLocationMD( Node iNode )
   {
      Node metadataNode = DOMTreeUtility.getNode( iNode, "metadata");

      if ( metadataNode != null )
      {
         //Gets all the location metadata
         Vector locationNodeList =DOMTreeUtility.getNodes( metadataNode,
                                                           "location");

         // iterate through the vector and get the attribute names and values

         int locationNodeListSize = locationNodeList.size();
         for ( int i = 0; i < locationNodeListSize; i++ )
         {

            //Gets the location value of each node
             String locationValue =  
               DOMTreeUtility.getNodeValue((Node)locationNodeList.elementAt(i) );
             locationValue = mManifestResourcesXMLBase + 
                             mResourceXMLBase + locationValue;
             mLocationList.add(locationValue);  

         }
      }
   }

   /**
    * This method removes the duplicate LaunchData elements that are stored in
    * the list during tracking.  This removal is based on the Resource
    * Identifier, XML Base directories, Location and Parameters.
    */
   private void removeDuplicateLaunchData()
   {
      int size = mLaunchDataList.size();
      LaunchData ldA;
      LaunchData ldB;
      String ldAid;
      String ldBid;
      String ldAll;
      String ldBll;

      for ( int i = 0; i < size; i++ )
      {
         ldA = (LaunchData)mLaunchDataList.elementAt(i);
         ldAid = ldA.getResourceIdentifier();

         for ( int j = i + 1; j < size; j++ )
         {
            ldB = (LaunchData)mLaunchDataList.elementAt(j);
            ldBid = ldB.getResourceIdentifier();

            if ( ldBid.equals(ldAid) )
            {
               ldAll = ldA.getItemIdentifier();
               ldBll = ldB.getItemIdentifier();

               if ( ldBll.equals(ldAll) )
               {
                  mLaunchDataList.removeElementAt(j);
                  j--;
                  size = mLaunchDataList.size();
               }
            }
         }
      }
   }

   /**
    * This method retrieves all the organization nodes from the content package
    * manifest dom.  This method serves as a helper for retrieving SCO
    * launch data.
    *
    * @param iDefaultOrganizationOnly boolean describing the scope of the
    *        organization that should be traversed for SCO launch data. Specific 
    *        to SRTE uses - will no longer be needed in future development.
    * 
    * @param iRootNode root node of test subject dom.
    * 
    * @return Vector Containing a list of organization nodes.
    */
   public static Vector getOrganizationNodes( Node iRootNode,
                                              boolean iDefaultOrganizationOnly )
   {
      //mLogger.entering("ManifestHandler", "getOrganizationNodes()");
      Vector result = new Vector();

      if ( iDefaultOrganizationOnly )
      {
         result.add( getDefaultOrganizationNode( iRootNode ) );
      }
      else
      {
         // get the list of organization nodes
         Node organizationsNode = DOMTreeUtility.getNode( iRootNode,
                                                          "organizations" );
         NodeList children = organizationsNode.getChildNodes();
         
         if ( children != null )
         {
            int numChildren = children.getLength();

            for ( int i = 0; i < numChildren; i++ )
            {
               Node currentChild = children.item(i);
               String currentChildName = currentChild.getLocalName();
               //String currentChildName = currentChild.getNodeName();
               if ( currentChildName.equals( "organization" ) )
               {
                  // add the organization node to the resulting list
                  result.add( currentChild );
               }
            }
         }
      }
      return result;
   }

   /**
    * This method returns the default organization node that is flagged
    * by the default attribute. This method serves as a helper method.
    *
    * @param iRootNode root node of test subject dom.
    * 
    * @return Node default organization
    */
   public static Node getDefaultOrganizationNode( Node iRootNode )
   {
      Node result = null;

      // find the value of the "default" attribute of the <organizations> node
      Node organizationsNode = DOMTreeUtility.getNode( iRootNode,
                                                       "organizations" );
      NamedNodeMap attrList = organizationsNode.getAttributes();
      String defaultIDValue = decodeHandler.processWhitespace((attrList.getNamedItem("default")).getNodeValue());

      // traverse the <organization> nodes and find the matching default ID
      NodeList children = organizationsNode.getChildNodes();

      if ( children != null )
      {
         int numChildren = children.getLength();

         for ( int i = 0; i < numChildren; i++ )
         {
            Node currentChild = children.item(i);
            String currentChildName = currentChild.getLocalName();

            if ( currentChildName.equals( "organization" ) )
            {
               // find the value of the "identifier" attribute of the
               // <organization> node
               NamedNodeMap orgAttrList = currentChild.getAttributes();
               String idValue =
                  decodeHandler.processWhitespace((orgAttrList.getNamedItem("identifier")).getNodeValue());

               if ( idValue.equals( defaultIDValue ) )
               {
                  result = currentChild;
                  break;
               }
            }
         }
      }

      return result;
   }

   /**
    * This method retrieves the minNormalizedMeasure element from the parent
    * sequencing element.
    *
    * @param iNode to be manipulated for minnormalizedmeasure value.
    *
    * @return String containing the minNormalizedMeasure value.
    */
   private String getMinNormalizedMeasure( Node iNode )
   {
      String minNormalizedMeasure = mEmptyString;
      String nodeName = iNode.getLocalName();

      if ( nodeName.equals("item") )
      {
         Node sequencingNode = DOMTreeUtility.getNode( iNode, "sequencing" );
         if ( sequencingNode != null )
         {
            Node objectivesNode = DOMTreeUtility.getNode( sequencingNode,
                                                            "objectives" );
            if ( objectivesNode != null )
            {
               Node primaryObjectiveNode = DOMTreeUtility.
                                            getNode( objectivesNode,
                                                     "primaryObjective" );
               if ( primaryObjectiveNode != null)
               {
                  String satisfiedByMeasureValue = DOMTreeUtility.getAttributeValue(
                                            primaryObjectiveNode,
                                            "satisfiedByMeasure" );
                  if( satisfiedByMeasureValue.equals("true") )
                  {
                     Node minNormalizedMeasureNode = DOMTreeUtility.getNode(
                                                    primaryObjectiveNode,
                                                    "minNormalizedMeasure" );
                     if( minNormalizedMeasureNode != null )
                     {
                       minNormalizedMeasure = DOMTreeUtility.getNodeValue(
                                                     minNormalizedMeasureNode );
                       if( minNormalizedMeasure.trim().equals(mEmptyString) )
                       {
                          minNormalizedMeasure = "1.0";
                       }
                     }
                     else
                     {
                       minNormalizedMeasure = "1.0";
                     }
                  }
               }
            }
         }
      }
      return minNormalizedMeasure;
   }

   /**
    * This method retrieves the attemptAbsoluteDurationLimit element from the
    * parent sequencing element.
    *
    * @param iNode  node to be manipulated for attemptAbsoluteDurationLimit
    * value.
    *
    * @return String containing the attemptAbsoluteDurationLimit value.
    */
   private String getAttemptAbsoluteDurationLimit( Node iNode )
   {
      String attemptAbsoluteDurationLimit = mEmptyString;

      String nodeName = iNode.getLocalName();

      if ( nodeName.equals("item") )
      {
         Node sequencingNode = DOMTreeUtility.getNode( iNode, "sequencing" );
         if ( sequencingNode != null )
         {
            Node limitConditionsNode = DOMTreeUtility.getNode( sequencingNode,
                                                           "limitConditions" );
            if ( limitConditionsNode != null )
            {
               attemptAbsoluteDurationLimit = DOMTreeUtility.getAttributeValue(
                                              limitConditionsNode,
                                            "attemptAbsoluteDurationLimit" );
            }
         }
      }
      return attemptAbsoluteDurationLimit;
   }

   /**
    * This method retrieves the information described by the &lt;item&gt;
    * element and saves it for SCO launch data information.  This method
    * traverses the &lt;item&gt;s of the &lt;organization&gt; recursively and
    * retrieves the identifiers, referenced identifier references and
    * corresponding parameters from the &lt;resources&gt; element.
    *
    * @param iNode The organization node.
    * 
    * @param iOrgID The ID of the organization.
    */
   private void addItemInfo( Node iNode, String iOrgID )
   {
      LOGGER.entering("ManifestHandler", "addItemInfo()");
      if ( iNode == null )
      {
         return;
      }

      int type = iNode.getNodeType();
      String orgID = iOrgID;

      switch ( type )
      {
         // document node
         // this is a fail safe case to handle an error where a document node
         // is passed
         case Node.DOCUMENT_NODE:
         {
            Node rootNode = ((Document)iNode).getDocumentElement();

            addItemInfo( rootNode, orgID );

            break;
         }

         // element node
         case Node.ELEMENT_NODE:
         {
            String nodeName = iNode.getLocalName();

            // get the needed values of the attributes
            if ( nodeName.equals("item") )
            {
               String orgIdentifier   = mEmptyString;
               String identifier      = mEmptyString;
               String identifierref   = mEmptyString;
               String parameters      = mEmptyString;
               String title           = mEmptyString;
               String dataFromLMS     = mEmptyString;
               String timeLimitAction = mEmptyString;
               String completionThreshold = mEmptyString;
               String objectiveslist = mEmptyString;
               List dataMapDataList  = new ArrayList();
               boolean previous       = false;
               boolean shouldContinue = false;
               boolean exit           = false;
               boolean exitAll        = false;
               boolean abandon        = false;
               boolean suspendAll     = false;

               //Assign orgIdentifier the value of the parameter iOrgID
               orgIdentifier = iOrgID;

               // get the value of the following attributes:
               // - identifier
               // - identifierref
               // - parameters
               //
               // leave the value at "" is the attribute does not exist
               NamedNodeMap attrList = iNode.getAttributes();
               int numAttr = attrList.getLength();
               Attr currentAttrNode;
               String currentNodeName;

               // loop through the attributes and get their values assuming that
               // the multiplicity of each attribute is 1 and only 1.
               for ( int i = 0; i < numAttr; i++ )
               {
                  currentAttrNode = (Attr)attrList.item(i);
                  currentNodeName = currentAttrNode.getLocalName();

                  // store the value of the attribute
                  if ( currentNodeName.equalsIgnoreCase("identifier") )
                  {
                     identifier = decodeHandler.processWhitespace(currentAttrNode.getValue());
                  }
                  else if ( currentNodeName.equalsIgnoreCase("identifierref") )
                  {
                     identifierref = currentAttrNode.getValue();
                  }
                  else if ( currentNodeName.equalsIgnoreCase("parameters") )
                  {
                     parameters = currentAttrNode.getValue();
                  }

               }

               // get the value of the title element
               // assume that there is 1 and only 1 child named title
               title = DOMTreeUtility.getNodeValue(
                                      DOMTreeUtility.
                                      getNode( iNode, "title" ) );

               // get the value of the datafromlms element
               dataFromLMS = DOMTreeUtility.getNodeValue(
                                            DOMTreeUtility.
                                            getNode( iNode, "dataFromLMS" ) );

               // get the value of the timelimitaction element
               timeLimitAction = DOMTreeUtility.getNodeValue(
                                                DOMTreeUtility.
                                                getNode( iNode,
                                                        "timeLimitAction" ) );

               // Due to 4th Edition updates we have to check the attributes first
               // then check the element value
               Node compThreshNode = DOMTreeUtility.getNode( iNode,"completionThreshold" );
               
               if ( compThreshNode != null )
               {
                  completionThreshold = DOMTreeUtility.getAttributeValue(compThreshNode, "minProgressMeasure");
               }
               
               if ( completionThreshold.equals("") )
               {               
                  // get the value of the completionThreshold element
                  completionThreshold = DOMTreeUtility.getNodeValue(
                                                   DOMTreeUtility.
                                                   getNode( iNode,
                                                         "completionThreshold" ) );
               }

               //Gets the sequencing objectives list for this item
               objectiveslist = getObjectivesList(DOMTreeUtility.getNode(iNode, "sequencing"));
          
               //Gets the data stores list for this item
               dataMapDataList = getDataMapData(iNode);

               //get the hideRTSUI elements and set the previous, continue,
               //exit, exitAll, abandon and suspendAll variables accordingly.
               Node presentationNode =
                  DOMTreeUtility.getNode( iNode, "presentation" );
               if ( presentationNode != null )
               {
                  Node navInterfaceNode =
                     DOMTreeUtility.getNode( presentationNode,
                                             "navigationInterface" );
                  if ( navInterfaceNode != null )
                  {
                     NodeList children = navInterfaceNode.getChildNodes();
                     if (children != null)
                     {
                        int numChildren = children.getLength();
                        for ( int i = 0; i < numChildren; i++ )
                        {
                           Node currentChild = children.item( i );
                           String currentChildName =
                               currentChild.getLocalName();
                           if ( currentChildName.equals("hideLMSUI") )
                           {
                              String currentChildValue =
                                  DOMTreeUtility.getNodeValue( currentChild );
                              if (  currentChildValue.equals("previous") )
                              {
                                 previous = true;
                              }
                              else if ( currentChildValue.equals( "continue" ) )
                              {
                                 shouldContinue = true;
                              }
                              else if ( currentChildValue.equals( "exit" ) )
                              {
                                 exit = true;
                              }
                              else if ( currentChildValue.equals( "exitAll" ) )
                              {
                                 exitAll = true;
                              }
                              else if ( currentChildValue.equals( "abandon" ) )
                              {
                                 abandon = true;
                              }
                              else if ( currentChildValue.
                                        equals( "suspendAll" ) )
                              {
                                 suspendAll = true;
                              }
                           }
                        }
                     }
                  }
               }

               // make sure this item actually points to a <resource>
               if ( ! identifierref.equals(mEmptyString) )
               {
                  // create an instance of the LaunchData data structure and
                  // add it to the LaunchDataList
                  LaunchData launchData = new LaunchData();

                  launchData.setOrganizationIdentifier( orgIdentifier );
                  launchData.setItemIdentifier( identifier );
                  launchData.setResourceIdentifier( identifierref );
                  launchData.setParameters( parameters );
                  launchData.setItemTitle( title );
                  launchData.setDataFromLMS( dataFromLMS );
                  launchData.setTimeLimitAction( timeLimitAction );
                  launchData.setCompletionThreshold( completionThreshold );
                  launchData.setPrevious( previous );
                  launchData.setContinue( shouldContinue );
                  launchData.setExit( exit );
                  launchData.setExitAll( exitAll );
                  launchData.setAbandon( abandon );
                  launchData.setSuspendAll( suspendAll );
                  launchData.setMinNormalizedMeasure(
                                             getMinNormalizedMeasure( iNode ) );
                  launchData.setAttemptAbsoluteDurationLimit(
                                     getAttemptAbsoluteDurationLimit( iNode ) );
                  launchData.setObjectivesList(objectiveslist);
                  launchData.setDataMapDataList(dataMapDataList);

                  mLaunchDataList.add( launchData );
               }
            }

            // get the child nodes and add their items info
            NodeList children = iNode.getChildNodes();

            if ( children != null )
            {
               int numChildren = children.getLength();
               Node currentChild;

               for ( int z = 0; z < numChildren; z++ )
               {
                  currentChild = children.item(z);
                  addItemInfo( currentChild, orgID );
               }
            }
            break;
         }
         // handle all other node types
         default:
         {
            break;
         }
      }
   }


   /**
    * This method gets all the sequencing objectives associated with the
    * current item.
    *
    * @param iNode root item node.
    * 
    * @return String - returns a string contaniing the objectives data 
    */
   private String getObjectivesList(Node iNode)
   {
      int j, k;
      NamedNodeMap attributesList = null;
      String result = mEmptyString;

      // Gets to the objectives node, if one exists
      if (iNode != null)
      {
         Node objNode = DOMTreeUtility.getNode(iNode, "objectives");

         if (objNode != null)
         {
            //Gets the primary objective id
            Node primaryObjNode = DOMTreeUtility.getNode( objNode, "primaryObjective" );
            if (primaryObjNode != null)
            {
               attributesList = primaryObjNode.getAttributes();

               // iterate through the NamedNodeMap and get the attribute names and values
               for(j = 0; j < attributesList.getLength(); j++)
               {
                  //Finds the schema location and parses out values
                  if (attributesList.item(j).getLocalName().equalsIgnoreCase("objectiveID"))
                  {
                     result = decodeHandler.processWhitespace(attributesList.item(j).getNodeValue());
                  }
               }
            }

            //Gets all objective ids
            Vector objNodes = DOMTreeUtility.getNodes(objNode, "objective");

            for(j = 0; j < objNodes.size(); j++)
            {
               Node currNode = (Node)objNodes.elementAt(j);
               attributesList = currNode.getAttributes();

               // iterate through the NamedNodeMap and get the attribute names and values
               for(k = 0; k < attributesList.getLength(); k++)
               {
                  //Finds the schema location and parses out values
                  if (attributesList.item(k).getLocalName().equalsIgnoreCase("objectiveID"))
                  {
                     result = result + "," + decodeHandler.processWhitespace(attributesList.item(k).getNodeValue());
                  }
               }
            } // end looping over nodes
         } // end if objNode != null
      } // end if iNode != null

      //returns objective list, if it was found.
      return result;
   }
   
   /**
    * This method gets all the sequencing data stores associated with the
    * current item.
    *
    * @param iNode root item node.
    * 
    * @return String - returns a string containing the data store data 
    */
   public List getDataMapData(Node iNode)
   {
      int j, k;
      NamedNodeMap attributesList = null;
      List dataMaps = new ArrayList();

      // Gets to the data node, if one exists
      if (iNode != null)
      {
         Node dataNode = DOMTreeUtility.getNode(iNode, "data");

         if (dataNode != null)
         {
            //Gets all target ids
            Vector mapNodes = DOMTreeUtility.getNodes(dataNode, "map");

            for(j = 0; j < mapNodes.size(); j++)
            {
               Node currNode = (Node)mapNodes.elementAt(j);
               attributesList = currNode.getAttributes();
               String targetID = "";
               String writeSharedData = "";
               String readSharedData = "";
               DataMapData dataMap = new DataMapData();

               // iterate through the NamedNodeMap and get the attribute names and values
               for(k = 0; k < attributesList.getLength(); k++)
               {
                  if ( attributesList.item(k).getLocalName().equalsIgnoreCase("targetID") )
                  {
                     targetID = decodeHandler.processWhitespace(attributesList.item(k).getNodeValue());
                  }
                  else if ( attributesList.item(k).getLocalName().equalsIgnoreCase("writeSharedData") )
                  {
                     writeSharedData = attributesList.item(k).getNodeValue().trim();
                  }
                  else if ( attributesList.item(k).getLocalName().equalsIgnoreCase("readSharedData") )
                  {
                     readSharedData = attributesList.item(k).getNodeValue().trim();
                  }
               }
               
               dataMap.setTargetID(targetID);
               if ( writeSharedData.equals("") )
               {
                  dataMap.setWriteSharedData("true");
               }
               else
               {
                  dataMap.setWriteSharedData(writeSharedData.toLowerCase());
               }
               if ( readSharedData.equals("") )
               {
                  dataMap.setReadSharedData("true");
               }
               else
               {
                  dataMap.setReadSharedData(readSharedData.toLowerCase());
               }
               dataMaps.add(dataMap);
            }
         }
      }

      //returns data map list, if it was found.
      return dataMaps;
   }

   /**
    * This method uses the information stored in the SCO Launch Data List
    * to get the associated Resource level data.
    *
    * @param iRootNode root node of the DOM.
    * 
    * @param iRemoveAssets boolean representing whether or not the assets should
    *                      be removed.  (The Sample RTE will never want to
    *                      remove the assets, where as the TestSuite will.)
    */
   private void addResourceInfo( Node iRootNode, boolean iRemoveAssets )
   {
      // get the <resources> node
      Node resourcesNode = DOMTreeUtility.getNode( iRootNode, "resources" );

      String scormType = mEmptyString;
      String location  = mEmptyString;
      String xmlBase   = mEmptyString;

      // launch data processing stuff
      int size = mLaunchDataList.size();
      LaunchData currentLaunchData;
      String resourceIdentifier = mEmptyString;
      String persistState    = mEmptyString;
      Node matchingResourceNode = null;

      // here we are dealing with a content aggregation package
      for ( int i = 0; i < size; i++ )
      {
         currentLaunchData = (LaunchData)mLaunchDataList.elementAt(i);
         resourceIdentifier = currentLaunchData.getResourceIdentifier();

         matchingResourceNode =
            getResourceNodeWithIdentifier( resourcesNode, resourceIdentifier );

         // Ensure resource node exists
         if ( matchingResourceNode != null )
         {
            // get the value of the following attributes:
            // - adlcp:scormtype
            // - href
            // - xml:base
            //
            // leave the value at "" is the attribute does not exist
            scormType = DOMTreeUtility.getAttributeValue( matchingResourceNode,
                                                          "scormType" );
            location  = DOMTreeUtility.getAttributeValue( matchingResourceNode,
                                                          "href" );
            xmlBase   = DOMTreeUtility.getAttributeValue( matchingResourceNode,
                                                          "base" );
            persistState = DOMTreeUtility.getAttributeValue(matchingResourceNode,
                                                          "persistState");
         }

         // populate the current Launch Data with the resource level values
         currentLaunchData.setSCORMType( scormType );
         currentLaunchData.setLocation( location );
         currentLaunchData.setResourceXMLBase( xmlBase );
         currentLaunchData.setPersistState( persistState );
         
         // populate the current LaunchInfo object for possible later logging use
         LaunchInfo.getInstance().addSCO(resourceIdentifier, location);

         try
         {
            mLaunchDataList.set( i, currentLaunchData );
         }
         catch ( ArrayIndexOutOfBoundsException aioobe )
         {
            System.out.println( "ArrayIndexOutOfBoundsException caught on " +
                                "Vector currentLaunchData.  Attempted index " +
                                "access is " + i + "size of Vector is " +
                                mLaunchDataList.size() );
         }
      }

      if ( size == 0 ) // then we are dealing with a resource package
      {
         // loop through resources to retieve all resource information
         // loop through the children of <resources>
         NodeList children = resourcesNode.getChildNodes();
         int childrenSize = children.getLength();

         if ( children != null )
         {
            for ( int z = 0; z < childrenSize; z++ )
            {
                Node currentNode = children.item( z );
                String currentNodeName = currentNode.getLocalName();

                if ( currentNodeName.equals("resource") )
                {
                   // create an instance of the LaunchData data structure and
                   // add it to the LaunchDataList
                   LaunchData launchData = new LaunchData();

                   // get the value adlcp:scormtype, href, base attribute
                   // leave the value at "" is the attribute does not exist
                   scormType = DOMTreeUtility.getAttributeValue( currentNode,
                                                                 "scormType" );

                   location  = DOMTreeUtility.getAttributeValue( currentNode,
                                                                 "href" );

                   xmlBase   = DOMTreeUtility.getAttributeValue( currentNode,
                                                                 "base" );

                   resourceIdentifier = DOMTreeUtility.getAttributeValue(
                                                                 currentNode,
                                                                 "identifier" );

                   // populate the  Launch Data with the resource level values
                   launchData.setSCORMType( scormType );
                   launchData.setLocation( location );
                   launchData.setResourceXMLBase( xmlBase );
                   launchData.setResourceIdentifier( resourceIdentifier );

                   mLaunchDataList.add( launchData );
                } // end if current node == resource
            } // end looping over children
         } // end if there are no children
      } // end if size == 0

      if( iRemoveAssets )
      {
         removeAssetsFromLaunchDataList();
      }
   }

   /**
    * This method retrieves the resource node that matches the passed in
    * identifier value.  This method serves as a helper method.
    *
    * @param iResourcesNode Parent resources node of the resource elements.
    * 
    * @param iResourceIdentifier identifier value of the resource node being
    * retrieved.
    *
    * @return Node resource element node that matches the identifier value.
    */
   private Node getResourceNodeWithIdentifier( Node iResourcesNode,
                                               String iResourceIdentifier )
   {
      Node result = null;

      // loop through the children of <resources>
      NodeList children = iResourcesNode.getChildNodes();

      if ( children != null )
      {
         int numChildren = children.getLength();
         Node currentChild = null;
         String currentChildName = mEmptyString;
         String currentResourceIdentifier = mEmptyString;

         for ( int i = 0; i < numChildren; i++ )
         {
            currentChild = children.item(i);
            currentChildName = currentChild.getLocalName();

            // locate the <resource> Nodes
            if ( currentChildName.equals( "resource" ) )
            {
               // get the identifier attribute of the current <resource> Node
               currentResourceIdentifier =
                  DOMTreeUtility.getAttributeValue( currentChild, "identifier" );

               // match the identifier attributes and get the missing data
               if ( currentResourceIdentifier.equals(iResourceIdentifier)  )
               {
                  result = currentChild;
                  break;
               }
            } // end if currentChildName == resource
         } // end looping over children
      } // end if there are no children

      return result;
   }

   /**
    * This method removes the asset information from the launch data list.
    * Assets are not launchable resources.
    */
   private void removeAssetsFromLaunchDataList()
   {

      int size = mLaunchDataList.size();
      LaunchData currentLaunchData;

      for ( int i = 0; i < size; )
      {
         currentLaunchData = (LaunchData)mLaunchDataList.elementAt(i);
         String scormType = currentLaunchData.getSCORMType();

         if ( scormType.equals( "asset" ) )
         {
            mLaunchDataList.removeElementAt( i );
            size = mLaunchDataList.size();
         }
         else
         {
            i++;
         }
      }
   }
}
