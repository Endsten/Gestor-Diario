# Gestor Diário 🏍️📦

O **Gestor Diário** é um aplicativo Android desenvolvido para auxiliar motofretistas autônomos e pequenos empreendedores na gestão eficiente de seus custos operacionais e controle de estoque. O foco do projeto é transformar o controle manual (frequentemente feito em papel) em dados estruturados, permitindo uma visão clara do lucro real e a programação de manutenções preventivas.

Este projeto foi desenvolvido como parte de uma atividade de extensão universitária para promover a inclusão digital e a autonomia financeira na comunidade local.

## 🚀 Funcionalidades

### 1. Gestão de Motocicletas
*   **Cadastro Completo:** Registro de placa, modelo e quilometragem inicial.
*   **Controle de Manutenção:** Registro detalhado de serviços realizados para aumentar a vida útil do veículo.
*   **Histórico de Abastecimento:** Controle de litros, valores pagos e atualização automática da quilometragem atual.
*   **Edição e Exclusão:** Flexibilidade para corrigir dados ou remover veículos.

### 2. Almoxarifado (Estoque)
*   **Controle de Insumos:** Cadastro de peças (pneus, óleo, pastilhas) com valor de custo e unidade de medida.
*   **Registro de Uso:** Baixa automática no estoque ao registrar a retirada de um item, com identificação do responsável.
*   **Restaurar Estoque:** Ao excluir um lançamento de uso por erro, a quantidade retorna automaticamente ao saldo do produto.

### 3. Painel de Controle (Dashboard)
*   **Visualização Estratégica:** Gráficos de gastos comparativos entre motos e cartões de resumo financeiro.
*   **Filtros Inteligentes:** Filtragem por períodos pré-definidos (7, 15, 30, 90 dias) ou por intervalo de datas personalizado (digitação manual).
*   **Gestão Centralizada:** Possibilidade de editar ou excluir qualquer lançamento diretamente na lista do painel.
*   **Exportação de Dados:** Geração de relatórios em formato CSV para abertura em softwares como Excel ou Google Sheets.

## 🛠️ Tecnologias Utilizadas

*   **Linguagem:** [Kotlin](https://kotlinlang.org/)
*   **Interface:** [Jetpack Compose](https://developer.android.com/jetpack/compose) com **Material Design 3**
*   **Banco de Dados:** [Room Persistence Library](https://developer.android.com/training/data-storage/room) (SQLite local)
*   **Arquitetura:** MVVM (Model-View-ViewModel)
*   **Navegação:** Navigation Compose
*   **Fluxo de Dados:** StateFlow e Coroutines para operações assíncronas e reativas.


## 🔧 Como Executar o Projeto

1.  Clone este repositório:
    ```bash
    git clone https://github.com/SEU_USUARIO/GestorDiario.git
    ```
2.  Abra o projeto no **Android Studio** (versão Ladybug ou superior recomendada).
3.  Certifique-se de que o **JDK 17** está configurado nas opções do Gradle.
4.  Sincronize o projeto com os arquivos Gradle.
5.  Execute o app em um emulador ou dispositivo físico com Android 7.0 (API 24) ou superior.

## 📄 Manual do Usuário

Um guia detalhado sobre como operar o aplicativo.
# Manual do Usuário - Gestor Diário 🏍️

Bem-vindo ao **Gestor Diário**, sua ferramenta completa para o controle operacional de motocicletas e almoxarifado. Este manual guiará você por todas as funcionalidades do aplicativo.

---

## 1. Navegação Principal
O aplicativo é dividido em três áreas principais acessíveis pela barra inferior:
*   **Painel (Dashboard):** Visão geral dos gastos, gráficos e relatórios.
*   **Estoque (Almoxarifado):** Controle de peças e insumos.
*   **Motos:** Cadastro e registro de atividades dos veículos.

---

## 2. Gestão de Motos
Acesse esta tela para gerenciar seus veículos de trabalho.

### Cadastrar uma Moto
1. Clique no botão flutuante **"+"** no canto inferior direito.
2. Preencha a **Placa**, o **Modelo** e a **Quilometragem (KM) inicial**.
3. Clique em **Salvar**.

### Editar ou Excluir
*   Use o ícone de **Lápis** no cartão da moto para alterar dados cadastrais.
*   Use o ícone da **Lixeira** para remover o veículo permanentemente.

### Registrar Atividades
*   **Abastecer:** Informe os litros, o valor pago e o KM atual do painel. O KM da moto será atualizado automaticamente.
*   **Manutenção:** Registre a descrição do serviço (ex: Troca de óleo) e o custo total.

---

## 3. Almoxarifado (Estoque)
Mantenha o controle das peças que você possui em mãos.

### Adicionar Itens
1. Clique no botão **"+"**.
2. Insira o nome do item, unidade de medida (Ex: Litro, Unidade), quantidade atual e valor de custo unitário.

### Registrar Retirada (Uso)
1. Clique no botão **"Retirar"** no cartão do item.
2. Informe a **Quantidade** retirada e o nome do **Responsável**.
3. O saldo em estoque será atualizado na hora.

### Correções
*   Se cadastrou um item errado, use o ícone da **Lixeira** para removê-lo.

---

## 4. Painel de Controle (Dashboard)
Analise sua saúde financeira e corrija lançamentos feitos por erro.

### Visualização e Gráficos
*   O gráfico no topo mostra a comparação de gastos entre as motos cadastradas.
*   O cartão de resumo exibe o total gasto com combustível e manutenção de forma separada.

### Filtros de Período
Você pode filtrar os dados de duas formas:
1.  **Filtro Rápido:** Selecione 7, 15, 30 ou 90 dias no botão de período.
2.  **Filtro Personalizado:** Escolha "Personalizado" e digite manualmente a data de **Início** e **Fim** (formato: `dd/mm/aaaa`).

### Edição e Exclusão de Lançamentos
*   Na lista de lançamentos, clique no ícone de **Lápis** para corrigir valores ou descrições.
*   Clique na **Lixeira** para apagar um registro errado. 
    *   *Nota: Se você excluir uma retirada de estoque, a quantidade voltará automaticamente para o saldo do produto.*

### Exportar Relatório
Clique no ícone de **Compartilhar** (topo direito) para gerar um arquivo **CSV**. Este arquivo pode ser aberto no Excel ou Google Sheets para uma gestão ainda mais detalhada.

---

## 5. Dicas Importantes
*   **Modo Offline:** O aplicativo salva tudo diretamente no seu celular. Você não precisa de internet para registrar seus gastos.
*   **Backup:** Lembre-se que os dados residem apenas no aparelho. Ao desinstalar o app sem backup, os dados serão perdidos.
*   **Precisão:** Ao abastecer, tente sempre informar o KM exato do painel para que os relatórios de consumo sejam precisos.

---
**Equipe de Desenvolvimento**
*Gestor Diário - Transformando dados em economia.*

