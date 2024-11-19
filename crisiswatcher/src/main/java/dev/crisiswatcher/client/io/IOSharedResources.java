package dev.crisiswatcher.client.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.crisiswatcher.schema.User;

public class IOSharedResources {
    private User user;
    private List<String> outputBuffer;

    public IOSharedResources() {
        user = new User();
        outputBuffer = Collections.synchronizedList(new ArrayList<>());
    }

    public User getUser() {
        return user;
    }

    public List<String> getOutputBuffer() {
        return outputBuffer;
    }
}
