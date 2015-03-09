package org.adl.datamodels;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * <strong>Filename:</strong>SCODataManager.java<br><br>
 * 
 * <strong>Description:</strong><br>
 * This class is responsible for maintaining the SCORM Data Model for a single
 * instance of a Sharable Content Object (SCO). The SCO Data Manager is
 * responsible for any interactions the LMS, learner or SCO may have with the
 * Data Model.<br><br>
 * 
 * <strong>Design Issues:</strong><br> none.
 * <br><br>
 * 
 * <strong>Implementation Issues:</strong><br> None
 * <br><br>
 * 
 * <strong>Known Problems:</strong> None<br><br>
 * 
 * <strong>Side Effects:</strong> None<br><br>
 * 
 * <strong>References:</strong> SCORM 2004<br>
 *  
 * @author ADL Technical Team
 */
public class SCODataManager implements Serializable
{
   /**
    * Describes the set of run-time data models managed for the SCO.
    */
   private Hashtable mDataModels = null;

   /**
    * Default constructor required for serialization support. Its only action
    * is to create a null Hashtable mDataModels.
    */
   public SCODataManager() 
   {
      // Default constructor - no explicit functionallity defined   
   }

   /**
    * Adds the identified data model to the set of run-time data models managed
    * for this SCO.  First checks the current set of managed data models
    * to ensure that the data model to be added is not aready present in the 
    * Hashtable.
    * 
    * @param iModel  Describes the run-time data model to be added.
    */
   public void addDM(int iModel)
   {
      // Create the indicated data model
      DataModel dm = DMFactory.createDM(iModel);

      if ( dm != null )
      {
         // Make sure this data model isn't already being managed
         if ( mDataModels == null )
         {
            mDataModels = new Hashtable();

            mDataModels.put(dm.getDMBindingString(), dm);
         }
         else
         {
            DataModel check = 
               (DataModel)mDataModels.get(dm.getDMBindingString());

            if ( check == null )
            {
               mDataModels.put(dm.getDMBindingString(), dm);
            }
         }
      }
   }

   /**
    * Processes an equals() request against the SCO's run-time data.
    * 
    * @param iRequest The request (<code>DMRequest</code>) being processed.
    * 
    * @return A data model error code indicating the result of this
    *         operation.
    */
   public int equals(DMRequest iRequest)
   {

      // Assume no processing errors
      int result = DMErrorCodes.NO_ERROR;

      // Make sure there is a request to process and some data model to
      // process it on
      if ( iRequest != null && mDataModels != null )
      {
         RequestToken tok = iRequest.getNextToken();

         // The first request token must be a data model token
         if ( tok.getType() == RequestToken.TOKEN_DATA_MODEL )
         {

            DataModel dm = (DataModel)mDataModels.get(tok.getValue());

            // Make sure the data model exists
            if ( dm != null )
            {
               // Process this request
               result = dm.equals(iRequest);
            }
            else
            {
               // Specified data model element does not exist
               result = DMErrorCodes.UNDEFINED_ELEMENT;
            }
         }
         else
         {
            // No data model specified
            result = DMErrorCodes.INVALID_REQUEST;
         }
      }
      else
      {
         // Nothing to process or nothing to process on
         result = DMErrorCodes.UNKNOWN_EXCEPTION;
      }

      return result;
   }

   /**
    * Retrieves a specific Data Model managed by this 
    * <code>SCODataManager</code>.
    * 
    * @param iDataModel  Describes the dot-notation binding string of the
    *                    desired data model.
    * 
    * @return The <code>DataModel</code> object associated with the
    *         requested data model.
    */
   public DataModel getDataModel(String iDataModel)
   {
      DataModel dm = null;

      if ( mDataModels != null )
      {
         dm = (DataModel)mDataModels.get(iDataModel);
      }

      return dm;
   }

