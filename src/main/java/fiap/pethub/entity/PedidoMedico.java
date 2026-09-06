package fiap.pethub.entity;

import fiap.pethub.enums.StatusPedidoMedico;
import fiap.pethub.enums.TipoPedidoMedico;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_PEDIDO_MEDICO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedido_seq")
    @SequenceGenerator(name = "pedido_seq", sequenceName = "SQ_PEDIDO_MEDICO", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id", nullable = false)
    private Consulta consulta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoPedidoMedico tipo;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "instrucoes")
    private String instrucoes;

    @Column(name = "data_limite")
    private LocalDate dataLimite;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPedidoMedico status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusPedidoMedico.PENDENTE;
        }
    }
}

