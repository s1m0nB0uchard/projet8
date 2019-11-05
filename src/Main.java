import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) {
        GridPane gp = new GridPane();


        //aller chercher les images
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            images.add(new Image("mario" + Integer.toString(i) + ".jpg"));
        }
        ImageView iv0 = new ImageView();
        ImageView iv1 = new ImageView();
        ImageView iv2 = new ImageView();
        ImageView iv3 = new ImageView();
        ImageView iv4 = new ImageView();
        ImageView iv5 = new ImageView();
        ImageView iv6 = new ImageView();
        ImageView iv7 = new ImageView();
        ImageView iv8 = new ImageView();

        //affichage puzzle

        List<ImageView> imageViews = Arrays.asList(iv0, iv1, iv2, iv3, iv4, iv5, iv6, iv7, iv8);
        HashMap hm = new HashMap();

        originale(images, hm);
        afficherImage(imageViews, images, gp);

        //commandes
        ImageView iv = new ImageView();
        for (ImageView image : imageViews) {
            image.setOnDragDetected((event) -> {
                Dragboard dragboard = image.startDragAndDrop(TransferMode.COPY);
                ClipboardContent contenu = new ClipboardContent();
                contenu.putImage(image.getImage());
                dragboard.setContent(contenu);

            });

            image.setOnDragOver(event -> event.acceptTransferModes(TransferMode.COPY));

            image.setOnDragDropped(event -> {
                ImageView source = (ImageView) event.getGestureSource();
                iv.setImage(image.getImage());
                image.setImage(source.getImage());

                event.setDropCompleted(true);
            });

            image.setOnDragDone((event -> {
                image.setImage(iv.getImage());
                if (corriger(imageViews, hm)) {
                    dialog(images, hm, imageViews, gp);
                }
            }));
        }

        //clavier
        Group group = new Group(gp);
        Scene scene = new Scene(group);
        scene.setOnKeyPressed((event) -> {
            if (event.getCode() == KeyCode.M && event.isControlDown()) {
                afficherImage(imageViews, images, gp);
            }
        });

        //affichage
        window.setTitle("Puzzle 2 à 5 ans");
        window.setScene(scene);
        window.show();
    }

    private static void shuffle(List<ImageView> _liste, List<Image> _images) {
        Collections.shuffle(_images);
        int i = 0;
        for (ImageView iv : _liste) {
            iv.setImage(_liste.get(i).getImage());
            i++;
        }
    }

    private static Boolean corriger(List<ImageView> imageViews, HashMap hm) {
        int i = 0;
        int complet = 0;
        Boolean done = false;
        for (ImageView iv : imageViews) {
            ImageView d = new ImageView((Image) hm.get(i));
            if (d.getImage().getUrl().equals(iv.getImage().getUrl())) {
                complet++;
            }
            i++;
        }
        if (complet == 9) {
            System.out.println("image complétée");
            done = true;


        }
        System.out.println();
        return done;
    }

    private static void effacerGrille(List<ImageView> imageViews, GridPane gp) {
        for (ImageView image : imageViews) {
            gp.getChildren().remove(image);
        }

    }

    private static void originale(List<Image> images, HashMap hm) {
        int z = 0;
        for (Image image : images) {
            hm.put(z, image);
            z++;
        }
    }

    private static void afficherImage(List<ImageView> imageViews, List<Image> images, GridPane gp) {

        int i = 0;
        int colonne = 0;
        int rangee = 0;
        effacerGrille(imageViews, gp);
        shuffle(imageViews, images);
        for (ImageView iv : imageViews) {
            iv.setImage(images.get(i));
            iv.setFitWidth(120);
            iv.setPreserveRatio(true);
            if (colonne % 3 == 0) {
                rangee++;
                colonne = 0;
            }
            gp.add(iv, colonne, rangee);

            colonne++;
            i++;
        }
    }

    private static void dialog(List<Image> images, HashMap hm, List<ImageView> imageViews, GridPane gp) {
        Label label = new Label("Félicitation, vous avez complété le puzzle");
        HBox hb = new HBox(label);
        Dialog dialog = new Dialog();
        dialog.getDialogPane().setContent(hb);
        dialog.getDialogPane().getButtonTypes().addAll(new ButtonType("Rejouer", ButtonBar.ButtonData.YES), new ButtonType("Admirer le casse-tête", ButtonBar.ButtonData.OK_DONE));
        ButtonType bouton = (ButtonType) dialog.showAndWait().get();
        if (bouton.getButtonData() == ButtonBar.ButtonData.YES) {
            originale(images, hm);
            afficherImage(imageViews, images, gp);
        }
    }
}
