package es.upm.miw.bantumi.ui.actividades;

import static es.upm.miw.bantumi.utils.FileUtils.guardarPartida;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.upm.miw.bantumi.modelos.Puntuacion;
import es.upm.miw.bantumi.modelos.PuntuacionRepositorio;
import es.upm.miw.bantumi.modelos.SavedGame;
import es.upm.miw.bantumi.ui.fragmentos.FinalAlertDialog;
import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.dominio.logica.JuegoBantumi;
import es.upm.miw.bantumi.ui.fragmentos.RestartDialogFragment;
import es.upm.miw.bantumi.ui.viewmodel.BantumiViewModel;
import es.upm.miw.bantumi.dominio.logica.JuegoBantumi.*;
import es.upm.miw.bantumi.enums.ResultadoPartida;

import androidx.preference.PreferenceManager;
import androidx.room.Room;

public class MainActivity extends AppCompatActivity {

    protected final String LOG_TAG = "MiW";
    public JuegoBantumi juegoBantumi;
    private BantumiViewModel bantumiVM;
    int numInicialSemillas;
    private ActivityResultLauncher<Intent> launcherRecuperarPartida;

    private PuntuacionRepositorio puntuacionRepositorio;

    private SharedPreferences prefs;

    public Turno turno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registrarLauncherRecuperarPartida();

