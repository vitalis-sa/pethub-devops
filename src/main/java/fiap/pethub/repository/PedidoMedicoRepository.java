package fiap.pethub.repository;

import fiap.pethub.entity.PedidoMedico;
import fiap.pethub.enums.StatusPedidoMedico;
import fiap.pethub.enums.TipoPedidoMedico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoMedicoRepository extends JpaRepository<PedidoMedico, Long> {
    Page<PedidoMedico> findByPetId(Long petId, Pageable pageable);

    /** Todos os registros dos pets de um responsavel. Base do filtro por posse. */
    Page<PedidoMedico> findByPetResponsavelId(Long responsavelId, Pageable pageable);
    Page<PedidoMedico> findByConsultaId(Long consultaId, Pageable pageable);
    Page<PedidoMedico> findByPetIdAndStatus(Long petId, StatusPedidoMedico status, Pageable pageable);
    Page<PedidoMedico> findByPetIdAndTipo(Long petId, TipoPedidoMedico tipo, Pageable pageable);
}

