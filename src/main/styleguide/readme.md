# Styleguide

The styleguide of the sources are checked with the [Maven Checkstyle Plugin](http://checkstyle.sourceforge.net).

## Working Branches / Tags

 * ```master``` is the current developing branch (should be compilable every time)
 * ```dev-<description>``` are current developing branches in parallel to the master branch, the dev-branches must be
 merged until the next release
 * ```hotfix-<tag name>``` are hotfix branches to a released tag


## Unit-Tests

* [jUnit](http://junit.org/) test are stored within ```src/test/java```
* all test-classes are stored in the same package structure like test testing-class
* test-classes uses the naming-convention ```Test-<classname>```

## Code Style Settings

In order to set the MecSim code style in Intellij Idea, follow the instructions as listed below:

* remove or rename the current 'codeStyleSettings.xml' in your mecsim/.idea folder
* set a symlink of custom MecSim code style with 'ln -s <path_to_mecsim_root>/src/main/styleguide/intellij/codeStyleSettings.xml> <path_to_mecsim_root>/.idea'
* selecting 'Code -> Reformat Code' in your Intellij IDEA menu reformats your code according to the new settings
