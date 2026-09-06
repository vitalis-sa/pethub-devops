package fiap.pethub.exception;

/**
 * Lançada quando o usuário autenticado pede um recurso que não é dele.
 *
 * É mapeada para 404, não 403: responder 403 confirmaria que aquele id existe e
 * permitiria enumerar o banco. Mesmo raciocínio do login, que não distingue
 * "senha errada" de "email inexistente".
 */
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
