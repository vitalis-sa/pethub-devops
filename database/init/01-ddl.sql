-- =====================================================================
-- PetHub - DDL das tabelas (Oracle Database XE 21c)
-- Checkpoint FIAP - Imagem e Containers em Nuvem
-- Grupo Vitalis - representante RM566234
--
-- Executado automaticamente na PRIMEIRA subida do container do banco,
-- pelo script /container-entrypoint-initdb.d/00-run-schema.sh, conectado
-- como o usuario da aplicacao dentro do PDB XEPDB1.
--
-- Este script e a FONTE DA VERDADE do schema: a aplicacao sobe com
-- spring.jpa.hibernate.ddl-auto=validate, ou seja, o Spring recusa
-- iniciar se o banco divergir de uma unica coluna deste arquivo.
--
-- 13 tabelas | 13 sequences | 20 chaves estrangeiras
-- =====================================================================

WHENEVER SQLERROR EXIT SQL.SQLCODE
SET SQLBLANKLINES ON
SET DEFINE OFF

-- =====================================================================
-- SEQUENCES (geracao de PKs - GenerationType.SEQUENCE, allocationSize=1)
-- =====================================================================
CREATE SEQUENCE sq_responsavel           START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_responsavel_endereco  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_responsavel_contato   START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_unidade_veterinario   START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_veterinario           START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_pet                   START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_consulta              START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_diagnostico           START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_exame                 START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_vacina_tratamento     START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_pedido_medico         START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_leitura_wearable      START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE sq_lembrete              START WITH 1 INCREMENT BY 1;


-- =====================================================================
-- 1) TB_RESPONSAVEL - tutor / dono do pet
-- =====================================================================
CREATE TABLE tb_responsavel (
    id          NUMBER(19,0)       NOT NULL,
    nome        VARCHAR2(150 CHAR) NOT NULL,
    cpf         VARCHAR2(11 CHAR)  NOT NULL,
    email       VARCHAR2(255 CHAR) NOT NULL,
    senha       VARCHAR2(255 CHAR) NOT NULL,
    ativo       NUMBER(1,0),
    created_at  TIMESTAMP(9),
    CONSTRAINT pk_responsavel       PRIMARY KEY (id),
    CONSTRAINT uk_responsavel_cpf   UNIQUE (cpf),
    CONSTRAINT ck_responsavel_ativo CHECK (ativo IN (0,1))
);

-- =====================================================================
-- 2) TB_RESPONSAVEL_ENDERECO - enderecos do responsavel (1:N)
-- =====================================================================
CREATE TABLE tb_responsavel_endereco (
    id             NUMBER(19,0)       NOT NULL,
    responsavel_id NUMBER(19,0)       NOT NULL,
    logradouro     VARCHAR2(255 CHAR) NOT NULL,
    numero         VARCHAR2(255 CHAR) NOT NULL,
    complemento    VARCHAR2(255 CHAR),
    bairro         VARCHAR2(255 CHAR) NOT NULL,
    cidade         VARCHAR2(255 CHAR) NOT NULL,
    estado         VARCHAR2(2 CHAR)   NOT NULL,
    cep            VARCHAR2(8 CHAR)   NOT NULL,
    principal      NUMBER(1,0),
    CONSTRAINT pk_responsavel_endereco PRIMARY KEY (id),
    CONSTRAINT ck_resp_end_principal   CHECK (principal IN (0,1))
);

-- =====================================================================
-- 3) TB_RESPONSAVEL_CONTATO - telefones do responsavel (1:N)
-- =====================================================================
CREATE TABLE tb_responsavel_contato (
    id             NUMBER(19,0)       NOT NULL,
    responsavel_id NUMBER(19,0)       NOT NULL,
    tipo           VARCHAR2(255 CHAR) NOT NULL,
    telefone       VARCHAR2(255 CHAR) NOT NULL,
    principal      NUMBER(1,0),
    CONSTRAINT pk_responsavel_contato PRIMARY KEY (id),
    CONSTRAINT ck_resp_cont_principal CHECK (principal IN (0,1))
);

-- =====================================================================
-- 4) TB_UNIDADE_VETERINARIO - clinicas / estabelecimentos
-- =====================================================================
CREATE TABLE tb_unidade_veterinario (
    id         NUMBER(19,0)       NOT NULL,
    nome       VARCHAR2(255 CHAR) NOT NULL,
    logradouro VARCHAR2(255 CHAR),
    numero     VARCHAR2(255 CHAR),
    bairro     VARCHAR2(255 CHAR),
    cidade     VARCHAR2(255 CHAR),
    estado     VARCHAR2(2 CHAR),
    cep        VARCHAR2(8 CHAR),
    CONSTRAINT pk_unidade_veterinario PRIMARY KEY (id)
);

