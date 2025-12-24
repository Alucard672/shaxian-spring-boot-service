package com.shaxian.biz.service.account;

import com.shaxian.biz.entity.AccountPayable;
import com.shaxian.biz.entity.PaymentRecord;
import com.shaxian.biz.repository.AccountPayableRepository;
import com.shaxian.biz.repository.PaymentRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AccountPayableService {

    private final AccountPayableRepository accountPayableRepository;
    private final PaymentRecordRepository paymentRecordRepository;

    public AccountPayableService(AccountPayableRepository accountPayableRepository,
                                 PaymentRecordRepository paymentRecordRepository) {
        this.accountPayableRepository = accountPayableRepository;
        this.paymentRecordRepository = paymentRecordRepository;
    }

    public List<AccountPayable> findPayables(Long supplierId, String status) {
        return accountPayableRepository.findByFilters(supplierId, status);
    }

    @Transactional
    public AccountPayable createPayable(AccountPayable payable) {
        BigDecimal paidAmount = payable.getPaidAmount() != null ? payable.getPaidAmount() : BigDecimal.ZERO;
        payable.setPaidAmount(paidAmount);
        BigDecimal unpaidAmount = payable.getPayableAmount().subtract(paidAmount);
        payable.setUnpaidAmount(unpaidAmount);
        payable.setStatus(unpaidAmount.compareTo(BigDecimal.ZERO) > 0 ?
                AccountPayable.AccountStatus.UNPAID : AccountPayable.AccountStatus.PAID);
        if (payable.getAccountDate() == null) {
            payable.setAccountDate(LocalDate.now());
        }
        return accountPayableRepository.save(payable);
    }

    public List<PaymentRecord> findPayments(Long payableId) {
        AccountPayable payable = accountPayableRepository.findById(payableId)
                .orElseThrow(() -> new IllegalArgumentException("应付账款不存在或无权访问"));
        return paymentRecordRepository.findByAccountPayableIdOrderByPaymentDateDesc(payableId);
    }

    @Transactional
    public PaymentRecord addPayment(Long payableId, PaymentRecord payment) {
        AccountPayable payable = accountPayableRepository.findById(payableId)
                .orElseThrow(() -> new IllegalArgumentException("应付账款不存在或无权访问"));
        
        payment.setAccountPayableId(payableId);
        PaymentRecord saved = paymentRecordRepository.save(payment);

        BigDecimal newPaidAmount = payable.getPaidAmount().add(payment.getAmount());
        payable.setPaidAmount(newPaidAmount);
        BigDecimal newUnpaidAmount = payable.getPayableAmount().subtract(newPaidAmount);
        payable.setUnpaidAmount(newUnpaidAmount);
        payable.setStatus(newUnpaidAmount.compareTo(BigDecimal.ZERO) > 0 ?
                AccountPayable.AccountStatus.UNPAID : AccountPayable.AccountStatus.PAID);
        accountPayableRepository.save(payable);

        return saved;
    }
}
