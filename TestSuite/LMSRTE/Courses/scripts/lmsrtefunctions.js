
/*******************************************************************************
**
** Filename:  lmsrtefunctions.js
**
** File Description:  This file contains several javascript variable definitions
**                   and functions that are used commonly by all of the SCO HTML
**                   files in the LMS Runtime Environment Conformance Test.  It
**                   is intended to be included in each SCO HTML file.
**
** Author: ADL Technical Team
**
** Contract Number:
**
**
** Design Issues:   None
** Implementation Issues:  None

** Known Problems: None
** Side Effects:  None
**
** References:
**
*******************************************************************************/

// Define exception/error codes
var _NoError = "0";

// page scoped variable definitions
var apiHandle = null;
var findAPITries = 0;
var noAPIFound = false;
var API = null;
var LMSDriver = null;
var url = window.location.href;
var query1 = "";
var query2 = "";

// this variable stores a value returned by the LMS
var lmsReturned = null;
var launchTestPassed = false;
var functionTestPassed = false;

// Data Model Conformance State values
var _Debug = false;  // set this to false to turn debugging off

//define the log message type constants
var _INFO = 0;  //  0 - informational (diagnostic, trace, etc.)
var _WARNING = 1;  //  1 - warning
var _PASSED = 2;  //  2 - conformance check passed
var _FAILED = 3;  //  3 - conformance check failure
var _TERMINATE = 4;  //  4 - test suite termination due to nonconformance or
                      //      error
var _CONFORMANT = 5;  //  5 - subject is found to be conformant
var _OTHER = 9;  //  9 - other

// define which log to write to
var _DETAILED_LOG = "Detailed";
var _SUMMARY_LOG = "Summary";
var _BOTH_LOG = "Both";
var _TERMINATED = "terminated";

// local variable definitions

// we'll track the status of the test using a state variable called scoStatus
// This is set by each SCO as it progresses through the test.
var scoStatus = null;
/*******************************************************************************
**
** Function: initLMSDriver()
** Inputs:  None
** Return:  None
**
**  Description: Initializes the LMSDriver variable. It is dependant upon which 
**  browser is being used for which applet will be implemented.
*******************************************************************************/
function initLMSDriver() 
{
   DetectBrowser();	
   if (IE) {
      LMSDriver = window.document.getElementById('LMSTestSCODriver');	
   } 
   else 
   {
      LMSDriver = window.document.getElementById('NNLMSTestSCODriver');		
   }
}
/*******************************************************************************
**
** Function: setScoStatus()
** Inputs:  param
** Return:  None
**
**  Description:
**
*******************************************************************************/
function setScoStatus(param) 
{
	initLMSDriver();
	LMSDriver.setSCOStatus(param);	
}
/*******************************************************************************
**
** Function: getScoStatus()
** Inputs:  None
** Return:  String
**
**  Description:
**
*******************************************************************************/
function getScoStatus() 
{
	var status;
	initLMSDriver();
	status = LMSDriver.getScoStatus();
	return status;
}
/*******************************************************************************
 **
 ** Function: writeLogEntry(type, msg)
 ** Inputs:  type - must be one of the following (constants defined above:
 **                      _INFO    - informational (diagnostic, trace, etc.)
 **                      _WARNING - warning
 **                      _PASSED  - conformance check passed
 **                      _FAILED  - conformance check failure
 **                      _TERMINATE - terminating due to nonconformance or error
 **                      _CONFORMANT - subject is conformant
 **                      _OTHER      - display no icon and use default font.
 **          msg - string containing log message
 **
 ** Return:  None
 **
 **  Description: This function displays a test suite log message.  Note: the
 **  LogWriterApplet must be present in the HTML file that this script is
 **  included in and be identified by the logWriter object id.
 **
 *******************************************************************************/
