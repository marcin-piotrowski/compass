package piotrowski.compass.ui;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import piotrowski.compass.R;
import piotrowski.compass.databinding.DialogDestinationInputBinding;

public class DestinationInputDialog extends DialogFragment {

    interface Listener {
        void onDestinationSet(double latitude, double longitude);
    }

    DestinationInputDialog(Listener listener) {
        this.listener = listener;
    }

    private final Listener listener;

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        DialogDestinationInputBinding binding = DialogDestinationInputBinding.inflate(inflater);

        builder
                .setView(binding.getRoot())
                .setPositiveButton(R.string.set, (dialog, id) -> {
                    String latitude = String.valueOf(binding.latitudeDegrees) + ':'
                            + binding.latitudeMinutes + ':'
                            + binding.latitudeSeconds;
                    String longitude = String.valueOf(binding.longitudeDegrees) + ':'
                            + binding.longitudeMinutes + ':'
                            + binding.longitudeSeconds;

                    listener.onDestinationSet(Location.convert(latitude), Location.convert(longitude));
                    DestinationInputDialog.this.getDialog().cancel();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        DestinationInputDialog.this.getDialog().cancel());

        return builder.create();
    }
}