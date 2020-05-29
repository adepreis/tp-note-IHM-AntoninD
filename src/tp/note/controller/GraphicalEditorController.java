package tp.note.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import tp.note.model.NextShapeToDraw;

/**
 *
 * @author Antonin
 */
public class GraphicalEditorController implements Initializable {
    
    private ToggleGroup tgGroup;
    private ArrayList<Shape> shapeList;
    
    private GraphicsContext graphicContext;

    private double lastX;
    private double lastY;
    
    private NextShapeToDraw nextShape;
    
    private Shape selectedShape;
    
    @FXML
    private Label labelOption;
    
    @FXML
    private RadioButton rbSelectMove;
    
    @FXML
    private RadioButton rbEllipse;
    
    @FXML
    private RadioButton rbRectangle;
    
    @FXML
    private RadioButton rbLine;
    
    @FXML
    private ColorPicker colorPicker;
    
    @FXML
    private Button btnDelete;
    
    @FXML
    private Button btnClone;
    
    @FXML
    private Canvas canvas;
    
    @FXML
    private void handleButtonDelete(ActionEvent event) {
        if (selectedShape != null) {
            shapeList.remove(selectedShape);
        }
        updateCanvas();
    }
    
    @FXML
    private void handleButtonClone(ActionEvent event) {
        if (selectedShape != null) {
            Shape duplicatedShape = null;
            if (selectedShape instanceof Rectangle) {
                Rectangle rect = (Rectangle)selectedShape;
                duplicatedShape = new Rectangle(rect.getX() + 10, rect.getY() + 10,
                        rect.getWidth(), rect.getHeight());
            } else if (selectedShape instanceof Ellipse) {
                Ellipse ell = (Ellipse)selectedShape;
                duplicatedShape = new Ellipse(ell.getCenterX() + 10, ell.getCenterY() + 10,
                        ell.getRadiusX(), ell.getRadiusY());
            } else if (selectedShape instanceof Line) {
                Line line = (Line)selectedShape;
                duplicatedShape = new Line(line.getStartX() + 10, line.getStartY() + 10,
                        line.getEndX(), line.getEndY());
            }
            
            if (duplicatedShape != null) {
                duplicatedShape.setFill(selectedShape.getFill());
                shapeList.add(duplicatedShape);
            }
        }
        updateCanvas();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.shapeList = new ArrayList<>();
        this.nextShape = NextShapeToDraw.NONE;
        graphicContext = canvas.getGraphicsContext2D();
        
        initGUI();
        
        initListeners();
    }

    private void initGUI() {
        this.labelOption.setStyle("-fx-background-color:lightgrey; ");
        
        this.tgGroup = new ToggleGroup();
        
        // links the radio buttons to the toggle group
        this.rbSelectMove.setToggleGroup(this.tgGroup);
        this.rbEllipse.setToggleGroup(this.tgGroup);
        this.rbRectangle.setToggleGroup(this.tgGroup);
        this.rbLine.setToggleGroup(this.tgGroup);
        
        this.rbSelectMove.setSelected(true);
        
        // by default, the buttons are disable
        this.btnDelete.setDisable(true);
        this.btnClone.setDisable(true);
    }

