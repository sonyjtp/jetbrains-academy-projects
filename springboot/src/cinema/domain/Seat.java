package cinema.domain;

import cinema.dto.PurchaseResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Seat {

    private final Integer row;
    private final Integer column;
    private final Integer price;

    @JsonIgnore
    private Boolean available;

    @JsonIgnore
    private String token;

    public Seat(Integer row, Integer column, Integer price, Boolean available) {
        this.row = row;
        this.column = column;
        this.price = price;
        this.available = available;
    }

    public PurchaseResponse toPurchaseResponse() {
        return new PurchaseResponse(
                this.token,
                new Ticket(this.row, this.column, this.price)
        );
    }

    public Integer getRow() {
        return row;
    }


    public Integer getColumn() {
        return column;
    }

    public Integer getPrice() {
        return price;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
