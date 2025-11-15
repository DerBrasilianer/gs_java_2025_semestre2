# LTAKN Care - Sistema de Gestão de Bem-Estar Corporativo

**Grupo:** LTAKN
- Enzo Prado Soddano — RM557937
- Lucas Resende Lima — RM556564
- Vinicius Prates Altafini — RM556183

---

## 📌 Descrição do Projeto

O **LTAKN Care** é uma solução inovadora que utiliza tecnologia para proteger a saúde mental e física dos colaboradores. O sistema monitora indicadores de jornada e gera sinais proativos para reduzir estresse e prevenir burnout, promovendo um ambiente de trabalho mais saudável e sustentável.

### 🎯 Problema Solucionado
- **Sobrecarga de trabalho** não detectada precocemente
- **Falta de indicadores proativos** sobre bem-estar da equipe
- **Gestão reativa** em vez de preventiva de saúde ocupacional
- **Dificuldade em identificar padrões** de risco de burnout

---

## 🚀 Deploy / Entrega

- 🌐 **Aplicação Live:**  
  https://gs-java-2025-semestre2.onrender.com

---

## 🛠️ Arquitetura e Tecnologias

### Backend
- **Java 17** + **Spring Boot 3.3.4**
- **Spring Security** com autenticação
- **Spring Data JPA** + **Hibernate**
- **Spring MVC** + **Thymeleaf** (templates)
- **Spring Cache** + **Caffeine**
- **Spring AMQP** + **RabbitMQ**
- **Spring AI** + **Groq API**

### Banco de Dados
- **Azure SQL Server** (Produção e Testes)

### Mensageria & Cache
- **RabbitMQ** (CloudAMQP) - Processamento assíncrono
- **Caffeine Cache** - Otimização de performance

### IA Generativa
- **Groq AI** - Análise de bem-estar e recomendações

### Deploy
- **Render.com** - Plataforma cloud
- **Docker** - Containerização

---

## ⚙️ Funcionalidades Principais

### 👥 Gestão de Usuários
- **Sistema de autenticação** com roles (USER/ADMIN)
- **Cadastro seguro** com password encoding
- **Controle de acesso** baseado em permissões

### 🏢 Gestão de Departamentos
- **CRUD completo** de departamentos
- **Configuração de limites** de horas máximas
- **Cache inteligente** para otimização

### 👨‍💼 Gestão de Funcionários
- **Monitoramento automático** de carga horária
- **Cálculo inteligente** de status (Saudável/Em Risco)
- **Paginação** e busca avançada
- **Análise de bem-estar** com IA

### 🤖 Inteligência Artificial
- **Análise individual** de bem-estar por funcionário
- **Relatórios de equipe** com recomendações
- **Sugestões proativas** para gestores
- **Integração Groq API** (LLaMA 3.1)

### 📊 Relatórios Assíncronos
- **Geração assíncrona** via RabbitMQ
- **Processamento em background** de relatórios
- **Notificações por email** simuladas

### 🌐 Internacionalização
- **Suporte PT-BR/EN** completo
- **Troca dinâmica** de idioma
- **Mensagens localizadas**

---

## 🔐 Sistema de Segurança

### Autenticação & Autorização
```java
ROLE_USER
ROLE_ADMIN;
```

### Endpoints Protegidos
- `/departamentos/**` - Gestão de departamentos
- `/funcionarios/**` - Gestão de funcionários
- `/api/**` - API REST protegida
- `/relatorios/**` - Geração de relatórios

### Endpoints Públicos
- `/`, `/home` - Página inicial
- `/login` - Autenticação
- `/register` - Cadastro de usuários
- `/css/**, /js/**, /images/**` - Recursos estáticos

---

## 🗃️ Estrutura do Banco de Dados

### Tabela: `GS_TB_DEPARTAMENTO`
```sql
id BIGINT (PK)
nome VARCHAR (NOT NULL)
numero_horas_maximas INT (NOT NULL)
```

