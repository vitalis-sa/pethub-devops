package fiap.pethub.entity;

import fiap.pethub.enums.TipoAlertaHidratacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_LEITURA_WEARABLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeituraWearable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leitura_seq")
    @SequenceGenerator(name = "leitura_seq", sequenceName = "SQ_LEITURA_WEARABLE", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PET_ID", nullable = false)
    private Pet pet;

    @Column(name = "TIMESTAMP", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "CONSUMO_ML_REGISTRADO", nullable = false)
    private Double consumoMlRegistrado;

    @Column(name = "CONSUMO_DIARIO_ACUMULADO", nullable = false)
    private Double consumoDiarioAcumulado;

    @Column(name = "META_DIARIA_ML", nullable = false)
    private Double metaDiariaML;

    @Column(name = "PERCENTUAL_META", nullable = false)
    private Double percentualMeta;

    @Column(name = "ALERTA_GERADO", nullable = false)
    @Builder.Default
    private Boolean alertaGerado = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ALERTA")
    private TipoAlertaHidratacao tipoAlerta;

    @Column(name = "DESCRICAO_ALERTA")
    private String descricaoAlerta;
}
