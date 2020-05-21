# 3D Line Renderer
## Info
Tento projekt je slouží jako ukázka základů 3D renderování (omezeného pouze na render hran). Pro výkonné aplikace by se 
renderování samozřejmě nepsalo v Javě, na CPU a s alokací tolika paměti v každém snímku.    
TODO


## Spuštění
TODO

gradle run --args "STLExamples/Globe.stl"
gradle javadoc 
gradle jar/classes



## Použití
Scénu reprezentuje objekt `World`, do kterého je možné přidávat 3D objekty pro renderování. Pro inicializaci objektu je 
třeba implementovat interface `Renderer`, který pak `World` používá pro render = kreslení čar. Pro samotný render jednoho 
snímku je třeba poslat pozici a orientaci kamery jako objekt `Camera`. Příklad použití je třída `Example`.

Pro vytvoření nového 3D objektu je třeba zdědit a dodefinovat abstraktní objekt `Object3D`. Takový objekt se pak dá poslat
`World` objektu pro přidání do scény. Příklady definice objektů se nachází v podbalíčku `objects.examples`.



## Ovládání "Example"
Pokud dostane na vstupu cestu k STL souboru, tak ho otevře a zobrazí, jinak načte demo scénu. Doporučuji zapnou debug a
přepnout do SORT_OBJECTS módu.

### Klávesnice
* j = zapnout/vypnout limit na vykreslení pouze prvních 2000 hran
* k = zapnout/vypnout zobrazení debug informací
* u = zapnout/vypnout zobrazení crosshair
* p = přepínání pořadí vykreslování DRAW_EDGES/DRAW_OBJECTS (viz. javadoc `World.DrawOrder`)
* i = zapnout/vypnout otáčení kamery okolo středu scény
* l = přepínání mezi normální a vyšší rychlostí pohybu kamery
* WSADEQ = pohyb podle os scény (WS podél Z, AD podél X, EQ podél Y)
* wsadeq = pohyb ve směru kamery (WD dopředu/dozadu, AD doleva/doprava, EQ nahoru/dolů)
* mezerník = vygeneruj nový strom (pouze v demo scéně)

### Myš
Libovolné tlačítko zamkne/odemkne pohyb myši. Když je myš zamknutá, tak ovládá kameru a to tak, že schová kurzor a 
podle změny pozice myši mezi dvěma snímky pohne kamerou a pak myš přesune zpět do předchozí pozice. 