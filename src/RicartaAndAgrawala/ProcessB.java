package RicartaAndAgrawala;

import java.io.IOException;
import java.net.DatagramSocket;

public class ProcessB {
    public static DatagramSocket processB;
    public static final int MAX_CONNECTIONS = 2;

    public static void main(String[] args) throws IOException, InterruptedException {

        processB = new DatagramSocket(Message.B_PORT);
        Message mssg;
        boolean haveToken = false;
        int timeStamp = 0;

        while (true) {

            /*
                COMUNICACIO PROCES A, ESPERAR TOKEN
             */
            while (!haveToken){
                mssg = Message.receiveMessage(processB);
                timeStamp = mssg.getTimeStamp();
                if (mssg.getMode() == Message.TOKEN) haveToken = true;
                //sleep(3000);
            }

            /*
                RicartaAndAgrawala
             */

            int finishedProc = 0;
            //LWB1
            mssg = new Message(Message.INIT, timeStamp, Message.B);
            Message.sendMessage(processB, mssg, Message.LWB1_PORT);
            //LWB2
            mssg = new Message(Message.INIT, timeStamp, Message.B);
            Message.sendMessage(processB, mssg, Message.LWB2_PORT);


            while (finishedProc < Message.MAX_CONNECTIONS){
                mssg = Message.receiveMessage(processB);
                if (mssg.getMode() == Message.FINISHED) {
                    finishedProc++;
                    System.out.println("[DEBUG]" + "Proces acabat: " +mssg.getSrc());
                }
                if (mssg.getMode() == Message.TO_SCREEN) System.out.println(mssg.getToScreen());

            }

            System.out.println("[DEBUG]" + "Han acabat tots els processos, envio token a A");


            /*
                COMUNICACIO PROCES A, ENVIAR TOKEN
            */
            haveToken = false;
            //enviar token
            mssg = new Message(Message.TOKEN, timeStamp, Message.B);
            Message.sendMessage(processB, mssg, Message.A_PORT);
            System.out.println("[DEBUG]" + "Token enviat");

        }



    }


}
