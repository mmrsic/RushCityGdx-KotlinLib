package de.web.mmrsic.rushcity;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.web.mmrsic.rushcity.CityMap.Street;

import java.util.LinkedList;
import java.util.List;

/**
 * {@link ApplicationListener} for Rush City.
 */
public class RushCity implements ApplicationListener {
    private static final String TAG = RushCity.class.getSimpleName();

    private CityMap cityMap;
    private List<Car> cars = new LinkedList<>();
    private TrafficLightsControl lightsControl;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        cityMap = new DefaultCityMapCreator().create(24, 32);
        cityMap.print(System.out);
        int maxRow = cityMap.getNumRows() - 1;
        int maxCol = cityMap.getNumCols() - 1;
        cars.add(createCar(0, 3, maxRow, 28));
        cars.add(createCar(7, 0, maxRow, 28));
        cars.add(createCar(12, maxCol, 0, 3));
        cars.add(createCar(maxRow, 18, 2, 0));
        lightsControl = new TrafficLightsControl(new TrafficLightsControl.Pattern() {
            @Override
            public double vehiclePhase() {
                return 2;
            }

            @Override
            public double pedestrianPhase() {
                return 5;
            }

            @Override
            public double wholePhase() {
                return vehiclePhase() + pedestrianPhase();
            }
        });

        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new StretchViewport(cityMap.getNumCols() * 20, cityMap.getNumRows() * 20, camera);
    }

    @Override
    public void resize(final int width, final int height) {
        Gdx.app.log(TAG, "Resized to: " + width + "x" + height);
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        final float deltaTime = 4 * Gdx.graphics.getRawDeltaTime();
        for (final Car car : cars) {
            car.addTime(deltaTime);
        }
        lightsControl.addTime(deltaTime);

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        for (final Street street : cityMap.streets()) {
            drawRect(Color.LIGHT_GRAY, street.x(), street.y(), 2);
            final List<GameTrafficLight> streetLights = new LinkedList<>();
            for (final Street.Lane lane : street.getLanes().values()) {
                final CityMap.TrafficLight trafficLight = lane.getTrafficLight();
                if (trafficLight != null) {
                    streetLights.add(new GameTrafficLight(trafficLight, (float) lane.x(), (float) lane.y()));
                }
            }
            new GameTrafficLight.Control(streetLights).controlThrough(lightsControl);
            for (final GameTrafficLight streetLight : streetLights) {
                drawRect(streetLight.getColor(), streetLight.getX(), streetLight.getY(), 0.5f);
            }
        }
        for (final Car car : cars) {
            drawRect(Color.YELLOW, car.x(), car.y(), 1);
        }
        shapeRenderer.end();
    }

    @Override
    public void pause() {
        Gdx.app.log(TAG, "Paused");
    }

    @Override
    public void resume() {
        Gdx.app.log(TAG, "Resumed");
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        Gdx.app.log(TAG, "Disposed");
    }

    // HELPERS //

    /**
     * Create a new {@link Car} instance for given start and target coordinates.
     *
     * @param startRow  row of start coordinate
     * @param startCol  column of start coordinate
     * @param targetRow row of target coordinate
     * @param targetCol column of target coordinate
     * @return newly created instance per call - never null
     */
    private Car createCar(int startRow, int startCol, int targetRow, int targetCol) {
        final Car car = new Car(cityMap.streetAt(startRow, startCol), cityMap.streetAt(targetRow, targetCol));
        Gdx.app.log(TAG, "Created: " + car);
        return car;
    }

    /**
     * Draw a rectangle on the display for a given color, given x and y positions, and a width-height factor.
     *
     * @param color    color to use for the rectangle to draw - must not be null
     * @param x        x position of the ?? corner of the rectangle
     * @param y        y position of the ?? corner of the rectangle TODO: Describe corner position
     * @param whFactor factor for width and height
     */
    private void drawRect(final Color color, final Number x, final Number y, final float whFactor) {
        shapeRenderer.setColor(color);
        int pixelFactor = 10;
        float pixelX = pixelFactor * x.floatValue();
        float pixelY = pixelFactor * (/*cityMap.getNumRows() * 2 -*/ y.floatValue());
        float pixelStretch = pixelFactor * whFactor;
        shapeRenderer.rect(pixelX, pixelY, pixelStretch, pixelStretch);
    }
}
