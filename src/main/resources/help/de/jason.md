# Jason (ASL Syntax)

[[Software-Agenten]] können mit Hilfe der Sprache [Jason](http://jason.sourceforge.net/) entwickelt werden, was
eine Instanz der Sprache [Agentspeak](http://en.wikipedia.org/wiki/AgentSpeak) ist. Dazu sind einige Einschränkungen /
Erweiterungen zu beachten.

## Deaktivierte Befehle

Einige Befehle des ASL Syntax sind deaktiviert, da sie in den Simulationsablauf eingreifen würden. Die Befehle können
innerhalb des Agenten verwendet werden, besitzen aber keine Ausführung:

 *  ```clone```
 * ```wait```
 * ```create_agent```
 * ```kill_agent```
 * ```stopMAS```

## Hinzugefügte Befehle

Da Agenten mit einem Java Objekt verknüpft werden können, existieren folgende Befehle, bei denen die Sichtbarkeit
nicht beachtet werden muss:

 * ```invoke( <Objektname>, <Methodenname> [, <Returntyp> | <Argumenttyp> | <Argumente> ] )``` führt eine
  Objekt Methode aus, der Objektname (case-sensitiv) wird durch die Programmierung festgelegt, der Methodentyp
  entspricht dem Namen der Methode (case-sensitiv). Nachfolgenden müssen Returntyp (Default ist _void_) und in
  einer Termliste die Argumenttypen (Default ist _void_) übergeben werden, gefolgt von den Übergabe Parametern.
  Da in Java durch die Polymorphie Überladungen von Methoden möglich sind, muss eben die Signatur der Methode
  spezifiziert werden, damit sie korrekt ausgeführt werden kann

  * ```set( <Objektname>, <Eigenschaftname>, <Wert> )``` setzt den Wert einer Eigenschaft eines Objektes. Der erste
  Parameter ist der Name des Objektes, der zweite der Eigenschaft und der dritte Wert ist der Wert, wobei
  Typenkonvertierung automatisch durchgeführt wird


## Code-Beispiel

Der Code eines Agenten kann über den Editor eingegeben werden:

```
!init.
```
