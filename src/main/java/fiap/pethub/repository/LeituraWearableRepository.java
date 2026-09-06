package fiap.pethub.repository;

import fiap.pethub.entity.LeituraWearable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface LeituraWearableRepository extends JpaRepository<LeituraWearable, Long> {

    Page<LeituraWearable> findByPetId(Long petId, Pageable pageable);

    /** Todos os registros dos pets de um responsavel. Base do filtro por posse. */
    Page<LeituraWearable> findByPetResponsavelId(Long responsavelId, Pageable pageable);

    Page<LeituraWearable> findByPetIdAndAlertaGeradoTrue(Long petId, Pageable pageable);

    Page<LeituraWearable> findByAlertaGeradoTrue(Pageable pageable);

    @Query("SELECT COALESCE(SUM(l.consumoMlRegistrado), 0.0) FROM LeituraWearable l " +
           "WHERE l.pet.id = :petId AND l.timestamp >= :inicioDia AND l.timestamp < :fimDia")
    Double somarConsumoDiario(@Param("petId") Long petId,
                               @Param("inicioDia") LocalDateTime inicioDia,
                               @Param("fimDia") LocalDateTime fimDia);

    /**
     * Retorna resumo diário paginado agrupado por pet: [Pet, consumoAcumulado (Double), metaDiariaML (Double)]
     */
    @Query("""
            SELECT l.pet, SUM(l.consumoMlRegistrado), MAX(l.metaDiariaML)
            FROM LeituraWearable l
            WHERE l.timestamp >= :inicioDia AND l.timestamp < :fimDia
            GROUP BY l.pet
            """)
    Page<Object[]> resumoDiarioPorPet(@Param("inicioDia") LocalDateTime inicioDia,
                                      @Param("fimDia") LocalDateTime fimDia,
                                      Pageable pageable);
}
