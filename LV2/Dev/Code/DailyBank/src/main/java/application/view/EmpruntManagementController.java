package application.view;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import application.DailyBankState;
import application.control.EmpruntManagement;
import application.control.OperationsManagement;
import application.tools.AlertUtilities;
import application.tools.NoSelectionModel;
import application.tools.PairsOfValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_Emprunt;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.RowNotFoundOrTooManyRowsException;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

public class EmpruntManagementController {

	// Etat courant de l'application
	private DailyBankState dailyBankState;

	// Contrôleur de Dialogue associé à OperationsManagementController
	private EmpruntManagement omDialogController;

	private ObservableList<String> oListCompteCourantList;

	// Fenêtre physique ou est la scène contenant le fichier xml contrôlé par this
	private Stage primaryStage;

	private double montant;
	private int duree;
	private double taux;

	// Manipulation de la fenêtre
	public void initContext(Stage _containingStage, EmpruntManagement _om, DailyBankState _dbstate) {
		this.primaryStage = _containingStage;
		this.dailyBankState = _dbstate;
		this.omDialogController = _om;
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
	private TextField txtMontant;
	@FXML
	private TextField txtDuree;
	@FXML
	private TextField txtTaux;

	@FXML
	private Button valider;

	@FXML
	private ListView lvComptes;

	private void genererSimu() {
		ArrayList<String> listeCpt = new ArrayList<>();

		double capitalRestant = this.montant;
		double mensualite = this.montant * (this.taux / (1 - Math.pow(1 + this.taux, -this.duree)));

		DecimalFormat decimalFormat = new DecimalFormat("#.##");

		for (int mois = 1; mois <= this.duree; mois++) {
			double interets = capitalRestant * this.taux;
			double montantPrincipal = mensualite - interets;
			double capitalFinPeriode = capitalRestant - montantPrincipal;

			String ligne = "Mois : " + mois +
					" | Capital restant : " + decimalFormat.format(capitalRestant) +
					" | Intérêts : " + decimalFormat.format(interets) +
					" | Montant principal : " + decimalFormat.format(montantPrincipal) +
					" | Mensualité : " + decimalFormat.format(mensualite) +
					" | Capital fin période : " + decimalFormat.format(capitalFinPeriode);

			listeCpt.add(ligne);

			capitalRestant = capitalFinPeriode;
		}

		this.oListCompteCourantList = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.oListCompteCourantList);
		this.oListCompteCourantList.clear();
		this.oListCompteCourantList.addAll(listeCpt);
	}

	@FXML
	private void doSimuler() throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		if (isSaisieValide()) {
			this.genererSimu();

			this.montant = Double.parseDouble(this.txtMontant.getText());
			this.duree = Integer.parseInt(this.txtDuree.getText());
			this.taux = Double.parseDouble(this.txtTaux.getText()) / 100;

			int num = omDialogController.getNumCli();
			CompteCourant getCompteCourant = omDialogController.getCompteCourant();
			Access_BD_Emprunt acc = new Access_BD_Emprunt();

			acc.insertEmprunt(this.taux, this.montant, this.duree, getCompteCourant);

		} else {
			AlertUtilities.showAlert(this.primaryStage, "Erreur", "Merci de saisir des valeurs valides", null,
					AlertType.INFORMATION);
		}
	}

	private boolean isSaisieValide() {
		try {
			this.montant = Double.parseDouble(this.txtMontant.getText());
			this.duree = Integer.parseInt(this.txtDuree.getText());
			this.taux = Double.parseDouble(this.txtTaux.getText()) / 100;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (this.montant >= 0 && this.duree >= 0 && this.taux >= 0) {

			return true;
		} else {

			return false;
		}
	}
}
