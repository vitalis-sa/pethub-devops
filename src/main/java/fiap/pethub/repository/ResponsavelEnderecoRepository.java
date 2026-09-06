package fiap.pethub.repository;

import fiap.pethub.entity.ResponsavelEndereco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsavelEnderecoRepository extends JpaRepository<ResponsavelEndereco, Long> {
    Page<ResponsavelEndereco> findByResponsavelId(Long responsavelId, Pageable pageable);
}

