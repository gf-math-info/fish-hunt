package fish.hunt.modele;

import fish.hunt.controleur.multijoueur.ControleurPartieMulti;

public class PartieMulti extends Partie {

    private ControleurPartieMulti controleurPartieMulti;

    public PartieMulti(ControleurPartieMulti controleurPartieMulti) {
        super(controleurPartieMulti);
        this.controleurPartieMulti = controleurPartieMulti;
    }

    @Override
    public void incrementerNbPoissonsTouches(boolean unProjectileUnMort) {
        if(unProjectileUnMort) {

            switch (++nbUnProjectileUnMort) {

                case 1:
                    score += 3;
                    controleurPartieMulti.miseAJourScore();
                    break;

                case 2:
                    controleurPartieMulti.attaquePoissonNormal();
                    incrementerScore();
                    break;

                case 3:
                    nbViesRestantes++;
                    incrementerScore();
                    break;

                case 4:
                    controleurPartieMulti.attaquePoissonSpecial();
                    incrementerScore();
                    break;

                default:
                    nbUnProjectileUnMort = 1;
                    score += 3;
                    controleurPartieMulti.miseAJourScore();
            }

        } else
            incrementerScore();

        if(++nbPoissonsTouches % NB_POISSONS_NIVEAU == 0)
            incrementerNiveau();

    }

    @Override
    public void incrementerScore() {
        super.incrementerScore();
        controleurPartieMulti.miseAJourScore();
    }
}
