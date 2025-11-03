1B - Inzenjeri i ekspertize

Kratko objasnjenje programa:
Program modeluje hijerarhiju inzenjera, zajednicke i specificne osobine i ponasanja kroz klase, nasljedjivanje i polimorfizam uz validaciju svih polja. Za rad sa podacima tj. razlicitim tipovima inzenjera implementirane su funkcionalnosti grupisanja, filtriranja i agregacije. 

Struktura klasa i odnosi:
interface Osoba 
- osnovno ponasanje svih osoba u sistemu, minimalan identitet osobe
- definise metode: identitet() i getTitula()
- omogućava da se bilo koji tip osobe (ne samo Inzenjer) moze koristiti na isti nacin

open class Inzenjer : Osoba
- bazna(super) klasa koja implementira Osoba interfejs i sadrzi zajednicka svojstva i validacije za sve inzenjere
- zajednicki, osnovni podaci tj. polja su ime, prezime, titula, godine iskustva i skup ekspertiza, a metod osnovneInfo() formira tekstualni prikaz osnovnih podataka (ima oznaku open kako bi ga izvedene klase mogle override-ati)
 
class SoftverskiInzenjer : Inzenjer, class InzenjerElektrotehnike : inzenjer
- izvedene klase koje nasljedjuju od klase Inzenjer, dodaju specificna polja, validacije i metode i predefinisu zajednicki metod zbog svojih specificnih karakteristika
- dodatni atribut za Softverskog inzenjera je broj projekata na kojim je radio, a dodatni
metod rangUspjesnosti() ocjenjuje uspješnost na osnovu iskustva i projekata
- InzenjerElektrotehnike dodatno ima polje za broj certifikata koje posjeduje i metod kvalifikovanZaVisokSlozenZadatak() kojim se procjenjuje spremnost za slozen problem

-> Osoba je apstrakcija-> Inzenjer implementira Osoba -> Svaki SoftverskiInzenjer i svaki InzenjerElektrotehnike je i Inzenjer

Validacija i sigurnost podataka:
Vrijednosti atributa validiraju se u sklopu primarnog konstruktora tj. unutar init bloka pomocu funkcije require().
Funkcija uzima boolean izraz (predstavlja neko pravilo/uslov) koji mora biti zadovoljen kako bi se objekat uspjesno kreirao. Opcionalno se moze navesti i drugi argument koji predstavlja poruku koja ce biti uključena u IllegalArgumentException ako uvjet nije evaluiran kao tacan.
Primjer ispisa u konzoli nakon neuspjele validacije:
Exception in thread "main" java.lang.IllegalArgumentException: Ime ne smije biti prazno!
Exception in thread "main" java.lang.IllegalArgumentException: Broj godina iskustva mora biti veci (ili jednak) 0!

Funkcionalne operacije i njihova upotreba u kodu:
Fold funkcija koristena je pri implementaciji funkcionalnosti za grupisanje inzenjera po ekspertizama. Fold prolazi kroz sve elemente kolekcije i koristeci lambda funkciju akumulira novu vrijednost - akumulator, sve dok ne dodje do kraja kada ce on predstavljati rezultat. 
Pocetna vrijednost akumulatora se obavezno prosljedjuje kao argument.
Konkretno u funkciji grupisiPoEkspertizi() akumulator je tipa MutableMap<String, MutableList<Inzenjer>>, a njegova inicijalna vrijednost je prazna mapa. Tokom folda, prolazeci kroz sve inzenjere i sve njihove ekspertize, mapa se puni kljucevima-ekspertizama i vrijednostima-lista inženjera sa tom ekspertizom.Na kraju, fold vraća konačnu akumuliranu mapu:ekspertiza → lista inženjera sa tim iskustvom.

Reduce funkcija koristena je unutar funkcionalnosti za odredjivanje najiskusnijeg inzenjera medju inzenjerima istog tipa. Reduce akumulira sve elemente kolekcije u jednu vrijednost, tako što primjenjuje lambda funkciju s lijeva na desno, počevši od prvog elementa kao početnog akumulatora. U mojoj funkciji najiskusnijiPoTipu(), reduce se primjenjuje nad listom inzenjera tako sto poredi godine iskustva i na kraju akumulira za rezultat najiskusnijeg inzenjera. 

Aggregate funkcija koristena je za izracunavanje ukupnog broja projekata svih softverskih inzenjera i certifikata svih elektrotehnickih inzenjera. Aggregate radi na Grouping<K, T> objektu, prolazi kroz sve elemente svake grupe i akumulira vrijednost za svaku grupu odrzavajući akumulator koji se azurira po elementu. Moja funkcija ukupnoProjekataICertifikata() grupise inzenjere po tituli sa groupingBy{}, a zatim aggregate prolazi kroz svaku grupu -> odredjuje vrijednost za akumuliranje na osnovu tipa inzenjera -> ako je element prvi u grupi, akumulator se postavlja na trentnu vrijednost za akumuliranje, za sve ostale elemente, akumulator se ažurira zbrajanjem. Rezultat je mapa gdje je ključ titula, a vrijednost je zbir projekata ili certifikata. 