-- =====================================================================
-- 5) TB_VETERINARIO - profissionais
-- =====================================================================
CREATE TABLE tb_veterinario (
    id            NUMBER(19,0)       NOT NULL,
    nome          VARCHAR2(255 CHAR) NOT NULL,
    crmv          VARCHAR2(255 CHAR) NOT NULL,
    email         VARCHAR2(255 CHAR) NOT NULL,
    senha         VARCHAR2(255 CHAR) NOT NULL,
    telefone      VARCHAR2(255 CHAR),
    especialidade VARCHAR2(255 CHAR),
    ativo         NUMBER(1,0)        NOT NULL,
    unidade_id    NUMBER(19,0),
    CONSTRAINT pk_veterinario       PRIMARY KEY (id),
    CONSTRAINT uk_veterinario_crmv  UNIQUE (crmv),
    CONSTRAINT uk_veterinario_email UNIQUE (email),
    CONSTRAINT ck_veterinario_ativo CHECK (ativo IN (0,1))
);

-- =====================================================================
-- 6) TB_PET - animais atendidos
-- =====================================================================
CREATE TABLE tb_pet (
    id                         NUMBER(19,0)       NOT NULL,
    nome                       VARCHAR2(255 CHAR) NOT NULL,
    especie                    VARCHAR2(255 CHAR) NOT NULL,
    raca                       VARCHAR2(255 CHAR),
    idade                      NUMBER(10,0),
    peso                       BINARY_DOUBLE,
    genero                     VARCHAR2(255 CHAR),
    responsavel_id             NUMBER(19,0)       NOT NULL,
    veterinario_responsavel_id NUMBER(19,0),
    CONSTRAINT pk_pet PRIMARY KEY (id)
);

-- =====================================================================
-- 7) TB_CONSULTA - agendamentos / atendimentos
-- =====================================================================
CREATE TABLE tb_consulta (
    id             NUMBER(19,0)       NOT NULL,
    pet_id         NUMBER(19,0)       NOT NULL,
    veterinario_id NUMBER(19,0)       NOT NULL,
    unidade_id     NUMBER(19,0),
    data_hora      TIMESTAMP(9)       NOT NULL,
    tipo           VARCHAR2(255 CHAR) NOT NULL,
    status         VARCHAR2(255 CHAR) NOT NULL,
    observacoes    VARCHAR2(255 CHAR),
    CONSTRAINT pk_consulta        PRIMARY KEY (id),
    CONSTRAINT ck_consulta_tipo   CHECK (tipo   IN ('PRESENCIAL','TELECONSULTA')),
    CONSTRAINT ck_consulta_status CHECK (status IN ('AGENDADA','REALIZADA','CANCELADA'))
);

-- =====================================================================
-- 8) TB_DIAGNOSTICO - sintomas + predicao de ML / analise GenAI
-- =====================================================================
CREATE TABLE tb_diagnostico (
    id                       NUMBER(19,0)        NOT NULL,
    consulta_id              NUMBER(19,0)        NOT NULL,
    pet_id                   NUMBER(19,0)        NOT NULL,
    data                     TIMESTAMP(9)        NOT NULL,
    sintoma1                 VARCHAR2(255 CHAR),
    sintoma2                 VARCHAR2(255 CHAR),
    sintoma3                 VARCHAR2(255 CHAR),
    sintoma4                 VARCHAR2(255 CHAR),
    duracao_sintomas         VARCHAR2(255 CHAR),
    perda_apetite            NUMBER(1,0)         NOT NULL,
    vomito                   NUMBER(1,0)         NOT NULL,
    diarreia                 NUMBER(1,0)         NOT NULL,
    tosse                    NUMBER(1,0)         NOT NULL,
    dificuldade_respiratoria NUMBER(1,0)         NOT NULL,
    secrecao_nasal           NUMBER(1,0)         NOT NULL,
    secrecao_ocular          NUMBER(1,0)         NOT NULL,
    lesoes_pele              NUMBER(1,0)         NOT NULL,
    claudicacao              NUMBER(1,0)         NOT NULL,
    temperatura_corporal     BINARY_DOUBLE,
    frequencia_cardiaca      NUMBER(10,0),
    doenca_predita           VARCHAR2(255 CHAR),
    confianca_predicao       BINARY_DOUBLE,
    analise_gen_ai           VARCHAR2(2000 CHAR),
    CONSTRAINT pk_diagnostico        PRIMARY KEY (id),
    CONSTRAINT ck_diag_perda_apetite CHECK (perda_apetite            IN (0,1)),
    CONSTRAINT ck_diag_vomito        CHECK (vomito                   IN (0,1)),
    CONSTRAINT ck_diag_diarreia      CHECK (diarreia                 IN (0,1)),
    CONSTRAINT ck_diag_tosse         CHECK (tosse                    IN (0,1)),
    CONSTRAINT ck_diag_dif_resp      CHECK (dificuldade_respiratoria IN (0,1)),
    CONSTRAINT ck_diag_sec_nasal     CHECK (secrecao_nasal           IN (0,1)),
    CONSTRAINT ck_diag_sec_ocular    CHECK (secrecao_ocular          IN (0,1)),
    CONSTRAINT ck_diag_lesoes_pele   CHECK (lesoes_pele              IN (0,1)),
    CONSTRAINT ck_diag_claudicacao   CHECK (claudicacao              IN (0,1))
);

