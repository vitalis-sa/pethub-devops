package fiap.pethub.service;

import fiap.pethub.dto.request.VacinaTratamentoRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.VacinaTratamentoResponse;
import fiap.pethub.entity.Pet;
import fiap.pethub.entity.VacinaTratamento;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.enums.TipoLembrete;
import fiap.pethub.enums.TipoVacinaTratamento;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.VacinaTratamentoMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.security.EscopoDoUsuario;
import fiap.pethub.repository.VacinaTratamentoRepository;
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
public class VacinaTratamentoService {

    private final VacinaTratamentoRepository repository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final ConsultaRepository consultaRepository;
    private final VacinaTratamentoMapper mapper;
    private final EscopoDoUsuario escopo;
    private final LembreteService lembreteService;

    public Page<VacinaTratamentoResponse> findAll(Long petId, TipoVacinaTratamento tipo, Pageable pageable) {
        if (petId != null) {
            exigirPosseDoPet(petId);
        }
        return Stream.<Map.Entry<Boolean, Supplier<Page<VacinaTratamento>>>>of(
                Map.entry(petId != null && tipo != null, () -> repository.findByPetIdAndTipo(petId, tipo, pageable)),
                Map.entry(petId != null,                 () -> repository.findByPetId(petId, pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(listarNoEscopo(pageable))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "vacinas", key = "#id + '-' + @escopoDoUsuario.chaveDeCache()")
    public VacinaTratamentoResponse findById(Long id) {
        VacinaTratamento entidade = findEntityById(id);
        escopo.exigirPosse(entidade.getPet().getResponsavel().getId());
        return mapper.toResponse(entidade);
    }

    @Transactional
    @CacheEvict(value = "vacinas", allEntries = true)
    public VacinaTratamentoResponse create(VacinaTratamentoRequest request) {
        VacinaTratamento entity = buildVacinaEntity(request);
        VacinaTratamento saved = repository.save(entity);
        notificarProximaDose(saved);
        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "vacinas", allEntries = true)
    public VacinaTratamentoResponse update(Long id, VacinaTratamentoRequest request) {
        VacinaTratamento entity = findEntityById(id);
        escopo.exigirPosse(entity.getPet().getResponsavel().getId());
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "vacinas", allEntries = true)
    public DeleteResponse delete(Long id) {
        VacinaTratamento entidade = findEntityById(id);
        escopo.exigirPosse(entidade.getPet().getResponsavel().getId());
        repository.delete(entidade);
        return DeleteResponse.of("Vacina/Tratamento", id);
    }

    private VacinaTratamento buildVacinaEntity(VacinaTratamentoRequest request) {
        VacinaTratamento entity = mapper.toEntity(request);
        entity.setPet(findPet(request.getPetId()));
        entity.setVeterinario(findVeterinario(request.getVeterinarioId()));
        applyConsulta(request.getConsultaId(), entity);
        return entity;
    }

    private void applyConsulta(Long consultaId, VacinaTratamento entity) {
        Optional.ofNullable(consultaId)
                .map(cid -> consultaRepository.findById(cid)
                        .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + cid)))
                .ifPresent(entity::setConsulta);
    }

    private void notificarProximaDose(VacinaTratamento saved) {
        Optional.ofNullable(saved.getProximaDose())
                .ifPresent(data -> {
                    Pet pet = saved.getPet();
                    lembreteService.criarLembrete(
                            pet.getResponsavel().getId(),
                            pet.getId(),
                            TipoLembrete.VACINA,
                            data,
                            "Próxima dose de " + saved.getNome() + " para " + pet.getNome(),
                            saved.getId(),
                            "VacinaTratamento"
                    );
                });
    }

    private Pet findPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + petId));
    }

    private Veterinario findVeterinario(Long veterinarioId) {
        return veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com id: " + veterinarioId));
    }

    /** Lista respeitando o escopo: veterinario ve tudo, responsavel so os pets dele. */
    private Supplier<Page<VacinaTratamento>> listarNoEscopo(Pageable pageable) {
        return () -> escopo.ehVeterinario()
                ? repository.findAll(pageable)
                : repository.findByPetResponsavelId(escopo.idDoResponsavel(), pageable);
    }

    /** Filtrar por um pet alheio nao pode revelar sequer que ele existe. */
    private void exigirPosseDoPet(Long petId) {
        escopo.exigirPosse(findPet(petId).getResponsavel().getId());
    }

    private VacinaTratamento findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacina/Tratamento não encontrado com id: " + id));
    }
}

