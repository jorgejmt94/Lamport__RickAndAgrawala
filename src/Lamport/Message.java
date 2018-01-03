package Lamport;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Message {

    public static int A = 11;
    public static int B = 22;

    public static int A_PORT = 1110;
    public static int B_PORT = 2220;


    public static int LWA1 = 0;
    public static int LWA2 = 1;
    public static int LWA3 = 2;

    public static int LWA1_PORT = 1111;
    public static int LWA2_PORT = 1112;
    public static int LWA3_PORT = 1113;


    public static final int TOKEN = 30;
    public static final int INIT = 31;
    public static final int FINISHED = 32;
    public static final int REQUEST = 33;
    public static final int RELEASE = 34;
    public static final int ACK = 35;
    public static final int TO_SCREEN = 36;


    public static final int MAX_MESSAGE = 40;
    public static final int MAX_CONNECTIONS = 3;





    private int mode;
    private int timeStamp;
    private int src;
    private String toScreen;


    public Message(int mode, int timeStamp, int src) {
        this.mode = mode;
        this.timeStamp = timeStamp;
        this.src = src;
    }

    public Message(int mode, String toScreen, int src) {
        this.mode = mode;
        this.toScreen = toScreen;
        this.src = src;
    }

    public Message(String mssg){

        String[] mssgSplit = mssg.split("-");
        this.mode = Integer.parseInt(mssgSplit[0]);
        if (this.mode == TO_SCREEN) this.toScreen = mssgSplit[1];
        else this.timeStamp = Integer.parseInt(mssgSplit[1]);
        this.src = Integer.parseInt(mssgSplit[2]);
    }


    public static Message receiveMessage(DatagramSocket process) throws IOException {

        byte[] mssgBytes = new byte[Message.MAX_MESSAGE];
        DatagramPacket packet = new DatagramPacket(mssgBytes, Message.MAX_MESSAGE);
        process.receive(packet);
        //System.out.println("[DEBUG-RECIEVE] "+new String (packet.getData()));
        return new Message(new String (packet.getData()));
    }

    public static void sendMessage(DatagramSocket process, Message message, int destination) throws IOException {

        if( message.getMode() == TO_SCREEN){
            byte[] mssgBytes = message.serializeToScreen().getBytes();
            DatagramPacket packet = new DatagramPacket(mssgBytes, mssgBytes.length, InetAddress.getLocalHost(), destination);
            process.send(packet);
            System.out.println("[DEBUG-SEND] message:"+ new String(packet.getData()) + " ->port: " + destination);

        }else{
            byte[] mssgBytes = message.serialize().getBytes();
            DatagramPacket packet = new DatagramPacket(mssgBytes, mssgBytes.length, InetAddress.getLocalHost(), destination);
            process.send(packet);
            //System.out.println("[DEBUG-SEND] message:"+ new String(packet.getData()) + " ->port: " + destination);

        }
}

    public String serialize(){

        return mode + "-" + timeStamp + "-" + src + "-";
    }

    public String serializeToScreen(){

        return mode + "-" + toScreen + "-" + src + "-";
    }


    public int getMode() {

        return mode;
    }

    public void setMode(int mode) {

        this.mode = mode;
    }

    public int getTimeStamp() {

        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {

        this.timeStamp = timeStamp;
    }

    public int getSrc() {

        return src;
    }

    public void setSrc(int src) {

        this.src = src;
    }
    public String getToScreen() {
        return toScreen;
    }

    public void setToScreen(String toScreen) {
        this.toScreen = toScreen;
    }
}
