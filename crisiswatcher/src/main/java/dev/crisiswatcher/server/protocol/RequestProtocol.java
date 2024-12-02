package dev.crisiswatcher.server.protocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.crisiswatcher.server.request.RequestHandler;
import dev.crisiswatcher.server.manager.Manager;
import dev.crisiswatcher.server.model.RequestModel;
import dev.crisiswatcher.server.model.RequestModel.RequestLevel;

public class RequestProtocol {

    public static List<RequestModel> requests = new ArrayList<>();

    public static Manager DBManager = Manager.getInstance();

    public static String processInput(String input) {
        String output = null;
        if (input.contains("/request")) {
            sendRequest(input);
        }
        return output;
    }

    public synchronized static void sendRequest(String input){
        String[] splitedInput = input.split(" ");
        if(splitedInput.length == 2){
            RequestModel request = new RequestModel();
            RequestLevel requestLevel = RequestLevel.getEnum(splitedInput[1]);
            if (requestLevel.getValue().equals("EVAC") ||
                requestLevel.getValue().equals("COMM") ||
                requestLevel.getValue().equals("RES")) {
                    request.setRequestLevel(requestLevel);
                    request.setApproved(null);
                    boolean inserted = DBManager.insertRequest(request);

                    if (inserted) {
                        requests.add(request);
                        try {
                            (new RequestHandler(request)).start();
                        } catch (IOException ignored) {}
                    }
                }
        }
    } 

    
    
}
