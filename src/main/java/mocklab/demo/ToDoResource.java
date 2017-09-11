package mocklab.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@Controller
public class ToDoResource {

    private final RestTemplate restTemplate;

    @Value("${mockapi.baseurl}")
    private String baseUrl;

    public ToDoResource() {
        restTemplate = new RestTemplate();
    }

    @GetMapping("/")
    public String indexPage(Map<String, Object> model) {
        ToDoList toDoList = restTemplate.getForObject(URI.create(baseUrl + "/todo-items"), ToDoList.class);
        model.put("items", toDoList.getItems());
        return "index";
    }

    @PostMapping("/todo-items/{id}/delete")
    public String deleteItem(@PathVariable String id) {
        restTemplate.delete(baseUrl + "/todo-items/{id}", id);
        return "redirect:/";
    }


}
