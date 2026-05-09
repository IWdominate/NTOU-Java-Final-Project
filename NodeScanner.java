import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 第一階段：UDP 組播節點發現
 * 參考 AirShit 技術，實作自動握手與在線列表維護
 */
public class NodeScanner {
    // 預設組播位址與埠號 (可自訂)
    private static final String MULTICAST_ADDRESS = "239.255.42.99";
    private static final int PORT = 50000;
    private static final int BUFFER_SIZE = 1024;

    // 儲存目前在線的節點 (IP -> NodeInfo)
    private final Map<String, Long> onlineNodes = new ConcurrentHashMap<>();
    private final String localIp;

    public NodeScanner() throws Exception {
        this.localIp = InetAddress.getLocalHost().getHostAddress();
    }

    // 啟動接收執行緒：監聽誰上線了
    public void startListening() {
        new Thread(() -> {
            try (MulticastSocket socket = new MulticastSocket(PORT)) {
                InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
                NetworkInterface netIf = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
                socket.joinGroup(new InetSocketAddress(group, PORT), netIf);

                System.out.println("UDP 監聽服務已啟動，等待節點加入...");

                byte[] buffer = new byte[BUFFER_SIZE];
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    String senderIp = packet.getAddress().getHostAddress();

                    if (!senderIp.equals(localIp)) {
                        if (message.startsWith("HELLO")) {
                            System.out.println("發現新節點: " + senderIp);
                            onlineNodes.put(senderIp, System.currentTimeMillis());
                            // 收到招呼後回覆，確保對方也知道我在線
                            sendAnnouncement("I_AM_HERE");
                        } else if (message.equals("I_AM_HERE")) {
                            onlineNodes.put(senderIp, System.currentTimeMillis());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 發送廣播：告訴大家「我上線了」
    public void sendAnnouncement(String message) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, group, PORT);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 啟動心跳檢查：移除超時（例如 10 秒沒回應）的節點
    public void startHeartbeatChecker() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000); // 每 5 秒檢查一次
                    long now = System.currentTimeMillis();
                    onlineNodes.entrySet().removeIf(entry -> (now - entry.getValue() > 10000));
                    System.out.println("目前在線節點: " + onlineNodes.keySet());
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        NodeScanner scanner = new NodeScanner();
        scanner.startListening();
        scanner.startHeartbeatChecker();

        // 模擬發送上線通知
        scanner.sendAnnouncement("HELLO_AIRSHIT_PROJECT");
    }

    // jwjqdbqbdbq
    // hbabdabx
}