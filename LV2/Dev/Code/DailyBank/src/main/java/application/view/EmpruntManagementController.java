package application.view;

import java.text.DecimalFormat;
import java.util.ArrayList;

import application.DailyBankState;
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
	private ListView lvComptes;

	/**
	 * Génère la simulation de remboursement de l'emprunt.
	 * La simulation calcule les informations pour chaque période de remboursement
	 * et les affiche dans une liste.
	 * Les informations incluent le mois, le capital restant, les intérêts, le
	 * montant principal, la mensualité et le capital en fin de période.
	 *
	 * @author Bradley DJEDJE
	 */
	private void genererSimulation() {
		ArrayList<String> listeCpt = new ArrayList<>();

		double capitalRestant = this.montant;
		double mensualite = this.montant * (this.taux / (1 - Math.pow(1 + this.taux, -this.duree)));

		DecimalFormat decimalFormat = new DecimalFormat("#.##");

		for (int mois = 1; mois <= this.duree; mois++) {
			double interets = capitalRestant * this.taux;
			double montantPrincipal = mensualite - interets;
			double capitalFinPeriode = capitalRestant - montantPrincipal;

			String ligne = "Mois: " + mois +
					" | Capital restant: " + decimalFormat.format(capitalRestant) +
					" | Intérêts: " + decimalFormat.format(interets) +
					" | Montant principal: " + decimalFormat.format(montantPrincipal) +
					" | Mensualité: " + decimalFormat.format(mensualite) +
					" | Capital fin période: " + decimalFormat.format(capitalFinPeriode);

			listeCpt.add(ligne);

			capitalRestant = capitalFinPeriode;
		}

		this.oListCompteCourantList = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.oListCompteCourantList);
		this.oListCompteCourantList.clear();
		this.oListCompteCourantList.addAll(listeCpt);
	}

	/**
	 * Effectue le calcul de l'assurance en utilisant les valeurs saisies.
	 * Calcule le montant de l'assurance en fonction des paramètres de saisie.
	 * Si la saisie est valide, l'opération de calcul de l'assurance est effectuée.
	 * Sinon, une alerte d'erreur est affichée.
	 * 
	 * @author Bradley DJEDJE
	 */
	public void calculerAssurance() {
		ArrayList<String> listeCpt = new ArrayList<>();

		double montantAssurance = (this.montant * this.taux);

		// double montantAssuranceMensuelle = montantAssurance / (this.duree * 12);

		listeCpt.add("Le montant de l'assurance d'emprunt par an est de :" + montantAssurance);

		this.oListCompteCourantList = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.oListCompteCourantList);
		this.oListCompteCourantList.clear();
		this.oListCompteCourantList.addAll(listeCpt);
	}

	/**
	 * Effectue la simulation en utilisant les valeurs saisies.
	 * Génère une simulation en fonction des paramètres de saisie.
	 * Si la saisie est valide, l'opération de simulation est effectuée.
	 * Sinon, une alerte d'erreur est affichée.
	 *
	 * @throws RowNotFoundOrTooManyRowsException si une ligne n'est pas trouvée ou
	 *                                           si trop de lignes sont trouvées
	 *                                           lors de l'accès aux données.
	 * @throws DataAccessException               si une exception d'accès aux
	 *                                           données se produit.
	 * @throws DatabaseConnexionException        si une exception de connexion à la
	 *                                           base de données se produit.
	 * @author Bradley DJEDJE
	 */
	@FXML
	private void doSimuler() throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		if (isSaisieValide()) {
			this.genererSimulation();

			// CompteCourant getCompteCourant = omDialogController.getCompteCourant();
			// Access_BD_Emprunt acc = new Access_BD_Emprunt();
			// acc.insertEmprunt(this.taux, this.montant, this.duree, getCompteCourant);

		} else {
			AlertUtilities.showAlert(this.primaryStage, "Erreur", "Merci de saisir des valeurs valides", null,
					AlertType.INFORMATION);
		}
	}

	/**
	 * Effectue le calcul de l'assurance en utilisant les valeurs saisies.
	 * Calcule le montant de l'assurance en fonction des paramètres de saisie.
	 * Si la saisie est valide, l'opération de calcul de l'assurance est effectuée.
	 * Sinon, une alerte d'erreur est affichée.
	 * 
	 * @author Bradley DJEDJE
	 */
	@FXML
	private void doAssurance() {
		if (isSaisieValide()) {
			this.calculerAssurance();
		} else {
			AlertUtilities.showAlert(this.primaryStage, "Erreur", "Merci de saisir des valeurs valides", null,
					AlertType.INFORMATION);
		}
	}

	// @FXML
	// private void valider() throws RowNotFoundOrTooManyRowsException,
	// DataAccessException, DatabaseConnexionException{
	// CompteCourant getCompteCourant = omDialogController.getCompteCourant();
	// Access_BD_Emprunt acc = new Access_BD_Emprunt();
	// acc.insertEmprunt(this.taux, this.montant, this.duree, getCompteCourant);
	// }

	private boolean isSaisieValide() {
		try {
			this.montant = Double.parseDouble(this.txtMontant.getText());
			this.duree = Integer.parseInt(this.txtDuree.getText());
			this.taux = Double.parseDouble(this.txtTaux.getText()) / 100;
		} catch (NumberFormatException e) {
			AlertUtilities.showAlert(primaryStage, "Erreur saisie", "Saisie invalide", "Veillez saisier des nombres.",
					AlertType.ERROR);
			return false;
		}
		if (this.montant >= 0 && this.duree >= 0 && this.taux >= 0) {
			return true;
		} else {
			return false;
		}
	}
}