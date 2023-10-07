package cinema;

import cinema.dto.*;
import cinema.domain.Theater;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TheaterController {

    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @GetMapping("/seats")
    public Theater getSeats() {
        return theaterService.getSeats();
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(
            @RequestParam(name = "password", required = false) String password) {
        try {
            return buildResponse(theaterService.getStats(password));
        } catch (TheaterException e) {
            return ResponseEntity
                    .status(401)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getErrorResponse());
        }
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchase(@RequestBody PurchaseRequest request) {
        try {
           PurchaseResponse response = theaterService.purchaseSeat(request);
            if(response != null) {
                return buildResponse(response);
            }
        } catch(TheaterException e) {
            return buildErrorResponse(e);
        }
        return null;
    }

    @PostMapping("/return")
    public ResponseEntity<?> refund(@RequestBody RefundRequest request) {
        try {
            return buildResponse(theaterService.getTicket(request.token()));
        } catch (TheaterException e) {
                return buildErrorResponse(e);
        }
    }

    private <T> ResponseEntity<?> buildResponse(T body) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    private  ResponseEntity<?> buildErrorResponse(TheaterException e) {
        return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getErrorResponse());
    }


}
