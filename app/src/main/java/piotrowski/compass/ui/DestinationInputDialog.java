package piotrowski.compass.ui;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import piotrowski.compass.R;
import piotrowski.compass.databinding.DialogDestinationInputBinding;

public class DestinationInputDialog extends DialogFragment {

    interface Listener {
        void onDestinationChoose(@NotNull Location location);
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
                .setPositiveButton(R.string.ok, (dialog, id) -> {
                    String latitude = String.valueOf(binding.latitudeDegreesEditText.getText()) + ':'
                            + binding.latitudeMinutesEditText.getText() + ':'
                            + binding.latitudeSecondsEditText.getText();
                    String longitude = String.valueOf(binding.longitudeDegreesEditText.getText()) + ':'
                            + binding.longitudeMinutesEditText.getText() + ':'
                            + binding.longitudeSecondsEditText.getText();

                    Location destination = new Location("");
                    destination.setLatitude(Location.convert(latitude));
                    destination.setLongitude(Location.convert(longitude));

                    listener.onDestinationChoose(destination);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) ->
                        DestinationInputDialog.this.getDialog().cancel());

        binding.latitudeDegreesEditText.requestFocus();
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog;
    }
}