package mocklab.demo.todo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ToDoList {

    private final List<ToDoItem> items;

    public ToDoList(@JsonProperty("items") List<ToDoItem> items) {
        this.items = items;
    }

    public List<ToDoItem> getItems() {
        return items;
    }
}
