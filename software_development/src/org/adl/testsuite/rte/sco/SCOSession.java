package org.adl.testsuite.rte.sco;

import java.util.Vector;

/**
 * <strong>Filename</strong>:  SCOSession.java<br><br>
 * 
 * <strong>Description</strong>:  TThis class is used in the SCO RTE Test.  It 
 * is responsible for keeping track of each SCO being tested.  It maintains 
 * information like errors, level of conformance, what API functions have been 
 * called and the status of each of the API calls<br><br>
 * 
 * @author ADL Technical Team
 * 
 */
public class SCOSession
{
   /**
    * Boolean that indicates if the Conformance Category is set to SCO RTE 1
    */
   private boolean mScoRTE1 = false;
   
   /**
    * Boolean that indicates if the Conformance Category is set to SCO RTE 2
    * (SCO - RTE 1 plus correctly uses one or more LMS mandatory data elements)
    */
   private boolean mScoRTE2 = false;
   
   /**
    * Boolean that indicates if the Conformance Category is set to SCO RTE 3
    * (SCO - RTE 1 plus correctly uses one or more LMS optional data elements)
    */
   private boolean mScoRTE3 = false;

   /**
    * Value indicating whether or not LMSInitialize() was invoked on the LMS
    */
   private boolean mLmsInitializeCalled = false;
   
   /**
    * Value indicating whether or not LMSFinish() was invoked on the LMS.
    */
   private boolean mLmsFinishedCalled = false;
   
   /**
    * Flag that indicates whether or not <code>GetValue()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private final Vector mLmsGetValueCalled;
   
   /**
    * Flag that indicates whether or not <code>SetValue()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private final Vector mLmsSetValueCalled;
   
   /**
    * Flag that indicates whether or not <code>Commit()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private final Vector mLmsCommitCalled;
   
   /**
    * Flag that indicates whether or not <code>GetLastError()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private final Vector mLmsGetLastErrorCalled;
   
   /**
    * Flag that indicates whether or not <code>GetErrorString()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private Vector mLmsGetErrorStringCalled;
   
   /**
    * Flag that indicates whether or not <code>LmsGetDiagnosticCalled()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private Vector mLmsGetDiagnosticCalled;

   /**
    * Flag that indicates whether or not <code>ExecutionStateCalled()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private boolean mExecutionStateCalled = false;
   
   /**
    * Flag that indicates whether or not <code>DataTransferCalled()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private boolean mDataTransferCalled = false;
   
   /**
    * Flag that indicates whether or not <code>StateManagementCalled()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private boolean mStateManagementCalled = false;
   
   /**
    * Flag indicating whether or not the SCO was able to locate the API 
    * Adapter
    */
   private boolean mFindAPI = false;

   /**
    *  Flag that indicates whether or not <code>Initialize()</code> has been 
    *  called successfully. If SCO makes call unsuccessfully, then the flag 
    *  is set to false.
    */
   private boolean mLmsInitializeStatus = true;
   
   /**
    *  Flag that indicates whether or not <code>Terminate()</code> has been 
    *  called successfully. If SCO makes call unsuccessfully, then the flag 
    *  is set to false.
    */
   private boolean mLmsFinishStatus = true;

   /**
    * Flag that indicates whether or not <code>GetValue()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private Vector mLmsGetValueStatus;
   
   /**
    * Flag that indicates whether or not <code>SetValue()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private Vector mLmsSetValueStatus;
   
   /**
    * Flag that indicates whether or not <code>Commit()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private Vector mLmsCommitStatus;
   
   /**
    * Flag that indicates whether or not <code>GetLastError()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private Vector mLmsGetLastErrorStatus;
   
   /**
    * Flag that indicates whether or not <code>GetLastErrorString()</code> has 
    * been called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private Vector mLmsGetErrorStringStatus;
   
   /**
    * Flag that indicates whether or not <code>GetDiagnostic()</code> has been 
    * called successfully. If SCO makes call unsuccessfully, then the flag 
    * is set to false.
    */
   private Vector mLmsGetDiagnosticStatus;

