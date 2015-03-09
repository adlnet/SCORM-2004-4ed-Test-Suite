/*************************************************************************
**
** Filename:  About.js
**
** File Description:
**    Abstract versioning info for all Test Suite about pages.
**
** Author: ADLI Project
**
** Module/Package Name:  none
** Module/Package Description: none
**
** Design Issues:
**
** Implementation Issues:
** Known Problems:
** Side Effects:
**
** References: ADL SCORM
**
***************************************************************************
        

 
**************************************************************************/

//global variables
var version = "Version 1.1.1";
var scormVersion = "2004 4<sup>th</sup> Edition";
var certificationCode =
         "<center>" +
         "   <table border='1' cellspacing='3' width='550px'>" +
         "      <tr >" +
         "         <td id='tableColor'>" +
         "             <p>Certification Note: Successful outcome of this" +
         "               test does not constitute ADL Certification " +
         "               unless an ADL Certification Auditor" +
         "               conducted the test.</p>" +
         "         </td>" +
         "      </tr>" +
         "   </table>" +
         "</center>";



/*******************************************************************************
**
** Function: displayVersion()
** Inputs:  None
** Return:  None
**
** Description:
**    Write the version number to the html page.
**
*******************************************************************************/
function displayVersion()
{
   document.write( version );
}

/*******************************************************************************
**
** Function: displayScormVersion()
** Inputs:  None
** Return:  None
**
** Description:
**    Write the SCORM version number to the html page.
**
*******************************************************************************/
function displayScormVersion()
{
   document.write( scormVersion );
}

/*******************************************************************************
**
** Function: displayTestSuiteVersion()
** Inputs:  None
** Return:  None
**
** Description:
**    Write the test type (Auditor/Self Test) to the html page.
**
*******************************************************************************/
function displayTestSuiteVersion()
{
   document.write( version );
}


/*******************************************************************************
**
**Function: displayCertification()
**Inputs:  None
**Return:  NONE
**
**Description:
**   Write the certification note.
**
*******************************************************************************/
function displayCertification()
{
    document.write( certificationCode )
}

