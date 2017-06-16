package es.urjc.mov.javsan.timetab;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import es.urjc.mov.javsan.Protocol.Message;
import es.urjc.mov.javsan.Protocol.RepErr;
import es.urjc.mov.javsan.Protocol.RepTimeTables;
import es.urjc.mov.javsan.Protocol.ReqTimeTables;
import es.urjc.mov.javsan.Structures.Groups;
import es.urjc.mov.javsan.Structures.Student;
import es.urjc.mov.javsan.Structures.StudentGroups;

public class TimeTabClient {

    private final int TIMEOUT = 2000; //ms

    private Socket socket;
    private OutputStream tx;
    private InputStream rx;

    private StudentGroups studentGroups;

    TimeTabClient () {
        socket = null;
        rx = null;
        tx = null;
        studentGroups = new StudentGroups();
    }

    public Student getStudent() {
        return studentGroups.getStudent();
    }
    public Groups getGroups() {
        return studentGroups.getGroups();
    }

    public String connect(String ip , int port)  {
        InetSocketAddress endPoint = new InetSocketAddress(ip, port);
        boolean fail = false;
        String err = "";

        try {
            socket = new Socket();
            socket.connect(endPoint, TIMEOUT);
            rx = socket.getInputStream();
            tx = socket.getOutputStream();
        } catch (IOException e) {
            fail = true;
            err = "Error connecting with server... " + "ip : " + ip + " port : " + port;
        }
        return connectFailed(err, fail);
    }

    public String sendRequestTimeTab(String dni) throws IOException {
        if (tx == null) {
            return "We can't sendRequestTimeTab from socket...";
        }
        ReqTimeTables req = new ReqTimeTables(dni);

        req.send(tx);
        return "";
    }

    public String receiveReplyTimeTab() throws IOException {
        if (rx == null){
            return "We can't receiveReplyTimeTab from socket...";
        }
        String err = "";
        Message msg = Message.produce(rx);
        if (msg instanceof RepTimeTables) {
            RepTimeTables rep = (RepTimeTables) msg;
            studentGroups = rep.getTimeTab();
        } else if (msg instanceof RepErr) {
            RepErr error = (RepErr) msg;
            err = "Reply Error ..." + error.toString();
        } else {
            err =  "Bad type of message...";
        }
        return err;
    }

    public String close() {
        String err = "";

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            err = "Error closing client socket... " + e.toString();
        }
        return err;
    }

    private String connectFailed(String err, boolean fail) {
        try {
            if (fail && socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            err += "Error closing connection later fail : " + e.toString();
        }
        return err;
    }
}