-- =====================================================================
-- 9) TB_EXAME - exames clinicos realizados
-- =====================================================================
CREATE TABLE tb_exame (
    id                NUMBER(19,0)       NOT NULL,
    consulta_id       NUMBER(19,0)       NOT NULL,
    pet_id            NUMBER(19,0)       NOT NULL,
    tipo              VARCHAR2(255 CHAR) NOT NULL,
    data              DATE               NOT NULL,
    resultado         VARCHAR2(255 CHAR),
    arquivo_resultado VARCHAR2(255 CHAR),
    CONSTRAINT pk_exame PRIMARY KEY (id)
);

-- =====================================================================
-- 10) TB_VACINA_TRATAMENTO - vacinas, medicamentos e procedimentos
-- =====================================================================
CREATE TABLE tb_vacina_tratamento (
    id             NUMBER(19,0)       NOT NULL,
    pet_id         NUMBER(19,0)       NOT NULL,
    veterinario_id NUMBER(19,0)       NOT NULL,
    consulta_id    NUMBER(19,0),
    tipo           VARCHAR2(255 CHAR) NOT NULL,
    nome           VARCHAR2(255 CHAR) NOT NULL,
    dose           VARCHAR2(255 CHAR),
    data_aplicacao DATE               NOT NULL,
    proxima_dose   DATE,
    observacoes    VARCHAR2(255 CHAR),
    CONSTRAINT pk_vacina_tratamento PRIMARY KEY (id),
    CONSTRAINT ck_vactrat_tipo      CHECK (tipo IN ('VACINA','MEDICAMENTO','PROCEDIMENTO'))
);

-- =====================================================================
-- 11) TB_PEDIDO_MEDICO - exames solicitados / medicacoes para casa
-- =====================================================================
CREATE TABLE tb_pedido_medico (
    id          NUMBER(19,0)       NOT NULL,
    consulta_id NUMBER(19,0)       NOT NULL,
    pet_id      NUMBER(19,0)       NOT NULL,
    tipo        VARCHAR2(255 CHAR) NOT NULL,
    descricao   VARCHAR2(255 CHAR) NOT NULL,
    instrucoes  VARCHAR2(255 CHAR),
    status      VARCHAR2(255 CHAR) NOT NULL,
    data_limite DATE,
    created_at  TIMESTAMP(9)       NOT NULL,
    CONSTRAINT pk_pedido_medico PRIMARY KEY (id),
    CONSTRAINT ck_pedmed_tipo   CHECK (tipo   IN ('EXAME','MEDICAMENTO')),
    CONSTRAINT ck_pedmed_status CHECK (status IN ('PENDENTE','CONCLUIDO','CANCELADO'))
);

-- =====================================================================
-- 12) TB_LEITURA_WEARABLE - telemetria IoT de hidratacao do pet
-- =====================================================================
CREATE TABLE tb_leitura_wearable (
    id                       NUMBER(19,0)       NOT NULL,
    pet_id                   NUMBER(19,0)       NOT NULL,
    timestamp                TIMESTAMP(9)       NOT NULL,
    consumo_ml_registrado    BINARY_DOUBLE      NOT NULL,
    consumo_diario_acumulado BINARY_DOUBLE      NOT NULL,
    meta_diaria_ml           BINARY_DOUBLE      NOT NULL,
    percentual_meta          BINARY_DOUBLE      NOT NULL,
    alerta_gerado            NUMBER(1,0)        NOT NULL,
    tipo_alerta              VARCHAR2(255 CHAR),
    descricao_alerta         VARCHAR2(255 CHAR),
    CONSTRAINT pk_leitura_wearable      PRIMARY KEY (id),
    CONSTRAINT ck_leitura_alerta_gerado CHECK (alerta_gerado IN (0,1)),
    CONSTRAINT ck_leitura_tipo_alerta   CHECK (tipo_alerta IN
        ('BAIXO_CONSUMO','DESIDRATACAO_CRITICA','META_ATINGIDA','CONSUMO_EXCESSIVO'))
);

