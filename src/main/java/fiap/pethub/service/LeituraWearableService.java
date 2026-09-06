package fiap.pethub.service;

import fiap.pethub.dto.request.LeituraWearableRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.LeituraWearableResponse;
import fiap.pethub.entity.LeituraWearable;
import fiap.pethub.entity.Pet;
import fiap.pethub.enums.TipoAlertaHidratacao;
import fiap.pethub.enums.TipoLembrete;
import fiap.pethub.exception.ResourceNotFoundException;
import fiap.pethub.mapper.LeituraWearableMapper;
import fiap.pethub.repository.LeituraWearableRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.security.EscopoDoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LeituraWearableService {

    private static final int HORARIO_AVALIACAO = 20;
    private static final int PAGE_SIZE = 50;

    private final LeituraWearableRepository repository;
    private final PetRepository petRepository;
    private final LeituraWearableMapper mapper;
    private final LembreteService lembreteService;
    private final EscopoDoUsuario escopo;

    public Page<LeituraWearableResponse> findAll(Long petId, Boolean apenasAlertas, Pageable pageable) {
        if (petId != null) {
            exigirPosseDoPet(petId);
        }
        boolean filtroPet    = petId != null;
        boolean filtroAlerta = Boolean.TRUE.equals(apenasAlertas);

        Page<LeituraWearable> resultado = filtroPet && filtroAlerta ? repository.findByPetIdAndAlertaGeradoTrue(petId, pageable)
                                        : filtroPet                ? repository.findByPetId(petId, pageable)
                                        : filtroAlerta             ? repository.findByAlertaGeradoTrue(pageable)
                                                                   : listarNoEscopo(pageable);

        return resultado.map(mapper::toResponse);
    }

    @Cacheable(value = "leituras", key = "#id + '-' + @escopoDoUsuario.chaveDeCache()")
    public LeituraWearableResponse findById(Long id) {
        LeituraWearable leitura = findEntityById(id);
        escopo.exigirPosse(leitura.getPet().getResponsavel().getId());
        return mapper.toResponse(leitura);
    }

    @Transactional
    @CacheEvict(value = "leituras", allEntries = true)
    public LeituraWearableResponse create(LeituraWearableRequest request) {
        return mapper.toResponse(repository.save(buildLeituraEntity(request)));
    }

    @Transactional
    @CacheEvict(value = "leituras", allEntries = true)
    public LeituraWearableResponse update(Long id, LeituraWearableRequest request) {
        LeituraWearable entity = findEntityById(id);
        escopo.exigirPosse(entity.getPet().getResponsavel().getId());
        mapper.updateEntity(request, entity);
        recalcularMetricas(entity);
        return mapper.toResponse(repository.save(entity));
    }

    @Transactional
    @CacheEvict(value = "leituras", allEntries = true)
    public DeleteResponse delete(Long id) {
        LeituraWearable entidade = findEntityById(id);
        escopo.exigirPosse(entidade.getPet().getResponsavel().getId());
        repository.delete(entidade);
        return DeleteResponse.of("Leitura wearable", id);
    }

    // -------------------------------------------------------------------------
    // Scheduler — verifica hidratação diária às 21h
    // -------------------------------------------------------------------------

    @Scheduled(cron = "0 0 21 * * *")
    @Transactional
    public void verificarHidratacaoDiaria() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDia   = inicioDia.plusDays(1);
        int page = 0;
        Page<Object[]> pagina;

        do {
            pagina = repository.resumoDiarioPorPet(inicioDia, fimDia, PageRequest.of(page++, PAGE_SIZE));
            pagina.getContent().forEach(row -> processarResumo(row, LocalDate.now()));
        } while (pagina.hasNext());
    }

    private void processarResumo(Object[] row, LocalDate hoje) {
        Pet pet       = (Pet)    row[0];
        double consumo = (Double) row[1];
        double meta    = (Double) row[2];
        double pct     = (consumo / meta) * 100.0;

        if (pct >= 50) return;

        String mensagem = pct < 25
            ? String.format("⚠️ CRÍTICO: %s consumiu apenas %.0fml/%.0fml (%.1f%%). Risco de desidratação grave!", pet.getNome(), consumo, meta, pct)
            : String.format("⚠️ Atenção: %s consumiu %.0fml/%.0fml hoje (%.1f%% da meta). Incentive-o a beber mais água.", pet.getNome(), consumo, meta, pct);

        lembreteService.criarLembrete(
                pet.getResponsavel().getId(), pet.getId(),
                TipoLembrete.HIDRATACAO, hoje,
                mensagem, null, "LEITURA_WEARABLE");
    }

    private LeituraWearable buildLeituraEntity(LeituraWearableRequest request) {
        LeituraWearable entity = mapper.toEntity(request);
        entity.setPet(findPet(request.getPetId()));

        LocalDateTime inicioDia = request.getTimestamp().toLocalDate().atStartOfDay();
        double acumulado = repository.somarConsumoDiario(request.getPetId(), inicioDia, inicioDia.plusDays(1));
        entity.setConsumoDiarioAcumulado(acumulado + request.getConsumoMlRegistrado());

        recalcularMetricas(entity);
        return entity;
    }

    private void recalcularMetricas(LeituraWearable entity) {
        double pct     = (entity.getConsumoDiarioAcumulado() / entity.getMetaDiariaML()) * 100.0;
        boolean fimDia = entity.getTimestamp().getHour() >= HORARIO_AVALIACAO;

        entity.setPercentualMeta(Math.round(pct * 100.0) / 100.0);

        if      (pct >= 120)          setAlerta(entity, true,  TipoAlertaHidratacao.CONSUMO_EXCESSIVO,   String.format("Consumo excessivo: %.1f%% da meta. Verifique possível polidipsia.", pct));
        else if (pct >= 100)          setAlerta(entity, false, TipoAlertaHidratacao.META_ATINGIDA,        String.format("Meta atingida: %.1f%%. Hidratação saudável.", pct));
        else if (fimDia && pct < 25)  setAlerta(entity, true,  TipoAlertaHidratacao.DESIDRATACAO_CRITICA, String.format("Felino consumiu %.1f%% da meta. Risco de desidratação crítica.", pct));
        else if (fimDia && pct < 50)  setAlerta(entity, true,  TipoAlertaHidratacao.BAIXO_CONSUMO,        String.format("Consumo abaixo do ideal: %.1f%% da meta.", pct));
        else                          setAlerta(entity, false, null, null);
    }

    private void setAlerta(LeituraWearable entity, boolean gerado, TipoAlertaHidratacao tipo, String descricao) {
        entity.setAlertaGerado(gerado);
        entity.setTipoAlerta(tipo);
        entity.setDescricaoAlerta(descricao);
    }

    private Pet findPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com id: " + petId));
    }

    /** Lista respeitando o escopo: veterinario ve tudo, responsavel so os pets dele. */
    private Page<LeituraWearable> listarNoEscopo(Pageable pageable) {
        return escopo.ehVeterinario()
                ? repository.findAll(pageable)
                : repository.findByPetResponsavelId(escopo.idDoResponsavel(), pageable);
    }

    /** Filtrar por um pet alheio nao pode revelar sequer que ele existe. */
    private void exigirPosseDoPet(Long petId) {
        escopo.exigirPosse(findPet(petId).getResponsavel().getId());
    }

    private LeituraWearable findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leitura não encontrada com id: " + id));
    }
}

