package RicartaAndAgrawala;

import java.io.IOException;
import java.net.DatagramSocket;

import static java.lang.Thread.sleep;

public class ProcessLWB1 {

    public static DatagramSocket processLWB;

    public static void main(String[] args) throws IOException, InterruptedException {

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
                        System.out.println("sendMessage LWB1_PORT Catch");
                    }
                if (pid == Message.LWB2)
                    try {
                        Message.sendMessage(processLWB, message, Message.LWB2_PORT);
                    } catch (IOException e) {
                        System.out.println("sendMessage LWB2_PORT Catch");
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
                                System.out.println("[DEBUG] <--LWB2 REQUEST");
                                break;
                            case Message.OKAY:
                                handleMsg(message);
                                System.out.println("[DEBUG] <--LWB2 OKAY");
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


        while (true){
            boolean start = false;
            //esperar al proces B
            while (!start){

                mssg = Message.receiveMessage(processLWB);
                if (mssg.getMode() == Message.INIT) start = true;
            }
            raMutex.requestCS();

            mssg = new Message(Message.TO_SCREEN, "Sóc el procés LWB1" , Message.LWB1);
            for (int i = 0; i < 10; i++) {
                Message.sendMessage(processLWB, mssg, Message.B_PORT);
                sleep(1000);
            }

            raMutex.releaseCS();

            //notificar proces B que ja he acabat
            mssg = new Message(Message.FINISHED, timeStamp, Message.LWB1);
            Message.sendMessage(processLWB, mssg, Message.B_PORT);

        }


    }
}
