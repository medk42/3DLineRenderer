# 3D Line Renderer
## Info
Tento projekt je ukázka základů 3D renderování omezeného pouze na render hran. 


## Spuštění
Pro spuštění programu stačí zavolat `gradle run`. Programu lze předávat argumenty pomocí gradle parametru 
`--args "<params>"`. Program se dá zavolat následovně:
* `gradle run`: volání bez parametru spustí příklad scény
* `gradle run --args "path-to-stl-file.stl"`: volání s cestou k STL souboru zobrazí wireframe objektu obsaženého v STL
* `gradle run --args "compatibility"`: program spustí příklad scény v módu kompatibility (je třeba na Linuxu)
* `gradle run --args "compatibility path-to-stl-file.stl"`: program zobrazí wireframe STL objektu v módu kompatibility 
(je třeba na Linuxu)

Mód kompatibility má značně horší výkon, kvůli použití pomalejšího rendereru v příkladové třídě `Example` a nepodporuje
otáčení kamery pomocí myši.

Pro vyzkoušení otevírání STL souborů lze použít soubory ve složce STLExamples, tedy např. volání 
`gradle run --args "STLExamples/Globe.stl"`.

Pro vygenerování dokumentace lze zavolat `gradle javadoc`.

Pro kompilaci lze zavolat požadovanou gradle task, např. `gradle jar` nebo `gradle classes`. Při ruční kompilaci je 
ale potřeba dodat potřebné knihovny pro spuštění, proto doporučuji spuštět s pomocí gradle.


## Použití
Scénu reprezentuje objekt `World`, do kterého je možné přidávat 3D objekty pro renderování. Pro inicializaci objektu je 
třeba implementovat interface `Renderer`, který pak `World` používá pro render = kreslení čar. Pro samotný render jednoho 
snímku je třeba poslat pozici a orientaci kamery jako objekt `Camera`. Příklad použití je třída `Example`.

Pro vytvoření nového 3D objektu je třeba zdědit a dodefinovat abstraktní objekt `Object3D`. Takový objekt se pak dá poslat
`World` objektu pro přidání do scény. Příklady definice objektů se nachází v podbalíčku `objects.examples`.



## Ovládání "Example"
Pokud dostane na vstupu cestu k STL souboru, tak ho otevře a zobrazí, jinak načte demo scénu. Obsahuje `compatibility`
mód, vzhledem k tomu, že výkonnější renderer nefunguje dobře na Linuxu. Viz. "Spuštění" pro více info. 

Doporučuji zapnou debug a přepnout do SORT_OBJECTS módu. Okno jde maximalizovat a měnit jeho velikost.

### Klávesnice
* j = zapnout/vypnout limit na vykreslení pouze prvních 2000 hran
* k = zapnout/vypnout zobrazení debug informací
* u = zapnout/vypnout zobrazení crosshair
* p = přepínání pořadí vykreslování DRAW_EDGES/DRAW_OBJECTS (viz. javadoc `World.DrawOrder`)
* i = zapnout/vypnout otáčení kamery okolo středu scény
* l = přepínání mezi normální a vyšší rychlostí pohybu kamery
* WSADEQ = pohyb podle os scény (WS podél Z, AD podél X, EQ podél Y)
* wsadeq = pohyb ve směru kamery (WD dopředu/dozadu, AD doleva/doprava, EQ nahoru/dolů)
* fght/FGHT = otáčení kamery pomocí klávesnice
* mezerník = vygeneruj nový strom (pouze v demo scéně)

### Myš
Libovolné tlačítko myši zamkne/odemkne její pohyb. Když je myš zamknutá, tak ovládá kameru a to tak, že schová kurzor a 
podle změny pozice myši mezi dvěma snímky pohne kamerou a pak myš přesune zpět do předchozí pozice. Myš je také odemčena
pokud okno aplikace ztratí focus.

Rotace kamery pomocí myši funguje pouze mimo `compatibility` mód - v `compativility` módu je třeba používat klávesnici
pro rotaci kamery.