package org.adl.sequencer;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Encapsulation of information required for delivery.<br><br>
 * 
 * <strong>Filename:</strong> ADLValidRequests.java<br><br>
 * 
 * <strong>Description:</strong><br>
 * The <code>ADLUIState</code> encapsulates the information required by the
 * SCORM 2004 4th Edition Sample RTE delivery system to determine which
 * navigation UI controls should be enabled on for the current launched
 * activity.<br><br>
 * 
 * <strong>Design Issues:</strong><br>
 * This implementation is intended to be used by the 
 * SCORM 2004 4th Edition Sample RTE.<br>
 * <br>
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
 *     <li>IMS SS 1.0
 *     <li>SCORM 2004 4th Edition
 * </ul>
 * 
 * @author ADL Technical Team
 */ 
public class ADLValidRequests implements Serializable
{

   /**
    * Should a 'Start' button be enabled before the sequencing session begins
    */
   public boolean mStart = false;


   /**
    * Should a 'Resume All' button be enabled before the sequencing session begins
    */
   public boolean mResume = false;


   /**
    * Should a 'Continue' button be enabled during delivery of the current
    * activity.
    */
   public boolean mContinue = false;

   /**
    * Should a 'Continue' button be enabled during delivery of the current
    * activity that triggers an Exit navigation request.
    */
   public boolean mContinueExit = false;

   /**
    * Should a 'Previous' button be enabled during the delivery of the
    * current activity.
    */
   public boolean mPrevious = false;

   /**
    * Indictates if the sequencing session has begun and a 'SuspendAll'
    * navigation request is valid.
    */
   public boolean mSuspend = false;

   /**
    * Set of valid targets for a choice navigation request
    */
   public Hashtable mChoice = null;

   /**
    * Set of valid targets for a jump navigation request.
    */
   public Hashtable mJump = null;
   
   /**
    * The currently valid table of contents (list of <code>ADLTOC</code>) to be
    * provided during the current activity.
    */
   public Vector mTOC = null;

}  // end ADLValidRequests
