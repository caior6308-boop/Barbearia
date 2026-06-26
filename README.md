# Jhon Barbearia

Sistema web completo para gestão de barbearia — agendamento online, autenticação, painel administrativo financeiro e recuperação de senha.

---

## Sumário

- [Visão Geral](#visao-geral)
- [Stack Tecnológica](#stack-tecnologica)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Modelagem do Banco](#modelagem-do-banco)
- [Rotas da Aplicação](#rotas-da-aplicacao)
- [Sistema de Permissões](#sistema-de-permissoes)
- [Como Executar](#como-executar)
- [Variáveis de Ambiente](#variaveis-de-ambiente)
- [Design System](#design-system)

---

## Visão Geral

O **Jhon Barbearia** é uma plataforma web que permite:

- **Clientes** — visualizarem serviços, horários e galeria; realizarem agendamento online com escolha de barbeiro, serviços e horário; gerenciarem perfil e consultarem histórico de agendamentos.
- **Equipe (barbeiros)** — acompanharem a agenda operacional do dia.
- **Administradores/Proprietários** — painel com métricas financeiras (faturamento diário, semanal, mensal), gráficos de serviços mais solicitados e desempenho dos barbeiros, CRUD completo de barbeiros, serviços e permissões de usuários.

---

## Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| **Linguagem** | Java 25 |
| **Framework** | Spring Boot 4.0.5 |
| **Segurança** | Spring Security 6 (BCrypt + roles) |
| **Frontend** | Thymeleaf + Bootstrap 5.3 + CSS3 |
| **Banco** | MySQL 8 (produção) / H2 (testes) |
| **ORM** | Spring Data JPA / Hibernate |
| **Build** | Maven + Wrapper |
| **Email** | JavaMailSender (SMTP Gmail) |
| **Template** | Thymeleaf fragments + layout |

### Dependências principais (`pom.xml`)

- `spring-boot-starter-data-jpa` — persistência
- `spring-boot-starter-security` — autenticação/autorização
- `spring-boot-starter-thymeleaf` — templates server-side
- `spring-boot-starter-webmvc` — MVC
- `thymeleaf-extras-springsecurity6` — integração Thymeleaf + Security
- `mysql-connector-j` — driver MySQL
- `h2` — banco em memória para testes
- `lombok` — redução de boilerplate
- `spring-boot-starter-mail` — envio de emails
- `chart.js` — gráficos no dashboard (CDN)

---

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/jhon/barbearia/
│   │   ├── BarbeariaApplication.java        # Entry point Spring Boot
│   │   ├── config/
│   │   │   └── SecurityConfig.java          # Security filter chain, BCrypt, login form
│   │   ├── controller/
│   │   │   ├── AdminController.java         # Dashboard + CRUD barbeiros/servicos/permissoes
│   │   │   ├── AgendamentoApiController.java# REST: horários disponiveis
│   │   │   ├── AgendamentoController.java   # Agendamento + cancelamento
│   │   │   ├── CadastroController.java     # Cadastro de novos clientes
│   │   │   ├── GestaoController.java        # Agenda operacional do dia
│   │   │   ├── IndexController.java         # Página inicial
│   │   │   ├── LoginController.java         # Tela de login
│   │   │   ├── PerfilController.java        # Edição de perfil
│   │   │   └── RecuperacaoController.java   # Recuperação de senha por email
│   │   ├── domain/
│   │   │   ├── Agendamento.java            # Agendamento (N:N Servicos)
│   │   │   ├── Barbeiro.java              # Barbeiro
│   │   │   ├── BarbeiroEscala.java        # Escala de horarios por barbeiro
│   │   │   ├── Cliente.java               # Cliente/Usuario (com papel)
│   │   │   └── Servico.java               # Serviço com preço
│   │   ├── repository/
│   │   │   ├── AgendamentoRepository.java  # Queries complexas (faturamento, ranking)
│   │   │   ├── BarbeiroEscalaRepository.java
│   │   │   ├── BarbeiroRepository.java
│   │   │   ├── ClienteRepository.java
│   │   │   └── ServicoRepository.java
│   │   └── service/
│   │       ├── AgendamentoService.java
│   │       ├── AgendamentoServiceImpl.java # Validações, conflito de horário
│   │       ├── ClienteService.java
│   │       ├── ClienteServiceImpl.java     # Cadastro, senha, normalização email
│   │       ├── ClienteUserDetailsService.java # UserDetailsService custom
│   │       ├── DashboardService.java
│   │       ├── DashboardServiceImpl.java   # Métricas financeiras
│   │       ├── DashboardResumo.java        # Record de métricas
│   │       ├── IndicadorFinanceiro.java    # Record comissão
│   │       └── IndicadorRanking.java       # Record ranking
│   └── resources/
│       ├── static/
│       │   ├── css/style.css               # Design system + componentes
│       │   ├── js/script.js                # Toast, mascara telefone, confirmações
│       │   └── image/                      # Imagens do sistema
│       ├── templates/
│       │   ├── Index.html                  # Landing page
│       │   ├── login.html                  # Login / Cadastro / Recuperar senha
│       │   ├── agendamento.html            # Tela de agendamento
│       │   ├── perfil.html                 # Edição de perfil
│       │   ├── meus-agendamentos.html      # Histórico do cliente
│       │   ├── fragments.html              # Navbar + Footer (Thymeleaf fragments)
│       │   ├── gestao/agenda.html          # Agenda do dia (barbeiros)
│       │   └── admin/dashboard.html        # Dashboard administrativo
│       └── application.properties          # Configurações + env vars
└── test/
    └── java/com/jhon/barbearia/
        └── BarbeariaApplicationTests.java  # Teste de contexto Spring Boot
```

---

## Modelagem do Banco

### Entidades

```
CLIENTES
├── id (PK, auto increment)
├── nome (varchar 100, not null)
├── email (varchar 150, unique, not null)
├── telefone (varchar 20)
├── senha (varchar, BCrypt)
├── papel (varchar 20): CLIENTE | ADMIN | PROPRIETARIO | BARBEIRO
├── ativo (boolean, default true)
└── criado_em (datetime)

BARBEIROS
├── id (PK, auto increment)
├── nome (varchar 100, not null)
├── especialidade (varchar 100)
└── foto (varchar 200)

SERVICOS
├── id (PK, auto increment)
├── nome (varchar 100, not null)
└── preco (decimal, not null)

AGENDAMENTOS
├── id (PK, auto increment)
├── cliente_id (FK → clientes.id)
├── barbeiro_id (FK → barbeiros.id)
├── data_hora (datetime, not null)
├── status (varchar 20): Agendado | Cancelado
├── criado_em (datetime, @PrePersist)
└── UNIQUE(barbeiro_id, data_hora) — evita dupla reserva

AGENDAMENTO_SERVICOS (N:N)
├── agendamento_id (FK → agendamentos.id)
└── servico_id (FK → servicos.id)

BARBEIRO_ESCALAS
├── id (PK, auto increment)
├── barbeiro_id (FK → barbeiros.id)
├── dia_semana (varchar 20)
├── hora_inicio (time)
└── hora_fim (time)
```

### Regras de negócio no banco

- `unique constraint` em `agendamentos(barbeiro_id, data_hora)` — impede agendamento duplicado no mesmo horário para o mesmo barbeiro.
- `ddl-auto=update` — o Hibernate cria/altera as tabelas automaticamente com base nas entidades.

---

## Rotas da Aplicação

### Públicas

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/` | Landing page com serviços, horários, galeria |
| `GET` | `/login` | Tela de login |
| `GET` | `/cadastro` | Abre a aba de cadastro na tela de login |
| `GET` | `/api/agendamentos/horarios-disponiveis` | API REST que retorna horários livres para um barbeiro + data |

### Autenticadas (cliente logado)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/agendamento?barbeiroId=X` | Tela de agendamento com serviços e horários |
| `POST` | `/agendamento/salvar` | Confirma o agendamento |
| `GET` | `/meus-agendamentos` | Histórico do cliente |
| `POST` | `/meus-agendamentos/cancelar` | Cancela um agendamento |
| `GET` | `/perfil` | Visualiza/edita dados pessoais |
| `POST` | `/perfil/atualizar` | Salva alterações do perfil |

### Gestão (barbeiros, admin, proprietário)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/gestao/agenda` | Agenda operacional do dia (filtro por data) |

### Administrativo (admin, proprietário)

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/admin` | Dashboard com métricas + CRUDs |
| `POST` | `/admin/barbeiros/salvar` | Cria/edita barbeiro |
| `POST` | `/admin/barbeiros/excluir` | Exclui barbeiro (com proteção de integridade) |
| `POST` | `/admin/servicos/salvar` | Cria/edita serviço |
| `POST` | `/admin/servicos/excluir` | Exclui serviço |
| `POST` | `/admin/clientes/permissao` | Altera papel e status de um usuário |

### Autenticação

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/login` | Login processing (Spring Security) |
| `POST` | `/sair` | Logout |
| `POST` | `/recuperar-senha` | Envia senha temporária por email |

---

## Sistema de Permissões

### Papéis (roles)

| Papel | Acesso |
|---|---|
| `CLIENTE` | Agendamento, perfil, histórico próprio |
| `BARBEIRO` | Tudo de CLIENTE + agenda operacional do dia |
| `ADMIN` | Tudo de BARBEIRO + dashboard + CRUD completo |
| `PROPRIETARIO` | Mesmo que ADMIN (papel distinto para diferenciação futura) |

### Regras no SecurityConfig

```
/**, /login, /cadastro, /recuperar-senha,
/api/agendamentos/horarios-disponiveis,
/css/**, /js/**, /image/**          →  PERMIT ALL

/admin/**                           →  ADMIN, PROPRIETARIO
/gestao/**                          →  ADMIN, PROPRIETARIO, BARBEIRO
/agendamento/**, /meus-agendamentos/**,
/perfil/**                          →  AUTENTICADO
```

### Primeiro usuário

O primeiro cadastro recebe automaticamente o papel `ADMIN`. Os demais cadastros são criados como `CLIENTE`.

---

## Como Executar

### Pré-requisitos

- Java 25+ (JDK)
- MySQL 8+ rodando
- Maven (ou usar o wrapper `mvnw` incluso)

### Passos

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd barbearia

# 2. Crie o banco MySQL
mysql -u root -e "CREATE DATABASE barbearia CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. Configure as variáveis de ambiente (ou edite application.properties)
export DB_URL=jdbc:mysql://localhost:3306/barbearia?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Fortaleza
export DB_USERNAME=root
export DB_PASSWORD=sua-senha

# 4. Compile e execute
./mvnw spring-boot:run

# 5. Acesse
open http://localhost:8080
```

### Build para produção

```bash
./mvnw package -DskipTests
java -jar target/barbearia-0.0.1-SNAPSHOT.jar
```

---

## Variáveis de Ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/barbearia?...` | URL de conexão MySQL |
| `DB_USERNAME` | `root` | Usuário do banco |
| `DB_PASSWORD` | *(vazio)* | Senha do banco |
| `DB_DRIVER` | `com.mysql.cj.jdbc.Driver` | Driver JDBC |
| `JPA_SHOW_SQL` | `false` | Exibir SQL no console |
| `MAIL_HOST` | `smtp.gmail.com` | Servidor SMTP |
| `MAIL_PORT` | `587` | Porta SMTP |
| `MAIL_USERNAME` | *(vazio)* | Email remetente |
| `MAIL_PASSWORD` | *(vazio)* | Senha do email (app password) |

> **Segurança:** Nenhuma senha está hardcoded no código-fonte. Todas são injetadas via variáveis de ambiente com fallback seguro.

---

## Design System

### Paleta de Cores

```css
--gold:        #c9a04e    /* dourado premium - cor principal */
--gold-light:  #dbb45c    /* hover de botões */
--gold-dark:   #a8833a    /* active de botões */

--bg:          #faf9f7    /* fundo principal - off-white */
--bg-card:     #ffffff     /* superfície de cards */
--bg-dark:     #0f0f1a    /* navbar e footer - deep navy */
--text:        #1a1a2e    /* texto principal */
```

### Tipografia

- **Interface:** Inter (sans-serif) — legibilidade e modernidade
- **Títulos de destaque:** Playfair Display (serif) — elegância e personalidade

### Componentes

- **Navbar:** glassmorphism com `backdrop-filter: blur(16px)`
- **Cards:** bordas sutis, sombras progressivas (`sm → md → lg` no hover)
- **Botões:** `border-width: 2px`, `translateY(-1px)` no hover, `box-shadow` glow no gold
- **Formulários:** `focus-ring` de 4px com a cor gold
- **Tabelas:** cabeçalho uppercase com tracking, hover com gold suave
- **Scrollbar customizada** — escura e fina (8px)

### Responsividade

Breakpoints:
- **768px:** navbar reduz para 56px, hero para 260px, fonte dos títulos reduz
- **576px:** botões e logo da navbar menores

### JavaScript

- **Toast notifications:** sistema dinâmico com slideInRight/slideOutRight, auto-dismiss em 5s
- **Máscara de telefone:** formata automático para `(99) 99999-9999`
- **Confirmação em exclusões:** `confirm()` acionado em todos os botões `btn-outline-danger`
- **Auto-dismiss de alerts:** alerts do Bootstrap fecham automaticamente após 5s
- Tudo encapsulado em IIFE para não poluir o escopo global

---

## Segurança

- **Senhas:** armazenadas com BCrypt (`PasswordEncoder`)
- **CSRF:** ativado (Spring Security default) — todos os formulários POST incluem `_csrf.token`
- **Acesso:** verificação dupla (Spring Security na requisição + verificação de sessão nos controllers)
- **Senha temporária:** numérica de 6 dígitos, enviada por email, deve ser alterada no primeiro acesso
- **CORS:** não configurado (aplicação monolítica — frontend e backend no mesmo domínio)
- **Validações de agendamento:**
  - Horário deve ser futuro
  - Pelo menos 1 serviço deve ser selecionado
  - Não permite conflito de horário com o mesmo barbeiro
  - Cancelamento permitido apenas para agendamentos futuros e do próprio cliente

---

## Licença

Projeto privado — todos os direitos reservados.
