package fiap.pethub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "TB_VETERINARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vet_seq")
    @SequenceGenerator(name = "vet_seq", sequenceName = "SQ_VETERINARIO", allocationSize = 1)
    private Long id;

    @NotBlank
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank
    @Column(name = "crmv", nullable = false, unique = true)
    private String crmv;

    @Column(name = "especialidade")
    private String especialidade;

    @NotBlank
    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "telefone")
    private String telefone;

    @NotBlank
    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private UnidadeVeterinario unidade;
}

