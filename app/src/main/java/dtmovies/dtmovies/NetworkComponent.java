package dtmovies.dtmovies;

import javax.inject.Singleton;

import dagger.Component;
import dtmovies.dtmovies.ui.MainActivity;
import dtmovies.dtmovies.api.NetworkModule;
import dtmovies.dtmovies.ui.detail.MovieDetailActivity;
import dtmovies.dtmovies.ui.detail.MovieDetailFragment;
import dtmovies.dtmovies.ui.grid.MoviesGridFragment;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface NetworkComponent {

    void inject(MoviesGridFragment moviesGridFragment);

    void inject(MainActivity mainActivity);

    void inject(MovieDetailActivity movieDetailActivity);

    void inject(MovieDetailFragment movieDetailFragment);

}
