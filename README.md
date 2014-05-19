# Fortgeschrittenenprojekt - Traffic Simulation

## Requirements

* [Java 1.6](http://www.java.com)
* [Maven](http://maven.apache.org/)
* [Doxygen](http://www.doxygen.org/)

### getestete IDEs

* [IntelliJ Community Edition](http://www.jetbrains.com/idea/)
* [Eclipse](http://www.eclipse.org/)

## Installation / Konfiguration

Mittels Maven kann ein Jar inkl. Abhängigkeiten erzeugt werden, das in dem Pfad```target/Fortgeschrittenenprojekt-1.0-jar-with-dependencies.jar``` zu finden ist. Das Jar
kann direkt ausgeführt werden. Da der Graph, in dem sich die Fahrzeuge bewegen mehr RAM benötigt, muss das Programm mit dem Parameter ```-Xmx``` und einer RAM Größe gestartet werden (die Angabe ```-Xmx2g``` liefert gute Ergebnisse).

### Datenspeicherung

Das Programm legt im Userverzeichnis ein Unterverzeichnis ```.tucwinf``` an, in dem die Konfiguration und die Graphen gespeichert werden. Das die Konfiguration des Programms ist in der [Json-Datei](http://de.wikipedia.org/wiki/JSON) ```config.json``` gespeichert. Die Datei hat folgenden Aufbau

```
{
   "ViewPoint" : 
   {
       "latitude":51.965740552826,
       "longitude":10.249385833740234
   },
   "Zoom" : 7,
   "MaxThreadNumber" : 5,
   "WindowWidth" : 2310,
   "WindowHeight" : 1414,
   "CellSampling" : 2,
   "ThreadSleepTime" : 100,
   "RoutingMap" : "europe/germany/niedersachsen",
   "RoutingAlgorithm" : "astarbi"
}
```

* der _ViewPoint_ definiert die Position auf der Weltkarte, die beim Starten angezeigt wird. _Zoom_ die dazu gehörige Zoomstufe
* _MaxThreadNumber_ setzt die Anzahl der Threads, wobei diese aber durch das System automatisch bestimmt werden
* _WindowWidth_ und _WindowHeight_ geben die Breite und Höhe des Fensters beim Start an
* _CellSampling_ setzt die Größe (in Meter) einer Zelle
* _ThreadSleepTime_ ist die Zeit (in Millisekunden), die die Threads während der Berechnung pausieren
* _RountingMap_ setzt die Karte, mit der gearbeitet werden soll (siehe "Datenimport")
* _RoutingAlgorithm_ legt den Routing-Algorithmus, der verwendet werden soll (siehe interne Dokumentation)

### Datenimport

Das System bezieht die Daten für den Graph online von dem Dienst [Geofabrik](http://download.geofabrik.de/), das [OpenStreetMap](http://www.openstreetmap.de/) Daten bereit stellt. Die OSM Daten müssen zum Start in eine Graphen konvertiert werden, was einige Zeit beanspruchen kann (zusätzlich ist der benötigte RAM Bedarf zu beachten). Als Default-Import wird _Niedersachen_ verwendet, möchte man die Karte verändern, muss nur der Eintrag _RoutingMap_ geändert werden. Auf der Webseite von Geofabrik kann man innerhalb der Downloads die passenden Karten finden und erhält zu den einzelnen Karten folgenden Link ```http://download.geofabrik.de/north-america/us/california.html```. Für den Eintrag _RoutingMap_ muss der Eintrag zur Domain und die Dateiendung nur entfernt werden, somit ergibt sich ```north-america/us/california``` als Eintrag für die Konfiguration.

## Benutzung

Nach dem Start des Programms erscheint die Karte. Man kann die Karte mittels Maus und / oder Cursortasten bewegen. Durch Klicken können blaue Quellen, an denen Fahrzeuge erzeugt werden, in die Karte eingefügt werden (ebenso kann eine Quelle wieder entfernt werden). Über das Menü kann die Simulation gestartet, gestoppt oder resettet werden. Bei einem Reset werden alle Fahrzeuge entfernt und nur die Quellen bleiben bestehen.

### Ansicht / Tips

* werden Quellen auf Autobahnen gesetzt, zeigt sich sehr schnell ein Verkehrsstau
* die Fahrzeuge besitzen unterschiedliche Farben mit folgender Bedeutung:
  * schwarz / dunkel-grau: das Fahrzeug fährt weniger als 50km/h
  * grün: das Fahrzeug fährt zwischen 50 und 100 km/h
  * rot: das Fahrzeug fährt mehr als 100 km/h
* an einigen Stellen fahren die Fahrzeuge nicht exakt die gezeigte Straße, dies ist dadurch bedingt, dass die unterliegenden GPS Koordinaten nicht exakt interpoliert werden können  