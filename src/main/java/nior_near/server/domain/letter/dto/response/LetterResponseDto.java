package nior_near.server.domain.letter.dto.response;

import lombok.Builder;
import lombok.Data;
import nior_near.server.domain.letter.entity.Letter;

import java.time.LocalDateTime;

@Data
@Builder
public class LetterResponseDto {

    private long letterId;
    private long senderId;
    private String senderName;
    private String status;
    private String imageUrl;
    private LocalDateTime createAt;

    public static LetterResponseDto of(Letter letter) {

        return LetterResponseDto.builder()
                .letterId(letter.getId())
                .senderId(letter.getSender().getId())
                .senderName(letter.getSender().getNickname())
                .status(letter.getStatus().getValue())
                .imageUrl(letter.getImageLink())
                .createAt(letter.getCreatedAt())
                .build();
    }
}
