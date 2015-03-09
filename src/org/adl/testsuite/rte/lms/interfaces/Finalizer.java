package org.adl.testsuite.rte.lms.interfaces;

import org.adl.logging.LmsLogger;

/**
 * Public interface for the finalizer. 
 * 
 * @author ADL Technical Team
 *
 */
public interface Finalizer
{

   /**
    * Performs closing operations and finalizations of the LMS Test.
    * 
    * @param iLogr LmsLogger object.
    */
   void lmsFinalize(LmsLogger iLogr);

}