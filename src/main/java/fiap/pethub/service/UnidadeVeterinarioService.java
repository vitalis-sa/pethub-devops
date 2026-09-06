package fiap.pethub.service;

import fiap.pethub.dto.request.UnidadeVeterinarioRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.UnidadeVeterinarioResponse;
import fiap.pethub.entity.UnidadeVeterinario;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.UnidadeVeterinarioMapper;
import fiap.pethub.repository.UnidadeVeterinarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UnidadeVeterinarioService {

    private final UnidadeVeterinarioRepository repository;
    private final UnidadeVeterinarioMapper mapper;

    public Page<UnidadeVeterinarioResponse> findAll(String nome, String cidade, Pageable pageable) {
        if (nome != null)   return repository.findByNomeContainingIgnoreCase(nome, pageable).map(mapper::toResponse);
        if (cidade != null) return repository.findByCidade(cidade, pageable).map(mapper::toResponse);
        return repository.findAll(pageable).map(mapper::toResponse);
    }

    @Cacheable(value = "unidades", key = "#id")
    public UnidadeVeterinarioResponse findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada com id: " + id));
    }

    @Transactional
    @CacheEvict(value = "unidades", allEntries = true)
    public UnidadeVeterinarioResponse create(UnidadeVeterinarioRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    @Transactional
    @CacheEvict(value = "unidades", key = "#id")
    public UnidadeVeterinarioResponse update(Long id, UnidadeVeterinarioRequest request) {
        UnidadeVeterinario entity = findEntityById(id);
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "unidades", key = "#id")
    public DeleteResponse delete(Long id) {
        repository.findById(id)
                .ifPresentOrElse(repository::delete,
                        () -> { throw new ResourceNotFoundException("Unidade não encontrada com id: " + id); });
        return DeleteResponse.of("Unidade veterinária", id);
    }

    private UnidadeVeterinario findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada com id: " + id));
    }
}
