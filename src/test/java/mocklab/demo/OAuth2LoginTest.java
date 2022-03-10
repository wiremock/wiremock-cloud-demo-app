package mocklab.demo;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static mocklab.demo.MockLabDemoApp.DEFAULT_APP_PORT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = MockLabDemoApp.class)
public class OAuth2LoginTest {

    static final String APP_BASE_URL = "http://localhost:" + DEFAULT_APP_PORT;

    private WebDriver webDriver;

    @Rule
    public WireMockRule mockOAuth2Provider = new WireMockRule(wireMockConfig()
            .port(8077)
            .extensions(new ResponseTemplateTransformer(true)));

    @Before
    public void setUp() {
        webDriver = new ChromeDriver();

        mockOAuth2Provider.stubFor(get(urlPathEqualTo("/favicon.ico")).willReturn(notFound()));

        mockOAuth2Provider.stubFor(get(urlPathMatching("/oauth/authorize?.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/html")
                        .withBodyFile("login.html")));

        mockOAuth2Provider.stubFor(post(urlPathEqualTo("/login"))
                .willReturn(temporaryRedirect("{{formData request.body 'form' urlDecode=true}}{{{form.redirectUri}}}?code={{{randomValue length=30 type='ALPHANUMERIC'}}}&state={{{form.state}}}")));

        mockOAuth2Provider.stubFor(post(urlPathEqualTo("/oauth/token"))
                .willReturn(okJson("{\"token_type\": \"Bearer\",\"access_token\":\"{{randomValue length=20 type='ALPHANUMERIC'}}\"}")));

        mockOAuth2Provider.stubFor(get(urlPathEqualTo("/userinfo"))
                .willReturn(okJson("{\"sub\":\"my-id\",\"email\":\"bwatkins@test.com\"}")));
    }

    @After
    public void reset() {
        if (webDriver != null) {
            webDriver.close();
        }
    }

    @Test
    public void logs_in_via_wiremock_sso() throws Exception {
        webDriver.get(APP_BASE_URL + "/oauth2/authorization/wiremock");
        assertThat(webDriver.getCurrentUrl()).startsWith("http://localhost:8077/oauth/authorize");

        webDriver.findElement(By.name("username")).sendKeys("bwatkins@test.com");
        webDriver.findElement(By.name("password")).sendKeys("pass123");
        webDriver.findElement(By.id("submit")).click();

        assertThat(webDriver.getCurrentUrl()).contains("/user");
        assertThat(webDriver.findElement(By.tagName("h1")).getText()).contains("Hello bwatkins@test.com!");
    }

    static {
        System.setProperty("webdriver.chrome.driver", "lib/chromedriver"); // This is the OSX driver. You'll need to tweak this if you want to run on Windows or Linux.
    }
}
