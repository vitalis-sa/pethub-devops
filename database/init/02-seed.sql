-- =====================================================================
-- PetHub - Carga inicial (seed)
-- Checkpoint FIAP - Imagem e Containers em Nuvem
--
-- Cria o minimo necessario para a demonstracao do CRUD: uma unidade,
-- um veterinario e um responsavel (com endereco e contato).
-- As demais tabelas sao populadas ao vivo, via API, durante o video.
--
-- As senhas sao hashes BCrypt reais, gerados pela mesma classe que a
-- aplicacao usa (fiap.pethub.config.PasswordUtil / BCryptPasswordEncoder).
-- Nenhuma senha em texto puro e gravada no banco.
--   responsavel.ana@pethub.com.br  -> PetHub@2026
--   marcos.vet@pethub.com.br       -> Vet@2026
-- =====================================================================

WHENEVER SQLERROR EXIT SQL.SQLCODE
SET SQLBLANKLINES ON
SET DEFINE OFF

-- ---------------------------------------------------------------------
-- Unidade veterinaria
-- ---------------------------------------------------------------------
INSERT INTO tb_unidade_veterinario
    (id, nome, logradouro, numero, bairro, cidade, estado, cep)
VALUES
    (sq_unidade_veterinario.NEXTVAL, 'PetHub Clinica Paulista',
     'Avenida Paulista', '1578', 'Bela Vista', 'Sao Paulo', 'SP', '01310200');

-- ---------------------------------------------------------------------
-- Veterinario vinculado a unidade
-- ---------------------------------------------------------------------
INSERT INTO tb_veterinario
    (id, nome, crmv, email, senha, telefone, especialidade, ativo, unidade_id)
VALUES
    (sq_veterinario.NEXTVAL, 'Marcos Andrade', 'CRMV-SP-12345',
     'marcos.vet@pethub.com.br',
     '$2a$10$vYZ8SBjAPNGlhQMKVPt0Fe7Rfr2pSbVMajsyTbOaTdllaKPcyyjKO',
     '11988887777', 'Clinica Geral', 1,
     (SELECT id FROM tb_unidade_veterinario WHERE nome = 'PetHub Clinica Paulista'));

-- ---------------------------------------------------------------------
-- Responsavel (tutor) + endereco + contato
-- ---------------------------------------------------------------------
INSERT INTO tb_responsavel
    (id, nome, cpf, email, senha, ativo, created_at)
VALUES
    (sq_responsavel.NEXTVAL, 'Ana Flavia Camelo', '11122233344',
     'responsavel.ana@pethub.com.br',
     '$2a$10$aRpTGRgUDDZduZGEU8i6q.QgG/y1bg5HL0hFgUhvUlqDTgSZ.r9Am',
     1, SYSTIMESTAMP);

INSERT INTO tb_responsavel_endereco
    (id, responsavel_id, logradouro, numero, complemento,
     bairro, cidade, estado, cep, principal)
VALUES
    (sq_responsavel_endereco.NEXTVAL,
     (SELECT id FROM tb_responsavel WHERE cpf = '11122233344'),
     'Rua Vergueiro', '3185', 'Apto 71', 'Vila Mariana',
     'Sao Paulo', 'SP', '04101300', 1);

INSERT INTO tb_responsavel_contato
    (id, responsavel_id, tipo, telefone, principal)
VALUES
    (sq_responsavel_contato.NEXTVAL,
     (SELECT id FROM tb_responsavel WHERE cpf = '11122233344'),
     'CELULAR', '11977776666', 1);

COMMIT;

-- ---------------------------------------------------------------------
-- Conferencia da carga
-- ---------------------------------------------------------------------
SELECT 'unidades='       || COUNT(*) AS carga FROM tb_unidade_veterinario;
SELECT 'veterinarios='   || COUNT(*) AS carga FROM tb_veterinario;
SELECT 'responsaveis='   || COUNT(*) AS carga FROM tb_responsavel;

EXIT;
