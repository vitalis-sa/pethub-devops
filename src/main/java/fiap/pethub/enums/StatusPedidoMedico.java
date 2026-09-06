package fiap.pethub.enums;

public enum StatusPedidoMedico {
    PENDENTE("Pendente"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String description;

    StatusPedidoMedico(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

