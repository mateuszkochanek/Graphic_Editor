package Edytor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

/**
 * Surface is an object we are painting on.
 * 
 * @author      Mateusz Kochanek
 */
class Surface extends JPanel{
    /** List of {@link Figure} objects created */
    public List<Figure> figures = new ArrayList<>();

    public Surface() {
        setBackground(new Color(254, 254, 254));
        setOpaque(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Figure figure : figures) {
            figure.draw(g);
        }
    }
}
/**
 * Object containing coordinates of a point on {@link Surface}
 * 
 * @author      Mateusz Kochanek
 * @see       Surface
 */
class Point implements Serializable{
    public float x;
    public float y;
    Point(float locX, float locY) {
        x = locX;
        y = locY;
    }
}
/**
 * Object defining Polygon shape and its methods.
 * 
 * @author      Mateusz Kochanek
 */
class Polygon implements Serializable{

    /** x coordinate of first point of polygon */
    private float firstX;
    /**y coordinate of first point of polygon*/
    private float firstY;
    /** Point of Click during resizing of a Polygon*/
    private Point pointOfClick = new Point(0, 0);
    /** Index of a point which is closest to {@link pointOfClick}*/
    private int minPointIndex;
    /** List of Polygon's points*/
    private List<Point> points = new ArrayList<>();
    /** Shape of Polygon*/
    public GeneralPath PolygonShape = new GeneralPath();

    Polygon(float locX, float locY) {
        points.add(new Point(locX, locY));
        firstX = locX;
        firstY = locY;
        PolygonShape.moveTo(firstX, firstY);
    }

    /** Adds a Point to {@link points} */
    void AddPoint(float locX, float locY) {
        points.add(new Point(locX, locY));
        PolygonShape.lineTo(points.get(points.size() - 1).x, points.get(points.size() - 1).y);
    }

    /** Gets {@link pointOfClick} */
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

    /** Increases x of  {@link points} during {@link Edytor.ShapeEditionAdapter#doMove(MouseEvent)}*/
    void incX(float dx) {
        firstX += dx;
        for (int i = 0; i < points.size(); i++) {
            points.get(i).x += dx;
        }

    }

    /** Increases y of  {@link points} during {@link Edytor.ShapeEditionAdapter#doMove(MouseEvent)}*/
    void incY(float dy) {
        firstY += dy;
        for (int i = 0; i < points.size(); i++) {
            points.get(i).y += dy;
        }
    }

    /** Increases y of point with {@link minPointIndex} */
    void incHeight(float dy) {
        points.get(minPointIndex).y += dy;
        if (minPointIndex == 0)
            firstY += dy;
    }

    /** Increases x of point with {@link minPointIndex} */
    void incWidth(float dx) {
        points.get(minPointIndex).x += dx;
        if (minPointIndex == 0)
            firstX += dx;
    }

    /** 
     * Recreates Polygon when  {@link points} are updated in {@link ShapeEditionAdapter}
     * @see Edytor.ShapeEditionAdapter#doMove(MouseEvent)
     * @see Edytor.ShapeEditionAdapter#doResize(MouseEvent)
    */
    void Recreate() {
        PolygonShape = new GeneralPath();
        PolygonShape.moveTo(firstX, firstY);
        for (int i = 1; i < points.size(); i++) {
            PolygonShape.lineTo(points.get(i).x, points.get(i).y);
        }
    }
}

/**
 * Object defining Figures and their methods.
 * 
 *  @author Mateusz Kochanek
 * 
 */ 
class Figure implements Serializable{
    /** Name of a Figure passed from {@link Edytor.ShapeEditionAdapter} or {@link Edytor.ShapeCreationAdapter} */
    public String FigName;
    /** Color of a Figure*/
    public Color color;
    /** Object containing created Rectangle (if we create one)*/
    private Rectangle2D.Float rect;
    /** Object containing created Ellipse (if we create one)*/
    private Ellipse2D.Float ellipse;
    /** Object containing created Polygon (if we create one)*/
    public Polygon polygon;