-- =====================================================================
-- 13) TB_LEMBRETE - notificacoes agendadas para o responsavel
-- =====================================================================
CREATE TABLE tb_lembrete (
    id              NUMBER(19,0)       NOT NULL,
    pet_id          NUMBER(19,0)       NOT NULL,
    responsavel_id  NUMBER(19,0)       NOT NULL,
    tipo            VARCHAR2(255 CHAR) NOT NULL,
    mensagem        VARCHAR2(255 CHAR) NOT NULL,
    data_agendada   DATE,
    status          VARCHAR2(255 CHAR),
    referencia_id   NUMBER(19,0),
    referencia_tipo VARCHAR2(255 CHAR),
    created_at      TIMESTAMP(9),
    CONSTRAINT pk_lembrete        PRIMARY KEY (id),
    CONSTRAINT ck_lembrete_tipo   CHECK (tipo IN
        ('VACINA','CONSULTA','EXAME','MEDICAMENTO','HIDRATACAO')),
    CONSTRAINT ck_lembrete_status CHECK (status IN ('PENDENTE','ENVIADO','FALHOU'))
);


-- =====================================================================
-- CHAVES ESTRANGEIRAS (20)
-- =====================================================================
ALTER TABLE tb_responsavel_endereco ADD CONSTRAINT fk_resp_end_responsavel
    FOREIGN KEY (responsavel_id) REFERENCES tb_responsavel (id);

ALTER TABLE tb_responsavel_contato ADD CONSTRAINT fk_resp_cont_responsavel
    FOREIGN KEY (responsavel_id) REFERENCES tb_responsavel (id);

ALTER TABLE tb_veterinario ADD CONSTRAINT fk_veterinario_unidade
    FOREIGN KEY (unidade_id) REFERENCES tb_unidade_veterinario (id);

ALTER TABLE tb_pet ADD CONSTRAINT fk_pet_responsavel
    FOREIGN KEY (responsavel_id) REFERENCES tb_responsavel (id);

ALTER TABLE tb_pet ADD CONSTRAINT fk_pet_veterinario
    FOREIGN KEY (veterinario_responsavel_id) REFERENCES tb_veterinario (id);

ALTER TABLE tb_consulta ADD CONSTRAINT fk_consulta_pet
    FOREIGN KEY (pet_id) REFERENCES tb_pet (id);

ALTER TABLE tb_consulta ADD CONSTRAINT fk_consulta_veterinario
    FOREIGN KEY (veterinario_id) REFERENCES tb_veterinario (id);

ALTER TABLE tb_consulta ADD CONSTRAINT fk_consulta_unidade
    FOREIGN KEY (unidade_id) REFERENCES tb_unidade_veterinario (id);

ALTER TABLE tb_diagnostico ADD CONSTRAINT fk_diagnostico_consulta
    FOREIGN KEY (consulta_id) REFERENCES tb_consulta (id);

ALTER TABLE tb_diagnostico ADD CONSTRAINT fk_diagnostico_pet
    FOREIGN KEY (pet_id) REFERENCES tb_pet (id);

ALTER TABLE tb_exame ADD CONSTRAINT fk_exame_consulta
    FOREIGN KEY (consulta_id) REFERENCES tb_consulta (id);

ALTER TABLE tb_exame ADD CONSTRAINT fk_exame_pet
    FOREIGN KEY (pet_id) REFERENCES tb_pet (id);

ALTER TABLE tb_vacina_tratamento ADD CONSTRAINT fk_vactrat_pet
    FOREIGN KEY (pet_id) REFERENCES tb_pet (id);

ALTER TABLE tb_vacina_tratamento ADD CONSTRAINT fk_vactrat_veterinario
    FOREIGN KEY (veterinario_id) REFERENCES tb_veterinario (id);

ALTER TABLE tb_vacina_tratamento ADD CONSTRAINT fk_vactrat_consulta
    FOREIGN KEY (consulta_id) REFERENCES tb_consulta (id);

ALTER TABLE tb_pedido_medico ADD CONSTRAINT fk_pedmed_consulta
    FOREIGN KEY (consulta_id) REFERENCES tb_consulta (id);

ALTER TABLE tb_pedido_medico ADD CONSTRAINT fk_pedmed_pet
    FOREIGN KEY (pet_id) REFERENCES tb_pet (id);

ALTER TABLE tb_leitura_wearable ADD CONSTRAINT fk_leitura_pet
    FOREIGN KEY (pet_id) REFERENCES tb_pet (id);

ALTER TABLE tb_lembrete ADD CONSTRAINT fk_lembrete_pet
    FOREIGN KEY (pet_id) REFERENCES tb_pet (id);

ALTER TABLE tb_lembrete ADD CONSTRAINT fk_lembrete_responsavel
    FOREIGN KEY (responsavel_id) REFERENCES tb_responsavel (id);


-- =====================================================================
-- INDICES de apoio as consultas mais frequentes da API
-- =====================================================================
CREATE INDEX ix_pet_responsavel      ON tb_pet (responsavel_id);
CREATE INDEX ix_consulta_pet         ON tb_consulta (pet_id);
CREATE INDEX ix_consulta_status      ON tb_consulta (status);
CREATE INDEX ix_diagnostico_pet      ON tb_diagnostico (pet_id);
CREATE INDEX ix_exame_pet            ON tb_exame (pet_id);
CREATE INDEX ix_vactrat_pet          ON tb_vacina_tratamento (pet_id);
CREATE INDEX ix_pedmed_pet           ON tb_pedido_medico (pet_id);
CREATE INDEX ix_leitura_pet          ON tb_leitura_wearable (pet_id);
CREATE INDEX ix_lembrete_responsavel ON tb_lembrete (responsavel_id);

