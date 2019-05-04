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
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

//Surface we are painting on, created in App
class Surface extends JPanel{
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

class Point implements Serializable{
    float x;
    float y;

    Point(float locX, float locY) {
        x = locX;
        y = locY;
    }

    void PointAddX(float dx) {
        x += dx;
    }

    void PointAddY(float dy) {
        y += dy;
    }

    void MakeItMiddle(float number) {
        x /= number;
        y /= number;
    }
}

class Polygon implements Serializable{
    float firstX;
    float firstY;
    public Point middle = new Point(0, 0);
    public Point pointOfClick = new Point(0, 0);
    int minPointIndex;
    public List<Point> points = new ArrayList<>();
    GeneralPath PolygonShape = new GeneralPath();

    Polygon(float locX, float locY) {
        points.add(new Point(locX, locY));
        firstX = locX;
        firstY = locY;
        PolygonShape.moveTo(firstX, firstY);
    }

    void AddPoint(float locX, float locY) {
        points.add(new Point(locX, locY));
        PolygonShape.lineTo(points.get(points.size() - 1).x, points.get(points.size() - 1).y);
    }

    void GetPointOfClick(float locX, float locY) {
        pointOfClick = new Point(locX, locY);
        Point tempPoint = new Point(0, 0);
        Point minPoint = points.get(0);
        minPointIndex = 0;
        float wayToMinP = (float) java.lang.Math.sqrt((minPoint.x - pointOfClick.x) * (minPoint.x - pointOfClick.x)
                + (minPoint.y - pointOfClick.y) * (minPoint.y - pointOfClick.y));
        float wayToTempP = wayToMinP;
        for (int i = 0; i < points.size(); i++) {
            tempPoint = points.get(i);
            wayToTempP = (float) java.lang.Math.sqrt((tempPoint.x - pointOfClick.x) * (tempPoint.x - pointOfClick.x)
                    + (tempPoint.y - pointOfClick.y) * (tempPoint.y - pointOfClick.y));
            if (wayToTempP < wayToMinP) {
                minPointIndex = i;
                minPoint = tempPoint;
                wayToMinP = wayToTempP;
            }
        }
    }

    void incX(float dx) {
        firstX += dx;
        for (int i = 0; i < points.size(); i++) {
            points.get(i).PointAddX(dx);
        }

    }

    void incY(float dy) {
        firstY += dy;
        for (int i = 0; i < points.size(); i++) {
            points.get(i).PointAddY(dy);
        }
    }

    void incHeight(float dy) {
        points.get(minPointIndex).y += dy;
        if (minPointIndex == 0)
            firstY += dy;
    }

    void incWidth(float dx) {
        points.get(minPointIndex).x += dx;
        if (minPointIndex == 0)
            firstX += dx;
    }

    void Recreate() {
        PolygonShape = new GeneralPath();
        PolygonShape.moveTo(firstX, firstY);
        for (int i = 1; i < points.size(); i++) {
            PolygonShape.lineTo(points.get(i).x, points.get(i).y);
        }
        CalculateMiddle();
    }

    void CalculateMiddle() {
        middle.x = firstX;
        middle.y = firstY;
        for (int i = 1; i < points.size(); i++) {
            middle.x += points.get(i).x;
            middle.y += points.get(i).y;
        }
        middle.MakeItMiddle(points.size());
    }
}

// Class of figures that i keep in a list on my surface
class Figure implements Serializable{
    public float locX = 0;
    public float locY = 0;
    private float width = 100f;
    private float height = 100f;
    public String FigName;
    public Color color;
    private Rectangle2D.Float rect;
    private Ellipse2D.Float ellipse;
    protected Polygon polygon;

    public Figure(int locX, int locY, String x) {
        this.locX = locX;
        this.locY = locY;
        color = Color.BLACK;
        FigName = x;
        if (FigName.compareTo("Circle") == 0) {
            ellipse = new Ellipse2D.Float(locX, locY, 100f, 100f);
        }
        if (FigName.compareTo("Rectangle") == 0) {
            rect = new Rectangle2D.Float(locX, locY, 200f, 100f);
        }
        if (FigName.compareTo("Polygon") == 0) {
            polygon = new Polygon(locX, locY);
        }
    }

