package fiap.pethub.repository;

import fiap.pethub.entity.Responsavel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {
    Optional<Responsavel> findByCpf(String cpf);

    Optional<Responsavel> findByEmail(String email);
    Page<Responsavel> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Responsavel> findByAtivo(Boolean ativo, Pageable pageable);
    Page<Responsavel> findByNomeContainingIgnoreCaseAndAtivo(String nome, Boolean ativo, Pageable pageable);
}

