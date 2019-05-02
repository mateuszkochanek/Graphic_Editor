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
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

//Surface we are painting on, created in App
class Surface extends JPanel {
    public Graphics2D g2d;
    public List<Figure> figures = new ArrayList<>();

    public Surface() {
        setBackground(new Color(254, 254, 254));
        setOpaque(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g);
        for (Figure figure : figures) {
            figure.draw(g);
        }
    }
}

// Class of figures that i keep in a list on my surface
class Figure {
    public float locX = 0;
    public float locY = 0;
    private float width = 100f;
    private float height = 100f;
    private String FigName;
    private Color color;
    private Rectangle2D.Float rect;
    private Ellipse2D.Float ellipse;

    public Figure(int locX, int locY, String x) {
        this.locX = locX;
        this.locY = locY;
        FigName = x;
        if (FigName.compareTo("Circle") == 0)
        {
            ellipse = new Ellipse2D.Float(locX, locY, 100f, 100f);
        }
        if (FigName.compareTo("Rectangle") == 0)
        {
            rect = new Rectangle2D.Float(locX, locY, 200f, 100f);
        }
        //if (FigName.compareTo("Polygon") == 0)
            //{}
    }

    public boolean isHit (float x, float y) {
        if (FigName.compareTo("Circle") == 0)
            return ellipse.getBounds2D().contains(x, y);
        if (FigName.compareTo("Rectangle") == 0)
            return rect.getBounds2D().contains(x, y);
        //if (FigName.compareTo("Polygon") == 0)
        //;  
        return false;        
    }

    public void addX(float x) {
        if (FigName.compareTo("Circle") == 0)
            ellipse.x += x;
        if (FigName.compareTo("Rectangle") == 0)
            rect.x += x;;
        //if (FigName.compareTo("Polygon") == 0)
            //;  
        this.locX += x;
    }

    public void addY(float y) {
        
        if (FigName.compareTo("Circle") == 0)
            ellipse.y += y;
        if (FigName.compareTo("Rectangle") == 0)
            rect.y += y;
        //if (FigName.compareTo("Polygon") == 0)
            //;  
        this.locY += y;
    }

    public void addWidth(float w) {
        
        if (FigName.compareTo("Circle") == 0)
            ellipse.width += w;
        if (FigName.compareTo("Rectangle") == 0)
            rect.width += w;
        //if (FigName.compareTo("Polygon") == 0)
            //;  
    }

    public void addHeight(float h) {
        
        if (FigName.compareTo("Circle") == 0)
            ellipse.height += h;
        if (FigName.compareTo("Rectangle") == 0)
            rect.height += h;
        //if (FigName.compareTo("Polygon") == 0)
            //;  
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);

        if (FigName.compareTo("Circle") == 0)
            g2d.fill(ellipse);
        if (FigName.compareTo("Rectangle") == 0)
            g2d.fill(rect);
        if (FigName.compareTo("Polygon") == 0)
            ;
        // g2d.fillOval(locX, locY, 10, 10);
    }
}

// Adapter used for creating new shapes
class ShapeCreationAdapter extends MouseAdapter  {
    public int x;
    public int y;
    public String Name;
    public Figure figure;
    public Surface surface;

    public ShapeCreationAdapter(String x, Surface sur) {
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
}

class ShapeEditionAdapter extends MouseAdapter  {
    public float x;
    public float y;
    private Figure figure;
    public String Name;
    public Surface surface;
    boolean IsRightPressed, IsLeftPressed;

    public ShapeEditionAdapter(String x, Surface sur) {
        Name = x;
        surface = sur;
    }

    public void mousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        IsLeftPressed = SwingUtilities.isLeftMouseButton(e);
        IsRightPressed = SwingUtilities.isRightMouseButton(e);
        for (Figure figure_test : surface.figures) {
            if(figure_test.isHit(x,y))
            {
                figure = figure_test;
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        System.out.println(" ddddd");
        if(IsLeftPressed && !IsRightPressed)
            doMove(e);
        else if(IsRightPressed && !IsLeftPressed)
            doResize(e);
    }
    public void doMove(MouseEvent e) {
        float dx = e.getX() - x;
        float dy = e.getY() - y;

        figure.addX(dx);
        figure.addY(dy);

        surface.repaint();
        x+=dx;
        y+=dy;
    }
    public void doResize(MouseEvent e) {
        float dx = e.getX()-x;
        float dy = e.getY()-y;

        figure.addHeight(dy);
        figure.addWidth(dx);

        surface.repaint();
        x+=dx;
        y+=dy;
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
        surface.addMouseListener(new ShapeCreationAdapter(super.getText(), surface));
    }
}
class MyCheckBox extends JCheckBox implements ActionListener{
    Surface surface;
    MyButton CircleButton,RectangleButton,PolygonButton;
    public MyCheckBox(String text, Surface sur, MyButton C, MyButton R, MyButton P) {
        surface = sur;
        super.setText(text);
        addActionListener(this);
        CircleButton = C; 
        RectangleButton = R; 
        PolygonButton = P;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MouseListener[] listerners = surface.getMouseListeners();
        for (MouseListener listener : listerners) {
            surface.removeMouseListener(listener);
        }
        MouseMotionListener[] mlisterners = surface.getMouseMotionListeners();
        for (MouseMotionListener mlistener : mlisterners) {
            surface.removeMouseMotionListener(mlistener);
        }
        if(super.isSelected())
        {
            MouseAdapter ma = new ShapeEditionAdapter(super.getText(), surface);
            surface.addMouseListener(ma);
            surface.addMouseMotionListener(ma);
            CircleButton.setEnabled(false);
            RectangleButton.setEnabled(false);
            PolygonButton.setEnabled(false);
        }
        else
        {
            CircleButton.setEnabled(true);
            RectangleButton.setEnabled(true);
            PolygonButton.setEnabled(true);
        }
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
        MyButton CircleButton = new MyButton("Circle", surface);
        MyButton RectangleButton = new MyButton("Rectangle", surface);
        MyButton PolygonButton = new MyButton("Polygon", surface);
        MyCheckBox EditButton  = new MyCheckBox("Edit",surface,CircleButton,RectangleButton,PolygonButton);

        toolbar.add(CircleButton);
        toolbar.add(RectangleButton);
        toolbar.add(PolygonButton);
        toolbar.add(EditButton);

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