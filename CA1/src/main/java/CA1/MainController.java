package CA1;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    public static MainController mainController = new MainController();
    public Menu fileMenu;
    public MenuItem openImage;
    public ImageView imageView;
    private final FileChooser fileChooser = new FileChooser();
    public TextField pillNameField;
    public Menu imageMenu;
    public Button viewImageDetailsButton;
    public Button addButton;
    public VBox baseVBox;
    public Slider sSlider;
    public Slider hSlider;
    public Slider bSlider;
    public AnchorPane anchorPane;
    public ImageView bwImageView;
    public Slider minimumSlider;
    public Label minimumSizeLabel;
    public Label totalLabel;
    public ListView<String> sameTypeList;
    private int width;
    private int height;
    public DisjointSet disjointSet;
    HashMap<Integer, Pill> pillMap = new HashMap<>();
    public int[] imageArray;
    public WritableImage bwImg;
    public WritableImage pillColoursImage;
    public WritableImage randomColoursImage;
    public Image original;
    ArrayList<Integer> rootsSelected = new ArrayList<>();
    WritableImage rectImg;
    private Tooltip pillTip;
    HashMap<String, Integer> pillCount = new HashMap<>();
    public Image fullBW;

    public void openImage() {
        resetAllPills();
        imageView.setImage(null);
        bwImageView.setImage(null);
        File file = fileChooser.showOpenDialog(new Stage());
        fileChooser.setInitialDirectory(new File("C:\\Users"));
        if (file != null) {
            Image selectedImage = new Image(file.toURI().toString(), (int) imageView.getFitWidth(), (int) imageView.getFitHeight(), false, true);
            original = selectedImage;
            imageView.setImage(selectedImage);
            bwImageView.setImage(selectedImage);
            width = (int) selectedImage.getWidth();
            height = (int) selectedImage.getHeight();
            imageArray = new int[height*width];
            disjointSet = new DisjointSet(height * width);
        }
    }

    public void pillSelected(MouseEvent event) {
        boolean selectedPrev = false;

        if (imageView.getImage() != null) {
            if (pillMap != null) {
                for (Pill p : pillMap.values()) {
                    for (int i : p.getIndexes()) {
                        if (i == (((int) event.getY()) * width + ((int) event.getX()))) {
                            selectedPrev = true;
                            break;
                        }
                    }
                }
            }

            if (!selectedPrev) {
                PixelReader pixelReader = original.getPixelReader();
                WritableImage blackAndWhite = new WritableImage(width, height);
                Color pillColour = pixelReader.getColor((int) event.getX(), (int) event.getY());

                if (pillColour != null) {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            Color currentCol = pixelReader.getColor(x, y);
                            if (pillColour.getHue() >= currentCol.getHue() - hSlider.getValue() &&
                                    pillColour.getHue() <= currentCol.getHue() + hSlider.getValue() &&
                                    pillColour.getSaturation() >= currentCol.getSaturation() - sSlider.getValue() &&
                                    pillColour.getSaturation() <= currentCol.getSaturation() + sSlider.getValue() &&
                                    pillColour.getBrightness() >= currentCol.getBrightness() - bSlider.getValue() &&
                                    pillColour.getBrightness() <= currentCol.getBrightness() + bSlider.getValue()) {
                                blackAndWhite.getPixelWriter().setColor(x, y, Color.WHITE);
                                imageArray[y * width + x] = 0;
                            } else {
                                blackAndWhite.getPixelWriter().setColor(x, y, Color.BLACK);
                                imageArray[y * width + x] = -1;
                            }
                        }
                    }

                    bwImg = blackAndWhite;
                    bwImageView.setImage(blackAndWhite);

                    for (int row = 0; row < height; row++) {
                        for (int col = 0; col < width; col++) {
                            int currentPixel = imageArray[row * width + col];
                            int currentIndex = row * width + col;

                            if (currentPixel == 0) {
                                if (col + 1 < width && imageArray[row * width + (col + 1)] == 0) {
                                    int rightIndex = row * width + col + 1;
                                    disjointSet.union(currentIndex, rightIndex);
                                }

                                if (row + 1 < height && imageArray[(row + 1) * width + col] == 0) {
                                    int belowIndex = (row + 1) * width + col;
                                    disjointSet.union(currentIndex, belowIndex);
                                }
                            }
                        }
                    }
                }
            } else {
                rectangles((int) event.getX(),(int) event.getY());
            }
        }
    }

    public void confirmPill() {
        if (imageView.getImage() != null) {
            WritableImage processedImage = new WritableImage(width, height);
            PixelWriter pixelWriter = processedImage.getPixelWriter();
            bwImageView.setImage(processedImage);

            String name;
            double minSize = minimumSlider.getValue();

            if (minSize > 0 && !pillNameField.getText().isEmpty()) {
                name = pillNameField.getText();
                for (Pill p : pillMap.values()) {
                    if (p.getName().equals(name)) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);

                        alert.setTitle("ERROR");
                        alert.setHeaderText("PLEASE ENTER A UNIQUE PILL NAME");
                        alert.setContentText("A pill with the same name was found in the system! Please choose a new name and try again!");
                        alert.showAndWait();

                        return;
                    }
                }

                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        int colour = imageArray[row * width + col];

                        if (colour == 0) {
                            int index = row * width + col;
                            int root = disjointSet.find(index);

                            if (!pillMap.containsKey(root)) {
                                pillMap.put(root, new Pill(name, root,imageView.getImage().getPixelReader().getColor(col, row)));
                            } else {
                                pillMap.get(root).getIndexes().add(index);
                            }

                            pixelWriter.setColor(col, row, Color.WHITE);
                        } else {
                            pixelWriter.setColor(col, row, Color.BLACK);
                        }
                    }
                }

                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        int index = row * width + col;
                        int root = disjointSet.find(index);

                        if (disjointSet.sizes[root] < minSize) {
                            if (pillMap.containsKey(root)) {
                                if (pillMap.get(root).getName().equals(name)) {
                                    for (int i : pillMap.get(root).getIndexes()) {
                                        imageArray[i] = -1;
                                    }
                                    pillMap.remove(root);
                                }
                            }
                            pixelWriter.setColor(col, row, Color.BLACK);
                        }
                    }
                }

                bwImg = processedImage;
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);

                alert.setTitle("ERROR");
                alert.setHeaderText("SET A NAME AND MINIMUM SIZE");
                alert.setContentText("please for the love of god");
                alert.showAndWait();
            }

            if (!pillMap.isEmpty()) {
                setTotals();
            }
        }
    }

    public void checkMinimum() {
        if (imageView.getImage() != null) {
            WritableImage processedImage = new WritableImage(width, height);
            PixelWriter pixelWriter = processedImage.getPixelWriter();
            bwImageView.setImage(processedImage);
            double minSize = minimumSlider.getValue();

            if (minSize > 0) {
                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        int colour = imageArray[row * width + col];

                        if (colour == 0) {
                            pixelWriter.setColor(col, row, Color.WHITE);
                        } else {
                            pixelWriter.setColor(col, row, Color.BLACK);
                        }
                    }
                }

                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        int index = row * width + col;
                        int root = disjointSet.find(index);

                        if (disjointSet.sizes[root] < minSize) {
                            pixelWriter.setColor(col, row, Color.BLACK);
                        }
                    }
                }
            }
        }
    }

    public void setTotals() {
        pillCount.clear();
        int pills = 0;

        for (Pill p : pillMap.values()) {
            String name = p.getName();
            pillCount.put(name,pillCount.getOrDefault(name,0) + 1);
            totalLabel.setText("TOTAL PILLS: " + ++pills);
        }

        if (sameTypeList != null) {
            sameTypeList.getItems().clear();
            for (Map.Entry<String, Integer> entry : pillCount.entrySet()) {
                sameTypeList.getItems().add(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    public void rectangles(int x, int y) {
        imageView.setImage(null);

        Canvas canvas = new Canvas(width, height);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.drawImage(original, 0, 0, width, height);

        Pill pill = pillMap.get(disjointSet.roots[(y * width) + x]);

        for (Pill p : pillMap.values()) {
            if (p.getName().equalsIgnoreCase(pill.getName()) || rootsSelected.contains(p.getRoot())) {
                ArrayList<Integer> xs = new ArrayList<>();
                ArrayList<Integer> ys = new ArrayList<>();

                for (int index : p.getIndexes()) {
                    xs.add(index % width);
                    ys.add((index - (index % width)) / width);
                }

                Collections.sort(xs);
                Collections.sort(ys);

                int firstX = xs.get(0);
                int firstY = ys.get(0);
                int lastX = xs.get(xs.size() - 1);
                int lastY = ys.get(ys.size() - 1);

                int rectWidth = lastX - firstX;
                int rectHeight = lastY - firstY;

                graphicsContext.setStroke(Color.WHITE);
                graphicsContext.setFill(Color.TRANSPARENT);
                graphicsContext.setLineWidth(1);
                graphicsContext.strokeRect(firstX, firstY, rectWidth, rectHeight);

                if(!rootsSelected.contains(p.getRoot())) rootsSelected.add(p.getRoot());
            }
        }

        rectImg = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null,rectImg);

        Canvas numCanvas = new Canvas(width,height);
        GraphicsContext numGraphicsContext = numCanvas.getGraphicsContext2D();
        numGraphicsContext.drawImage(rectImg, 0, 0, width, height);

        Collections.sort(rootsSelected);
        int pillCount = 1;

        for (int i : rootsSelected) {
            x = i % width;
            y = (i - (i % width)) / width;

            numGraphicsContext.setFill(Color.WHITE);
            numGraphicsContext.fillText(String.valueOf(pillCount++),x, y + 12);
        }

        WritableImage numImg = new WritableImage((int) numCanvas.getWidth(), (int) numCanvas.getHeight());
        numCanvas.snapshot(null,numImg);

        imageView.setImage(null);
        imageView.setImage(numImg);
    }

    public void toolTipHover(@NotNull MouseEvent e) {
        double mouseX = e.getX();
        double mouseY = e.getY();

        boolean onPill = false;
        Pill pillOn = null;

        for (Pill p : pillMap.values()) {
            for (int i : p.getIndexes()) {
                if (((mouseY * width) + mouseX) == i) {
                    onPill = true;
                    pillOn = p;

                    break;
                }
            }
        }

        if (onPill) {
            Main.mainScene.setCursor(Cursor.HAND);
            pillTip.setText("Name: " + pillOn.getName() + ",\nPill Root: " + pillOn.getRoot() +
                    ",\nPill Size: " + disjointSet.sizes[pillOn.getRoot()] + ",\nPill Colour: " + pillOn.getColour());
            pillTip.show(imageView, e.getScreenX() + 10, e.getScreenY() + 10);
        } else {
            pillTip.hide();
            Main.mainScene.setCursor(Cursor.DEFAULT);
        }
    }

    public void viewImageDetails() {
        if(imageView.getImage() != null && bwImg != null && !pillMap.isEmpty()) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("imageViewer.fxml"));
                Parent root = fxmlLoader.load();

                ImageView bAndWImageView = (ImageView) root.lookup("#bAndWImageView");
                ImageView pillColourImage = (ImageView) root.lookup("#pillColourImage");
                ImageView randomColourImage = (ImageView) root.lookup("#randomColourImage");
                ImageView originalImage = (ImageView) root.lookup("#originalImage");

                colourPills();
                randomColourPills();
                fullBlackAndWhite();

                bAndWImageView.setImage(fullBW);
                pillColourImage.setImage(pillColoursImage);
                randomColourImage.setImage(randomColoursImage);
                originalImage.setImage(original);

                Stage popUp = new Stage();
                popUp.setTitle("Image Details");
                popUp.setResizable(false);
                Scene newScene = new Scene(root, 1000, 600);
                popUp.setScene(newScene);
                popUp.show();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);

                alert.setTitle("ERROR");
                alert.setHeaderText("ERROR");
                alert.setContentText("ERROR");
                alert.showAndWait();
            }
        }
    }

    public void colourPills() {
        if (bwImg != null) {
            WritableImage bw = bwImg;
            PixelReader pixelReader = bw.getPixelReader();
            WritableImage colourImage = new WritableImage((int) bw.getWidth(), (int) bw.getHeight());
            PixelWriter pixelWriter = colourImage.getPixelWriter();

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    if (pixelReader.getColor(col, row).equals(Color.BLACK)) {
                        pixelWriter.setColor(col, row, Color.BLACK);
                    }
                }
            }

            for (Pill pill : pillMap.values()) {
                for (int index : pill.getIndexes()) {
                    int x = index % width;
                    int y = index / width;
                    pixelWriter.setColor(x, y, pill.getColour());
                }
            }

            pillColoursImage = colourImage;
            bwImageView.setImage(colourImage);
        }
    }

    public void randomColourPills() {
        if (bwImg != null) {
            WritableImage bw = bwImg;
            PixelReader pixelReader = bw.getPixelReader();
            WritableImage colourImage = new WritableImage((int) bw.getWidth(), (int) bw.getHeight());
            PixelWriter pixelWriter = colourImage.getPixelWriter();

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    if (pixelReader.getColor(col, row).equals(Color.BLACK)) {
                        pixelWriter.setColor(col, row, Color.BLACK);
                    }
                }
            }

            for (Pill pill : pillMap.values()) {
                Random rand = new Random();
                Color randomColour = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));

                for (int index : pill.getIndexes()) {
                    int x = index % width;
                    int y = index / width;
                    pixelWriter.setColor(x, y, randomColour);
                }
            }

            randomColoursImage = colourImage;
            bwImageView.setImage(colourImage);
        }
    }

    public void fullBlackAndWhite() {
        if (bwImg != null) {
            WritableImage bw = bwImg;
            PixelReader pixelReader = bw.getPixelReader();
            WritableImage fullBW = new WritableImage((int) bw.getWidth(), (int) bw.getHeight());
            PixelWriter pixelWriter = fullBW.getPixelWriter();

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    if (pixelReader.getColor(col, row).equals(Color.BLACK)) {
                        pixelWriter.setColor(col, row, Color.BLACK);
                    }
                }
            }

            for (Pill pill : pillMap.values()) {
                for (int index : pill.getIndexes()) {
                    int x = index % width;
                    int y = index / width;
                    pixelWriter.setColor(x, y, Color.WHITE);
                }
            }

            this.fullBW = fullBW;
            bwImageView.setImage(fullBW);
        }
    }

    public void minSizeLabel() {
        minimumSizeLabel.setText("(" + Math.round(minimumSlider.getValue()) + ")");
    }

    public void exit() {
        Platform.exit();
    }

    public void resetRectangles() {
        imageView.setImage(original);
        bwImageView.setImage(original);
        rootsSelected.clear();
    }

    public void resetAllPills() {
        imageView.setImage(original);
        bwImageView.setImage(original);
        sameTypeList.getItems().clear();
        rootsSelected.clear();
        totalLabel.setText("TOTAL PILLS: 0");
        pillMap.clear();
    }

    public void switchTwoTone() {Main.mainStage.setScene(Main.twoToneScene);}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainController = this;
        pillTip = new Tooltip("TEST");
        imageView.setOnMouseMoved(this::toolTipHover);
    }
}

