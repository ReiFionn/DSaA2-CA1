package CA1;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.*;

public class TwoToneController implements Initializable {
    public static TwoToneController twoToneController = new TwoToneController();
    public ImageView imageView;
    private final FileChooser fileChooser = new FileChooser();
    public ImageView bwImageView;
    public ColorPicker colourPickerOne;
    public ColorPicker colourPickerTwo;
    public ToggleGroup twoColours;
    public RadioButton colourOneRadio;
    public RadioButton colourTwoRadio;
    public TextField pillNameField;
    private int width;
    private int height;
    public DisjointSet disjointSet;
    public int[] imageArray;
    public Image original;
    public Label totalLabel;
    public ListView<String> sameTypeList;
    HashMap<Integer, Pill> pillMap = new HashMap<>();
    public Slider minimumSlider;
    public Label minimumSizeLabel;
    private Tooltip pillTip;
    public WritableImage bwImg;
    public Slider sSlider;
    public Slider hSlider;
    public Slider bSlider;

    public void openImage() {
        MainController.mainController.resetAllPills();
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

            for (int i : imageArray) {
                imageArray[i] = -1;
            }
        }
    }

    public void exit() {
        Platform.exit();
    }

    public void minSizeLabel() {
        minimumSizeLabel.setText("(" + Math.round(minimumSlider.getValue()) + ")");
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        twoToneController = this;
        pillTip = new Tooltip("TEST");
        imageView.setOnMouseMoved(this::toolTipHover);
        colourPickerOne.setValue(null);
        colourPickerTwo.setValue(null);
    }

    ///////////////////////////////////////////////////

    public void previousScene() {
        Main.mainStage.setScene(Main.mainScene);
    }

    public void pillSelected(MouseEvent event) {
        PixelReader pixelReader = original.getPixelReader();
        WritableImage blackAndWhite = new WritableImage(width, height);
        Color pillColour = pixelReader.getColor((int) event.getX(), (int) event.getY());

        if (pillColour != null) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    Color currentCol = pixelReader.getColor(x, y);

                    if (twoColours.getSelectedToggle().equals(colourOneRadio)) {
                        if (pillColour.getHue() >= currentCol.getHue() - hSlider.getValue() &&
                                pillColour.getHue() <= currentCol.getHue() + hSlider.getValue() &&
                                pillColour.getSaturation() >= currentCol.getSaturation() - sSlider.getValue() &&
                                pillColour.getSaturation() <= currentCol.getSaturation() + sSlider.getValue() &&
                                pillColour.getBrightness() >= currentCol.getBrightness() - bSlider.getValue() &&
                                pillColour.getBrightness() <= currentCol.getBrightness() + bSlider.getValue()) {
                            blackAndWhite.getPixelWriter().setColor(x, y, Color.WHITE);
                            imageArray[y * width + x] = 0; //0 = first colour

                            colourPickerOne.setValue(currentCol);
                        } else {
                            blackAndWhite.getPixelWriter().setColor(x, y, Color.BLACK);
                        }
                    } else {
                        if (colourPickerOne.getValue() != null) {
                            if (pillColour.getHue() >= currentCol.getHue() - hSlider.getValue() &&
                                    pillColour.getHue() <= currentCol.getHue() + hSlider.getValue() &&
                                    pillColour.getSaturation() >= currentCol.getSaturation() - sSlider.getValue() &&
                                    pillColour.getSaturation() <= currentCol.getSaturation() + sSlider.getValue() &&
                                    pillColour.getBrightness() >= currentCol.getBrightness() - bSlider.getValue() &&
                                    pillColour.getBrightness() <= currentCol.getBrightness() + bSlider.getValue()) {
                                blackAndWhite.getPixelWriter().setColor(x, y, Color.WHITE);
                                if (imageArray[y * width + x] != 0) {
                                    imageArray[y * width + x] = 1; //1 = second colour
                                }

                                colourPickerTwo.setValue(currentCol);
                            } else if (imageArray[y * width + x] == 0){
                                blackAndWhite.getPixelWriter().setColor(x, y, Color.WHITE);
                            } else {
                                blackAndWhite.getPixelWriter().setColor(x, y, Color.BLACK);
                            }
                        } else {
                            Alert alert = new Alert(Alert.AlertType.WARNING);

                            alert.setTitle("ERROR");
                            alert.setHeaderText("PLEASE CHOOSE COLOUR ONE");
                            alert.setContentText("please");
                            alert.showAndWait();

                            return;
                        }
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

                    if (currentPixel == 1) {
                        if (col + 1 < width && imageArray[row * width + (col + 1)] == 1) {
                            int rightIndex = row * width + col + 1;
                            disjointSet.union(currentIndex, rightIndex);
                        }

                        if (row + 1 < height && imageArray[(row + 1) * width + col] == 1) {
                            int belowIndex = (row + 1) * width + col;
                            disjointSet.union(currentIndex, belowIndex);
                        }
                    }

                    //check to see if any of these sets are next to each other, if they are, union them together, if they are by themselves, remove them.
                }
            }
        }
    }

    public void confirmPill() {
        //TODO Validate inputs + check for duplicates
        for (int i : imageArray) {
            System.out.print(i + ", ");
        }

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
                        alert.setContentText("please for the love of god");
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
                        } else if (colour == 1) {
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

                        if (disjointSet.sizes[root] < minSize) { //remove if less than minimum size
                            if (pillMap.containsKey(root)) {
                                if (pillMap.get(root).getName().equals(name)) {
                                    for (int i : pillMap.get(root).getIndexes()) {
                                        imageArray[i] = -1;
                                    }
                                    pillMap.remove(root);
                                }
                            }
                            pixelWriter.setColor(col, row, Color.BLACK);
                        } else {
                            int firstColour = -1;
                            int increment = 0;

                            if (pillMap.containsKey(root)) {
                                int[] pillColours = new int[disjointSet.sizes[root]];

                                for (int i : pillMap.get(root).getIndexes()) {
                                    if (firstColour == -1) {
                                        firstColour = imageArray[i]; //0 OR 1
                                        pillColours[++increment] = firstColour;
                                    } else {
                                        pillColours[++increment] = imageArray[i];
                                    }
                                }

                                boolean color1 = false;
                                boolean color2 = false;

                                for (int i : pillColours) {
                                    if (color1 && color2) {
                                        break;
                                    } else if (i == 0) {
                                        color1 = true;
                                    } else if (i == 1) {
                                        color2 = true;
                                    }
                                }

                                if ((!color1 && color2) || (color1 && !color2))  {
                                    if (pillMap.containsKey(root)) {
                                        for (int i : pillMap.get(root).getIndexes()) {
                                            imageArray[i] = -1;
                                        }
                                        pillMap.remove(root);
                                    }
                                }
                            }
                        }
                    }
                }

                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        int index = row * width + col;

                        if (pillMap != null) {
                            for (Pill p : pillMap.values()) {
                                for (int i : p.getIndexes()) {
                                    if (i == index) {
                                        pixelWriter.setColor(col, row, Color.WHITE);
                                    }
                                }
                            }
                        }
                    }
                }

                assert pillMap != null;
                System.out.println(pillMap.size());
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);

                alert.setTitle("ERROR");
                alert.setHeaderText("SET A NAME AND MINIMUM SIZE");
                alert.setContentText("please for the love of god");
                alert.showAndWait();
            }
        }
    }
}
