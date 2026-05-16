import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class WhiteboardPanel extends JPanel {

    private Color currentColor = Color.BLACK;
    private int brushSize = 3;
    private Point lastPoint = null;
    private boolean isEraser = false;

    // Store all strokes to repaint
    private final ArrayList<int[]> strokes = new ArrayList<>();
    // [x1, y1, x2, y2, r, g, b, brushSize]

    public WhiteboardPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastPoint = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastPoint != null) {
                    Point current = e.getPoint();
                    Color drawColor = isEraser ? Color.WHITE : currentColor;
                    drawLine(lastPoint.x, lastPoint.y, current.x, current.y, drawColor, brushSize);
                    lastPoint = current;
                }
            }
        });
    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color, int size) {
        strokes.add(new int[] { x1, y1, x2, y2, color.getRed(), color.getGreen(), color.getBlue(), size });
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int[] s : strokes) {
            g2d.setColor(new Color(s[4], s[5], s[6]));
            g2d.setStroke(new BasicStroke(s[7]));
            g2d.drawLine(s[0], s[1], s[2], s[3]);
        }
    }

    public void clear() {
        strokes.clear();
        repaint();
    }

    public void setColor(Color color) {
        this.currentColor = color;
    }

    public void setBrushSize(int size) {
        this.brushSize = size;
    }

    public void setEraser(boolean eraser) {
        this.isEraser = eraser;
    }
}