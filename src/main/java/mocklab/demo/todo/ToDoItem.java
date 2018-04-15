package mocklab.demo.todo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ToDoItem {

    private final String id;
    private final String description;

    public ToDoItem(@JsonProperty("id") String id,
                    @JsonProperty("description") String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
