package model.data;

public class Emprunt {
    public int idNumEmprunt;
    public int taux;
    public double capitalMP;
    public String date; // "O" ou "N"
    public int idNumCli;

    public Emprunt(int idNumEmprunt, int taux, double capitalMP, String date, int idNumCli) {
        super();
        this.idNumEmprunt = idNumEmprunt;
        this.taux = taux;
        this.capitalMP = capitalMP;
        this.date = date;
        this.idNumCli = idNumCli;
    }
}
