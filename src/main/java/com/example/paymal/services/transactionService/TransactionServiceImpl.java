package com.example.paymal.services.transactionService;

import com.example.paymal.model.entity.Merchant;
import com.example.paymal.model.entity.Payment;
import com.example.paymal.model.entity.Transaction;
import com.example.paymal.model.enums.PaymentProvider;
import com.example.paymal.model.enums.PaymentStatus;
import com.example.paymal.model.enums.TransactionStatus;
import com.example.paymal.model.request.TransactionReq;
import com.example.paymal.model.response.ApiResponse;
import com.example.paymal.model.response.MerchantRes;
import com.example.paymal.model.response.PaymentRes;
import com.example.paymal.model.response.TransactionRes;
import com.example.paymal.repositories.MerchantRepository;
import com.example.paymal.repositories.PaymentRepository;
import com.example.paymal.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentRepository paymentRepository;

    @Value("${click.service.id}")
    private String clickServiceId;

    @Value("${click.merchant.id}")
    private String clickMerchantId;

    @Value("${click.payment.base.url}")
    private String clickBaseUrl;

    @Override
    public HttpEntity<?> create(TransactionReq req, UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        PaymentProvider provider = PaymentProvider.valueOf(req.getProvider().toUpperCase());

        Transaction transaction = transactionRepository.findByPaymentAndPaymentProvider(payment, provider)
                .orElseGet(() -> {
                    Transaction newTransaction = Transaction.builder()
                            .payment(payment)
                            .paymentProvider(provider)
                            .amount(payment.getAmount())
                            .currency(com.example.paymal.model.enums.Currency.UZS)
                            .status(TransactionStatus.PENDING)
                            .build();
                    return transactionRepository.save(newTransaction);
                });

        if (provider == PaymentProvider.CLICK) {
            if (transaction.getTransactionUrl() == null) {
                transaction.setTransactionUrl(generateClickUrl(payment));
                transaction = transactionRepository.save(transaction);
            }
        }

        return ResponseEntity.ok(ApiResponse.success(toTransactionRes(transaction)));
    }

    private TransactionRes toTransactionRes(Transaction transaction) {
        return TransactionRes.builder()
                .id(transaction.getId())
                .paymentProvider(transaction.getPaymentProvider().name())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency().name())
                .status(transaction.getStatus().name())
                .transactionUrl(transaction.getTransactionUrl())
                .build();
    }

    private String generateClickUrl(Payment payment) {
        String amount = payment.getAmount().toString();
        String merchantTransId = payment.getId().toString();
        String returnUrl = payment.getReturnUrl() != null ? payment.getReturnUrl() : "";

        StringBuilder query = new StringBuilder(String.format(
                "service_id=%s&merchant_id=%s&amount=%s&transaction_param=%s",
                urlEncode(clickServiceId),
                urlEncode(clickMerchantId),
                urlEncode(amount),
                urlEncode(merchantTransId)));

        if (!returnUrl.isEmpty()) {
            query.append("&return_url=").append(urlEncode(returnUrl));
        }

        return clickBaseUrl + "?" + query;
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return value;
        }
    }

    @Override
    public HttpEntity<?> cancel(String apiKey, UUID id) {
        Merchant merchant = merchantRepository.findByApiKey(apiKey);
        if (merchant == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid API Key"));
        }

        Payment payment = paymentRepository.findByIdAndMerchant(id, merchant)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            String message;
            switch (payment.getStatus()) {
                case COMPLETED -> message = "bu tolov allaqachon to'langan";
                case FAILED -> message = "tolovni tolashda xatolik bolgan";
                case EXPIRED -> message = "tolov muddati yaroqsiz";
                case CANCELLED -> message = "bu tolov allaqachon bekor qilingan";
                default -> message = "tolovni bekor qilib bolmaydi";
            }
            return ResponseEntity.status(400).body(ApiResponse.error(message, 400));
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        return ResponseEntity.ok(ApiResponse.success(toRes(payment)));
    }

    @Override
    public HttpEntity<?> get(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return ResponseEntity.ok(ApiResponse.success(toRes(payment)));
    }

    private MerchantRes toMerchantRes(Merchant merchant) {
        if (merchant == null) return null;

        return MerchantRes.builder()
                .id(merchant.getId())
                .websiteUrl(merchant.getWebsiteUrl())
                .title(merchant.getTitle())
                .status(merchant.getStatus())
                .imageUrl(merchant.getLogo() != null ? "/files/" + merchant.getLogo().getId() : null)
                .build();
    }

    private PaymentRes toRes(Payment payment) {
        return PaymentRes.builder()
                .id(payment.getId())
                .merchant(toMerchantRes(payment.getMerchant()))
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .status(payment.getStatus().name())
                .expiresAt(payment.getExpiresAt().toString())
                .paymentUrl(payment.getPaymentUrl())
                .returnUrl(payment.getReturnUrl())
                .build();
    }
}
