package mocklab.demo.paypal;

public class PaymentResponse {

    private final String amount;

    public PaymentResponse(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }
}
