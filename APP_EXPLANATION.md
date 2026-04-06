# Guia da Aplicação

## Resumo

Esta aplicação é uma **Pokédex multiplataforma** feita com **Kotlin Multiplatform** e **Compose Multiplatform**.

O projeto foi estruturado para:

- compartilhar a maior parte da UI e da lógica entre Android e iOS
- manter a navegação e o estado principal em código comum
- diferenciar visualmente Android e iOS no ponto pedido pelo projeto
- usar dados mockados organizados em uma camada de repositório

O fluxo principal do app é:

1. abrir a Home
2. navegar para a lista da Pokédex
3. abrir os detalhes de um Pokémon
4. adicionar esse Pokémon ao time
5. visualizar e remover itens na tela de Meu Time


## Estrutura do Projeto

### `composeApp/src/commonMain`

Contém o código compartilhado entre Android e iOS.

Principais áreas:

- `data/`
- `navigation/`
- `ui/screens/`
- `ui/components/`
- `ui/theme/`
- `App.kt`

### `composeApp/src/androidMain`

Contém o código específico de Android.

Principais arquivos:

- `MainActivity.kt`
- `ui/screens/TeamScreen.kt`
- `AndroidManifest.xml`

### `composeApp/src/iosMain`

Contém o código específico de iOS.

Principais arquivos:

- `MainViewController.kt`
- `ui/screens/TeamScreen.kt`

### `iosApp`

É a entrada do aplicativo iOS via SwiftUI/Xcode, usada para embutir a UI Compose dentro do app iOS.


## Fluxo Geral da Aplicação

O ponto central do app é o arquivo [App.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/kotlin/com/example/pokedex/App.kt).

Ele é responsável por:

- aplicar o tema da aplicação
- criar o `NavController`
- manter o estado do time
- montar o `Scaffold`
- configurar o `NavHost`

### Fluxo interno do `App.kt`

1. o app aplica `PokedexTheme`
2. cria o `NavController` com `rememberNavController()`
3. cria o estado `team` com `remember { mutableStateOf(...) }`
4. observa a rota atual para atualizar `topBar` e `bottomBar`
5. monta o `NavHost` com as rotas

### Como o estado percorre o app

- `team` nasce em `App.kt`
- `PokemonDetailsScreen` recebe um callback para adicionar um Pokémon
- `TeamScreen` recebe a lista pronta e um callback para remover Pokémon

Isso faz com que a lógica de time fique centralizada e simples.


## Camada de Dados

### `Pokemon.kt`

Arquivo:

- [Pokemon.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/kotlin/com/example/pokedex/data/Pokemon.kt)

Esse arquivo define:

- `PokemonType`
- `Pokemon`

### `PokemonType`

É um `enum class` com todos os tipos usados pela aplicação:

- FIRE
- WATER
- GRASS
- ELECTRIC
- PSYCHIC
- ICE
- DRAGON
- DARK
- FAIRY
- NORMAL
- FIGHTING
- FLYING
- POISON
- GROUND
- ROCK
- BUG
- GHOST
- STEEL

### `Pokemon`

É uma `data class` que representa um Pokémon.

Campos:

- `id`
- `name`
- `types`
- `description`
- `hp`
- `attack`
- `defense`
- `speed`

### `PokemonRepository.kt`

Arquivo:

- [PokemonRepository.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/kotlin/com/example/pokedex/data/PokemonRepository.kt)

Esse arquivo implementa um repositório mockado com uma lista fixa de 10 Pokémon.

Funções principais:

- `getPokemonList()`
- `getPokemonById(id)`

### Papel do repositório

Mesmo sem usar rede ou banco, o repositório separa a origem dos dados da UI.

Isso melhora:

- organização
- legibilidade
- manutenção


## Navegação

### `Routes.kt`

Arquivo:

- [Routes.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/kotlin/com/example/pokedex/navigation/Routes.kt)

As rotas são modeladas com `sealed interface` e `@Serializable`.

Rotas existentes:

- `Route.Home`
- `Route.PokedexList`
- `Route.PokemonDetails(id)`
- `Route.TeamBuilder`

### Vantagens dessa abordagem

- navegação tipada
- passagem segura de parâmetros
- menos dependência de strings soltas

### Como o `NavHost` foi montado

No `App.kt`, cada rota é associada a uma tela:

- `Home` -> `HomeScreen`
- `PokedexList` -> `PokedexListScreen`
- `PokemonDetails(id)` -> `PokemonDetailsScreen`
- `TeamBuilder` -> `TeamScreen`

### Navegação de topo

