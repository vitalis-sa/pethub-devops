package fiap.pethub.entity;

import fiap.pethub.enums.StatusLembrete;
import fiap.pethub.enums.TipoLembrete;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_LEMBRETE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lembrete {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lembrete_seq")
    @SequenceGenerator(name = "lembrete_seq", sequenceName = "SQ_LEMBRETE", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RESPONSAVEL_ID", nullable = false)
    private Responsavel responsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PET_ID", nullable = false)
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false)
    private TipoLembrete tipo;

    @Column(name = "DATA_AGENDADA")
    private LocalDate dataAgendada;

    @Column(name = "MENSAGEM", nullable = false)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    @Builder.Default
    private StatusLembrete status = StatusLembrete.PENDENTE;

    @Column(name = "REFERENCIA_ID")
    private Long referenciaId;

    @Column(name = "REFERENCIA_TIPO")
    private String referenciaTipo;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;
}
