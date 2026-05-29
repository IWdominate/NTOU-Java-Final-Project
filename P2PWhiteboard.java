import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class P2PWhiteboard {
    private String remoteIp = "10.210.xx.xx"; // ***請填入組員的 ZeroTier IP***
    private static final int PORT = 50001;
    private WhiteboardPanel whiteboardPanel;
    private MainFrame mainFrame;

    public P2PWhiteboard(WhiteboardPanel whiteboardPanel, MainFrame mainFrame) {
        this.whiteboardPanel = whiteboardPanel;
        this.mainFrame = mainFrame;
    }

    // TCP Server：接收對方的繪圖座標
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                System.out.println("Server started on port " + PORT);
                while (true) {
                    try (Socket socket = serverSocket.accept();
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(socket.getInputStream()))) {
                        String msg = in.readLine();
                        if (msg != null) {
                            String[] parts = msg.split(",");
                            if (parts[0].equals("DRAW")) {
                                // DRAW,x1,y1,x2,y2,r,g,b,size
                                int x1 = Integer.parseInt(parts[1]);
                                int y1 = Integer.parseInt(parts[2]);
                                int x2 = Integer.parseInt(parts[3]);
                                int y2 = Integer.parseInt(parts[4]);
                                Color color = new Color(
                                        Integer.parseInt(parts[5]),
                                        Integer.parseInt(parts[6]),
                                        Integer.parseInt(parts[7]));
                                int size = Integer.parseInt(parts[8]);
                                // Draw on whiteboard from network
                                SwingUtilities.invokeLater(() -> whiteboardPanel.drawLine(x1, y1, x2, y2, color, size));
                            } else if (parts[0].equals("CLEAR")) {
                                SwingUtilities.invokeLater(() -> whiteboardPanel.clear());
                            } else if (parts[0].equals("CHAT")) {
                                // CHAT,username,message
                                String chatMsg = parts[1] + ": " + parts[2];
                                SwingUtilities.invokeLater(() -> mainFrame.getChatPanel().appendMessage(chatMsg));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // TCP Client：傳送繪圖資料給對方
    public void sendDraw(int x1, int y1, int x2, int y2, Color color, int size) {
        String data = "DRAW," + x1 + "," + y1 + "," + x2 + "," + y2 + ","
                + color.getRed() + "," + color.getGreen() + "," + color.getBlue()
                + "," + size;
        sendData(data);
    }

    // Send clear event
    public void sendClear() {
        sendData("CLEAR");
    }

    // Send chat message
    public void sendChat(String username, String message) {
        sendData("CHAT," + username + "," + message);
    }

    // Generic send
    private void sendData(String data) {
        new Thread(() -> {
            try (Socket socket = new Socket(remoteIp, PORT);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(data);
            } catch (IOException e) {
                // Can't reach peer, that's okay
            }
        }).start();
    }

    public void setRemoteIp(String ip) {
        this.remoteIp = ip;
    }
}