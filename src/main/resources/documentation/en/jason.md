# Jason (ASL Syntax)

[[Software-Agents]] can be developed with the help of the language [Jason](http://jason.sourceforge.net/). Jason is an instance of the language [Agentspeak](http://en.wikipedia.org/wiki/AgentSpeak). There are some constraints and extensions which have to be considered.

## Disabled Commands

Some commands of ASL syntax are disabled, since they would interfere the simulation. The commands can be used inside the agent, but they do not have an execution:

 * ```clone```
 * ```wait```
 * ```create_agent```
 * ```kill_agent```
 * ```stopMAS```

## Added Commands

Since Agents can be linked to a Java object, following commands exists for which the visibility does not need to be considered:

 * ```invoke( <Objectname>, <Methodname> [, <Returntype> | <Argumenttype> | <Arguments> ] )``` executes an object-method, the objectname (case-sensitive) is declared by coding, the method-type matches the name of the method (case-sensitive). Subsequently, returntype (default is _void_) and argumenttypes (default is _void_), which have to reside in a termlist, have to be passed followed by transfer parameters. 
 Since polymorphism in Java enables overloading methods, the signature of the method has to be specified in order to be executed correctly.

 * ```set( <Objectname>, <Propertyname>, <Value> )``` sets the value of an objects property. The first parameter is the name of the object, the second parameter is the characteristic, and the third parameter is the value whereby typeconversion is executed automatically.

## Code-Example

The code of an agent can be entered in the editor:

```
!init.
```
