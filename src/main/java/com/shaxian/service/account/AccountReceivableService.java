package com.shaxian.service.account;

import com.shaxian.entity.AccountReceivable;
import com.shaxian.entity.ReceiptRecord;
import com.shaxian.repository.AccountReceivableRepository;
import com.shaxian.repository.ReceiptRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AccountReceivableService {

    private final AccountReceivableRepository accountReceivableRepository;
    private final ReceiptRecordRepository receiptRecordRepository;

    public AccountReceivableService(AccountReceivableRepository accountReceivableRepository,
                                    ReceiptRecordRepository receiptRecordRepository) {
        this.accountReceivableRepository = accountReceivableRepository;
        this.receiptRecordRepository = receiptRecordRepository;
    }

    public List<AccountReceivable> findReceivables(Long customerId, String status) {
        return accountReceivableRepository.findByFilters(customerId, status);
    }

    @Transactional
    public AccountReceivable createReceivable(AccountReceivable receivable) {
        BigDecimal receivedAmount = receivable.getReceivedAmount() != null ? receivable.getReceivedAmount() : BigDecimal.ZERO;
        receivable.setReceivedAmount(receivedAmount);
        BigDecimal unpaidAmount = receivable.getReceivableAmount().subtract(receivedAmount);
        receivable.setUnpaidAmount(unpaidAmount);
        receivable.setStatus(unpaidAmount.compareTo(BigDecimal.ZERO) > 0 ?
                AccountReceivable.AccountStatus.UNPAID : AccountReceivable.AccountStatus.PAID);
        if (receivable.getAccountDate() == null) {
            receivable.setAccountDate(LocalDate.now());
        }
        return accountReceivableRepository.save(receivable);
    }

    public List<ReceiptRecord> findReceipts(Long receivableId) {
        return receiptRecordRepository.findByAccountReceivableIdOrderByReceiptDateDesc(receivableId);
    }

    @Transactional
    public ReceiptRecord addReceipt(Long receivableId, ReceiptRecord receipt) {
        receipt.setAccountReceivableId(receivableId);
        ReceiptRecord saved = receiptRecordRepository.save(receipt);

        AccountReceivable receivable = accountReceivableRepository.findById(receivableId).orElseThrow();
        BigDecimal newReceivedAmount = receivable.getReceivedAmount().add(receipt.getAmount());
        receivable.setReceivedAmount(newReceivedAmount);
        BigDecimal newUnpaidAmount = receivable.getReceivableAmount().subtract(newReceivedAmount);
        receivable.setUnpaidAmount(newUnpaidAmount);
        receivable.setStatus(newUnpaidAmount.compareTo(BigDecimal.ZERO) > 0 ?
                AccountReceivable.AccountStatus.UNPAID : AccountReceivable.AccountStatus.PAID);
        accountReceivableRepository.save(receivable);

        return saved;
    }
}
