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

COMMIT;
EXIT;
