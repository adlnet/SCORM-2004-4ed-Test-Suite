package org.adl.datamodels;

import java.io.Serializable;


/**
 * Encapsulation of information required for processing a data model request.
 * <br><br>
 * 
 * <strong>Filename:</strong> DMDelimiterDescriptor.java<br><br>
 * 
 * <strong>Description:</strong><br>
 * Encapsulation of all information required to describe one dot-notation bound
 * delimiter.  This information will be used to create instances of delimiters
 * assocaited with data model elements.<br><br>
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
public class DMDelimiterDescriptor implements Serializable 
{

   /**
    * Describes the name of this delimiter
    */
   public String mName = null;


   /**
    * Describes if the default value of this delimiter
    */
   public String mDefault = null;


   /**
    * Describes the SPM for the value.
    */
   public int mValueSPM = -1;


   /**
    * Describes the method used to validate the value of this delimiter.
    */
   public DMTypeValidator mValidator = null;

   /**
    * Provides a way to store delimiter information such as name, default value,
    * and type of validator.
    * 
    * @param iName  The name of the delimiter
    * @param iDefault  The default value for the delimiter
    * @param iValidator  The validator associated with the delimiter
    */
   public DMDelimiterDescriptor(String iName, 
                                String iDefault,
                                DMTypeValidator iValidator)
   {
      mName = iName;
      mDefault = iDefault;
      mValidator = iValidator;
   }

   /**
    * Provides a way to store delimiter information such as name, default value,
    * and type of validator.
    * 
    * @param iName The name of the delimiter
    * 
    * @param iDefault The default value for the delimiter
    * 
    * @param iValueSPM The smallest permitted maximum size allowed for this
    *                      delimiter
    * 
    * @param iValidator The validator associated with the delimiter
    */
   public DMDelimiterDescriptor(String iName, 
                                String iDefault,
                                int iValueSPM,
                                DMTypeValidator iValidator)
   {
      mName = iName;
      mDefault = iDefault;
      mValueSPM = iValueSPM;
      mValidator = iValidator;
   }


}  // end DMDelimiterDescriptor