function writeLogEntry(log, type, msg) 
{
	LMSDriver.writeToLog(log, type, msg);
}
/**********************************************************************
**
** Function setParameters()
** Inputs: None
** Return: None
**
** Description:
** Sets the global variables to track the parameters coming in.  We
** will assume that uri fragments will not be used for the parameters.
** We also expect the parameters to be in the format "?tc=<string>&act=<num>",
** where <string> represents the name associated with the test case
** and <num> represents the number associated with the activity.
**
**********************************************************************/
function setParameters() 
{
   var queries = new RegExp("tc=([^&]+)\\&act=([^&^#]+)").exec(url);
   if ( queries != null && queries.length > 2 )
   {
      query1 = queries[1];
	  query2 = queries[2];
	  return true;
   }
   else
   {
      var message = "The url did not have the parameters from the manifest appended" +
	                " or the parameters were not formatted correctly - url: " + url;
	  if ( LMSDriver != null )
      {
		 writeLogEntry(_BOTH_LOG, _FAILED, message);
         terminateTest();
      }
	  else
	  {
         alert(message);
	  }
      return false;
   }
}

/******************************************************************************
 **
 ** Function testSCOLaunch()
 ** Inputs:  None
 ** Return: Boolean
 **
 ** Description:
 **
 **
 ******************************************************************************/
function testSCOLaunch() 
{
   initLMSDriver();
   if ( setParameters() )
   {
      var result = LMSDriver.evalID( query1 + "!" + query2 );
      return result;
   }
   return false;
}
/*******************************************************************************
 **
 ** Function terminateTest()
 ** Inputs:  None
 ** Return:  None
 **
 ** Description:
 ** This function terminates the current test when a non-conformance
 ** condition is encountered.
 **
 ******************************************************************************/
function terminateTest() 
{
	LMSDriver.terminateTest();
}
/*******************************************************************************
 **
 ** Function handleAPINotFound()
 ** Inputs:  None
 ** Return:  None
 **
 ** Description:
 ** This function is called when the API object is not found, or is null when a
 ** non-null value is expected.  It logs an appropriate error and terminates the
 ** current test.
 **
 *******************************************************************************/
function handleAPINotFound() 
{
	writeLogEntry(_BOTH_LOG, _FAILED, "Unable to locate the LMS API object");
	terminateTest();
}