    public Figure(float locX, float locY, String x) {
        color = Color.BLACK;
        FigName = x;

        if (FigName.compareTo("Circle") == 0) {
            ellipse = new Ellipse2D.Float(locX, locY, 100f, 100f);
        } else if (FigName.compareTo("Rectangle") == 0) {
            rect = new Rectangle2D.Float(locX, locY, 200f, 100f);
        } else if (FigName.compareTo("Polygon") == 0) {
            polygon = new Polygon(locX, locY);
        }
    }
    /** Method checking of Figure was Clicked in {@link Edytor.ShapeEditionAdapter}*/
    public boolean isHit(float x, float y) {
        if (FigName.compareTo("Circle") == 0){
            return ellipse.getBounds2D().contains(x, y);
        } else if (FigName.compareTo("Rectangle") == 0){
            return rect.getBounds2D().contains(x, y);
        } else if (FigName.compareTo("Polygon") == 0){
            return polygon.PolygonShape.getBounds2D().contains(x, y);
        }

        return false;
    }
    /** Moves clicked Figure for x*/
    public void addX(float x) {
        if (FigName.compareTo("Circle") == 0){
            ellipse.x += x;
        } else if (FigName.compareTo("Rectangle") == 0){
            rect.x += x;
        } else if (FigName.compareTo("Polygon") == 0) {
            polygon.incX(x);
            polygon.Recreate();
        }
    }
    /** Moves clicked Figure for y*/
    public void addY(float y) {

        if (FigName.compareTo("Circle") == 0){
            ellipse.y += y;
        } else if (FigName.compareTo("Rectangle") == 0){
            rect.y += y;
        } else if (FigName.compareTo("Polygon") == 0) {
            polygon.incY(y);
            polygon.Recreate();
        }
    }
    /** Increases Width of clicked figure*/
    public void addWidth(float w) {

        if (FigName.compareTo("Circle") == 0){
            ellipse.width += w;
        } else if (FigName.compareTo("Rectangle") == 0){
            rect.width += w;
        } else if (FigName.compareTo("Polygon") == 0) {
            polygon.incWidth(w);
            polygon.Recreate();
        }
    }
    /** Increases Height of clicked figure*/
    public void addHeight(float h) {

        if (FigName.compareTo("Circle") == 0){
            ellipse.height += h;
        } else if (FigName.compareTo("Rectangle") == 0){
            rect.height += h;
        } else if (FigName.compareTo("Polygon") == 0) {
            polygon.incHeight(h);
            polygon.Recreate();
        }
    }
    /** Draws figure, diffrently depending on {@link FigName}*/
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);

        if (FigName.compareTo("Circle") == 0){
            g2d.fill(ellipse);
        } else if (FigName.compareTo("Rectangle") == 0){
            g2d.fill(rect);
        } else if (FigName.compareTo("Polygon") == 0) {
            g2d.fill(polygon.PolygonShape);
            g2d.setColor(Color.RED);
        }
    }
}

/**
 * Used during Figure Creation
 * 
 *  @author Mateusz Kochanek
 * 
 */ 
class ShapeCreationAdapter extends MouseAdapter {
    /**Name of {@link Figure} we are creating */
    private String NameOfFigure;
    /**{@link Figure} we are creating */
    private Figure figure;
    /**{@link Surface} we are painting on */
    private Surface surface;
    /**Informs us if we have already clicked befor with our mouse during creation of {@link figure}*/
    private boolean clicked = false;

    public ShapeCreationAdapter(String Name, Surface sur) {
        NameOfFigure = Name;
        surface = sur;
    }

    public void mouseClicked(MouseEvent e) {
        float x = e.getX();
        float y = e.getY();
        if (NameOfFigure.compareTo("Polygon") == 0 && !clicked) {
            clicked = true;
            figure = new Figure(x, y, NameOfFigure);
            surface.figures.add(figure);
        } else if (NameOfFigure.compareTo("Polygon") == 0 && clicked) {
            (surface.figures.get(surface.figures.size() - 1)).polygon.AddPoint(x, y);
            surface.repaint();
        } else {
            figure = new Figure(x, y, NameOfFigure);
            surface.figures.add(figure);
            surface.repaint();
            System.out.println(NameOfFigure);
        }

    }
}
/**
 * Used during Figure Edition
 * 
 *  @author Mateusz Kochanek
 * 
 */ 
class ShapeEditionAdapter extends MouseAdapter {
    /**x coordinate of our mouse on {@link Surface}*/
    private float x;
    /**y coordinate of our mouse on {@link Surface}*/
    private float y;
    /**{@link Figure} we are editing*/
    private Figure figure;
    /**Name of EditionAdapter created, Move and Resize/Color Choosing*/
    private String Name;
    /**{@link Surface} we are painting on */
    private Surface surface;
    /**Tells if we clicked with left/right mouse button*/
    private boolean IsRightPressed, IsLeftPressed;

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
            try {
                figure.color = JColorChooser.showDialog(null, "Change Color", figure.color);
                surface.repaint();
            } catch(NullPointerException e1){
                System.out.println("No figure was chosen");
            }
            
            MouseMotionListener[] mlisterners = surface.getMouseMotionListeners();
            for (MouseMotionListener mlistener : mlisterners) {
               surface.removeMouseMotionListener(mlistener);
            }
            MouseListener[] listerners = surface.getMouseListeners();
            for (MouseListener listener : listerners) {
                surface.removeMouseListener(listener);
            }

