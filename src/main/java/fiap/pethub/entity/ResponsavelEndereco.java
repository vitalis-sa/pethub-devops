package fiap.pethub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TB_RESPONSAVEL_ENDERECO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsavelEndereco {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "responsavel_endereco_seq")
    @SequenceGenerator(name = "responsavel_endereco_seq", sequenceName = "SQ_RESPONSAVEL_ENDERECO", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESPONSAVEL_ID", nullable = false)
    private Responsavel responsavel;

    @Column(name = "LOGRADOURO", nullable = false)
    private String logradouro;

    @Column(name = "NUMERO", nullable = false)
    private String numero;

    @Column(name = "COMPLEMENTO")
    private String complemento;

    @Column(name = "BAIRRO", nullable = false)
    private String bairro;

    @Column(name = "CIDADE", nullable = false)
    private String cidade;

    @Column(name = "ESTADO", length = 2, nullable = false)
    private String estado;

    @Column(name = "CEP", length = 8, nullable = false)
    private String cep;

    @Column(name = "PRINCIPAL")
    @Builder.Default
    private Boolean principal = false;
}

