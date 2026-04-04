package com.example.paymal.services.merchantService;

import com.example.paymal.model.entity.Attachment;
import com.example.paymal.model.entity.Merchant;
import com.example.paymal.model.entity.User;
import com.example.paymal.model.enums.MerchantStatus;
import com.example.paymal.model.request.MerchantReq;
import com.example.paymal.model.response.ApiResponse;
import com.example.paymal.model.response.MerchantRes;
import com.example.paymal.repositories.AttachmentRepository;
import com.example.paymal.repositories.MerchantRepository;
import org.springframework.http.HttpEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final AttachmentRepository attachmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public HttpEntity<?> create(MerchantReq req) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Merchant merchant = Merchant.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .websiteUrl(req.getWebsiteUrl())
                .webhookUrl(req.getWebhookUrl())
                .status(MerchantStatus.PENDING)
                .user(user)
                .apiKey(UUID.randomUUID().toString())
                .apiSecret(UUID.randomUUID().toString())
                .feePercentage(req.getFeePercentage() != null ? req.getFeePercentage() : new java.math.BigDecimal("5.0"))
                .isFeeIncluded(req.getIsFeeIncluded() != null ? req.getIsFeeIncluded() : false)
                .build();

        if (req.getLogoId() != null) {
            Attachment logo = attachmentRepository.findById(req.getLogoId()).orElse(null);
            merchant.setLogo(logo);
        }

        merchantRepository.save(merchant);
        return ResponseEntity.ok(ApiResponse.success(toRes(merchant)));
    }

    @Override
    public HttpEntity<?> update(UUID id, MerchantReq req) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        if (!merchant.getUser().getId().equals(user.getId()) && !user.getRoleName().equals("ROLE_ADMIN")) {
            throw new RuntimeException("Access denied");
        }

        merchant.setTitle(req.getTitle());
        merchant.setDescription(req.getDescription());
        merchant.setWebsiteUrl(req.getWebsiteUrl());
        merchant.setWebhookUrl(req.getWebhookUrl());
        merchant.setIsFeeIncluded(req.getIsFeeIncluded() != null ? req.getIsFeeIncluded() : merchant.getIsFeeIncluded());

        if (user.getRoleName().equals("ROLE_ADMIN") && req.getFeePercentage() != null) {
            merchant.setFeePercentage(req.getFeePercentage());
        }

        if (req.getLogoId() != null) {
            if (merchant.getLogo() != null && !merchant.getLogo().getId().equals(req.getLogoId())) {
                merchant.setLogo(null);
            }
            Attachment logo = attachmentRepository.findById(req.getLogoId()).orElse(null);
            merchant.setLogo(logo);
        }

        merchantRepository.save(merchant);
        return ResponseEntity.ok(ApiResponse.success(toRes(merchant)));
    }

    @Override
    public HttpEntity<?> get(UUID id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
        return ResponseEntity.ok(ApiResponse.success(toRes(merchant)));
    }

    @Override
    public HttpEntity<?> getAll() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Merchant> merchants;

        if (user.getRoleName().equals("ROLE_ADMIN")) {
            merchants = merchantRepository.findAll();
        } else {
            merchants = merchantRepository.findAllByUser(user);
        }

        List<MerchantRes> resList = merchants.stream()
                .map(this::toRes)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(resList));
    }

    @Override
    public HttpEntity<?> delete(UUID id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        if (!merchant.getUser().getId().equals(user.getId()) && !user.getRoleName().equals("ROLE_ADMIN")) {
            throw new RuntimeException("Access denied");
        }

        merchantRepository.delete(merchant);
        return ResponseEntity.ok(ApiResponse.success("Merchant deleted successfully"));
    }

    @Override
    public HttpEntity<?> getSecretKey(UUID id, String password) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        if (!merchant.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(400).body(ApiResponse.error("Parol xato!", 400));
        }

        return ResponseEntity.ok(ApiResponse.success(merchant.getApiSecret()));
    }

    @Override
    @Transactional
    public HttpEntity<?> rotateApiKey(UUID id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        if (!merchant.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        merchant.setApiKey(UUID.randomUUID().toString());
        merchantRepository.save(merchant);
        return ResponseEntity.ok(ApiResponse.success(merchant.getApiKey()));
    }

    @Override
    @Transactional
    public HttpEntity<?> rotateSecretKey(UUID id, String password) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        if (!merchant.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(400).body(ApiResponse.error("Parol xato!", 400));
        }

        merchant.setApiSecret(UUID.randomUUID().toString());
        merchantRepository.save(merchant);
        return ResponseEntity.ok(ApiResponse.success(merchant.getApiSecret()));
    }

    private MerchantRes toRes(Merchant merchant) {
        String imageUrl = null;
        if (merchant.getLogo() != null) {
            imageUrl = "/files/" + merchant.getLogo().getId();
        }

        return MerchantRes.builder()
                .id(merchant.getId())
                .title(merchant.getTitle())
                .description(merchant.getDescription())
                .websiteUrl(merchant.getWebsiteUrl())
                .webhookUrl(merchant.getWebhookUrl())
                .apiKey(merchant.getApiKey())
                .status(merchant.getStatus())
                .imageUrl(imageUrl)
                .feePercentage(merchant.getFeePercentage() != null ? merchant.getFeePercentage() : new java.math.BigDecimal("5.0"))
                .isFeeIncluded(Boolean.TRUE.equals(merchant.getIsFeeIncluded()))
                .build();
    }
}
