package fiap.pethub.entity;

import fiap.pethub.enums.TipoVacinaTratamento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "TB_VACINA_TRATAMENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VacinaTratamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vacina_seq")
    @SequenceGenerator(name = "vacina_seq", sequenceName = "SQ_VACINA_TRATAMENTO", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoVacinaTratamento tipo;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "data_aplicacao", nullable = false)
    private LocalDate dataAplicacao;

    @Column(name = "proxima_dose")
    private LocalDate proximaDose;

    @Column(name = "dose")
    private String dose;

    @Column(name = "observacoes")
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;
}

