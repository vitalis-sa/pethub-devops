package fiap.pethub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_RESPONSAVEL_CONTATO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsavelContato {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "responsavel_contato_seq")
    @SequenceGenerator(name = "responsavel_contato_seq", sequenceName = "SQ_RESPONSAVEL_CONTATO", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESPONSAVEL_ID", nullable = false)
    private Responsavel responsavel;

    @Column(name = "TIPO", nullable = false)
    private String tipo;

    @Column(name = "TELEFONE", nullable = false)
    private String telefone;

    @Column(name = "PRINCIPAL")
    @Builder.Default
    private Boolean principal = false;
}

