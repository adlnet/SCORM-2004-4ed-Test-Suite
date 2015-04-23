SCORM 2004 4th Edition Test Suite
=================================
The SCORM 2004 4th Edition Test Suite is a collection of tests designed to 
valitate software conforming to the SCORM 2004 4th Edtion. It includes a 
Learning Management System (LMS) Test, Content Package Test, SCO Test, and 
Manifest Test, along with a utility test to verify a content package matches 
a Content Package Test log.  

This repository is made available to allow developers to update the source 
to work in their specific environments.

## Building
### Prerequisites  
- Java 6 update 10 Java Development Kit (JDK)  
- Apache Ant 1.7.1  (Instructions [here](http://ant.apache.org/manual/install.html))

### Configuration
- Set JAVA_HOME environment variable to the root folder of Java's JDK  
	- ex: C:\Program Files\Java\jdk1.6.0_31  
	- To enable running the java compiler from any folder, add %JAVA_HOME%\bin to the system path  
- Set ANT_HOME environment variable to the root folder of Apache Ant  
	- ex: C:\apache-ant-1.7.1 
	- To enable running the ant command from any folder, add %ANT_HOME%\bin to the system path	
- Set SCORM4ED_TS111_HOME environment variable to the Test Suite folder containing the ant build scripts  
	- ex: C:\dev\SCORM-2004-4ed-Test-Suite\  
- Include a code signing certificate (keystore) in the `software_development` folder  
- Set the `build.properties` values for the code signing certificate  
```
<!-- signing certificate values -->
keystore=<!-- path to keystore -->
keystoreAlias=
keystorePassword=
```

### Building
The Test Suite uses Apache Ant build scripts. To initiate the build, navigate to the 
folder containing the Ant build scripts in a terminal and execute the following command:  
```
C:\dev\SCORM-2004-4ed-Test-Suite\software_development > ant
```

## Running
### Prerequisites
- Internet Explorer 8 (Or a lesser version)  
- Java 6 update 10 Runtime Environment (JRE)  

### Launching the Test Suite
- Navigate to the TestSuite folder 
	- ex: C:\dev\SCORM-2004-4ed-Test-Suite\software_development\TestSuite\  
- Double click main.htm  

### Test Suite Readme
There is a Readme for operating the Test Suite found at `C:\dev\SCORM-2004-4ed-Test-Suite\software_development\CTS_Readme`  
  
  
## License
   Copyright 2014 Advanced Distributed Learning

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 
## Collaboration
For those wishing to contribute to the programming efforts of the SCORM 2004 4th
Edition Test Suite, please follow this process:

### Set up
If you are not currently working with GitHub and git, follow these set up steps 
first. GitHub provides excellent help at [https://help.github.com/articles/set-up-git](https://help.github.com/articles/set-up-git)

#### Sign up for a GitHub account
If you do not already have a GitHub account, [sign up](https://github.com/signup/free).


#### Fork the ADL repository
Go to the SCORM-2004-4ed-Test-Suite repository. Fork the repository to your own account using 
the "Fork" button on the top right of SCORM-2004-4ed-Test-Suite repository page. This makes a 
copy of the SCORM-2004-4ed-Test-Suite repository. This fork gives you the ability to edit your 
version of the document without impacting the master copy.


#### Install Git (use cmd line) or Install Windows/Mac GitHub client
You need to install Git to work with a GitHub repository. If you are on a Windows machine, you can download the GitHub client app. If you use a Mac you can download the GitHub client app but will also have to download git to add a remote to the master repository. Otherwise install git from the 
git site.

__Git__  
This provides a command line client app for working with a git repository (like 
GitHub)  
Download and run [git install](http://git-scm.com/downloads)

__GitHub Client__  
GitHub Client provides a GUI interface to simplify working with a repository on 
GitHub. This does not currently support synchronizing with a master repository so 
some commands will still need to be completed using the command line.

__Mac:__ http://mac.github.com/  
__Windows:__ http://windows.github.com/


#### Clone your GitHub fork to your machine
To make edits and work on the files in the repository, clone your repository to 
your local machine using Git. The url is provided on the home page of your 
repository (ex. ```https://github.com/<your username>/xAPI-Spec/```)  

__Git__  
```git clone https://github.com/<your username>/xAPI-Spec/>```  

__GitHub Client__  
On the home screen of the client app, select your account under 'github' and 
choose the repository you want to clone. Selecting the repository from the list 
gives you an option to clone it. 

#### Add ADL repository as upstream remote
Add a remote repository to git to reference the master repository. This will make 
synchronizing with the master respository a bit easier.  

__Git__  
```git remote add upstream https://github.com/adlnet/xAPI-Spec```  

__GitHub Client__  
Currently the GitHub clients don't have a way to synchronize with the master 
repository. In order to do this, open your repository on the GitHub client 
app home screen. On the repository screen select 'tools' and 'open a shell 
here'. Alternatively use the 'Git Shell' shortcut if it was created during 
installation. **NOTE:** If you're using a Mac there is no shell shortcut so navigate to ```/your/repo/path/xAPI-Spec``` then follow the shell instructions.
  
In the shell, enter..  
```git remote add upstream https://github.com/adlnet/xAPI-Spec```  


### Workflow

#### Sync up with Master ADL Repository
Pull down changes from the master repository. This automatically does a 
fetch of the master repository and a merge into your local repository.  

__Git and GitHub Client__  
```git pull upstream master```

#### Make Changes Locally
Edit the local copy of the file, save and commit. Rule of thumb: Use commits 
like save points. Commit to indicate logical groups of edits, and places 
where the edits could be safely rolled back.  

__Git__  
```git commit -a -m "<commit message>"```  

__GitHub Client__  
The GitHub client will detect saved changes to the documents in your 
local repository and present a button to commit your edits at the top 
right of the repository screen.  

#### Push Changes to Your Repository (Origin)
Pushing your changes to your remote GitHub repository stages the files 
so that you can then make requests to the master repository to merge in 
your changes.

__Git__  
```git push origin```

__GitHub Client__  
The GitHub client has a 'sync' button at the top of the repository screen. 
This will synchronize your local and remote (origin) repository.  

#### Submit a Pull Request to Master ADL Repository (Upstream)
When you forked from the Experience API repository, a link back to the master 
repository is remembered. To send your changes back the the master repository, 
click the "Pull Request" button at the top of your repository page. This will 
direct you to a page that gives you the ability to submit a request to the 
master repository to merge in the changes you committed.



