package RicartaAndAgrawala;


import java.util.LinkedList;

public abstract class RAMutex {

    private int myts;
    private LamportClock c = new LamportClock();
    private LinkedList<Integer> pendingQ = new LinkedList<Integer>();
    private int numOkay = 0;
    private int myId;

    abstract void sendMsg(int pid, Message message);
    abstract void broadcastMessage(Message message);
    abstract void myWait();


    public RAMutex(int myId) {

        myts = Integer.MAX_VALUE;
        this.myId = myId;
    }

    public synchronized void requestCS() {

        c.tick();
        myts = c.getValue();
        broadcastMessage(new Message(Message.REQUEST, myts, myId));
        numOkay = 0;
        while (numOkay < Message.MAX_CONNECTIONS - 1){

            myWait();
        }
    }

    public synchronized void releaseCS(){

        myts = Integer.MAX_VALUE;
        while(!pendingQ.isEmpty()){
            int pid = pendingQ.removeFirst();
            sendMsg(pid, new Message(Message.OKAY, myId, c.getValue()));
        }
    }

    public synchronized void handleMsg(Message msg/* int src, String tag*/){

        c.recieveAction(msg.getSrc(), msg.getTimeStamp());
        if(msg.getMode() == Message.REQUEST) {
            if((myts == Integer.MAX_VALUE) || (msg.getTimeStamp() < myts) || (msg.getSrc()< myId))
                 sendMsg(msg.getSrc(), new Message(Message.OKAY, c.getValue(), myId));
            else pendingQ.push(msg.getSrc());

        }else if(msg.getMode() == Message.OKAY) numOkay++;

    }

}
