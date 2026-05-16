import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private WhiteboardPanel whiteboardPanel;
    private ChatPanel chatPanel;
    private JPanel colorIndicator;

    public MainFrame() {
        setTitle("Collaborative Whiteboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        whiteboardPanel = new WhiteboardPanel();
        add(whiteboardPanel, BorderLayout.CENTER);

        chatPanel = new ChatPanel();
        chatPanel.setPreferredSize(new Dimension(300, 700));
        add(chatPanel, BorderLayout.EAST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clearBtn = new JButton("Clear");
        JButton eraserBtn = new JButton("Eraser");
        JButton brushBtn = new JButton("Brush");
        JLabel statusLabel = new JLabel("Status: Offline");

        clearBtn.addActionListener(e -> whiteboardPanel.clear());
        eraserBtn.addActionListener(e -> {
            whiteboardPanel.setEraser(true);
            whiteboardPanel.setBrushSize(20);
        });
        brushBtn.addActionListener(e -> {
            whiteboardPanel.setEraser(false);
            whiteboardPanel.setBrushSize(3);
        });

        // Color indicator box
        colorIndicator = new JPanel();
        colorIndicator.setPreferredSize(new Dimension(24, 24));
        colorIndicator.setBackground(Color.BLACK);
        colorIndicator.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        Color[] colors = {
                Color.BLACK,
                new Color(80, 80, 80),
                new Color(169, 169, 169),
                Color.WHITE,
                new Color(255, 59, 59),
                new Color(255, 140, 0),
                new Color(255, 220, 0),
                new Color(50, 200, 50),
                new Color(0, 122, 255),
                new Color(88, 86, 214),
                new Color(255, 45, 146),
                new Color(139, 69, 19),
        };

        toolbar.add(clearBtn);
        toolbar.add(eraserBtn);
        toolbar.add(brushBtn);
        toolbar.add(new JLabel(" | "));

        for (Color color : colors) {
            JButton colorBtn = new JButton();
            colorBtn.setBackground(color);
            colorBtn.setPreferredSize(new Dimension(24, 24));
            colorBtn.setBorderPainted(false);
            colorBtn.setOpaque(true);
            colorBtn.addActionListener(e -> {
                whiteboardPanel.setColor(color);
                whiteboardPanel.setEraser(false);
                whiteboardPanel.setBrushSize(3);
                colorIndicator.setBackground(color);
            });
            toolbar.add(colorBtn);
        }

        toolbar.add(new JLabel(" | "));

        JButton customColorBtn = new JButton("Custom");
        customColorBtn.addActionListener(e -> {
            JColorChooser colorChooser = new JColorChooser();
            colorChooser.setChooserPanels(new javax.swing.colorchooser.AbstractColorChooserPanel[] {
                    colorChooser.getChooserPanels()[1]
            });
            JDialog dialog = JColorChooser.createDialog(
                    this, "Pick a Color", true, colorChooser,
                    ok -> {
                        Color chosen = colorChooser.getColor();
                        whiteboardPanel.setColor(chosen);
                        whiteboardPanel.setEraser(false);
                        whiteboardPanel.setBrushSize(3);
                        colorIndicator.setBackground(chosen);
                    },
                    null);
            dialog.setVisible(true);
        });

        toolbar.add(customColorBtn);
        toolbar.add(new JLabel(" | Color: "));
        toolbar.add(colorIndicator);
        toolbar.add(new JLabel(" | "));
        toolbar.add(statusLabel);
        add(toolbar, BorderLayout.NORTH);

        setVisible(true);
    }
}