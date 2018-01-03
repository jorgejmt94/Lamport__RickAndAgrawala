package Lamport;


import java.io.IOException;

public abstract class Lamport {
    public static final int N = 3;

    private DirectClock v;
    private int[] q;
    private int myId;

    private boolean okayCS = false;


    public abstract void sendMessage(int src, Message message) throws IOException;
    protected abstract void broadcastMessage(Message message);
    abstract void myWait();

    public Lamport(int myId, int numProc){

        this.myId = myId;
        v = new DirectClock(myId, numProc);
        q = new int[numProc];
        for(int j = 0; j < numProc; j++){
            q[j] = Integer.MAX_VALUE;
        }
    }

    public synchronized void requestCS(){
        v.tick();
        q[myId] = v.getValue(myId);
        broadcastMessage(new Message(Message.REQUEST, q[myId], myId));

        //while (!okayCS()) myWait();
        while (!okayCS) myWait();
        okayCS = false;
    }


    public synchronized void releaseCS(){
        q[myId] = Integer.MAX_VALUE;
        broadcastMessage(new Message(Message.RELEASE, v.getValue(myId), myId));
    }

    private boolean okayCS(){
        for(int j = 0; j < N; j++){
            if(isGreater(q[myId], myId, q[j], j)) {
                return false;
            }
            if(isGreater(q[myId], myId, v.getValue(j), j)) {
                return false;
            }
        }
        return true;
    }

    private boolean isGreater(int entry1, int pid1, int entry2, int pid2){
        if(entry2 == Integer.MAX_VALUE) return false;
        if( (entry1 > entry2) || ((entry1 == entry2) && (pid1 > pid2)))  return true;
        return false;
    }


    public synchronized void handleMsg(Message mssg) throws IOException {

        int timeStamp = mssg.getTimeStamp();
        v.receiveAction(mssg.getSrc(), mssg.getTimeStamp());
        if(mssg.getMode() == Message.REQUEST) {
            q[mssg.getSrc()] = timeStamp;
            sendMessage(mssg.getSrc(), new Message(Message.ACK, v.getValue(myId), myId));
        }else if(mssg.getMode() == Message.RELEASE) {
            q[mssg.getSrc()] = Integer.MAX_VALUE;
        }

        notify();
        okayCS = okayCS();

    }



}
