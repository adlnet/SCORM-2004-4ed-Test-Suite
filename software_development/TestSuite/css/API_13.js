
/*******************************************************************************
**
** This file is being presented to Content Developers, Content Programmers and
** Instructional Designers to demonstrate one way to abstract API calls from the
** actual content to allow for uniformity and reuse of content fragments.
**
** The purpose in wrapping the calls to the API is to (1) provide a
** consistent means of finding the LMS API adapter within the window
** hierarchy, (2) to ensure that the method calls are called correctly by the
** SCO and (3) to make possible changes to the actual API Specifications and
** Standards easier to implement/change quickly.
**
** This is just one possible example for implementing the API guidelines for
** runtime communication between an LMS and executable content components.
** There are many other possible implementations.
**
*******************************************************************************/

// 1.3 API

// define the log message type constants
// _FAILED is the only one used in this file
var _FAILED = 3;      //  3 = conformance check failure
var logWindow = opener.logWindow;

var mCalledOnce = false;

/**************************************************************************
**
** Function: Initialize()
** Inputs:  String - Input parameter.  Should be empty string ("")
** Return:  CMIBoolean true if the initialization was successful, or
**          CMIBoolean false if the initialization failed.
**
** Description:
** Initialize communication with LMS by calling the Initialize
** function which will be implemented by the LMS.
**
//**************************************************************************/
function Initialize(iInParameter)
{
   var result = "";

   var api13 = findAPI(window);

   // Fail if: No parameter, OR if the parameter is anything other than
   // an empty string
   if ((iInParameter == null) ||
       ((iInParameter != null) && (iInParameter.length != 0)))
   {
      result = api13.Initialize(iInParameter, false);
   }
   else
   {
      result = api13.Initialize(iInParameter, true);
   }

   return result.toString();
}

/**************************************************************************
**
** Function: Terminate()
** Inputs:  String - Input parameter.  Should be empty string ("")
** Return:  CMIBoolean true if termination was successful, or
**          CMIBoolean false if the termination failed.
**
** Description:
** Terminate communication with LMS by calling the Initialize
** function which will be implemented by the LMS.
**
**************************************************************************/
function Terminate(iInParameter)
{
   var api13 = findAPI(window);
   var result = "";

   // Fail if: No parameter, OR if the parameter is anything other than
   // an empty string
   if ((iInParameter == null) ||
       ((iInParameter != null) && (iInParameter.length != 0)))
   {
      result = api13.Terminate(iInParameter, false);
   }
   else
   {
      result = api13.Terminate(iInParameter, true);
   }

   return result.toString();
}

/**************************************************************************
**
** Function: GetValue()
** Inputs:  Name of Element to retrieve
** Return:  Result of the getValue call
**
** Description:
** This funciton takes in the name of the element and returns the
** appropriate return value.
**
**************************************************************************/
function GetValue( name )
{
   var api13 = findAPI(window);
   var result = "";

   // Fail if no parameter received
   if ((name == null) || (name == ""))
   {
      // CALL GetValue
      result = api13.GetValue( name, false );
   }
   else
   {
      // CALL GetValue
      result = api13.GetValue( name, true );
   }

   // Needed for Netscape
   if (result == null)
   {
      result = "";
   }

   return result.toString();
}

/**************************************************************************
**
** Function: SetValue()
** Inputs:  Element name and value to set it to
** Return:  Status of the call
**
** Description:
** This setValue call excepts the value and checks
**
***************************************************************************/
function SetValue( name, value )
{
   var api13 = findAPI(window);

   // Ensures a literal string - specifically for the case where the SCO
   // uses a string object to create the value to be set
   if (value != null)
   {
      value = value.toString();
   }

   // Fail if anything other than two parameters received
   if ((name == null) || (value == null))
   {
     // CALL SetValue with boolean set to false
     var result = api13.SetValue( name, value, false );
   }
   else
   {
     // CALL SetValue with boolean set to true
     var result = api13.SetValue( name, value, true );
   }
   
   return result.toString();
}

