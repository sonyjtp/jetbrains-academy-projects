package cinema;

import cinema.dto.ErrorResponse;
import cinema.dto.PurchaseRequest;
import cinema.domain.Seat;
import cinema.domain.Theater;
import cinema.domain.Ticket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TheaterRepository {

    private final Theater theater;

    public TheaterRepository() {
        theater = new Theater(9, 9, addSeats());
    }

    public Seat purchase(PurchaseRequest request) throws TheaterException {
       Seat found = find(request.row(), request.column());
       found.setAvailable(false);
       found.setToken(UUID.randomUUID().toString());
       theater.setIncome(theater.getIncome()  + found.getPrice());
       theater.setPurchased(theater.getPurchased() + 1);
       return found;
    }

    public Seat find(Integer row, Integer column) throws TheaterException {
        Seat found = theater.getSeats().stream().filter(
                it -> it.getRow().equals(row) &&
                        it.getColumn().equals(column)).findFirst().orElse(null);
        if (found == null || !found.getAvailable()) {
            throw new TheaterException(
                new ErrorResponse("The ticket has been already purchased!")
            );
        }
        return found;
    }

    public Ticket vacateAndRefund(String token) throws TheaterException {
        Seat bookedSeat = theater.getSeats().stream()
                .filter(seat ->
                token.equals(seat.getToken())).findAny().orElse(null);
        if (bookedSeat != null){
            bookedSeat.setAvailable(true);
            bookedSeat.setToken("");
            theater.setPurchased(theater.getPurchased() - 1);
            theater.setIncome(theater.getIncome() -  bookedSeat.getPrice());
            return new Ticket(
                    bookedSeat.getRow(),
                    bookedSeat.getColumn(),
                    bookedSeat.getPrice());
        } else throw new TheaterException(
              new ErrorResponse("Wrong token!")
        );
    }

    private List<Seat> addSeats() {
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j < 10; j++) {
                seats.add(new Seat(i, j, i <=4 ? 10: 8, true));
            }
        }
        return seats;
    }

    public Theater getTheater() {
        return theater;
    }
}
