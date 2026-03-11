package com.example.paymal.services.paymentService;

import com.example.paymal.model.entity.Merchant;
import com.example.paymal.model.entity.Payment;
import com.example.paymal.model.enums.PaymentStatus;
import com.example.paymal.model.request.PaymentReq;
import com.example.paymal.model.response.ApiResponse;
import com.example.paymal.model.response.MerchantRes;
import com.example.paymal.model.response.PaymentRes;
import com.example.paymal.repositories.MerchantRepository;
import com.example.paymal.repositories.PaymentRepository;
import com.example.paymal.exceptions.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MerchantRepository merchantRepository;

    @Override
    public HttpEntity<?> create(String apiKey, PaymentReq req) {
        Merchant merchant = merchantRepository.findByApiKey(apiKey);
        if (merchant == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid API Key"));
        }

        Payment payment = Payment.builder()
                .merchant(merchant)
                .orderId(req.getOrderId())
                .amount(req.getAmount())
                .description(req.getDescription())
                .status(PaymentStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        payment = paymentRepository.save(payment);
        payment.setPaymentUrl("https://paymal.uz/pay/" + payment.getId());
        payment = paymentRepository.save(payment);

        return ResponseEntity.ok(ApiResponse.success(toRes(payment)));
    }

    @Override
    public HttpEntity<?> cancel(String apiKey, UUID id) {
        Merchant merchant = merchantRepository.findByApiKey(apiKey);
        if (merchant == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Invalid API Key"));
        }

        Payment payment = paymentRepository.findByIdAndMerchant(id, merchant)
                .orElseThrow(() -> new CustomException("Payment not found", HttpStatus.NOT_FOUND));

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
                .orElseThrow(() -> new CustomException("Payment not found", HttpStatus.NOT_FOUND));
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
