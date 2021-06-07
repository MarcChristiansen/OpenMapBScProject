# Indledende
Dette projekt bruger Gradle 6.8.2 og Java 15.0.2

# Gradle task eksempler
Et simpelt eksempel på en graf kan indlæses fra eksmepel filerne. I dette tilfælde en optimeret version af Danmark
```bash
gradle tasks mapguiDK --console=plain
```
Alternativt kan egne grafer laves ud fra enten (.osm) eller (.osm.pbf) filer hentet fra fx: http://download.geofabrik.de/europe.html. 

De to nedenstående tasks kan hjælpe med dette. Disse tager begge input i consollen og spørg om de ting der er relevante.

Compile tasken kan compilere en fil til en JSON fil.

Mapgui tasken kan åbne en JSON fil. Mapgui kræver en JSON fil, men (.json) er teknisk set ikke nødvendigt på filen der bruges.

```bash
gradle tasks compile --console=plain
gradle tasks mapgui --console=plain
```
# Knapper
M1 (Venstre museklik): Panorering på kortet
ScrollWheel: Styrer zoom
Sideknapperne: Styrer punkterne for pathfinderen (M4 og M5)