   /**
    * The number of errors for the SCO being tested
    */
   private int mErrors;

   /**
    * Flag that indicates whether or not DM Elements are supported. 
    * If SCO makes call unsuccessfully, then the flag is set to false.
    */
   private Vector mDmElementsSupported;
   
   /**
    * Flag that indicates whether or not data transfer calls are supported. 
    * If SCO makes call unsuccessfully, then the flag is set to false.
    */
   private Vector mDataTransferCallsSupported;
   
   /**
    * Flag that indicates whether or not state management calls are supported. 
    * If SCO makes call unsuccessfully, then the flag is set to false.
    */
   private Vector mStateManagementCallsSupported;


   /**
    * Default constructor.
    */
   public SCOSession()
   {
      mErrors = 0;
      mDmElementsSupported = new Vector();
      mDataTransferCallsSupported = new Vector();
      mStateManagementCallsSupported = new Vector();
      mLmsGetValueCalled = new Vector();
      mLmsGetValueStatus = new Vector();
      mLmsSetValueCalled = new Vector();
      mLmsSetValueStatus = new Vector();
      mLmsCommitCalled = new Vector();
      mLmsCommitStatus = new Vector();
      mLmsGetLastErrorCalled = new Vector();
      mLmsGetLastErrorStatus = new Vector();
      mLmsGetErrorStringCalled = new Vector();
      mLmsGetErrorStringStatus = new Vector();
      mLmsGetDiagnosticCalled = new Vector();
      mLmsGetDiagnosticStatus = new Vector();
   }

   /**
    * This method is called when Initialize() is invoked successfully
    */
   public void lmsInitializedCalled()
   {
      mLmsInitializeCalled = true;
   }
   
   /**
    * This method is called when lmsFinishCalled() is invoked successfully
    */
   public void lmsFinishCalled()
   {
      mLmsFinishedCalled = true;
   }
   
   /**
    * This method is called when lmsGetValueCalled() is invoked successfully
    */
   public void lmsGetValueCalled()
   {
      mLmsGetValueCalled.addElement( true );
   }
   
   /**
    * This method is called when lmsSetValueCalled() is invoked successfully
    */
   public void lmsSetValueCalled()
   {
      mLmsSetValueCalled.addElement( true );
   }
   
   /**
    * This method is called when lmsCommitCalled() is invoked successfully
    */
   public void lmsCommitCalled()
   {
      mLmsCommitCalled.addElement( true );
   }
   
   /**
    * This method is called when lmsGetLashErrorCalled() is invoked successfully
    */
   public void lmsGetLastErrorCalled()
   {
      mLmsGetLastErrorCalled.addElement( true );
   }
   
   /**
    * This method is called when lmsGetErrorStringCalled() is invoked successfully
    */
   public void lmsGetErrorStringCalled()
   {
      mLmsGetErrorStringCalled.addElement( true );
   }
   
   /**
    * This method is called when lmsGetDiagnosticCalled() is invoked successfully
    */
   public void lmsGetDiagnosticCalled()
   {
      mLmsGetDiagnosticCalled.addElement( true );
   }


   /**
    * These methods keep track of the status (success/failure) of
    * the API calls.
    */
   
   /**
    * Keeps track of the status of Initialize().
    */
   public void setInitializeStatus()
   {
      mLmsInitializeStatus = false;
   }
   
   /**
    * Keeps track of the status of Finish().
    */
   public void setFinishStatus()
   {
      mLmsFinishStatus = false;
   }
   
   /**
    * Keeps track of the status of GetValue().
    * @param iValue - boolean indicating if the API call was successful.
    */
   public void setGetValueStatus( boolean iValue )
   {
      mLmsGetValueStatus.addElement( new Boolean(iValue) );
   }
   