/**************************************************************************
**
** Function: Commit()
** Inputs:  String - Input parameter.  Should be empty string ("")
** Return:  CMIBoolean true if the data was successfully persisted 
**          to a long-term data store.
**          CMIBoolean false if the data was unsuccessfully persisted 
**          to a long-term data store.
**
** Description:
** Requests forwarding to the persistent data store any data 
**
***************************************************************************/
function Commit(iInParameter)
{
   var api13 = findAPI(window);
   var result = "";

   // Fail if: No parameter, OR if the parameter is anything other than
   // an empty string
   if ((iInParameter == null) ||
       ((iInParameter != null) && (iInParameter.length != 0)))
   {
      result = api13.Commit(iInParameter, false);
   }
   else
   {
      result = api13.Commit(iInParameter, true);
   }

   return result.toString();
}

/**************************************************************************
**
** Function: GetLastError()
** Inputs:  None
** Return:  The error code reflecting the current error state of the API Instance
**
** Description:
** Requests the error code for the current error state of the API Instance.
**
**************************************************************************/
function GetLastError()
{
   var api13 = findAPI(window);

   // CALL GetLastError
   var result = api13.GetLastError();
   return result.toString();
}

/**************************************************************************
**
** Function: GetErrorString()
** Inputs:  String - The characterstring of the error code (integer value)
**          corresponding to an error message
** Return:  A textual description of the current error state
**
** Description:
** Retrieves a textual description of the current error state
**
**
**************************************************************************/
function GetErrorString( errCode )
{
   var api13 = findAPI(window);

   // Fail if no parameter received
   if (errCode == null)
   {
      // CALL GetErrorString and fail
      var result = api13.GetErrorString( errCode, false );
   }
   else
   {
      // CALL GetErrorString and accept parameter
      var result = api13.GetErrorString( errCode, true );
   }
   
   return result.toString();
}

/**************************************************************************
**
** Function: GetDiagnostic()
** Inputs:  String - May be an error code, but is not limited to just error codes
** Return:  CMIBoolean true if the initialization was successful, or
**          CMIBoolean false if the initialization failed.
**
** Description:
** Initialize communication with LMS by calling the Initialize
** function which will be implemented by the LMS.
**
**************************************************************************/
function GetDiagnostic( error )
{
   var api13 = findAPI(window);

   // Fail if no parameter received
   if (error == null)
   {
      // CALL GetDiagnostic and fail
      var result = api13.GetDiagnostic( error, false );
   }
   else
   {
      // CALL GetDiagnostic and accept parameter
      var result = api13.GetDiagnostic( error, true );
   }
   return result.toString();
}

/*******************************************************************************
**
** This function looks for an object named API in parent and opener windows
**
** Inputs:  Object - The Window Object
**
** Return:  Object - If the API object is found, it's returned, otherwise null
**          is returned
**
*******************************************************************************/
function findAPI(win)
{
   win = window.opener;
   return win.API_1484_11;
}

/**************************************************************************
**
** Function: version12APISCO()
** Inputs:  None
** Return:  Empty String
**
** Description:
** This method will be called if SCORM Version 1.2 API methods are called.
** It will simply end the SCO test.
**
//**************************************************************************/
function version12APISCO()
{
   var api = findAPI(window);
   var result = "";

   if (!mCalledOnce)
   {

      mCalledOnce = true
      api.doWriteLogEntry("0",_FAILED,"The Test Suite detected a SCO that is " +
                              "using SCORM Version 1.2 API Methods");

   }

   return result.toString();
}


/**************************************************************************
**
** Function: version12APICP()
** Inputs:  None
** Return:  Empty String
**
** Description:
** This method will be called if SCORM Version 1.2 API methods are called.
** It will simply end the SCO test currently being run by Content Package.
**
//**************************************************************************/
function version12APICP()
{
   var api = findAPI(window);
   var result = "";

   if (!mCalledOnce)
   {
      mCalledOnce = true
      api.doWriteLogEntry("0",_FAILED,"The Test Suite detected a SCO that is " +
                              "using SCORM Version 1.2 API Methods");
   }

   return result.toString();
}

