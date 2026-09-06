package fiap.pethub.repository;

import fiap.pethub.entity.UnidadeVeterinario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadeVeterinarioRepository extends JpaRepository<UnidadeVeterinario, Long> {
    Page<UnidadeVeterinario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<UnidadeVeterinario> findByCidade(String cidade, Pageable pageable);
}
