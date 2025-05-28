package com.cntt2.logistics.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VNPayConfig {
    public static String tmnCode =  "L1RMQ2OT";

    public static String hashSecret = "VR9WTSQELPRZHRN84SP8U8SJDBD9J1MA";

    public static String payUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    public static String returnUrl = "http://localhost:5173/payment/vnpay-return";

    public String getTmnCode() { return tmnCode; }
    public String getHashSecret() { return hashSecret; }
    public String getPayUrl() { return payUrl; }
    public String getReturnUrl() { return returnUrl; }

}