### Tabela: `GS_TB_FUNCIONARIO`
```sql
id BIGINT (PK)
nome VARCHAR (NOT NULL)
departamento_id BIGINT (FK)
horas_trabalhadas_ultimo_mes INT
status VARCHAR (SAUDAVEL/EM_RISCO)
```

### Tabela: `GS_TB_USER`
```sql
id BIGINT (PK)
username VARCHAR (UNIQUE, NOT NULL)
password VARCHAR (NOT NULL)
role VARCHAR (USER/ADMIN)
```

---

## ⚙️ Configuração de Variáveis de Ambiente

### 🔑 Variáveis para Render.com
```env
# Banco de Dados (Azure SQL Server)
SPRING_DATASOURCE_URL=jdbc:sqlserver://sqlserver-gsfinal-945.database.windows.net:1433;database=sqldb-gsfinal;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;
SPRING_DATASOURCE_USERNAME=adminGSFINAL
SPRING_DATASOURCE_PASSWORD=GsFinal@2025

# RabbitMQ (CloudAMQP)
SPRING_RABBITMQ_HOST=fly-01.rmq.cloudamqp.com
SPRING_RABBITMQ_USERNAME=ciadsexh
SPRING_RABBITMQ_PASSWORD=nebfh7Q6NYX7hlAnzDcf_zcv1ZKEhcO4
SPRING_RABBITMQ_VIRTUAL_HOST=ciadsexh

# Groq AI
GROQ_API_KEY=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
GROQ_API_URL=https://api.groq.com/openai/v1
```

### 🐳 Dockerfile
```dockerfile
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Argumentos de build
ARG SPRING_DATASOURCE_URL
ARG SPRING_DATASOURCE_USERNAME
ARG SPRING_DATASOURCE_PASSWORD
ARG SPRING_RABBITMQ_HOST
ARG SPRING_RABBITMQ_USERNAME
ARG SPRING_RABBITMQ_PASSWORD
ARG SPRING_RABBITMQ_VIRTUAL_HOST
ARG GROQ_API_KEY
ARG GROQ_API_URL

# ... configurações completas no Dockerfile
```

---

## 📡 Endpoints da API

### 🔐 Autenticação
- `POST /api/register` - Registrar novo usuário
- `POST /login` - Autenticar usuário

### 🏢 Departamentos (REST API)
- `GET /api/departamentos` - Listar todos
- `GET /api/departamentos/{id}` - Buscar por ID
- `POST /api/departamentos` - Criar departamento
- `PUT /api/departamentos/{id}` - Atualizar departamento
- `DELETE /api/departamentos/{id}` - Excluir departamento

### 👥 Funcionários (REST API)
- `GET /api/funcionarios` - Listar com paginação
- `GET /api/funcionarios/{id}` - Buscar por ID
- `POST /api/funcionarios` - Criar funcionário
- `PUT /api/funcionarios/{id}` - Atualizar funcionário
- `DELETE /api/funcionarios/{id}` - Excluir funcionário

### 🤖 IA Generativa
- `GET /api/ai/funcionarios/{id}/analise-bem-estar` - Análise individual
- `GET /api/ai/resumo-equipe` - Relatório da equipe
- `GET /api/ai/departamentos/{id}/recomendacoes` - Recomendações por departamento

### 📊 Cache & Monitoramento
- `GET /api/cache/stats` - Estatísticas de cache
- `GET /api/cache/clear` - Limpar caches

---

## 🌐 Interface Web (Thymeleaf)

### Páginas Principais
- `/` - Dashboard inicial
- `/departamentos` - Gestão de departamentos
- `/funcionarios` - Gestão de funcionários
- `/teste-mensageiria` - Teste de RabbitMQ
- `/relatorios/funcionarios-risco` - Relatórios

### Funcionalidades Web
- **Busca inteligente** em tabelas
- **Paginação** com navegação
- **Modais** para confirmações
- **Formulários validados**
- **Interface responsiva**

---

## 🔄 Sistema de Cache

### Invalidação via Mensageria
- **Sincronização em tempo real** entre instâncias
- **Mensagens RabbitMQ** para invalidar cache
- **Processamento assíncrono** de atualizações

