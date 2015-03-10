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
