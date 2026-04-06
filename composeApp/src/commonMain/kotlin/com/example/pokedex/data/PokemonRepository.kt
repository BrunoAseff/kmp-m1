package com.example.pokedex.data

object PokemonRepository {
    private val pokemonList = listOf(
        Pokemon(
            id = 1,
            name = "Bulbasaur",
            types = listOf(PokemonType.GRASS, PokemonType.POISON),
            description = "Há uma semente de planta em suas costas desde o dia em que este Pokémon nasce. A semente cresce lentamente com o tempo.",
            hp = 45, attack = 49, defense = 49, speed = 45
        ),
        Pokemon(
            id = 4,
            name = "Charmander",
            types = listOf(PokemonType.FIRE),
            description = "Tem preferência por coisas quentes. Quando chove, diz-se que vapor sai da ponta de sua cauda.",
            hp = 39, attack = 52, defense = 43, speed = 65
        ),
        Pokemon(
            id = 7,
            name = "Squirtle",
            types = listOf(PokemonType.WATER),
            description = "Quando recolhe seu longo pescoço para dentro do casco, esguicha água com grande força.",
            hp = 44, attack = 48, defense = 65, speed = 43
        ),
        Pokemon(
            id = 25,
            name = "Pikachu",
            types = listOf(PokemonType.ELECTRIC),
            description = "Quando acerta o oponente com sua cauda em forma de raio, libera uma descarga elétrica comparável a um relâmpago.",
            hp = 35, attack = 55, defense = 40, speed = 90
        ),
        Pokemon(
            id = 133,
            name = "Eevee",
            types = listOf(PokemonType.NORMAL),
            description = "Um Pokémon extremamente raro que pode evoluir de várias formas diferentes dependendo dos estímulos que recebe.",
            hp = 55, attack = 55, defense = 50, speed = 55
        ),
        Pokemon(
            id = 150,
            name = "Mewtwo",
            types = listOf(PokemonType.PSYCHIC),
            description = "Foi criado por um cientista após anos de terríveis experimentos de manipulação genética e engenharia de DNA.",
            hp = 106, attack = 110, defense = 90, speed = 130
        ),
        Pokemon(
            id = 94,
            name = "Gengar",
            types = listOf(PokemonType.GHOST, PokemonType.POISON),
            description = "Na noite de lua cheia, se as sombras se moverem sozinhas e rirem, isso certamente é obra de Gengar.",
            hp = 60, attack = 65, defense = 60, speed = 110
        ),
        Pokemon(
            id = 149,
            name = "Dragonite",
            types = listOf(PokemonType.DRAGON, PokemonType.FLYING),
            description = "Dizem que este Pokémon vive em algum lugar do mar e que voa pelos céus. No entanto, isso ainda é tratado como rumor.",
            hp = 91, attack = 134, defense = 95, speed = 80
        ),
        Pokemon(
            id = 3,
            name = "Venusaur",
            types = listOf(PokemonType.GRASS, PokemonType.POISON),
            description = "Sua planta floresce quando está absorvendo energia solar. Ele permanece em movimento para buscar luz do sol.",
            hp = 80, attack = 82, defense = 83, speed = 80
        ),
        Pokemon(
            id = 6,
            name = "Charizard",
            types = listOf(PokemonType.FIRE, PokemonType.FLYING),
            description = "Cospe fogo quente o bastante para derreter pedras. Pode causar incêndios florestais ao soprar suas chamas.",
            hp = 78, attack = 84, defense = 78, speed = 100
        )
    )

    fun getPokemonList(): List<Pokemon> = pokemonList

    fun getPokemonById(id: Int): Pokemon? = pokemonList.find { it.id == id }
}
