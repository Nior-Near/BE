package nior_near.server.domain.letter.api;

import lombok.RequiredArgsConstructor;
import nior_near.server.domain.letter.application.LetterService;
import nior_near.server.domain.letter.dto.request.ThankLetterRequestDto;
import nior_near.server.domain.letter.dto.response.LetterResponseDto;
import nior_near.server.domain.letter.dto.response.ThankLetterResponseDto;
import nior_near.server.global.common.BaseResponseDto;
import nior_near.server.global.common.ResponseCode;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/letters")
public class LetterController {

    private final LetterService letterService;
    private final int MAILBOX_PAGE_LETTER_LIMIT = 9;

    // @Operation(summary = "편지함 전체 조회")
    @GetMapping
    BaseResponseDto<List<LetterResponseDto>> getAllLetters(@RequestParam(defaultValue = "0") int page) {

        return BaseResponseDto.onSuccess(letterService.getAllLetters(page, MAILBOX_PAGE_LETTER_LIMIT), ResponseCode.OK);
    }

    // @Operation(summary = "편지함 전체 조회")
    @PutMapping("/{letterId}")
    BaseResponseDto<Long> updateLetterStatus(@PathVariable Long letterId) {

        return BaseResponseDto.onSuccess(letterService.updateLetterStatus(letterId), ResponseCode.OK);
    }

    // @Operation(summary = "감사 편지 작성")
    @PostMapping("/thank")
    BaseResponseDto<ThankLetterResponseDto> addThankLetter(@RequestBody ThankLetterRequestDto thankLetterDto) {

        return BaseResponseDto.onSuccess(letterService.registerThankLetter(thankLetterDto), ResponseCode.OK);
    }
}