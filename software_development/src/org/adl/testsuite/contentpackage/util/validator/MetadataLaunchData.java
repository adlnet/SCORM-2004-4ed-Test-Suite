package org.adl.testsuite.contentpackage.util.validator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * <strong>Filename: </strong><br>MetadataLaunchData.java<br><br>
 *
 * <strong>Description: </strong><br>A <CODE>MetadataLaunchData</CODE> is a Data
 * Structure used to store information for the validation of Metadata found
 * in the Manifest.  This data structure tracks inline metadata (extensions to
 * the imsmanifest) as well as the location of the external metadata instances.
 * The metadata application profile type of each metadata tracked is stored in
 * this data structure as well.  Finally, a unique set of identifiers for 
 * each metadata instance are also tracked.
 * 
 * @author ADL Technical Team
 */
public class MetadataLaunchData extends MetadataData
{
   /**
    * This attribute stores the identifier value of the major elements
    * (item, orgs, etc/) that house the metadata instance.
    */
   private Set mIdentifiers;

   /**
    * The default constructor. Sets the attributes to their initial values.
    */
   public MetadataLaunchData()
   {
      mIdentifiers = new HashSet();
      super.setLocation("");
   }

   /**
    * This method returns the identifier attribute which stores the identifier
    * value of the major elements (item, orgs, etc/) that house the metadata
    * instance.
    *
    * @return String The identifier value of the parent of the metadata.
    */
   public String getIdentifiers()
   {
      Iterator iter = mIdentifiers.iterator();
      String ids = "";
      String id = "";
      while ( iter.hasNext() )
      {
         id = iter.next().toString();
         
         // We don't want a comma at the end
         if ( iter.hasNext() )
         {
            ids = ids + id + ", ";
         }
         else
         {
            ids = ids + id;
         }
      }
      
      return ids;
   }

   /**
    * This method sets the identifier value of the major elements
    * (item, orgs, etc/) that house the metadata instance.
    *
    * @param iIdentifier the identifier value to be set.
    */
   public void addIdentifier( String iIdentifier )
   {
      mIdentifiers.add(iIdentifier);
   }
}