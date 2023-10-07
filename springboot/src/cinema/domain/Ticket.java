package cinema.domain;

import cinema.dto.RefundResponse;

public record Ticket(Integer row, Integer column, Integer price) {
    public RefundResponse toRefundResponse() {
        return new RefundResponse(this);
    }
}
