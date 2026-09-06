package fiap.pethub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TB_RESPONSAVEL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Responsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "responsavel_seq")
    @SequenceGenerator(name = "responsavel_seq", sequenceName = "SQ_RESPONSAVEL", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME", length = 150, nullable = false)
    private String nome;

    @Column(name = "CPF", length = 11, nullable = false, unique = true)
    private String cpf;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "SENHA", nullable = false)
    private String senha;

    @Column(name = "ATIVO")
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "responsavel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResponsavelEndereco> enderecos;

    @OneToMany(mappedBy = "responsavel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResponsavelContato> contatos;
}

