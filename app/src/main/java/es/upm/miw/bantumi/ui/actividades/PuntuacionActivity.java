package es.upm.miw.bantumi.ui.actividades;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.modelos.Puntuacion;
import es.upm.miw.bantumi.modelos.PuntuacionRepositorio;
import es.upm.miw.bantumi.ui.adaptadores.PuntuacionAdaptador;

public class PuntuacionActivity extends AppCompatActivity {

    private ListView lvPuntuaciones;
    private Button btnTop10;
    private PuntuacionRepositorio puntuacionRepositorio;
    PuntuacionAdaptador adapter;
    List<Puntuacion> puntuaciones;

    private boolean mostrandoTop10 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_puntuacion);

        lvPuntuaciones = findViewById(R.id.lv_puntuacion);
        btnTop10 = findViewById(R.id.btnTop10);

        puntuacionRepositorio = Room.databaseBuilder(
                        getApplicationContext(),
                        PuntuacionRepositorio.class,
                        PuntuacionRepositorio.BASE_DATOS
                )
                .allowMainThreadQueries()
                .build();




        lvPuntuaciones = findViewById(R.id.lv_puntuacion);
        cargarTodo();

        btnTop10.setOnClickListener(v -> cargarTop10());

    }

    private void cargarTodo(){
        puntuaciones = puntuacionRepositorio.puntuacionDAO().getAll();
        adapter = new PuntuacionAdaptador(this, puntuaciones);
        lvPuntuaciones.setAdapter(adapter);
    }
    private void cargarTop10() {
        if(mostrandoTop10 == false){
            puntuaciones = puntuacionRepositorio.puntuacionDAO().getTop10score();
            btnTop10.setText("Restaurar");
            btnTop10.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DEB887")));
            mostrandoTop10 = true;
        }else{
            puntuaciones = puntuacionRepositorio.puntuacionDAO().getAll();
            btnTop10.setText(R.string.mostrar_10_mejores_puntuaciones);
            btnTop10.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff33b5e5")));
            mostrandoTop10 = false;
        }
        adapter.clear();
        adapter.addAll(puntuaciones);
        adapter.notifyDataSetChanged();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.puntuacion_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        switch (item.getItemId()) {
            case R.id.opcVolver_puntuacion:
                finish();
                return true;
            case R.id.opcEliminar_puntuacion:
                eliminarTodoScore();
                return true;

        }
        return true;
    }

    private void eliminarTodoScore(){
        new AlertDialog.Builder(this)
                .setTitle("Confirmar")
                .setMessage("¿Quieres eliminar todos los resultados?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    puntuacionRepositorio.puntuacionDAO().eliminarTodos();
                    cargarTodo();
                    Toast.makeText(this, "Todos los resultados eliminados", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}