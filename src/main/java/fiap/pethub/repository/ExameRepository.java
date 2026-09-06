package fiap.pethub.repository;

import fiap.pethub.entity.Exame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExameRepository extends JpaRepository<Exame, Long> {
    Page<Exame> findByPetId(Long petId, Pageable pageable);

    /** Todos os registros dos pets de um responsavel. Base do filtro por posse. */
    Page<Exame> findByPetResponsavelId(Long responsavelId, Pageable pageable);
    Page<Exame> findByConsultaId(Long consultaId, Pageable pageable);
}

