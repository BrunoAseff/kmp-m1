Pokédex Multiplatform

1 OBJETIVO
Construir uma aplicação Pokédex utilizando Kotlin
Multiplatform (KMP) e Compose Multiplatform. O foco é a aplicação
de conceitos avançados de navegação, arquitetura de UI, gerenciamento de
estado e a diferenciação de experiência entre plataformas (Android e iOS)
através do mecanismo de expect/actual.

2 ESTRUTURA BASE E NAVEGAÇÃO
A aplicação deve ser construída sobre o componente Scaffold,
garantindo uma estrutura de UI consistente que suporte barras de navegação e
carregamento de conteúdo.
• Scaffold: Implementação obrigatória do Scaffold para gerenciar a
estrutura da tela, incluindo topBar (título dinâmico) e bottomBar (para
alternar entre a Pokédex e o Time).
• Jetpack Navigation: Implementar rotas tipadas utilizando a biblioteca de
navegação oficial com suporte a @Serializable.
• Fluxo de Telas:
o Home Screen: Dashboard de entrada com identidade visual
impactante e botões de acesso rápido.
o Pokedex List (Grid): Listagem de Pokémons utilizando
LazyVerticalGrid, exibindo nome, número e tipos.
o Pokemon Details: Exibição detalhada com descrição, atributos
(stats) e o botão "Adicionar ao Time".
o Team Builder: Tela dedicada para listar os Pokémons selecionados
pelo usuário.

3 CAMADA DE DADOS
Para simular uma consulta real a um repositório ou API, o aluno deve
organizar o projeto seguindo boas práticas de separação de pacotes:
• Simulação de Consulta: Utilize uma lista de objetos (Data Classes)
mockados que representem pelo menos 10 Pokémons com diferentes
tipos e atributos.

• Repositório: Recomenda-se a criação de uma função que retorne esses
dados (ex: getPokemonList()), simulando o comportamento de uma busca
em banco de dados ou rede.

4 COMPONENTES E ESTÉTICA
Deve ser utilizado componentes do Compose que  elevem a estética do app (ex: ElevatedCard, Material3 SearchBar,
LinearProgressIndicator para os stats, gradientes de cores baseados nos tipos
dos Pokémons). O design deve ser inspirado em aplicações modernas.
A tela de Team Builder (meu time) deverá implementar de forma distinta
para cada sistema operacional, utilizando obrigatoriamente o mecanismo de
expect/actual.
• No Android (androidMain), a interface deve seguir os padrões visuais do
Material Design.
• No iOS (iosMain), a implementação deve utilizar componentes com
estética Apple.
É fundamental que a diferenciação de UI entre as plataformas seja
claramente visível ao executar o projeto em cada simulador/emulador.
Para o gerenciamento de estado do time você deve implementar uma
lógica que permita adicionar um Pokémon na tela de Detalhes e visualizá-lo na
tela de Team Builder.

5 CRITÉRIOS DE AVALIAÇÃO
Os critérios de avaliação consideram a correta aplicação da arquitetura
e do Scaffold, a organização do pacote de dados mockados e a eficiência na
navegação tipada. Além disso, será pontuada a implementação do mecanismo
de expect/actual para a diferenciação visual entre Android e iOS, a qualidade
estética da UI utilizando componentes do Material 3 (como SearchBar e
ElevatedCard) e o funcionamento lógico do fluxo de gerenciamento de estado ao
adicionar Pokémons ao Team Builder.

7 CONCEPÇÃO VISUAL
Para a concepção visual e funcional do aplicativo, você deve
buscar inspiração em interfaces modernas de design mobile, como o Material
Design 3 (Google) e o Human Interface Guidelines (Apple)