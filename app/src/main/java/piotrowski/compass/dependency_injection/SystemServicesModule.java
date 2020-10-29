package piotrowski.compass.dependency_injection;

import android.content.Context;
import android.hardware.SensorManager;

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
}
