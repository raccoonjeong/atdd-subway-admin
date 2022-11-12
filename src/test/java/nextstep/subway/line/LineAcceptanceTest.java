package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }
    /**
     * 지하철노선 생성
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");

        ExtractableResponse<Response> response =
            RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames =
            RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf("1호선");
    }

    /**
     * 지하철노선 목록 조회
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getTwoStations() {
        // given
        Map<String, String> param1 = new HashMap<>();
        param1.put("name", "1호선");
        Map<String, String> param2 = new HashMap<>();
        param2.put("name", "2호선");

        // when
        RestAssured.given().log().all()
            .body(param1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all();

        RestAssured.given().log().all()
            .body(param2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all();

        List<String> lineNames =
            RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames).hasSize(2);
    }

    /**
     * 지하철노선 조회
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getStations() {
        // given
        Map<String, String> param1 = new HashMap<>();
        param1.put("name", "3호선");

        // when
        RestAssured.given().log().all()
            .body(param1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all();

        List<String> lineNames =
            RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames).containsAnyOf("3호선");
    }
    /**
     * 지하철노선 수정
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateStations() {
        // given
        Map<String, String> param = new HashMap<>();
        param.put("name", "4호선");

        // when
        RestAssured.given().log().all()
            .body(param)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all();

        // 수정
        Map<String, String> updateParam = new HashMap<>();
        updateParam.put("name", "5호선");

        RestAssured.given().log().all()
            .body(updateParam)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/1")
            .then().log().all();

        List<String> lineNames =
            RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

        // then
        assertThat(lineNames).hasSize(1).containsAnyOf("5호선");
    }
    /**
     * 지하철노선 삭제
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteLine() {
        // Given
        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "1호선");

        ExtractableResponse<Response> response1 = RestAssured.given().log().all()
            .body(params1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all().extract();

        List<String> lineNames1 =
            RestAssured.given()
                .log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

        assertThat(lineNames1.size()).isEqualTo(1);

        // When
        RestAssured
            .given().log().all()
            .param("id")
            .when().delete("/lines/1")
            .then().log().all();

        // Then
        List<String> lineNames2 =
            RestAssured.given()
                .log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);

        assertThat(lineNames2.size()).isEqualTo(0);

    }
}
