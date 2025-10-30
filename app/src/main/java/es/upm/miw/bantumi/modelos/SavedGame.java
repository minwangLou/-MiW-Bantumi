package es.upm.miw.bantumi.modelos;

import java.io.Serializable;
import java.util.List;

import es.upm.miw.bantumi.dominio.logica.JuegoBantumi.*;

public class SavedGame implements Serializable {

    private long id; //momento en que se guardo la patida
    private String name; //nombre de la partida guardada
    private List<Integer> gameInfo; //numero de la semilla en cada casilla
    private Turno turno;
    public SavedGame(long id, String name, List<Integer> gameInfo, Turno turno) {
        this.id = id;
        this.name = name;
        this.gameInfo = gameInfo;
        this.turno = turno;
    }

    public long getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(List<Integer> gameInfo) {
        this.gameInfo = gameInfo;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }



}
