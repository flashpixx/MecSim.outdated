# Programmfunktionsweise

## System-Start

Der Start des Programms verläuft in verschiedenen _Stages_, die die verschiedenen Ebenen des Systems mit den notwendigen Komponenten starten. Jede Ebene des
Systems ist für sich gesehen eine abschlossene Struktur. Für den Startvorgang ist maßgeblich die Klasse _CBootstrap_ verantwortlich, in der zentral alle
Events verarbeitet werden. Eine Besonderheit ist das Präprozessing beim Start durch die Java Runtime. Der Initialstart erfolgt in der Klasse _CBoot_ über
die die aktuelle Version und der Hersteller der Java Runtime geprüft wird, diese leitet dann den weiteren Startup-Prozess an die Klasse _CMain_ weiter, in
der der eigentliche Start durchgeführt wird. Die nachfolgenden Diagramme visualisiert den groben Startprozess.

![Startup](image/startup-diagramm.png)


### Start der Simulationsumgebung

Die Simulationsumgebung ( _CSimulation_ mit _CWorld_) wird immer gestartet. Die Simulation repräsentiert den Kern, d.h. die Berechnungsstruktur für alle
Simualtionsobjekte. Die Welt beinhaltet alle Objekte, die simuliert werden sollen mit ihren dazugehörigen Strukturen. Dieser Simulationskern hat keine
direkte visuelle Repräsentation, so dass die Simulation auch ohne UI gestartet werden kann. 


### Start der UI

Die UI ist optional und wir somit in Abhängigkeit der Programmübergabe-Parameter geladen. Die UI besteht einmal aus den Tabs innerhalb der Java Anwendung und
der HTMl darstellung. Die Tabs ( _Main_ und _OSM_ ) sind die internen Java-Komponenten die direkt durch den Simulationskern angesprochen werden können. Die
Darstellung innerhalb des Tabs _Main_ stellt für den Benutzer die Eingabemasken mittels HTML dar. Die Default-Einstellungen für das Binden des Webservers
ist die URL ```http://localhost:9876``` vorgesehen. Über die Programmparameter kann diese Adresse beim Start überschrieben werden.


## Konfiguration / Speicherort

### Programmparameter

Dem Programm können initiale Parameter mit gegeben werden, um den Startprozess zu beeinflussen. Die aktuellen möglichen Parameter können mittels ```--help```
inkl. einer Beschreibung eingesehen werden.


### Speicherort

Der Defaultspeicherort der Daten liegt im Homeverzeichnis der Users im Unterverzeichnis ```.mecsim```. Innerhalb dieser Verzeichnis findet sich folgende Struktur:

* die Datei ```config.json``` beinhaltet alle Einstellungen
* das Verzeichnis ```graphs``` enthält die Graphdaten der Simulation ([GraphHopper](https://graphhopper.com/))
* innerhalb des Verzeichnis ```jar``` können beliebige [[Java Archive]] abgelegt werden, die beim Start geladen werden, hierzu zählen z.B. Datenbanktreiber
* das Unterverzeichnis ```mas``` beinhaltet alle Scriptdateien der Agenten
* als [[Cache]] der visuelle Kacheldarstellung wird das Verzeichnis ```tile.openstreetmap.org``` verwendet
* um eigene HTML Inhalte abzulegen kann das Verzeichnis ```www``` benutzt werden, wobei die Dokument-Root-Datei ```index.htm``` lauten muss





