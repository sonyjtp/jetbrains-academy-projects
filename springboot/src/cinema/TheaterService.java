package cinema;

import cinema.domain.Theater;
import cinema.dto.*;
import org.springframework.stereotype.Service;

@Service
public class TheaterService {

    private final TheaterRepository theaterRepository;

    public TheaterService(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    public Theater getSeats() {
        return theaterRepository.getTheater();
    }

    public IncomeResponse getStats(String password) throws TheaterException {
        if ("super_secret".equalsIgnoreCase(password)) {
            Theater theater = getSeats();
            int seats = theater.getRows() * theater.getColumns();
            return new IncomeResponse(
                    theater.getIncome(),
                    seats - theater.getPurchased(),
                    theater.getPurchased()
            );
        } else throw new TheaterException(
                new ErrorResponse("The password is wrong!")
        );
    }

    public PurchaseResponse purchaseSeat(PurchaseRequest request) throws  TheaterException{
        Theater theater = getSeats();
        if (isNotBetween(request.row(), theater.getRows())
                || isNotBetween(request.column(), theater.getColumns())) {
            throw new TheaterException(
                    new ErrorResponse("The number of a row or a column is out of bounds!")
            );
        }
        return theaterRepository.purchase(request).toPurchaseResponse();
    }

    public RefundResponse getTicket(String token) throws TheaterException {
        return theaterRepository.vacateAndRefund(token).toRefundResponse();
    }

    private boolean isNotBetween(int a, int upper) {
        return a < 1 || a > upper;
    }



}
