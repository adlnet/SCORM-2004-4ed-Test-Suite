package org.adl.validator.util;

/**
 * Holds the String values of the ADL default keys used in the object hash table
 * in CheckerStateData. 
 *
 * @author ADL Technical Team
 *
 */
public class ValidatorKeyNames
{
   
   /**
    * Holds the ADL reserved key name that indicates if the application profile 
    * type is 'contentaggregation' or 'resource'.
    */
   public static final String APP_PROFILE_TYPE_KEY = "appProfileType";
   
   /**
    * Holds the key name that indicates if a list of checkers besides the default
    * ADL list of checkers should be used.
    */
   public static final String CHECKER_LIST_KEY = "checkerList";
   
   /**
    * Holds the ADL reserved key name for the list of controlling schemas. 
    */
   public static final String CONTROLLING_SCHEMAS_KEY = "controllingSchemas";
   
   /**
    * Holds the ADL reserved key name for the name of the file being validated.  
    */
   public static final String FILE_NAME_KEY = "fileName";
   
   /**
    * Holds the ADL reserved key name for the name of the file being validated.  
    */
   public static final String IDENTIFIER_LIST = "identifierList";
   
   /**
    * Holds the ADL reserved key name for the name of the file being validated.  
    */
   public static final String ID_LIST = "IDList";
   
   /**
    * Holds the ADL reserved key name that indicates if the run of the validator 
    * is a manifest only run.
    */
   public static final String MANIFEST_ONLY_KEY = "manifestOnly";
   
   /**
    * Holds the key name that indicates if a list of resource files exist.
    */
   public static final String RESOURCE_FILE_LIST_KEY = "resourceFileList";
   
   /**
    * Holds the ADL reserved key name for the location of the root directory.
    */
   public static final String ROOT_DIRECTORY_KEY = "rootDirectory";
   
   /**
    * Holds the ADL reserved key for the name of the manifest being validated
    */
   public static final String XML_FILE_NAME_KEY = "xmlFileName";   
   
   /**
    * Holds the ADL reserved key for the JDom tree containing the XML instance
    */
   public static final String XML_FILE_JDOM_KEY = "xmlDOM";
}