   /**
    * Processes a GetValue() request against the SCO's run-time data.
    * 
    * @param iRequest The request (<code>DMRequest</code>) being processed.
    * 
    * @param oInfo    Provides the value returned by this request.
    * 
    * @return A data model error code indicating the result of this
    *         operation.
    */
   public int getValue(DMRequest iRequest, DMProcessingInfo oInfo)
   {
      // Assume no processing errors
      int result = DMErrorCodes.NO_ERROR;

      // Make sure there is a request to process and some data model to
      // process it on
      if ( iRequest != null && mDataModels != null )
      {
         RequestToken tok = iRequest.getNextToken();

         // The first request token must be a data model token
         if ( tok.getType() == RequestToken.TOKEN_DATA_MODEL )
         {
            DataModel dm = (DataModel)mDataModels.get(tok.getValue());

            // Make sure the data model exists
            if ( dm != null )
            {
               // Process this request
               result = dm.getValue(iRequest, oInfo);
            }
            else
            {
               // Specified data model element does not exist
               result = DMErrorCodes.UNDEFINED_ELEMENT;
            }
         }
         else
         {
            // No data model specified
            result = DMErrorCodes.INVALID_REQUEST;
         }
      }
      else
      {
         // Nothing to process or nothing to process on
         result = DMErrorCodes.UNKNOWN_EXCEPTION;
      }

      return result;
   }

   /** 
    * Initializes all data models being managed for this SCO.
    */
   public void initialize()
   {
      if ( mDataModels != null )
      {
         Enumeration theEnum = mDataModels.elements();

         while ( theEnum.hasMoreElements() )
         {
            DataModel dm = (DataModel)theEnum.nextElement();

            dm.initialize();
         }
      }
   }

   /**
    * Processes a SetValue() request against the SCO's run-time data.
    * 
    * @param iRequest The request (<code>DMRequest</code>) being processed.
    * 
    * @return A data model error code indicating the result of this
    *         operation.
    */
   public int setValue(DMRequest iRequest)
   {
      return setValue(iRequest, false);
   }
   
   /**
    * Processes a SetValue() request against the SCO's run-time data.
    * 
    * @param iRequest The request (<code>DMRequest</code>) being processed.
    * 
    * @param iSetBySCO Indicates whether or not the value is being set by the SCO.
    * 
    * @return A data model error code indicating the result of this
    *         operation.
    */
   public int setValue(DMRequest iRequest, boolean iSetBySCO)
   {
      // Assume no processing errors
      int result = DMErrorCodes.NO_ERROR;

      // Make sure there is a request to process and some data model to
      // process it on
      if ( iRequest != null && mDataModels != null )
      {
         RequestToken tok = iRequest.getNextToken();

         // The first request token must be a data model token
         if ( tok.getType() == RequestToken.TOKEN_DATA_MODEL )
         {
            DataModel dm = (DataModel)mDataModels.get(tok.getValue());

            // Make sure the data model exists
            if ( dm != null )
            {
               // Process this request
               result = dm.setValue(iRequest, iSetBySCO);
            }
            else
            {
               // Specified data model element does not exist
               result = DMErrorCodes.UNDEFINED_ELEMENT;
            }
         }
         else
         {
            // No data model specified
            result = DMErrorCodes.INVALID_REQUEST;
         }
      }
      else
      {
         // Nothing to process or nothing to process on
         result = DMErrorCodes.UNKNOWN_EXCEPTION;
      }

      return result;
   }

   /** 
    * Terminates all data models being managed for this SCO.
    */
   public void terminate()
   {
      if ( mDataModels != null )
      {
         Enumeration theEnum = mDataModels.elements();

         while ( theEnum.hasMoreElements() )
         {
            DataModel dm = (DataModel)theEnum.nextElement();

            dm.terminate();
         }
      }
   }

   /**
    * Processes a validate() request against the SCO's run-time data.
    * 
    * @param iRequest The request (<code>DMRequest</code>) being processed.
    * 
    * @return A data model error code indicating the result of this
    *         operation.
    */
   public int validate(DMRequest iRequest)
   {
      // Assume no processing errors
      int result = DMErrorCodes.NO_ERROR;

      // Make sure there is a request to process and some data model to
      // process it on
      if ( iRequest != null && mDataModels != null )
      {
         RequestToken tok = iRequest.getNextToken();

         // The first request token must be a data model token
         if ( tok.getType() == RequestToken.TOKEN_DATA_MODEL )
         {

            DataModel dm = (DataModel)mDataModels.get(tok.getValue());

            // Make sure the data model exists
            if ( dm != null )
            {
               // Process this request
               result = dm.validate(iRequest);
            }
            else
            {
               // Specified data model element does not exist
               result = DMErrorCodes.UNDEFINED_ELEMENT;
            }
         }
         else
         {
            // No data model specified
            result = DMErrorCodes.INVALID_REQUEST;
         }
      }
      else
      {
         // Nothing to process or nothing to process on
         result = DMErrorCodes.UNKNOWN_EXCEPTION;
      }

      return result;
   }

}  // SCODataManager
