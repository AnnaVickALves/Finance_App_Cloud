# FinanceApp - Gerenciador Financeiro

Um aplicativo desktop intuitivo para gerenciamento de finanças pessoais, permitindo o controle completo de receitas e despesas com recursos avançados de relatórios e sincronização em nuvem.

## 🎯 Características Principais

- **Gerenciamento de Transações**: Registre receitas e despesas com categorização detalhada
- **Dashboard Interativo**: Visualize seu balanço atual e gráficos de análise financeira
- **Filtros Avançados**: Filtre transações por período, categoria e tipo
- **Exportação de Dados**: Exporte relatórios em CSV e PDF
- **Impressão de Relatórios**: Imprima seus extratos financeiros diretamente
- **Sincronização em Nuvem**: Backup automático de seus dados
- **Interface Moderna**: Desenvolvido com JavaFX para uma experiência visual agradável

## 📋 Pré-requisitos

- **Java**: JDK 21 ou superior
- **PostgreSQL**: Versão 12 ou superior
- **Maven**: Versão 3.8 ou superior

## 🚀 Como Instalar

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/FinanceApp.git
cd finnace_app
```

### 2. Configure o Banco de Dados

Crie um banco de dados PostgreSQL para a aplicação:

```sql
CREATE DATABASE financeapp;
```

### 3. Configure a Conexão com o Banco de Dados

Localize o arquivo `DatabaseConnection.java` e configure as credenciais:

```java
// Edite as variáveis de conexão
private static final String URL = "jdbc:postgresql://localhost:5432/financeapp";
private static final String USER = "seu_usuario";
private static final String PASSWORD = "sua_senha";
```

### 4. Compile e Execute

```bash
# Compile o projeto
mvn clean compile

# Execute a aplicação
mvn javafx:run
```

Ou crie um executável:

```bash
# Empacote a aplicação
mvn clean package

