# Welt

## Aufbau

Die Welt ist als _Schichtstruktur_ ([[Mannigfaltigkeit]]) aufgebaut, so dass jede Schicht als _eigenständige Struktur_ ([[Topologischer Raum]]) aufgefasst werden
kann. Jede Schicht der Welt kann Simulationsobjekte enthalten, die bei Ausführung durch den Simulationskern ausgeführt werden. Es werden alle Objekte pro Schicht
verarbeitet, bevor die nächste Schicht bearbeitet wird. Die Schichten besitzen eine festgelegte Ordnung, in welcher Reihenfolge sie abgearbeitet werden. Das
nachfolgende Diagramm zeigt die den grundlegenden Aufbau:

![Weltaufbau](image/welt.png)

Die Welt wird bildlich von _oben nach unten_ berechnet, d.h. von der Schicht mit dem kleinsten Index zu der Schicht mit dem größten. Als Standart-Verhalten
werden zuerst die _Jason Agenten, Fahrzeuge, Wegpunkte / Quellen, Inkonsistenzen_ und abschließend _statistische Auswertung mit Transfer in eine Datenbank_
ausgeführt.


## Verkehrsmodell

Die Simulation unterstützt aktuell das [[Nagel-Schreckenberg-Modell]] um Verkehr zu simulieren in zwei Formen:

* ein erweitertes Modell, das zusätzlich zu dem Basisverhalten Beschleunigungs- und Bremvorgänge individuell pro Fahrzeug betrachtet
* ein modifiertes erweitertes Modell, das speziell für Fahrzeuge, die durch Agenten gesteuert werden, die Kontrolle für die Eigenschaften des Fahrzeuges
überlässt (dieses Modell ist als Default-Einstellung aktiviert)


### Diskretisierung des Verkehrsnetzes




### Zeitdiskretisierung

 
 
 
 
## Wahrnehmung für [Jason Agenten](jason.md)
 