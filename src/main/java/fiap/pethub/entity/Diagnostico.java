package fiap.pethub.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_DIAGNOSTICO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnostico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diag_seq")
    @SequenceGenerator(name = "diag_seq", sequenceName = "SQ_DIAGNOSTICO", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    @Column(name = "sintoma1")
    private String sintoma1;

    @Column(name = "sintoma2")
    private String sintoma2;

    @Column(name = "sintoma3")
    private String sintoma3;

    @Column(name = "sintoma4")
    private String sintoma4;

    @Column(name = "duracao_sintomas")
    private String duracaoSintomas;

    @Column(name = "perda_apetite", nullable = false)
    @Builder.Default
    private Boolean perdaApetite = false;

    @Column(name = "vomito", nullable = false)
    @Builder.Default
    private Boolean vomito = false;

    @Column(name = "diarreia", nullable = false)
    @Builder.Default
    private Boolean diarreia = false;

    @Column(name = "tosse", nullable = false)
    @Builder.Default
    private Boolean tosse = false;

    @Column(name = "dificuldade_respiratoria", nullable = false)
    @Builder.Default
    private Boolean dificuldadeRespiratoria = false;

    @Column(name = "claudicacao", nullable = false)
    @Builder.Default
    private Boolean claudicacao = false;

    @Column(name = "lesoes_pele", nullable = false)
    @Builder.Default
    private Boolean lesoesPele = false;

    @Column(name = "secrecao_nasal", nullable = false)
    @Builder.Default
    private Boolean secrecaoNasal = false;

    @Column(name = "secrecao_ocular", nullable = false)
    @Builder.Default
    private Boolean secrecaoOcular = false;

    @Column(name = "temperatura_corporal")
    private Double temperaturaCorporal;

    @Column(name = "frequencia_cardiaca")
    private Integer frequenciaCardiaca;

    @Column(name = "doenca_predita")
    private String doencaPredita;

    @Column(name = "confianca_predicao")
    private Double confiancaPredicao;

    @Column(name = "analise_gen_ai", length = 2000)
    private String analiseGenAI;
}

