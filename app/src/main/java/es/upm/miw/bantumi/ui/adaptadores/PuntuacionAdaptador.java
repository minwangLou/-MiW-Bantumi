package es.upm.miw.bantumi.ui.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.modelos.Puntuacion;

public class PuntuacionAdaptador  extends ArrayAdapter<Puntuacion>{
    private final LayoutInflater inflater;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public PuntuacionAdaptador(@NonNull Context context, @NonNull List<Puntuacion> puntuaciones) {
        super(context, 0, puntuaciones);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_puntuacion, parent, false);
        }

        Puntuacion p = getItem(position);

        TextView tvNombre = convertView.findViewById(R.id.tvNombreJugador);
        TextView tvResultado = convertView.findViewById(R.id.tvResultado);
        TextView tvFecha = convertView.findViewById(R.id.tvFecha);
        TextView tvPuntuaciones = convertView.findViewById(R.id.tvPuntuaciones);

        tvNombre.setText("Nombre: " + p.getNombreJugador());
        tvResultado.setText("Resultado: " + p.getResultadoPartida().name());
        tvFecha.setText("Fecha: " + sdf.format(p.getFecha()));
        tvPuntuaciones.setText("Puntuación Jugador 1: " + p.getPuntuacionJugador1() + " | Puntuación Jugador 2: " + p.getPuntuacionJugador2());

        return convertView;
    }
}
