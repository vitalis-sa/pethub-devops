package fiap.pethub.exception;

/**
 * Um email só pode existir em um dos cadastros. Se existisse nos dois, o perfil
 * do usuário — e portanto o que ele pode fazer — ficaria indefinido.
 */
public class EmailJaCadastradoException extends RuntimeException {

    public EmailJaCadastradoException(String email) {
        super("O email %s já está cadastrado no sistema".formatted(email));
    }
}
