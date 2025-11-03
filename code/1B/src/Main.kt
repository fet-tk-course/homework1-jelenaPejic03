interface Osoba {
    fun identitet(): String
    fun getTitula(): String
}

//bazna klasa
open class Inzenjer (
    private val ime: String,
    private val prezime: String,
    private val titula: String,
    val iskustvo: Int,
    val ekspertize: Set<String>
) : Osoba {
    //init blok se izvrsava pri kreiranju instance klase i validira podatke
    //*koristen AI alat za pojasnjenje nacina validacija  podataka tj. upotrebe odgovarajuce funkcije
    init {
        require(ime.isNotBlank()) { "Ime ne smije biti prazno!" }
        require(prezime.isNotBlank()) { "Prezime ne smije biti prazno!" }
        require(iskustvo >= 0) { "Broj godina iskustva mora biti veci (ili jednak) 0!" }
        require(ekspertize.isNotEmpty()) { "Skup ekspertiza ne smije biti prazan!" }
    }
    //implementacija interfejsa
    override fun identitet(): String {
        return this.ime +" " + this.prezime
    }
    override fun getTitula(): String {
        return this.titula
    }
    //open metoda klase Inzenjer vraca info o inzenjeru, izvedene klase je mogu override-ati
    open fun osnovneInfo(): String {
        return "Ime i prezime:${identitet()}\nProfesionalna titula: ${getTitula()}\nIskustvo:${iskustvo} godina\nEkspertize: ${ekspertize.joinToString(", ")}"
    }
}

class SoftverskiInzenjer(
    ime: String,
    prezime: String,
    iskustvo: Int,
    ekspertize: Set<String>,
    val projekti: Int
) : Inzenjer (ime, prezime, "Softverski inzenjer", iskustvo, ekspertize) {
    init { //dodatna validacija za broj projekata
        require(projekti >= 0) {"Broj odradjenih projekata mora biti veci (ili jednak) 0!"}
    }

    //dodatna metoda za ovaj tip inzenjera ->izracun uspjesnosti na osnovu godina iskustva i odradjenih projekata
    fun rangUspjesnosti(): String {
        val score = projekti * (1.0 + iskustvo / 10.0)
        return when {
            score < 10 -> "Niska"
            score < 20 -> "Srednja"
            else -> "Visoka"
        }
    }
    //override metode iz roditeljske klase jer je neophodno dodati specificne info
    override fun osnovneInfo(): String {
        return super.osnovneInfo() +
                "\nBroj projekata: $projekti" +
                "\nRang uspješnosti na osnovu godina iskustva i odradjenih projekata: ${rangUspjesnosti()}"
    }
}

class InzenjerElektrotehnike(
    ime: String,
    prezime: String,
    iskustvo: Int,
    ekspertize: Set<String>,
    val certifikati: Int
) : Inzenjer (ime, prezime, "Inzenjer elektrotehnike", iskustvo, ekspertize){
    init {//dodatna validacija za broj certifikara
        require(certifikati >= 0) {"Broj certifikata mora biti veci (ili jednak) 0!"}
    }

    //dodatna metoda za ovaj tip inzenjera -> procjena kvalificiranosti za slozeniji zadatak
    fun kvalifikovanZaVisokSlozenZadatak(): Boolean {
        return certifikati >= 5 && iskustvo >= 5
    }
    //override metode iz roditeljske klase jer je neophodno dodati specificne info
    override fun osnovneInfo(): String {
        val kvalifikovan = if (kvalifikovanZaVisokSlozenZadatak()) "Da" else "Ne"
        return super.osnovneInfo() +
                "\nBroj certifikata: $certifikati" +
                "\nKvalifikovan za visokosloženi zadatak: $kvalifikovan"
    }
}

