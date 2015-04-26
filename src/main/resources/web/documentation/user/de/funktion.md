# Funktionsweise

## System-Start

Der Start des Programms verläuft in verschiedenen _Stages_, die die verschiedenen Ebenen des Systems mit den notwendigen Komponenten starten. Jede Ebene des
Systems ist für sich gesehen eine abschlossene Struktur. Für den Startvorgang ist maßgeblich die Klasse _CBootstrap_ verantwortlich, in der zentral alle
Events verarbeitet werden. Eine Besonderheit ist das Präprozessing beim Start durch die Java Runtime. Der Initialstart erfolgt in der Klasse _CBoot_ über
die die aktuelle Version und der Hersteller der Java Runtime geprüft wird, diese leitet dann den weiteren Startup-Prozess an die Klasse _CMain_ weiter, in
der der eigentliche Start durchgeführt wird. Die nachfolgenden Diagramme visualisiert den groben Startprozess.


### Start der Simulationsumgebung

Die Simulationsumgebung (_CSimulation_ mit _CWorld_) wird immer gestartet. Die Simulation repräsentiert den Kern, d.h. die Berechnungsstruktur für alle
Simualtionsobjekte. Die Welt beinhaltet alle Objekte, die simuliert werden sollen mit ihren dazugehörigen Strukturen. Dieser Simulationskern hat keine
direkte visuelle Repräsentation, so dass die Simulation auch ohne UI gestartet werden kann. 

![Startup](image/startup-diagram.png)

### Start der UI




### Startparameter

Dem Programm können initiale Parameter mit gegeben werden, um den Startprozess zu beeinflussen.



## Weltaufbau

Die Welt ist als _Schichtstruktur_ ([[Mannigfaltigkeit]]) aufgebaut, so dass jede Schicht als _eigenständige Struktur_ ([[Topologischer Raum]]) aufgefasst werden
kann. Jede Schicht der Welt kann Simulationsobjekte enthalten, die bei Ausführung durch den Simulationskern ausgeführt werden. Es werden alle Objekte pro Schicht
verarbeitet, bevor die nächste Schicht bearbeitet wird. Die Schichten besitzen eine festgelegte Ordnung, in welcher Reihenfolge sie abgearbeitet werden. Das nachfolgende
Diagramm zeigt die den grundlegenden Aufbau.

![Weltaufbau](image/welt.png)

Die Welt wird bildlich von _oben nach unten_ berechnet, d.h. von der Schicht mit dem kleinsten Index zu der Schicht mit dem größten. Als Standart-Verhalten werden
zuerst die _Jason Agenten, Fahrzeuge, Quellen_ und abschließend _statische Auswertung mit Transfer in eine Datenbank_ ausgeführt. 


