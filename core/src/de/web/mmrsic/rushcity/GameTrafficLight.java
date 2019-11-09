package de.web.mmrsic.rushcity;

import com.badlogic.gdx.graphics.Color;

import java.util.LinkedList;
import java.util.List;

/**
 * Traffic light with coordinates.
 */
public class GameTrafficLight {
    private final CityMap.TrafficLight trafficLight;
    private final float x, y;

    public GameTrafficLight(final CityMap.TrafficLight trafficLight, final float x, final float y) {
        this.trafficLight = trafficLight;
        this.x = x;
        this.y = y;
    }

    public CityMap.TrafficLight getTrafficLight() {
        return trafficLight;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Color getColor() {
        return trafficLight.isRed() ? Color.RED : Color.GREEN;
    }

    public static final class Control {
        private final List<GameTrafficLight> lights;

        public Control(final List<GameTrafficLight> lights) {
            this.lights = new LinkedList<>(lights);
        }

        public List<GameTrafficLight> getLights() {
            return lights;
        }

        public void controlThrough(final TrafficLightsControl lightsControl) {
            final List<CityMap.TrafficLight> trafficLights = new LinkedList<>();
            for (final GameTrafficLight light : getLights()) {
                trafficLights.add(light.getTrafficLight());
            }
            lightsControl.setLights(trafficLights);
        }
    }
}