-- =====================================================================
-- COMENTARIOS DE TABELAS E COLUNAS
--
-- Documentacao do schema dentro do proprio banco: fica consultavel por
-- USER_TAB_COMMENTS e USER_COL_COMMENTS, sem depender deste arquivo.
-- =====================================================================

-- --- Responsavel (tutor do animal) -----------------------------------
COMMENT ON TABLE  tb_responsavel                  IS 'Tutor responsavel pelo animal. Autentica no app mobile com perfil RESPONSAVEL.';
COMMENT ON COLUMN tb_responsavel.id               IS 'Chave primaria. Gerada por sq_responsavel.';
COMMENT ON COLUMN tb_responsavel.nome             IS 'Nome completo do tutor.';
COMMENT ON COLUMN tb_responsavel.cpf              IS 'CPF somente digitos, 11 posicoes. Unico: identifica o tutor no cadastro.';
COMMENT ON COLUMN tb_responsavel.email            IS 'E-mail de login. Unico em todo o sistema, incluindo veterinarios.';
COMMENT ON COLUMN tb_responsavel.senha            IS 'Hash BCrypt da senha. Nunca armazena texto puro.';
COMMENT ON COLUMN tb_responsavel.ativo            IS 'Cadastro ativo: 1 permite login, 0 bloqueia o acesso imediatamente.';
COMMENT ON COLUMN tb_responsavel.created_at       IS 'Data e hora do cadastro.';

COMMENT ON TABLE  tb_responsavel_endereco         IS 'Enderecos do tutor. Um tutor pode ter varios; um deles e o principal.';
COMMENT ON COLUMN tb_responsavel_endereco.id      IS 'Chave primaria. Gerada por sq_responsavel_endereco.';
COMMENT ON COLUMN tb_responsavel_endereco.responsavel_id IS 'FK para tb_responsavel: dono do endereco.';
COMMENT ON COLUMN tb_responsavel_endereco.logradouro     IS 'Nome da rua, avenida ou praca.';
COMMENT ON COLUMN tb_responsavel_endereco.numero         IS 'Numero do imovel.';
COMMENT ON COLUMN tb_responsavel_endereco.complemento    IS 'Apartamento, bloco ou referencia. Opcional.';
COMMENT ON COLUMN tb_responsavel_endereco.bairro         IS 'Bairro.';
COMMENT ON COLUMN tb_responsavel_endereco.cidade         IS 'Municipio.';
COMMENT ON COLUMN tb_responsavel_endereco.estado         IS 'Sigla da unidade federativa, 2 letras.';
COMMENT ON COLUMN tb_responsavel_endereco.cep            IS 'CEP somente digitos, 8 posicoes.';
COMMENT ON COLUMN tb_responsavel_endereco.principal      IS 'Endereco preferencial do tutor: 1 sim, 0 nao.';

COMMENT ON TABLE  tb_responsavel_contato          IS 'Telefones de contato do tutor, usados pelos lembretes.';
COMMENT ON COLUMN tb_responsavel_contato.id       IS 'Chave primaria. Gerada por sq_responsavel_contato.';
COMMENT ON COLUMN tb_responsavel_contato.responsavel_id IS 'FK para tb_responsavel: dono do contato.';
COMMENT ON COLUMN tb_responsavel_contato.tipo     IS 'Natureza do telefone: CELULAR, RESIDENCIAL ou COMERCIAL.';
COMMENT ON COLUMN tb_responsavel_contato.telefone IS 'Numero somente digitos, com DDD.';
COMMENT ON COLUMN tb_responsavel_contato.principal IS 'Contato preferencial do tutor: 1 sim, 0 nao.';

-- --- Clinica e corpo clinico -----------------------------------------
COMMENT ON TABLE  tb_unidade_veterinario          IS 'Unidade fisica da clinica onde as consultas presenciais acontecem.';
COMMENT ON COLUMN tb_unidade_veterinario.id       IS 'Chave primaria. Gerada por sq_unidade_veterinario.';
COMMENT ON COLUMN tb_unidade_veterinario.nome     IS 'Nome comercial da unidade.';
COMMENT ON COLUMN tb_unidade_veterinario.logradouro IS 'Nome da rua ou avenida da unidade.';
COMMENT ON COLUMN tb_unidade_veterinario.numero   IS 'Numero do imovel.';
COMMENT ON COLUMN tb_unidade_veterinario.bairro   IS 'Bairro.';
COMMENT ON COLUMN tb_unidade_veterinario.cidade   IS 'Municipio.';
COMMENT ON COLUMN tb_unidade_veterinario.estado   IS 'Sigla da unidade federativa, 2 letras.';
COMMENT ON COLUMN tb_unidade_veterinario.cep      IS 'CEP somente digitos, 8 posicoes.';

