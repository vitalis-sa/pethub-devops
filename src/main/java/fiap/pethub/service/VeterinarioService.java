package fiap.pethub.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import fiap.pethub.dto.request.VeterinarioRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.VeterinarioResponse;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.VeterinarioMapper;
import fiap.pethub.repository.UnidadeVeterinarioRepository;
import fiap.pethub.repository.VeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VeterinarioService {

    private final VeterinarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UnidadeVeterinarioRepository unidadeRepository;
    private final VeterinarioMapper mapper;

    public Page<VeterinarioResponse> findAll(String nome, Boolean ativo, Pageable pageable) {
        return Stream.<Map.Entry<Boolean, Supplier<Page<Veterinario>>>>of(
                Map.entry(nome != null,              () -> repository.findByNomeContainingIgnoreCase(nome, pageable)),
                Map.entry(Boolean.TRUE.equals(ativo), () -> repository.findByAtivoTrue(pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(() -> repository.findAll(pageable))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "veterinarios", key = "#id")
    public VeterinarioResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + id));
    }

    @Transactional
    @CacheEvict(value = "veterinarios", allEntries = true)
    public VeterinarioResponse create(VeterinarioRequest request) {
        Veterinario entity = buildVeterinarioEntity(request);
        Veterinario saved = repository.save(entity);
        vincularUnidade(request.getUnidadeId(), saved);
        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "veterinarios", key = "#id")
    public VeterinarioResponse update(Long id, VeterinarioRequest request) {
        Veterinario entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        applySenha(request.getSenha(), entity);
        applyAtivo(request.getAtivo(), entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "veterinarios", key = "#id")
    public DeleteResponse delete(Long id) {
        repository.findById(id)
                .ifPresentOrElse(
                        repository::delete,
                        () -> { throw new ResourceNotFoundException("Veterinário não encontrado com id: " + id); }
                );
        return DeleteResponse.of("Veterinário", id);
    }

    private Veterinario buildVeterinarioEntity(VeterinarioRequest request) {
        Veterinario entity = mapper.toEntity(request);
        entity.setSenha(passwordEncoder.encode(request.getSenha()));
        entity.setAtivo(request.getAtivo() != null ? request.getAtivo() : true);
        return entity;
    }

    private void applySenha(String senha, Veterinario entity) {
        Optional.ofNullable(senha)
                .filter(s -> !s.isBlank())
                .map(passwordEncoder::encode)
                .ifPresent(entity::setSenha);
    }

    private void applyAtivo(Boolean ativo, Veterinario entity) {
        Optional.ofNullable(ativo).ifPresent(entity::setAtivo);
    }

    private Veterinario findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + id));
    }

    private void vincularUnidade(Long unidadeId, Veterinario vet) {
        Optional.ofNullable(unidadeId).ifPresent(id -> {
            var unidade = unidadeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada com id: " + id));
            vet.setUnidade(unidade);
            repository.save(vet);
        });
    }
}
