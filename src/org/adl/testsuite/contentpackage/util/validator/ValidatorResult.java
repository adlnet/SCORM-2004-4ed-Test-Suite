package org.adl.testsuite.contentpackage.util.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the results of the various stages of CTS package validation
 * 
 * @author ADL Technical Team
 *
 */
public class ValidatorResult
{
   /**
    * The result of the manifest validation
    */
   private boolean mManifestOutcome;
   
   /**
    * The results of the metadata validation
    */
   private List mMetadataOutcomes;

   /**
    * Whether or not a sub-manifest was used in the manfiest
    */
   private boolean mSubmanifestReferenced;
   
   /**
    * Whether or not an external file reference was used in the manfiest
    */
   private boolean mExternalFileReferenced;
   
   /**
    * Whether or not to run SCO testing
    */
   private boolean mExecuteScoTesting;
   
   /**
    * Default constructor
    */
   public ValidatorResult()
   {
      this.mManifestOutcome = false;
      this.mMetadataOutcomes = new ArrayList();
      this.mSubmanifestReferenced = false;
      this.mExternalFileReferenced = false;
      this.mExecuteScoTesting = false;
   }

   /**
    * Gets the results of manifest validation
    * 
    * @return the result of manifest validation
    */
   public boolean getManifestOutcome()
   {
      return mManifestOutcome;
   }

   /**
    * Sets the outcome of the manifest validation
    * 
    * @param iManifestOutcome the result of the manifest validation
    */
   public void setManifestOutcome(boolean iManifestOutcome)
   {
      this.mManifestOutcome = iManifestOutcome;
   }

   /**
    * Gets the list of metadata file validation results
    * 
    * @return a list of metadata file validation results
    */
   public List getMetadataOutcomes()
   {
      return mMetadataOutcomes;
   }

   /**
    * Sets a list of metadata file validation results
    * 
    * @param iMetadataOutcomes the list of metadata file validation results
    */
   public void setMetadataOutcomes(List iMetadataOutcomes)
   {
      this.mMetadataOutcomes = iMetadataOutcomes;
   }

   /**
    * Gets a boolean indicating whether or not a sub-manifest was referenced
    * 
    * @return a boolean indicating whether or not a sub-manifest was referenced
    */
   public boolean getSubmanifestReferenced()
   {
      return mSubmanifestReferenced;
   }

   /**
    * Sets the boolean indicating whether or not a sub-manifest was referenced
    * 
    * @param iSubmanifestReferenced a boolean indicating whether or not a sub-manifest was referenced
    */
   public void setSubmanifestReferenced(boolean iSubmanifestReferenced)
   {
      this.mSubmanifestReferenced = iSubmanifestReferenced;
   }
   
   /**
    * Gets a boolean indicating whether or not an External File was referenced
    * 
    * @return a boolean indicating whether or not an External File was referenced
    */
   public boolean getExternalFileReferenced()
   {
      return mExternalFileReferenced;
   }

   /**
    * Sets the boolean indicating whether or not an External File was referenced
    * 
    * @param iExternalFileReferenced a boolean indicating whether or not an External File was referenced
    */
   public void setExternalFileReferenced(boolean iExternalFileReferenced)
   {
      this.mExternalFileReferenced = iExternalFileReferenced;
   }

   /**
    * Gets the results of the SCO testing
    * 
    * @return a boolean indicating the result of the SCO testing
    */
   public boolean getExecuteScoTesting()
   {
      return mExecuteScoTesting;
   }

   /**
    * Sets the results of the SCO testing
    * 
    * @param iExecuteScoTesting the results of the SCO testing
    */
   public void setExecuteScoTesting(boolean iExecuteScoTesting)
   {
      mExecuteScoTesting = iExecuteScoTesting;
   }
   
}
