package kusitms.gallae.dto.user;


import kusitms.gallae.global.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {
    private Long id;
    private String nickName;
    private String email;
    private String name;
    private String imageUrl;
    private String phoneNumber;
    private Role role;
    private String tokenType;
    private String accessToken;
    private String refreshToken;

    @Builder
    public LoginResponse(Long id, String name, String phoneNumber, String nickName, String email, String imageUrl, Role role, String tokenType, String accessToken, String refreshToken) {
        this.id = id;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = role;
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}