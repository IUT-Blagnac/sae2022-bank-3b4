package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.CategorieOperation;
import application.tools.PairsOfValue;
import application.tools.StageManagement;
import application.view.EmpruntManagementController;
import application.view.OperationEditorPaneController;
import application.view.OperationsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;
import model.orm.Access_BD_CompteCourant;
import model.orm.Access_BD_Operation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

/**
 * Cette classe représente le contrôleur pour la gestion des opérations
 * bancaires.
 * Elle gère l'affichage des opérations sur un compte bancaire, l'enregistrement
 * d'une opération et la lecture des opérations et du solde du compte.
 */
public class EmpruntManagement {

	private Stage primaryStage;
	private DailyBankState dailyBankState;
	private EmpruntManagementController omcViewController;
	private Client clientDuCompte;
	private CompteCourant compteConcerne;

	private CompteCourant cc;

	/**
	 * Constructeur de la classe OperationsManagement.
	 * 
	 * @param _parentStage la fenêtre parente de la fenêtre de gestion des
	 *                     opérations
	 * @param _dbstate     l'état quotidien de la banque
	 * @param client       le client associé au compte bancaire
	 * @param compte       le compte bancaire concerné par les opérations
	 */
	public EmpruntManagement(Stage _parentStage, DailyBankState _dbstate, CompteCourant _cc) {
		this.cc = _cc;
		this.dailyBankState = _dbstate;
		try {
			System.out.println("dadadadad");
			FXMLLoader loader = new FXMLLoader(EmpruntManagementController.class.getResource("empruntsimuler.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des emprunt");
			this.primaryStage.setResizable(false);

			primaryStage.show();

			this.omcViewController = loader.getController();
			this.omcViewController.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Affiche la fenêtre de gestion des opérations.
	 */
	public void doempruntManagementDialog() {
		this.omcViewController.displayDialog();
	}

	public int getNumCli() {
		return this.cc.idNumCli;
	}

	public CompteCourant getCompteCourant() {
		return this.cc;
	}
}
