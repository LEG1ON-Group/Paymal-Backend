package com.example.paymal.services.clickService;

import com.example.paymal.model.entity.Merchant;
import com.example.paymal.model.entity.Payment;
import com.example.paymal.model.entity.Transaction;
import com.example.paymal.model.enums.PaymentStatus;
import com.example.paymal.model.enums.ReferenceType;
import com.example.paymal.model.enums.TransactionStatus;
import com.example.paymal.repositories.PaymentRepository;
import com.example.paymal.repositories.TransactionRepository;
import com.example.paymal.services.balance.BalanceService;
import com.example.paymal.services.telegram.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClickWebhookServiceImpl implements ClickWebhookService {

    private final TransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final BalanceService balanceService;
    private final TelegramService telegramService;

    @Value("${click.service.id}")
    private String serviceId;

    @Value("${click.secret.key}")
    private String secretKey;

    @Override
    @Transactional
    public Map<String, Object> prepare(Map<String, String> params) {
        String clickTransId = params.get("click_trans_id");
        String serviceIdParam = params.get("service_id");
        String merchantTransId = params.get("merchant_trans_id"); // Payment ID
        String amount = params.get("amount");
        String action = params.get("action");
        String signTime = params.get("sign_time");
        String signString = params.get("sign_string");

        // VALIDATION
        if (clickTransId == null || serviceIdParam == null || merchantTransId == null || amount == null || action == null || signTime == null || signString == null) {
            log.warn("Click Prepare validation failed: Missing parameters. clickTransId={}, serviceId={}, merchantTransId={}, amount={}, action={}, signTime={}, signString={}",
                    clickTransId, serviceIdParam, merchantTransId, amount, action, signTime, signString);
            return makeResponse(-8, "Missing parameters");
        }

        if (!serviceId.equals(serviceIdParam)) {
            log.warn("Click Prepare validation failed: Incorrect service ID. Expected: {}, Received: {}", serviceId, serviceIdParam);
            return makeResponse(-2, "Incorrect service ID");
        }

        // SIGNATURE VERIFICATION
        String expectedSign = DigestUtils.md5DigestAsHex((clickTransId + serviceIdParam + secretKey + merchantTransId + amount + action + signTime).getBytes());
        if (!expectedSign.equals(signString)) {
            log.warn("Click Prepare Sign Check Failed! Expected: {}, Received: {}, Params used: click_trans_id={}, service_id={}, secret_key (hidden), merchant_trans_id={}, amount={}, action={}, sign_time={}",
                    expectedSign, signString, clickTransId, serviceIdParam, merchantTransId, amount, action, signTime);
            return makeResponse(-1, "SIGN CHECK FAILED");
        }
        log.info("Click Prepare Sign Check PASSED for click_trans_id={}", clickTransId);

        // FIND PAYMENT (merchant_trans_id)
        Optional<Payment> paymentOpt = paymentRepository.findById(UUID.fromString(merchantTransId));
        if (paymentOpt.isEmpty()) {
            log.warn("Click Prepare failed: Payment not found for ID: {}", merchantTransId);
            return makeResponse(-3, "Payment not found");
        }
        Payment payment = paymentOpt.get();

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            log.warn("Click Prepare failed: Already paid. Payment ID: {}", merchantTransId);
            return makeResponse(-4, "Already paid");
        }

        BigDecimal amountDecimal = new BigDecimal(amount);
        if (amountDecimal.compareTo(payment.getAmount()) != 0) {
            return makeResponse(-5, "Amount mismatch");
        }

        Transaction transaction = transactionRepository.findByClickTransId(clickTransId)
                .orElseGet(() -> Transaction.builder()
                        .payment(payment)
                        .paymentProvider(com.example.paymal.model.enums.PaymentProvider.CLICK)
                        .amount(payment.getAmount())
                        .currency(payment.getMerchant().getUser().getFirstName() != null ? com.example.paymal.model.enums.Currency.UZS : com.example.paymal.model.enums.Currency.UZS) // Helper: default UZS
                        .status(TransactionStatus.PENDING)
                        .clickTransId(clickTransId)
                        .clickPaydocId(params.get("click_paydoc_id"))
                        .build());

        transaction.setMerchantPrepareId(transaction.getId() != null ? transaction.getId().toString() : null);
        transaction = transactionRepository.save(transaction);

        if (transaction.getMerchantPrepareId() == null) {
            transaction.setMerchantPrepareId(transaction.getId().toString());
            transaction = transactionRepository.save(transaction);
        }
        log.info("Click Prepare transaction saved/updated: ID={}, Status={}", transaction.getId(), transaction.getStatus());

        Map<String, Object> response = makeResponse(0, "Success");
        response.put("click_trans_id", clickTransId);
        response.put("merchant_trans_id", merchantTransId);
        response.put("merchant_prepare_id", transaction.getId().toString());
        return response;
    }

    @Override
    @Transactional
    public Map<String, Object> complete(Map<String, String> params) {
        String clickTransId = params.get("click_trans_id");
        String serviceIdParam = params.get("service_id");
        String merchantTransId = params.get("merchant_trans_id");
        String merchantPrepareId = params.get("merchant_prepare_id");
        String amount = params.get("amount");
        String action = params.get("action");
        String error = params.get("error");
        String signTime = params.get("sign_time");
        String signString = params.get("sign_string");

        if (clickTransId == null || serviceIdParam == null || merchantTransId == null || merchantPrepareId == null || amount == null || action == null || signTime == null || signString == null) {
            log.warn("Click Complete validation failed: Missing parameters. clickTransId={}, serviceId={}, merchantTransId={}, merchantPrepareId={}, amount={}, action={}, signTime={}, signString={}",
                    clickTransId, serviceIdParam, merchantTransId, merchantPrepareId, amount, action, signTime, signString);
            return makeResponse(-8, "Missing parameters");
        }

        String expectedSign = DigestUtils.md5DigestAsHex((clickTransId + serviceIdParam + secretKey + merchantTransId + merchantPrepareId + amount + action + signTime).getBytes());
        if (!expectedSign.equals(signString)) {
            log.warn("Click Complete Sign Check Failed! Expected: {}, Received: {}, Params: click_trans_id={}, service_id={}, secret_key (hidden), merchant_trans_id={}, merchant_prepare_id={}, amount={}, action={}, sign_time={}",
                    expectedSign, signString, clickTransId, serviceIdParam, merchantTransId, merchantPrepareId, amount, action, signTime);
            return makeResponse(-1, "SIGN CHECK FAILED");
        }
        log.info("Click Complete Sign Check PASSED for click_trans_id={}", clickTransId);

        Optional<Transaction> transactionOpt = transactionRepository.findById(UUID.fromString(merchantPrepareId));
        if (transactionOpt.isEmpty()) {
            log.warn("Click Complete failed: Transaction not found for ID (merchant_prepare_id): {}", merchantPrepareId);
            return makeResponse(-3, "Transaction not found");
        }
        Transaction transaction = transactionOpt.get();
        Payment payment = transaction.getPayment();

        if (payment == null || !payment.getId().toString().equals(merchantTransId)) {
            return makeResponse(-3, "Payment mismatch");
        }

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            return makeResponse(-4, "Already paid");
        }

        if (error != null && Integer.parseInt(error) < 0) {
            log.warn("Click Complete failed: Click returned error={}. click_trans_id={}", error, clickTransId);
            transaction.setStatus(TransactionStatus.FAIL);
            transactionRepository.save(transaction);
            return makeResponse(-9, "Transaction cancelled by Click");
        }

        BigDecimal amountDecimal = new BigDecimal(amount);
        if (amountDecimal.compareTo(payment.getAmount()) != 0) {
            return makeResponse(-5, "Amount mismatch");
        }

        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setMerchantConfirmId(transaction.getId().toString());
        transaction = transactionRepository.save(transaction);
        log.info("Click Complete transaction SUCCESS: ID={}", transaction.getId());

        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        balanceService.updateBalance(payment.getMerchant(), payment.getAmount(), ReferenceType.PAYMENT, null);

        sendSuccessNotification(payment);

        Map<String, Object> response = makeResponse(0, "Success");
        response.put("click_trans_id", clickTransId);
        response.put("merchant_trans_id", merchantTransId);
        response.put("merchant_confirm_id", transaction.getId().toString());
        return response;
    }

    private Map<String, Object> makeResponse(int error, String note) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("error_note", note);
        return response;
    }

    private void sendSuccessNotification(Payment payment) {
        Merchant merchant = payment.getMerchant();
        String message = String.format(
                "✅ <b>Yangi to'lov muvaffaqiyatli amalga oshirildi!</b>\n\n" +
                        "💰 Summa: <code>%s UZS</code>\n" +
                        "🏢 Merchant: <code>%s</code>\n" +
                        "📦 Order ID: <code>%s</code>\n" +
                        "📝 Tavsif: <code>%s</code>\n" +
                        "👤 Foydalanuvchi: <code>%s</code>\n" +
                        "🕒 Vaqt: <code>%s</code>",
                payment.getAmount(),
                merchant.getTitle(),
                payment.getOrderId(),
                payment.getDescription(),
                merchant.getUser().getPhone(),
                java.time.LocalDateTime.now()
        );
        telegramService.sendMessage(message);
    }
}
