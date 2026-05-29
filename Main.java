public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Ask for username and remote IP on startup
            String username = javax.swing.JOptionPane.showInputDialog(null,
                    "Enter your username:", "Username", javax.swing.JOptionPane.PLAIN_MESSAGE);
            if (username == null || username.trim().isEmpty())
                username = "User";

            String remoteIp = javax.swing.JOptionPane.showInputDialog(null,
                    "Enter your friend's ZeroTier IP:", "Remote IP", javax.swing.JOptionPane.PLAIN_MESSAGE);
            if (remoteIp == null || remoteIp.trim().isEmpty())
                remoteIp = "";

            new MainFrame(username, remoteIp);
        });
    }
}