    public boolean isHit(float x, float y) {
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
            rect.x += x;
        ;
        if (FigName.compareTo("Polygon") == 0) {
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
        if (FigName.compareTo("Polygon") == 0) {
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
        if (FigName.compareTo("Polygon") == 0) {
            polygon.incWidth(w);
            polygon.Recreate();
        }
    }

    public void addHeight(float h) {

        if (FigName.compareTo("Circle") == 0)
            ellipse.height += h;
        if (FigName.compareTo("Rectangle") == 0)
            rect.height += h;
        if (FigName.compareTo("Polygon") == 0) {
            polygon.incHeight(h);
            polygon.Recreate();
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);

        if (FigName.compareTo("Circle") == 0)
            g2d.fill(ellipse);
        if (FigName.compareTo("Rectangle") == 0)
            g2d.fill(rect);
        if (FigName.compareTo("Polygon") == 0) {
            g2d.fill(polygon.PolygonShape);
            g2d.setColor(Color.RED);
            g2d.drawLine((int) polygon.middle.x, (int) polygon.middle.y, (int) polygon.middle.x + 1,
                    (int) polygon.middle.y + 1);// TODO delet this, only for testing purposes
        }
        // g2d.fill(polygon.PolygonShape);
    }
}

// Adapter used for creating new shapes
class ShapeCreationAdapter extends MouseAdapter {
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
        if (Name.compareTo("Polygon") == 0 && !clicked) {
            clicked = true;
            figure = new Figure(x, y, Name);
            surface.figures.add(figure);
        } else if (Name.compareTo("Polygon") == 0 && clicked) {
            (surface.figures.get(surface.figures.size() - 1)).polygon.AddPoint(x, y);
            (surface.figures.get(surface.figures.size() - 1)).polygon.CalculateMiddle();
            surface.repaint();
        } else {
            figure = new Figure(x, y, Name);
            surface.figures.add(figure);
            surface.repaint();
            System.out.println(Name);
        }

    }
}

class ShapeEditionAdapter extends MouseAdapter {
    public float x;
    public float y;
    private Figure figure;
    public String Name;
    public String HitFigureName;
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
            if (figure_test.isHit(x, y)) {
                figure = figure_test;
                if (IsRightPressed && !IsLeftPressed && figure.FigName.compareTo("Polygon") == 0) {
                    figure.polygon.GetPointOfClick(x, y);
                }

            }
        }
        if (Name.compareTo("Choose Color") == 0) {
            figure.color = JColorChooser.showDialog(null, "Change Color", figure.color);
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
        if (IsLeftPressed && !IsRightPressed)
            doMove(e);
        else if (IsRightPressed && !IsLeftPressed)
            doResize(e);
    }

    public void doMove(MouseEvent e) {
        float dx = e.getX() - x;
        float dy = e.getY() - y;

        figure.addX(dx);
        figure.addY(dy);

        surface.repaint();
        x += dx;
        y += dy;
    }

    public void doResize(MouseEvent e) {
        float dx = e.getX() - x;
        float dy = e.getY() - y;

        figure.addHeight(dy);
        figure.addWidth(dx);

        surface.repaint();
        x += dx;
        y += dy;
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
        //TODO usun
        MouseListener[] listerners = surface.getMouseListeners();
        for (MouseListener listener : listerners) {
            surface.removeMouseListener(listener);
        }
        if (super.getText().compareTo("Choose Color") == 0) {
            surface.addMouseListener(new ShapeEditionAdapter(super.getText(), surface));
        }
        if (super.getText().compareTo("Info") == 0) {
            JOptionPane.showMessageDialog(surface,
                    "Made by Mateusz Kochanek\nCreated to make Shapes\nWhen in Edit mode:\n-> LeftMouse Button to move\n-> RightMouseButton to resize");
        } else
            surface.addMouseListener(new ShapeCreationAdapter(super.getText(), surface));
    }
}

class MyCheckBox extends JCheckBox implements ActionListener {
    Surface surface;
    MyButton CircleButton, RectangleButton, PolygonButton, ColorChooserButton;

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
        if (super.isSelected()) {
            MouseAdapter ma = new ShapeEditionAdapter(super.getText(), surface);
            surface.addMouseListener(ma);
            surface.addMouseMotionListener(ma);
            CircleButton.setEnabled(false);
            RectangleButton.setEnabled(false);
            PolygonButton.setEnabled(false);
            ColorChooserButton.setEnabled(true);
        } else {
            CircleButton.setEnabled(true);
            RectangleButton.setEnabled(true);
            PolygonButton.setEnabled(true);
            ColorChooserButton.setEnabled(false);
        }
    }
}