   /**
    * Keeps track of the status of SetValue().
    * @param iValue - boolean indicating if the API call was successful.
    */
   public void setSetValueStatus( boolean iValue )
   {
      mLmsSetValueStatus.addElement( new Boolean(iValue) );
   }
   
   /**
    * Keeps track of the status of Commit().
    * @param iValue - boolean indicating if the API call was successful.
    */
   public void setCommitStatus( boolean iValue )
   {
      mLmsCommitStatus.addElement( new Boolean(iValue) );
   }
   
   /**
    * Keeps track of the status of GetLastError().
    * @param iValue - boolean indicating if the API call was successful.
    */
   public void setGetLastErrorStatus( boolean iValue )
   {
      mLmsGetLastErrorStatus.addElement( new Boolean(iValue) );
   }
   
   /**
    * Keeps track of the status of GetErrorString().
    * @param iValue - boolean indicating if the API call was successful.
    */
   public void setGetErrorStringStatus( boolean iValue )
   {
      mLmsGetErrorStringStatus.addElement( new Boolean(iValue) );
   }
   
   /**
    * Keeps track of the status of GetDiagnostic().
    * @param iValue - boolean indicating if the API call was successful.
    */
   public void setGetDiagnosticStatus( boolean iValue )
   {
      mLmsGetDiagnosticStatus.addElement( new Boolean(iValue) );
   }


   /**
    * The other API calls fall into two categories:
    * <ul>
    *    <li><strong>Data Transfer:</strong>
    *       <ul>
    *          <li><code>GetValue()</code></li>
    *          <li><code>SetValue()</code></li>
    *          <li><code>Commit()</code></li>
    *       </ul>
    *    </li>
    *    <li><strong>State Management:</strong>
    *       <ul>
    *          <li><code>GetLastError()</code></li>
    *          <li><code>GetErrorString()</code></li>
    *          <li><code>GetDiagnostic()</code></li>
    *       </ul>
    *    </li>
    * </ul>
    *
    * @return boolean - Flag indicating whether or not the other API calls
    *                   were made.
    */
   public boolean calledOtherAPIs()
   {
     return ( calledDataTransfer() || calledStateManagment() );     
   }


   /**
    * The Data Transfer calls are
    * <ul>
    *    <li><code>GetValue()</code></li>
    *    <li><code>SetValue()</code></li>
    *    <li><code>Commit()</code></li>
    * </ul>
    * @return boolean - Flag indicating whether or not Data Transfer calls
    *                   were made.
    */
   public boolean calledDataTransfer()
   {
      return !mLmsGetValueCalled.isEmpty() ||
             !mLmsSetValueCalled.isEmpty() ||
             !mLmsCommitCalled.isEmpty();
   }


   /**
    * The Data Transfer calls are
    * <ul>
    *    <li><code>GetLastError()</code></li>
    *    <li><code>GetErrorString()</code></li>
    *    <li><code>GetDiagnostic()</code></li>
    * </ul>
    *
    * @return boolean - Flag indicating whether or not State Management calls
    *                   were made.
    */
   public boolean calledStateManagment()
   {
      return ( ( !mLmsGetLastErrorCalled.isEmpty() ) ||
           ( !mLmsGetErrorStringCalled.isEmpty() ) ||
           ( !mLmsGetDiagnosticCalled.isEmpty() ) );     
   }


   /**
    * returns the value stored in mLmsFinishedCalled
    * @return boolean - Value indicating whether or not LMSFinish() was
    *                   invoked on the LMS.
    */
   public boolean isLMSFinished()
   {
      return mLmsFinishedCalled;
   }


   /**
    * returns the value stored in mLmsInitializeCalled
    * @return boolean - Value indicating whether or not LMSInitialize() was
    *                   invoked on the LMS.
    */
   public boolean isLMSInitialized()
   {
      return mLmsInitializeCalled;
   }


