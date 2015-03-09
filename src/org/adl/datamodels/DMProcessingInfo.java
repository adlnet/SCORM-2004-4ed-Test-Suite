package org.adl.datamodels;

import java.util.Vector;

/**
 * Encapsulation of information required for processing a data model request.
 * 
 * <strong>Filename:</strong> DMProcessingInfo.java<br><br>
 * 
 * <strong>Description:</strong><br>
 * The <code>ADLLaunch</code> encapsulates the information that may be returned
 * (out parameters) during the processing of a data model request.<br><br>
 * 
 * <strong>Design Issues:</strong><br><br>
 * 
 * <strong>Implementation Issues:</strong><br>
 * All fields are purposefully public to allow immediate access to known data
 * elements.<br><br>
 * 
 * <strong>Known Problems:</strong><br><br>
 * 
 * <strong>Side Effects:</strong><br><br>
 * 
 * <strong>References:</strong><br>
 * <ul>
 *     <li>SCORM 2004
 * </ul>
 * 
 * @author ADL Technical Team
 */ 
public class DMProcessingInfo
{

   /**
    * Describes the value being maintained by a data model element.
    */
   public String mValue = null;


   /**
    * Describes the data model element that processing should be applied to.
    */
   public DMElement mElement = null;


   /**
    * Describes the set this data model element is contained in.
    */
   public Vector mRecords = null;

   /**
    * Describes whether this data model element is initialized or not.
    */
   public boolean mInitialized = false;
   
   /**
    * Describes if this data model element's value was set by the SCO or not.
    */
   public boolean mSetBySCO = false;
}  // end DMProcessingInfo
