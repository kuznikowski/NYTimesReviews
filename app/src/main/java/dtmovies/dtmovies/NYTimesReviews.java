package dtmovies.dtmovies;

import android.app.Application;
import dtmovies.dtmovies.api.NetworkModule;

public class NYTimesReviews extends Application {

    private NetworkComponent networkComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        networkComponent = DaggerNetworkComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public NetworkComponent getNetworkComponent() {
        return networkComponent;
    }

}