O `bottomBar` usa navegação de nível superior com `findStartDestination()`.

Isso garante:

- comportamento estável ao trocar de aba
- restauração de estado
- evitar múltiplas cópias da mesma tela na pilha


## Gerenciamento de Estado

O estado principal do app é o time do usuário.

Esse estado fica em:

- [App.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/kotlin/com/example/pokedex/App.kt)

### Como foi implementado

Foi usado Compose state:

- `remember`
- `mutableStateOf`

### Fluxo de adição

1. o usuário entra nos detalhes de um Pokémon
2. a tela chama `onAddToTeam`
3. o `App.kt` busca o Pokémon no repositório
4. verifica se ele já não está no time
5. adiciona à lista

### Fluxo de remoção

1. o usuário entra na tela Meu Time
2. clica em remover
3. a tela chama `onRemoveFromTeam`
4. o `App.kt` filtra o item da lista

### Característica do estado

O estado é:

- local
- reativo
- em memória

Ou seja, ao alterar a lista, a UI recompõe automaticamente.


## Tema e Identidade Visual

### `PokedexTheme.kt`

Arquivo:

- [PokedexTheme.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/kotlin/com/example/pokedex/ui/theme/PokedexTheme.kt)

Foi criado um tema próprio da aplicação com:

- `lightColorScheme`
- `darkColorScheme`

Esse arquivo centraliza as cores principais da aplicação.

### Objetivo do tema

- evitar aparência totalmente padrão
- dar identidade ao projeto
- manter consistência entre as telas


## Componentes Compartilhados

### `PokemonUi.kt`

Arquivo:

- [PokemonUi.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/kotlin/com/example/pokedex/ui/components/PokemonUi.kt)

Esse arquivo concentra os componentes reutilizáveis relacionados aos Pokémon.

### `PokemonArtwork`

Responsável por mostrar a imagem do Pokémon.

Ele recebe:

- `pokemonId`
- `contentDescription`
- `modifier`
- `contentScale`

### Como a imagem é resolvida

Existe um mapeamento interno de `id -> drawable resource`.

Exemplos:

- `1 -> bulbasaur`
- `25 -> pikachu`
- `149 -> dragonite`

Se não existir imagem cadastrada para um `id`, é mostrado um placeholder com ícone de Pokébola.

### `PokemonTypeTag`

Renderiza um tipo como uma pequena chip visual.

Características:

- cor baseada no tipo
- formato arredondado
- tamanho compacto
- texto em uma linha

### `PokemonTypeRow`

Renderiza os tipos em uma `FlowRow`.

Isso permite:

- quebrar a linha quando necessário
- manter espaçamento controlado
- evitar que palavras sejam quebradas verticalmente

### `pokemonBrush`

Gera o gradiente usado em cards e áreas de destaque.

O gradiente é construído a partir dos tipos do Pokémon.

### `typeColor`

Mapeia cada `PokemonType` para uma cor específica.

Isso dá consistência visual ao app inteiro.


## Recursos de Imagem

As imagens dos Pokémon estão em:

- [composeResources/drawable](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/composeResources/drawable)

Essa abordagem foi escolhida em vez de carregar imagens por URL.

### Motivos

- funciona melhor no Preview
- não depende de internet
- evita falhas em sala ou em demonstração
- simplifica o comportamento entre plataformas

### Como o Compose acessa esses recursos

O projeto usa `org.jetbrains.compose.resources` para ler os drawables compartilhados do `commonMain`.


## Telas Compartilhadas

### Home

Arquivo:

- [HomeScreen.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/kotlin/com/example/pokedex/ui/screens/HomeScreen.kt)

Responsabilidades:

- apresentar o app
- mostrar uma entrada visual forte
- dar acesso rápido à Pokédex e ao Time

Elementos principais:

- card hero com imagem
- dois cards de ação
- botão para iniciar o fluxo principal

### Pokédex

Arquivo:

- [PokedexListScreen.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/kotlin/com/example/pokedex/ui/screens/PokedexListScreen.kt)

Responsabilidades:

- mostrar a lista dos Pokémon em grade
- permitir busca
- permitir filtro por tipo
- indicar se o Pokémon já está no time

### Fluxo da tela da Pokédex

1. lê a lista do repositório
2. ordena por `id`
3. mantém `query` e `selectedType` em estado local
4. filtra os Pokémon com base nesses dois valores
5. renderiza os resultados em `LazyVerticalGrid`

### Busca

A busca usa `SearchBar` Material 3.

Ela permite procurar por:

- nome
- número do Pokémon

### Filtro por tipo

Os filtros são renderizados em uma `LazyRow` horizontal.