class MyMenuItem extends JMenuItem implements ActionListener {
    Surface surface;
    String Name;
    App appW;

    MyMenuItem(String text, Surface sur, App app) {
        surface = sur;
        Name = text;
        appW = app;
        super.setText(Name);
        addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (super.getText().compareTo("Open") == 0) {
            String fileName = JOptionPane.showInputDialog(surface, "Podaj nazwę pliku:");
            ObjectInputStream pl2 = null;
            Surface sur = null;
            try {
                fileName ="src/main/java/Edytor/" + fileName;
                pl2 = new ObjectInputStream(new FileInputStream(fileName));
                sur = (Surface) pl2.readObject();
                System.out.println("Donzo");

            } catch (EOFException ex) {
                System.out.println("Koniec pliku");
            } catch (FileNotFoundException e1) {
                System.out.println("Brak pliku");
            } catch (IOException e1) {
                System.out.println(e1.getMessage());
            } catch (ClassNotFoundException e1) {
                System.out.println("Brak Takiej klasy");
            }
 
            finally{
               if(pl2!=null)
                    try {
                        pl2.close();
                        appW.ChangeSurface(sur);
                    } catch (IOException e1) {
                        System.out.println("Nope");
                    }
            }
        }
        else if (super.getText().compareTo("Save") == 0)
        {
            String fileName = JOptionPane.showInputDialog(surface, "Podaj nazwę pliku:");
            ObjectOutputStream pl=null;
            try{
                fileName ="src/main/java/Edytor/" + fileName;
                pl=new ObjectOutputStream(new FileOutputStream(fileName)); 
                pl.writeObject(surface);
                pl.flush();
            } catch (IOException e1) {
                System.out.println(e1.getMessage());
            }
            finally{
                if(pl!=null)
                    try {
                        pl.close();
                    } catch (IOException e1) {
                        System.out.println("Nope");
                    }
        }
        }
        else if (super.getText().compareTo("New") == 0)
        {
            appW.NewSurface();
        }
    }
}
public class App extends JFrame {
    public Surface surface = new Surface();
    public App() {
        initUI();
    }

    private void initUI() {
        add(surface, BorderLayout.CENTER);
        CreateToolBar();

        setTitle("Edytor");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public void ChangeSurface(Surface sur)
    {
        this.remove(surface);
        this.surface = sur;
        add(surface, BorderLayout.CENTER);
        this.surface.repaint();
    }
    public void NewSurface()
    {
        System.out.println("aaaa plox nie czysc plox");
        this.remove(surface);
        this.surface = new Surface();
        add(surface, BorderLayout.CENTER);
        this.surface.repaint();
        this.validate();
    }
    private void CreateToolBar() {

        JToolBar toolbar = new JToolBar("Options",1);
        JMenuBar menubar = new JMenuBar();
        MyMenuItem Open = new MyMenuItem("Open",surface,this);
        MyMenuItem Save = new MyMenuItem("Save",surface,this);
        MyMenuItem New = new MyMenuItem("New",surface,this);
        MyButton InfoButton = new MyButton("Info",surface);
        JMenu fileMenu = new JMenu("File");
        MyButton CircleButton = new MyButton("Circle", surface);
        MyButton RectangleButton = new MyButton("Rectangle", surface);
        MyButton PolygonButton = new MyButton("Polygon", surface);
        MyButton ColorChooserButton = new MyButton("Choose Color",surface);  
        MyCheckBox EditButton  = new MyCheckBox("Edit",surface,CircleButton,RectangleButton,PolygonButton,ColorChooserButton);
        ColorChooserButton.setEnabled(false);

        //making button look like a manu item
        InfoButton.setOpaque(true);
        InfoButton.setContentAreaFilled(false);
        InfoButton.setBorderPainted(false);
        InfoButton.setFocusable(false);

        toolbar.add(CircleButton);
        toolbar.add(RectangleButton);
        toolbar.add(PolygonButton);
        toolbar.add(EditButton);
        toolbar.add(ColorChooserButton);

        menubar.add(fileMenu);
        fileMenu.add(Open);
        fileMenu.add(Save);
        fileMenu.add(New);
        menubar.add(InfoButton);

        add(toolbar, BorderLayout.EAST);
        add(menubar, BorderLayout.NORTH);
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