COMMENT ON TABLE  tb_veterinario                  IS 'Veterinario do corpo clinico. Autentica com perfil VETERINARIO e e quem produz o prontuario.';
COMMENT ON COLUMN tb_veterinario.id               IS 'Chave primaria. Gerada por sq_veterinario.';
COMMENT ON COLUMN tb_veterinario.nome             IS 'Nome completo do veterinario.';
COMMENT ON COLUMN tb_veterinario.crmv             IS 'Registro no Conselho Regional de Medicina Veterinaria. Unico.';
COMMENT ON COLUMN tb_veterinario.email            IS 'E-mail de login. Unico em todo o sistema, incluindo tutores.';
COMMENT ON COLUMN tb_veterinario.senha            IS 'Hash BCrypt da senha. Nunca armazena texto puro.';
COMMENT ON COLUMN tb_veterinario.telefone         IS 'Telefone de contato profissional.';
COMMENT ON COLUMN tb_veterinario.especialidade    IS 'Area de atuacao, por exemplo Clinica Geral ou Dermatologia.';
COMMENT ON COLUMN tb_veterinario.ativo            IS 'Vinculo ativo: 1 permite login, 0 desliga o acesso imediatamente.';
COMMENT ON COLUMN tb_veterinario.unidade_id       IS 'FK para tb_unidade_veterinario: unidade de lotacao.';

-- --- Nucleo do dominio: o animal --------------------------------------
COMMENT ON TABLE  tb_pet                          IS 'Animal atendido pela plataforma. Tabela central: todo registro clinico aponta para ela.';
COMMENT ON COLUMN tb_pet.id                       IS 'Chave primaria. Gerada por sq_pet.';
COMMENT ON COLUMN tb_pet.nome                     IS 'Nome do animal.';
COMMENT ON COLUMN tb_pet.especie                  IS 'Especie do animal, por exemplo Felino ou Canino.';
COMMENT ON COLUMN tb_pet.raca                     IS 'Raca do animal. Opcional para animais sem raca definida.';
COMMENT ON COLUMN tb_pet.idade                    IS 'Idade em anos completos.';
COMMENT ON COLUMN tb_pet.peso                     IS 'Peso em quilogramas. Base do calculo da meta diaria de hidratacao.';
COMMENT ON COLUMN tb_pet.genero                   IS 'Sexo do animal: Macho ou Femea.';
COMMENT ON COLUMN tb_pet.responsavel_id           IS 'FK para tb_responsavel: tutor dono do animal. Delimita o que o tutor enxerga na API.';
COMMENT ON COLUMN tb_pet.veterinario_responsavel_id IS 'FK para tb_veterinario: veterinario de referencia do animal. Opcional.';

-- --- Atendimento e prontuario ----------------------------------------
COMMENT ON TABLE  tb_consulta                     IS 'Atendimento veterinario, presencial ou por teleconsulta. Raiz do prontuario daquele encontro.';
COMMENT ON COLUMN tb_consulta.id                  IS 'Chave primaria. Gerada por sq_consulta.';
COMMENT ON COLUMN tb_consulta.pet_id              IS 'FK para tb_pet: animal atendido.';
COMMENT ON COLUMN tb_consulta.veterinario_id      IS 'FK para tb_veterinario: profissional responsavel pelo atendimento.';
COMMENT ON COLUMN tb_consulta.unidade_id          IS 'FK para tb_unidade_veterinario: unidade do atendimento. Nulo em teleconsulta.';
COMMENT ON COLUMN tb_consulta.data_hora           IS 'Data e hora do atendimento.';
COMMENT ON COLUMN tb_consulta.tipo                IS 'Modalidade: PRESENCIAL ou TELECONSULTA.';
COMMENT ON COLUMN tb_consulta.status              IS 'Situacao: AGENDADA, REALIZADA ou CANCELADA.';
COMMENT ON COLUMN tb_consulta.observacoes         IS 'Anotacoes livres do veterinario sobre o atendimento.';

