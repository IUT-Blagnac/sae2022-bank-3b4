package application.view;

import java.text.DecimalFormat;
import java.util.ArrayList;

import application.DailyBankState;
import application.control.EmpruntControllerPane;
import application.control.EmpruntManagement;
import application.tools.AlertUtilities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.CompteCourant;
import model.orm.Access_BD_Emprunt;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

public class EmpruntController {

    // Etat courant de l'application
    private DailyBankState dailyBankState;

    // Contrôleur de Dialogue associé à OperationsManagementController
    private EmpruntManagement omDialogController;

    private ObservableList<String> oListCompteCourantList;

    // Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
    private Stage primaryStage;

    // Manipulation de la fenêtre
    public void initContext(Stage _containingStage, DailyBankState _dbstate) {
        this.primaryStage = _containingStage;
        this.dailyBankState = _dbstate;
        // this.omDialogController = _om;
        this.configure();
    }

    private void configure() {
        this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
    }

    public void displayDialog() {
        this.primaryStage.showAndWait();
    }

    // Gestion du stage
    private Object closeWindow(WindowEvent e) {
        this.doCancel();
        e.consume();
        return null;
    }

    @FXML
    private void doCancel() {
        this.primaryStage.close();
    }

    @FXML
    private ListView lView;
    @FXML
    private Button emprunt;
    @FXML
    private ListView assEmprunt;

    private EmpruntControllerPane controller;

    private EmpruntManagement empruntCotroller;

    @FXML
    private void doSimuler() {
        controller.simuler();
    }
}
