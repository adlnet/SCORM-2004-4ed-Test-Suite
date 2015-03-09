   /*************************************************************************
       

 
   *************************************************************************/ 
   var lmsApplet;
   var cPArrayProperOrder = true;
   var driverArray = new Array();
   var cPArray = new Array();
   
   /*************************************************************************
   **
   ** Method:  logWriter()
   ** Input:   logInfo
   ** Output:  none
   **
   ** Description:
   **    This function writes calls to the HTML Log page.
   **
   *************************************************************************/ 
   function logWriter(logInfo)
   {
      parent.logFrame.appendHTML(logInfo);
   }
   
   /*************************************************************************
   **
   ** Method:  logEraser()
   ** Input:   cls
   ** Output:  none
   **
   ** Description:
   **    This function clears log information from the HTML Log page.
   **
   *************************************************************************/ 
   function logEraser(cls)
   {
	   parent.logFrame.document.getElementById('logTitle').innerHTML = cls;
   }
   
   /*************************************************************************
   **
   ** Method:  displayInterface()
   ** Input:   adl, scorm, testsuite, testlog
   ** Output:  none
   **
   ** Description:
   **    This function is responsbile for displaying test information.
   **
   *************************************************************************/ 
   function displayInterface(adl, scorm, testsuite, testlog)
   {
	   parent.logFrame.appendHTML(
       "<p style='text-align:center;'><span class='ADLTitle'>"+adl+"</span><br />" +
	   "<span class='logTitle'>"+scorm+"</span><br />" +
	   "<span class='logTitle'>"+testsuite+"</span><br />" +
	   "<span class='logTitle'>"+testlog+"</span><br /><br />");
   }  

   /*************************************************************************
   **
   ** Method:  initializeApplet()
   ** Input:   none
   ** Output:  none
   **
   ** Description:
   **    This function determines the browser and sets a variable to
   **    create a handle to the instance of the Applet. The applet used is 
   **    dependent on the browser.
   **
   *************************************************************************/
   function initializeApplet()
   {
	   DetectBrowser();
       if (IE) 
       { 
           lmsApplet = parent.controls.document.getElementById("LMSTestApplet");       		
       } 
       else 
       {
       		lmsApplet = parent.controls.document.getElementById("NNLMSTestApplet");
       		//lmsApplet = parent.controls.document.NNLMSTestApplet;            
       }
      
   }
   
    /**********************************************************************
    **
    ** function:  parseArrayFromDriver()
    ** Input:   none
    ** Output:  none
    **
    ** Description: This function breaks the String value of all Content 
    **              Packages into an array of Strings.
    **    
    **
    **********************************************************************/
    function parseArrayFromDriver()
    {
        var cPList = lmsApplet.getCPArrayString();
        var tokensRemain = true;
        var driverIndex = 0;
        var current = 0;
        var next;
		var packageName;
		
        while (tokensRemain)
        {
           next = cPList.indexOf('~', current);
		   packageName = cPList.substring(current, next);
		   
           driverArray[driverIndex] = packageName;

           driverIndex++;
           current = next;
           current++;
		   
           if (next == cPList.lastIndexOf('~'))
           {
              tokensRemain = false;
           }
        }
		driverArray.shift();		
        driverArray.sort();
		sort2(driverArray, parent.instructions.document.getElementById("possible"));
    }

    /**********************************************************************
    **
    ** function:  createOption()
    ** Input:     id
    ** Output:    newOption
    **
    ** Description:  This function creates Option objects that will be 
    **               placed into the selection boxes
    **    
    **
    **********************************************************************/
    function createOption(id_textValue)
    {
        var newOption = new Option(id_textValue);
        newOption.text =id_textValue;
        newOption.id = id_textValue;
        newOption.label = id_textValue;
        
        return newOption;
    }
    
    /****************************************************************************
    **
    ** function:  sort2()
    ** Input:   entireArray, destination
    ** Output:  none
    **
    ** Description:  This function sorts the elements chosen to be run as 
    **               test packages to place 'API', 'DMI', and 'DMB' at the top
    **               of the list
    **
    ***************************************************************************/
    function sort2(entireArray, destination)
    {
       var editedArray = new Array();
       var apiSelected = false; 
       var dmiSelected = false; 
       var dmbSelected = false;
       var ddmSelected = false;
       var index = 0;
	    var element = 0;
       var x = 0;
	   
       for ( var i = 0; i < entireArray.length; i++ )
       {
          if (entireArray[i] != "API")
          {
             if (entireArray[i] != "DMI")
             {
                if (entireArray[i] != "DMB")
                {
               	 if (entireArray[i] != "DDM")
               	 {
                      editedArray[element] = entireArray[i];
   				       element++;
               	 }
               	 else
               	 {
               		 var ddmID = entireArray[i];
               		 ddmSelected = true;
               	 }
                }
                else
                {
                   var dmbID = entireArray[i];
                   dmbSelected = true;
                }                
             }
             else
             {
                var dmiID = entireArray[i];
                dmiSelected = true;
             }
          }
          else
          {
             var apiID = entireArray[i];
             apiSelected = true;
          }
       }
      editedArray.sort();
      if (apiSelected)
      {  
         destination.options[index] = createOption(apiID);
         index++;
      }
      if (dmiSelected)
      {  
         destination.options[index] = createOption(dmiID);
         index++;
      }
      if (dmbSelected)
      {  
         destination.options[index] = createOption(dmbID);
         index++;
      }
      if (ddmSelected)
      {
         destination.options[index] = createOption(ddmID);
         index++;      	
      }
	  var totalPkgs = index + editedArray.length;
      // populate select box with data other than API, DMI, DMB and DDM
      for ( z = index ; z < totalPkgs; z++)
      {
         destination.options[z] = createOption(editedArray[x]);
		 x++;
      }
    }
   

   /**********************************************************************
   **
   ** function:  step02()
   ** Input:   none
   ** Output:  none
   **
   ** Description: This function is called once the Content Package Import
   **              process is finished.  All Packages must be imported and
   **              selected to continue with the test.
   **
   **********************************************************************/
   function step02()
   {
      var courseNum;
      var step2Successful = false;
      var chosenArray = new Array();
      chosenArray = buildCPArray();

      if (cPArrayProperOrder)
	  {
         if (cPArray.length < 1) 
         {
            alert("Please select a LMS Test Content Package to test.");
		    step2Successful = false;
         }
         else 
         {
            step2Successful = true;

            // Find out if the user selected all of the packages
            if ( cPArray.length == driverArray.length ) 
            {
               alert('All LMS Test Content Packages have been chosen.');
            }
            else 
            {
               alert( "The tester indicated that all of the LMS Test " +
                      "Content\nPackages are not being run/imported during " + 
                      "this test session.\nThe SCORM Conformance Label will " + 
                      "not be evaluated unless\nall LMS Test Content Packages "+
                      "are run.");
            }    
         }
	  }
	  
      if (!step2Successful)
      {
		 return "";
      }
	  else
	  {
	     return chosenArray;
	  }
   }  

   /**********************************************************************
    **
    ** function:  buildCPArray()
    ** Input:   none
    ** Output:  none
    **
    ** Description: This function is called by Step 2. It stores the
    **              selected package elements in cPArray.
    **
    **********************************************************************/
   function buildCPArray()
   {
      var idx = 0;
      var chosen = parent.instructions.document.getElementById("chosen");
	  var apiNotFirst = false;
	  var dmiNotSecond = false;
	  var dmbNotThird = false;
	  var ddmNotForth = false;
	  var autoSort;
	  
      for ( var i = 0; i < chosen.options.length; i++ ) 
      {
         cPArray[idx] = chosen.options[i].id;
         idx++;
      }
	  
      if ( cPArray.length == driverArray.length )
      {
      	if (cPArray[0] != "API")
         {
			apiNotFirst = true;
         }
         if (cPArray[1] != "DMI")
         {
			dmiNotSecond = true;
         }
         if (cPArray[2] != "DMB")
         {
	        dmbNotThird = true;
         }
         if (cPArray[3] != "DDM")
         {
         	ddmNotForth = true;
         }
		 
		 if ( (apiNotFirst) || (dmiNotSecond) || (dmbNotThird)  || (ddmNotForth) )
		 {
            autoSort = confirm("If testing all LMS Test Content Packages " +
                               "you must select 'API', 'DMI', 'DMB', and 'DDM' to be tested " + 
                               "first and in that order.\n" + 
						       "Would you like to do this now?");
	        if (autoSort)
	        {
		       sort2(cPArray, chosen);
		       buildCPArray();
	        }
	        else
	        {
		       cPArrayProperOrder = false;
	        }
         }
		 else
		 {
			cPArrayProperOrder = true;
		 }
		 
		 return cPArray;
      }
	  else
	  {
		 return cPArray;
	  }
   }

   /**********************************************************************
    **
    ** function:    contains() 
    ** Input:       list, id       
    ** Output:      bool
    **
    ** Description: This function checks the selection boxes to see if
    **              an instance of a package already exists
    **
    **********************************************************************/
    function contains(list,id)
    {
        var bool = false;
        for ( var i = 0; i < list.length; i++ )
        {
            var test = list[i].text;
            if ( test == id )
            {
                bool = true;
                break;
            }    
            else
            {
                bool = false;
            }
        }
        return bool;
    }
       
   /**********************************************************************
    **
    ** function:  moveItem()
    ** Input:   from, to, func

    ** Output:  none
    **
    ** Description: This function handles all the moving of packages from
    **              one selection box to another
    **
    **********************************************************************/
    function moveItem(from, to, func)
    {   
        isSelected = false;
            
        for ( var i = 0; i < from.options.length; i++ )
        {
            if ( from.options[i].selected )
            {
                isSelected = true;
                to.options[to.length] = createOption(from.options[i].id);
                from.options[i] = null;
                // must decrement i because of null statement
                i--;
            }
        }
        
        // sort 'possible' select box when removing from 'chosen' select box
        if (func == "remove")
        {
           if (isSelected)
           {              
              var array = new Array();
              for ( var i = 0; i < to.options.length; i++ )
              {
                 array[i] = to.options[i].id;
              }
              array.sort();
              sort2(array, to);
           }
        }
     }
  
   /**********************************************************************
    **
    ** function:  addItem()
    ** Input:   from, to
    ** Output:  none
    **
    ** Description: This function checks for package dependencies, then
    **              calls moveItem() to add selected packages to the 
    **              desired selection box
    **
    **********************************************************************/
    function addItem(from, to)
    {
        for ( var i = 0; i < from.options.length; i++ )
        {
            if ( from.options[i].selected )
            {
                
                if ( from.options[i].id == "OB-03b" )
                {
                    // OB3b is dependent upon OB3a, so a check must be made to
                    // determine if OB3a is selected and select it if it is not already
                    if ( !contains( to, "OB-03a" ) )
                    {
                        from.options[i-1].selected = true;  
                    }
                }
                else if ( from.options[i].id == "OB-03c" )
                {
                    // OB3c is dependent upon OB3b, so a check must be made to
                    // determine if OB3b is selected and select it if it is not already               
                    if ( !contains ( to, "OB-03b" ) )
                    {
                        from.options[i-1].selected = true;
                        // OB3b is dependent upon OB3a, so a check must be made to
                        // determine if OB3a is selected and select it if it is not already
                        if ( !contains ( to, "OB-03a" ) )
                        {
                           from.options[i-2].selected = true;
                        }
                    } 
                }
                else if ( from.options[i].id == "OB-09b" )
                {
                    // OB9b is dependent upon OB9a, so a check must be made to
                    // determine if OB9a is selected and select it if it is not already               
                    if ( !contains ( to, "OB-09a" ) )
                    {
                        from.options[i-1].selected = true;
                    }
                }
                else if ( from.options[i].id == "SX-11b" )
                {
                    // SX11b is dependent upon SX11a, so a check must be made to
                    // determine if SX11a is selected and select it if it is not already
                    if ( !contains( to, "SX-11a" ) )
                    {
                        from.options[i-1].selected = true;  
                    }
                }
                else if ( from.options[i].id == "SX-11c" )
                {
                    // SX11c is dependent upon SX11b, so a check must be made to
                    // determine if SX11b is selected and select it if it is not already               
                    if ( !contains ( to, "SX-11b" ) )
                    {
                        from.options[i-1].selected = true;
                        // SX11b is dependent upon SX11a, so a check must be made to
                        // determine if SX11a is selected and select it if it is not already
                        if ( !contains ( to, "SX-11a" ) )
                        {
                           from.options[i-2].selected = true;
                        }
                    } 
                }
                else if ( from.options[i].id == "CO-07b" )
                {
                    // CO-08b is dependent upon CO-08a, so a check must be made to
                    // determine if CO-08a is selected and select it if it is not already
                    if ( !contains( to, "CO-07a" ) )
                    {
                        from.options[i-1].selected = true;  
                    }
                }
                
            }
        }
  
        moveItem(from, to, "add");

        if (!isSelected)
        {
            alert("LMS Test Content Package(s) must be selected from the Available "+
                  "Test Packages selection box in order to perform Add.");
        }
    }

   /**********************************************************************
    **
    ** function:  addAll()
    ** Input:   from, to
    ** Output:  none
    **
    ** Description: This function selects all packages, then calls moveItem()
    **              to move them to the appropriate box
    **
    **********************************************************************/
   function addAll(from, to)
   {
      for ( var i = 0; i < from.options.length; i++ ) 
      {
         from.options[i].selected = true;
      }
     
      moveItem(from, to, "add");
   }

   /**********************************************************************
    **
    ** function:  removeItem()
    ** Input:   from, to
    ** Output:  none
    **
    ** Description: This function calls moveItem() to move selected 
    **              packages to the appropriate box
    **
    **********************************************************************/
   function removeItem(from, to)
   {
      moveItem(from, to, "remove");
      
      if (!isSelected)
      {
         alert("LMS Test Content Package(s) must be selected from the Chosen Test "+
               "Packages selection box in order to perform Remove.");
      }
   }

   /**********************************************************************
    **
    ** function:  removeAll()
    ** Input:   from, to
    ** Output:  none
    **
    ** Description: This function selects all packages, then calls moveItem()
    **              to move them to the appropriate box
    **
    **********************************************************************/
    function removeAll(from, to)
    {
        for ( var i = 0; i < from.options.length; i++ )
        {
            from.options[i].selected = true;
        }
     
        moveItem(from, to, "remove");
    }

   /**********************************************************************
    **
    ** function:  enableNewLoadButtons()
    ** Input:   none
    ** Output:  none
    **
    ** Description: When the applet is done loading this is called to 
	**              enable the buttons and write the log titles
    **
    **********************************************************************/
   function enableNewLoadButtons()
   {
      parent.instructions.document.getElementById('New').disabled=false;
      parent.instructions.document.getElementById('Load').disabled=false;

   }

   /****************************************************************************
    **
    ** function:  writeCurUserInstr()
    ** Input:   iMsg
    ** Output:  none
    **
    ** Description:  This function will display the Current User Instructions
    **               passed from the Applet.
    ****************************************************************************/
   function writeCurUserInstr(iMsg)
   {
	   parent.currentInstructions.document.getElementById("userInstruction").innerHTML = iMsg;
   }
