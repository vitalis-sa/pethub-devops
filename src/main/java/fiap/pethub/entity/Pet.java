package fiap.pethub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_PET")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pet_seq")
    @SequenceGenerator(name = "pet_seq", sequenceName = "SQ_PET", allocationSize = 1)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "especie", nullable = false)
    private String especie;

    @Column(name = "raca")
    private String raca;

    @Column(name = "idade")
    private Integer idade;

    @Column(name = "peso")
    private Double peso;

    @Column(name = "genero")
    private String genero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private Responsavel responsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_responsavel_id")
    private Veterinario veterinarioResponsavel;
}