   /**
    * This method sets the findAPI flag to true.  It indicates
    * that the SCO was able to find the API Adapter provided by the
    * test suite.
    */
   public void setFindAPI()
   {
      // SCO was able to Find the LMS provided API Adapter
      mFindAPI = true;
   }


   /**
    * These methods are used to set whether or not a category of API calls were
    * called
    */
   
   /**
    * sets mExecutionStateCalled to true
    */
   public void setExecutionState()
   {
      mExecutionStateCalled = true;
   }
   
   /**
    * sets mDataTransferCalled to true
    */
   public void setDataTransfer()
   {
      mDataTransferCalled = true;
   }
   
   /**
    * sets mStateManagementCalled to true
    */
   public void setStateManagement()
   {
      mStateManagementCalled = true;
   }

   /**
    * These methods are used to get whether or not a category of API calls were
    * called
    */
   
   /**
    * returns the value stored in mExecutionStateCalled
    * @return mExecutionStateCalled
    */
   public boolean getExecutionState()
   {
      return mExecutionStateCalled;
   }
   
   /**
    * returns the value stored in mDataTransferCalled
    * @return mDataTransferCalled
    */
   public boolean getDataTransfer()
   {
      return mDataTransferCalled;
   }
   
   /**
    * returns the value stored in mStateManagementCalled
    * @return mStateManagementCalled
    */
   public boolean getStateManagement()
   {
      return mStateManagementCalled;
   }


   /**
    * This method tests to see if the SCO supports the Minimum API calls:
    * <ul>
    *    <li>Find an LMS provided API</li>
    *    <li>Initialize()</li>
    *    <li>Terminate()</li>
    * </ul>
    *
    * @return boolean - Flag indicating whether or not the SCO supports
    *                   the Minimum API calls.
    */
   public boolean supportsMinAPI()
   {
     return ( mFindAPI  &&
           ( mLmsInitializeCalled && mLmsInitializeStatus ) &&
           ( mLmsFinishedCalled && mLmsFinishStatus ) );     
   }


   /**
    * This method returns the findAPI attribute.  This attribute
    * states whether or not the SCO found the API.
    *
    * @return boolean - Flag indicating whether or not the SCO could find the
    *                   API.
    */
   public boolean supportsFindAPI()
   {
      return mFindAPI;
   }
   
   /**
    * This method determines whether or not the SCO supports the Execution 
    * State API calls:
    * <ul>
    *    <li><code>Initialize()</code></li>
    *    <li><code>Terminate()</code></li>
    * </ul>
    *
    * @return boolean - Flag indicating whether or not the SCO supports
    *                   the Execution State API calls.
    */
   public boolean supportsExecutionState()
   {
      return mLmsInitializeStatus && mLmsFinishStatus;
   }

   /**
    * This method determines whether or not the SCO supports the Data Transfer 
    * API calls:
    * <ul>
    *    <li><code>GetValue()</code></li>
    *    <li><code>SetValue()</code></li>
    *    <li><code>Commit()</code></li>
    * </ul>
    *
    * @return boolean - Flag indicating whether or not the SCO supports
    *                   the Data Transfer API calls.
    */
   public boolean supportsDataTransfer()
   {
      boolean commitFlag = true;
      boolean getFlag = true;
      boolean setFlag = true;

      // Check to see if all of the calls to LMSCommit() were succesful
      if ( !mLmsCommitCalled.isEmpty() )
      {
         // Loop through the statuses of the calls
         for ( int i = 0; i < mLmsCommitStatus.size(); i++ )
         {
            if ( commitFlag && !((Boolean)mLmsCommitStatus.elementAt(i)).booleanValue())
            {              
                  // At least one of the LMSCommit() calls were invalide
                  // SCO does not support Data Transfer calls
                  commitFlag = false;               
            }
         }
      }

      // Check to see if all of the calls to LMSGetValue() were succesful
      if ( !mLmsGetValueCalled.isEmpty() )
      {
         // Loop through the statuses of the calls
         for ( int i = 0; i < mLmsGetValueStatus.size(); i++ )
         {
            if ( getFlag && !((Boolean)mLmsGetValueStatus.elementAt(i)).booleanValue())
            {             
                  // At least one of the LMSGetValue() calls were invalide
                  // SCO does not support Data Transfer calls
                  getFlag = false;               
            }
         }
      }

      // Check to see if all of the calls to LMSSetValue() were succesful
      if ( !mLmsSetValueCalled.isEmpty() )
      {
         // Loop through the statuses of the calls
         for ( int i = 0; i < mLmsSetValueStatus.size(); i++ )
         {
            if ( setFlag && !((Boolean)mLmsSetValueStatus.elementAt(i)).booleanValue() )
            {
                  // At least one of the LMSSetValue() calls were invalide
                  // SCO does not support Data Transfer calls
                  setFlag = false;               
            }
         }
      }

      return commitFlag && getFlag && setFlag;
   }


