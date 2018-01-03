package Lamport;

import java.io.IOException;
import java.net.DatagramSocket;

import static java.lang.Thread.sleep;

public class ProcessA {
    public static DatagramSocket processA;

    public static void main(String[] args) throws IOException, InterruptedException {

        processA = new DatagramSocket(Message.A_PORT);
        Message mssg;
        boolean haveToken = true;
        int timeStamp = 0;
        sleep(7000);//TODO: ESPERAS DELS FILLS


        while (true) {



            /*
                    ->  COMUNICACIO PROCCESSOS LWA  <-
             */
/*
            int finishedProc = 0;
            //LWA1
            mssg = new Message(Message.INIT, timeStamp, Message.A);
            Message.sendMessage(processA, mssg, Message.LWA1_PORT);
            //LWA2
            mssg = new Message(Message.INIT, timeStamp, Message.A);
            Message.sendMessage(processA, mssg, Message.LWA2_PORT);
            //LWA3
            mssg = new Message(Message.INIT, timeStamp, Message.A);
            Message.sendMessage(processA, mssg, Message.LWA3_PORT);


            while (finishedProc < Message.MAX_CONNECTIONS){
                mssg = Message.receiveMessage(processA);
                if (mssg.getMode() == Message.FINISHED) {
                    finishedProc++;
                    System.out.println("[DEBUG]" + "Proces acabat: " +mssg.getSrc());
                }
                if (mssg.getMode() == Message.TO_SCREEN) System.out.println(mssg.getToScreen());

            }

            System.out.println("[DEBUG]" + "Han acabat tots els processos, envio token a B");

*/
            /*
                    ->  COMUNICACIO PROCCES B  <-
             */
            //sleep(1000);
            //envio el token al proces B
            mssg = new Message(Message.TOKEN, timeStamp, Message.A);
            Message.sendMessage(processA, mssg, Message.B_PORT);
            haveToken = false;
            System.out.println("[DEBUG]" + "Token enviat");

            //espero a rebre token del proces B
            while (!haveToken){
                mssg = Message.receiveMessage(processA);
                timeStamp = mssg.getTimeStamp();
                if (mssg.getMode() == Message.TOKEN) haveToken = true;
            }
        }



    }
}
