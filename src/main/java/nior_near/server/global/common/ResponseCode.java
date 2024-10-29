package nior_near.server.global.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nior_near.server.global.error.GeneralException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum ResponseCode {

    // 정상 code
    OK(HttpStatus.OK,"2000", "Ok"),

    // Common Error
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON000", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON001","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON002","권한이 잘못되었습니다"),
    _METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON003", "지원하지 않는 Http Method 입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON004", "금지된 요청입니다."),

    // Member Error
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),

    // Review Error
    REVIEW_ALREADY_FOUND(HttpStatus.BAD_REQUEST, "REVIEW4001", "지정된 리뷰의 개수를 초과했습니다."),

    // Token Error
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "TOKEN4001", "헤더에 토큰 값이 없습니다"),
    TOKEN_PREFIX_VALUE_EXCEPTION(HttpStatus.BAD_REQUEST, "TOKEN4002", "토큰 prefix값이 잘못되었습니다 [naver_]"),
    BEARER_PREFIX_VALUE_EXCEPTION(HttpStatus.BAD_REQUEST, "TOKEN4002", "토큰 prefix값이 잘못되었습니다 [Bearer ]"),

    // Coupon Error
    COUPON_NOT_FOUND(HttpStatus.BAD_REQUEST, "COUPON4001", "쿠폰이 없습니다."),

    // AWS S3 Error
    S3_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, "S34001", "파일 업로드에 실패했습니다."),
    S3_PATH_NOT_FOUND(HttpStatus.BAD_REQUEST, "S34003", "파일이 존재하지 않습니다."),

    // Store Error
    STORE_UNAUTHORIZED(HttpStatus.BAD_REQUEST, "STORE4001", "해당 STORE 에 접근할 권한이 없습니다."),
    PAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "STORE4002", "없는 페이지입니다."),
    STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, "STORE4003", "존재하지 않는 상점입니다."),
    ALREADY_HEART_EXCEPTION(HttpStatus.BAD_REQUEST, "STORE4004", "이미 관심 상점으로 등록되었습니다."),
    NOT_HEART_EXCEPTION(HttpStatus.BAD_REQUEST, "STORE4005", "해당 상점은 관심 상점으로 등록되지 않았으므로 삭제할 수 없습니다."),
    PAGE_AND_SIZE_NOT_CORRECT(HttpStatus.BAD_REQUEST, "STORE4006", "page 와 size 는 0 과 양수만 입력 가능합니다."),
    STORE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "STORE4007", "이미 해당 계정의 요리사가 존재합니다."),

    // Order Error
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE4001", "존재하지 않는 주문입니다."),
    ORDER_UNAUTHORIZED(HttpStatus.BAD_REQUEST, "STORE4002", "해당 주문에 접근할 권한이 없습니다."),

    // Payment Error
    PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PAYMENT4001", "존재하지 않는 결제요청입니다."),
    NOT_PAID(HttpStatus.BAD_REQUEST, "PAYMENT4002", "미완료된 결제 내역입니다."),
    SUSPICIOUS_PRICE(HttpStatus.BAD_REQUEST, "PAYMENT4002", "결제금액 위변조가 의심됩니다."),

    // AuthCategory Error
    AUTH_NOT_FOUND(HttpStatus.BAD_REQUEST, "AUTH4001", "존재하지 않는 인증입니다."),

    // Menu Error
    MENU_NOT_FOUND(HttpStatus.BAD_REQUEST, "MENU4001", "해당 메뉴는 메뉴 목록에 없는 메뉴입니다."),

    // Region Error
    REGION_NOT_FOUND(HttpStatus.NOT_FOUND, "REGION4001", "카테고리에 없는 지역입니다."),

    // Place Error
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE4001", "카테고리에 없는 픽업 장소입니다."),

    // Letter Error
    LETTER_NOT_FOUND(HttpStatus.NOT_FOUND, "LETTER4001", "편지가 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public String getMessage(Throwable e) {
        return this.getMessage(this.getMessage() + " - " + e.getMessage());
        // 결과 예시 - "Validation error - Reason why it isn't valid"
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.getMessage());
    }

    public static ResponseCode valueOf(HttpStatus httpStatus) {
        if(httpStatus == null) {
            throw new GeneralException("HttpStatus is null.");
        }

        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getHttpStatus() == httpStatus)
                .findFirst()
                .orElseGet(() -> {
                    if(httpStatus.is4xxClientError()) {
                        return ResponseCode._BAD_REQUEST;
                    } else if (httpStatus.is5xxServerError()) {
                        return ResponseCode._INTERNAL_SERVER_ERROR;
                    } else {
                        return ResponseCode.OK;
                    }
                });
    }
}
