package fiap.pethub.service;

import fiap.pethub.dto.response.ExameResponse;
import fiap.pethub.entity.Exame;
import fiap.pethub.entity.Pet;
import fiap.pethub.entity.Responsavel;
import fiap.pethub.exception.AcessoNegadoException;
import fiap.pethub.mapper.ExameMapper;
import fiap.pethub.repository.ConsultaRepository;
import fiap.pethub.repository.ExameRepository;
import fiap.pethub.repository.PetRepository;
import fiap.pethub.security.EscopoDoUsuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Exame é representativo das seis entidades clínicas: todas penduram em pet_id
 * e seguem exatamente esta regra. O que vale aqui vale para consulta,
 * diagnóstico, pedido médico, vacina e leitura de wearable.
 */
@ExtendWith(MockitoExtension.class)
class ExameServicePosseTest {

    @Mock private ExameRepository repository;
    @Mock private ConsultaRepository consultaRepository;
    @Mock private PetRepository petRepository;
    @Mock private ExameMapper mapper;
    @Mock private EscopoDoUsuario escopo;
    @InjectMocks private ExameService service;

    private final Pageable pagina = PageRequest.of(0, 10);

    private Exame exameDoTutor(Long responsavelId) {
        Responsavel dono = Responsavel.builder().id(responsavelId).build();
        Pet pet = Pet.builder().id(1L).responsavel(dono).build();
        return Exame.builder().id(10L).pet(pet).build();
    }

    @Test
    void veterinarioListaTodosOsExames() {
        lenient().when(escopo.ehVeterinario()).thenReturn(true);
        lenient().when(repository.findAll(pagina)).thenReturn(Page.empty());

        service.findAll(null, null, pagina);

        verify(repository).findAll(pagina);
        verify(repository, never()).findByPetResponsavelId(any(), any());
    }

    @Test
    void responsavelListaApenasExamesDosSeusPets() {
        lenient().when(escopo.ehVeterinario()).thenReturn(false);
        lenient().when(escopo.idDoResponsavel()).thenReturn(7L);
        lenient().when(repository.findByPetResponsavelId(7L, pagina)).thenReturn(Page.empty());

        service.findAll(null, null, pagina);

        verify(repository).findByPetResponsavelId(7L, pagina);
        verify(repository, never()).findAll(pagina);
    }

    @Test
    void responsavelNaoLeExameDePetDeOutro() {
        Exame alheio = exameDoTutor(99L);
        lenient().when(repository.findById(10L)).thenReturn(Optional.of(alheio));
        doThrow(new AcessoNegadoException("Recurso não encontrado")).when(escopo).exigirPosse(99L);

        assertThatThrownBy(() -> service.findById(10L))
                .isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void responsavelLeExameDoProprioPet() {
        Exame proprio = exameDoTutor(7L);
        lenient().when(repository.findById(10L)).thenReturn(Optional.of(proprio));
        lenient().when(mapper.toResponse(proprio)).thenReturn(ExameResponse.builder().id(10L).build());

        ExameResponse resposta = service.findById(10L);

        assertThat(resposta.getId()).isEqualTo(10L);
        verify(escopo).exigirPosse(7L);
    }

    @Test
    void responsavelNaoRemoveExameDePetDeOutro() {
        Exame alheio = exameDoTutor(99L);
        lenient().when(repository.findById(10L)).thenReturn(Optional.of(alheio));
        doThrow(new AcessoNegadoException("Recurso não encontrado")).when(escopo).exigirPosse(99L);

        assertThatThrownBy(() -> service.delete(10L))
                .isInstanceOf(AcessoNegadoException.class);

        verify(repository, never()).delete(any());
    }

    @Test
    void filtroPorPetRespeitaAPosse() {
        lenient().when(escopo.ehVeterinario()).thenReturn(false);
        lenient().when(escopo.idDoResponsavel()).thenReturn(7L);
        Pet pet = Pet.builder().id(1L).responsavel(Responsavel.builder().id(99L).build()).build();
        lenient().when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        doThrow(new AcessoNegadoException("Recurso não encontrado")).when(escopo).exigirPosse(99L);

        assertThatThrownBy(() -> service.findAll(1L, null, pagina))
                .isInstanceOf(AcessoNegadoException.class);

        verify(repository, never()).findByPetId(eq(1L), any());
    }
}
