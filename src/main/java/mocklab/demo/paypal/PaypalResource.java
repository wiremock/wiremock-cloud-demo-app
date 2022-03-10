package mocklab.demo.paypal;

import com.paypal.api.payments.*;
import com.paypal.base.Constants;
import com.paypal.base.rest.APIContext;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mocklab.demo.MockLabDemoApp.DEFAULT_APP_PORT;

@Controller
public class PaypalResource {

    private APIContext paypalApiContext;

    @Value("${backend.http.read-timeout}")
    private int readTimeoutMilliseconds;

    @Value("${paypal.endpoint}")
    private String paypalEndpoint;

    @Value("${paypal.client.id}")
    private String paypalClientId;

    @Value("${paypal.client.secret}")
    private String paypalClientSecret;

    @PostConstruct
    public void init() {
        HttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("mocklab-paypal-demo-1")
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(readTimeoutMilliseconds);

        Map<String, String> paypalConfig = new HashMap<>();

        paypalConfig.put(Constants.ENDPOINT, paypalEndpoint);
        paypalConfig.put(Constants.HTTP_CONNECTION_READ_TIMEOUT, String.valueOf(readTimeoutMilliseconds));

        paypalApiContext = new APIContext(
                paypalClientId,
                paypalClientSecret,
                "sandbox",
                paypalConfig
        );
    }

    @GetMapping("/paypal")
    public String indexPage(Map<String, Object> model) {
        return "paypal";
    }

    @PostMapping("/paypal/create-payment")
    public ResponseEntity<Map<String, Object>> createPayment(@ModelAttribute CreatePaymentRequest form) throws Exception {
        Payment payment = buildPayment(form.getAmount());
        payment = payment.create(paypalApiContext);

        Map<String, Object> data = new HashMap<>();
        data.put("id", payment.getId());
        return ResponseEntity.ok(data);
    }

    @PostMapping("/paypal/execute-payment")
    public ResponseEntity<PaymentResponse> executePayment(@ModelAttribute PaymentRequest form) throws Exception {


        Payment payment = new Payment();
        payment.setId(form.getPaymentID());

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(form.getPayerID());

        Payment executedPayment = payment.execute(paypalApiContext, paymentExecution);

        return ResponseEntity.ok(
                new PaymentResponse(executedPayment.getTransactions().get(0).getAmount().getTotal())
        );
    }

    private Payment buildPayment(String amountValue) {
        Amount amount = new Amount();
        amount.setCurrency("GBP");
        amount.setTotal(amountValue);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:" + DEFAULT_APP_PORT + "/cancel");
        redirectUrls.setReturnUrl("http://localhost:" + DEFAULT_APP_PORT + "/return");
        payment.setRedirectUrls(redirectUrls);

        return payment;
    }

}
