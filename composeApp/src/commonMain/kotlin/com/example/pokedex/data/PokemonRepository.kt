package com.example.pokedex.data

object PokemonRepository {
    private val pokemonList = listOf(
        Pokemon(
            id = 1,
            name = "Bulbasaur",
            types = listOf(PokemonType.GRASS, PokemonType.POISON),
            description = "There is a plant seed on its back from the day this Pokémon is born. The seed slowly grows larger.",
            hp = 45, attack = 49, defense = 49, speed = 45,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
        ),
        Pokemon(
            id = 4,
            name = "Charmander",
            types = listOf(PokemonType.FIRE),
            description = "It has a preference for hot things. When it rains, steam is said to spout from the tip of its tail.",
            hp = 39, attack = 52, defense = 43, speed = 65,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/4.png"
        ),
        Pokemon(
            id = 7,
            name = "Squirtle",
            types = listOf(PokemonType.WATER),
            description = "When it retracts its long neck into its shell, it squirts out water with vigorous force.",
            hp = 44, attack = 48, defense = 65, speed = 43,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/7.png"
        ),
        Pokemon(
            id = 25,
            name = "Pikachu",
            types = listOf(PokemonType.ELECTRIC),
            description = "When it smashes its opponent with its bolt-shaped tail, it delivers a jolt of electricity comparable to a lightning strike.",
            hp = 35, attack = 55, defense = 40, speed = 90,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png"
        ),
        Pokemon(
            id = 133,
            name = "Eevee",
            types = listOf(PokemonType.NORMAL),
            description = "An extremely rare Pokémon that may evolve in a number of different ways depending on stimuli.",
            hp = 55, attack = 55, defense = 50, speed = 55,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/133.png"
        ),
        Pokemon(
            id = 150,
            name = "Mewtwo",
            types = listOf(PokemonType.PSYCHIC),
            description = "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.",
            hp = 106, attack = 110, defense = 90, speed = 130,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/150.png"
        ),
        Pokemon(
            id = 94,
            name = "Gengar",
            types = listOf(PokemonType.GHOST, PokemonType.POISON),
            description = "On the night of a full moon, if shadows move on their own and laugh, it must be Gengar's doing.",
            hp = 60, attack = 65, defense = 60, speed = 110,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/94.png"
        ),
        Pokemon(
            id = 149,
            name = "Dragonite",
            types = listOf(PokemonType.DRAGON, PokemonType.FLYING),
            description = "It is said that this Pokémon lives somewhere in the sea and that it flies. However, these are only rumors.",
            hp = 91, attack = 134, defense = 95, speed = 80,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/149.png"
        ),
        Pokemon(
            id = 3,
            name = "Venusaur",
            types = listOf(PokemonType.GRASS, PokemonType.POISON),
            description = "Its plant blooms when it is absorbing solar energy. It stays on the move to seek sunlight.",
            hp = 80, attack = 82, defense = 83, speed = 80,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/3.png"
        ),
        Pokemon(
            id = 6,
            name = "Charizard",
            types = listOf(PokemonType.FIRE, PokemonType.FLYING),
            description = "It spits fire that is hot enough to melt boulders. It may cause forest fires by blowing flames.",
            hp = 78, attack = 84, defense = 78, speed = 100,
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/6.png"
        )
    )

    fun getPokemonList(): List<Pokemon> = pokemonList

    fun getPokemonById(id: Int): Pokemon? = pokemonList.find { it.id == id }
}
