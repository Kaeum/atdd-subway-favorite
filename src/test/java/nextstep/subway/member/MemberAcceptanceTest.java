package nextstep.subway.member;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.member.MemberSteps.*;
import static org.assertj.core.api.Assertions.assertThat;


public class MemberAcceptanceTest extends AcceptanceTest {
    private static final String DUMMY_TOKEN = "i1am2not3your4token";
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final String NEW_EMAIL = "newemail@email.com";
    public static final String NEW_PASSWORD = "newpassword";
    public static final int AGE = 20;
    public static final int NEW_AGE = 21;

    @DisplayName("회원 정보를 관리한다.")
    @Test
    void manageMember() {
        // when
        ExtractableResponse<Response> createResponse = 회원_생성_요청(EMAIL, PASSWORD, AGE);

        // then
        회원_생성됨(createResponse);

        // when
        ExtractableResponse<Response> readResponse = 회원_정보_조회_요청(createResponse);

        // then
        회원_정보_조회됨(readResponse, EMAIL, AGE);

        // when
        ExtractableResponse<Response> updateResponse = 회원_정보_수정_요청(createResponse, "new" + EMAIL, "new" + PASSWORD, AGE);

        // then
        회원_정보_수정됨(updateResponse);

        // when
        ExtractableResponse<Response> deleteResponse = 회원_삭제_요청(createResponse);

        // then
        회원_삭제됨(deleteResponse);
    }

    @DisplayName("나의 정보를 관리한다.")
    @Test
    void manageMyInfo() {
        // when
        ExtractableResponse<Response> createResponse = 회원_생성_요청(EMAIL, PASSWORD, AGE);

        // then
        회원_생성됨(createResponse);

        // given
        TokenResponse tokenResponse = 로그인_되어_있음(EMAIL, PASSWORD);
        TokenResponse fakeTokenResponse = new TokenResponse(DUMMY_TOKEN);

        // when
        ExtractableResponse<Response> readResponse = 내_회원_정보_조회_요청(tokenResponse);

        // then
        회원_정보_조회됨(readResponse, EMAIL, AGE);

        // when
        ExtractableResponse<Response> updateResponse = 내_회원_정보_수정_요청(tokenResponse, NEW_EMAIL, NEW_PASSWORD, NEW_AGE);

        // then
        회원_정보_수정됨(updateResponse);

        // given
        ExtractableResponse<Response> deleteResponse = 내_회원_정보_삭제_요청(tokenResponse);

        // then
        회원_삭제됨(deleteResponse);

        // when
        ExtractableResponse<Response> fakeReadResponse = 내_회원_정보_조회_요청(fakeTokenResponse);

        // then
        요청_실패함(fakeReadResponse);

        // when
        ExtractableResponse<Response> fakeUpdateResponse = 내_회원_정보_수정_요청(fakeTokenResponse, NEW_EMAIL, NEW_PASSWORD, NEW_AGE);

        // then
        요청_실패함(fakeUpdateResponse);

        // when
        ExtractableResponse<Response> fakeDeleteResponse = 내_회원_정보_삭제_요청(fakeTokenResponse);

        // then
        요청_실패함(fakeDeleteResponse);
    }

    private void 요청_실패함(ExtractableResponse<Response> fakeReadResponse) {
        assertThat(fakeReadResponse.statusCode()).isEqualTo(500);
    }
}