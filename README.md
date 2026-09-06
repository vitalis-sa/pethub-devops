# PetHub - API Veterinária 🐾

O **PetHub** é uma plataforma de cuidado veterinário contínuo desenvolvida em **Java 21 + Spring Boot 4.0** que liga tutor, veterinário, clínica e wearable IoT numa jornada única — do monitoramento diário à teleconsulta assistida por IA.

Este repositório contém a **entrega de DevOps Tools & Cloud Computing — Sprint 3**, com a aplicação e o banco de dados totalmente containerizados e deployados na Azure via **ACR + ACI**.

---

## 👥 Equipe

| RM | Nome Completo | Turma |
|:---:|:---|:---:|
| RM561489 | Ana Flávia Camelo | 2TDSPV |
| RM562745 | Gustavo Kenji Terada | 2TDSPV |
| RM566234 | João Guilherme Carvalho Novaes | 2TDSPV |
| RM565154 | Pedro Chasci Puga | 2TDSPV |
| RM561342 | Lucas Figueiredo Vieira | 2TDSPV |

- **Repositório GitHub**: *(preencher após publicar)*
- **Vídeo no YouTube**: *(preencher após gravar)*

---

## 📝 Descrição da Solução

O **PetHub** centraliza o ecossistema de clínicas veterinárias, tutores, pets, agendamentos de consultas e diagnósticos. A API gerencia:

- **Prontuário e Histórico Clínico Unificado**: Pets, tutores (responsáveis) e veterinários com perfis completos.
- **Gestão de Consultas**: Agendamento presencial e teleconsulta com notificação ativa ao tutor.
- **Diagnósticos e IA**: Registro de sintomas com predição de patologias via ML e análise por IA generativa.
- **Medicina Preventiva**: Vacinas, tratamentos e lembretes automáticos de próxima dose.
- **Monitoramento IoT**: Leituras de wearable de hidratação com alerta automático de desidratação.
- **Autenticação JWT**: Login com dois perfis (VETERINÁRIO e RESPONSÁVEL), rotas protegidas, hash BCrypt e controle de posse por recurso.

---

## 🚀 Benefícios para o Negócio

1. **Centralização do Histórico Clínico**: Acesso rápido e unificado aos registros de saúde do pet, evitando perda de informações entre clínicas.
2. **Prevenção Ativa**: Lembretes automáticos de vacinas e consultas reduzem a taxa de pets sem acompanhamento veterinário.
3. **Monitoramento Contínuo**: Alertas automáticos de hidratação crítica permitem intervenção antes de emergências.
4. **Escalabilidade na Nuvem**: Containerização completa (app + banco) permite escalar horizontalmente conforme a demanda de clínicas parceiras.
5. **Segurança**: Autenticação JWT com perfis diferenciados garante que cada usuário acesse apenas os dados pertinentes.

---

## 🏗️ Arquitetura da Solução (Nuvem)

```
┌──────────────────────────────────────────────────────────────────────┐
│                        AZURE CLOUD                                   │
│                                                                      │
│  ┌──────────────────┐    docker push     ┌────────────────────────┐  │
│  │   Desenvolvedor   │ ──────────────────▶│  Azure Container       │  │
│  │  (Git Bash / CLI) │                    │  Registry (ACR)        │  │
│  └──────────────────┘                    │  acrrm566234challenge  │  │
│                                          │  ┌─────────┐ ┌───────┐ │  │
│                                          │  │ app:v1  │ │ db:v1 │ │  │
│                                          │  └─────────┘ └───────┘ │  │
│                                          └────────────────────────┘  │
│                                             │ pull          │ pull   │
│                                             ▼               ▼        │
│  ┌──────────────────────────┐   ┌──────────────────────────────┐    │
│  │  ACI - App (pethub-app)  │   │  ACI - Banco (pethub-oracle) │    │
│  │  Spring Boot 4.0 / JRE21 │   │  Oracle XE 21c               │    │
│  │  USER: springuser (1001) │   │  USER: oracle (54321)        │    │
│  │  Porta: 8080             │──▶│  Porta: 1521                 │    │
│  │  CPU: 1 | RAM: 1.5 GB   │   │  CPU: 2 | RAM: 4 GB          │    │
│  └──────────────────────────┘   └──────────┬───────────────────┘    │
│                                             │ mount                  │
│                                    ┌────────▼─────────┐             │
│                                    │ Storage Account   │             │
│                                    │ File Share:oradata │             │
│                                    │ (persistência)    │             │
│                                    └──────────────────┘             │
└──────────────────────────────────────────────────────────────────────┘
```

