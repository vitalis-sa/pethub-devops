package fiap.pethub.service;

import fiap.pethub.dto.request.ConsultaRequest;
import fiap.pethub.dto.response.ConsultaResponse;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.entity.Consulta;
import fiap.pethub.entity.Pet;
import fiap.pethub.entity.Veterinario;
import fiap.pethub.enums.StatusConsulta;
import fiap.pethub.enums.TipoLembrete;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.ConsultaMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.security.EscopoDoUsuario;
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
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository repository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final UnidadeVeterinarioRepository unidadeRepository;
    private final ConsultaMapper mapper;
    private final EscopoDoUsuario escopo;
    private final LembreteService lembreteService;

    public Page<ConsultaResponse> findAll(Long petId, Long veterinarioId, StatusConsulta status, Pageable pageable) {
        if (petId != null) {
            exigirPosseDoPet(petId);
        }
        return Stream.<Map.Entry<Boolean, Supplier<Page<Consulta>>>>of(
                Map.entry(petId != null && status != null, () -> repository.findByPetIdAndStatus(petId, status, pageable)),
                Map.entry(petId != null,                   () -> repository.findByPetId(petId, pageable)),
                Map.entry(veterinarioId != null,           () -> repository.findByVeterinarioId(veterinarioId, pageable)),
                Map.entry(status != null,                  () -> repository.findByStatus(status, pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(listarNoEscopo(pageable))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "consultas", key = "#id + '-' + @escopoDoUsuario.chaveDeCache()")
    public ConsultaResponse findById(Long id) {
        Consulta entidade = findEntityById(id);
        escopo.exigirPosse(entidade.getPet().getResponsavel().getId());
        return mapper.toResponse(entidade);
    }

    @Transactional
    @CacheEvict(value = "consultas", allEntries = true)
    public ConsultaResponse create(ConsultaRequest request) {
        Consulta entity = buildConsultaEntity(request);
        Consulta saved = repository.save(entity);
        notificarResponsavel(saved);
        return mapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "consultas", allEntries = true)
    public ConsultaResponse update(Long id, ConsultaRequest request) {
        Consulta entity = findEntityById(id);
        escopo.exigirPosse(entity.getPet().getResponsavel().getId());
        mapper.updateEntity(request, entity);
        applyUnidade(request.getUnidadeId(), entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "consultas", allEntries = true)
    public DeleteResponse delete(Long id) {
        Consulta entidade = findEntityById(id);
        escopo.exigirPosse(entidade.getPet().getResponsavel().getId());
        repository.delete(entidade);
        return DeleteResponse.of("Consulta", id);
    }

    private Consulta buildConsultaEntity(ConsultaRequest request) {
        Consulta entity = mapper.toEntity(request);
        entity.setPet(findPet(request.getPetId()));
        entity.setVeterinario(findVeterinario(request.getVeterinarioId()));
        applyUnidade(request.getUnidadeId(), entity);
        return entity;
    }

    private void applyUnidade(Long unidadeId, Consulta entity) {
        unidadeRepository.findById(unidadeId)
                .ifPresentOrElse(
                        entity::setUnidade,
                        () -> { throw new ResourceNotFoundException("Unidade veterinária não encontrada com id: " + unidadeId);
                        });
    }

    private void notificarResponsavel(Consulta saved) {
        Pet pet = saved.getPet();
        lembreteService.criarLembrete(
                pet.getResponsavel().getId(),
                pet.getId(),
                TipoLembrete.CONSULTA,
                saved.getDataHora().toLocalDate(),
                "Consulta agendada para " + pet.getNome() + " em " + saved.getDataHora(),
                saved.getId(),
                "Consulta"
        );
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
    private Supplier<Page<Consulta>> listarNoEscopo(Pageable pageable) {
        return () -> escopo.ehVeterinario()
                ? repository.findAll(pageable)
                : repository.findByPetResponsavelId(escopo.idDoResponsavel(), pageable);
    }

    /** Filtrar por um pet alheio nao pode revelar sequer que ele existe. */
    private void exigirPosseDoPet(Long petId) {
        escopo.exigirPosse(findPet(petId).getResponsavel().getId());
    }

    private Consulta findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + id));
    }
}

