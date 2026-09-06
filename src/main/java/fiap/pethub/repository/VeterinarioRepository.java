package fiap.pethub.repository;

import fiap.pethub.entity.Veterinario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    Optional<Veterinario> findByCrmv(String crmv);
    Optional<Veterinario> findByEmail(String email);
    Page<Veterinario> findByAtivoTrue(Pageable pageable);
    Page<Veterinario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}