   /**
    * This method determines whether or not the SCO supports the State 
    * Management API calls:
    * <ul>
    *    <li><code>GetLastError()</code></li>
    *    <li><code>GetErrorString()</code></li>
    *    <li><code>GetDiagnostic()</code></li>
    * </ul>
    *
    * @return boolean - Flag indicating whether or not the SCO supports
    *                   the State Management API calls.
    */
   public boolean supportsStateManagement()
   {
      boolean getLastErrorFlag = true;
      boolean getErrorStringFlag = true;
      boolean getDiagnosticFlag = true;

      // Check to see if all of the calls to LMSGetLastError() were succesful
      if ( !mLmsGetLastErrorCalled.isEmpty() )
      {
         // Loop through the statuses of the calls
         for ( int i = 0; i < mLmsGetLastErrorStatus.size(); i++ )
         {
            if ( ((Boolean)mLmsGetLastErrorStatus.elementAt(i)).booleanValue() == false )
            {
               // At least one of the LMSGetLastError() calls were invalide
               // SCO does not support Data Transfer calls
               getLastErrorFlag = false;
               break;
            }
         }
      }

      // Check to see if all of the calls to LMSGetErrorString() were succesful
      if ( !mLmsGetErrorStringCalled.isEmpty() )
      {
         // Loop through the statuses of the calls
         for ( int i = 0; i < mLmsGetErrorStringStatus.size(); i++ )
         {
            if ( ((Boolean)mLmsGetErrorStringStatus.elementAt(i)).booleanValue() == false )
            {
               // At least one of the LMSGetErrorString() calls were invalid
               // SCO does not support Data Transfer calls
               getErrorStringFlag = false;
               break;
            }
         }
      }

      // Check to see if all of the calls to LMSGetDiagnostic() were succesful
      if ( !mLmsGetDiagnosticCalled.isEmpty() )
      {
         // Loop through the statuses of the calls
         for ( int i = 0; i < mLmsGetDiagnosticStatus.size(); i++ )
         {
            if ( ((Boolean)mLmsGetDiagnosticStatus.elementAt(i)).booleanValue() == false )
            {
               // At least one of the LMSGetDiagnostic() calls were invalide
               // SCO does not support Data Transfer calls
               getDiagnosticFlag = false;
               break;
            }
         }
      }

      return getLastErrorFlag && getErrorStringFlag && getDiagnosticFlag;
   }


   /**
    * This method increments the number of errors for the
    * SCO Session.
    */
   public void setError()
   {
      mErrors++;
   }


   /**
    * This method returns the number of errors encountered
    * during the SCO Session.
    *
    * @return integer - Value of the total number of errors for the SCO
    *                   Session.
    */
   public int getErrors()
   {
      return mErrors;
   }


   /**
    * This method returns a flag that indicates is the
    * data model was used.
    *
    * @return boolean - Flag that indicates if the Data Model was used.
    */
   public boolean isDMElementsUsed()
   {
      return !mDmElementsSupported.isEmpty();
   }


