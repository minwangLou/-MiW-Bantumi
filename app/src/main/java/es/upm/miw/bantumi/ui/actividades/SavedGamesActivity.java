package es.upm.miw.bantumi.ui.actividades;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.modelos.SavedGame;
import static es.upm.miw.bantumi.utils.FileUtils.guardarPartido;

public class SavedGamesActivity extends AppCompatActivity {

    private ListView lvSavedGames;
    private ArrayAdapter<String> adapter;
    private List<SavedGame> partidas;
    private List<String> nombres;
    private SavedGame partidoActual;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_games);

        Intent intent = getIntent();
        partidoActual = (SavedGame) intent.getSerializableExtra("savedGame");

        lvSavedGames = findViewById(R.id.lvSavedGames);
        Button btnDeleteAll = findViewById(R.id.btnDeleteAll);

        partidas = cargarPartidasGuardadas();
        nombres = new ArrayList<>();
        for (SavedGame p : partidas) {
            nombres.add(p.getName());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
        lvSavedGames.setAdapter(adapter);


        // Mostrar dialogo al pulsar un partido guardado
        lvSavedGames.setOnItemClickListener((parent, view, position, id) -> {
            SavedGame selected = partidas.get(position);
            mostrarDialogo(selected, position);
        });

        //Añadir listener al botón de eliminar todos los guardados
        btnDeleteAll.setOnClickListener(v -> eliminarTodo());


    }


    private List<SavedGame> cargarPartidasGuardadas() {
        List<SavedGame> partidas = new ArrayList<>();

        File directorio = new File(getFilesDir(), "saved_games");
        File[] archivos = directorio.listFiles();

        if (archivos != null) {
            Gson gson = new Gson();
            for (File archivo : archivos) {
                if (archivo.getName().endsWith(".json")) {
                    try (FileInputStream fis = new FileInputStream(archivo)) {
                        String json = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            json = new String(fis.readAllBytes());
                        }
                        SavedGame partida = gson.fromJson(json, SavedGame.class);
                        partidas.add(partida);
                        Log.i("DEBUG", "Archivo encontrado: " + partida.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return partidas;
    }

    private void mostrarDialogo(SavedGame partida, int index) {
        File directorio = new File(getFilesDir(), "saved_games");

        String[] opciones = {"Cargar", "Renombrar", "Eliminar", "Cancelar"};

        new AlertDialog.Builder(this)
                .setTitle(partida.getName() + "\n¿Qué operación quería realizar?")
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: // Cargar
                            cargarPartido(partida);
                            break;

                        case 1: // Renombrar
                            renombrarPartida(partida, index);
                            break;

                        case 2: // Eliminar
                            File file = new File(directorio, "partida_" + partida.getId() + ".json");
                            eliminarUnPartido(file, index);
                            break;

                        case 3: // Cancelar
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void cargarPartido (SavedGame partidoSeleccionado){
        if (partidoActual.getGameInfo().equals(partidoSeleccionado.getGameInfo())){
            finish();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Confirmación")
                    .setMessage("¿Desea cargar el partido seleccionado?")
                    .setPositiveButton("Sí", (dialog, which) -> {

                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("savedGame",partidoSeleccionado);
                        setResult(RESULT_OK, intent);
                        finish();
                })
                    .setNegativeButton("No", null)
                    .show();

        }
    }

    private void renombrarPartida(SavedGame partida, int index) {
        final EditText input = new EditText(this);
        input.setText(partida.getName());

        new AlertDialog.Builder(this)
                .setTitle("Renombrar partida")
                .setView(input)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    String nuevoNombre = input.getText().toString().trim();
                    if (!nuevoNombre.isEmpty()) {
                        partida.setName(nuevoNombre);
                        guardarPartido(this,partida);          // 写回 JSON
                        nombres.set(index, nuevoNombre);   // 更新显示列表
                        adapter.notifyDataSetChanged();    // 刷新 ListView
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void eliminarUnPartido(File file, int index){
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Eliminar el partido guardado?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (file.exists()) file.delete();

                    partidas.remove(index);
                    nombres.remove(index);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Partida eliminada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();

    }

    private void eliminarTodo() {
        File directorio = new File(getFilesDir(), "saved_games");

        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Eliminar todas las partidas guardadas?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    File[] files = directorio.listFiles();
                    if (files != null) {
                        for (File f : files) {
                            if (f.getName().endsWith(".json")) f.delete();
                        }
                    }
                    partidas.clear();
                    nombres.clear();
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("No", null)
                .show();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.saved_game_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()) {
            case R.id.opcVolver:

                finish();

                return true;

        }
        return true;
    }

}