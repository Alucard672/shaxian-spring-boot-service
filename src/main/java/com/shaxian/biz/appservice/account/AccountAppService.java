package com.shaxian.biz.appservice.account;

import com.shaxian.biz.entity.*;
import com.shaxian.biz.service.account.AccountPayableService;
import com.shaxian.biz.service.account.AccountReceivableService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class AccountAppService {

    private final AccountReceivableService accountReceivableService;
    private final AccountPayableService accountPayableService;

    public AccountAppService(AccountReceivableService accountReceivableService,
                             AccountPayableService accountPayableService) {
        this.accountReceivableService = accountReceivableService;
        this.accountPayableService = accountPayableService;
    }

    // receivable
    public List<AccountReceivable> listReceivables(String customerId, String status) {
        Long cid = customerId != null ? parseLong(customerId) : null;
        return accountReceivableService.findReceivables(cid, status);
    }

    public AccountReceivable createReceivable(Map<String, Object> request) {
        AccountReceivable receivable = new AccountReceivable();
        receivable.setCustomerId(parseLong(request.get("customerId")));
        receivable.setCustomerName((String) request.get("customerName"));
        receivable.setSalesOrderId(parseLong(request.get("salesOrderId")));
        receivable.setSalesOrderNumber((String) request.get("salesOrderNumber"));
        receivable.setReceivableAmount(new BigDecimal(request.get("receivableAmount").toString()));
        if (request.containsKey("receivedAmount")) {
            receivable.setReceivedAmount(new BigDecimal(request.get("receivedAmount").toString()));
        }
        receivable.setAccountDate(LocalDate.parse((String) request.get("accountDate")));
        return accountReceivableService.createReceivable(receivable);
    }

    public List<ReceiptRecord> listReceipts(Long receivableId) {
        return accountReceivableService.findReceipts(receivableId);
    }

    public ReceiptRecord createReceipt(Long receivableId, Map<String, Object> request) {
        ReceiptRecord receipt = new ReceiptRecord();
        receipt.setAccountReceivableId(receivableId);
        receipt.setAmount(new BigDecimal(request.get("amount").toString()));
        if (request.containsKey("paymentMethod")) {
            receipt.setPaymentMethod(ReceiptRecord.PaymentMethod.valueOf((String) request.get("paymentMethod")));
        }
        receipt.setReceiptDate(LocalDate.parse((String) request.get("receiptDate")));
        receipt.setOperator((String) request.get("operator"));
        if (request.containsKey("remark")) receipt.setRemark((String) request.get("remark"));
        return accountReceivableService.addReceipt(receivableId, receipt);
    }

    // payable
    public List<AccountPayable> listPayables(String supplierId, String status) {
        Long sid = supplierId != null ? parseLong(supplierId) : null;
        return accountPayableService.findPayables(sid, status);
    }

    public AccountPayable createPayable(Map<String, Object> request) {
        AccountPayable payable = new AccountPayable();
        payable.setSupplierId(parseLong(request.get("supplierId")));
        payable.setSupplierName((String) request.get("supplierName"));
        payable.setPurchaseOrderId(parseLong(request.get("purchaseOrderId")));
        payable.setPurchaseOrderNumber((String) request.get("purchaseOrderNumber"));
        payable.setPayableAmount(new BigDecimal(request.get("payableAmount").toString()));
        if (request.containsKey("paidAmount")) {
            payable.setPaidAmount(new BigDecimal(request.get("paidAmount").toString()));
        }
        payable.setAccountDate(LocalDate.parse((String) request.get("accountDate")));
        return accountPayableService.createPayable(payable);
    }

    public List<PaymentRecord> listPayments(Long payableId) {
        return accountPayableService.findPayments(payableId);
    }

    public PaymentRecord createPayment(Long payableId, Map<String, Object> request) {
        PaymentRecord payment = new PaymentRecord();
        payment.setAccountPayableId(payableId);
        payment.setAmount(new BigDecimal(request.get("amount").toString()));
        if (request.containsKey("paymentMethod")) {
            payment.setPaymentMethod(PaymentRecord.PaymentMethod.valueOf((String) request.get("paymentMethod")));
        }
        payment.setPaymentDate(LocalDate.parse((String) request.get("paymentDate")));
        payment.setOperator((String) request.get("operator"));
        if (request.containsKey("remark")) payment.setRemark((String) request.get("remark"));
        return accountPayableService.addPayment(payableId, payment);
    }

    private Long parseLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ID 参数格式错误", e);
            }
        }
        throw new IllegalArgumentException("不支持的 ID 类型: " + value.getClass());
    }
}
