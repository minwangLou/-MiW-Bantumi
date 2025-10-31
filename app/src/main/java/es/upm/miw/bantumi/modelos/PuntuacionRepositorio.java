package es.upm.miw.bantumi.modelos;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import es.upm.miw.bantumi.datos.Converters;

@Database(entities = {Puntuacion.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class PuntuacionRepositorio extends RoomDatabase {

    public static final String BASE_DATOS = Puntuacion.TABLA + ".db";
    public abstract PuntuacionDAO puntuacionDAO();
}
