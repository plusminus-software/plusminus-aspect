package software.plusminus.aspect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import software.plusminus.aspect.fixtures.TestAspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HttpAspectTest {

    @LocalServerPort
    private int port;

    @SpyBean
    private TestAspect testAspect;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void doFilterInternal() {
        String url = "http://localhost:" + port + "/test";

        String response = this.restTemplate.getForObject(url, String.class);

        assertThat(response).isEqualTo("ok");
        verify(testAspect).before();
        verify(testAspect).around(any());
        verify(testAspect).after();
        verify(testAspect, never()).onException(any());
        verify(testAspect).finish();
    }
}
