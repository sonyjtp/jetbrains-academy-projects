package cinema;

import cinema.dto.ErrorResponse;

public class TheaterException extends Exception {

    private final ErrorResponse errorResponse;

    public TheaterException(ErrorResponse errorResponse) {
        super(errorResponse.error());
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