O usuário pode:

- ver todos
- selecionar um tipo específico

### Card de Pokémon

Cada card mostra:

- número
- nome
- tipos
- imagem
- selo “No time” quando aplicável

### Detalhes

Arquivo:

- [PokemonDetailsScreen.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/kotlin/com/example/pokedex/ui/screens/PokemonDetailsScreen.kt)

Responsabilidades:

- mostrar informações completas do Pokémon
- mostrar os tipos
- mostrar descrição
- mostrar stats
- adicionar ao time

### Fluxo da tela de detalhes

1. recebe `pokemonId`
2. busca o Pokémon no repositório
3. monta o cabeçalho com gradiente e imagem
4. renderiza descrição e stats
5. verifica se o Pokémon já está no time
6. mostra botão adequado

### Stats

Os atributos são renderizados com:

- nome do atributo
- valor numérico
- `LinearProgressIndicator`


## Tela de Time com `expect/actual`

### Contrato comum

Arquivo:

- [common TeamScreen.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/commonMain/kotlin/com/example/pokedex/ui/screens/TeamScreen.kt)

Esse arquivo declara:

- `expect fun TeamScreen(...)`

Isso significa que o código comum conhece a função, mas a implementação real depende da plataforma.

### Android

Arquivo:

- [android TeamScreen.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/androidMain/kotlin/com/example/pokedex/ui/screens/TeamScreen.kt)

Características:

- visual mais Material
- cards com linguagem mais Android
- ações com ícones e estrutura direta

### iOS

Arquivo:

- [ios TeamScreen.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/iosMain/kotlin/com/example/pokedex/ui/screens/TeamScreen.kt)

Características:

- fundo mais claro
- aparência mais leve
- cabeçalho com mais espaço
- cards brancos lembrando grouped cards
- composição visual mais próxima do estilo iOS

### Benefício dessa abordagem

Ela permite:

- compartilhar o fluxo e os dados
- customizar a experiência visual por plataforma
- manter o projeto organizado


## Entradas por Plataforma

### Android

Arquivos:

- [MainActivity.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/androidMain/kotlin/com/example/pokedex/MainActivity.kt)
- [AndroidManifest.xml](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/androidMain/AndroidManifest.xml)

O Android inicia o Compose diretamente dentro da `MainActivity`.

### iOS

Arquivos:

- [MainViewController.kt](/home/bruno-aseff/Repos/uni/kmp-m1/composeApp/src/iosMain/kotlin/com/example/pokedex/MainViewController.kt)
- [ContentView.swift](/home/bruno-aseff/Repos/uni/kmp-m1/iosApp/iosApp/ContentView.swift)

O iOS usa `ComposeUIViewController` embutido em SwiftUI.


## Bibliotecas Utilizadas

### Compose Multiplatform

Base da interface compartilhada.

### Material 3

Usado em elementos como:

- `Scaffold`
- `TopAppBar`
- `NavigationBar`
- `Card`
- `SearchBar`
- `LinearProgressIndicator`

### Navigation Compose

Usado para:

- `NavController`
- `NavHost`
- navegação entre telas

### Kotlinx Serialization

Usado para suportar as rotas tipadas serializáveis.

### Compose Resources

Usado para:

- armazenar imagens em `commonMain`
- carregar drawables compartilhados


## Conceitos de Kotlin Aplicados

### `data class`

Usado para modelar `Pokemon`.

### `enum class`

Usado para modelar os tipos em `PokemonType`.

### `object`

Usado em `PokemonRepository` como singleton simples.

### `sealed interface`

Usado nas rotas para limitar as possibilidades de navegação.

### `remember` e `mutableStateOf`

Usados para estado reativo em Compose.

### Funções `@Composable`

Usadas em toda a construção da interface.


## Conceitos de Kotlin Multiplatform Aplicados

### `commonMain`

Compartilha:

- dados
- navegação
- telas principais
- componentes
- tema

### `androidMain`

Contém a integração e a UI específica de Android.

### `iosMain`

Contém a integração e a UI específica de iOS.

### `expect/actual`

Usado para a tela de Meu Time, que tem implementação diferente em cada plataforma.


## Resumo do Funcionamento Interno

Em alto nível, o fluxo do código é:

1. a plataforma abre a entry point
2. a entry point renderiza `App()`
3. `App()` aplica o tema, cria navegação e estado
4. o `NavHost` decide qual tela renderizar
5. as telas leem dados do repositório
6. callbacks alteram o estado do time
7. a UI recompõe automaticamente

Isso mantém o projeto simples, compartilhado e fácil de entender.