    private void initListeners() {
        // enable buttons when the corresponding radio button is selected
        this.btnDelete.disableProperty().bind(this.rbSelectMove.selectedProperty().not());
        this.btnClone.disableProperty().bind(this.rbSelectMove.selectedProperty().not());
        
        this.tgGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldToggle, Toggle newToggle) {
                if (tgGroup.getSelectedToggle() != null) {
                    if (tgGroup.getSelectedToggle() == rbRectangle) {
                        nextShape = NextShapeToDraw.RECTANGLE;
                    }
                    else if (tgGroup.getSelectedToggle() == rbEllipse) {
                        nextShape = NextShapeToDraw.ELLIPSE;
                    }
                    else if (tgGroup.getSelectedToggle() == rbLine) {
                        nextShape = NextShapeToDraw.LINE;
                    }
                    else if (tgGroup.getSelectedToggle() == rbSelectMove) {
                        nextShape = NextShapeToDraw.NONE;
                    }
                }
            }
        });
        
        
        

        canvas.setOnMousePressed(e -> {
            lastX = e.getX();
            lastY = e.getY();
            
            if (nextShape == NextShapeToDraw.NONE) {
                selectedShape = getCanvasShapeAt(lastX, lastY);
                updateCanvas();
            }
        });

        canvas.setOnMouseDragged(e -> {
            updateCanvas();

            // when a correct shape is selected
            if (nextShape != NextShapeToDraw.NONE) {
                // init drawing properties..
                double x = e.getX();
                double y = e.getY();
                
                
                /* TODO : HANDLE left sliding !!! */
                
                double dx = (x > lastX) ? x - lastX : -(x - lastX);
                double dy = (y > lastY) ? y - lastY : -(y - lastY);
                
                
//                double dx = x - lastX;
//                double dy = y - lastY;
                
                
//                if( dx < 0 ) {
//                    lastX = x;
//                    dx = -dx;
//                }
//
//                if( dy < 0 ) {
//                    lastY = y;
//                    dy = -dy;
//                }
                
                
                
                // ..and draw the selected shape
                switch (nextShape) {
                    case RECTANGLE:
                        graphicContext.fillRect(lastX, lastY, dx, dy);
                        graphicContext.strokeRect(lastX, lastY, dx, dy);
                        break;
                    case ELLIPSE:
                        graphicContext.fillOval(lastX, lastY, dx, dy);
                        graphicContext.strokeOval(lastX, lastY, dx, dy);
                        break;
                    case LINE:
                        graphicContext.setLineWidth(5);
                        graphicContext.fill();
                        graphicContext.setStroke(this.colorPicker.getValue());
                        graphicContext.strokeLine(lastX, lastY, x, y);
                        
                        // reset default line width
                        graphicContext.setLineWidth(1);
                        graphicContext.setFill(this.colorPicker.getValue());
                        graphicContext.setStroke(Color.BLACK);
                        break;
                    default:
                        // should'nt be reached
                        return;
                }

//                lastX = 0;
//                lastY = 0;
            }
        });
        
        
        canvas.setOnMouseReleased(e -> {
            
            // when a correct shape is selected
            if (nextShape != NextShapeToDraw.NONE) {
                // init drawing properties..
                double x = e.getX();
                double y = e.getY();
                
                
                /* TODO : HANDLE left sliding !!! */
                
                double dx = (x > lastX) ? x - lastX : -(x - lastX);
                double dy = (y > lastY) ? y - lastY : -(y - lastY);
                
                
//                double dx = x - lastX;
//                double dy = y - lastY;
                
                
                
                Shape shapeToDraw = null;
                
                // ..and draw the selected shape
                switch (nextShape) {
                    case RECTANGLE:
                        shapeToDraw = new Rectangle(lastX, lastY, dx, dy);
                        shapeToDraw.setFill(this.colorPicker.getValue());
                        shapeToDraw.setStroke(Color.BLACK);
                        break;
                    case ELLIPSE:
                        shapeToDraw = new Ellipse(lastX, lastY, dx, dy);
                        shapeToDraw.setFill(this.colorPicker.getValue());
                        shapeToDraw.setStroke(Color.BLACK);
                        break;
                    case LINE:
                        shapeToDraw = new Line(lastX, lastY, x, y);
                        
                        ((Line)shapeToDraw).setStrokeWidth(5);
                        
                        ((Line)shapeToDraw).setFill(null);
                        
                        ((Line)shapeToDraw).setStroke(this.colorPicker.getValue());
                        break;
                    default:
                        // should'nt be reached
                        return;
                }
                
                
                if (shapeToDraw != null) {
                    if (x < lastX)
                        shapeToDraw.setTranslateX(dx);
                    if (y < lastY)
                        shapeToDraw.setTranslateY(dy);

                    // then add the created shape to the shape collection :
                    this.shapeList.add(shapeToDraw);

                    lastX = 0;
                    lastY = 0;
                }
            }
            
            updateCanvas();
            
        });
    }
    
    private void updateCanvas() {
            // Redraw the shapes.  The entire list of shapes
            // is redrawn whenever the user adds a new shape
            // or moves an existing shape.
        graphicContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        for (Shape shape : shapeList) {
            graphicContext.setFill(shape.getFill());
            graphicContext.setStroke(shape.getStroke());
            
            if (shape instanceof Rectangle) {
                graphicContext.fillRect(((Rectangle) shape).getX(), ((Rectangle) shape).getY(),
                        ((Rectangle) shape).getWidth(), ((Rectangle) shape).getHeight());
                graphicContext.strokeRect(((Rectangle) shape).getX(), ((Rectangle) shape).getY(),
                        ((Rectangle) shape).getWidth(), ((Rectangle) shape).getHeight());
            }
            else if (shape instanceof Ellipse) {
                graphicContext.fillOval(((Ellipse) shape).getCenterX(), ((Ellipse) shape).getCenterY(),
                        ((Ellipse) shape).getRadiusX(), ((Ellipse) shape).getRadiusY());
                graphicContext.strokeOval(((Ellipse) shape).getCenterX(), ((Ellipse) shape).getCenterY(),
                        ((Ellipse) shape).getRadiusX(), ((Ellipse) shape).getRadiusY());
            }
            else if (shape instanceof Line) {
                graphicContext.setLineWidth(5);
                graphicContext.fill();
                graphicContext.strokeLine(((Line) shape).getStartX(), ((Line) shape).getStartY(),
                        ((Line) shape).getEndX(), ((Line) shape).getEndY());
                graphicContext.setLineWidth(1);
            }
        }
        
        
        graphicContext.setFill(this.colorPicker.getValue());
        graphicContext.setStroke(Color.BLACK);
    }

    private Shape getCanvasShapeAt(double x, double y) {
        for (Shape shape : shapeList) {
            if (shape.contains(new Point2D(x, y))) {
                shape.setStrokeWidth(5);
                shape.setCursor(Cursor.CROSSHAIR);
                shape.setEffect(new DropShadow(0.5, 0, 0, Color.ALICEBLUE));
                return shape;
            }
        }
        return null;
    }
}
