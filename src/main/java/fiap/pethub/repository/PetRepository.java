package fiap.pethub.repository;

import fiap.pethub.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
    Page<Pet> findByResponsavelId(Long responsavelId, Pageable pageable);
    Page<Pet> findByVeterinarioResponsavelId(Long veterinarioId, Pageable pageable);
    Page<Pet> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    /** Busca por nome dentro dos pets de um responsavel. */
    Page<Pet> findByNomeContainingIgnoreCaseAndResponsavelId(String nome, Long responsavelId, Pageable pageable);

    /** Pets de um veterinario, restritos a um responsavel. */
    Page<Pet> findByVeterinarioResponsavelIdAndResponsavelId(Long veterinarioId, Long responsavelId, Pageable pageable);
}

