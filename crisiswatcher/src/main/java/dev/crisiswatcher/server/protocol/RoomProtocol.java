package dev.crisiswatcher.server.protocol;
import java.util.UUID;

public class RoomProtocol {
    /**
     * First start digit for the ip generator
     */
    private static int firstDigit = 224;
    /**
     * Second start digit for the ip generator
     */
    private static int secondDigit = 0;
    /**
     * Third start digit for the ip generator
     */
    private static int thirdDigit = 0;
    /**
     * Forth start digit for the ip generator
     */
    private static int forthDigit = 0;
    /**
     * Max first digit range for the ip generator
     */
    private static int maxFirst = 239;
    /**
     * Max second, third, forth digit range for the ip generator
     */
    private static int maxOther = 255;

    /**
     * Custom code generator to join rooms
     * @return
     */
    public synchronized static String generateCode(){
        UUID uniqueId = UUID.randomUUID();
        return uniqueId.toString();
    }

    /**
     * Ip generator for the rooms
     * @return
     */
    public synchronized static String getIp(){
        if(forthDigit > maxOther-1){
            if(thirdDigit > maxOther-1){
                if(secondDigit > maxOther-1){
                    if(firstDigit > maxFirst-1){
                        return "MAX IPS";
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
