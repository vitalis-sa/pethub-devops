package fiap.pethub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "TB_UNIDADE_VETERINARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadeVeterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unidade_seq")
    @SequenceGenerator(name = "unidade_seq", sequenceName = "SQ_UNIDADE_VETERINARIO", allocationSize = 1)
    private Long id;

    @OneToMany(mappedBy = "unidade", fetch = FetchType.LAZY)
    private List<Veterinario> veterinarios;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "logradouro")
    private String logradouro;

    @Column(name = "numero")
    private String numero;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "estado", length = 2)
    private String estado;

    @Column(name = "cep", length = 8)
    private String cep;
}