Poređenje fold, reduce, i aggregate:
fold -> akumulira vrijednost pocevsi od eksplicitno zadate pocetne vrijednosti koja moze biti bilo kojeg tipa i vraca rezultat tog tipa
prednosti: eksplicitna vrijednost init - sigurnije za prazne kolekcije, fleksibilan povratni tip, moze se koristiti za popunjavanje mape, liste, kombinovanje elemenata...
mane: rucno zadavanje init vrijednosti, sporo

reduce -> kombinuje elemente kolekcije bez eksplicitne pocetne vrijednosti, pocinje od prvog elementa s lijeva, a povratna vrijednost je istog tipa kao elementi kolekcije prednosti: jednostavno za pronalazenje maksimuma, minimuma ili slicnih agregacija unutar jedne liste
mane: ne radi na praznoj kolekciji - baca exception, povratni tip nije fleksibilan

aggregate -> koristi se nad grupisanim kolekcijama, akumulira vrijednost po grupi, a pocetnu vrijednost implicitno odredjuje lambda funkcija, vraća mapu kljuceva i agregiranih vrijednosti
prednosti: tipicna primjena za zbir, prosjek ili druge statisticke agregacije po grupama, fleksibilan povratni tip
mane: slozenija funkcija, radi samo na grupisanim kolekcijama

Pokretanje programa:
Folder 1B predtavlja projekat koji je potrebno 
otvoriti u IntelliJ IDEA ili drugom Kotlin IDE-u.
1b/src/Main.kt je fajl koji je potrebno pokrenuti.
Rezultati će biti prikazani u konzoli.

Konzolni ispis:
```
--- Popis svih inženjera ---
- Softverski inzenjer -
Ime i prezime:Amina Ibrahimovic
Profesionalna titula: Softverski inzenjer
Iskustvo:8 godina
Ekspertize: Kotlin, Android, CI/CD
Broj projekata: 15
Rang uspješnosti na osnovu godina iskustva i odradjenih projekata: Visoka
-----------------------
Ime i prezime:Marko Petrovic
Profesionalna titula: Softverski inzenjer
Iskustvo:3 godina
Ekspertize: Java, Spring, SQL
Broj projekata: 5
Rang uspješnosti na osnovu godina iskustva i odradjenih projekata: Niska
-----------------------
Ime i prezime:Jelena Pejic
Profesionalna titula: Softverski inzenjer
Iskustvo:12 godina
Ekspertize: Kotlin, Backend, Microservices
Broj projekata: 25
Rang uspješnosti na osnovu godina iskustva i odradjenih projekata: Visoka
-----------------------

- Inzenjer elektrotehnike -
Ime i prezime:Ivan Novak
Profesionalna titula: Inzenjer elektrotehnike
Iskustvo:7 godina
Ekspertize: Embedded, C, PCB
Broj certifikata: 4
Kvalifikovan za visokosloženi zadatak: Ne
-----------------------
Ime i prezime:Sara Kovacevic
Profesionalna titula: Inzenjer elektrotehnike
Iskustvo:10 godina
Ekspertize: PowerSystems, Matlab, PCB
Broj certifikata: 6
Kvalifikovan za visokosloženi zadatak: Da
-----------------------
Ime i prezime:Azur Mesanovic
Profesionalna titula: Inzenjer elektrotehnike
Iskustvo:2 godina
Ekspertize: SignalProcessing, Python
Broj certifikata: 1
Kvalifikovan za visokosloženi zadatak: Ne
-----------------------

--- Inzenjeri sa vise od 5 godina iskustva grupisani po ekspertizama ---
Kotlin: Amina Ibrahimovic;Jelena Pejic; 
Android: Amina Ibrahimovic; 
CI/CD: Amina Ibrahimovic; 
Backend: Jelena Pejic; 
Microservices: Jelena Pejic; 
Embedded: Ivan Novak; 
C: Ivan Novak; 
PCB: Ivan Novak;Sara Kovacevic; 
PowerSystems: Sara Kovacevic; 
Matlab: Sara Kovacevic; 

--- Najiskusniji po tipu inzenjera ---
Softverski inzenjer: Jelena Pejic - 12 godina iskustva
Inzenjer elektrotehnike: Sara Kovacevic - 10 godina iskustva

--- Ukupno projekata i certifikata --- 
56

--- Provjere ispravnosti ---
Najiskusniji Softverski inzenjer: očekivano=Jelena Pejic, dobijeno=Jelena Pejic -> PASS
Najiskusniji Inzenjer elektrotehnike: očekivano=Sara Kovacevic, dobijeno=Sara Kovacevic -> PASS
Ukupno projekata i certifikata: ocekivano = 56, dobijeno = 56 -> PASS
Svi u fold grupi imaju >5 godina iskustva: true

Rezime provjera: SVE PROLAZE (PASS)
```

AI alati koristeni za:
pojašnjenje Kotlin koncepata (fold, reduce, aggregate), 
pojasnjenje kako izvrsiti validaciju podataka (funkcija require),
formatiranje ispisa i generisanje testnih vrijednosti.