---

## 📨 Sistema de Mensageria (RabbitMQ)

### Fluxos Assíncronos
1. **Solicitação de relatório** → Fila de processamento
2. **Geração em background** → Análise de dados
3. **Notificação por email** → Resultado final

---

## 🧠 Integração com IA Generativa

### Modelos Groq Utilizados
- **LLaMA 3.1 8B Instant** - Análises principais
- **Fallback para mock** - Quando API indisponível

### Análises Geradas
1. **Análise Individual** - Bem-estar por funcionário
2. **Relatório de Equipe** - Visão geral + recomendações
3. **Sugestões Departamentais** - Ações específicas

### Prompt Engineering
```ava
String prompt = """
Analise o bem-estar do funcionário com base nos dados:
- Nome: %s
- Departamento: %s
- Horas trabalhadas: %d
- Limite máximo: %d
- Status: %s

Forneça:
1. Análise de risco
2. Recomendações específicas
3. Sugestões para o gestor
   """;
   ```

---

## 🚀 Como Executar Localmente

### 1. Clone o Repositório
```bash
git clone https://github.com/DerBrasilianer/gs_java_2025_semestre2.git
cd gs_java_2025_semestre2
```

### 2. Configure Variáveis de Ambiente
```bash
# application.properties já configurado para dev
# Apenas ajuste se necessário
```

### 3. Execute com Maven
```bash
mvn spring-boot:run
```

### 4. Acesse a Aplicação
```
http://localhost:8080
```

---

## 🧪 Testes Automatizados

### Suite de Testes Implementada
- **DepartamentoServiceTest** - CRUD de departamentos
- **FuncionarioServiceTest** - Gestão de funcionários + análise de bem-estar
- **UserServiceTest** - Autenticação e usuários
- **GestaoLtaknApplicationTests** - Contexto Spring

### Executar Testes
```
mvn test
```

---

## 📊 Métricas de Qualidade

### Cobertura de Funcionalidades
- ✅ **Spring Annotations** - Configuração completa com DI
- ✅ **Model/DTO** - Entidades e DTOs com validação
- ✅ **JPA Repository** - Spring Data JPA implementado
- ✅ **Bean Validation** - Validações em DTOs e entidades
- ✅ **Caching** - Caffeine com estratégias avançadas
- ✅ **Internationalization** - PT-BR/EN com troca dinâmica
- ✅ **Pagination** - Paginação em funcionários
- ✅ **Spring Security** - Autenticação + autorização
- ✅ **Error Handling** - Tratamento global de exceptions
- ✅ **Messaging** - RabbitMQ com filas assíncronas
- ✅ **Spring AI** - Integração Groq API + análises
- ✅ **Cloud Deploy** - Render.com com Docker
- ✅ **REST API** - Verbos HTTP + status codes adequados

---

## 🎯 Inovação e Impacto

### 💡 Diferenciais Inovadores
1. **Abordagem Proativa** - Prevenção em vez de reação
2. **IA Aplicada** - Análises contextualizadas de bem-estar
3. **Arquitetura Escalável** - Microserviços-ready com mensageria
4. **Experiência Internacional** - Suporte multilíngue nativo

### 🌍 Impacto Social
- **Melhoria da saúde mental** no ambiente corporativo
- **Redução de casos** de burnout e estresse
- **Cultura organizacional** mais saudável
- **Produtividade sustentável** a longo prazo

---

## 👥 Credenciais para Teste

### Usuários de Demonstração
```
👤 Usuário Comum:
Username: user
Password: userpass
Role: USER

👨‍💼 Administrador:
Username: admin  
Password: adminpass
Role: ADMIN
```

### Acesso ao Deploy
- **URL**: https://gs-java-2025-semestre2.onrender.com
- **Login**: Use as credenciais acima
- **Banco**: Azure SQL Server com dados de exemplo

---

## 🎥 Demonstração

### 🎬 Vídeo Demonstrativo
[Link para o vídeo de demonstração das funcionalidades]

---

**LTAKN Care - Tecnologia para cuidar das pessoas** ♥️
