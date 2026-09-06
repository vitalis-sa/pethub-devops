package fiap.pethub.repository;

import fiap.pethub.entity.Lembrete;
import fiap.pethub.enums.StatusLembrete;
import fiap.pethub.enums.TipoLembrete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LembreteRepository extends JpaRepository<Lembrete, Long> {
    Page<Lembrete> findByResponsavelId(Long responsavelId, Pageable pageable);
    Page<Lembrete> findByPetId(Long petId, Pageable pageable);
    Page<Lembrete> findByResponsavelIdAndStatus(Long responsavelId, StatusLembrete status, Pageable pageable);
    Page<Lembrete> findByResponsavelIdAndTipo(Long responsavelId, TipoLembrete tipo, Pageable pageable);
    Page<Lembrete> findByStatus(StatusLembrete status, Pageable pageable);
}
