package RicartaAndAgrawala;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ProcessLWB1 {

    public static DatagramSocket processLWB;

    public static void main(String[] args) throws SocketException {

        processLWB = new DatagramSocket(Message.LWB1_PORT);
        Message mssg;
        int timeStamp = 0;

        RAMutex raMutex = new RAMutex(Message.LWB1) {
            @Override
            void sendMsg(int pid, Message message) {
                if (pid == Message.LWB1)
                    try {
                        Message.sendMessage(processLWB, message, Message.LWB1_PORT);
                    } catch (IOException e) {
                        System.out.println("sendMessage LWA1_PORT Catch");
                    }
                if (pid == Message.LWB2)
                    try {
                        Message.sendMessage(processLWB, message, Message.LWB2_PORT);
                    } catch (IOException e) {
                        System.out.println("sendMessage LWA2_PORT Catch");
                    }
            }

            @Override
            void broadcastMessage(Message message) {
                try {
                    Message.sendMessage(processLWB, message, Message.LWB2_PORT);
                } catch (IOException e) {
                    System.out.println("BroadcastMessage Catch");
                }
            }

            @Override
            void myWait() {
                try {
                    Message message = Message.receiveMessage(processLWB);
                    if (message.getSrc() == Message.LWB2){
                        switch (message.getMode()){
                            case Message.REQUEST:
                                handleMsg(message);
                                System.out.println("[DEBUG] <--LWA2 REQUEST");
                                break;
                            case Message.OKAY:
                                handleMsg(message);
                                System.out.println("[DEBUG] <--LWA2 OKAY");
                                break;
                            default:
                                System.out.println("Default");
                        }
                    }

                } catch (IOException e) {
                    System.out.println("MyWait catch");
                }
            }
        };


    }
}
