-- =====================================================================
-- PetHub - Carga inicial (seed)
-- Checkpoint FIAP - Imagem e Containers em Nuvem
--
-- Cria o minimo necessario para a demonstracao do CRUD: uma unidade,
-- um veterinario, um responsavel (com endereco e contato) e as duas
-- tabelas relacionadas do core -- tb_pet e tb_consulta -- com duas linhas
-- cada. Assim o primeiro SELECT do roteiro do video ja devolve dados, e a
-- inclusao feita ao vivo aparece por contraste com a carga existente.
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

-- ---------------------------------------------------------------------
-- Pets do responsavel (core da solucao)
-- ---------------------------------------------------------------------
INSERT INTO tb_pet
    (id, nome, especie, raca, idade, peso, genero,
     responsavel_id, veterinario_responsavel_id)
VALUES
    (sq_pet.NEXTVAL, 'Mia', 'Felino', 'Siames', 3, 4.2, 'Femea',
     (SELECT id FROM tb_responsavel WHERE cpf = '11122233344'),
     (SELECT id FROM tb_veterinario WHERE crmv = 'CRMV-SP-12345'));

INSERT INTO tb_pet
    (id, nome, especie, raca, idade, peso, genero,
     responsavel_id, veterinario_responsavel_id)
VALUES
    (sq_pet.NEXTVAL, 'Thor', 'Canino', 'Golden Retriever', 5, 31.5, 'Macho',
     (SELECT id FROM tb_responsavel WHERE cpf = '11122233344'),
     (SELECT id FROM tb_veterinario WHERE crmv = 'CRMV-SP-12345'));

-- ---------------------------------------------------------------------
-- Consultas vinculadas aos pets (segunda tabela do CRUD relacionado)
-- ---------------------------------------------------------------------
INSERT INTO tb_consulta
    (id, pet_id, veterinario_id, unidade_id, data_hora, tipo, status, observacoes)
VALUES
    (sq_consulta.NEXTVAL,
     (SELECT id FROM tb_pet WHERE nome = 'Mia'),
     (SELECT id FROM tb_veterinario WHERE crmv = 'CRMV-SP-12345'),
     (SELECT id FROM tb_unidade_veterinario WHERE nome = 'PetHub Clinica Paulista'),
     SYSTIMESTAMP - INTERVAL '7' DAY, 'PRESENCIAL', 'REALIZADA',
     'Check-up anual. Ingestao de agua abaixo da meta; wearable de hidratacao instalado.');

INSERT INTO tb_consulta
    (id, pet_id, veterinario_id, unidade_id, data_hora, tipo, status, observacoes)
VALUES
    (sq_consulta.NEXTVAL,
     (SELECT id FROM tb_pet WHERE nome = 'Thor'),
     (SELECT id FROM tb_veterinario WHERE crmv = 'CRMV-SP-12345'),
     NULL,
     SYSTIMESTAMP + INTERVAL '3' DAY, 'TELECONSULTA', 'AGENDADA',
     'Retorno para avaliar claudicacao na pata traseira esquerda.');

COMMIT;

-- ---------------------------------------------------------------------
-- Conferencia da carga
-- ---------------------------------------------------------------------
SELECT 'unidades='       || COUNT(*) AS carga FROM tb_unidade_veterinario;
SELECT 'veterinarios='   || COUNT(*) AS carga FROM tb_veterinario;
SELECT 'responsaveis='   || COUNT(*) AS carga FROM tb_responsavel;
SELECT 'pets='           || COUNT(*) AS carga FROM tb_pet;
SELECT 'consultas='      || COUNT(*) AS carga FROM tb_consulta;

EXIT;
