package org.adl.validator.packagechecker.parsers;

import java.util.ArrayList;

import org.adl.validator.util.ValidatorMessage;

/**
 * This class extends the ValidatorSaxParser class to perform a wellformedness 
 * parse
 * 
 * @author ADL Technical Team
 *
 */
public class WellformednessSaxParser extends ValidatorSaxParser
{

   /**
    * Default constructor 
    */
   public WellformednessSaxParser()
   {
      mParseSuccess = true;
      mParseMessages = new ArrayList<ValidatorMessage>();
      this.configureParser();
   }   

}