            MouseAdapter ma = new ShapeEditionAdapter("Edit", surface);
            surface.addMouseListener(ma);
            surface.addMouseMotionListener(ma);
        }

    }

    public void mouseDragged(MouseEvent e) {
        if (IsLeftPressed && !IsRightPressed){
            doMove(e);
        } else if (IsRightPressed && !IsLeftPressed){
            doResize(e);
        }
    }
    /**Moves clicked {@link figure} */
    private void doMove(MouseEvent e) {
        float dx = e.getX() - x;
        float dy = e.getY() - y;

        figure.addX(dx);
        figure.addY(dy);

        surface.repaint();
        x += dx;
        y += dy;
    }
    /**Resizes clicked {@link figure} */
    private void doResize(MouseEvent e) {
        float dx = e.getX() - x;
        float dy = e.getY() - y;

        figure.addHeight(dy);
        figure.addWidth(dx);

        surface.repaint();
        x += dx;
        y += dy;
    }

}
/**
 * Used to implement Action listener to Buttons
 * 
 *  @author Mateusz Kochanek
 * 
 */ 
class MyButton extends JButton implements ActionListener {
    /**{@link Surface} we are painting on */
    private Surface surface;

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
        if (super.getText().compareTo("Choose Color") == 0) {
            surface.addMouseListener(new ShapeEditionAdapter(super.getText(), surface));
        } else if (super.getText().compareTo("Info") == 0) {
            JOptionPane.showMessageDialog(surface,
                    "Made by Mateusz Kochanek\nCreated to make Shapes\nWhen in Edit mode:\n-> LeftMouse Button to move\n-> RightMouseButton to resize");
        } else {
            surface.addMouseListener(new ShapeCreationAdapter(super.getText(), surface));
        }
    }
}
/**
 * Used to implement Action listener to CheckBoxes, used to switch between editing modes
 * 
 *  @author Mateusz Kochanek
 * 
 */ 
class MyCheckBox extends JCheckBox implements ActionListener {
    /**{@link Surface} we are painting on */
    private Surface surface;
    /**Turned off and on when we switch editing modes */
    private MyButton CircleButton, RectangleButton, PolygonButton, ColorChooserButton;

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
/**
 * Used to implement Action listener to MenuItems
 * 
 *  @author Mateusz Kochanek
 * 
 */ 
class MyMenuItem extends JMenuItem implements ActionListener {
    /**{@link Surface} we are painting on */
    Surface surface;
    /**Name of action we want */
    String Name;
    /**{@link App} we are working on*/
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
                fileName ="src/main/java/Edytor/Saved/" + fileName;
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
        } else if (super.getText().compareTo("Save") == 0) {
            String fileName = JOptionPane.showInputDialog(surface, "Podaj nazwę pliku:");
            ObjectOutputStream pl=null;
            try{
                if(fileName!=null){
                fileName ="src/main/java/Edytor/Saved/" + fileName;
                pl=new ObjectOutputStream(new FileOutputStream(fileName)); 
                pl.writeObject(surface);
                pl.flush();
                }
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
        } else if (super.getText().compareTo("New") == 0) {
            appW.NewSurface();
        }
    }
}
/**
 * App is a program that allows us to crate and edit different shapes
 * 
 * @author      Mateusz Kochanek
 */
public class App extends JFrame {
    /**{@link Surface} we are painting on */
    public Surface surface = new Surface();
    /**Toolbar we are adding to our App*/
    public JToolBar toolbar = new JToolBar();
    public App() {
        initUI();
    }
    /**Creates Graphic interface for App */
    private void initUI() {
        add(surface, BorderLayout.CENTER);
        CreateToolBar();
        CreateMenuBar();

        setTitle("Edytor");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    /**Replaces one surface with the other in {@link MyMenuItem}*/
    public void ChangeSurface(Surface sur)
    {
        this.remove(surface);
        this.remove(toolbar);
        this.surface = sur;
        add(surface, BorderLayout.CENTER);
        CreateToolBar();
        this.surface.repaint();
        this.validate(); 
    }
    /**Replaces one surface with the new one in {@link MyMenuItem}*/
    public void NewSurface()
    {
        this.remove(surface);
        this.remove(toolbar);
        surface = new Surface();
        add(surface, BorderLayout.CENTER);
        CreateToolBar();
        this.surface.repaint();
        this.validate(); 
    }
    /**Creates Toolbar for App */
    private void CreateToolBar() {

        toolbar = new JToolBar("Options",1);
        
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
    /**Creates Menubar for App */
    private void CreateMenuBar() {
        JMenuBar menubar = new JMenuBar();
        MyMenuItem Open = new MyMenuItem("Open",surface,this);
        MyMenuItem Save = new MyMenuItem("Save",surface,this);
        MyMenuItem New = new MyMenuItem("New",surface,this);
        MyButton InfoButton = new MyButton("Info",surface);
        JMenu fileMenu = new JMenu("File");

        //making button look like a manu item
        InfoButton.setOpaque(true);
        InfoButton.setContentAreaFilled(false);
        InfoButton.setBorderPainted(false);
        InfoButton.setFocusable(false);

        menubar.add(fileMenu);
        fileMenu.add(Open);
        fileMenu.add(Save);
        fileMenu.add(New);
        menubar.add(InfoButton);

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