   /**
    * This method returns a flag that indicates is the
    * data model was used.
    *
    * @return boolean - Flag that indicates if the Data Model was used.
    */
   public boolean isDataModelConformant()
   {
      boolean setFlag = false;
      boolean getFlag = false;

      if ( !mDmElementsSupported.isEmpty() )
      {
         // Only way that the SCO is entirely Data Model
         // is if the use of LMSGetValue() and LMSSetValue() were
         // used correctly
         // Check to see if all of the calls to LMSGetValue() were succesful
         if ( !mLmsGetValueCalled.isEmpty() )
         {
            // Non-CMI calls make .isEmpty false, but do not add to .size
            // If no CMI elements (and hence none failed) we do not want to fail
            if (mLmsGetValueStatus.isEmpty())
            {
               getFlag = true;
            }
            // Loop through the statuses of the calls
            for ( int i = 0; i < mLmsGetValueStatus.size(); i++ )
            {
               if ( ((Boolean)mLmsGetValueStatus.elementAt(i)).booleanValue() == false )
               {
                 // At least one of the LMSGetValue() calls were invalide
                 // SCO does not support Data Transfer calls
                 getFlag = false;
                 break;
               }
               // if the above 'if' statement isnt hit, hit this'n 
               getFlag = true;

            }
         }
         else
         {
            getFlag = true;
         }

         // Check to see if all of the calls to LMSSetValue() were succesful
         if ( !mLmsSetValueCalled.isEmpty() )
         {
            // Non-CMI calls make .isEmpty false, but do not add to .size
            // If no CMI elements (and hence none failed) we do not want to fail
            if (mLmsSetValueStatus.isEmpty())
            {
               setFlag = true;
            }
            // Loop through the statuses of the calls
            for ( int i = 0; i < mLmsSetValueStatus.size(); i++ )
            {
               if ( ((Boolean)mLmsSetValueStatus.elementAt(i)).booleanValue() == false )
               {
                 // At least one of the LMSSetValue() calls were invalide
                 // SCO does not support Data Transfer calls
                 setFlag = false;
                 break;
               }

               setFlag = true;

            }
         }
         else
         {
            setFlag = true;
         }
      }

      return (getFlag && setFlag);
   }


   /**
    * This method keeps track of the data model elements used by the SCO.
    * The method also checks to keeps to see if the data model element
    * is a mandatory element (from the LMS standpoint) and keeps track of
    * that information.
    *
    * @param iDmElement - Data model element that was used by the SCO.
    */
   public void setDataModel( String iDmElement )
   {

      // Check to see if the data model element is a mandatory element
      if ( (iDmElement.equalsIgnoreCase("cmi.core._children")) ||
           (iDmElement.equalsIgnoreCase("cmi.core.student_id")) ||
           (iDmElement.equalsIgnoreCase("cmi.core.student_name")) ||
           (iDmElement.equalsIgnoreCase("cmi.core.lesson_location")) ||
           (iDmElement.equalsIgnoreCase("cmi.core.credit")) ||
           (iDmElement.equalsIgnoreCase("cmi.core.lesson_status")) ||
           (iDmElement.equalsIgnoreCase("cmi.core.entry")) ||
           (iDmElement.equalsIgnoreCase("cmi.core.score._children")) ||
           (iDmElement.equalsIgnoreCase("cmi.core.score.raw")) ||
           (iDmElement.equalsIgnoreCase("cmi.core.total_time")) ||
           (iDmElement.equalsIgnoreCase("cmi.core.exit")) ||
           (iDmElement.equalsIgnoreCase("cmi.core.session_time")) ||
           (iDmElement.equalsIgnoreCase("cmi.suspend_data")) ||
           (iDmElement.equalsIgnoreCase("cmi.launch_data")) )
      {
         // Set the Conformance Category to SCO RTE 2
         // SCO - RTE 1 plus correctly uses one or more LMS mandatory
         // data elements
         mScoRTE2 = true;
      }
      else
      {
         // Set the Conformance Category to SCO RTE 3
         // SCO - RTE 1 plus correctly uses one or more LMS optional
         // data elements
         mScoRTE3 = true;
      }


      if ( !mDmElementsSupported.contains(iDmElement) )
      {
    	  // it's not there so add it
         mDmElementsSupported.addElement(iDmElement);
      }
   }


