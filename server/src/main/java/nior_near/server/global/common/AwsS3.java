package nior_near.server.global.common;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AwsS3 {

    private String key;
    private String path;

}
