package fiap.pethub.service;

import fiap.pethub.dto.request.DiagnosticoRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.DiagnosticoResponse;
import fiap.pethub.entity.Consulta;
import fiap.pethub.entity.Diagnostico;
import fiap.pethub.entity.Pet;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.DiagnosticoMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.DiagnosticoRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.security.EscopoDoUsuario;
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
public class DiagnosticoService {

    private final DiagnosticoRepository repository;
    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final DiagnosticoMapper mapper;
    private final EscopoDoUsuario escopo;

    public Page<DiagnosticoResponse> findAll(Long petId, Long consultaId, Pageable pageable) {
        if (petId != null) {
            exigirPosseDoPet(petId);
        }
        return Stream.<Map.Entry<Boolean, Supplier<Page<Diagnostico>>>>of(
                Map.entry(petId != null,     () -> repository.findByPetId(petId, pageable)),
                Map.entry(consultaId != null, () -> repository.findByConsultaId(consultaId, pageable))
        )
                .filter(Map.Entry::getKey)
                .findFirst()
                .map(Map.Entry::getValue)
                .map(Supplier::get)
                .orElseGet(listarNoEscopo(pageable))
                .map(mapper::toResponse);
    }

    @Cacheable(value = "diagnosticos", key = "#id + '-' + @escopoDoUsuario.chaveDeCache()")
    public DiagnosticoResponse findById(Long id) {
        Diagnostico entidade = findEntityById(id);
        escopo.exigirPosse(entidade.getPet().getResponsavel().getId());
        return mapper.toResponse(entidade);
    }

    @Transactional
    @CacheEvict(value = "diagnosticos", allEntries = true)
    public DiagnosticoResponse create(DiagnosticoRequest request) {
        Diagnostico entity = buildDiagnosticoEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "diagnosticos", allEntries = true)
    public DiagnosticoResponse update(Long id, DiagnosticoRequest request) {
        Diagnostico entity = findEntityById(id);
        escopo.exigirPosse(entity.getPet().getResponsavel().getId());
        mapper.updateEntity(request, entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "diagnosticos", allEntries = true)
    public DeleteResponse delete(Long id) {
        Diagnostico entidade = findEntityById(id);
        escopo.exigirPosse(entidade.getPet().getResponsavel().getId());
        repository.delete(entidade);
        return DeleteResponse.of("Diagnóstico", id);
    }

    private Diagnostico buildDiagnosticoEntity(DiagnosticoRequest request) {
        Diagnostico entity = mapper.toEntity(request);
        entity.setConsulta(findConsulta(request.getConsultaId()));
        entity.setPet(findPet(request.getPetId()));
        applyBooleanDefaults(entity);
        return entity;
    }

    private void applyBooleanDefaults(Diagnostico d) {
        d.setPerdaApetite(d.getPerdaApetite() != null ? d.getPerdaApetite() : false);
        d.setVomito(d.getVomito() != null ? d.getVomito() : false);
        d.setDiarreia(d.getDiarreia() != null ? d.getDiarreia() : false);
        d.setTosse(d.getTosse() != null ? d.getTosse() : false);
        d.setDificuldadeRespiratoria(d.getDificuldadeRespiratoria() != null ? d.getDificuldadeRespiratoria() : false);
        d.setClaudicacao(d.getClaudicacao() != null ? d.getClaudicacao() : false);
        d.setLesoesPele(d.getLesoesPele() != null ? d.getLesoesPele() : false);
        d.setSecrecaoNasal(d.getSecrecaoNasal() != null ? d.getSecrecaoNasal() : false);
        d.setSecrecaoOcular(d.getSecrecaoOcular() != null ? d.getSecrecaoOcular() : false);
    }

    private Consulta findConsulta(Long consultaId) {
        return consultaRepository.findById(consultaId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada com id: " + consultaId));
    }

    private Pet findPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + petId));
    }

    /** Lista respeitando o escopo: veterinario ve tudo, responsavel so os pets dele. */
    private Supplier<Page<Diagnostico>> listarNoEscopo(Pageable pageable) {
        return () -> escopo.ehVeterinario()
                ? repository.findAll(pageable)
                : repository.findByPetResponsavelId(escopo.idDoResponsavel(), pageable);
    }

    /** Filtrar por um pet alheio nao pode revelar sequer que ele existe. */
    private void exigirPosseDoPet(Long petId) {
        escopo.exigirPosse(findPet(petId).getResponsavel().getId());
    }

    private Diagnostico findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnóstico não encontrado com id: " + id));
    }
}
