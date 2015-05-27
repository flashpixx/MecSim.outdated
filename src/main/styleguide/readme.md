# Styleguide

The styleguide of the sources are checked with the [Maven Checkstyle Plugin](http://checkstyle.sourceforge.net).

## Unit-Tests

* [jUnit](http://junit.org/) test are stored within ```src/test/java```
* all test-classes are stored in the same package structure like test testing-class
* test-classes uses the naming-convention ```Test-<classname>```


## Use Code-Style in IntelliJ

* remove or rename the current ```codeStyleSettings.xml``` and ```encodings.xml``` in your ```mecsim/.idea``` folder
* set a symlink of custom MecSim code and encoding style e.g on unix systems ```ln -s <path_to_mecsim_project_directory>/src/main/styleguide/intellij/codeStyleSettings.xml> <path_to_mecsim_project_directory>/.idea```
* selecting 'Code -> Reformat Code' in your Intellij IDEA menu reformats your code according to the new settings


## Definition

### File & Directory Structure

* files & directories are in lower-case
* each directory represented a semantic structure
* spaces and additional character in the names are not allowed - use only the [7-bit ASCII code](http://en.wikipedia.org/wiki/ASCII#7-bit) without any national encoding
* spaces must be replaces by an underscore (```_```)


### Repository

* use an existing email address and a full-qualified name for the Git account data
* don't commit binary files into the repository e.g. test data
* add ignores (e.g. ```.gitignore```) to the directory for ignoring files & directories
* don't push any test branches
* use informative commit messages, branch & tag names (release tags use the prefix ```v```)
* check file list before your commit is closed, remove any temporary file e.g. ```.bak```
* don't store any project management files in the repository
* ```master``` is the current developing branch (should be compilable every time)
* ```dev-<description>``` are current developing branches in parallel to the master branch, the dev-branches must be merged until the next release
* ```hotfix-<tag name>``` are hotfix branches to a released tag

### Source Files & Formating

* files are used [UTF-8 encoding](http://en.wikipedia.org/wiki/UTF-8)
* indent / scope is defined with _4 spaces_ ( __not__ with tabulator)
* curly braces ```{}``` are placed in a separate line, except when used for initialization of arrays or structs e.g.

  ```
  void myFunction()
  {
      System.out.println("this is a test function")
  }
  ```
* spaces and new lines shall be used where they make sense, e.g. to structure the code into visible blocks
* use curly braces only if needed e.g.

  ```
  for(int i=0; i < 5; i++)
  {
      b += i;
  }
  ```
  better
  
  ```
  for(int i=0; i < 5; i++)
      b += i;
  ```


### Code Conventions

#### Imports

* start-imports (```import awt.*```) are not allowed - import the class directly
* add new packages with its definition also to the ```pom.xml``` (packages can be found e.g. on [mvnrepository.com](http://mvnrepository.com))
* packages without a public Maven repository may not be used


#### Packages

* packages are always lower-case and all parts of a package are stored in directory, which is named equal to the package
* package are not to be nested more than once. While ```a.b.CTestClass``` is still allowed, ```a.b.test.CTestClass``` is not

#### Classes

* interface & abstract classes uses the prefix ```I```
* implementated classes uses the prefix ```C```
* enum classes uses the prefix ```E```
* class names start with a capital (upper-case letter) and use [camel case](http://en.wikipedia.org/wiki/CamelCase)
* inner classes uses the equal definition
* JavaScript files does not use this convention - only a modul name is used

#### Generic Parameter

* generic parameters are written all upper-case
* use extends / implements calls for the generic types to restrict the tye
* use also the diamond operator ```<>``` / ```<?>``` to initialize types correctly


#### Methods

* use descriptive method and function names in [camel case](http://en.wikipedia.org/wiki/CamelCase) notation
* use correct visibilities (public, private, protected)
* use correct parameter definition (final for pushing data into the method, reference for send data back)
* use final to restrict inhertiance
* avoid empty methods, use abstract methods

  ```
  void run()
  {
  }
  ```
  better
  
  ```
  abstract void run();
  ```
  
#### Variables / Constants / Types

* non-static class members start with ```m_```
* constant values (static final) use the prefix ```c_```
* static class members start with ```s_```
* function / method parameters start with ```p_```
* local variables start with ```l_```, except numeric counting variables ```i```, ```n```, ```l```, ```j```
* variable / constants starts with a lower-case letter and use [camel case](http://en.wikipedia.org/wiki/CamelCase)
* string localization are defined by with ``CCommon.getResourceString( <class / object>, <label>, <parameter...> )``.
Localization files are found under ```src/main/resources/language```, labels are in alphabetic order and grouped. Parameter
will be set with ```{<index number>}```
* JavaScript variables are used also a type prefix (e.g. ```pn_number```):
  * ```x``` variant / function
  * ```a``` array
  * ```o``` (JSON) object
  * ```n``` numeric
  * ```c``` character / string


## Do's and Don'ts

* don't ignore warnings (warnings are usefull, optimize your code)
* don't use absolute file and path names - use always relative names 
* don't reference any files / directories outside the project directory
* don't include any test data in the source code or database scripts
* avoid long methods and class names, also avoid long parameter lists
* use descriptive name e.g. ```int getTimeInMilliseconds()```don't use ```int tim()```
* don't use on boolean values ```if (a == true)``` / ```if (a==false)```, use ```if (a)``` / ```if (!a)``` 
* don't use value allocation in if-clause e.g. ```if (a=0)```
* don't uncommend code block, delete the unused code blocks (the repository stores old versions)
* use correct scopes, don't do

  ```
  int i=0;
  for(; i < 5; i++)
  {
  }
  ```

* use early-return

  ```
  void myFunction( final boolean a )
  {
      if (a)
         return;
  }
  ```
  
* avoid deep indent / scope, don't use

  ```
  void myFunction( final boolean a )
  {
      if (a)
         return;
      else
      {
         // do something
      }
      
      // do something general
  }
  ```
  
  use
  
  ```
  void myFunction( final boolean a )
  {
      if (a)
         return;
         
      // do something
  }
  ```
  
* use switch-case only with break or return and check default value



### documentation

* each method / function / namespace / class must be documented, use block comments ```/* */``` for the documentation
* brief comments are optional
* don't write lyrics, write short and comprehensible documentation e.g. don't write _@return this method returns a integer for calculation something_ write _@return integer value for something calculation_
* all parameters have to be documented via ```@param```
* if the function returns a value there has to be a ```@return``` tag
* use ```@bug```, ```@warning``` and ```@note``` for import and additional information
* documentation should be above the code (not behind the line) e.g.

  ```
  // this is an increment of i
  i++;
  ```
  don't create
  
  ```
  i++; // this is an increment of i
  ```

* documentation must be written during / after developing, you do not write your documentation some times later - your wouldn't do this later
    