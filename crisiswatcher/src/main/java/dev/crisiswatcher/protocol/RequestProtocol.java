package dev.crisiswatcher.protocol;


public class RequestProtocol {
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
            Request request = Request.getEnum(splitedInput[1]);
            if(request.getValue().equals("EVAC")){

            }else if(request.getValue().equals("COMM")){

            }else if(request.getValue().equals("RES")){

            }
        }
    } 

    
}
