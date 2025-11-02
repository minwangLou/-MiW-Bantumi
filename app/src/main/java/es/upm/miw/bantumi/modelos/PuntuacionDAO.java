package es.upm.miw.bantumi.modelos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomDatabase;

import java.util.List;

@Dao
public interface PuntuacionDAO{

    @Query("SELECT * FROM " + Puntuacion.TABLA)
    List<Puntuacion> getAll();

    @Query("SELECT * FROM " + Puntuacion.TABLA +
            " ORDER BY CASE " +
            "WHEN puntuacionJugador1 >= puntuacionJugador2 THEN puntuacionJugador1 " +
            "ELSE puntuacionJugador2 END DESC " +
            "LIMIT 10")
    List<Puntuacion> getTop10score();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Puntuacion grupo);

    @Query("DELETE FROM " + Puntuacion.TABLA)
    void eliminarTodos();
}
