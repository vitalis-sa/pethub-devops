package fiap.pethub.enums;

public enum StatusConsulta {
    AGENDADA("Agendada"),
    REALIZADA("Realizada"),
    CANCELADA("Cancelada");

    private final String description;

    StatusConsulta(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