////////////////////////////////////////////////////////////////////////////////
//////                                                                    //////
//////   Functions used to interact directly with the test subject LMSs   //////
//////   API Functions                                                    //////
//////                                                                    //////
////////////////////////////////////////////////////////////////////////////////
/*******************************************************************************
**
** Function: doInitialize()
** Inputs:  param - parameter to be given when calling the LMSs Initialize()
**                  function.
** Return:  None
**
** Description:
** Initialize communication with LMS by calling the Initialize
** function which will be implemented by the LMS.
**
*******************************************************************************/
function doInitialize(param) 
{
	var api = getAPIHandle();
	initLMSDriver();
	if (api == null) {
		alert("Unable to locate the LMS's API Implementation.\nInitialize() " + "was not successful.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	try {
      // call Initialize to the LMSs API
		var result = api.Initialize(param);
		lmsReturned = result.toString();
	}
	catch (e) {
		writeLogEntry(_DETAILED_LOG, _FAILED, "Initialize( String ) NOT " + "found.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	return lmsReturned;
}
///////////////////////////////////////////////////////////////////////////////////////
//these functions copied in from APIWrapper.js
////////////////////////////////////////////////////////////////////////////////////////
/******************************************************************************
**
** Function getAPIHandle()
** Inputs:  None
** Return:  value contained by APIHandle
**
** Description:
** Returns the handle to API object if it was previously set,
** otherwise it returns null
**
*******************************************************************************/
function getAPIHandle() 
{
   if (apiHandle == null) 
   {
	  if (noAPIFound == false) 
      {
         apiHandle = getAPI();
	  } 
   }
   return apiHandle;
}
/*******************************************************************************
**
** Function findAPI(win)
** Inputs:  win - a Window Object
** Return:  If an API object is found, it's returned, otherwise null is returned
**
** Description:
** This function looks for an object named API in parent and opener windows
**
*******************************************************************************/
function findAPI(win) 
{
   //var browser = navigator.userAgent.toLowerCase();
   while ((win.API_1484_11 == null) && (win.parent != null) && (win.parent != win)) 
   {
      findAPITries++;
      // Note: 500 is a number based on the IEEE API Standards.
	  if (findAPITries > 500) 
      {
	     alert("Error finding API -- too deeply nested.");
		 return null;
	  }
	  win = win.parent;
	}
	return win.API_1484_11;	
}
/*******************************************************************************
**
** Function getAPI()
** Inputs:  none
** Return:  If an API object is found, it's returned, otherwise null is returned
**
** Description:
** This function looks for an object named API, first in the current window's
** frame hierarchy and then, if necessary, in the current window's opener window
** hierarchy (if there is an opener window).
**
*******************************************************************************/
function getAPI() 
{
   var theAPI = findAPI(window);
   if ((theAPI == null) && (window.opener != null) && (typeof (window.opener) != "undefined")) 
   {
      theAPI = findAPI(window.opener);
   }
   if (theAPI == null) {
      alert("RTE - Can not locate API adapter");
	  noAPIFound = true;
   }
   return theAPI;
}
///////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
/*******************************************************************************
**
** Function: doTerminate()
** Inputs:  param - parameter to be given when calling the LMSs Terminate()
**                  function.
** Return:  None
**
** Description:
** Terminate communication with LMS by calling the Terminate
** function which will be implemented by the LMS.
**
*******************************************************************************/
function doTerminate(param) 
{
	var api = getAPIHandle();
	if (api == null) {
		alert("Unable to locate the LMS's API Implementation.\nTerminate() " + "was not successful.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	try {
      // call Terminate to the LMSs API
		var result = api.Terminate(param);
		lmsReturned = result.toString();
	}
	catch (e) {
		writeLogEntry(_DETAILED_LOG, _FAILED, "Terminate( String ) NOT found.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	return lmsReturned;
}
/*******************************************************************************
**
** Function: doCommit()
** Inputs:  param - parameter to be given when calling the LMSs Commit()
**                  function.
** Return:  None
**
** Description:
** Commit communication with LMS by calling the Commit
** function which will be implemented by the LMS.
**
*******************************************************************************/
function doCommit(param) 
{
   var api = getAPIHandle();
   if (api == null) 
   {
      alert("Unable to locate the LMS's API Implementation.\nCommit() " + "was not successful.");
   	  terminateTest();
   	  lmsReturned = "";
	  //return;
   }
   try 
   {
      // call Commit to the LMSs API
      var result = api.Commit(param);
	  lmsReturned = result.toString();
   }
   catch (e) 
   {
      writeLogEntry(_DETAILED_LOG, _FAILED, "Commit( String ) NOT found.");
	  terminateTest();
	  lmsReturned = "";
	  //return;
   }
   return lmsReturned;
}
/*******************************************************************************
**
** Function: doGetValue()
** Inputs:  param - parameter to be given when calling the LMSs GetValue()
**                  function.
** Return:  None
**
** Description:
** GetValue communication with LMS by calling the GetValue
** function which will be implemented by the LMS.
**
*******************************************************************************/
function doGetValue(param) 
{
	var api = getAPIHandle();
	if (api == null) 
    {
		alert("Unable to locate the LMS's API Implementation.\nGetValue() " + "was not successful.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	try 
    {
      // call GetValue to the LMSs API 
		var result = api.GetValue(param);
		lmsReturned = result.toString();
	}
	catch (e) 
    {
		writeLogEntry(_DETAILED_LOG, _FAILED, "GetValue( String ) NOT found.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	return lmsReturned;
}
/*******************************************************************************
**
** Function: doSetValue()
** Inputs:  param - parameter to be given when calling the LMSs SetValue()
**                  function.
** Return:  None
**
** Description:
** SetValue communication with LMS by calling the SetValue
** function which will be implemented by the LMS.
**
*******************************************************************************/
function doSetValue(param1, param2) 
{
	var api = getAPIHandle();
	if (api == null) 
    {
		alert("Unable to locate the LMS's API Implementation.\nSetValue() " + "was not successful.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	try 
    {
      // call SetValue to the LMSs API
	  var result = api.SetValue(param1, param2);
	  lmsReturned = result.toString();
	}
	catch (e) 
    {
		writeLogEntry(_DETAILED_LOG, _FAILED, "SetValue( String, String ) " + "NOT found.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	return lmsReturned;
}
/*******************************************************************************
**
** Function: doGetLastError()
** Inputs:  param - parameter to be given when calling the LMSs GetLastError()
**                  function.
** Return:  None
**
** Description:
** GetLastError communication with LMS by calling the GetLastError
** function which will be implemented by the LMS.
**
*******************************************************************************/
function doGetLastError() 
{
	var api = getAPIHandle();
	if (api == null) 
    {
		alert("Unable to locate the LMS's API Implementation.\nGetLastError() " + "was not successful.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	try 
    {
      // call GetLastError to the LMSs API
		var result = api.GetLastError();
		lmsReturned = result.toString();
	}
	catch (e) 
    {
		writeLogEntry(_DETAILED_LOG, _FAILED, "GetLastError() NOT found.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	return lmsReturned;
}
/*******************************************************************************
**
** Function: doGetErrorString()
** Inputs:  param - parameter to be given when calling the LMSs GetErrorString()
**                  function.
** Return:  None
**
** Description:
** GetErrorString communication with LMS by calling the GetErrorString
** function which will be implemented by the LMS.
**
*******************************************************************************/
function doGetErrorString(param) 
{
	var api = getAPIHandle();
	if (api == null) 
    {
		alert("Unable to locate the LMS's API Implementation.\nGetErrorString() " + "was not successful.");
		terminateTest();
		lmsReturned = "";
	}     
	try 
    {
      // call GetErrorString to the LMSs API
		var result = api.GetErrorString(param);
		lmsReturned = result.toString();
	}
	catch (e) 
    {
		writeLogEntry(_DETAILED_LOG, _FAILED, "GetErrorString( String ) " + "NOT found.");
		terminateTest();    
		lmsReturned = "";
	}
	return lmsReturned;
}
/*******************************************************************************
**
** Function: doGetDiagnostic()
** Inputs:  param - parameter to be given when calling the LMSs GetDiagnostic()
**                  function.
** Return:  None
**
** Description:
** GetDiagnostic communication with LMS by calling the GetDiagnostic
** function which will be implemented by the LMS.
**
*******************************************************************************/
function doGetDiagnostic(param) 
{
	var api = getAPIHandle();
	if (api == null) 
    {
		alert("Unable to locate the LMS's API Implementation.\nGetDiagnostic() " + "was not successful.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	try 
    {
        // call GetDiagnostic to the LMSs API
		var result = api.GetDiagnostic(param);
		lmsReturned = result.toString();
	}
	catch (e) 
    {
		writeLogEntry(_DETAILED_LOG, _FAILED, "GetDiagnostic( String ) " + "NOT found.");
		terminateTest();
		lmsReturned = "";
		//return;
	}
	return lmsReturned;
}
function callTerminate() 
{
	// test for a successful call to Terminate()
	writeLogEntry(_DETAILED_LOG, _INFO, "Attempting to call Terminate(&quot;&quot;)");
	var result = driver.testTerminate("") && result;
}
function printDate() 
{
	var currentDate = new Date();
	writeLogEntry(_BOTH_LOG, _INFO, "@@@  ");
	writeLogEntry(_BOTH_LOG, _INFO, "@@@  time = " + currentDate.toLocaleString());
	writeLogEntry(_BOTH_LOG, _INFO, "@@@  ");
}
