package fiap.pethub.enums;

public enum TipoVacinaTratamento {
    VACINA("Vacina"),
    MEDICAMENTO("Medicamento"),
    PROCEDIMENTO("Procedimento");

    private final String description;

    TipoVacinaTratamento(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

