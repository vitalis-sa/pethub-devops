package fiap.pethub.enums;

/**
 * Tipos de usuário do PetHub. A diferença de permissão entre eles reflete o
 * domínio: o veterinário produz o prontuário, o responsável o consulta.
 */
public enum Perfil {

    /** Produz prontuário: consultas, diagnósticos, exames, prescrições e vacinas. */
    VETERINARIO,

    /** Tutor do animal. Consulta o histórico e gerencia os próprios lembretes. */
    RESPONSAVEL;

    /**
     * Nome da authority como o Spring Security a espera. O prefixo {@code ROLE_}
     * é a convenção que {@code hasRole(...)} assume ao comparar.
     */
    public String authority() {
        return "ROLE_" + name();
    }
}
