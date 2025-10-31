package es.upm.miw.bantumi.modelos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import es.upm.miw.bantumi.enums.ResultadoPartida;
@Entity(tableName = Puntuacion.TABLA)
public class Puntuacion {
    static public final String TABLA = "puntuaciones";

    @PrimaryKey(autoGenerate = true)
    private int uid;
    private long fecha;
    private String nombreJugador;
    private ResultadoPartida resultadoPartida;
    private int puntuacionJugador1;
    private int puntuacionJugador2;

    public Puntuacion(String nombreJugador, ResultadoPartida resultadoPartida, long fecha, int puntuacionJugador1, int puntuacionJugador2) {
        this.nombreJugador = nombreJugador;
        this.resultadoPartida = resultadoPartida;
        this.fecha = fecha;
        this.puntuacionJugador1 = puntuacionJugador1;
        this.puntuacionJugador2 = puntuacionJugador2;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid){
        this.uid= uid;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public ResultadoPartida getResultadoPartida() {
        return resultadoPartida;
    }

    public void setResultadoPartida(ResultadoPartida resultadoPartida) {
        this.resultadoPartida = resultadoPartida;
    }

    public int getPuntuacionJugador1() {
        return puntuacionJugador1;
    }

    public void setPuntuacionJugador1(int puntuacionJugador1) {
        this.puntuacionJugador1 = puntuacionJugador1;
    }

    public int getPuntuacionJugador2() {
        return puntuacionJugador2;
    }

    public void setPuntuacionJugador2(int puntuacionJugador2) {
        this.puntuacionJugador2 = puntuacionJugador2;
    }


}
