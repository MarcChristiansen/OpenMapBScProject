----------------------Indledende--------------------------------
Dette projekt bruger Gradle 6.8.2 og Java 15.0.2

----------------------Gradle task eksempler--------------------------------
Hvis et ekesempel på en graf indlæst i GUI er følgende eksempel en mulighed

gradle tasks mapguiDK --console=plain

Alternativt kan egne grafer laves ud fra enten (.osm) eller (.osm.pbf) filer hentet fra fx "http://download.geofabrik.de/europe.html". 
De to nedenstående tasks kan hjælpe med dette. Disse tager begge input i consollen og spørg om de ting der er relevante.
Mapgui kræver en JSON fil, men (.json) er teknisk set ikke nødvendigt på filen.

gradle tasks compile --console=plain
gradle tasks mapgui --console=plain