   /**
    * Methods used to help report conformance label and category
    *
    * @return mScoRTE1
    */
   public boolean isSCORTE1()
   {
      return mScoRTE1;
   }
   
   /**
    * Used to help report if conformance category that is SCO RTE 1 
    * plus correctly uses one or more LMS mandatory data elements
    *      
    * @return mScoRTE2
    */
   public boolean isSCORTE2()
   {
      return mScoRTE2;
   }
   
   /**
    * Used to help report if conformance category that is SCO RTE 1 
    * plus correctly uses one or more LMS optional data elements
    * 
    * @return mScoRTE3
    */
   public boolean isSCORTE3()
   {
      return mScoRTE3;
   }


   /**
    * Methods used to help report features supported by the SCO
    *
    * @return  mDmElementsSupported
    */
   public Vector getDataModelItems()
   {
      return mDmElementsSupported;
   }
   
   /**
    * Methods used to help report features supported by the SCO
    * 
    * @return mDataTransferCallsSupported
    */
   public Vector getDataTransferCalls()
   {
      return mDataTransferCallsSupported;
   }
   
   /**
    * Methods used to help report features supported by the SCO
    * 
    * @return mStateManagementCallsSupported
    */
   public Vector getStateMgmtCalls()
   {
      return mStateManagementCallsSupported;
   }

   /**
    * This method is used to keep track of the data transfer
    * calls made by the SCO.
    *
    * @param iCall - Actual data transfer call made by SCO.
    */
   public void setDataTransferCall( String iCall )
   {
      if ( !mDataTransferCallsSupported.contains(iCall) )
      {
         mDataTransferCallsSupported.addElement(iCall);
      }
   }


   /**
    * This method is used to keep track of the state management
    * calls made by the SCO.
    *
    * @param iCall - Actual state management call made by SCO.
    */
   public void setStateMgmtCall( String iCall )
   {
      if ( !mStateManagementCallsSupported.contains(iCall) )
      {
         mStateManagementCallsSupported.addElement(iCall);
      }
   }


   /**
    * This method is used to reset the SCO Session variables.
    */
   public void clearSession()
   {
      // Set the session back to the original state.
      mLmsFinishedCalled = false;
      mLmsInitializeCalled = false;
      
      mErrors = 0;
      mExecutionStateCalled = false;
      mDataTransferCalled = false;
      mStateManagementCalled = false;
      mFindAPI = false;
      mScoRTE1 = false;
      mScoRTE2 = false;
      mScoRTE3 = false;

      mLmsInitializeStatus = true;
      mLmsFinishStatus = true;


      mDmElementsSupported.removeAllElements();
      mDataTransferCallsSupported.removeAllElements();
      mStateManagementCallsSupported.removeAllElements();
      mLmsGetValueCalled.removeAllElements();
      mLmsGetValueStatus.removeAllElements();
      mLmsSetValueCalled.removeAllElements();
      mLmsSetValueStatus.removeAllElements();
      mLmsCommitCalled.removeAllElements();
      mLmsCommitStatus.removeAllElements();
      mLmsGetLastErrorCalled.removeAllElements();
      mLmsGetLastErrorStatus.removeAllElements();
      mLmsGetErrorStringCalled.removeAllElements();
      mLmsGetErrorStringStatus.removeAllElements();
      mLmsGetDiagnosticCalled.removeAllElements();
      mLmsGetDiagnosticStatus.removeAllElements();
   }
}