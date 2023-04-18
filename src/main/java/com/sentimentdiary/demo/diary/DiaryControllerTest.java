//package com.sentimentdiary.demo.diary;
//
//import com.epages.restdocs.apispec.ResourceSnippetParameters;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.shirohoo.docs.domain.UserRequest;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.MediaType;
//import org.springframework.restdocs.RestDocumentationContextProvider;
//import org.springframework.restdocs.RestDocumentationExtension;
//import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.reactive.function.BodyInserters;
//import reactor.core.publisher.Mono;
//
//import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
//import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
//import static com.epages.restdocs.apispec.Schema.schema;
//import static com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper.document;
//import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
//import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
//import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.*;
//import static org.springframework.test.web.reactive.server.WebTestClient.*;
//import static org.springframework.web.reactive.function.BodyInserters.*;
//
//@ExtendWith(RestDocumentationExtension.class)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class DiaryControllerTest {
//    @Autowired
//    ObjectMapper mapper; // json string 변환을 위해 주입
//
//    WebTestClient webTestClient;
//
//    @BeforeEach
//    void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
//        webTestClient = MockMvcWebTestClient.bindToApplicationContext(context) // 서블릿 컨테이너 바인딩
//                .configureClient() // 설정 추가
//                .filter(documentationConfiguration(restDocumentation)) // epages 문서 설정을 추가
//                .build();
//    }
//
//    @Test
//    @Order(1)
//    @Rollback(false)
//    void 사용자_정보를_생성한다() throws Exception {
//        // given
//        Mono<String> request = Mono.just(mapper.writeValueAsString(UserRequest.builder()
//                .name("홍길동")
//                .email("hong@email.com")
//                .phoneNumber("01012341234")
//                .build())
//        );
//
//        String expected = mapper.writeValueAsString(UserRequest.builder()
//                .id(1L)
//                .name("홍길동")
//                .email("hong@email.com")
//                .phoneNumber("01012341234")
//                .build());
//
//        // when
//        ResponseSpec exchange = webTestClient.post()
//                .uri("/api/v1/user")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .body(fromProducer(request, String.class))
//                .exchange();
//
//        // then
//        exchange.expectStatus().isOk() // 응답 상태코드가 200이면 통과
//                .expectBody().json(expected) // 응답 바디가 예상한 json string과 같으면 통과
//                .consumeWith(document("create", // 문서 작성 및 추가 검증 작업
//                        preprocessRequest(prettyPrint()), // 문서에 json 출력을 이쁘게 해준다
//                        preprocessResponse(prettyPrint()), // 문서에 json 출력을 이쁘게 해준다
//                        resource(
//                                ResourceSnippetParameters.builder()
//                                        .tag("User") // 문서에 표시될 태그
//                                        .summary("사용자 정보 생성") // 문서에 표시될 요약정보
//                                        .description("사용자 정보를 생성한다") // 문서에 표시될 상세정보
//                                        .requestSchema(schema("UserRequest")) // 문서에 표시될 요청객체 정보
//                                        .responseSchema(schema("UserResponse")) // 문서에 표시될 응답객체 정보
//                                        .requestFields( // 요청 field 검증 및 문서화
//                                                fieldWithPath("id").description("식별자"),
//                                                fieldWithPath("name").description("이름"),
//                                                fieldWithPath("email").description("이메일"),
//                                                fieldWithPath("phoneNumber").description("전화번호")
//                                        )
//                                        .responseFields( // 응답 field 검증 및 문서화
//                                                fieldWithPath("id").description("식별자"),
//                                                fieldWithPath("name").description("이름"),
//                                                fieldWithPath("email").description("이메일"),
//                                                fieldWithPath("phoneNumber").description("전화번호"),
//                                                fieldWithPath("createAt").description("등록일"),
//                                                fieldWithPath("updateAt").description("수정일")
//                                        )
//                                        .build()
//                        )));
//    }
//
//    @Test
//    @Order(2)
//    void 사용자_정보를_조회한다() throws Exception {
//        // given
//        String expected = mapper.writeValueAsString(UserRequest.builder()
//                .id(1L)
//                .name("홍길동")
//                .email("hong@email.com")
//                .phoneNumber("01012341234")
//                .build());
//
//        // when
//        ResponseSpec exchange = webTestClient.get()
//                .uri("/api/v1/user/{id}", 1)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange();
//
//        // then
//        exchange.expectStatus().isOk()
//                .expectBody().json(expected)
//                .consumeWith(document("read",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        resource(
//                                ResourceSnippetParameters.builder()
//                                        .tag("User")
//                                        .summary("사용자 정보 조회")
//                                        .description("사용자 정보를 조회한다")
//                                        .requestSchema(null)
//                                        .responseSchema(schema("UserResponse"))
//                                        .pathParameters(
//                                                parameterWithName("id").description("식별자")
//                                        )
//                                        .responseFields(
//                                                fieldWithPath("id").description("식별자"),
//                                                fieldWithPath("name").description("이름"),
//                                                fieldWithPath("email").description("이메일"),
//                                                fieldWithPath("phoneNumber").description("전화번호"),
//                                                fieldWithPath("createAt").description("등록일"),
//                                                fieldWithPath("updateAt").description("수정일")
//                                        )
//                                        .build()
//                        )));
//    }
//
//    @Test
//    @Order(3)
//    void 사용자_정보를_수정한다() throws Exception {
//        // given
//        Mono<String> request = Mono.just(mapper.writeValueAsString(UserRequest.builder()
//                .id(1L)
//                .name("아무개")
//                .email("hong@email.com")
//                .phoneNumber("01012341234")
//                .build())
//        );
//
//        // when
//        ResponseSpec exchange = webTestClient.put()
//                .uri("/api/v1/user")
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//                .body(fromProducer(request, String.class))
//                .exchange();
//
//        // then
//        exchange.expectStatus().isOk()
//                .expectBody().json(request.block())
//                .consumeWith(document("update",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        resource(
//                                ResourceSnippetParameters.builder()
//                                        .tag("User")
//                                        .summary("사용자 정보 수정")
//                                        .description("사용자 정보를 수정한다")
//                                        .requestSchema(schema("UserRequest"))
//                                        .responseSchema(schema("UserResponse"))
//                                        .requestFields(
//                                                fieldWithPath("id").description("식별자"),
//                                                fieldWithPath("name").description("이름"),
//                                                fieldWithPath("email").description("이메일"),
//                                                fieldWithPath("phoneNumber").description("전화번호")
//                                        )
//                                        .responseFields(
//                                                fieldWithPath("id").description("식별자"),
//                                                fieldWithPath("name").description("이름"),
//                                                fieldWithPath("email").description("이메일"),
//                                                fieldWithPath("phoneNumber").description("전화번호"),
//                                                fieldWithPath("createAt").description("등록일"),
//                                                fieldWithPath("updateAt").description("수정일")
//                                        )
//                                        .build()
//                        )));
//    }
//
//    @Test
//    @Order(4)
//    void 사용자_정보를_삭제한다() throws Exception {
//        // when
//        ResponseSpec exchange = webTestClient.delete()
//                .uri("/api/v1/user/{id}", 1)
//                .exchange();
//
//        // then
//        exchange.expectStatus().isOk()
//                .expectBody()
//                .consumeWith(document("delete",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(prettyPrint()),
//                        resource(
//                                ResourceSnippetParameters.builder()
//                                        .tag("User")
//                                        .summary("사용자 정보 삭제")
//                                        .description("사용자 정보를 삭제한다")
//                                        .requestSchema(null)
//                                        .responseSchema(null)
//                                        .pathParameters(
//                                                parameterWithName("id").description("식별자")
//                                        )
//                                        .build()
//                        )));
//    }
//}
