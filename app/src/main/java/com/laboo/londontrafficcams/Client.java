package com.laboo.londontrafficcams;

/**
 * Created by VaiosK on 15/12/2014.
 */


import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {

    public static final String ip = "54.77.177.180"; // server's ip
    // public static final String ip = "192.168.1.6"; // server's ip
    public static final int port = 11423; // server's port to connect
    private ArrayList<String> ServerMessage;// message received from the server
    private OnMessageReceived fwdmsg = null; // method to be executed
    // when a message is
    // received
    private boolean running = false;
    private PrintWriter BufferOut; // way to send messages to the server
    private ObjectInputStream BufferIn;// way to read messages from the server





    // OnMessagedReceived listens for the messages

    public Client(OnMessageReceived listener) {
        fwdmsg = listener;
    }

    // Sends the message entered by client to the server

    public void sendMessage(String message) {
        if (BufferOut != null && !BufferOut.checkError()) {
            BufferOut.println(message);
            BufferOut.flush();
        }
    }

    // Close the connection and release the members

    public void stopClient() {

        running = false;

        if (BufferOut != null) {
            BufferOut.flush();
            BufferOut.close();
        }

        fwdmsg = null;
        BufferIn = null;
        BufferOut = null;
        ServerMessage = null;
    }

    // method to check if the tcp connection is running
    public boolean isRunning() {

        return false;

    }

    @SuppressWarnings("unchecked")
    public void run() {
        running = true;

        try {

            InetAddress serverAddr = InetAddress.getByName(ip);

            Log.e("TCP Client", "C: Connecting...");

            Socket socket = new Socket(serverAddr, port);// create a socket to
            // make the
            // connection with
            // the server

            try {

                // sends message to server
                BufferOut = new PrintWriter(new OutputStreamWriter(
                        socket.getOutputStream()), true);
                // receives message from server
                BufferIn = new ObjectInputStream(socket.getInputStream());

                while (running) {

                    ServerMessage = (ArrayList<String>) BufferIn.readObject(); // the
                    // received
                    // message

                    if (ServerMessage != null && fwdmsg != null) {
                        // call the method messageReceived from the MainActivity
                        fwdmsg.messageReceived(ServerMessage);
                    }

                }

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);



            } finally {

                socket.close();
            }

        } catch (Exception e) {

            this.isRunning();
            Log.e("TCP", "C: Error", e);

        }

    }

    // messageReceived(String message) will be implemented in MainActivity

    public interface OnMessageReceived {
        public void messageReceived(ArrayList<String> message);

    }
}