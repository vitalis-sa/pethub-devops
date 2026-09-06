package fiap.pethub.entity;

import fiap.pethub.enums.StatusConsulta;
import fiap.pethub.enums.TipoConsulta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_CONSULTA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consulta_seq")
    @SequenceGenerator(name = "consulta_seq", sequenceName = "SQ_CONSULTA", allocationSize = 1)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoConsulta tipo;

    @Column(name = "observacoes")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusConsulta status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private UnidadeVeterinario unidade;
}

