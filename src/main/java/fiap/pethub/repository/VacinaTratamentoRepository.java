package fiap.pethub.repository;

import fiap.pethub.entity.VacinaTratamento;
import fiap.pethub.enums.TipoVacinaTratamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacinaTratamentoRepository extends JpaRepository<VacinaTratamento, Long> {
    Page<VacinaTratamento> findByPetId(Long petId, Pageable pageable);

    /** Todos os registros dos pets de um responsavel. Base do filtro por posse. */
    Page<VacinaTratamento> findByPetResponsavelId(Long responsavelId, Pageable pageable);
    Page<VacinaTratamento> findByPetIdAndTipo(Long petId, TipoVacinaTratamento tipo, Pageable pageable);
}

