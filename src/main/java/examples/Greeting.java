package examples;

/**
 * Created by jarndt on 1/3/17.
 */
public class Greeting {
    private final long id;
    private final String content, result;

    public Greeting(long id, String content, String result) {
        this.id = id;
        this.content = content;
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getResult() {
        return result;
    }

}
