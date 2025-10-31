package es.upm.miw.bantumi.datos;

import androidx.room.TypeConverter;
import es.upm.miw.bantumi.enums.ResultadoPartida;
public class Converters {
    @TypeConverter
    public static String fromResultado(ResultadoPartida resultado) {
        return resultado == null ? null : resultado.name();
    }

    @TypeConverter
    public static ResultadoPartida toResultado(String value) {
        return value == null ? null : ResultadoPartida.valueOf(value);
    }
}
