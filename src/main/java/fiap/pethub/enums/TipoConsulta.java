package fiap.pethub.enums;

public enum TipoConsulta {
    PRESENCIAL("Presencial"),
    TELECONSULTA("Teleconsulta");

    private final String description;

    TipoConsulta(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

