package org.adl.validator;

import java.util.List;

import org.adl.validator.packagevalidator.PackageValidator;
import org.adl.validator.util.ResultCollection;

/**
 * Provides a way to validate content packages.
 * 
 * @author ADL Technical Team
 *
 */
public class Validator
{
   
   /**
    * Holds the name of the SCORM Content Package test subject.
    */
   private String mFileName;
   
   /**
    * Holds the Application Profile type.
    */
   private String mAppProfileType;
   
   /**
    * Holds the boolean indicating whether or not the IMS Manifest is to be the
    * only subject validated.
    */
   private boolean mManifestOnly;
   
   /**
    * The controlling class the handles the validation of content packages.
    */
   private PackageValidator mPackageValidator;
   
   /**
    * Instance of the collection of Result objects that are returned form every
    * package checker.
    */
   private ResultCollection mResultCollection;
   
   /**
    * Overload Constructor.
    * 
    * @param iFileName The name of the SCORM Content Package test subject
    * @param iAppProfileType The Application Profile type of the test
    *           subject (contentaggregation or resource )
    * @param iManifestOnly The boolean describing whether or not the IMS
    *           Manifest is to be the only subject validated. True implies that
    *           validation occurs only on the IMS Manifest (checks include
    *           wellformedness, schema validation, and application profile
    *           checks). False implies that the entire Content Package be
    *           validated (IMS Manifest checks with the inclusion of the
    *           required files checks, metadata, and sco testing).
    */
   public Validator(String iFileName, String iAppProfileType, boolean iManifestOnly)
   {
      mFileName = iFileName;
      mAppProfileType = iAppProfileType.toLowerCase();
      mManifestOnly = iManifestOnly;
      mResultCollection = new ResultCollection();
      mPackageValidator = new PackageValidator(mFileName, mAppProfileType, mManifestOnly, mResultCollection);

   }
   
   /**
    * Method that starts the validation of the content package.
    */
   public void validate()
   {

      mResultCollection = mPackageValidator.executePackageCheckers();
      
   }
   /**
    * Provides a way to retrieve the ResultCollection object that was created 
    * when the Validator was ran.
    * 
    * @return ResultCollection List of Result objects that represent the 
    * results of the package checkers.
    */
   public ResultCollection getResultCollection()
   {
      return mResultCollection;
   }
   
   /**
    * Provides a way for the list of checkers to be ran to be changed or made 
    * specific for an application.
    * 
    * @param iCheckerList List of checkers.  The order of the checkers in the 
    *        list is the order in which the checkers are ran.  To use a checker
    *        that is included in the adlvalidator.jar only the path of the checker
    *        in String form is needed. Ex: "org.adl.validator.packagechecker.checks.WellformednessChecker"
    *        If the checker is located in an external location then the checker 
    *        must be passed in as a Class object.
    *        
    */
   public void setCheckerList(List<String> iCheckerList)
   {
      mPackageValidator.setCheckerList(iCheckerList);
   }
}
