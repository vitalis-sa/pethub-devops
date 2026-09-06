package fiap.pethub.repository;

import fiap.pethub.entity.ResponsavelContato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsavelContatoRepository extends JpaRepository<ResponsavelContato, Long> {
    Page<ResponsavelContato> findByResponsavelId(Long responsavelId, Pageable pageable);
}

