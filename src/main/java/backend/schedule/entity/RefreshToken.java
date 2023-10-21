package backend.schedule.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(name = "REFRESHTOKEN_SEQ_GENERATOR",
        sequenceName = "REFRESHTOKEN_SEQ")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "REFRESHTOKEN_SEQ_GENERATOR")
    @Column(name = "refreshToken_id", updatable = false)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "token")
    private String token;
}
