package nior_near.server.domain.letter.application;

import lombok.RequiredArgsConstructor;
import nior_near.server.domain.letter.dto.request.ThankLetterRequestDto;
import nior_near.server.domain.letter.dto.response.LetterResponseDto;
import nior_near.server.domain.letter.dto.response.ThankLetterResponseDto;
import nior_near.server.domain.letter.entity.Letter;
import nior_near.server.domain.letter.entity.LetterStatus;
import nior_near.server.domain.letter.exception.handler.LetterExceptionHandler;
import nior_near.server.domain.letter.repository.LetterRepository;
import nior_near.server.domain.user.entity.Member;
import nior_near.server.domain.user.exception.handler.MemberExceptionHandler;
import nior_near.server.domain.user.repository.MemberRepository;
import nior_near.server.global.common.ResponseCode;
import nior_near.server.global.util.SmsService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LetterServiceImpl implements LetterService {

    private final LetterRepository letterRepository;
    private final MemberRepository memberRepository;
    private final SmsService smsService;

    @Override
    public List<LetterResponseDto> getAllLetters(int page, int limit) {

        long memberId = 11L;
        /*
        Member member = memberRepository.findByName(memberName)
                .orElseThrow(() -> new MemberExceptionHandler(ResponseCode.MEMBER_NOT_FOUND));
         */

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberExceptionHandler(ResponseCode.MEMBER_NOT_FOUND));

        LocalDateTime startDate = LocalDateTime.now().minusYears(1);
        Pageable pageable = PageRequest.of(page, limit);

        // 해당 userId가 receiver이고 생성일이 1년이 되지 않았고, 그리고 최대 LIMIT 개의 편지를 조회
        List<Letter> letters = letterRepository.findAllByReceiverId(member.getId(), startDate, pageable);

        return letters.stream()
                .map(LetterResponseDto::of)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ThankLetterResponseDto registerThankLetter(ThankLetterRequestDto thankLetterDto) {

        long memberId = 11L;
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberExceptionHandler(ResponseCode.MEMBER_NOT_FOUND));
        /*
        Member member = memberRepository.findByName(memberName)
                .orElseThrow(() -> new MemberExceptionHandler(ResponseCode.MEMBER_NOT_FOUND));

         */

        Member receiver = memberRepository.findById(thankLetterDto.getReceiverId())
                .orElseThrow(() -> new MemberExceptionHandler(ResponseCode.MEMBER_NOT_FOUND));

        Letter letter = Letter.builder()
                .senderName(member.getNickname())
                .text(thankLetterDto.getContent())
                .status(LetterStatus.UNREAD)
                .sender(member)
                .receiver(receiver)
                .build();

        Letter updatedLetter = letterRepository.save(letter);

        smsService.sendLetterMessage(member, receiver, thankLetterDto.getContent());

        return ThankLetterResponseDto.builder()
                .letterId(updatedLetter.getId())
                .build();
    }

    @Override
    @Transactional
    public Long updateLetterStatus(Long letterId) {

        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new LetterExceptionHandler(ResponseCode.LETTER_NOT_FOUND));

        letter.setStatus(LetterStatus.READ);

        return letterRepository.save(letter).getId();
    }
}