COMMENT ON TABLE  tb_diagnostico                  IS 'Diagnostico da consulta, com os sintomas coletados e a predicao assistida por IA.';
COMMENT ON COLUMN tb_diagnostico.id               IS 'Chave primaria. Gerada por sq_diagnostico.';
COMMENT ON COLUMN tb_diagnostico.consulta_id      IS 'FK para tb_consulta: atendimento que originou o diagnostico.';
COMMENT ON COLUMN tb_diagnostico.pet_id           IS 'FK para tb_pet: animal diagnosticado.';
COMMENT ON COLUMN tb_diagnostico.data             IS 'Data e hora do registro do diagnostico.';
COMMENT ON COLUMN tb_diagnostico.sintoma1         IS 'Sintoma principal relatado.';
COMMENT ON COLUMN tb_diagnostico.sintoma2         IS 'Segundo sintoma relatado.';
COMMENT ON COLUMN tb_diagnostico.sintoma3         IS 'Terceiro sintoma relatado.';
COMMENT ON COLUMN tb_diagnostico.sintoma4         IS 'Quarto sintoma relatado.';
COMMENT ON COLUMN tb_diagnostico.duracao_sintomas IS 'Ha quanto tempo os sintomas se manifestam.';
COMMENT ON COLUMN tb_diagnostico.perda_apetite    IS 'Sinal clinico presente: 1 sim, 0 nao.';
COMMENT ON COLUMN tb_diagnostico.vomito           IS 'Sinal clinico presente: 1 sim, 0 nao.';
COMMENT ON COLUMN tb_diagnostico.diarreia         IS 'Sinal clinico presente: 1 sim, 0 nao.';
COMMENT ON COLUMN tb_diagnostico.tosse            IS 'Sinal clinico presente: 1 sim, 0 nao.';
COMMENT ON COLUMN tb_diagnostico.dificuldade_respiratoria IS 'Sinal clinico presente: 1 sim, 0 nao.';
COMMENT ON COLUMN tb_diagnostico.secrecao_nasal   IS 'Sinal clinico presente: 1 sim, 0 nao.';
COMMENT ON COLUMN tb_diagnostico.secrecao_ocular  IS 'Sinal clinico presente: 1 sim, 0 nao.';
COMMENT ON COLUMN tb_diagnostico.lesoes_pele      IS 'Sinal clinico presente: 1 sim, 0 nao.';
COMMENT ON COLUMN tb_diagnostico.claudicacao      IS 'Manqueira ao andar: 1 sim, 0 nao.';
COMMENT ON COLUMN tb_diagnostico.temperatura_corporal IS 'Temperatura aferida em graus Celsius.';
COMMENT ON COLUMN tb_diagnostico.frequencia_cardiaca  IS 'Batimentos por minuto.';
COMMENT ON COLUMN tb_diagnostico.doenca_predita   IS 'Doenca sugerida pelo modelo de machine learning.';
COMMENT ON COLUMN tb_diagnostico.confianca_predicao   IS 'Confianca da predicao, de 0 a 1.';
COMMENT ON COLUMN tb_diagnostico.analise_gen_ai   IS 'Texto explicativo gerado por IA, confirmado pelo veterinario antes de gravar.';

COMMENT ON TABLE  tb_exame                        IS 'Exame solicitado ou realizado durante a consulta.';
COMMENT ON COLUMN tb_exame.id                     IS 'Chave primaria. Gerada por sq_exame.';
COMMENT ON COLUMN tb_exame.consulta_id            IS 'FK para tb_consulta: atendimento que solicitou o exame.';
COMMENT ON COLUMN tb_exame.pet_id                 IS 'FK para tb_pet: animal examinado.';
COMMENT ON COLUMN tb_exame.tipo                   IS 'Tipo do exame, por exemplo Hemograma ou Raio-X.';
COMMENT ON COLUMN tb_exame.data                   IS 'Data de realizacao.';
COMMENT ON COLUMN tb_exame.resultado              IS 'Laudo ou resumo do resultado.';
COMMENT ON COLUMN tb_exame.arquivo_resultado      IS 'Caminho ou URL do arquivo do laudo.';

COMMENT ON TABLE  tb_vacina_tratamento            IS 'Vacina, medicamento ou procedimento aplicado. Agenda automaticamente o lembrete da proxima dose.';
COMMENT ON COLUMN tb_vacina_tratamento.id         IS 'Chave primaria. Gerada por sq_vacina_tratamento.';
COMMENT ON COLUMN tb_vacina_tratamento.pet_id     IS 'FK para tb_pet: animal que recebeu a aplicacao.';
COMMENT ON COLUMN tb_vacina_tratamento.veterinario_id IS 'FK para tb_veterinario: profissional que aplicou.';
COMMENT ON COLUMN tb_vacina_tratamento.consulta_id IS 'FK para tb_consulta: atendimento em que houve a aplicacao. Opcional.';
COMMENT ON COLUMN tb_vacina_tratamento.tipo       IS 'Natureza do registro: VACINA, MEDICAMENTO ou PROCEDIMENTO.';
COMMENT ON COLUMN tb_vacina_tratamento.nome       IS 'Nome da vacina, do medicamento ou do procedimento.';
COMMENT ON COLUMN tb_vacina_tratamento.dose       IS 'Dose aplicada, por exemplo primeira dose ou reforco.';
COMMENT ON COLUMN tb_vacina_tratamento.data_aplicacao IS 'Data em que foi aplicada.';
COMMENT ON COLUMN tb_vacina_tratamento.proxima_dose   IS 'Data da proxima dose. Preenchida, gera lembrete automatico para o tutor.';
COMMENT ON COLUMN tb_vacina_tratamento.observacoes    IS 'Anotacoes do veterinario sobre a aplicacao.';

