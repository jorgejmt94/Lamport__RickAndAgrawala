package Lamport;

import java.io.IOException;
import java.net.DatagramSocket;

import static java.lang.Thread.sleep;

public class ProcessLWA2 {

    public static DatagramSocket processLWA;

    public static void main(String[] args) throws IOException, InterruptedException {


        processLWA = new DatagramSocket(Message.LWA2_PORT);
        Message mssg;
        int timeStamp = 0;


        /*
            Lamport configure
         */

        Lamport lamport = new Lamport(Message.LWA2, Message.MAX_CONNECTIONS ) {
            @Override
            public void sendMessage(int src, Message message) {

                if (src == Message.LWA1)
                    try {
                        Message.sendMessage(processLWA, message, Message.LWA1_PORT);
                    } catch (IOException e) {
                        System.out.println("sendMessage LWA1_PORT Catch");
                    }
                if (src == Message.LWA2)
                    try {
                        Message.sendMessage(processLWA, message, Message.LWA2_PORT);
                    } catch (IOException e) {
                        System.out.println("sendMessage LWA2_PORT Catch");
                    }
                if (src == Message.LWA3)
                    try {
                        Message.sendMessage(processLWA, message, Message.LWA3_PORT);
                    } catch (IOException e) {
                        System.out.println("sendMessage LWA1_PORT Catch");
                    }

            }

            @Override
            protected void broadcastMessage(Message message) {
                try {
                    Message.sendMessage(processLWA, message, Message.LWA1_PORT);
                    Message.sendMessage(processLWA, message, Message.LWA3_PORT);

                } catch (IOException e) {
                    System.out.println("BroadcastMessage Catch");
                }
            }

            @Override
            void myWait() {

                try {
                    Message message = Message.receiveMessage(processLWA);
                    if (message.getSrc() == Message.LWA1){
                        switch (message.getMode()){
                            case Message.REQUEST:
                                handleMsg(message);
                                System.out.println("[DEBUG] <--LWA1 REQUEST");
                                break;
                            case Message.ACK:
                                handleMsg(message);
                                System.out.println("[DEBUG] <--LWA1 ACK");
                                break;
                            case Message.RELEASE:
                                handleMsg(message);
                                System.out.println("[DEBUG] <--LWA1 RELEASE");
                                break;
                            default:
                                System.out.println("Default");
                        }
                    }
                    if (message.getSrc() == Message.LWA3){
                        switch (message.getMode()){
                            case Message.REQUEST:
                                handleMsg(message);
                                System.out.println("[DEBUG] <--LWA3 REQUEST");
                                break;
                            case Message.ACK:
                                handleMsg(message);
                                System.out.println("[DEBUG] <--LWA3 ACK");
                                break;
                            case Message.RELEASE:
                                handleMsg(message);
                                System.out.println("[DEBUG] <--LWA3 RELEASE");
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
        /*
            end Lamport configure
         */

        //bucle infinit del LWA
        while (true) {

            boolean start = false;
            //esperar al proces A
            while (!start){

                mssg = Message.receiveMessage(processLWA);
                //timeStamp = mssg.getTimeStamp();
                if (mssg.getMode() == Message.INIT) start = true;
            }

            lamport.requestCS();

            mssg = new Message(Message.TO_SCREEN, "Sóc el procés LWA2" , Message.LWA2);
            for (int i = 0; i < 10; i++) {
                Message.sendMessage(processLWA, mssg, Message.A_PORT);
                sleep(1000);
            }

            lamport.releaseCS();


            //notificar proces A que ja he acabat
            mssg = new Message(Message.FINISHED, timeStamp, Message.LWA2);
            Message.sendMessage(processLWA, mssg, Message.A_PORT);

            //sleep(4000);

        }


    }
}