# Execute o JAR gerado
java -jar target/financeapp-1.0.0.jar
```

## 📂 Estrutura do Projeto

```
finnace_app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/senac/financeapp/
│   │   │       ├── MainApplication.java          # Classe principal da aplicação
│   │   │       ├── MainLauncher.java             # Launcher do aplicativo
│   │   │       ├── controller/
│   │   │       │   └── DashboardController.java  # Lógica da interface gráfica
│   │   │       ├── dao/
│   │   │       │   ├── DatabaseConnection.java   # Configuração do banco de dados
│   │   │       │   ├── TransactionDAO.java       # Operações com transações
│   │   │       │   └── BackupService.java        # Serviço de backup
│   │   │       ├── model/
│   │   │       │   └── Transaction.java          # Modelo de dados
│   │   │       └── util/
│   │   │           ├── CloudSyncService.java     # Sincronização em nuvem
│   │   │           ├── DateUtil.java             # Utilitários de data
│   │   │           └── NumberUtil.java           # Utilitários numéricos
│   │   └── resources/
│   │       └── com/senac/financeapp/
│   │           └── view/
│   │               ├── DashboardView.fxml        # Layout da interface
│   │               └── style.css                 # Estilos da aplicação
│   └── test/                                     # Testes unitários
├── pom.xml                                       # Configuração Maven
└── README.md                                     # Este arquivo
```

## 🔧 Configuração das Dependências

O projeto utiliza as seguintes dependências principais:

| Dependência | Versão | Propósito |
|---|---|---|
| **JavaFX Controls** | 21.0.1 | Interface gráfica |
| **JavaFX FXML** | 21.0.1 | Carregamento de layouts XML |
| **PostgreSQL Driver** | 42.7.3 | Conexão com PostgreSQL |
| **HikariCP** | 5.1.0 | Pool de conexões |
| **OpenPDF** | 1.3.40 | Geração de relatórios PDF |

## 💼 Funcionalidades Detalhadas

### 1. Adicionar Transações

- Selecione o tipo (Receita/Despesa)
- Informe o valor
- Escolha a data
- Categorize a transação
- Adicione uma descrição
- Clique em "Salvar Transação"

### 2. Visualizar Transações

- Consulte o histórico completo na tabela
- O saldo atual é atualizado automaticamente
- Gráficos mostram a distribuição de despesas por categoria

### 3. Filtrar Dados

- Use o filtro por período (data inicial e final)
- Filtre por categoria específica
- Aplique múltiplos filtros simultaneamente

### 4. Exportar Relatórios

**CSV**: Exporte para análise em planilhas
```bash
Clique em "Exportar CSV" e selecione o local
```

**PDF**: Gere relatórios formatados
```bash
Clique em "Exportar PDF" para download
```

### 5. Imprimir Extratos

- Clique em "Imprimir"
- Configure as opções da impressora
- Confirme para imprimir

## 🗄️ Modelo de Dados

### Transaction (Transação)

```java
class Transaction {
    int id;                    // Identificador único
    String type;              // "RECEITA" ou "DESPESA"
    BigDecimal amount;        // Valor da transação
    LocalDate date;           // Data da transação
    String category;          // Categoria (ex: "Alimentação", "Transporte")
    String description;       // Descrição detalhada
}
```

## 🔐 Segurança

- **Pool de Conexões**: Utiliza HikariCP para gerenciamento eficiente de conexões
- **Prepared Statements**: Previne SQL Injection
- **Validação de Dados**: Valores monetários validados como BigDecimal
- **Sincronização Segura**: Backup automático em nuvem

## 🐛 Solução de Problemas

### Erro de Conexão ao Banco de Dados

Verifique se:
- PostgreSQL está em execução
- As credenciais estão corretas em `DatabaseConnection.java`
- O banco de dados `financeapp` foi criado

### Erro de Compilação

Execute um limpeza completa:

```bash
mvn clean
mvn compile
```

### A Interface Não Carrega

Verifique se os arquivos FXML e CSS estão em:
```
src/main/resources/com/senac/financeapp/view/
```

## 📊 Exemplos de Uso

### Adicionar uma Receita Mensal

1. Selecione "RECEITA" no tipo
2. Informe o valor: 3000.00
3. Escolha a data
4. Categoria: "Salário"
5. Descrição: "Salário do mês de fevereiro"

### Gerar Relatório de Despesas

1. Filtre por período (ex: últimos 30 dias)
2. Selecione a categoria (ex: "Alimentação")
3. Clique em "Exportar PDF"
4. Salve o arquivo

## 🚀 Construção de Executável

Para criar um instalador Windows:

```bash
mvn clean package
mvn jpackage:jpackage
```

O executável será gerado em `target/FinanceApp/`

## 📝 Convenções de Código

- **Nomes de Classes**: PascalCase (ex: `DashboardController`)
- **Métodos**: camelCase (ex: `saveTransaction`)
- **Constantes**: UPPER_SNAKE_CASE (ex: `DATABASE_URL`)
- **Atributos Privados**: prefixo `_` (ex: `_conexao`)

## 🤝 Contribuindo

Se deseja contribuir para o projeto:

1. Faça um Fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👨‍💻 Autor

**Anna Alves e Turma**

- Projeto Acadêmico - SENAC
- UC-09: Desenvolvimento de Aplicações Desktop

## 📞 Suporte

Para dúvidas ou problemas, entre em contato através de:
- Issues do GitHub
- Email: seu-email@example.com

## 🗂️ Histórico de Versões

### v1.0.0 (Atual)
- ✅ Gerenciamento completo de transações
- ✅ Dashboard interativo
- ✅ Exportação de relatórios
- ✅ Sincronização em nuvem
- ✅ Interface gráfica moderna

### Planejado (v1.1.0)
- ⏳ Gráficos mais detalhados
- ⏳ Metas financeiras
- ⏳ Análise de tendências
- ⏳ Notificações de alerta

---

**Última atualização**: Fevereiro de 2026
