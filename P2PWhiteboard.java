import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class P2PWhiteboard extends Application {
    private String remoteIp = "10.210.xx.xx"; // ***請填入組員的 ZeroTier IP***
    private static final int PORT = 50001;
    private GraphicsContext gc;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();
        gc.setLineWidth(2.0);

        // 滑鼠拖動事件：本地繪圖 + 發送座標
        canvas.setOnMouseDragged(e -> {
            double x = e.getX();
            double y = e.getY();
            drawPoint(x, y, Color.BLACK); // 本地畫
            sendData(x + "," + y);        // 傳給對方
        });

        startServer(); // 啟動接收端

        primaryStage.setScene(new Scene(new StackPane(canvas)));
        primaryStage.setTitle("P2P 協作白板 - 我的 IP: 10.210.63.104");
        primaryStage.show();
    }

    // 在畫布上畫一個點
    private void drawPoint(double x, double y, Color color) {
        Platform.runLater(() -> {
            gc.setFill(color);
            gc.fillOval(x, y, 4, 4);
        });
    }

    // TCP Server：接收對方的繪圖座標
    private void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                while (true) {
                    try (Socket socket = serverSocket.accept();
                         BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        String msg = in.readLine();
                        if (msg != null) {
                            String[] coords = msg.split(",");
                            drawPoint(Double.parseDouble(coords[0]), 
                                      Double.parseDouble(coords[1]), Color.RED); // 對方畫的用紅色顯示
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                }
            } catch (IOException e) { e.printStackTrace(); }
        }).start();
    }

    // TCP Client：傳送自己的座標給對方
    private void sendData(String data) {
        new Thread(() -> {
            try (Socket socket = new Socket(remoteIp, PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(data);
            } catch (IOException e) {
                // 連不到對方是正常的，除非對方也開啟了程式
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}