package es.upm.miw.bantumi.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;

import es.upm.miw.bantumi.modelos.SavedGame;

public class FileUtils {

    public static void guardarPartido(Context context, SavedGame partidoParaGuardar) {
        Gson gson = new Gson();
        String json = gson.toJson(partidoParaGuardar);

        String nombreArchivo = "partida_" + partidoParaGuardar.getId() + ".json";

        File directorio = new File(context.getFilesDir(), "saved_games");
        if (!directorio.exists()) directorio.mkdirs();

        File archivo = new File(directorio, nombreArchivo);

        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            fos.write(json.getBytes());
            Toast.makeText(context, "Partida guardada: " + nombreArchivo, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("FileUtils", "FILE I/O ERROR: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(context, "Error al guardar partida", Toast.LENGTH_SHORT).show();
        }
    }
}