        instanciarRepositorio();

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        turno = obtenerTurnoPreferencia(prefs);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Instancia el ViewModel y el juego, y asigna observadores a los huecos
        numInicialSemillas = getResources().getInteger(R.integer.intNumInicialSemillas);
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, turno, numInicialSemillas);
        crearObservadores();
    }

    private Turno obtenerTurnoPreferencia (SharedPreferences prefs){
        Turno turno;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String prefTurno = prefs.getString("jugador_inical", "Jugador 1");
        if (prefTurno.equals("Jugador 1")) {
            turno = Turno.turnoJ1;
        }
        else {
            turno = Turno.turnoJ2;
        }
        return turno;
    }

    /**
     * Crea y subscribe los observadores asignados a las posiciones del tablero.
     * Si se modifica el contenido del tablero -> se actualiza la vista.
     */
    private void crearObservadores() {
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            int finalI = i;
            bantumiVM.getNumSemillas(i).observe(    // Huecos y almacenes
                    this,
                    new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            mostrarValor(finalI, juegoBantumi.getSemillas(finalI));
                        }
                    });
        }
        bantumiVM.getTurno().observe(   // Turno
                this,
                new Observer<JuegoBantumi.Turno>() {
                    @Override
                    public void onChanged(JuegoBantumi.Turno turno) {
                        marcarTurno(juegoBantumi.turnoActual());
                    }
                }
        );
    }

    /**
     * Indica el turno actual cambiando el color del texto
     *
     * @param turnoActual turno actual
     */
    private void marcarTurno(@NonNull JuegoBantumi.Turno turnoActual) {
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        switch (turnoActual) {
            case turnoJ1:
                tvJugador1.setTextColor(getColor(R.color.white));
                tvJugador1.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                tvJugador2.setTextColor(getColor(R.color.black));
                tvJugador2.setBackgroundColor(getColor(R.color.white));
                break;
            case turnoJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador1.setBackgroundColor(getColor(R.color.white));
                tvJugador2.setTextColor(getColor(R.color.white));
                tvJugador2.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                break;
            default:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.black));
        }
    }

    /**
     * Muestra el valor <i>valor</i> en la posición <i>pos</i>
     *
     * @param pos posición a actualizar
     * @param valor valor a mostrar
     */
    private void mostrarValor(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
        if (0 != idBoton) {
            TextView viewHueco = findViewById(idBoton);
            viewHueco.setText(String.valueOf(valor));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.opcAjustes: // @todo Preferencias
                startActivity(new Intent(this, BantumiPrefs.class));
                return true;
            case R.id.opcAcercaDe:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.aboutTitle)
                        .setMessage(R.string.aboutMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
            case R.id.opcReiniciarPartida:
                turno = obtenerTurnoPreferencia(prefs);
                new RestartDialogFragment().show(getSupportFragmentManager(), "RESTART_DIALOG");
                return true;
            case R.id.opcGuardarPartida:
                guardarPartida(this, getGameInfo ());
                return true;

            case R.id.opcRecuperarPartida:
                intent = new Intent (MainActivity.this, SavedGamesActivity.class);
                intent.putExtra("savedGame", getGameInfo());

                launcherRecuperarPartida.launch(intent);
                return true;

            case R.id.opcMejoresResultados:
                intent = new Intent (MainActivity.this, PuntuacionActivity.class);
                startActivity(intent);

            // @TODO!!! resto opciones

            default:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtSinImplementar),
                        Snackbar.LENGTH_LONG
                ).show();
        }
        return true;
    }

    /**
     * Acción que se ejecuta al pulsar sobre cualquier hueco
     *
     * @param v Vista pulsada (hueco)
     */
    public void huecoPulsado(@NonNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);
        switch (juegoBantumi.turnoActual()) {
            case turnoJ1:
                Log.i(LOG_TAG, "* Juega Jugador");
                juegoBantumi.jugar(num);
                break;
            case turnoJ2:
                Log.i(LOG_TAG, "* Juega Computador");
                juegoBantumi.juegaComputador();
                break;
            default:    // JUEGO TERMINADO
                finJuego();
        }
        if (juegoBantumi.juegoTerminado()) {
            finJuego();
        }
    }

    /**
     * El juego ha terminado. Volver a jugar?
     */
    private void finJuego() {
        String texto = (juegoBantumi.getSemillas(6) > 6 * numInicialSemillas)
                ? "Gana Jugador 1"
                : "Gana Jugador 2";
        if (juegoBantumi.getSemillas(6) == 6 * numInicialSemillas) {
            texto = "¡¡¡ EMPATE !!!";
        }

        guardarPuntuacion(juegoBantumi.getSemillas(6), juegoBantumi.getSemillas(13));

        // terminar
        new FinalAlertDialog(texto).show(getSupportFragmentManager(), "ALERT_DIALOG");
    }

    private SavedGame getGameInfo (){
        //tiempo exacto cuando almaceno
        long timestamp = System.currentTimeMillis();

        //el nombre del partida guardado
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = sdf.format(date);
        String name = "Partida " + formattedDate;

        List<Integer> gameInfo = new ArrayList<Integer>();
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++){
            gameInfo.add(juegoBantumi.getSemillas(i));
        }

        Turno turno = juegoBantumi.turnoActual();

        return new SavedGame(timestamp, name, gameInfo, turno);
    }

    private void registrarLauncherRecuperarPartida() {
        launcherRecuperarPartida = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            SavedGame partidaRecuperada = (SavedGame) data.getSerializableExtra("savedGame");
                            // 处理恢复游戏
                            recuperarPartida(partidaRecuperada);
                        }
                    }
                }
        );
    }


    private void recuperarPartida(SavedGame partidaARecuperar){
            for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++){
                bantumiVM.setNumSemillas(i,partidaARecuperar.getGameInfo().get(i));
            }
            bantumiVM.setTurno(partidaARecuperar.getTurno());
    }

    private void instanciarRepositorio (){
        puntuacionRepositorio = Room.databaseBuilder(
                getApplicationContext(),
                PuntuacionRepositorio.class,
                PuntuacionRepositorio.BASE_DATOS
                )
                .allowMainThreadQueries()
                .build();
    }

    private void guardarPuntuacion (int puntuacionJugador1, int puntuacionJugador2){

        //Obtener nombre del jugador a partir de la preferencia
        String nombreJugador = prefs.getString("nombre_jugador", "Jugador");

        ResultadoPartida resultado;
        if (puntuacionJugador1 > puntuacionJugador2){
            resultado = ResultadoPartida.JUGADOR1_GANADO;
        }else if (puntuacionJugador1 < puntuacionJugador2)
            resultado = ResultadoPartida.JUGADOR2_GANADO;
        else{
            resultado = ResultadoPartida.EMPATADO;
        }

        //Obtener fecha actual
        long fechaActual = System.currentTimeMillis();

        Puntuacion resultadoPartido = new Puntuacion(
                nombreJugador,resultado, fechaActual, puntuacionJugador1, puntuacionJugador2
        );

        puntuacionRepositorio.puntuacionDAO().insert(resultadoPartido);
        Toast.makeText(this, "Puntuación guardada", Toast.LENGTH_SHORT).show();

    }


}