//ispis svih inzenjera uz jasno naglasen tip
fun ispisiInzenjere(inzenjeri: List<Inzenjer>) {
    print("--- Popis svih inženjera ---")
    val grupePoTituli: Map<String, List<Inzenjer>> = inzenjeri.groupBy { it.getTitula() } //mapiranje svih inzenjera prema tipu kako bi se mogli skladno ispisati
    for ((titula, listaInzenjera) in grupePoTituli) {
        print("\n- $titula -")

        for (inzenjer in listaInzenjera) {
            print("\n${inzenjer.osnovneInfo()}")
            print("\n-----------------------")
        }
        println("")
    }
}

//*koristen AI alat za pojasnjenje i bolje razumijevanje koncepata fold, reduce i aggregate
//grupisanje inzenjera sa vise od 5god. iskustva po ekspertizama
fun grupisiPoEkspertizi(inzenjeri: List<Inzenjer>): Map<String, List<Inzenjer>> {
    val iskusni = inzenjeri.filter { it.iskustvo > 5 }

    //fold prolazi kroz listu iskusnih inzenjera i akumulira rezultat koristeci pocetnu vrijednost i lambda funkciju
    val rezultat = iskusni.fold(mutableMapOf<String, MutableList<Inzenjer>>()) { mapa, inzenjer ->
        for (ekspertiza in inzenjer.ekspertize) {
            val lista = mapa.getOrPut(ekspertiza) { mutableListOf<Inzenjer>() }  // getOrPut vraca postojecu listu za ekspertizu ili kreira novu praznu
            lista.add(inzenjer)
        }
        mapa
    }
    return rezultat.mapValues { it.value.toList() } //value ne treba biti mutable radi sigurnosti
}

//izdvajanje najiskusnijeg od svih inzenjera istog tipa
fun najiskusnijiPoTipu(inzenjeri: List<Inzenjer>): Map<String, Inzenjer> {
    //mapiranje inzenjera po tituli
    val grupePoTituli: Map<String, List<Inzenjer>> = inzenjeri.groupBy { it.getTitula() }

    val najiskusniji: Map<String, Inzenjer> = grupePoTituli.mapValues { (_, lista) ->
        lista.reduce { a, b -> if (a.iskustvo >= b.iskustvo) a else b } //funkcija reduce "racuna" najiskusnijeg koristeci lambda funkciju
    }
    return najiskusniji
}

//ukupan zbir rezultata iz razlicitih kategorija tj. zbir projekata i certifikata inzenjera razlicitog tipa -> agregacija po tituli
fun ukupnoProjekataICertifikata(inzenjeri: List<Inzenjer>): Int {
    val mapa = inzenjeri
        .groupingBy { it.getTitula() }
        .aggregate { _, acc: Int?, element, first -> //aggregate prolazi kroz sve elemente za svaki kljuc i akumulira novu vrijednost
            val curr = when (element) {
                is SoftverskiInzenjer -> element.projekti
                is InzenjerElektrotehnike -> element.certifikati
                else -> 0
            }
            if (first) curr // first = true za prvi element grupe — tada je acc null(zato je tip Int?), pa ga inicijalizujemo sa curr
            else acc!! + curr // acc!! koristimo jer sigurno nije vise null
        }
    val rez: Int = mapa.values.sumOf{ it }
    return rez
}

