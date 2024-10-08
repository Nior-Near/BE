package nior_near.server.domain.payment.application;

import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import nior_near.server.domain.payment.dto.request.PayStatusRequestDto;
import nior_near.server.domain.payment.dto.request.PaymentCallbackRequest;
import nior_near.server.domain.payment.dto.request.RequestPayDto;
import nior_near.server.domain.payment.dto.response.PayStatusResponseDto;
import nior_near.server.domain.payment.dto.response.PaymentResponseDto;
import nior_near.server.global.common.BaseResponseDto;

public interface PaymentService {
    // 결제 요청 데이터 조회
    RequestPayDto findRequestDto(String Uid);

    // 결제, 콜백
    IamportResponse<Payment> paymentByCallBack(PaymentCallbackRequest request);

    BaseResponseDto<PaymentResponseDto> getPayInfo(Long id);

    BaseResponseDto<PayStatusResponseDto> getPayStatus(PayStatusRequestDto requestDto);
}