COMMENT ON TABLE  tb_pedido_medico                IS 'Pedido de exame ou prescricao de medicamento emitido na consulta.';
COMMENT ON COLUMN tb_pedido_medico.id             IS 'Chave primaria. Gerada por sq_pedido_medico.';
COMMENT ON COLUMN tb_pedido_medico.consulta_id    IS 'FK para tb_consulta: atendimento que emitiu o pedido.';
COMMENT ON COLUMN tb_pedido_medico.pet_id         IS 'FK para tb_pet: animal destinatario.';
COMMENT ON COLUMN tb_pedido_medico.tipo           IS 'Natureza do pedido: EXAME ou MEDICAMENTO.';
COMMENT ON COLUMN tb_pedido_medico.descricao      IS 'O que foi pedido ou prescrito.';
COMMENT ON COLUMN tb_pedido_medico.instrucoes     IS 'Orientacoes ao tutor, como posologia ou preparo.';
COMMENT ON COLUMN tb_pedido_medico.status         IS 'Situacao: PENDENTE, CONCLUIDO ou CANCELADO.';
COMMENT ON COLUMN tb_pedido_medico.data_limite    IS 'Prazo para cumprir o pedido.';
COMMENT ON COLUMN tb_pedido_medico.created_at     IS 'Data e hora da emissao.';

-- --- Bem-estar: wearable de hidratacao --------------------------------
COMMENT ON TABLE  tb_leitura_wearable             IS 'Leitura do wearable de hidratacao felina. Alimenta a verificacao diaria que dispara alerta ao tutor.';
COMMENT ON COLUMN tb_leitura_wearable.id          IS 'Chave primaria. Gerada por sq_leitura_wearable.';
COMMENT ON COLUMN tb_leitura_wearable.pet_id      IS 'FK para tb_pet: animal monitorado.';
COMMENT ON COLUMN tb_leitura_wearable.timestamp   IS 'Momento exato da leitura enviada pelo dispositivo.';
COMMENT ON COLUMN tb_leitura_wearable.consumo_ml_registrado    IS 'Volume ingerido nesta leitura, em mililitros.';
COMMENT ON COLUMN tb_leitura_wearable.consumo_diario_acumulado IS 'Soma do consumo do dia ate esta leitura, em mililitros.';
COMMENT ON COLUMN tb_leitura_wearable.meta_diaria_ml           IS 'Meta diaria de ingestao, calculada a partir do peso do animal.';
COMMENT ON COLUMN tb_leitura_wearable.percentual_meta          IS 'Percentual da meta diaria ja atingido.';
COMMENT ON COLUMN tb_leitura_wearable.alerta_gerado            IS 'Indica se esta leitura originou alerta: 1 sim, 0 nao.';
COMMENT ON COLUMN tb_leitura_wearable.tipo_alerta              IS 'Classificacao do alerta: BAIXO_CONSUMO, DESIDRATACAO_CRITICA, META_ATINGIDA ou CONSUMO_EXCESSIVO.';
COMMENT ON COLUMN tb_leitura_wearable.descricao_alerta         IS 'Mensagem do alerta enviada ao tutor.';

-- --- Comunicacao com o tutor -----------------------------------------
COMMENT ON TABLE  tb_lembrete                     IS 'Lembrete enviado ao tutor. Criado automaticamente por vacina, consulta, pedido medico ou alerta de hidratacao.';
COMMENT ON COLUMN tb_lembrete.id                  IS 'Chave primaria. Gerada por sq_lembrete.';
COMMENT ON COLUMN tb_lembrete.pet_id              IS 'FK para tb_pet: animal a que o lembrete se refere.';
COMMENT ON COLUMN tb_lembrete.responsavel_id      IS 'FK para tb_responsavel: tutor que recebe o lembrete.';
COMMENT ON COLUMN tb_lembrete.tipo                IS 'Origem do lembrete: VACINA, CONSULTA, EXAME, MEDICAMENTO ou HIDRATACAO.';
COMMENT ON COLUMN tb_lembrete.mensagem            IS 'Texto apresentado ao tutor.';
COMMENT ON COLUMN tb_lembrete.data_agendada       IS 'Data em que o lembrete deve ser entregue.';
COMMENT ON COLUMN tb_lembrete.status              IS 'Situacao do envio: PENDENTE, ENVIADO ou FALHOU.';
COMMENT ON COLUMN tb_lembrete.referencia_id       IS 'Id do registro que originou o lembrete.';
COMMENT ON COLUMN tb_lembrete.referencia_tipo     IS 'Tabela de origem do lembrete, por exemplo Consulta ou LEITURA_WEARABLE.';
COMMENT ON COLUMN tb_lembrete.created_at          IS 'Data e hora da criacao do lembrete.';

COMMIT;
EXIT;
