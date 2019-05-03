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
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
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
class Point{
    float x;
    float y;
    Point (float locX,float locY)
    {
        x=locX;
        y=locY;
    }
    void PointAddX(float dx){x+=dx;}
    void PointAddY(float dy){y+=dy;}
}
class Polygon{
    float firstX;
    float firstY;
    public List<Point> points = new ArrayList<>();
    GeneralPath PolygonShape = new GeneralPath();
    Polygon(float locX, float locY)
    {
        points.add(new Point(locX,locY));
        firstX = locX;
        firstY = locY;
        PolygonShape.moveTo(firstX, firstY);
    }

    void AddPoint(float locX, float locY)
    {
        points.add(new Point(locX,locY));
        PolygonShape.lineTo(points.get(points.size()-1).x, points.get(points.size()-1).y);
    }

    void incX(float dx){
        firstX += dx;
        for (int i=0;i<points.size();i++) {
            points.get(i).PointAddX(dx);
        }

    }
    void incY(float dy){
        firstY += dy;
        for (int i=0;i<points.size();i++) {
            points.get(i).PointAddY(dy);
        }
    }
    void Recreate ()
    {
        PolygonShape = new GeneralPath();
        PolygonShape.moveTo(firstX, firstY);
        for (int i=1;i<points.size();i++) {
            PolygonShape.lineTo(points.get(i).x, points.get(i).y);
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
    public Color color;
    private Rectangle2D.Float rect;
    private Ellipse2D.Float ellipse;
    protected Polygon polygon;

    public Figure(int locX, int locY, String x) {
        this.locX = locX;
        this.locY = locY;
        color = Color.BLACK;
        FigName = x;
        if (FigName.compareTo("Circle") == 0)
        {
            ellipse = new Ellipse2D.Float(locX, locY, 100f, 100f);
        }
        if (FigName.compareTo("Rectangle") == 0)
        {
            rect = new Rectangle2D.Float(locX, locY, 200f, 100f);
        }
        if (FigName.compareTo("Polygon") == 0)
        {
            polygon = new Polygon(locX, locY);
        }
    }

    public boolean isHit (float x, float y) {
        if (FigName.compareTo("Circle") == 0)
            return ellipse.getBounds2D().contains(x, y);
        if (FigName.compareTo("Rectangle") == 0)
            return rect.getBounds2D().contains(x, y);
        if (FigName.compareTo("Polygon") == 0)
            return polygon.PolygonShape.getBounds2D().contains(x, y);  
        return false;        
    }

    public void addX(float x) {
        if (FigName.compareTo("Circle") == 0)
            ellipse.x += x;
        if (FigName.compareTo("Rectangle") == 0)
            rect.x += x;;
        if (FigName.compareTo("Polygon") == 0)
            {
                polygon.incX(x);
                polygon.Recreate();
            }  
        this.locX += x;
    }

    public void addY(float y) {
        
        if (FigName.compareTo("Circle") == 0)
            ellipse.y += y;
        if (FigName.compareTo("Rectangle") == 0)
            rect.y += y;
        if (FigName.compareTo("Polygon") == 0)
        {
            polygon.incY(y);
            polygon.Recreate();
        }   
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
        g2d.setColor(color);

        if (FigName.compareTo("Circle") == 0)
            g2d.fill(ellipse);
        if (FigName.compareTo("Rectangle") == 0)
            g2d.fill(rect);
        if (FigName.compareTo("Polygon") == 0)
            g2d.fill(polygon.PolygonShape);
    }
}

class PolygonCreationAdapter extends MouseAdapter{
    public int x;
    public int y;
    public Figure figure;
    public Surface surface;

    public PolygonCreationAdapter(Figure fig, Surface sur) {
        figure = fig;
        surface = sur;
    }
    public void mouseClicked(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        figure.polygon.AddPoint(x,y);
        surface.repaint();
    }
}

// Adapter used for creating new shapes
class ShapeCreationAdapter extends MouseAdapter  {
    public int x;
    public int y;
    public String Name;
    public Figure figure;
    public Surface surface;
    public boolean clicked = false;

    public ShapeCreationAdapter(String x, Surface sur) {
        Name = x;
        surface = sur;
    }

    public void mouseClicked(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        if(Name.compareTo("Polygon")==0 && !clicked)
        {
            clicked=true;
            figure = new Figure(x, y, Name);
            surface.figures.add(figure);
        }
        else if(Name.compareTo("Polygon")==0 && clicked)
        {
            (surface.figures.get(surface.figures.size()-1)).polygon.AddPoint(x,y);
            surface.repaint();
        }
        else
        {
            figure = new Figure(x, y, Name);
            surface.figures.add(figure);
            surface.repaint();
            System.out.println(Name);
        }

    }
}

class ShapeEditionAdapter extends MouseAdapter  {
    public float x;
    public float y;
    private Figure figure;
    public String Name;
    public Surface surface;
    public JColorChooser chooser;
    public Color color;
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
        if(Name.compareTo("Choose Color")==0)
        {
            figure.color = JColorChooser.showDialog(null, "Change Color",figure.color);
            surface.repaint();
            MouseListener[] listerners = surface.getMouseListeners();
            for (MouseListener listener : listerners) {
                surface.removeMouseListener(listener);
            MouseMotionListener[] mlisterners = surface.getMouseMotionListeners();
            for (MouseMotionListener mlistener : mlisterners) {
                    surface.removeMouseMotionListener(mlistener);
                }
            MouseAdapter ma = new ShapeEditionAdapter("Edit", surface);
            surface.addMouseListener(ma);
            surface.addMouseMotionListener(ma);
        }
        }
        
    }

    public void mouseDragged(MouseEvent e) {
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
        if (super.getText().compareTo("Choose Color") == 0)
        {
            surface.addMouseListener(new ShapeEditionAdapter(super.getText(), surface));
        }
        else
            surface.addMouseListener(new ShapeCreationAdapter(super.getText(), surface));
    }
}
class MyCheckBox extends JCheckBox implements ActionListener{
    Surface surface;
    MyButton CircleButton,RectangleButton,PolygonButton,ColorChooserButton;
    public MyCheckBox(String text, Surface sur, MyButton C, MyButton R, MyButton P, MyButton Col) {
        surface = sur;
        super.setText(text);
        addActionListener(this);
        CircleButton = C; 
        RectangleButton = R; 
        PolygonButton = P;
        ColorChooserButton = Col;
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
            ColorChooserButton.setEnabled(true);
        }
        else
        {
            CircleButton.setEnabled(true);
            RectangleButton.setEnabled(true);
            PolygonButton.setEnabled(true);
            ColorChooserButton.setEnabled(false);
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
        MyButton ColorChooserButton = new MyButton("Choose Color",surface);
        MyCheckBox EditButton  = new MyCheckBox("Edit",surface,CircleButton,RectangleButton,PolygonButton,ColorChooserButton);
        ColorChooserButton.setEnabled(false);

        toolbar.add(CircleButton);
        toolbar.add(RectangleButton);
        toolbar.add(PolygonButton);
        toolbar.add(EditButton);
        toolbar.add(ColorChooserButton);

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