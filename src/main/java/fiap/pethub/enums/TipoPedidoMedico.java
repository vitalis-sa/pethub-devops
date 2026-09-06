package fiap.pethub.enums;

public enum TipoPedidoMedico {
    EXAME("Exame"),
    MEDICAMENTO("Medicamento");

    private final String description;

    TipoPedidoMedico(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