//glavni tok
fun main() {

    //lista instanci inzenjera oba tipa
    //*koristen AI alat za generisanje podataka
    val inzenjeri: List<Inzenjer> = listOf(
        SoftverskiInzenjer("Amina", "Ibrahimovic",  8, setOf("Kotlin", "Android", "CI/CD"), 15),
        SoftverskiInzenjer("Marko", "Petrovic", 3, setOf("Java", "Spring", "SQL"), 5),
        SoftverskiInzenjer("Jelena", "Pejic", 12, setOf("Kotlin", "Backend", "Microservices"), 25),
        InzenjerElektrotehnike("Ivan", "Novak", 7, setOf("Embedded", "C", "PCB"), 4),
        InzenjerElektrotehnike("Sara", "Kovacevic",  10, setOf("PowerSystems", "Matlab", "PCB"), 6),
        InzenjerElektrotehnike("Azur", "Mesanovic",  2, setOf("SignalProcessing", "Python"), 1)
    )

//    nepravilno instanciranje objekta rezultira IllegalArgumentException-om uz odgovarajucu poruku
//        val  inzenjer =
//        SoftverskiInzenjer("", "Petrovic", 5, setOf("Java"), 2)
//         SoftverskiInzenjer("Marko", "", 5, setOf("Java"), 2)
//         SoftverskiInzenjer("Marko", "Petrovic", -1, setOf("Java"), 2)
//         SoftverskiInzenjer("Marko", "Petrovic", 5, emptySet(), 2) },
//         SoftverskiInzenjer("Marko", "Petrovic", 5, setOf("Java"), -3)
//         InzenjerElektrotehnike("Ivan", "Novak", 5, emptySet(), 2)
//         InzenjerElektrotehnike("Ivan", "Novak", 5, setOf("C"), -1)


    //*koristen AI alat za pregledniji prikaz rezultata npr.generisao --- uz naslov
    //implementirane funkcionalnosti:

    ispisiInzenjere(inzenjeri)

    val grupisano = grupisiPoEkspertizi(inzenjeri)
    println("\n--- Inzenjeri sa vise od 5 godina iskustva grupisani po ekspertizama ---")
    for ((exp, lista) in grupisano) {
        print("$exp: ")
        for (i in lista) print("${i.identitet()};")
        println(" ")
    }

    val najiskusniji = najiskusnijiPoTipu(inzenjeri)
    println("\n--- Najiskusniji po tipu inzenjera ---")
    for ((tip, eng) in najiskusniji) {
        println("$tip: ${eng.identitet()} - ${eng.iskustvo} godina iskustva")
    }

    val total = ukupnoProjekataICertifikata(inzenjeri)
    println("\n--- Ukupno projekata i certifikata --- \n$total")



    println("\n--- Provjere ispravnosti ---")
    //poredim rezultate implementiranih funkcija sa rezultatima dobijenim na "direktan" nacin, bez fold,reduce i aggregate
    //*koristen AI alat za prijedlog funkcija potrebnih za direktan izracun
    var najiskusnijiOK = true
    for ((tip, dobijeni) in najiskusniji) {
        val listaPoTipu= inzenjeri.filter { it.getTitula() == tip }
        val ocekivani= listaPoTipu.maxByOrNull { it.iskustvo } // direktni proračun - najveci broj godina iskustva od svih inzenjera istog tipa
        val pass = (ocekivani == dobijeni)
        println("Najiskusniji $tip: očekivano=${ocekivani?.identitet()}, dobijeno=${dobijeni.identitet()} -> ${if (pass) "PASS" else "FAIL"}")
        if (!pass) {najiskusnijiOK = false
            println("  ---> Neispravno!")}
    }

    val ocekivaniBroj = inzenjeri.filterIsInstance<SoftverskiInzenjer>().sumOf { it.projekti } +
            inzenjeri.filterIsInstance<InzenjerElektrotehnike>().sumOf { it.certifikati } //direktno sabiranje projekata i certifikata
    val aggregateOK = (ocekivaniBroj == total)
    println("Ukupno projekata i certifikata: ocekivano = $ocekivaniBroj, dobijeno = $total -> ${if (aggregateOK) "PASS" else "FAIL"}")
    if (!aggregateOK) println("  ---> Neispravno!")

    val sviGrupisani= grupisano.values.flatten()
    val foldOK = sviGrupisani.all { it.iskustvo > 5 } //provjerava da svi elementi koje je grupisao fold imaju iskustvo>5
    println("Svi u fold grupi imaju >5 godina iskustva: $foldOK")
    if (!foldOK) {
        println("  ---> Neispravno: nalazi se inženjer sa <=5 godina u rezultatu fold().")
    }

    val sveProvjere = najiskusnijiOK && aggregateOK && foldOK
    println("\nRezime provjera: ${if (sveProvjere) "SVE PROLAZE (PASS)" else "NEKE PROLAZE (FAILED)"}")

}
