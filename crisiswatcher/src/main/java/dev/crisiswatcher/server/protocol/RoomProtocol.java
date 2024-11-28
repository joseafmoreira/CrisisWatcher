package dev.crisiswatcher.server.protocol;
import java.util.UUID;

public class RoomProtocol {
    private static int firstDigit = 224;
    private static int secondDigit = 0;
    private static int thirdDigit = 0;
    private static int forthDigit = 0;
    private static int maxFirst = 239;
    private static int maxOther = 255;


    public synchronized static String generateCode(){
        UUID uniqueId = UUID.randomUUID();
        return uniqueId.toString();
    }

    public synchronized static String getIp(){
        if(forthDigit > maxOther-1){
            if(thirdDigit > maxOther-1){
                if(secondDigit > maxOther-1){
                    if(firstDigit > maxFirst-1){
                        return "MAX IPS"; //NUNCA VAI ACONTECER :D
                    }
                    forthDigit = 0;
                    thirdDigit = 0;
                    secondDigit = 0;
                    firstDigit++;
                    return firstDigit+"."+secondDigit+"."+thirdDigit+"."+forthDigit;
                }
                forthDigit = 0;
                thirdDigit = 0;
                secondDigit++;
                return firstDigit+"."+secondDigit+"."+thirdDigit+"."+forthDigit;
            }
            forthDigit = 0;
            thirdDigit++;
            return firstDigit+"."+secondDigit+"."+thirdDigit+"."+forthDigit;
        }
        forthDigit++;
        return firstDigit+"."+secondDigit+"."+thirdDigit+"."+forthDigit;
    }
}
