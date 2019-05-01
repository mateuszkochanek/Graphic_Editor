package Edytor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

class Surface extends JPanel {
    public Graphics2D g2d;
    private int locX = 0;
    private int locY = 0;
    private String FigName;
    public List<Figure> figures = new ArrayList<>();

    public Surface() {
        setBackground(new Color(200, 0, 0));
        setOpaque(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (Figure figure : figures) {
            figure.draw(g);
        }
    }
}

class Figure {
    private int locX = 0;
    private int locY = 0;
    private String FigName;

    public Figure(int locX, int locY, String x) {
        this.locX = locX;
        this.locY = locY;
        FigName = x;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);

        if (FigName.compareTo("Circle") == 0)
            g2d.fillOval(locX, locY, 100, 100);
        if (FigName.compareTo("Rectangle") == 0)
            g2d.fillRect(locX, locY, 200, 100);
        if (FigName.compareTo("Polygon") == 0)
            ;
        // g2d.fillOval(locX, locY, 10, 10);
    }
}

class ClickAdapter extends MouseAdapter /* implements Runnable */ {
    public int x;
    public int y;
    public String Name;
    public Figure figure;
    public Surface surface;

    public ClickAdapter(String x, Surface sur) {
        Name = x;
        surface = sur;
    }

    public void mouseClicked(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        figure = new Figure(x, y, Name);
        surface.figures.add(figure);
        surface.repaint();
        System.out.println(Name);
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    public Figure GetFigure() {
        return figure;
    }

}

class MyButton extends JButton implements ActionListener {

    Surface surface;

    public MyButton(String text, Surface sur) {
        surface = sur;
        super.setText(text);
        addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MouseListener[] listerners = surface.getMouseListeners();
        for (MouseListener listener : listerners) {
            surface.removeMouseListener(listener);
        }
        surface.addMouseListener(new ClickAdapter(super.getText(), surface));
    }
}


public class App extends JFrame {
    private Surface surface;
    public App() {
        initUI();
    }

    private void initUI() {
        surface = new Surface();
        add(surface, BorderLayout.CENTER);
        CreateToolBar();

        setTitle("Edytor");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void CreateToolBar() {

        JToolBar toolbar = new JToolBar("Options",1);
        MyButton CButton = new MyButton("Circle", surface);
        MyButton RButton = new MyButton("Rectangle", surface);
        MyButton PButton = new MyButton("Polygon", surface);
        JCheckBox Edit  = new JCheckBox("Edit");

        toolbar.add(CButton);
        toolbar.add(RButton);
        toolbar.add(PButton);
        toolbar.add(Edit);

        add(toolbar, BorderLayout.EAST);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                App app = new App();
                app.setVisible(true);
            }
        });
    }
}