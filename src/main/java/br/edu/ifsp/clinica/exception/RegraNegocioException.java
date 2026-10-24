package br.edu.ifsp.clinica.exception;

/** Lançada quando a operação viola uma regra de negócio (HTTP 409). */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
