package fiap.pethub.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import fiap.pethub.dto.request.ResponsavelContatoRequest;
import fiap.pethub.dto.request.ResponsavelEnderecoRequest;
import fiap.pethub.dto.request.ResponsavelRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.ResponsavelContatoResponse;
import fiap.pethub.dto.response.ResponsavelEnderecoResponse;
import fiap.pethub.dto.response.ResponsavelResponse;
import fiap.pethub.entity.Responsavel;
import fiap.pethub.entity.ResponsavelContato;
import fiap.pethub.entity.ResponsavelEndereco;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.ResponsavelMapper;
import fiap.pethub.repository.ResponsavelContatoRepository;
import fiap.pethub.repository.ResponsavelEnderecoRepository;
import fiap.pethub.repository.ResponsavelRepository;
import fiap.pethub.security.EscopoDoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final ResponsavelContatoRepository contatoRepository;
    private final ResponsavelEnderecoRepository enderecoRepository;
    private final ResponsavelMapper mapper;
    private final EscopoDoUsuario escopo;

    // ─── Responsavel ──────────────────────────────────────────────────────────

    public Page<ResponsavelResponse> findAll(String nome, Boolean ativo, Pageable pageable) {
        // O tutor nao lista outros tutores: a listagem dele contem so ele mesmo.
        if (!escopo.ehVeterinario()) {
            return repository.findById(escopo.idDoResponsavel())
                    .map(mapper::toResponse)
                    .map(r -> (Page<ResponsavelResponse>) new PageImpl<>(List.of(r), pageable, 1))
                    .orElseGet(Page::empty);
        }
        if (nome != null && ativo != null)
            return repository.findByNomeContainingIgnoreCaseAndAtivo(nome, ativo, pageable).map(mapper::toResponse);
        if (nome != null)
            return repository.findByNomeContainingIgnoreCase(nome, pageable).map(mapper::toResponse);
        if (ativo != null)
            return repository.findByAtivo(ativo, pageable).map(mapper::toResponse);
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "responsaveis", key = "#id + '-' + @escopoDoUsuario.chaveDeCache()")
    public ResponsavelResponse findById(Long id) {
        escopo.exigirPosse(id);
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado com id: " + id));
    }

    @Cacheable(value = "responsaveis", key = "#cpf + '-' + @escopoDoUsuario.chaveDeCache()")
    public ResponsavelResponse findByCpf(String cpf) {
        Responsavel responsavel = repository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado com CPF: " + cpf));
        // Sem esta guarda, um tutor descobriria o cadastro de outro pelo CPF.
        escopo.exigirPosse(responsavel.getId());
        return mapper.toResponse(responsavel);
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public ResponsavelResponse create(ResponsavelRequest request) {
        Responsavel responsavel = mapper.toEntity(request);
        responsavel.setSenha(passwordEncoder.encode(request.getSenha()));
        responsavel.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        responsavel.setCreatedAt(LocalDateTime.now());
        return mapper.toResponse(repository.save(responsavel));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public ResponsavelResponse update(Long id, ResponsavelRequest request) {
        escopo.exigirSerOProprio(id);
        Responsavel responsavel = findEntityById(id);
        mapper.updateEntity(request, responsavel);
        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            responsavel.setSenha(passwordEncoder.encode(request.getSenha()));
        }
        return mapper.toResponse(repository.save(responsavel));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public DeleteResponse delete(Long id) {
        escopo.exigirSerOProprio(id);
        repository.findById(id)
                .ifPresentOrElse(
                        repository::delete,
                        () -> { throw new ResourceNotFoundException("Responsável não encontrado com id: " + id); }
                );
        return DeleteResponse.of("Responsável", id);
    }

    // ─── Contatos ─────────────────────────────────────────────────────────────

    @Cacheable(value = "responsaveis", key = "'contatos-' + #responsavelId + #pageable + '-' + @escopoDoUsuario.chaveDeCache()")
    public Page<ResponsavelContatoResponse> findContatos(Long responsavelId, Pageable pageable) {
        escopo.exigirPosse(responsavelId);
        ensureResponsavelExists(responsavelId);
        return contatoRepository.findByResponsavelId(responsavelId, pageable)
                .map(mapper::toContatoResponse);
    }

    @Cacheable(value = "responsaveis", key = "'contato-' + #contatoId + '-' + @escopoDoUsuario.chaveDeCache()")
    public ResponsavelContatoResponse findContatoById(Long responsavelId, Long contatoId) {
        escopo.exigirPosse(responsavelId);
        ensureResponsavelExists(responsavelId);
        return contatoRepository.findById(contatoId)
                .map(mapper::toContatoResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com id: " + contatoId));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public ResponsavelContatoResponse addContato(Long responsavelId, ResponsavelContatoRequest request) {
        escopo.exigirSerOProprio(responsavelId);
        Responsavel responsavel = findEntityById(responsavelId);
        ResponsavelContato contato = mapper.contatoToEntity(request);
        contato.setResponsavel(responsavel);
        contato.setPrincipal(request.getPrincipal() != null ? request.getPrincipal() : false);
        return mapper.toContatoResponse(contatoRepository.save(contato));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public ResponsavelContatoResponse updateContato(Long responsavelId, Long contatoId, ResponsavelContatoRequest request) {
        escopo.exigirSerOProprio(responsavelId);
        ensureResponsavelExists(responsavelId);
        ResponsavelContato contato = contatoRepository.findById(contatoId)
                .orElseThrow(() -> new ResourceNotFoundException("Contato não encontrado com id: " + contatoId));
        mapper.updateContato(request, contato);
        return mapper.toContatoResponse(contatoRepository.save(contato));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public DeleteResponse deleteContato(Long responsavelId, Long contatoId) {
        escopo.exigirSerOProprio(responsavelId);
        ensureResponsavelExists(responsavelId);
        contatoRepository.findById(contatoId)
                .ifPresentOrElse(
                        contatoRepository::delete,
                        () -> { throw new ResourceNotFoundException("Contato não encontrado com id: " + contatoId); }
                );
        return DeleteResponse.of("Contato", contatoId);
    }

    // ─── Endereços ────────────────────────────────────────────────────────────

    @Cacheable(value = "responsaveis", key = "'enderecos-' + #responsavelId + #pageable + '-' + @escopoDoUsuario.chaveDeCache()")
    public Page<ResponsavelEnderecoResponse> findEnderecos(Long responsavelId, Pageable pageable) {
        escopo.exigirPosse(responsavelId);
        ensureResponsavelExists(responsavelId);
        return enderecoRepository.findByResponsavelId(responsavelId, pageable)
                .map(mapper::toEnderecoResponse);
    }

    @Cacheable(value = "responsaveis", key = "'endereco-' + #enderecoId + '-' + @escopoDoUsuario.chaveDeCache()")
    public ResponsavelEnderecoResponse findEnderecoById(Long responsavelId, Long enderecoId) {
        escopo.exigirPosse(responsavelId);
        ensureResponsavelExists(responsavelId);
        return enderecoRepository.findById(enderecoId)
                .map(mapper::toEnderecoResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com id: " + enderecoId));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public ResponsavelEnderecoResponse addEndereco(Long responsavelId, ResponsavelEnderecoRequest request) {
        escopo.exigirSerOProprio(responsavelId);
        Responsavel responsavel = findEntityById(responsavelId);
        ResponsavelEndereco endereco = mapper.enderecoToEntity(request);
        endereco.setResponsavel(responsavel);
        endereco.setPrincipal(request.getPrincipal() != null ? request.getPrincipal() : false);
        return mapper.toEnderecoResponse(enderecoRepository.save(endereco));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public ResponsavelEnderecoResponse updateEndereco(Long responsavelId, Long enderecoId, ResponsavelEnderecoRequest request) {
        escopo.exigirSerOProprio(responsavelId);
        ensureResponsavelExists(responsavelId);
        ResponsavelEndereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado com id: " + enderecoId));
        mapper.updateEndereco(request, endereco);
        return mapper.toEnderecoResponse(enderecoRepository.save(endereco));
    }

    @Transactional
    @CacheEvict(value = "responsaveis", allEntries = true)
    public DeleteResponse deleteEndereco(Long responsavelId, Long enderecoId) {
        escopo.exigirSerOProprio(responsavelId);
        ensureResponsavelExists(responsavelId);
        enderecoRepository.findById(enderecoId)
                .ifPresentOrElse(
                        enderecoRepository::delete,
                        () -> { throw new ResourceNotFoundException("Endereço não encontrado com id: " + enderecoId); }
                );
        return DeleteResponse.of("Endereço", enderecoId);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    public Responsavel findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado com id: " + id));
    }

    private void ensureResponsavelExists(Long responsavelId) {
        if (!repository.existsById(responsavelId))
            throw new ResourceNotFoundException("Responsável não encontrado com id: " + responsavelId);
    }
}

