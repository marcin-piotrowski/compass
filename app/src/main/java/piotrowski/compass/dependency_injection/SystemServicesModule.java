package piotrowski.compass.dependency_injection;

import android.content.Context;
import android.hardware.SensorManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn(ApplicationComponent.class)
public class SystemServicesModule {

    @Provides
    public static SensorManager provideSensorManager(
            @ApplicationContext Context context
    ) {
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Provides
    public static FusedLocationProviderClient provideLocationProvider(
            @ApplicationContext Context context
    ) {
        return LocationServices.getFusedLocationProviderClient(context);
    }
}
