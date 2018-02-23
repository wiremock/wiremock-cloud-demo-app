package mocklab.demo;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        classes = MockLabDemoConfiguration.class)
public class ToDoWebTest {

    static final String APP_BASE_URL = "http://localhost:9000";

    @Value("${mockapi.baseurl}")
    private String mockApiBaseUrl;

    private WebDriver webDriver;

    private WireMock mockToDoApi;

    @Before
    public void init() {
        mockToDoApi = new WireMock(URI.create(mockApiBaseUrl).getHost(), 80); // Change the hostname to point to your mock API (see the Settings page in the MockLab UI)
        reset();

        webDriver = new ChromeDriver();
    }

    @After
    public void reset() {
        mockToDoApi.resetToDefaultMappings();
        if (webDriver != null) {
            webDriver.close();
        }
    }

    private static final String OK_JSON = "{\n" +
            "  \"items\": [\n" +
            "    {\n" +
            "      \"id\": \"1\",\n" +
            "      \"description\": \"First item description\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"2\",\n" +
            "      \"description\": \"Second thing description\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"3\",\n" +
            "      \"description\": \"Third thing\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"id\": \"4\",\n" +
            "      \"description\": \"Number four task\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void shows_to_to_list_items_returned_from_the_api() {
        // Given
        mockToDoApi.register(get("/todo-items").willReturn(okJson(OK_JSON)));

        // When
        webDriver.get(APP_BASE_URL + "/");
        List<WebElement> toDoItems = webDriver.findElements(By.className("todo-item"));

        // Then
        assertThat(toDoItems.get(0).getText()).contains("First item description");
        assertThat(toDoItems.get(2).getText()).contains("Third thing");
    }

    private static final String OK_POST_RESPONSE_JSON = "{ \"message\": \"Successfully added an item\" }";
    private static final String EXPECTED_NEW_ITEM_JSON = "{ \"id\": null, \"description\": \"My very urgent thing\" }";

    @Test
    public void shows_response_message_when_new_item_successfully_submitted() throws Exception {
        // Given
        mockToDoApi.register(get("/todo-items").willReturn(okJson(OK_JSON)));
        mockToDoApi.register(post("/todo-items")
                .willReturn(okJson(OK_POST_RESPONSE_JSON)));

        // When
        webDriver.get(APP_BASE_URL + "/");
        Thread.sleep(3000);
        webDriver.findElement(By.name("description")).sendKeys("My very urgent thing");
        Thread.sleep(2000);
        webDriver.findElement(By.name("add")).click();
        String message = webDriver.findElement(By.id("message")).getText();


        // Then
        assertThat(message).isEqualTo("Successfully added an item");
        mockToDoApi.verifyThat(postRequestedFor(urlPathEqualTo("/todo-items"))
                .withRequestBody(equalToJson(EXPECTED_NEW_ITEM_JSON)));


        Thread.sleep(2000);
    }

    static {
        System.setProperty("webdriver.chrome.driver", "lib/chromedriver");
    }
}
