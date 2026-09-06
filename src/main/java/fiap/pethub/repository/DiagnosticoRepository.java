package fiap.pethub.repository;

import fiap.pethub.entity.Diagnostico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticoRepository extends JpaRepository<Diagnostico, Long> {
    Page<Diagnostico> findByPetId(Long petId, Pageable pageable);

    /** Todos os registros dos pets de um responsavel. Base do filtro por posse. */
    Page<Diagnostico> findByPetResponsavelId(Long responsavelId, Pageable pageable);
    Page<Diagnostico> findByConsultaId(Long consultaId, Pageable pageable);
}