> Dois ACIs distintos conversando entre si via FQDN público. O File Share persiste os dados do Oracle entre reinicializações.

---

## 💻 How-to: Deploy Completo (Passo a Passo)

### Pré-requisitos

- [Git](https://git-scm.com/)
- [Docker](https://docs.docker.com/get-docker/) e Docker Compose
- [Azure CLI](https://learn.microsoft.com/pt-br/cli/azure/install-azure-cli) (`az login` já autenticado)
- Subscription ativa na Azure (Azure for Students ou similar)

### 1. Clone o repositório

```bash
git clone https://github.com/vitalis-sa/pethub-devops.git
cd pethub-devops
```

### 2. Configure as credenciais

```bash
cp .env.example .env
# Edite o .env com suas senhas reais:
nano .env
```

### 3. Teste local (opcional)

```bash
docker compose up -d --build
docker compose logs -f
# Aguarde "Started PethubApplication" nos logs
# Acesse: http://localhost:8080/swagger-ui.html
```

### 4. Deploy na Azure (ACR + ACI)

Execute os scripts na ordem. Execute "az login" caso necessário. Cada um mostra o próximo passo ao terminar:

```bash
# 1. Criar Resource Group + Azure Container Registry
bash ./azure/01-acr.sh

# 2. Build local das imagens + push para o ACR
bash ./azure/02-build-push.sh

# 3. Criar Conta de Armazenamento + File Share (persistência)
bash ./azure/03-storage.sh

# 4. Deploy do ACI do banco (Oracle XE)
# Primeira subida leva 10-30 min (extração do Oracle XE)
bash ./azure/04-aci-db.sh

# 5. Deploy do ACI do app (Spring Boot)
bash ./azure/05-aci-app.sh

# 6. Verificar status de todos os recursos
bash ./azure/06-status.sh
```

### 5. Acesse a API na nuvem

Após o passo 5, o script exibirá a URL:

```
http://rm566234-pethub-app.canadacentral.azurecontainer.io:8080/swagger-ui.html
```

### 6. Limpeza (após gravar o vídeo)

```bash
bash ./azure/99-cleanup.sh
```

---

## 🛠️ Artefatos DevOps Entregues

| Artefato | Descrição |
|---|---|
| [`Dockerfile`](./Dockerfile) | Imagem do app. Multi-stage build (Maven → JRE Alpine). **Usuário não-root** (`springuser`, uid 1001). |
| [`database/Dockerfile`](./database/Dockerfile) | Imagem do banco. Oracle XE 21c com DDL e seed embutidos. Usuário `oracle` (uid 54321). |
| [`docker-compose.yml`](./docker-compose.yml) | Orquestração local: app + banco com healthcheck e volume persistente. |
| [`script_bd.sql`](./script_bd.sql) | DDL completo das 13 tabelas (requisito da rubrica). |
| [`azure/00-vars.sh`](./azure/00-vars.sh) | Variáveis centralizadas (nomes de recursos, sem segredos). |
| [`azure/01-acr.sh`](./azure/01-acr.sh) | Cria Resource Group e ACR via Azure CLI. |
| [`azure/02-build-push.sh`](./azure/02-build-push.sh) | Build das 2 imagens Docker + push para o ACR. |
| [`azure/03-storage.sh`](./azure/03-storage.sh) | Cria Storage Account + File Share para persistência do Oracle. |
| [`azure/04-aci-db.sh`](./azure/04-aci-db.sh) | Deploy do ACI do banco com monitoramento da inicialização. |
| [`azure/05-aci-app.sh`](./azure/05-aci-app.sh) | Deploy do ACI do app, conectando ao FQDN do banco. |
| [`azure/06-status.sh`](./azure/06-status.sh) | Resumo de todos os recursos provisionados. |
| [`azure/99-cleanup.sh`](./azure/99-cleanup.sh) | Destrói todo o Resource Group (pós-vídeo). |
| [`.env.example`](./.env.example) | Exemplo de configuração. **Nenhuma credencial real no código.** |

---

## 🐳 Comandos Docker Utilizados (Requisito 8.4)

Os scripts Bash automatizam o processo, mas os comandos exatos utilizados nos bastidores para compilar, testar e subir as imagens são:

**Build das imagens:**
```bash
docker build -t rm566234-pethub-app:v1 .
docker build -t rm566234-pethub-oracle:v1 ./database
```

**Execução local (Docker Compose):**
```bash
docker compose up -d --build
docker compose down -v
```

**Tag e Push para o Azure Container Registry:**
```bash
docker tag rm566234-pethub-app:v1 acrrm566234challenge.azurecr.io/rm566234-pethub-app:v1
docker push acrrm566234challenge.azurecr.io/rm566234-pethub-app:v1
```

---

## 🗺️ Rotas da API (CRUD)

O sistema suporta CRUD completo sobre **todas as 13 entidades**. As duas tabelas principais para demonstração no vídeo são **Pets** e **Consultas** (relacionadas entre si):

### 🐾 Pets — `/api/pets`

| Método | Path | Descrição |
|---|---|---|
| GET | `/api/pets` | Listar pets (paginado) |
| GET | `/api/pets/{id}` | Buscar pet por ID |
| POST | `/api/pets` | Cadastrar pet |
| PUT | `/api/pets/{id}` | Atualizar pet |
| DELETE | `/api/pets/{id}` | Remover pet |

### 📅 Consultas — `/api/consultas`

| Método | Path | Descrição |
|---|---|---|
| GET | `/api/consultas` | Listar consultas (paginado) |
| GET | `/api/consultas/{id}` | Buscar consulta por ID |
| POST | `/api/consultas` | Criar consulta |
| PUT | `/api/consultas/{id}` | Atualizar consulta |
| DELETE | `/api/consultas/{id}` | Remover consulta |

> Para o schema completo de cada request/response, acesse o **Swagger UI** da aplicação em execução.

---

## 🔐 Segurança

- **Nenhum dado sensível** (senha, token, chave) está exposto no código-fonte.
- Todas as credenciais são injetadas via variáveis de ambiente (`.env` → ignorado pelo Git).
- Os templates YAML do ACI usam `secureValue` para senhas — não aparecem em `az container show`.
- O container do app roda como `springuser` (uid 1001), **sem privilégio administrativo**.

---

## Como testar as operações CRUD na database 

1. **Clone do Repositório e Deploy**: 
   - Certifique-se de que os recursos na Azure foram criados corretamente executando os scripts `01` a `05` disponíveis no diretório `azure/`.
2. **Conexão ao Banco na Nuvem (via CLI)**: 
   - Para verificar o estado do banco diretamente na nuvem, você pode acessar o `sqlplus` por dentro do container ACI do Oracle usando o seguinte comando:
   ```bash
   az container exec --resource-group rg-rm566234-challenge-sprint3 --name rm566234-aci-db --exec-command "bash -c 'sqlplus pethub/SUA_SENHA_AQUI@//localhost:1521/XEPDB1'"
   ```
   *(Substitua `SUA_SENHA_AQUI` pela senha configurada no arquivo `.env`)*

3. **Preparação para Visualização (no SQLPlus)**: 
   - Para evitar que as tabelas fiquem desconfiguradas no terminal, execute as formatações básicas a seguir antes das consultas:
   ```sql
   SET LINESIZE 200;
   SET PAGESIZE 100;
   COLUMN NOME FORMAT A15;
   COLUMN RACA FORMAT A15;
   COLUMN MOTIVO FORMAT A20;
   COLUMN TIPO_CONSULTA FORMAT A15;
   ```

4. **Teste de Leitura Inicial (GET)**:
   - Gere o token JWT através da rota de Login no Swagger e realize um `GET /api/pets`.
   - No SQLPlus, confirme os dados retornados executando: `SELECT ID_PET, NOME, RACA FROM TB_PET;`.

5. **Teste de Inclusão (POST)**:
   - Envie um `POST /api/pets` no Swagger com os dados de um novo pet.
   - Valide no SQLPlus rodando novamente: `SELECT ID_PET, NOME, RACA FROM TB_PET;` para observar o novo registro inserido na tabela.

6. **Teste de Alteração (PUT)**:
   - Envie um `PUT /api/pets/{id}` para alterar informações do pet criado.
   - Confirme a mudança consultando o ID correspondente via SQLPlus.

7. **Teste de Exclusão (DELETE)**:
   - Envie um `DELETE /api/pets/{id}` no Swagger.
   - Ao executar o SELECT no banco, o registro deverá ter sido removido (ou seu status inativado).

8. **Testes Relacionais (Consultas)**:
   - Siga a mesma lógica na tabela `TB_CONSULTA` (vinculada à `TB_PET`).
   - Você pode validá-la com o comando: `SELECT ID_CONSULTA, TIPO_CONSULTA, MOTIVO, DATA_CONSULTA FROM TB_CONSULTA;`.
   
Este fluxo garante a demonstração clara de que a aplicação Containerizada e o Banco de Dados em Nuvem (ambos ACIs) comunicam-se adequadamente dentro da rede virtualizada da Azure.
